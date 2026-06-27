package com.example.serviconectamobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviconectamobile.data.SessionManager
import com.example.serviconectamobile.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onLoginClick: () -> Unit, onCategoryClick: (Int) -> Unit, onProfileClick: () -> Unit, onMessagesClick: () -> Unit) {
    val viewModel: CatalogViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn = sessionManager.isLoggedIn()
    val orangeBrand = Color(0xFFFF9900)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Servi", color = Color.White, fontWeight = FontWeight.Black)
                        Surface(color = orangeBrand, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(start = 4.dp)) {
                            Text("Conecta", color = Color.Black, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                },
                actions = {
                    if (!isLoggedIn) {
                        Button(onClick = onLoginClick, colors = ButtonDefaults.buttonColors(containerColor = orangeBrand)) {
                            Text("LOGIN", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    } else {
                        IconButton(onClick = onMessagesClick) { Icon(Icons.Default.Email, null, tint = orangeBrand) }
                        Box(modifier = Modifier.size(35.dp).background(orangeBrand, CircleShape).clickable { onProfileClick() }, contentAlignment = Alignment.Center) {
                            Text(sessionManager.getUserName().take(1).uppercase(), color = Color.Black, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // --- DETECCIÓN DE ERROR / NO INTERNET ---
            if (viewModel.categories.isEmpty() && !viewModel.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.WifiOff, null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Sin conexión al servidor", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Revisa tu internet e intenta de nuevo.", color = Color.Gray, fontSize = 12.sp)
                    Button(onClick = { viewModel.loadCategories() }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = orangeBrand)) {
                        Text("REINTENTAR", color = Color.Black)
                    }
                }
            } else if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = orangeBrand) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(viewModel.categories) { category ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onCategoryClick(category.CategoryID) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(40.dp).background(orangeBrand, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(category.CategoryName.take(1), color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(category.CategoryName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(category.Description ?: "Ver profesionales", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}