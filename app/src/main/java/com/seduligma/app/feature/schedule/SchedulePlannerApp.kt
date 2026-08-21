package com.seduligma.app.feature.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.RecurrenceRule
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val displayFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM · HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulePlannerApp() {
    val viewModel: ScheduleListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showEditor by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Seduligma", fontWeight = FontWeight.Bold)
                        Text("Personal scheduler · Foundation beta", style = MaterialTheme.typography.labelSmall)
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showEditor = true
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create schedule")
            }
        },
    ) { padding ->
        SchedulePlannerScreen(
            modifier = Modifier.padding(padding),
            schedules = uiState.schedules,
            isLoading = uiState.isLoading,
            deviceHealth = uiState.deviceHealth,
            onPause = viewModel::pauseSchedule,
            onCancel = viewModel::cancelSchedule,
            onActivate = viewModel::activateSchedule,
        )
    }
    if (showEditor) {
        ScheduleEditorDialog(
            onDismiss = { showEditor = false },
            onSave = { title, message, scheduledAt, timezoneId, recurrence ->
                viewModel.createDraft(
                    title = title,
                    messagePreview = message,
                    scheduledAt = scheduledAt,
                    timezoneId = timezoneId,
                    recurrence = recurrence,
                )
                showEditor = false
            },
        )
    }
}

@Composable
private fun SchedulePlannerScreen(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    isLoading: Boolean,
    deviceHealth: DeviceHealthReport,
    onPause: (String) -> Unit,
    onCancel: (String) -> Unit,
    onActivate: (Schedule) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Device health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        deviceHealthMessage(deviceHealth),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        "Exact alarms: ${healthLabel(deviceHealth.exactAlarm)} · Notifications: ${healthLabel(deviceHealth.notifications)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item { Text("Your schedules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (isLoading) {
            item { Text("Loading local schedules…") }
        } else if (schedules.isEmpty()) {
            item { Text("No schedules yet. Tap + to create your first local draft.") }
        }
        items(schedules, key = { it.id }) { schedule ->
            ScheduleCard(
                schedule = schedule,
                onPause = onPause,
                onCancel = onCancel,
                onActivate = onActivate,
            )
        }
    }
}

private fun deviceHealthMessage(report: DeviceHealthReport): String = when (report.overall) {
    HealthState.READY -> "Your device has the permissions required for the current local scheduling beta."
    HealthState.ACTION_REQUIRED -> "Exact alarm permission is required before a schedule can run at the selected time."
    HealthState.LIMITED -> "Scheduling can remain limited until notifications or device permissions are enabled."
}

private fun healthLabel(state: HealthState): String = state.name.lowercase().replace('_', ' ')

@Composable
private fun ScheduleCard(
    schedule: Schedule,
    onPause: (String) -> Unit,
    onCancel: (String) -> Unit,
    onActivate: (Schedule) -> Unit,
) {
    val localTime = schedule.scheduledAt.atZone(ZoneId.of(schedule.timezoneId)).format(displayFormatter)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(schedule.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(schedule.messagePreview, style = MaterialTheme.typography.bodyMedium)
            Text("$localTime · ${schedule.timezoneId}", style = MaterialTheme.typography.labelMedium)
            AssistChip(
                onClick = {},
                label = { Text(schedule.state.name.lowercase().replace('_', ' ')) },
            )
            if (schedule.state == com.seduligma.app.domain.model.ScheduleState.DRAFT ||
                schedule.state == com.seduligma.app.domain.model.ScheduleState.NEEDS_PERMISSION ||
                schedule.state == com.seduligma.app.domain.model.ScheduleState.PAUSED
            ) {
                Button(onClick = { onActivate(schedule) }) { Text("Activate") }
            }
            if (schedule.state != com.seduligma.app.domain.model.ScheduleState.CANCELLED) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (schedule.state != com.seduligma.app.domain.model.ScheduleState.PAUSED) {
                        Button(onClick = { onPause(schedule.id) }) { Text("Pause") }
                    }
                    Button(onClick = { onCancel(schedule.id) }) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
private fun ScheduleEditorDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Instant, String, RecurrenceRule) -> Unit,
) {
    val context = LocalContext.current
    val timezone = remember { ZoneId.systemDefault() }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf(LocalDateTime.now().plusHours(1).withSecond(0).withNano(0)) }
    var recurrence by remember { mutableStateOf(RecurrenceRule.ONCE) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val dateTimeText = dateTime.atZone(timezone).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy · HH:mm"))

    fun openDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dateTime = dateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
            },
            dateTime.year,
            dateTime.monthValue - 1,
            dateTime.dayOfMonth,
        ).show()
    }

    fun openTimePicker() {
        TimePickerDialog(
            context,
            { _, hour, minute -> dateTime = dateTime.withHour(hour).withMinute(minute) },
            dateTime.hour,
            dateTime.minute,
            true,
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create local schedule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "This creates a local draft only. No message is dispatched in this feature slice.",
                    style = MaterialTheme.typography.bodySmall,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Schedule title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message preview") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                Text("$dateTimeText · ${timezone.id}", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = ::openDatePicker) { Text("Choose date") }
                    TextButton(onClick = ::openTimePicker) { Text("Choose time") }
                }
                Text("Repeat", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RecurrenceRule.entries.forEach { rule ->
                        AssistChip(
                            onClick = { recurrence = rule },
                            label = { Text(rule.name.lowercase().replaceFirstChar { it.titlecase() }) },
                        )
                    }
                }
                validationError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        title.isBlank() -> validationError = "Add a schedule title."
                        message.isBlank() -> validationError = "Add a message preview."
                        dateTime.atZone(timezone).toInstant() <= Instant.now() -> validationError = "Choose a future date and time."
                        else -> onSave(title, message, dateTime.atZone(timezone).toInstant(), timezone.id, recurrence)
                    }
                },
            ) { Text("Save draft") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
