package com.example.biomax.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.AuditLog
import com.example.biomax.model.UserAccount
import com.example.biomax.security.SecurityCryptoManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SecurityComplianceScreen(
    user: UserAccount?,
    auditLogs: List<AuditLog>,
    onOpenMfaChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val secInfo = SecurityCryptoManager.getEncryptionInfo()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    var auditSearchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(auditLogs, auditSearchQuery) {
        if (auditSearchQuery.isBlank()) auditLogs
        else auditLogs.filter {
            it.action.contains(auditSearchQuery, ignoreCase = true) ||
            it.entityAffected.contains(auditSearchQuery, ignoreCase = true) ||
            it.actorEmail.contains(auditSearchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("security_compliance_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Section: MFA Status & Fast Challenge Trigger
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = BioGreenPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = BioGreenPrimary, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Multi-Factor Authentication", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("FIPS 140-3 Hardware Token / TOTP 6-Digit PIN", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = StatusSettled.copy(alpha = 0.2f)) {
                            Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusSettled, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onOpenMfaChallenge,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BioGreenDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_mfa_challenge_button")
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify MFA Authentication Challenge", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: End-to-End Encryption Specification
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BioGreenDark.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, BioGreenDark.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BioGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("End-to-End Cryptography Engine", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BioGreenDark)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = StatusSettled.copy(alpha = 0.2f)) {
                            Text("ENCRYPTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusSettled, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Cipher Suite: ${secInfo.cipherSuite}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("• Hardware Handshake: TLS 1.3 with Curve25519 Key Exchange", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Public Key Fingerprint: ${secInfo.keyFingerprint}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Compliance: ${secInfo.certificateStatus}", fontSize = 10.sp, color = BioGreenDark, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Section: International Regulatory Compliance Grid
        item {
            Text(
                text = "GLOBAL REGULATORY COMPLIANCE LEDGER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ComplianceStandardCard(
                    title = "EU RED II Directive (Renewable Energy)",
                    standard = "Annex IX Part A - Food Scraps GHG Abatement Certified",
                    status = "Certified (Level 1)",
                    color = BioGreenPrimary
                )
                ComplianceStandardCard(
                    title = "ISO 14001:2015 Environmental Standard",
                    standard = "Audited Zero-Landfill Chain of Custody Protocol",
                    status = "Verified Active",
                    color = BioTealAccent
                )
                ComplianceStandardCard(
                    title = "US EPA Renewable Fuel Standard (RFS)",
                    standard = "D3 / D5 Renewable Identification Number (RIN) Generator",
                    status = "Compliance Synchronized",
                    color = BioAmberEnergy
                )
            }
        }

        // Section: Tamper-Proof Administrative Audit Log
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADMINISTRATIVE AUDIT LOGGING (${auditLogs.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = auditSearchQuery,
                onValueChange = { auditSearchQuery = it },
                placeholder = { Text("Filter audit trail by action or actor...", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        items(filteredLogs) { log ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.action,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = log.entityAffected,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Actor: ${log.actorEmail} (${log.actorRole.name})",
                            fontSize = 9.sp,
                            color = BioTealAccent
                        )
                        Text(
                            text = "Sig: ${log.securitySignature}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = BioGreenDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplianceStandardCard(
    title: String,
    standard: String,
    status: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(standard, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.15f)) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
