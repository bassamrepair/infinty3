package com.drnfis.infinty

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults

/* =========================
   شاشة الطالب + الهوم
   ========================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainScreen(navController: NavHostController) {

    val innerNav = rememberNavController()
    val innerBackStack by innerNav.currentBackStackEntryAsState()
    val isOnHome = innerBackStack?.destination?.route == "home"

    // حوار التأكيد عند ضغط رجوع من الهوم
    var showExitDialog by remember { mutableStateOf(false) }

    // حالات تمرير للإعدادات
    val darkMode = remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("العربية") }
    val ctx = LocalContext.current

    // اعتراض زر الرجوع
    BackHandler {
        if (!isOnHome) {
            innerNav.popBackStack()           // ارجع داخل التنقل الداخلي
        } else {
            showExitDialog = true             // اطلب تأكيد الخروج إلى شاشة الدخول
        }
    }

    // حوار تأكيد
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("تأكيد الخروج") },
            text  = { Text("هل تريد تسجيل الخروج والعودة إلى شاشة الدخول؟") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("registerType") {
                        popUpTo(0) { inclusive = true }  // تنظيف المكدس بالكامل
                        launchSingleTop = true
                    }
                }) { Text("نعم") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("إلغاء") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("الرئيسية") },
                actions = {
                    IconButton(onClick = { innerNav.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "الإشعارات")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        }
    ) { padding ->

        NavHost(
            navController = innerNav,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            // الهوم
            composable("home") {
                HomeScreen(onOpenSettings = { innerNav.navigate("settings") })
            }

            // الإشعارات
            composable("notifications") { NotificationsScreen() }

            // الإعدادات
            composable("settings") {
                SettingsScreen(
                    darkMode = darkMode.value,
                    onDarkModeChange = { darkMode.value = it },

                    selectedLanguageLabel = selectedLanguage,
                    onLanguagePick = { selectedLanguage = it },

                    onOpenNotifications = { innerNav.navigate("notifications") },

                    onContactDevelopers = {
                        val email = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@infinty.app"))
                            putExtra(Intent.EXTRA_SUBJECT, "استفسار من تطبيق INFINTY")
                        }
                        ctx.startActivity(email)
                    },
                    onReportProblem = { /* TODO: افتح نموذج الإبلاغ */ },
                    onOpenDownloads = { /* TODO: افتح شاشة التنزيلات */ },
                    onOpenPrivacy = {
                        val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"))
                        ctx.startActivity(web)
                    },

                    // زر تسجيل الخروج داخل الإعدادات
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("registerType") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

/* =========================
   HomeScreen + عناصرها
   ========================= */

@Composable
private fun SummaryCard(title: String, suffix: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFE)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            AssistChip(onClick = {}, label = { Text(suffix) })
        }
    }
}

