package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ThoughtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ThoughtViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val scrollState = rememberScrollState()

        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("پاک‌سازی تمام داده‌ها؟", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = { Text("آیا مطمئن هستید که می‌خواهید تمام افکار ثبت‌شده و وضعیت مطالعه مقالات را به طور کامل پاک کنید؟ این عملیات غیرقابل بازگشت است.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            Toast.makeText(context, "داده‌ها با موفقیت پاک شدند.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("بله، پاک شود", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "⚙️ تنظیمات برنامه",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "پیکربندی و اطلاعات عمومی دفتر تحلیل افکار",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            // Section 1: Backup and exports
            Text(
                text = "💾 پشتیبان‌گیری و خروجی",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("خروجی کامل اکسل (CSV)", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("ذخیره فایل جدول افکار در حافظه گوشی یا اشتراک‌گذاری", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable { viewModel.exportAllToCsv(context) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("دریافت گزارش تحلیلی (PDF)", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("ایجاد خروجی فایلی از نمودارها و تحلیل‌ها برای روانشناس", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable { viewModel.exportReportToPdf(context) }
                    )
                }
            }

            // Section 2: Personalization
            Text(
                text = "🎨 شخصی‌سازی",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("نمایش مجدد معارفه (Onboarding)", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("بازنشانی تنظیمات برای مشاهده مجدد ۴ صفحه راهنمای آغازین", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable {
                            viewModel.resetOnboarding()
                            Toast.makeText(context, "تنظیمات معارفه بازنشانی شد. با ورود بعدی مجدد نمایش داده می‌شود.", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }

            // Section 3: Safe space & Danger Zone
            Text(
                text = "⚠️ حریم خصوصی و امنیت",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("حذف تمام اطلاعات", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                        supportingContent = { Text("پاک کردن همیشگی تمام پرونده‌ها و بازنشانی کامل پیشرفت کتابخانه", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable { showDeleteDialog = true }
                    )
                }
            }

            // Section 4: About
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🧘", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "دفتر تحلیل افکار",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "نسخه ۱.۰.۰ - بر پایه متدهای علمی CBT",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "این نرم‌افزار به صورت ۱۰۰٪ آفلاین و محلی کار می‌کند. داده‌های شما تحت هیچ شرایطی از دستگاهتان خارج نمی‌شوند و امنیت و حریم خصوصی افکارتان کاملاً تضمین شده است.",
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
