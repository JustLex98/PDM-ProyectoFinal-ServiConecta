package com.example.serviconectamobile.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.example.serviconectamobile.data.*
import com.example.serviconectamobile.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onLogoutSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = remember { SessionManager(context) }
    val orangeBrand = Color(0xFFFF9900)

    var businessName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryName by remember { mutableStateOf("Seleccionar Categoría") }
    var selectedCategoryId by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        try {
            categories = RetrofitClient.instance.getCategories()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("MI CUENTA", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = orangeBrand) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(80.dp).background(orangeBrand, CircleShape), contentAlignment = Alignment.Center) {
                Text(session.getUserName().take(1).uppercase(), fontSize = 35.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(10.dp))
            Text(session.getUserName(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Surface(color = if(session.getUserRole() == "Contratista") orangeBrand else Color.Gray, shape = RoundedCornerShape(4.dp)) {
                Text(session.getUserRole().uppercase(), color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }

            Spacer(Modifier.height(24.dp))

            if (session.getUserRole() == "Contratista") {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CONFIGURACIÓN PROFESIONAL", color = orangeBrand, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))

                        ProfileField("Nombre del Negocio", businessName, { businessName = it }, orangeBrand)
                        ProfileField("Años de Experiencia", experience, { experience = it }, orangeBrand, KeyboardType.Number)
                        ProfileField("Breve Biografía", bio, { bio = it }, orangeBrand)

                        // --- DROPDOWN SELECTOR ---
                        Text("CATEGORÍA DE SERVICIO", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategoryName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = orangeBrand,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color(0xFF1A1A1A))
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.CategoryName, color = Color.White) },
                                        onClick = {
                                            selectedCategoryName = category.CategoryName
                                            selectedCategoryId = category.CategoryID
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (selectedCategoryId == -1) {
                                    Toast.makeText(context, "Elige una categoría", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                scope.launch {
                                    isSaving = true
                                    try {
                                        val response = RetrofitClient.instance.updateContractorProfile(
                                            UpdateContractorRequest(session.getUserId(), businessName, bio, experience.toIntOrNull() ?: 0, selectedCategoryId)
                                        )
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "¡Perfil actualizado!", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                    } finally { isSaving = false }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = orangeBrand),
                            enabled = !isSaving
                        ) {
                            if (isSaving) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                            else Text("GUARDAR CAMBIOS", color = Color.Black, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { session.logout(); onLogoutSuccess() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CERRAR SESIÓN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit, color: Color, kbType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = kbType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = Color.DarkGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}