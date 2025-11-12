package com.drnfis.infinty

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    startDestination: String = "registerType",
    externalNavController: NavHostController? = null
) {
    val navController = externalNavController ?: rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // شاشة اختيار نوع التسجيل
        composable("registerType") {
            // لا تغيّر التوقيع: لامبدا واحدة تعيد userType
            RegisterTypeScreen { userType ->
                navController.navigate("login/$userType")
            }
        }

        // شاشة تسجيل الدخول
        composable("login/{userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "student"

            // ملاحظة: إن لم يكن عندك onCreateAccount في StudentLoginScreen
            // أضف زرًا داخلها ينفّذ navController.navigate("register_student")
            StudentLoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("student_main") {
                        popUpTo(0) { inclusive = true }  // مسح الستاك بعد الدخول
                    }
                },
                // إن كان عندك باراميتر إضافي لزر إنشاء حساب فعِّله، وإلا تجاهله
                onCreateAccount = {
                    if (userType == "student") {
                        navController.navigate("StudentRegisterScreen")
                    }
                }
            )
        }

        // شاشة التسجيل الجديد (تستخدم اسمك: StudentRegisterScreen)
        composable("register_student") {
            StudentRegisterScreen(
                navController = navController,
                onRegisteredGoToLogin = {
                    // بعد نجاح التسجيل نعود لتسجيل الدخول (طالب)
                    navController.navigate("login/student") {
                        popUpTo("registerType") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // الرئيسية بعد الدخول
        composable("student_main") {
            StudentMainScreen(navController = navController)
        }
    }
}
