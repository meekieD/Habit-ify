package com.dyusov.feature.habit.details.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HabitDetailsScreenPlaceholder(
    habitId: String,
    onFirstScreenButtonClick: () -> Unit,
    onSecondScreenButtonClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("HabitDetailsScreenPlaceholder habitId: $habitId")
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = onFirstScreenButtonClick) {
                    Text("Go to Agenda")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onSecondScreenButtonClick) {
                    Text("Go to Add Edit")
                }
            }
        }
    }
}