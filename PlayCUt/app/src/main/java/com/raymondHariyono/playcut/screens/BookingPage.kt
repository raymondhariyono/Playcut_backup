package com.raymondHariyono.playcut.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.data.Barber

@Composable
fun BookingPage(
    navController: NavController,
    barber: Barber,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTime by rememberSaveable { mutableStateOf("") }
    var selectedService by rememberSaveable { mutableStateOf("") }
    val selectedOthersService = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<String>() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.book_with, barber.name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.select_service), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ServicesSelectionDropDown { selectedService = it }

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.select_additional_service), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OtherServicesGrid { newList ->
                selectedOthersService.clear()
                selectedOthersService.addAll(newList)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.select_available_time), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            TimeSelectionGrid(barber = barber) { selectedTime = it }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (selectedTime.isEmpty() || selectedService.isEmpty() || selectedOthersService.isEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_select_all),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.booking_confirmed, barber.name, selectedTime),
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.confirm_booking), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ServicesSelectionDropDown(onServiceSelected: (String) -> Unit) {
    val dropdownServices = listOf("Haircut", "Haircut + Keramas", "Haircut + Keramas + Treatment")
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedDropdownService by rememberSaveable { mutableStateOf("") }

    onServiceSelected(selectedDropdownService)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedDropdownService,
            onValueChange = {},
            label = { Text(stringResource(R.string.service)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropdownServices.forEach { service ->
                DropdownMenuItem(
                    text = { Text(service) },
                    onClick = {
                        selectedDropdownService = service
                        onServiceSelected(service)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OtherServicesGrid(onServiceSelected: (List<String>) -> Unit) {
    val otherServices = listOf("Coloring", "Shave", "Hair Spa", "Perming", "Smoothing", "Braids")
    val selectedService = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>()
    }
    onServiceSelected(selectedService)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(otherServices) { service ->
            val isSelected = selectedService.contains(service)
            Card(
                modifier = Modifier
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if(selectedService.contains(service)) {
                            selectedService.remove(service)
                        } else{
                            selectedService.add(service)
                        }
                        onServiceSelected(selectedService)
                    }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = service,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSelectionGrid(barber: Barber, onTimeSelected: (String) -> Unit) {
    var selectedTime by rememberSaveable { mutableStateOf("") }

    onTimeSelected(selectedTime)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        items(barber.availableTimes) { time ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTime = time
                        onTimeSelected(time)
                    }
                    .border(
                        width = if (selectedTime == time) 2.dp else 1.dp,
                        color = if (selectedTime == time) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTime == time) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time,
                        color = if (selectedTime == time) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
