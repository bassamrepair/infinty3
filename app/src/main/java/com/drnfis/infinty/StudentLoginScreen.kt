package com.drnfis.infinty

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults // <-- إضافة هذا الاستيراد
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// Credential Manager
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.GetPasswordOption

// استثناءات الحفظ (مفصّلة)
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.CreateCredentialException

// استثناءات الجلب
import androidx.credentials.exceptions.GetCredentialException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit = {},
    onCreateAccount: () -> Unit
) {
    val ctx = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val cm = remember { CredentialManager.create(ctx) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    fun showSnack(msg: String) = scope.launch {
        snackbarHostState.showSnackbar(msg, withDismissAction = true)
    }

    // ✅ محاولة جلب كلمة مرور محفوظة للتعبئة/الدخول تلقائيًا
    LaunchedEffect(Unit) {
        try {
            val request = GetCredentialRequest(listOf(GetPasswordOption()))
            val result: GetCredentialResponse = cm.getCredential(ctx, request)
            (result.credential as? PasswordCredential)?.let { cred ->
                email = cred.id
                password = cred.password
                loading = true // نبدأ التحميل
                auth.signInWithEmailAndPassword(cred.id, cred.password)
                    .addOnCompleteListener { task ->
                        loading = false // ننتهي من التحميل
                        if (task.isSuccessful) {
                            onLoginSuccess()
                            navController.navigate("student_main") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            // إذا فشل تسجيل الدخول التلقائي، نعرض رسالة ونمسح كلمة المرور ليمكن للمستخدم إدخالها يدويًا
                            showSnack("تعذّر تسجيل الدخول التلقائي: ${task.exception?.message ?: ""}")
                            password = "" // مسح كلمة المرور بعد فشل
                        }
                    }
            }
        } catch (e: GetCredentialException) {
            Log.d("CredMan", "No credential picked or user cancelled: ${e.message}")
            // لا يوجد بيانات محفوظة أو المستخدم ألغى، نستمر بشكل طبيعي
        } catch (e: Exception) {
            Log.e("CredMan", "Unexpected error during credential retrieval: ${e.message}", e)
            showSnack("خطأ أثناء جلب بيانات الدخول المحفوظة.")
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
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
                )
                .padding(padding),
            contentAlignment = Alignment.Center // لتوسيط المحتوى في المنتصف
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // جعل البطاقة تأخذ 90% من عرض الشاشة
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp) // حواف دائرية أكبر للبطاقة الرئيسية
                    )
                    .padding(32.dp)
                    .align(Alignment.Center), // توسيط العمود داخل الـ Box
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "أهلا بك",
                    fontSize = 32.sp,                 // ثابت
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E40AF),
                    modifier = Modifier
                        .fillMaxWidth()              // خليه يأخذ عرض الشاشة
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,     // توسيط
                    maxLines = 1,                     // سطر واحد فقط
                    softWrap = false,                 // منع الالتفاف
                    overflow = TextOverflow.Ellipsis, // نقاط إذا ضاق المكان
                    lineHeight = 35.sp                // ارتفاع سطر مناسب لتفادي التداخل
                )
                Text(
                    text = "تسجيل الدخول إلى حساب الطالب",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("البريد الإلكتروني", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = Color(0xFF1E40AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors( // <-- تم التعديل هنا
                        focusedBorderColor = Color(0xFF1E40AF),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(16.dp)) // مسافة أكبر بين الحقول

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = Color(0xFF1E40AF)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors( // <-- تم التعديل هنا
                        focusedBorderColor = Color(0xFF1E40AF),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(30.dp)) // مسافة أكبر قبل الزر

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            showSnack("⚠️ الرجاء إدخال البريد الإلكتروني وكلمة المرور.")
                            return@Button
                        }
                        loading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // ✅ اطلب الحفظ أولًا، ثم نفّذ الانتقال بعد اكتمال الطلب
                                    scope.launch {
                                        var canNavigate = true
                                        try {
                                            cm.createCredential(
                                                context = ctx,
                                                request = CreatePasswordRequest(
                                                    id = email,
                                                    password = password
                                                )
                                            )
                                            // نجاح الحفظ
                                        } catch (e: CreateCredentialCancellationException) {
                                            // المستخدم أغلق واجهة الحفظ - لا نعتبره خطأ يمنع التنقل
                                            Log.d("CredMan", "User cancelled credential save.")
                                        } catch (e: CreateCredentialProviderConfigurationException) {
                                            canNavigate = false
                                            showSnack("⚠️ يرجى تفعيل مدير كلمات المرور (مثل Google Password Manager) على جهازك للحفظ التلقائي.")
                                        } catch (e: CreateCredentialUnknownException) {
                                            Log.d("CredMan", "Unknown credential save error: ${e.message}")
                                        } catch (e: CreateCredentialException) {
                                            Log.d("CredMan", "Credential save API error: ${e.message}")
                                        } catch (e: Exception) {
                                            Log.e("CredMan", "General error during credential save", e)
                                        }

                                        if (canNavigate) {
                                            // مهلة قصيرة لضمان إغلاق ورقة الحفظ بسلاسة قبل الانتقال
                                            delay(350)
                                            loading = false // إنهاء التحميل قبل التنقل
                                            onLoginSuccess()
                                            navController.navigate("student_main") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        } else {
                                            loading = false // إذا لم نتمكن من التنقل بسبب خطأ، نوقف التحميل
                                        }
                                    }
                                } else {
                                    loading = false
                                    val msg = when {
                                        task.exception?.message?.contains("password", true) == true -> "❌ كلمة مرور غير صحيحة، يرجى المحاولة مرة أخرى."
                                        task.exception?.message?.contains("no user", true) == true || task.exception?.message?.contains("badly formatted", true) == true -> "❌ المستخدم غير موجود أو البريد الإلكتروني غير صحيح."
                                        else -> "❌ فشل تسجيل الدخول: تحقق من اتصالك بالإنترنت."
                                    }
                                    showSnack(msg)
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !loading, // تعطيل الزر أثناء التحميل
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E40AF), // لون أزرق للزر
                        disabledContainerColor = Color(0xFF1E40AF).copy(alpha = 0.6f)
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("جاري تسجيل الدخول...", fontSize = 18.sp, color = Color.White)
                    } else {
                        Text("تسجيل الدخول", fontSize = 18.sp, color = Color.White)
                    }
                }

                TextButton(
                    onClick = { navController.navigate("register_student") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("ليس لديك حساب؟ أنشئ حسابًا الآن", color = Color(0xFF1E40AF), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentLoginScreen() {
    StudentLoginScreen(
        navController = rememberNavController(),
        onLoginSuccess = TODO(),
        onCreateAccount = TODO(),
    )
}