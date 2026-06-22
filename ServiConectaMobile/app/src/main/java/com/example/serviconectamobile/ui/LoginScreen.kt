package com.example.serviconectamobile.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviconectamobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val orangeBrand = Color(0xFFFF9900)

    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            Toast.makeText(context, "¡Bienvenido a ServiConecta!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetStates() // Limpiar éxito para futuros logins
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Servi", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black)
        Surface(color = orangeBrand, shape = RoundedCornerShape(4.dp)) {
            Text("CONECTA", color = Color.Black, fontSize = 40.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orangeBrand,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Contraseña", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orangeBrand,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(viewModel.errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onLoginClick(context) }, // <--- PASAMOS EL CONTEXTO AQUÍ
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.isLoading) Color.Gray else orangeBrand),
            shape = RoundedCornerShape(12.dp),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            else Text("INGRESAR", color = Color.Black, fontWeight = FontWeight.Black)
        }

        TextButton(onClick = onNavigateToRegister, modifier = Modifier.padding(top = 16.dp)) {
            Row {
                Text("¿No tienes cuenta? ", color = Color.White)
                Text("Regístrate", color = orangeBrand, fontWeight = FontWeight.Bold)
            }
        }
    }
}