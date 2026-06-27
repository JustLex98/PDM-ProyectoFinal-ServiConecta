package com.example.serviconectamobile.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviconectamobile.data.*
import com.example.serviconectamobile.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorDetailScreen(contractorId: Int, onBack: () -> Unit, onContactClick: (Int, String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = remember { SessionManager(context) }
    val orangeBrand = Color(0xFFFF9900)

    var detail by remember { mutableStateOf<ContractorFullDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var serviceCompleted by remember { mutableStateOf(false) } // <--- ESTADO DE SEGURIDAD

    var myRating by remember { mutableStateOf(5) }
    var myComment by remember { mutableStateOf("") }

    LaunchedEffect(contractorId) {
        try {
            detail = RetrofitClient.instance.getContractorDetail(contractorId)

            // VALIDACIÓN DE SEGURIDAD:
            val chatId = if (session.getUserId() < contractorId) "${session.getUserId()}_$contractorId" else "${contractorId}_${session.getUserId()}"
            FirebaseFirestore.getInstance().collection("chats").document(chatId).get()
                .addOnSuccessListener { doc ->
                    // Solo es válido si el contratista marcó el servicio como 'completed'
                    serviceCompleted = doc.getString("status") == "completed"
                }
        } catch (e: Exception) { } finally { isLoading = false }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("PERFIL PROFESIONAL", fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = orangeBrand) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            if (detail != null) {
                FloatingActionButton(onClick = {
                    if (session.isLoggedIn()) onContactClick(detail!!.profile.UserID, detail!!.profile.FirstName)
                    else Toast.makeText(context, "Inicia sesión para contactar", Toast.LENGTH_SHORT).show()
                }, containerColor = orangeBrand, contentColor = Color.Black, shape = CircleShape) {
                    Icon(Icons.Default.Chat, null)
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = orangeBrand) }
        } else {
            detail?.let { data ->
                Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                    Box(Modifier.fillMaxWidth().height(140.dp).background(Color(0xFF111111)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(70.dp).background(orangeBrand, CircleShape), contentAlignment = Alignment.Center) {
                                Text(data.profile.FirstName.take(1), fontSize = 35.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = orangeBrand, modifier = Modifier.size(18.dp))
                                Text(" ${data.averageRating}", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(data.profile.BusinessName ?: "${data.profile.FirstName} ${data.profile.LastName}", color = orangeBrand, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("${data.profile.YearsOfExperience} Años de Experiencia", color = Color.Gray)

                        Spacer(Modifier.height(30.dp))

                        // SECCIÓN DE RESEÑA: Solo se muestra si el servicio fue completado oficialmente
                        if (session.isLoggedIn() && serviceCompleted && session.getUserId() != contractorId) {
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("VALORA EL TRABAJO REALIZADO", color = orangeBrand, fontWeight = FontWeight.Black)
                                    Row {
                                        repeat(5) { index ->
                                            IconButton(onClick = { myRating = index + 1 }) {
                                                Icon(Icons.Default.Star, null, tint = if (index < myRating) orangeBrand else Color.DarkGray)
                                            }
                                        }
                                    }
                                    OutlinedTextField(
                                        value = myComment, onValueChange = { myComment = it },
                                        placeholder = { Text("¿Cómo fue el servicio de ${data.profile.FirstName}?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = orangeBrand, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                    )
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                RetrofitClient.instance.postReview(ReviewRequest(myRating, myComment, session.getUserId(), contractorId))
                                                Toast.makeText(context, "¡Gracias por tu reseña!", Toast.LENGTH_SHORT).show()
                                                serviceCompleted = false // Ocultar después de calificar
                                            }
                                        },
                                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = orangeBrand)
                                    ) { Text("PUBLICAR RESEÑA", color = Color.Black, fontWeight = FontWeight.Bold) }
                                }
                            }
                        } else if (session.isLoggedIn() && !serviceCompleted && session.getUserId() != contractorId) {
                            // Mensaje informativo para el cliente
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)), border = BorderStroke(1.dp, Color.DarkGray)) {
                                Text(
                                    "La opción de calificar se habilitará una vez que el profesional marque el servicio como 'Finalizado' en el chat.",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(30.dp))
                        Text("RESEÑAS RECIENTES", color = Color.White, fontWeight = FontWeight.Black)
                        data.reviews.forEach { review ->
                            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
                                Column(Modifier.padding(12.dp)) {
                                    Row {
                                        Text(review.ClientName, color = orangeBrand, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        repeat(review.Rating) { Icon(Icons.Default.Star, null, tint = orangeBrand, modifier = Modifier.size(10.dp)) }
                                    }
                                    Text(review.Comment ?: "", color = Color.White)
                                }
                            }
                        }
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}