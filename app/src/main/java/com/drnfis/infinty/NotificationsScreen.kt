package com.drnfis.infinty

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationsScreen() {
    val notifications = listOf(
        "📢 تم رفع محاضرة جديدة في مادة الفيزياء",
        "📅 امتحان مادة الكيمياء يوم الخميس القادم",
        "🕓 محاضرة الرياضيات ستبدأ بعد 10 دقائق"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("الإشعارات", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
        notifications.forEach { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7FF)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(msg, modifier = Modifier.padding(16.dp), fontSize = 16.sp, color = Color(0xFF1E3A8A))
            }
        }
    }
}
