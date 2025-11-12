package com.drnfis.infinty

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguageLabel: String,
    onLanguagePick: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onContactDevelopers: () -> Unit,
    onReportProblem: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenPrivacy: () -> Unit,
    // ✅ جديد: كول باك تسجيل الخروج
    onLogout: () -> Unit,
) {
    val ctx = LocalContext.current
    val languages: List<String> = remember {
        ctx.resources.getStringArray(R.array.languages).toList()
    }
    var showLanguageSheet by remember { mutableStateOf(false) }

    val bg = MaterialTheme.colorScheme.background

    // نسخة التطبيق (بديل BuildConfig)
    val versionName = remember {
        try {
            val pm = ctx.packageManager
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(ctx.packageName, 0)
            }
            info.versionName ?: "1.0"
        } catch (_: Exception) { "1.0" }
    }

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {

            // قسم: عام
            item {
                SectionHeader(
                    text = stringResource(R.string.settings_general_section),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // الوضع الليلي
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_dark_mode)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_camera),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_blue)
                        )
                    },
                    trailingContent = {
                        Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Divider()
            }

            // الإشعارات
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_notifications)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_notifications),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_red)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenNotifications() }
                )
                Divider()
            }

            // اللغة
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_language)) },
                    supportingContent = { Text(selectedLanguageLabel, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_map),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_gray)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageSheet = true }
                )
                Divider()
            }

            // قسم: الدعم
            item {
                SectionHeader(
                    text = stringResource(R.string.settings_support_section),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    topPadding = 20.dp
                )
            }

            // تواصل مع المطورين
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_contact_developers)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_email),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_green)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onContactDevelopers() }
                )
                Divider()
            }

            // أخبرنا عن مشكلة
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_report_problem)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_bug_report),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_orange)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReportProblem() }
                )
                Divider()
            }

            // التنزيلات
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_manage_downloads)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_download),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_link)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDownloads() }
                )
                Divider()
            }

            // سياسة الخصوصية
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_privacy_policy)) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ic_lock),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_link)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = null,
                            tint = colorResource(R.color.settings_arrow_tint)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPrivacy() }
                )
                Divider()
            }

            // ✅ قسم: الحساب — تسجيل الخروج
            item {
                SectionHeader(
                    text = stringResource(R.string.settings_account_section),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    topPadding = 20.dp
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_logout)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = colorResource(R.color.settings_icon_tint_gray)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(vertical = 4.dp)
                )
                Divider()
            }

            // إصدار التطبيق
            item {
                Text(
                    text = stringResource(R.string.settings_app_version) + " " + versionName,
                    style = MaterialTheme.typography.labelMedium,
                    color = colorResource(R.color.settings_app_version_text_color),
                    modifier = Modifier
                        .padding(top = 18.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
    }

    // BottomSheet اختيار اللغة
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Divider()
                languages.forEach { label ->
                    ListItem(
                        headlineContent = { Text(label) },
                        trailingContent = {
                            if (label == selectedLanguageLabel) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguagePick(label)
                                showLanguageSheet = false
                            }
                            .padding(horizontal = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/* ===== رؤوس الأقسام الخفيفة ===== */
@Composable
private fun SectionHeader(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    topPadding: Dp = 12.dp
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = topPadding, bottom = 8.dp)
    )
}
