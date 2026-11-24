package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DataStoreDailyCheckRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.SeniorCareTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        DataStoreDailyCheckRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = IvoryBackground.toArgb(),
                darkScrim = IvoryBackground.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = IvoryBackground.toArgb(),
                darkScrim = IvoryBackground.toArgb()
            )
        )
        setContent {
            SeniorCareTheme {
                val viewModel: DailyCheckViewModel = viewModel(
                    factory = DailyCheckViewModel.factory(repository)
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshDate()
                }

                DisposableEffect(context, viewModel) {
                    val dateChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(ctx: Context?, intent: Intent?) {
                            viewModel.refreshDate()
                        }
                    }
                    val filter = IntentFilter().apply {
                        addAction(Intent.ACTION_DATE_CHANGED)
                        addAction(Intent.ACTION_TIME_CHANGED)
                        addAction(Intent.ACTION_TIMEZONE_CHANGED)
                    }
                    ContextCompat.registerReceiver(
                        context,
                        dateChangeReceiver,
                        filter,
                        ContextCompat.RECEIVER_EXPORTED
                    )
                    onDispose {
                        context.unregisterReceiver(dateChangeReceiver)
                    }
                }

                DailyCheckScreen(
                    uiState = uiState,
                    onToggleMedication = viewModel::toggleMedication,
                    onAddWaterCup = viewModel::addWaterCup,
                    onRemoveWaterCup = viewModel::removeWaterCup
                )
            }
        }
    }
}
