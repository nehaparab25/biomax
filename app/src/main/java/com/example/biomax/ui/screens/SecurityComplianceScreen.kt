package com.example.biomax.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("security_compliance_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Section: Cryptographic Security Badge & MFA status
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Multi-Factor & FIPS 140-3 Cryptography", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Hardware Token & TOTP 6-Digit PIN", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenMfaChallenge,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_mfa_challenge_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Launch Security Token & Key Inspector", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Encryption Protocols & Standards
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cryptographic Key Specifications", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))

                    CryptoSpecRow("Data Encryption Standard", secInfo.cipherSuite)
                    CryptoSpecRow("Logistics Ledger Integrity", secInfo.keyFingerprint)
                    CryptoSpecRow("Authentication Protocol", "TOTP (RFC 6238) / SHA-512")
                    CryptoSpecRow("Regulatory Compliance", secInfo.certificateStatus)
                }
            }
        }

        // Section: Searchable Audit Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IMMUTABLE AUDIT LOGS (${filteredLogs.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
                Text("Tamper-Evident SHA-256", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = auditSearchQuery,
                onValueChange = { auditSearchQuery = it },
                placeholder = { Text("Filter audit logs by action, user or transaction...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_search_input")
            )
        }

        items(filteredLogs, key = { it.id }) { log ->
            AuditLogRowCard(log = log, dateFormat = dateFormat)
        }
    }
}

@Composable
private fun CryptoSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun AuditLogRowCard(
    log: AuditLog,
    dateFormat: SimpleDateFormat
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.action,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = dateFormat.format(Date(log.timestamp)),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                    text = "${log.actorEmail} (${log.actorRole})",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sig: ${log.securitySignature.take(12)}...",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
