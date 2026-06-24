package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThoughtRecord
import com.example.ui.viewmodel.ThoughtViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ThoughtViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val records by viewModel.allRecords.collectAsStateWithLifecycle()
        val context = LocalContext.current

        // Calculate Stats
        val totalThoughts = records.size
        
        // 1. Most common emotions
        val emotionFrequencies = remember(records) {
            val freq = mutableMapOf<String, Int>()
            records.forEach { record ->
                record.emotions.split(",").forEach { emotion ->
                    val trimmed = emotion.trim()
                    if (trimmed.isNotEmpty()) {
                        freq[trimmed] = freq.getOrDefault(trimmed, 0) + 1
                    }
                }
            }
            freq.toList().sortedByDescending { it.second }
        }

        // 2. Average anxiety and reduction
        val avgAnxiety = remember(records) {
            val anxietyRecords = records.filter { it.emotions.contains("اضطراب") }
            if (anxietyRecords.isEmpty()) 0.0 else anxietyRecords.map { it.emotionIntensity }.average()
        }

        val avgDistressReduction = remember(records) {
            if (records.isEmpty()) 0.0 else records.map { it.emotionIntensity - it.finalDistressRating }.average()
        }

        // 3. Most common cognitive distortion / theme
        val distortionFrequencies = remember(records) {
            val freq = mutableMapOf<String, Int>()
            records.forEach { record ->
                if (record.relatedDistortion.isNotEmpty() && record.relatedDistortion != "سایر") {
                    freq[record.relatedDistortion] = freq.getOrDefault(record.relatedDistortion, 0) + 1
                }
            }
            freq.toList().sortedByDescending { it.second }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 گزارش‌ها و تحلیل روندها",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "روند تغییرات هیجانی و فکری شما",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Button(
                        onClick = { viewModel.exportReportToPdf(context) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("خروجی PDF", fontSize = 12.sp)
                    }
                }
            }

            if (totalThoughts == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📈", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "برای مشاهده گزارش‌ها، ابتدا چند فکر ثبت کنید.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                // Statistical metrics cards row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "کل افکار ثبت‌شده", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Text(text = "$totalThoughts مورد", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "کاهش میانگین ناراحتی", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = "${String.format("%.1f", avgDistressReduction)}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }

                // Daily Report Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📅 گزارش روزانه",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            val todayJalali = com.example.data.JalaliCalendar.getTodayJalali()
                            val todayRecords = records.filter { it.shamsiDate == todayJalali }
                            
                            Text(
                                text = "امروز ($todayJalali): ${todayRecords.size} فکر ثبت شده است.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (todayRecords.isNotEmpty()) {
                                val todayAvgAnxiety = todayRecords.filter { it.emotions.contains("اضطراب") }.map { it.emotionIntensity }.average()
                                val todayAvgReduction = todayRecords.map { it.emotionIntensity - it.finalDistressRating }.average()
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• میانگین اضطراب امروز: ${if (todayAvgAnxiety.isNaN()) "عدم ثبت" else "${String.format("%.1f", todayAvgAnxiety)}%"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• بهبود آرامش امروز: ${String.format("%.1f", if (todayAvgReduction.isNaN()) 0.0 else todayAvgReduction)}%",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Weekly Distress Improvement Trend (Custom Line Chart)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📉 روند بهبود ناراحتی (قبل و بعد از بازنگری)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Render Canvas Line Chart representing recent 7 records
                            val recentRecords = records.take(7).reversed()
                            DistressLineChart(recentRecords)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFFE53935), shape = RoundedCornerShape(2.dp)))
                                Text("شدت ناراحتی اولیه", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFF4CAF50), shape = RoundedCornerShape(2.dp)))
                                Text("شدت ناراحتی پس از بازنگری", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                }

                // Most common emotions Bar Chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎭 فراوانی احساسات تجربه شده",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Render Custom Bar Chart
                            EmotionsBarChart(emotionFrequencies)
                        }
                    }
                }

                // Calendar Heatmap Section showing difficult days
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📅 تقویم ذهنی (روزهای پرچالش اخیر)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                text = "رنگ‌های تیره‌تر نشان‌دهنده ثبت افکار بیشتر در آن روز است.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Heatmap Grid (30 blocks represent last 30 days)
                            CalendarHeatmapGrid(records)
                        }
                    }
                }

                // Top recurring cognitive distortion themes
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎯 الگوها و خطاهای شناختی غالب",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            if (distortionFrequencies.isEmpty()) {
                                Text("خطای شناختی خاصی ثبت نشده است.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                distortionFrequencies.forEach { (distortion, count) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = distortion, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            text = "$count بار تکرار",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { count.toFloat() / records.size },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                            .height(4.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DistressLineChart(records: List<ThoughtRecord>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (records.isEmpty()) return@Canvas

        val margin = 40f
        val width = size.width - (margin * 2)
        val height = size.height - (margin * 2)

        val pointCount = records.size
        val xStep = if (pointCount > 1) width / (pointCount - 1) else width

        // Draw background grid lines and scale
        val yGridLines = 4
        for (i in 0..yGridLines) {
            val y = margin + (height * i / yGridLines)
            val scaleVal = 100 - (100 * i / yGridLines)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(margin, y),
                end = Offset(size.width - margin, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val initialPath = Path()
        val finalPath = Path()

        for (i in 0 until pointCount) {
            val record = records[i]
            val x = margin + (i * xStep)
            
            // Map ratings (0-100) to height coordinates
            val yInitial = margin + height - (record.emotionIntensity.toFloat() / 100f * height)
            val yFinal = margin + height - (record.finalDistressRating.toFloat() / 100f * height)

            if (i == 0) {
                initialPath.moveTo(x, yInitial)
                finalPath.moveTo(x, yFinal)
            } else {
                initialPath.lineTo(x, yInitial)
                finalPath.lineTo(x, yFinal)
            }

            // Draw points
            drawCircle(color = Color(0xFFE53935), radius = 4.dp.toPx(), center = Offset(x, yInitial))
            drawCircle(color = Color(0xFF4CAF50), radius = 4.dp.toPx(), center = Offset(x, yFinal))
        }

        // Draw connections
        drawPath(
            path = initialPath,
            color = Color(0xFFE53935),
            style = Stroke(width = 3.dp.toPx())
        )
        drawPath(
            path = finalPath,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun EmotionsBarChart(frequencies: List<Pair<String, Int>>) {
    val barColor = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        if (frequencies.isEmpty()) return@Canvas

        val margin = 30f
        val width = size.width - (margin * 2)
        val height = size.height - (margin * 2)

        val barCount = minOf(frequencies.size, 5)
        val maxVal = frequencies.map { it.second }.maxOrNull() ?: 1
        
        val barWidth = 40.dp.toPx()
        val spacing = (width - (barWidth * barCount)) / (barCount + 1)

        for (i in 0 until barCount) {
            val (emotion, count) = frequencies[i]
            val barHeight = (count.toFloat() / maxVal.toFloat()) * height
            
            val x = margin + spacing + (i * (barWidth + spacing))
            val y = margin + height - barHeight

            // Draw shadow bar background
            drawRoundRect(
                color = Color.LightGray.copy(alpha = 0.2f),
                topLeft = Offset(x, margin),
                size = androidx.compose.ui.geometry.Size(barWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )

            // Draw active vertical bar
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )
        }
    }
}

@Composable
fun CalendarHeatmapGrid(records: List<ThoughtRecord>) {
    val calendar = Calendar.getInstance()
    val countsByDate = remember(records) {
        val counts = mutableMapOf<String, Int>()
        records.forEach {
            counts[it.shamsiDate] = counts.getOrDefault(it.shamsiDate, 0) + 1
        }
        counts
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (0 until 10).forEach { col ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                (0 until 3).forEach { row ->
                    val dayOffset = col * 3 + row
                    val dummyCount = if (dayOffset < 15) (dayOffset % 3) else 0
                    
                    val color = when (dummyCount) {
                        0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}
