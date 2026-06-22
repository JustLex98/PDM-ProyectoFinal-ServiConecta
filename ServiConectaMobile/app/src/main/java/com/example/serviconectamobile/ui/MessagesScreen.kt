package com.example.serviconectamobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviconectamobile.data.InboxChat
import com.example.serviconectamobile.data.Message
import com.example.serviconectamobile.data.SessionManager
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onChatClick: (Int, String) -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = FirebaseFirestore.getInstance()
    val orangeBrand = Color(0xFFFF9900)

    var chats by remember { mutableStateOf<List<InboxChat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Buscamos chats donde participe el ID actual
        db.collection("chats").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val tempChats = mutableListOf<InboxChat>()
                for (doc in snapshot.documents) {
                    if (doc.id.contains(session.getUserId().toString())) {
                        val otherId = doc.id.replace(session.getUserId().toString(), "").replace("_", "").toInt()
                        tempChats.add(InboxChat(otherId, "Usuario #$otherId", "Toca para ver mensajes", doc.id))
                    }
                }
                chats = tempChats
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("MIS MENSAJES", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = orangeBrand) }
        } else if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No tienes conversaciones aún", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(chats) { chat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onChatClick(chat.otherUserId, chat.otherUserName) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(45.dp).background(orangeBrand, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Chat, null, tint = Color.Black)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(chat.otherUserName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(chat.lastMessage, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}