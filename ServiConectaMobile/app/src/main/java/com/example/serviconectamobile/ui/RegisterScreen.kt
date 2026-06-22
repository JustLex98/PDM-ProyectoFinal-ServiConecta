package com.example.serviconectamobile.ui

import android.widget.Toast
import androidx.compose.foundation.*
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
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBack: () -> Unit) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val orangeBrand = Color(0xFFFF9900)

    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CREAR CUENTA", color = orangeBrand, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(32.dp))

        RegisterInputField("Nombre", viewModel.firstName, { viewModel.firstName = it }, orangeBrand)
        RegisterInputField("Apellido", viewModel.lastName, { viewModel.lastName = it }, orangeBrand)
        RegisterInputField("Email", viewModel.email, { viewModel.email = it }, orangeBrand)
        RegisterInputField("Contraseña", viewModel.password, { viewModel.password = it }, orangeBrand, true)

        Spacer(Modifier.height(16.dp))
        Text("¿Cómo quieres usar ServiConecta?", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = viewModel.selectedRole == "Cliente", onClick = { viewModel.selectedRole = "Cliente" }, colors = RadioButtonDefaults.colors(selectedColor = orangeBrand))
            Text("Cliente", color = Color.White)
            Spacer(Modifier.width(20.dp))
            RadioButton(selected = viewModel.selectedRole == "Contratista", onClick = { viewModel.selectedRole = "Contratista" }, colors = RadioButtonDefaults.colors(selectedColor = orangeBrand))
            Text("Contratista", color = Color.White)
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(viewModel.errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onRegisterClick() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orangeBrand),
            shape = RoundedCornerShape(12.dp),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(color = Color.Black)
            else Text("FINALIZAR REGISTRO", color = Color.Black, fontWeight = FontWeight.Black)
        }
        TextButton(onClick = onBack) { Text("Cancelar", color = orangeBrand) }
    }
}

@Composable
fun RegisterInputField(label: String, value: String, onValueChange: (String) -> Unit, color: Color, isPass: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        visualTransformation = if (isPass) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = color, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    )
}