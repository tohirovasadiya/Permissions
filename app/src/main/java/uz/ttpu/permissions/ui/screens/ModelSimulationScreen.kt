package uz.ttpu.permissions.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ttpu.permissions.model.PermissionModel

@Composable
fun ModelSimulationScreen(modifier: Modifier = Modifier) {
    val model = remember { PermissionModel() }
    val logs = remember { mutableStateListOf<String>() }

    fun runAuto4StepTest() {
        logs.clear()
        val testModel = PermissionModel()
        logs.add("Boshlang'ich holat: $testModel")

        logs.add("\n1-so'rov (Rad etish / Deny):")
        logs.add(testModel.request(userAllows = false))

        logs.add("\n2-so'rov (Rad etish / Deny):")
        logs.add(testModel.request(userAllows = false))

        logs.add("\n3-so'rov (Qayta so'rov chaqiruvi):")
        logs.add(testModel.request(userAllows = false))

        logs.add("\n4-so'rov (Ruxsat so'rashga urinish):")
        logs.add(testModel.request(userAllows = true))

        logs.add("\nYakuniy holat: $testModel")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "1-topshiriq: PermissionModel Simulyatsiyasi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Joriy Model Holati:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "• granted: ${model.granted}", fontSize = 16.sp)
                Text(text = "• rationale: ${model.rationale}", fontSize = 16.sp)
                Text(text = "• dialogShows: ${model.dialogShows}", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val result = model.request(userAllows = true)
                    logs.add("Interaktiv User [Ruxsat berdi] -> $result")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Allow (True)")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val result = model.request(userAllows = false)
                    logs.add("Interaktiv User [Rad etdi] -> $result")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("Deny (False)")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { runAuto4StepTest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🚀 4 ta qadamli testni avtomatik ishga tushirish")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Console Output Log:",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (logs.isEmpty()) {
                    Text(
                        text = "Testni boshlash uchun yuqoridagi tugmalardan birini bosing...",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                } else {
                    logs.forEach { log ->
                        Text(
                            text = log,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
