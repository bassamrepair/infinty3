package com.drnfis.infinty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // لو كان هناك مستخدم مسجل بالفعل، ابدأ مباشرة بالـ student_main
        val startDest = if (FirebaseAuth.getInstance().currentUser != null) {
            "student_main"
        } else {
            "registerType"
        }

        setContent {
            AppNavigation(startDestination = startDest)
        }
    }
}
