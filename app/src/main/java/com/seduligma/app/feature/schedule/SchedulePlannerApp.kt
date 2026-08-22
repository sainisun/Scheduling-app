package com.seduligma.app.feature.schedule

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import com.seduligma.app.domain.model.LocalEvidence
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleEventType
import com.seduligma.app.domain.model.ScheduleState
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { viewModel.refreshDeviceHealth() }
    var showEditor by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<Schedule?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshDeviceHealth()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                    editingSchedule = null
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
            recentEvents = uiState.recentEvents,
            totalScheduleCount = uiState.totalScheduleCount,
            searchQuery = uiState.searchQuery,
            selectedFilter = uiState.selectedFilter,
            isLoading = uiState.isLoading,
            deviceHealth = uiState.deviceHealth,
            onRequestExactAlarm = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:${context.packageName}"),
                        ),
                    )
                }
            },
            onOpenNotificationSettings = {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    },
                )
            },
            onRequestNotifications = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        },
                    )
                }
            },
            onRefreshDeviceHealth = viewModel::refreshDeviceHealth,
            onPause = viewModel::pauseSchedule,
            onCancel = viewModel::cancelSchedule,
            onActivate = viewModel::activateSchedule,
            onEdit = { schedule ->
                editingSchedule = schedule
                showEditor = true
            },
            onSearchQueryChanged = viewModel::setSearchQuery,
            onFilterChanged = viewModel::setFilter,
        )
    }

    if (showEditor) {
        ScheduleEditorDialog(
            existing = editingSchedule,
            onDismiss = {
                editingSchedule = null
                showEditor = false
            },
            onSave = { title, message, scheduledAt, timezoneId, recurrence ->
                editingSchedule?.let { existing ->
                    viewModel.updateDraft(
                        existing = existing,
                        title = title,
                        messagePreview = message,
                        scheduledAt = scheduledAt,
                        timezoneId = timezoneId,
                        recurrence = recurrence,
                    )
                } ?: viewModel.createDraft(
                    title = title,
                    messagePreview = message,
                    scheduledAt = scheduledAt,
                    timezoneId = timezoneId,
                    recurrence = recurrence,
                )
                editingSchedule = null
                showEditor = false
            },
        )
    }
}