@Composable
private fun HomeScreen(onOpenSettings: (() -> Unit)? = null) {
    val bg = Color(0xFFEFF2F6)
    val panel = Color(0xFFFDFDFE)
    val brand = Color(0xFF1E3A8A)
    val track = Color(0xFFE2E8F0)
    val chipBg = Color(0xFFE9EEF9)

    val banners = listOf(
        Banner("أحداث الجامعة", "اطّلع على آخر الفعاليات"),
        Banner("عروض الدراسة", "منح وبرامج مميّزة"),
        Banner("ورش العمل", "طوّر مهاراتك")
    )
    val professors = listOf(
        Prof("د. أحمد منصور", "برمجة الحاسوب"),
        Prof("د. خالد دهيني", "نظم قواعد البيانات"),
        Prof("د. أحمد منصور", "برمجة الحاسوب"),
        Prof("د. أحمد ستسنيتر", "الخوارزميات"),
        Prof("د. قيس عيّاط", "منطق رقمي")
    )
    val courses = listOf(
        CourseCard("هياكل البيانات", "CS301", "ريدون ستيوس", "Pre 3801", status = "أساسي"),
        CourseCard("نظم قواعد البيانات", "DB330", "ريدون ستيوس", "Pre 3301", status = "اختياري"),
        CourseCard("تطوير الويب", "WD210", "ريدون ستيوس", "Pre 2101", status = "مستمر"),
        CourseCard("تطوير الويب", "WD220", "ريدون ستيوس", "Pre 2201", status = "مؤجل"),
    )

    val gpa = 3.85f
    val completed = 90
    val weeklyHours = 3
    val progressTarget = 0.75f
    val progress by animateFloatAsState(targetValue = progressTarget, label = "gpaProgress")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { HeaderCard(name = "ملف الطالب", onSettings = onOpenSettings) }
        item { BannerRow(banners) }
        item { SummaryCard(title = "ملخص أكاديمي", suffix = "حسب السنة") }

        // بطاقة GPA
        item {
            Card(
                colors = CardDefaults.cardColors(panel),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("%.2f".format(gpa), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2F3B4B))
                        Spacer(Modifier.width(8.dp))
                        Text("هندسة برمجيات", fontSize = 18.sp, color = Color(0xFF6B7A90), fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("الساعات المكتسبة: $completed", color = Color(0xFF8A99AD), style = MaterialTheme.typography.labelLarge)

                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        AssistChip(
                            onClick = { },
                            label = { Text("ساعات هذا الأسبوع: $weeklyHours") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = chipBg, labelColor = brand)
                        )
                        Spacer(Modifier.weight(1f))
                        Text("${(progress * 100).toInt()}%", color = Color(0xFF6B7A90))
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = brand,
                        trackColor = track
                    )
                }
            }
        }

        // أساتذتي
        item {
            Card(
                colors = CardDefaults.cardColors(panel),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("أساتذتي", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(professors) { p -> ProfessorItem(p) }
                    }
                }
            }
        }

        // المواد الدراسية
        item {
            Text(
                text = "المواد الدراسية",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2F3B4B),
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 6.dp)
            )
        }
        items(courses) { c ->
            CourseCardVertical(c)
            Spacer(Modifier.height(8.dp))
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

/* ===== عناصر مساعدة ===== */

@Composable
private fun HeaderCard(name: String, onSettings: (() -> Unit)?) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6B7A90).copy(alpha = 0.22f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFEFF2F6)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF5B6B84), modifier = Modifier.size(40.dp)) }

            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color(0xFF2F3B4B))
                Text("مرحبًا بعودتك 👋", color = Color(0xFF6B7A90), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onSettings?.invoke() }) {
                Icon(Icons.Default.Settings, contentDescription = "الإعدادات", tint = Color(0xFF2F3B4B))
            }
        }
    }
}

@Composable
private fun BannerRow(items: List<Banner>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { b -> BannerCard(b.title, b.subtitle) }
    }
}

@Composable
private fun BannerCard(title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.width(260.dp).height(80.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .padding(12.dp)
        ) {
            Column(Modifier.align(Alignment.CenterStart)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFFDFE7F5), style = MaterialTheme.typography.labelMedium)
            }
            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xCCFFFFFF), modifier = Modifier.align(Alignment.CenterEnd).size(36.dp))
        }
    }
}

@Composable
private fun ProfessorItem(p: Prof) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(58.dp).clip(CircleShape).background(Color(0xFFEFF2F6)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF5B6B84), modifier = Modifier.size(42.dp)) }
        Spacer(Modifier.height(6.dp))
        Text(p.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(p.role, color = Color(0xFF8A99AD), fontSize = 11.sp)
    }
}

@Composable
private fun CourseCardVertical(c: CourseCard) {
    val headerColor = when (c.title) {
        "هياكل البيانات" -> Color(0xFF1E4DB7)
        "نظم قواعد البيانات" -> Color(0xFF0EA5A5)
        else -> Color(0xFFE57C1F)
    }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(headerColor)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(c.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(c.code, color = Color(0xFFE2E8F0), style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.BottomStart))
            }
            Column(Modifier.padding(14.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(c.status) },
                    leadingIcon = { Icon(Icons.Default.Label, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFF2F6FF), labelColor = Color(0xFF1E3A8A))
                )
                Spacer(Modifier.height(6.dp))
                Text(c.teacher, fontWeight = FontWeight.Medium)
                Text(c.section, color = Color(0xFF8A99AD), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(onClick = { /* تفاصيل */ }) { Icon(Icons.Default.Info, contentDescription = null) }
                    FilledTonalIconButton(onClick = { /* متابعة */ }) { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                }
            }
        }
    }
}

/* ===== نماذج بيانات بسيطة ===== */
private data class Banner(val title: String, val subtitle: String)
private data class Prof(val name: String, val role: String)
private data class CourseCard(val title: String, val code: String, val teacher: String, val section: String, val status: String)
