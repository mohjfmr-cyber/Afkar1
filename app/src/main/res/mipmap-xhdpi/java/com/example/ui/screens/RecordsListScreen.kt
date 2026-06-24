package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThoughtRecord
import com.example.ui.viewmodel.ThoughtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsListScreen(
    viewModel: ThoughtViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val records by viewModel.filteredRecords.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedEmotion by viewModel.selectedFilterEmotion.collectAsStateWithLifecycle()
        val selectedDistortion by viewModel.selectedFilterDistortion.collectAsStateWithLifecycle()

        val context = LocalContext.current
        var expandedRecordId by remember { mutableStateOf<Long?>(null) }

        val emotionsList = listOf("همه", "اضطراب", "ترس", "غم", "خشم", "شرم", "احساس گناه", "ناامیدی")
        val distortionList = listOf("همه", "فاجعه‌سازی", "همه یا هیچ دیدن", "ذهن‌خوانی", "احساس به جای واقعیت", "احساس گناه افراطی")

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🗄️ سوابق و آرشیو افکار",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تاریخچه تحلیل‌ها و بازنگری‌های شما",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Export buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { viewModel.exportAllToCsv(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "خروجی CSV", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Search text field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                placeholder = { Text("جستجو در وضعیت، افکار، تاریخ...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "پاک کردن")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Dynamic Filters Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Emotion Filter Dropdown
                var emotionMenuExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().clickable { emotionMenuExpanded = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedEmotion.isEmpty()) "فیلتر احساس" else "احساس: $selectedEmotion",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = emotionMenuExpanded,
                        onDismissRequest = { emotionMenuExpanded = false }
                    ) {
                        emotionsList.forEach { emotion ->
                            DropdownMenuItem(
                                text = { Text(emotion) },
                                onClick = {
                                    viewModel.setFilterEmotion(if (emotion == "همه") "" else emotion)
                                    emotionMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Distortion Filter Dropdown
                var distortionMenuExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().clickable { distortionMenuExpanded = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedDistortion.isEmpty()) "فیلتر خطای فکر" else "خطا: ${selectedDistortion.take(12)}...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = distortionMenuExpanded,
                        onDismissRequest = { distortionMenuExpanded = false }
                    ) {
                        distortionList.forEach { distortion ->
                            DropdownMenuItem(
                                text = { Text(distortion) },
                                onClick = {
                                    viewModel.setFilterDistortion(if (distortion == "همه") "" else distortion)
                                    distortionMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Records List
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "هیچ فکر ثبت‌شده‌ای پیدا نشد.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        val isExpanded = expandedRecordId == record.id
                        ThoughtRecordItem(
                            record = record,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedRecordId = if (isExpanded) null else record.id
                            },
                            onDelete = { viewModel.deleteRecord(record) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThoughtRecordItem(
    record: ThoughtRecord,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date + Distortion Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = record.shamsiDate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (record.relatedDistortion != "سایر") {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = record.relatedDistortion,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Intrusive thought display
            Text(
                text = "فکر مزاحم: ${record.intrusiveThought}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Progress Indicators: distress rating reduction
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "میزان ناراحتی:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${record.emotionIntensity}% ➔ ${record.finalDistressRating}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.finalDistressRating < record.emotionIntensity) Color(0xFF4CAF50) else Color.Red
                    )
                }

                // Expand icon (KeyboardArrowUp / KeyboardArrowDown)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded CBT Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider()

                    DetailRow(title = "📌 وضعیت محرک:", content = record.situation)
                    DetailRow(title = "⚡ شدت باور به فکر اولیه:", content = "${record.beliefRating}%")
                    DetailRow(title = "🎭 احساسات همراه:", content = "${record.emotions} (شدت: ${record.emotionIntensity}%)")
                    DetailRow(title = "✅ شواهد تایید کننده:", content = record.supportingEvidence)
                    DetailRow(title = "❌ شواهد مخالف فکر:", content = record.contraryEvidence)
                    DetailRow(title = "🌱 فکر متعادل جایگزین:", content = record.balancedThought)
                    DetailRow(title = "📈 باور به فکر متعادل:", content = "${record.balancedBeliefRating}%")
                    DetailRow(title = "🔄 بازنگری و پاسخ:", content = record.reviewText)
                }
            }
        }
    }
}

@Composable
fun DetailRow(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            fontSize = 13.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
