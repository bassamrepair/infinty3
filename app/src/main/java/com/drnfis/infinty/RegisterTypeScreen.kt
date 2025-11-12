package com.drnfis.infinty

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// تأكد من وجود الموارد التالية في مجلد res/drawable
// R.drawable.student
// R.drawable.teacher
// R.drawable.school
// R.drawable.institute
// ولأجل المعاينة فقط، يمكن إنشاء ملفات صور dummy أو استخدام أيقونات Material Icons.

// لغرض المعاينة فقط، سأفترض وجود هذه الموارد.
// في مشروعك الحقيقي، تأكد من وجود ملفات res/drawable/student.png, teacher.png, school.png, institute.png
// أو استبدلها بـ Icons.Default.* إذا كنت تستخدم Material Icons.
// For preview purposes, we'll use placeholder icons if R.drawable isn't available
private val dummyStudentIcon = R.drawable.student // Replace with actual R.drawable.student
private val dummyTeacherIcon = R.drawable.teacher // Replace with actual R.drawable.teacher
private val dummySchoolIcon = R.drawable.school   // Replace with actual R.drawable.school
private val dummyInstituteIcon = R.drawable.institute // Replace with actual R.drawable.institute

@Composable
fun RegisterTypeScreen(onSelect: (String) -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8EC5FC), // لون علوي فاتح
                        Color(0xFFE0C3FC)  // لون سفلي بنفسجي فاتح
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f) // جعل البطاقة تأخذ 90% من عرض الشاشة
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp) // حواف دائرية أكبر للبطاقة الرئيسية
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "اختر نوع التسجيل",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E40AF), // لون أزرق غامق
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "حدد نوع حسابك للمتابعة",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // شبكة (صفين × عمودين)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp) // مسافات أقل بين الصفوف
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly, // توزيع العناصر بالتساوي
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // العنصر الأول: طالب
                    TypeSelectionCircle(
                        title = "طالب",
                        iconResId = dummyStudentIcon, // استخدم المتغير المؤقت
                        contentDesc = "تسجيل كطالب",
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF60A5FA), Color(0xFF3B82F6)) // أزرق
                        )
                    ) { onSelect("student") }

                    // العنصر الثاني: أستاذ
                    TypeSelectionCircle(
                        title = "أستاذ",
                        iconResId = dummyTeacherIcon, // استخدم المتغير المؤقت
                        contentDesc = "تسجيل كأستاذ",
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF34D399), Color(0xFF10B981)) // أخضر
                        )
                    ) { onSelect("teacher") }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // العنصر الثالث: مدرسة
                    TypeSelectionCircle(
                        title = "مدرسة",
                        iconResId = dummySchoolIcon, // استخدم المتغير المؤقت
                        contentDesc = "تسجيل كمدرسة",
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B)) // أصفر
                        )
                    ) { onSelect("school") }

                    // العنصر الرابع: معهد
                    TypeSelectionCircle(
                        title = "معهد",
                        iconResId = dummyInstituteIcon, // استخدم المتغير المؤقت
                        contentDesc = "تسجيل كمعهد",
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFFB7185), Color(0xFFF43F5E)) // أحمر
                        )
                    ) { onSelect("institute") }
                }
            }
        }
    }
}

/**
 * مكون دائرة اختيار النوع المحسن.
 */
@Composable
fun TypeSelectionCircle(
    title: String,
    iconResId: Int,
    contentDesc: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // تأثير التكبير/التصغير بناءً على حالة الضغط
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "scaleAnim",
    )

    Column(
        modifier = Modifier
            .width(120.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // لا يوجد تأثير تموج (Ripple effect) مرئي
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            shape = CircleShape,
            // استخدام Elevation ثابتة أو من الثيم
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // ظل للبطاقة
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = contentDesc, // إضافة وصف المحتوى (Accessibility)
                    modifier = Modifier.size(52.dp) // حجم الأيقونة
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            color = Color(0xFF1E40AF), // لون أزرق داكن للنص
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterTypeScreenPreview() {
    RegisterTypeScreen()
}