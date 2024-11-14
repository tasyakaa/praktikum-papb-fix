package com.example.prakpapb_m2.screen

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.prakpapb_m2.ui.theme.Blue80

@Composable
fun MatkulScreen() {
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() } // Menginisialisasi FirebaseFirestore
    val items = remember { mutableStateListOf<Map<String, Any?>>() } // State lokal untuk menyimpan item

    LaunchedEffect(Unit) {
        firestore.collection("jadwal_kuliah").get()
            .addOnSuccessListener { result ->
                items.clear()
                for (document in result) {
                    val data = document.data
                    Log.d("Firestore Data", "Document: $data")
                    items.add(data)
                }
                // Mengurutkan item berdasarkan day dan time
                items.sortWith(compareBy({ it["day"] as? String }, { it["time"] as? String }))
            }
            .addOnFailureListener { exception ->
                Log.e("MatkulScreen", "Error getting documents: ", exception)
                Toast.makeText(context, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // List dengan LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(items) { item ->
            val day = item["day"] as? String ?: "N/A"
            val time = item["time"] as? String ?: "N/A"
            val className = item["class"] as? String ?: "N/A"
            val code = item["code"] as? String ?: "N/A"
            val subject = item["subject"] as? String ?: "N/A"
            val lecturer = item["lecturer"] as? String ?: "N/A"
            val room = item["room"] as? String ?: "N/A"

            // Setiap item berupa Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { /* Handle card click if needed */ },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(2.dp, Blue80)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Blue80
                    )
                    Divider(
                        color = Blue80,
                        thickness = 2.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "Day: $day", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Time: $time", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Class: $className", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Code: $code", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Lecturer: $lecturer", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "Room: $room", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Schedule",
                fontWeight = FontWeight.Bold,
                color = Blue80
            )
        }
    )
}