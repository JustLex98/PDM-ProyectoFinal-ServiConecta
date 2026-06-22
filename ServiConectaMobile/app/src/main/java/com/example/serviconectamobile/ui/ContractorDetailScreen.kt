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

    // Estados para la nueva reseña
    var myRating by remember { mutableStateOf(5) }
    var myComment by remember { mutableStateOf("") }

    LaunchedEffect(contractorId) {
        try { detail = RetrofitClient.instance.getContractorDetail(contractorId) }
        catch (e: Exception) { } finally { isLoading = false }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("PERFIL PROFESIONAL", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = orangeBrand) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            if (detail != null) {
                FloatingActionButton(onClick = {
                    if (session.isLoggedIn()) onContactClick(detail!!.profile.UserID, detail!!.profile.FirstName)
                    else Toast.makeText(context, "Inicia sesión para chatear", Toast.LENGTH_SHORT).show()
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
                                Icon(Icons.Default.Star, null, tint = orangeBrand, modifier = Modifier.size(16.dp))
                                Text(" ${data.averageRating}", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(data.profile.BusinessName ?: "${data.profile.FirstName} ${data.profile.LastName}", color = orangeBrand, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("${data.profile.YearsOfExperience} Años de Experiencia", color = Color.Gray)
                        Spacer(Modifier.height(20.dp))
                        Text("BIO", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(data.profile.Bio ?: "Sin biografía.", color = Color.LightGray)

                        Spacer(Modifier.height(30.dp))

                        // FORMULARIO PARA DEJAR RESEÑA (Solo Clientes logueados)
                        if (session.isLoggedIn() && session.getUserId() != contractorId) {
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("DEJAR UNA RESEÑA", color = orangeBrand, fontWeight = FontWeight.Black)
                                    Row {
                                        repeat(5) { index ->
                                            IconButton(onClick = { myRating = index + 1 }) {
                                                Icon(Icons.Default.Star, null, tint = if (index < myRating) orangeBrand else Color.DarkGray)
                                            }
                                        }
                                    }
                                    OutlinedTextField(
                                        value = myComment, onValueChange = { myComment = it },
                                        placeholder = { Text("¿Cómo fue tu experiencia?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = orangeBrand, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                    )
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val res = RetrofitClient.instance.postReview(ReviewRequest(myRating, myComment, session.getUserId(), contractorId))
                                                if (res.isSuccessful) {
                                                    Toast.makeText(context, "Reseña publicada", Toast.LENGTH_SHORT).show()
                                                    myComment = ""
                                                }
                                            }
                                        },
                                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = orangeBrand)
                                    ) { Text("PUBLICAR CALIFICACIÓN", color = Color.Black, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
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