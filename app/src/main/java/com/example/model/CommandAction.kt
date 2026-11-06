package com.example.model

enum class ActionType {
    SETTING_TOGGLE,
    LAUNCH_APP,
    BACKGROUND_EXECUTE,
    SECURITY_AUDIT,
    SETTING_ADJUST,
    SYSTEM_COMMAND,
    TASK_WORKFLOW,
    NOTIFICATION_SUMMARY,
    PROMPT_GENERATION
}

enum class ActionTarget {
    WIFI,
    BLUETOOTH,
    VOLUME,
    DATA,
    FLASHLIGHT,
    BATTERY,
    DISPLAY,
    HAPTIC,
    CAMERA,
    SETTINGS,
    NOTIFICATIONS,
    APP,
    DIAGNOSTICS,
    PROMPT_ENGINE,
    CYBERSECURITY,
    BACKGROUND_DAEMON,
    AUTOMATION_MACRO,
    MULTI_STEP,
    UNKNOWN
}

enum class ExecutionStatus {
    READY,
    EXECUTED,
    FAILED,
    REDIRECTED_TO_SETTINGS
}

data class CommandAction(
    val type: ActionType,
    val target: ActionTarget,
    val state: String = "",
    val targetAppName: String = "",
    val taskDescription: String = "",
    val auditParameter: String = "",
    val rawBlock: String,
    val jsonString: String,
    var status: ExecutionStatus = ExecutionStatus.READY,
    val details: String = ""
)

data class InstalledApp(
    val name: String,
    val packageName: String,
    val isSystem: Boolean = false
)

data class DeviceNotification(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "SYSTEM"
)

data class WorkflowStep(
    val stepIndex: Int,
    val label: String,
    val action: CommandAction,
    var isExecuted: Boolean = false
)
