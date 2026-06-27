package com.example.serviconectamobile.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviconectamobile.data.Message
import com.example.serviconectamobile.data.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(receiverId: Int, receiverName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = FirebaseFirestore.getInstance()
    val orangeBrand = Color(0xFFFF9900)

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var chatStatus by remember { mutableStateOf("active") }

    val chatId = if (session.getUserId() < receiverId) "${session.getUserId()}_$receiverId" else "${receiverId}_${session.getUserId()}"

    LaunchedEffect(Unit) {
        // Escuchar mensajes
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) messages = snapshot.toObjects(Message::class.java)
            }

        // Escuchar estado del chat (para saber si ya se finalizó)
        db.collection("chats").document(chatId).addSnapshotListener { doc, _ ->
            chatStatus = doc?.getString("status") ?: "active"
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(receiverName.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = orangeBrand) }
                },
                actions = {
                    // BOTÓN DE SEGURIDAD: Solo el contratista puede finalizar el servicio
                    if (session.getUserRole() == "Contratista" && chatStatus == "active") {
                        TextButton(onClick = {
                            db.collection("chats").document(chatId).update("status", "completed")
                            Toast.makeText(context, "Servicio marcado como Finalizado", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.CheckCircle, null, tint = orangeBrand)
                            Text("FINALIZAR", color = orangeBrand, fontWeight = FontWeight.Bold)
                        }
                    } else if (chatStatus == "completed") {
                        Text("FINALIZADO", color = Color.Green, fontSize = 10.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                items(messages) { msg ->
                    val isMe = msg.senderId == session.getUserId()
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart) {
                        Surface(
                            color = if (isMe) orangeBrand else Color(0xFF222222),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(text = msg.message, color = if (isMe) Color.Black else Color.White, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                        }
                    }
                }
            }

            Surface(color = Color(0xFF1A1A1A)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe un mensaje...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        enabled = chatStatus == "active", // Bloquea el chat si ya terminó
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = orangeBrand, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(25.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val msgData = Message(session.getUserId(), receiverId, messageText, System.currentTimeMillis(), session.getUserName())
                                db.collection("chats").document(chatId).collection("messages").add(msgData)

                                val chatSummary = mapOf(
                                    "lastMessage" to messageText,
                                    "user1Id" to session.getUserId(),
                                    "user1Name" to session.getUserName(),
                                    "user2Id" to receiverId,
                                    "user2Name" to receiverName,
                                    "timestamp" to System.currentTimeMillis(),
                                    "status" to chatStatus // Mantiene el estado actual
                                )
                                db.collection("chats").document(chatId).set(chatSummary)
                                messageText = ""
                            }
                        },
                        enabled = chatStatus == "active",
                        modifier = Modifier.background(if(chatStatus == "active") orangeBrand else Color.Gray, RoundedCornerShape(50.dp))
                    ) { Icon(Icons.Default.Send, null, tint = Color.Black) }
                }
            }
        }
    }
}