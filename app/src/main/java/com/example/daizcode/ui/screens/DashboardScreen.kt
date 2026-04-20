package com.example.daizcode.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daizcode.data.model.DashboardState
import com.example.daizcode.ui.components.DetectionFeedItem
import com.example.daizcode.ui.components.LineChart
import com.example.daizcode.ui.components.PieChart
import com.example.daizcode.ui.components.StatCardsRow
import com.example.daizcode.ui.theme.AccentBlue
import com.example.daizcode.ui.theme.AccentBlueGlow
import com.example.daizcode.ui.theme.AccentGreen
import com.example.daizcode.ui.theme.AccentGreenGlow
import com.example.daizcode.ui.theme.AccentRed
import com.example.daizcode.ui.theme.AccentRedGlow
import com.example.daizcode.ui.theme.DarkBackground
import com.example.daizcode.ui.theme.TextSecondary
import com.example.daizcode.ui.theme.TextTertiary
import com.example.daizcode.ui.viewmodel.DashboardViewModel

/**
 * Main dashboard screen composable.
 * Displays stat cards, charts, and live detection feed.
 * Optimized for various screen sizes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚙",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SeeMeEdge",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Industrial Detection",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    LivePulseIndicator()
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        DashboardContent(
            state = state,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stat Cards Row
        item {
            StatCardsRow(
                totalProcessed = state.totalProcessed,
                goodCount = state.goodCount,
                defectiveCount = state.defectiveCount,
                accentBlue = AccentBlue,
                blueGlow = AccentBlueGlow,
                accentGreen = AccentGreen,
                greenGlow = AccentGreenGlow,
                accentRed = AccentRed,
                redGlow = AccentRedGlow
            )
        }

        // Charts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Line Chart
                LineChart(
                    dataPoints = state.defectsPerMinute,
                    timeLabels = state.timeLabels,
                    modifier = Modifier.weight(0.55f)
                )

                // Pie Chart
                PieChart(
                    goodCount = state.goodCount,
                    defectiveCount = state.defectiveCount,
                    modifier = Modifier.weight(0.45f)
                )
            }
        }

        // Live Feed Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "🔴",
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE FEED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${state.recentEvents.size} events",
                    fontSize = 10.sp,
                    color = TextTertiary
                )
            }
        }

        // Detection events list
        items(
            items = state.recentEvents,
            key = { it.id }
        ) { event ->
            DetectionFeedItem(event = event)
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LivePulseIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(pulseAlpha)
                .background(AccentGreen, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "LIVE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGreen,
            letterSpacing = 1.sp
        )
    }
}
