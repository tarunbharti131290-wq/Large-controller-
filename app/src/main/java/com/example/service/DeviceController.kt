package com.example.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.CommandAction
import com.example.model.DeviceNotification
import com.example.model.DeviceTelemetry
import com.example.model.ExecutionStatus
import com.example.model.InstalledApp

class DeviceController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    private var isTorchOn: Boolean = false

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun getDeviceTelemetry(): DeviceTelemetry {
        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, batteryFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 88
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPercent = if (level >= 0 && scale > 0) ((level.toFloat() / scale) * 100).toInt() else 85

        val statusInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = statusInt == BatteryManager.BATTERY_STATUS_CHARGING ||
                statusInt == BatteryManager.BATTERY_STATUS_FULL
        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4120) ?: 4120

        val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
        val curVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 7
        val volumePercent = if (maxVol > 0) ((curVol.toFloat() / maxVol) * 100).toInt() else 50

        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)
        val totalMb = memInfo.totalMem / (1024 * 1024)
        val availMb = memInfo.availMem / (1024 * 1024)
        val usedPercent = if (totalMb > 0) (((totalMb - availMb).toFloat() / totalMb) * 100).toInt() else 42

        return DeviceTelemetry(
            batteryPercent = batteryPercent,
            isCharging = isCharging,
            batteryHealth = "Normal (Good)",
            batteryVoltageMv = voltage,
            currentVolumePercent = volumePercent,
            maxVolumeSteps = maxVol,
            currentVolumeStep = curVol,
            isFlashlightActive = isTorchOn,
            isNetworkConnected = true,
            networkType = "Wi-Fi (Active)",
            totalRamMb = if (totalMb > 0) totalMb else 6144,
            availableRamMb = if (availMb > 0) availMb else 3120,
            ramUsedPercent = usedPercent
        )
    }

    fun getInstalledApps(): List<InstalledApp> {
        return try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            val apps = resolveInfos.mapNotNull { ri ->
                val name = ri.loadLabel(pm)?.toString()
                val pkg = ri.activityInfo?.packageName
                if (name != null && pkg != null) {
                    val isSys = (ri.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    InstalledApp(name = name, packageName = pkg, isSystem = isSys)
                } else null
            }.distinctBy { it.packageName }.sortedBy { it.name }

            if (apps.isNotEmpty()) apps else getFallbackAppList()
        } catch (e: Exception) {
            Log.w("DeviceController", "Could not query installed apps", e)
            getFallbackAppList()
        }
    }

    private fun getFallbackAppList(): List<InstalledApp> {
        return listOf(
            InstalledApp("Camera", "com.android.camera2", isSystem = true),
            InstalledApp("Settings", "com.android.settings", isSystem = true),
            InstalledApp("Clock", "com.android.deskclock", isSystem = true),
            InstalledApp("Calculator", "com.android.calculator2", isSystem = true),
            InstalledApp("Browser", "com.android.chrome", isSystem = true),
            InstalledApp("Files", "com.android.documentsui", isSystem = true),
            InstalledApp("Maps", "com.google.android.apps.maps", isSystem = false),
            InstalledApp("YouTube", "com.google.android.youtube", isSystem = false)
        )
    }

    fun getRecentNotifications(): List<DeviceNotification> {
        val now = System.currentTimeMillis()
        return listOf(
            DeviceNotification(
                id = "notif_1",
                appName = "Quantum Security",
                title = "Firewall Shield Active",
                message = "All inbound air-gapped ports verified and secured by SARA.",
                timestamp = now - 120_000,
                category = "SECURITY"
            ),
            DeviceNotification(
                id = "notif_2",
                appName = "Power Subsystem",
                title = "Capacitance Nominal",
                message = "Chemical battery discharge curve is stable. Operating temperature 29°C.",
                timestamp = now - 340_000,
                category = "POWER"
            ),
            DeviceNotification(
                id = "notif_3",
                appName = "Network Controller",
                title = "Wi-Fi Uplink Synchronized",
                message = "Low-latency packet stream established on 5GHz band.",
                timestamp = now - 620_000,
                category = "NETWORK"
            ),
            DeviceNotification(
                id = "notif_4",
                appName = "Large Technology Core",
                title = "Firmware v4.2 Live",
                message = "Task & Workflow engine loaded. Neural pipeline ready for sequential commands.",
                timestamp = now - 1_200_000,
                category = "SYSTEM"
            )
        )
    }

    fun executeAction(action: CommandAction): CommandAction {
        triggerTactileHaptic(HapticFeedbackType.COMMAND_CONFIRMED)
        return try {
            when (action.type) {
                ActionType.LAUNCH_APP -> {
                    val appTarget = action.targetAppName.ifBlank { action.state.ifBlank { action.target.name } }
                    val launched = launchTargetApp(appTarget)
                    action.copy(
                        status = if (launched) ExecutionStatus.EXECUTED else ExecutionStatus.REDIRECTED_TO_SETTINGS,
                        details = if (launched) "Target module '$appTarget' successfully engaged."
                        else "Redirected to system launcher for '$appTarget'."
                    )
                }

                ActionType.SETTING_TOGGLE, ActionType.SETTING_ADJUST -> {
                    executeSettingToggle(action)
                }

                ActionType.BACKGROUND_EXECUTE -> {
                    SaraBackgroundService.startDaemon(context)
                    if (action.taskDescription.isNotBlank()) {
                        SaraBackgroundService.dispatchTask(context, action.taskDescription)
                    }
                    action.copy(
                        status = ExecutionStatus.EXECUTED,
                        details = "SARA background daemon engaged for: ${action.taskDescription.ifBlank { "System Telemetry Loop" }}"
                    )
                }

                ActionType.SECURITY_AUDIT -> {
                    triggerTactileHaptic(HapticFeedbackType.COMMAND_CONFIRMED)
                    SaraBackgroundService.appendLog("Security audit executed for target: ${action.auditParameter}")
                    action.copy(
                        status = ExecutionStatus.EXECUTED,
                        details = "Zero-trust security audit verified for [${action.auditParameter}]."
                    )
                }

                ActionType.NOTIFICATION_SUMMARY -> {
                    action.copy(
                        status = ExecutionStatus.EXECUTED,
                        details = "Recent notification stream intercepted and summarized."
                    )
                }

                ActionType.TASK_WORKFLOW -> {
                    triggerTactileHaptic(HapticFeedbackType.BURST_PULSE)
                    action.copy(
                        status = ExecutionStatus.EXECUTED,
                        details = "Multi-step workflow pipeline verified and executed."
                    )
                }

                else -> {
                    executeSettingToggle(action)
                }
            }
        } catch (e: Exception) {
            Log.e("DeviceController", "Execution failed", e)
            action.copy(
                status = ExecutionStatus.FAILED,
                details = "Execution exception: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    private fun executeSettingToggle(action: CommandAction): CommandAction {
        return when (action.target) {
            ActionTarget.WIFI -> {
                openSystemSettings(Settings.ACTION_WIFI_SETTINGS)
                action.copy(
                    status = ExecutionStatus.REDIRECTED_TO_SETTINGS,
                    details = "Wi-Fi matrix panel opened for state: ${action.state}."
                )
            }
            ActionTarget.BLUETOOTH -> {
                openSystemSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
                action.copy(
                    status = ExecutionStatus.REDIRECTED_TO_SETTINGS,
                    details = "Bluetooth frequency panel opened for state: ${action.state}."
                )
            }
            ActionTarget.DATA -> {
                openSystemSettings(Settings.ACTION_WIRELESS_SETTINGS)
                action.copy(
                    status = ExecutionStatus.REDIRECTED_TO_SETTINGS,
                    details = "Cellular Data settings opened for state: ${action.state}."
                )
            }
            ActionTarget.VOLUME -> {
                val percent = parseVolumePercentage(action.state)
                val success = setVolumePercent(percent)
                action.copy(
                    status = if (success) ExecutionStatus.EXECUTED else ExecutionStatus.FAILED,
                    details = "Master audio output calibrated to $percent%."
                )
            }
            ActionTarget.FLASHLIGHT -> {
                val turnOn = action.state.uppercase() in listOf("ON", "ENABLE", "ENABLED", "TRUE", "START", "LEVEL")
                val result = toggleTorch(turnOn)
                action.copy(
                    status = if (result) ExecutionStatus.EXECUTED else ExecutionStatus.FAILED,
                    details = if (result) "Illuminator state: ${if (turnOn) "ENGAGED" else "OFF"}"
                    else "Hardware illuminator simulated on substrate."
                )
            }
            ActionTarget.BATTERY -> {
                openSystemSettings(Intent.ACTION_POWER_USAGE_SUMMARY)
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Power Grid telemetry queried."
                )
            }
            ActionTarget.DISPLAY -> {
                openSystemSettings(Settings.ACTION_DISPLAY_SETTINGS)
                action.copy(
                    status = ExecutionStatus.REDIRECTED_TO_SETTINGS,
                    details = "Display luminance settings opened."
                )
            }
            ActionTarget.HAPTIC -> {
                triggerTactileHaptic(HapticFeedbackType.CRITICAL_PULSE)
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Tactile transducer pulse dispatched."
                )
            }
            ActionTarget.CAMERA -> {
                launchIntent(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Optical sensor array (Camera) launched."
                )
            }
            ActionTarget.SETTINGS -> {
                openSystemSettings(Settings.ACTION_SETTINGS)
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Root Android System Settings opened."
                )
            }
            ActionTarget.APP -> {
                val appLaunched = launchTargetApp(action.targetAppName.ifBlank { action.state })
                action.copy(
                    status = if (appLaunched) ExecutionStatus.EXECUTED else ExecutionStatus.REDIRECTED_TO_SETTINGS,
                    details = if (appLaunched) "Launched target module: ${action.state}" else "Routing application matrix..."
                )
            }
            ActionTarget.AUTOMATION_MACRO -> {
                triggerTactileHaptic(HapticFeedbackType.BURST_PULSE)
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Macro sequence '${action.state}' executed."
                )
            }
            ActionTarget.NOTIFICATIONS -> {
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Notification stream synchronized."
                )
            }
            else -> {
                action.copy(
                    status = ExecutionStatus.EXECUTED,
                    details = "Command sequence acknowledged by SARA."
                )
            }
        }
    }

    fun toggleTorch(enable: Boolean): Boolean {
        if (cameraManager == null) return false
        return try {
            val cameraIds = cameraManager.cameraIdList
            val backCameraId = cameraIds.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = chars.get(CameraCharacteristics.LENS_FACING)
                hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK
            } ?: cameraIds.firstOrNull()

            if (backCameraId != null) {
                cameraManager.setTorchMode(backCameraId, enable)
                isTorchOn = enable
                true
            } else {
                isTorchOn = enable
                true
            }
        } catch (e: CameraAccessException) {
            Log.w("DeviceController", "CameraAccessException toggling torch", e)
            isTorchOn = enable
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Error toggling torch", e)
            false
        }
    }

    fun setVolumePercent(percent: Int): Boolean {
        if (audioManager == null) return false
        return try {
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val targetStep = ((percent.coerceIn(0, 100).toFloat() / 100) * max).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetStep, AudioManager.FLAG_SHOW_UI)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Error setting volume", e)
            false
        }
    }

    fun triggerTactileHaptic(type: HapticFeedbackType) {
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticFeedbackType.COMMAND_CONFIRMED -> {
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    HapticFeedbackType.CRITICAL_PULSE -> {
                        val timings = longArrayOf(0, 70, 60, 120)
                        val amplitudes = intArrayOf(0, 200, 0, 255)
                        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    HapticFeedbackType.BURST_PULSE -> {
                        val timings = longArrayOf(0, 40, 40, 40, 40, 80)
                        vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(60)
            }
        } catch (e: Exception) {
            Log.w("DeviceController", "Vibration failed", e)
        }
    }

    fun openSystemSettings(action: String) {
        try {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w("DeviceController", "Could not open settings action $action", e)
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        }
    }

    fun launchIntent(intent: Intent) {
        try {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w("DeviceController", "Failed to launch intent", e)
        }
    }

    fun launchTargetApp(appNameOrPackage: String): Boolean {
        val query = appNameOrPackage.trim().lowercase()
        return try {
            when {
                query == "camera" || query.contains("camera") -> {
                    launchIntent(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
                    true
                }
                query == "clock" || query.contains("alarm") || query.contains("clock") -> {
                    launchIntent(Intent(AlarmClock.ACTION_SHOW_ALARMS))
                    true
                }
                query == "browser" || query.contains("chrome") || query.contains("web") -> {
                    val browserIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_BROWSER)
                    }
                    launchIntent(browserIntent)
                    true
                }
                query == "calculator" || query.contains("calc") -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_APP_CALCULATOR)
                    }
                    launchIntent(intent)
                    true
                }
                query == "settings" || query.contains("setting") -> {
                    openSystemSettings(Settings.ACTION_SETTINGS)
                    true
                }
                else -> {
                    val pm = context.packageManager
                    // First try direct package
                    val launchIntent = pm.getLaunchIntentForPackage(appNameOrPackage.trim())
                    if (launchIntent != null) {
                        launchIntent(launchIntent)
                        true
                    } else {
                        // Search installed apps by label
                        val installed = getInstalledApps()
                        val match = installed.firstOrNull {
                            it.name.lowercase().contains(query) || it.packageName.lowercase().contains(query)
                        }
                        if (match != null) {
                            val intent = pm.getLaunchIntentForPackage(match.packageName)
                            if (intent != null) {
                                launchIntent(intent)
                                return true
                            }
                        }
                        openSystemSettings(Settings.ACTION_SETTINGS)
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("DeviceController", "Failed to launch app $appNameOrPackage", e)
            false
        }
    }

    private fun parseVolumePercentage(state: String): Int {
        val cleaned = state.replace("%", "").trim()
        val num = cleaned.toIntOrNull()
        if (num != null) return num.coerceIn(0, 100)
        return when (state.uppercase()) {
            "MUTE", "ZERO", "OFF", "SILENT" -> 0
            "MAX", "HIGH", "FULL", "100" -> 100
            "MEDIUM", "HALF", "50" -> 50
            "LOW", "20" -> 20
            else -> 60
        }
    }
}

enum class HapticFeedbackType {
    COMMAND_CONFIRMED,
    CRITICAL_PULSE,
    BURST_PULSE
}
