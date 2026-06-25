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
import com.example.serviconectamobile.data.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
        // Escuchar cambios en la colección de chats
        db.collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val tempChats = mutableListOf<InboxChat>()
                    for (doc in snapshot.documents) {
                        val u1 = doc.getLong("user1Id")?.toInt() ?: 0
                        val u2 = doc.getLong("user2Id")?.toInt() ?: 0

                        // Si el usuario actual es parte de este chat
                        if (u1 == session.getUserId() || u2 == session.getUserId()) {
                            val isUser1 = u1 == session.getUserId()
                            val otherId = if (isUser1) u2 else u1
                            val otherName = if (isUser1) doc.getString("user2Name") else doc.getString("user1Name")
                            val lastMsg = doc.getString("lastMessage") ?: "Nuevo mensaje"

                            tempChats.add(InboxChat(otherId, otherName ?: "Usuario", lastMsg, doc.id))
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
                title = { Text("BANDEJA DE ENTRADA", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = orangeBrand) }
        } else if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes mensajes todavía.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(chats) { chat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onChatClick(chat.otherUserId, chat.otherUserName) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(45.dp).background(orangeBrand, CircleShape), contentAlignment = Alignment.Center) {
                                Text(chat.otherUserName.take(1).uppercase(), color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(chat.otherUserName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(chat.lastMessage, color = orangeBrand, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}