@Composable
internal fun SchedulePlannerScreen(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    recentEvents: List<ScheduleEvent> = emptyList(),
    totalScheduleCount: Int,
    searchQuery: String,
    selectedFilter: ScheduleListFilter,
    isLoading: Boolean,
    deviceHealth: DeviceHealthReport,
    onRequestExactAlarm: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onRequestNotifications: () -> Unit,
    onRefreshDeviceHealth: () -> Unit,
    onPause: (String) -> Unit,
    onCancel: (String) -> Unit,
    onActivate: (Schedule) -> Unit,
    onEdit: (Schedule) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (ScheduleListFilter) -> Unit,
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
                    Text(deviceHealthMessage(deviceHealth), style = MaterialTheme.typography.bodyMedium)
                    Text("Exact alarms: ${healthLabel(deviceHealth.exactAlarm)}", style = MaterialTheme.typography.bodySmall)
                    if (deviceHealth.exactAlarm == HealthState.ACTION_REQUIRED) {
                        Button(onClick = onRequestExactAlarm) { Text("Allow exact alarms") }
                    }
                    Text("Notifications: ${healthLabel(deviceHealth.notifications)}", style = MaterialTheme.typography.bodySmall)
                    if (deviceHealth.notifications != HealthState.READY) {
                        Text(
                            "Notifications are needed for manual-confirmation and schedule-ready reminders.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Button(onClick = onRequestNotifications) { Text("Request notifications") }
                        TextButton(onClick = onOpenNotificationSettings) { Text("Open notification settings") }
                    }
                    TextButton(onClick = onRefreshDeviceHealth) { Text("Refresh device health") }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your schedules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Search title or preview") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScheduleListFilter.entries.forEach { filter ->
                        AssistChip(
                            onClick = { onFilterChanged(filter) },
                            label = { Text(if (filter == selectedFilter) "✓ ${filter.label}" else filter.label) },
                        )
                    }
                }
                if (totalScheduleCount != schedules.size) {
                    Text(
                        "Showing ${schedules.size} of $totalScheduleCount schedules",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (isLoading) {
            item { Text("Loading local schedules…") }
        } else if (schedules.isEmpty()) {
            item {
                Text(
                    if (totalScheduleCount == 0) {
                        "No schedules yet. Tap + to create your first local draft."
                    } else {
                        "No schedules match this search or filter."
                    },
                )
            }
        }
        items(schedules, key = { it.id }) { schedule ->
            ScheduleCard(
                schedule = schedule,
                onPause = onPause,
                onCancel = onCancel,
                onActivate = onActivate,
                onEdit = onEdit,
            )
        }
        if (recentEvents.isNotEmpty()) {
            item {
                Text("Recent activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(recentEvents, key = { it.id }) { event ->
                ScheduleEventCard(event)
            }
        }
    }
}

@Composable
private fun ScheduleEventCard(event: ScheduleEvent) {
    val occurredAt = event.occurredAt.atZone(ZoneId.systemDefault()).format(displayFormatter)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(eventTitle(event.eventType), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(eventEvidenceMessage(event), style = MaterialTheme.typography.bodySmall)
            Text(occurredAt, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun eventTitle(eventType: ScheduleEventType): String = when (eventType) {
    ScheduleEventType.CREATED -> "Draft created"
    ScheduleEventType.ACTIVATED -> "Schedule activated"
    ScheduleEventType.PAUSED -> "Schedule paused"
    ScheduleEventType.CANCELLED -> "Schedule cancelled"
    ScheduleEventType.ATTEMPTED -> "Schedule attempt recorded"
    ScheduleEventType.OUTCOME_RECORDED -> "Schedule outcome recorded"
}

private fun eventEvidenceMessage(event: ScheduleEvent): String = when (event.localEvidence) {
    LocalEvidence.NOTIFICATION_POSTED -> "Local evidence: a review notification was posted. This does not prove a message was sent."
    LocalEvidence.USER_CONFIRMED -> "Local evidence: the user confirmed a local action. This does not prove delivery or read status."
    LocalEvidence.ALARM_REGISTERED -> "Local evidence: the device alarm was registered."
    LocalEvidence.NONE -> event.reasonCode?.name?.lowercase()?.replace('_', ' ') ?: "No local evidence was available."
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
    onEdit: (Schedule) -> Unit,
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
            if (schedule.state == ScheduleState.AWAITING_USER) {
                Text(
                    "Manual confirmation is required. Review this draft yourself; Seduligma will not send anything automatically.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (schedule.state in setOf(ScheduleState.DRAFT, ScheduleState.NEEDS_PERMISSION, ScheduleState.PAUSED)) {
                Button(onClick = { onActivate(schedule) }) { Text("Activate") }
            }
            if (schedule.state != ScheduleState.CANCELLED) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onEdit(schedule) }) { Text("Edit") }
                    if (schedule.state != ScheduleState.PAUSED) {
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
    existing: Schedule?,
    onDismiss: () -> Unit,
    onSave: (String, String, Instant, String, RecurrenceRule) -> Unit,
) {
    val context = LocalContext.current
    var timezoneId by remember(existing?.id) { mutableStateOf(existing?.timezoneId ?: ZoneId.systemDefault().id) }
    var title by remember(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var message by remember(existing?.id) { mutableStateOf(existing?.messagePreview.orEmpty()) }
    var dateTime by remember(existing?.id) {
        val existingZone = existing?.timezoneId?.takeIf(ScheduleEditorValidator::isValidTimezone)?.let(ZoneId::of)
            ?: ZoneId.systemDefault()
        mutableStateOf(
            existing?.scheduledAt?.atZone(existingZone)?.toLocalDateTime()
                ?: LocalDateTime.now().plusHours(1).withSecond(0).withNano(0),
        )
    }
    var recurrence by remember(existing?.id) { mutableStateOf(existing?.recurrence ?: RecurrenceRule.ONCE) }
    var validationError by remember(existing?.id) { mutableStateOf<String?>(null) }
    val timezoneForDisplay = timezoneId.takeIf(ScheduleEditorValidator::isValidTimezone)?.let(ZoneId::of)
        ?: ZoneId.systemDefault()
    val dateTimeText = dateTime.atZone(timezoneForDisplay).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy · HH:mm"))

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
        title = { Text(if (existing == null) "Create local schedule" else "Edit local schedule") },
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
                OutlinedTextField(
                    value = timezoneId,
                    onValueChange = { timezoneId = it },
                    label = { Text("Timezone (IANA)") },
                    supportingText = { Text("Example: Asia/Kolkata") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Text("$dateTimeText · ${timezoneForDisplay.id}", style = MaterialTheme.typography.bodyMedium)
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
                    val timezone = timezoneId.trim().takeIf(ScheduleEditorValidator::isValidTimezone)?.let(ZoneId::of)
                    val scheduledAt = timezone?.let { dateTime.atZone(it).toInstant() }
                    val error = if (scheduledAt == null) {
                        ScheduleEditorError.TIMEZONE_INVALID
                    } else {
                        ScheduleEditorValidator.validate(
                            input = ScheduleEditorInput(
                                title = title,
                                messagePreview = message,
                                scheduledAt = scheduledAt,
                                timezoneId = timezoneId,
                            ),
                            now = Instant.now(),
                        )
                    }
                    if (error != null) {
                        validationError = error.userMessage
                    } else {
                        onSave(title, message, checkNotNull(scheduledAt), timezoneId.trim(), recurrence)
                    }
                },
            ) { Text(if (existing == null) "Save draft" else "Save changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
