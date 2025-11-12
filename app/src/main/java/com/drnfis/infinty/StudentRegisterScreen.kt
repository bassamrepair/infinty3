package com.drnfis.infinty

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll // <-- استيراد جديد
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// اختياري: حفظ كلمة المرور في Credential Manager
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegisterScreen(
    navController: NavController,
    onRegisteredGoToLogin: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val cm = remember { CredentialManager.create(ctx) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState() // <-- تعريف ScrollState

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    fun showSnack(msg: String) = scope.launch {
        snackbar.showSnackbar(msg, withDismissAction = true)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
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
                    .align(Alignment.Center)
                    .verticalScroll(scrollState), // <-- تطبيق التمرير هنا
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "! انضم إلينا",
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
                    text = "تسجيل حساب طالب جديد",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // حقول الإدخال
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("الاسم الكامل", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name Icon", tint = Color(0xFF1E40AF)) },
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it.trim() },
                    label = { Text("البريد الإلكتروني", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = Color(0xFF1E40AF)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("رقم الهاتف", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon", tint = Color(0xFF1E40AF)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = school, onValueChange = { school = it },
                    label = { Text("المدرسة", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = "School Icon", tint = Color(0xFF1E40AF)) },
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = stage, onValueChange = { stage = it },
                    label = { Text("المرحلة الدراسية", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.AutoStories, contentDescription = "Stage Icon", tint = Color(0xFF1E40AF)) },
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = governorate, onValueChange = { governorate = it },
                    label = { Text("المحافظة", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Governorate Icon", tint = Color(0xFF1E40AF)) },
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("كلمة المرور", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = Color(0xFF1E40AF)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirm, onValueChange = { confirm = it },
                    label = { Text("تأكيد كلمة المرور", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = "Confirm Password Icon", tint = Color(0xFF1E40AF)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E40AF), unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            name.isBlank() || email.isBlank() || phone.isBlank() ||
                                    school.isBlank() || stage.isBlank() || governorate.isBlank() ||
                                    password.isBlank() || confirm.isBlank() -> {
                                showSnack("⚠️ الرجاء ملء جميع الحقول.")
                            }
                            password != confirm -> showSnack("❌ كلمتا المرور غير متطابقتين.")
                            password.length < 6 -> showSnack("⚠️ كلمة المرور يجب أن لا تقل عن 6 أحرف.")
                            else -> {
                                loading = true
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { t ->
                                        if (!t.isSuccessful) {
                                            loading = false
                                            val errorMessage = t.exception?.localizedMessage ?: "حدث خطأ غير معروف."
                                            showSnack("❌ فشل التسجيل: ${
                                                when {
                                                    errorMessage.contains("email address is already in use", true) -> "البريد الإلكتروني مستخدم بالفعل."
                                                    errorMessage.contains("badly formatted", true) -> "صيغة البريد الإلكتروني غير صحيحة."
                                                    else -> errorMessage
                                                }
                                            }")
                                            return@addOnCompleteListener
                                        }
                                        val uid = auth.currentUser?.uid.orEmpty()
                                        val data = mapOf(
                                            "uid" to uid,
                                            "name" to name,
                                            "email" to email,
                                            "phone" to phone,
                                            "school" to school,
                                            "stage" to stage,
                                            "governorate" to governorate
                                        )
                                        FirebaseFirestore.getInstance()
                                            .collection("students")
                                            .document(uid)
                                            .set(data)
                                            .addOnSuccessListener {
                                                // اختياري: حفظ بيانات الدخول في Credential Manager
                                                scope.launch {
                                                    try {
                                                        cm.createCredential(
                                                            context = ctx,
                                                            request = CreatePasswordRequest(
                                                                id = email,
                                                                password = password
                                                            )
                                                        )
                                                        // showSnack("✅ تم حفظ بيانات الدخول تلقائياً.") // اختياري
                                                    } catch (e: CreateCredentialCancellationException) {
                                                        Log.d("CredMan", "User cancelled credential save.")
                                                    } catch (e: CreateCredentialProviderConfigurationException) {
                                                        showSnack("⚠️ يرجى تفعيل مدير كلمات المرور (مثل Google Password Manager) لحفظ البيانات تلقائياً.")
                                                    } catch (e: CreateCredentialUnknownException) {
                                                        Log.d("CredMan", "Unknown credential save error: ${e.message}")
                                                    } catch (e: CreateCredentialException) {
                                                        Log.d("CredMan", "Credential save API error: ${e.message}")
                                                    } catch (e: Exception) {
                                                        Log.e("CredMan", "General error during credential save", e)
                                                    }
                                                }
                                                loading = false
                                                showSnack("✅ تم إنشاء الحساب بنجاح.")
                                                scope.launch {
                                                    delay(500) // مهلة بسيطة قبل الانتقال
                                                    onRegisteredGoToLogin()
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                loading = false
                                                showSnack("❌ فشل حفظ بيانات الطالب: ${e.localizedMessage ?: "حدث خطأ غير معروف."}")
                                                // قد ترغب في حذف المستخدم من Firebase Auth هنا إذا فشل حفظ البيانات في Firestore
                                                auth.currentUser?.delete()
                                            }
                                    }
                            }
                        }
                    },
                    enabled = !loading,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E40AF),
                        disabledContainerColor = Color(0xFF1E40AF).copy(alpha = 0.6f)
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("جارٍ التسجيل…", fontSize = 18.sp, color = Color.White)
                    } else {
                        Text("تسجيل", fontSize = 18.sp, color = Color.White)
                    }
                }

                TextButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("لديك حساب بالفعل؟ تسجيل الدخول", color = Color(0xFF1E40AF), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentRegisterScreen() {
    StudentRegisterScreen(navController = rememberNavController())
}