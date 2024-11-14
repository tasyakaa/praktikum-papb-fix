package com.example.prakpapb_m2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.prakpapb_m2.screen.MatkulScreen
import com.example.prakpapb_m2.screen.MyTopBar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.prakpapb_m2.ui.theme.PRAKPAPB_M2Theme

class ListActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = Firebase.firestore

        setContent {
            PRAKPAPB_M2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MyTopBar()

                        MatkulScreen()

                        // Spacer untuk mengisi sisa ruang
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}