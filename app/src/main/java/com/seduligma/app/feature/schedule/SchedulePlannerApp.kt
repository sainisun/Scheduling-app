package com.seduligma.app.feature.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val displayFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM · HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulePlannerApp() {
    var schedules by remember { mutableStateOf(seedSchedules()) }
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
                    schedules = schedules + Schedule(
                        id = "local-${System.currentTimeMillis()}",
                        title = "New schedule",
                        messagePreview = "Schedule editor is the next feature slice.",
                        scheduledAt = Instant.now().plusSeconds(60 * 60),
                        timezoneId = ZoneId.systemDefault().id,
                        recurrence = RecurrenceRule.ONCE,
                        state = ScheduleState.DRAFT,
                    )
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create schedule")
            }
        },
    ) { padding ->
        SchedulePlannerScreen(
            modifier = Modifier.padding(padding),
            schedules = schedules,
        )
    }
}

@Composable
private fun SchedulePlannerScreen(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
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
                    Text("Foundation ready", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "This initial build creates a local planner only. No message is dispatched, and no personal credentials are collected.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Button(onClick = {}) { Text("Open device health (next slice)") }
                }
            }
        }
        item { Text("Your schedules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(schedules, key = { it.id }) { schedule ->
            ScheduleCard(schedule)
        }
    }
}

@Composable
private fun ScheduleCard(schedule: Schedule) {
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
        }
    }
}

private fun seedSchedules(): List<Schedule> = listOf(
    Schedule(
        id = "foundation-1",
        title = "Permission check",
        messagePreview = "The device-health workflow will validate notification and alarm access.",
        scheduledAt = Instant.now().plusSeconds(60 * 60 * 24),
        timezoneId = ZoneId.systemDefault().id,
        recurrence = RecurrenceRule.ONCE,
        state = ScheduleState.NEEDS_PERMISSION,
    ),
)
