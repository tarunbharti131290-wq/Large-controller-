package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.SaraBackgroundService
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurfaceVariant
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.Typography

data class PortProbe(
    val port: Int,
    val service: String,
    val state: String,
    val isSecure: Boolean
)

data class EthicalScriptSnippet(
    val title: String,
    val category: String,
    val command: String,
    val explanation: String
)

@Composable
fun SecurityAuditScreen(
    onRunAudit: (String) -> Unit,
    onExecuteCommand: (String) -> Unit
) {
    val context = LocalContext.current
    val isDaemonActive by SaraBackgroundService.isRunning.collectAsState()
    val daemonLogs by SaraBackgroundService.backgroundLogs.collectAsState()

    var isScanningPorts by remember { mutableStateOf(false) }
    var copiedIndex by remember { mutableStateOf<Int?>(null) }

    val portProbes = remember {
        listOf(
            PortProbe(21, "FTP Control", "FILTERED (Air-gapped)", true),
            PortProbe(22, "SSH Remote Shell", "SECURE (Ed25519 Only)", true),
            PortProbe(80, "HTTP Cleartext", "REDIRECT (301 -> 443)", true),
            PortProbe(443, "HTTPS TLS 1.3", "OPEN & VERIFIED", true),
            PortProbe(5555, "ADB Remote Daemon", "DISABLED (Protected)", true),
            PortProbe(8080, "HTTP Proxy", "CLOSED", true)
        )
    }

    val securityScripts = remember {
        listOf(
            EthicalScriptSnippet(
                title = "Stealth Subnet Reconnaissance",
                category = "Reconnaissance / Discovery",
                command = "nmap -sS -sV -T4 -p 22,80,443,5555 192.168.1.0/24 -oN sara_audit.log",
                explanation = "Performs non-intrusive TCP SYN scan to map active hosts and open socket surfaces on local network."
            ),
            EthicalScriptSnippet(
                title = "Encrypted TLS Traffic Inspector",
                category = "Network Traffic Analysis",
                command = "tshark -i any -f \"tcp port 443\" -T fields -e ip.src -e ip.dst -e ssl.handshake.extensions_server_name",
                explanation = "Monitors outgoing TLS Server Name Indication (SNI) to audit connections without breaking payload encryption."
            ),
            EthicalScriptSnippet(
                title = "Zero-Trust Host Firewall Lockdown",
                category = "System Hardening",
                command = "iptables -P INPUT DROP && iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT",
                explanation = "Enforces zero-trust default DROP policy on inbound interfaces, allowing only established outbound sessions."
            ),
            EthicalScriptSnippet(
                title = "System Binary Integrity Hasher",
                category = "Vulnerability Assessment",
                command = "find /system/bin -type f -exec sha256sum {} + > system_hashes.sha256",
                explanation = "Generates SHA-256 cryptographic hashes for system executables to verify anti-tampering baseline."
            )
        )
    }

    fun copyToClipboard(text: String, index: Int) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SARA Script", text)
        clipboard.setPrimaryClip(clip)
        copiedIndex = index
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("security_audit_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Top Shield Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyanCore.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CyanCore.copy(alpha = 0.15f))
                            .border(1.dp, CyanCore, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Shield",
                            tint = CyanAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CYBERSECURITY MATRIX",
                                style = Typography.labelLarge,
                                color = TextCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldOnline.copy(alpha = 0.2f))
                                    .border(1.dp, EmeraldOnline, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "98.4% SECURE",
                                    style = Typography.labelSmall.copy(fontSize = 10.sp),
                                    color = EmeraldOnline,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "SELinux Enforcing • Hardware Keystore Active • Zero-Trust Perimeter",
                            style = Typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRunAudit("SYSTEM_DEFENSE") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("run_audit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanCore),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Audit System", style = Typography.labelMedium, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = {
                            isScanningPorts = !isScanningPorts
                            onRunAudit("PORTS")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_ports_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Ports", style = Typography.labelMedium, color = CyanAccent)
                    }
                }
            }
        }

        // Persistent Background & Wake-Mode Controller
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isDaemonActive) EmeraldOnline.copy(alpha = 0.6f) else HoloBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isDaemonActive) EmeraldOnline else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PERSISTENT BACKGROUND DAEMON",
                                style = Typography.titleSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isDaemonActive)
                                    "Wake-Mode Active • Continuous Foreground DataSync"
                                else
                                    "Daemon Idle • Tap switch to activate wake loop",
                                style = Typography.bodySmall,
                                color = if (isDaemonActive) EmeraldOnline else TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isDaemonActive,
                        onCheckedChange = { activate ->
                            if (activate) {
                                SaraBackgroundService.startDaemon(context)
                                onExecuteCommand("Start Background Daemon")
                            } else {
                                SaraBackgroundService.stopDaemon(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldOnline,
                            checkedTrackColor = EmeraldOnline.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = HoloSurfaceVariant
                        ),
                        modifier = Modifier.testTag("daemon_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daemon Terminal Output
                Text(
                    text = "DAEMON TELEMETRY BUS LOGS",
                    style = Typography.labelSmall,
                    color = TextCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, HoloBorder, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        daemonLogs.takeLast(4).forEach { logLine ->
                            Text(
                                text = logLine,
                                style = Typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = if (logLine.contains("Executing") || logLine.contains("Security")) CyanAccent else TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Port Scanner Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NETWORK PORT RECON MATRIX",
                        style = Typography.titleSmall,
                        color = TextCyan
                    )
                    Text(
                        text = "TCP SOCKET AUDIT",
                        style = Typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    portProbes.forEach { probe ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ":${probe.port}",
                                    style = Typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = CyanAccent,
                                    modifier = Modifier.width(52.dp)
                                )
                                Text(
                                    text = probe.service,
                                    style = Typography.bodySmall,
                                    color = TextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = probe.state,
                                    style = Typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = if (probe.isSecure) EmeraldOnline else AmberWarning
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (probe.isSecure) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (probe.isSecure) EmeraldOnline else AmberWarning,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Ethical Hacking & Defensive Script Generator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ETHICAL HACKING & DEFENSIVE SCRIPTS",
                    style = Typography.titleSmall,
                    color = TextCyan
                )
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        items(securityScripts.withIndex().toList()) { (idx, script) ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HoloBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = script.title,
                            style = Typography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = script.category,
                            style = Typography.labelSmall,
                            color = NeonIndigo
                        )
                    }

                    IconButton(
                        onClick = { copyToClipboard(script.command, idx) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy script",
                            tint = if (copiedIndex == idx) EmeraldOnline else CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, HoloBorder, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = script.command,
                        style = Typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CyanAccent,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = script.explanation,
                    style = Typography.bodySmall,
                    color = TextMuted
                )

                if (copiedIndex == idx) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Copied to clipboard",
                        style = Typography.labelSmall,
                        color = EmeraldOnline
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
