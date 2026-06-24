package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class ThoughtViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = ThoughtRepository(database.thoughtDao())

    // Onboarding State (Shared Preferences)
    private val prefs = application.getSharedPreferences("thought_journal_prefs", Context.MODE_PRIVATE)
    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // Active screen navigation (custom navigation fallback if needed, but we can also use NavigationCompose)
    // We will support Navigation Compose but use ViewModel for shared state.

    // Thought records
    val allRecords: StateFlow<List<ThoughtRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Article Statuses
    val allArticleStatuses: StateFlow<List<ArticleStatus>> = repository.allArticleStatuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilterEmotion = MutableStateFlow("")
    val selectedFilterEmotion: StateFlow<String> = _selectedFilterEmotion.asStateFlow()

    private val _selectedFilterDistortion = MutableStateFlow("")
    val selectedFilterDistortion: StateFlow<String> = _selectedFilterDistortion.asStateFlow()

    // Filtered records
    val filteredRecords: StateFlow<List<ThoughtRecord>> = combine(
        allRecords,
        searchQuery,
        selectedFilterEmotion,
        selectedFilterDistortion
    ) { records, query, emotion, distortion ->
        records.filter { record ->
            val matchesQuery = query.isEmpty() || 
                    record.situation.contains(query, ignoreCase = true) ||
                    record.intrusiveThought.contains(query, ignoreCase = true) ||
                    record.balancedThought.contains(query, ignoreCase = true) ||
                    record.reviewText.contains(query, ignoreCase = true) ||
                    record.shamsiDate.contains(query, ignoreCase = true)
            
            val matchesEmotion = emotion.isEmpty() || record.emotions.contains(emotion)
            val matchesDistortion = distortion.isEmpty() || record.relatedDistortion == distortion

            matchesQuery && matchesEmotion && matchesDistortion
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily psychology tip (changes daily based on day of year)
    val dailyTip: String = run {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = dayOfYear % KnowledgeLibrary.psychologyTips.size
        KnowledgeLibrary.psychologyTips[index]
    }

    // Article Read Completion & Bookmarks computed
    val knowledgeCompletionPercentage: StateFlow<Int> = allArticleStatuses.map { statuses ->
        val totalArticles = KnowledgeLibrary.articles.size
        if (totalArticles == 0) return@map 0
        val readCount = statuses.count { it.isRead }
        (readCount * 100) / totalArticles
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Operations
    fun insertRecord(
        situation: String,
        intrusiveThought: String,
        beliefRating: Int,
        emotions: List<String>,
        emotionIntensity: Int,
        supportingEvidence: String,
        contraryEvidence: String,
        balancedThought: String,
        balancedBeliefRating: Int,
        reviewText: String,
        finalDistressRating: Int,
        relatedDistortion: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val record = ThoughtRecord(
                situation = situation,
                intrusiveThought = intrusiveThought,
                beliefRating = beliefRating,
                emotions = emotions.joinToString(","),
                emotionIntensity = emotionIntensity,
                supportingEvidence = supportingEvidence,
                contraryEvidence = contraryEvidence,
                balancedThought = balancedThought,
                balancedBeliefRating = balancedBeliefRating,
                reviewText = reviewText,
                finalDistressRating = finalDistressRating,
                shamsiDate = JalaliCalendar.getTodayJalali(),
                relatedDistortion = relatedDistortion
            )
            repository.insertRecord(record)
            onComplete()
        }
    }

    fun deleteRecord(record: ThoughtRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            val currentStatus = repository.getArticleStatus(articleId) ?: ArticleStatus(articleId)
            val newStatus = currentStatus.copy(isBookmarked = !currentStatus.isBookmarked)
            repository.insertArticleStatus(newStatus)
        }
    }

    fun markArticleAsRead(articleId: String) {
        viewModelScope.launch {
            val currentStatus = repository.getArticleStatus(articleId) ?: ArticleStatus(articleId)
            if (!currentStatus.isRead) {
                val newStatus = currentStatus.copy(isRead = true)
                repository.insertArticleStatus(newStatus)
            }
        }
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        _onboardingCompleted.value = true
    }

    fun resetOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", false).apply()
        _onboardingCompleted.value = false
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterEmotion(emotion: String) {
        _selectedFilterEmotion.value = emotion
    }

    fun setFilterDistortion(distortion: String) {
        _selectedFilterDistortion.value = distortion
    }

    // Export reports to CSV
    fun exportAllToCsv(context: Context) {
        viewModelScope.launch {
            val records = allRecords.value
            if (records.isEmpty()) {
                Toast.makeText(context, "هیچ فکری برای خروجی گرفتن وجود ندارد.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val csvContent = StringBuilder()
                // CSV Header
                csvContent.append("شناسه,تاریخ شمسی,وضعیت,فکر مزاحم,شدت باور,احساسات,شدت احساس,شواهد موافق,شواهد مخالف,فکر متعادل,شدت باور متعادل,بازنگری,شدت ناراحتی پس از بازنگری,خطای شناختی\n")
                
                for (record in records) {
                    csvContent.append(
                        "${record.id}," +
                        "\"${record.shamsiDate}\"," +
                        "\"${record.situation.replace("\"", "\"\"")}\"," +
                        "\"${record.intrusiveThought.replace("\"", "\"\"")}\"," +
                        "${record.beliefRating}," +
                        "\"${record.emotions.replace("\"", "\"\"")}\"," +
                        "${record.emotionIntensity}," +
                        "\"${record.supportingEvidence.replace("\"", "\"\"")}\"," +
                        "\"${record.contraryEvidence.replace("\"", "\"\"")}\"," +
                        "\"${record.balancedThought.replace("\"", "\"\"")}\"," +
                        "${record.balancedBeliefRating}," +
                        "\"${record.reviewText.replace("\"", "\"\"")}\"," +
                        "${record.finalDistressRating}," +
                        "\"${record.relatedDistortion.replace("\"", "\"\"")}\"\n"
                    )
                }

                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                
                val csvFile = File(exportDir, "cbt_thoughts_export.csv")
                FileOutputStream(csvFile).use { out ->
                    out.write(csvContent.toString().toByteArray(Charsets.UTF_8))
                }

                shareFile(context, csvFile, "text/csv", "خروجی افکار (CSV)")
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در خروجی CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Export Reports to PDF
    fun exportReportToPdf(context: Context) {
        viewModelScope.launch {
            val records = allRecords.value
            if (records.isEmpty()) {
                Toast.makeText(context, "هیچ فکری برای خروجی گرفتن وجود ندارد.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val paint = Paint()

                // Draw simple header
                paint.color = Color.BLACK
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText("CBT Cognitive Thought Record Report", 50f, 60f, paint)

                paint.textSize = 12f
                paint.isFakeBoldText = false
                paint.color = Color.DKGRAY
                canvas.drawText("Generated on: ${JalaliCalendar.getTodayJalali()} (Shamsi)", 50f, 90f, paint)
                canvas.drawText("Total Thoughts Recorded: ${records.size}", 50f, 110f, paint)

                // Render some stats
                val totalDistressReduction = records.map { it.emotionIntensity - it.finalDistressRating }.average()
                val averageAnxiety = records.filter { it.emotions.contains("اضطراب") }.map { it.emotionIntensity }.average()
                
                canvas.drawText("Average Distress Reduction: ${String.format("%.1f", if (totalDistressReduction.isNaN()) 0.0 else totalDistressReduction)}%", 50f, 140f, paint)
                canvas.drawText("Average Anxiety Level: ${String.format("%.1f", if (averageAnxiety.isNaN()) 0.0 else averageAnxiety)}%", 50f, 160f, paint)

                // Render list
                var yOffset = 210f
                paint.color = Color.BLACK
                paint.isFakeBoldText = true
                paint.textSize = 14f
                canvas.drawText("Recent Thought Logs:", 50f, yOffset, paint)
                yOffset += 30f

                paint.isFakeBoldText = false
                paint.textSize = 10f
                paint.color = Color.BLACK

                for ((index, record) in records.take(10).withIndex()) {
                    if (yOffset > 800f) break
                    val recordStr = "${index + 1}. [${record.shamsiDate}] Thought: ${record.intrusiveThought.take(45)}... (Belief: ${record.beliefRating}% -> ${record.finalDistressRating}%)"
                    canvas.drawText(recordStr, 50f, yOffset, paint)
                    yOffset += 20f
                }

                pdfDocument.finishPage(page)

                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                
                val pdfFile = File(exportDir, "cbt_thoughts_report.pdf")
                FileOutputStream(pdfFile).use { out ->
                    pdfDocument.writeTo(out)
                }

                pdfDocument.close()
                shareFile(context, pdfFile, "application/pdf", "گزارش افکار (PDF)")
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در خروجی PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        // Find proper package name dynamically
        val authority = "${context.packageName}.fileprovider"
        val fileUri: Uri = FileProvider.getUriForFile(context, authority, file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
