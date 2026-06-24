package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ThoughtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddThoughtScreen(
    viewModel: ThoughtViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentStep by remember { mutableStateOf(1) }

        // Form Fields State
        var situation by remember { mutableStateOf("") }
        var intrusiveThought by remember { mutableStateOf("") }
        var beliefRating by remember { mutableStateOf(50f) }
        var relatedDistortion by remember { mutableStateOf("سایر") }

        val selectedEmotions = remember { mutableStateListOf<String>() }
        var emotionIntensity by remember { mutableStateOf(50f) }

        var supportingEvidence by remember { mutableStateOf("") }
        var contraryEvidence by remember { mutableStateOf("") }

        var balancedThought by remember { mutableStateOf("") }
        var balancedBeliefRating by remember { mutableStateOf(50f) }

        var reviewText by remember { mutableStateOf("") }
        var finalDistressRating by remember { mutableStateOf(50f) }

        val scrollState = rememberScrollState()

        val distortionList = listOf(
            "فاجعه‌سازی",
            "همه یا هیچ دیدن",
            "ذهن‌خوانی",
            "احساس به جای واقعیت",
            "احساس گناه افراطی",
            "سایر"
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ثبت فکر منطقی جدید (CBT)", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                // Step Progress Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { step ->
                        val isCurrent = currentStep == step
                        val isDone = step < currentStep
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        isDone -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    }
                                )
                        )
                    }
                }

                // Step Description label
                Text(
                    text = when (currentStep) {
                        1 -> "گام ۱: شناسایی وضعیت و فکر مزاحم"
                        2 -> "گام ۲: ارزیابی احساسات فعلی"
                        3 -> "گام ۳: به چالش کشیدن فکر (بررسی شواهد)"
                        4 -> "گام ۴: بازسازی فکر متعادل"
                        else -> "گام ۵: ارزیابی مجدد و بازنگری نهایی"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Main Form Content depending on step
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentStep) {
                        1 -> {
                            Text(
                                text = "موقعیت محرک چیست؟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            OutlinedTextField(
                                value = situation,
                                onValueChange = { situation = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("کجا بودی؟ چه اتفاقی افتاد؟ با چه کسی بودی؟...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text(
                                text = "فکر مزاحم یا منفی که به ذهنت آمد چیست؟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            OutlinedTextField(
                                value = intrusiveThought,
                                onValueChange = { intrusiveThought = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("مثال: حتماً خراب می‌کنم و آبرویم می‌رود...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "میزان باور به فکر مزاحم:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${beliefRating.toInt()}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = beliefRating,
                                    onValueChange = { beliefRating = it },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )
                            }

                            Text(
                                text = "خطای شناختی احتمالی مرتبط چیست؟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(distortionList) { item ->
                                    val isSelected = relatedDistortion == item
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { relatedDistortion = item },
                                        label = { Text(item, fontSize = 12.sp) }
                                    )
                                }
                            }

                            if (relatedDistortion != "سایر") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "مطالعه پیشنهادی در کتابخانه دانش:",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "خطای شناختی: $relatedDistortion",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            Text(
                                text = "چه احساساتی را تجربه می‌کنی؟ (چند مورد را انتخاب کن)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Clean, stable manual 3-column rows of checkboxes instead of experimental FlowRow
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val row1 = listOf("اضطراب", "ترس", "غم")
                                val row2 = listOf("خشم", "شرم", "احساس گناه")
                                val row3 = listOf("ناامیدی", "سایر")

                                listOf(row1, row2, row3).forEach { rowList ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowList.forEach { emotion ->
                                            val isSelected = selectedEmotions.contains(emotion)
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    if (isSelected) selectedEmotions.remove(emotion)
                                                    else selectedEmotions.add(emotion)
                                                },
                                                label = { Text(emotion, fontSize = 13.sp) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        // Pad out the last row if it has fewer items
                                        if (rowList.size < 3) {
                                            repeat(3 - rowList.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "شدت کلی این احساسات:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${emotionIntensity.toInt()}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = emotionIntensity,
                                    onValueChange = { emotionIntensity = it },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )
                            }
                        }

                        3 -> {
                            Text(
                                text = "شواهد تأییدکننده این فکر چیست؟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = supportingEvidence,
                                onValueChange = { supportingEvidence = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("چه واقعیت‌هایی نشان می‌دهد این فکر منفی تا حدی درست است؟...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "شواهد مخالف این فکر چیست؟ (بسیار مهم)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = contraryEvidence,
                                onValueChange = { contraryEvidence = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("چه واقعیت‌ها، تجارب گذشته یا مدارکی برخلاف این فکر داری؟...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        4 -> {
                            Text(
                                text = "فکر متعادل و واقع‌بینانه جایگزین چیست؟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = balancedThought,
                                onValueChange = { balancedThought = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("با توجه به شواهد بالا، فکر منطقی‌تری بنویس که هر دو جنبه را در بر بگیرد...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "میزان باور فعلی به فکر متعادل:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${balancedBeliefRating.toInt()}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = balancedBeliefRating,
                                    onValueChange = { balancedBeliefRating = it },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )
                            }
                        }

                        5 -> {
                            Text(
                                text = "بازنگری احساسات (چگونه با این مسئله برخورد می‌کنی؟)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = reviewText,
                                onValueChange = { reviewText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("بعد از بررسی شواهد و نوشتن فکر جدید، چه احساسی به این شرایط داری؟...", fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "شدت ناراحتی پس از بازنگری:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${finalDistressRating.toInt()}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = finalDistressRating,
                                    onValueChange = { finalDistressRating = it },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )
                            }
                        }
                    }
                }

                // Wizard control buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("قبلی")
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < 5) {
                                currentStep++
                            } else {
                                viewModel.insertRecord(
                                    situation = situation,
                                    intrusiveThought = intrusiveThought,
                                    beliefRating = beliefRating.toInt(),
                                    emotions = selectedEmotions.toList(),
                                    emotionIntensity = emotionIntensity.toInt(),
                                    supportingEvidence = supportingEvidence,
                                    contraryEvidence = contraryEvidence,
                                    balancedThought = balancedThought,
                                    balancedBeliefRating = balancedBeliefRating.toInt(),
                                    reviewText = reviewText,
                                    finalDistressRating = finalDistressRating.toInt(),
                                    relatedDistortion = relatedDistortion,
                                    onComplete = onBack
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(start = if (currentStep > 1) 8.dp else 0.dp),
                        enabled = when (currentStep) {
                            1 -> situation.isNotEmpty() && intrusiveThought.isNotEmpty()
                            2 -> selectedEmotions.isNotEmpty()
                            3 -> supportingEvidence.isNotEmpty() && contraryEvidence.isNotEmpty()
                            4 -> balancedThought.isNotEmpty()
                            else -> reviewText.isNotEmpty()
                        }
                    ) {
                        if (currentStep < 5) {
                            Text("بعدی")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                        } else {
                            Text("ثبت نهایی فکر ✨")
                        }
                    }
                }
            }
        }
    }
}
