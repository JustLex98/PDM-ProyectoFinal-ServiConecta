package com.example.serviconectamobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviconectamobile.data.ContractorResponse
import com.example.serviconectamobile.viewmodel.ContractorsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorListScreen(
    categoryId: Int,
    onBack: () -> Unit,
    onContractorClick: (Int) -> Unit // Parámetro vital para la navegación
) {
    val viewModel: ContractorsViewModel = viewModel()
    val orangeBrand = Color(0xFFFF9900)

    LaunchedEffect(categoryId) {
        viewModel.loadContractors(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROFESIONALES", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = orangeBrand)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orangeBrand)
            }
        } else if (viewModel.contractors.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay profesionales disponibles", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(viewModel.contractors) { contractor ->
                    ContractorCard(contractor, orangeBrand, onContractorClick)
                }
            }
        }
    }
}

@Composable
fun ContractorCard(
    contractor: ContractorResponse,
    accentColor: Color,
    onContractorClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contractor.BusinessName ?: "${contractor.FirstName} ${contractor.LastName}",
                        color = accentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${contractor.YearsOfExperience} años de experiencia", color = Color.LightGray, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = accentColor, modifier = Modifier.size(16.dp))
                    Text(" 4.7", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onContractorClick(contractor.UserID) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("VER PERFIL COMPLETO", color = Color.Black, fontWeight = FontWeight.Black)
            }
        }
    }
}