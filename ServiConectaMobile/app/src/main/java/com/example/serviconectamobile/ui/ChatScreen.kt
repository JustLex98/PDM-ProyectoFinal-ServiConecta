package com.example.serviconectamobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    // ID Único para la conversación (ordenamos los IDs para que siempre sea el mismo canal)
    val chatId = if (session.getUserId() < receiverId)
        "${session.getUserId()}_$receiverId"
    else
        "${receiverId}_${session.getUserId()}"

    // Escuchar mensajes en TIEMPO REAL
    LaunchedEffect(Unit) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages = snapshot.toObjects(Message::class.java)
                }
            }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(receiverName.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("CHAT DE SERVICONECTA", fontSize = 10.sp, color = orangeBrand)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = orangeBrand)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Lista de mensajes (Burbujas)
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == session.getUserId()
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            color = if (isMe) orangeBrand else Color(0xFF222222),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isMe) 12.dp else 0.dp,
                                bottomEnd = if (isMe) 0.dp else 12.dp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp).widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = if (isMe) Color.Black else Color.White,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Barra de entrada de texto
            Surface(
                color = Color(0xFF1A1A1A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe un mensaje...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeBrand,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val newMsg = Message(
                                    senderId = session.getUserId(),
                                    receiverId = receiverId,
                                    message = messageText,
                                    timestamp = System.currentTimeMillis(),
                                    senderName = session.getUserName()
                                )
                                db.collection("chats").document(chatId)
                                    .collection("messages").add(newMsg)
                                messageText = ""
                            }
                        },
                        modifier = Modifier.background(orangeBrand, RoundedCornerShape(50.dp))
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.Black)
                    }
                }
            }
        }
    }
}