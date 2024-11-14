package com.example.prakpapb_m2.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.prakpapb_m2.GithubUser
import com.example.prakpapb_m2.RetrofitInstance
import com.example.prakpapb_m2.ui.theme.Blue80
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun ProfileScreen() {
    val username = "tasyakaa"
    var user by remember { mutableStateOf<GithubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(username) {
        scope.launch {
            try {
                user = RetrofitInstance.api.getUserProfile(username)
                isLoading = false
            } catch (e: HttpException) {
                errorMessage = "Failed to fetch user data: ${e.message()}"
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "An unexpected error occurred: ${e.message}"
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
            user != null -> {
                user?.let {
                    // Circular profile image
                    Image(
                        painter = rememberImagePainter(data = it.avatar_url),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape) // Make the image circular
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // User details in boxes
                    ProfileDetailBox(title = "Username", content = it.login)
                    ProfileDetailBox(title = "Name", content = it.name ?: "N/A")
                    ProfileDetailBox(title = "Followers", content = it.followers.toString())
                    ProfileDetailBox(title = "Following", content = it.following.toString())
                }
            }
        }
    }
}

@Composable
fun ProfileDetailBox(title: String, content: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Blue80 // Set title color to Blue80
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Adjust color for visibility
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make Box fill the width of the Card
                    .padding(8.dp), // Padding around content
                contentAlignment = Alignment.Center // Center the content
            ) {
                Text(
                    text = content,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(), // Make Text fill the width
                    textAlign = TextAlign.Center // Center text within the Text component
                )
            }
        }
    }
}