package com.example.biomax.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.*
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    authState: AuthState,
    onAuthEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF0C1912),
                        DarkBackground
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                UnauthenticatedLoginForm(
                    state = authState,
                    onAuthEvent = onAuthEvent
                )
            }
            is AuthState.Authenticating -> {
                AuthenticatingStateCard(state = authState)
            }
            is AuthState.MfaChallenge -> {
                MfaChallengeCard(
                    state = authState,
                    onAuthEvent = onAuthEvent
                )
            }
            is AuthState.Authenticated -> {
                // Handled in main parent navigation
            }
        }
    }
}

@Composable
private fun UnauthenticatedLoginForm(
    state: AuthState.Unauthenticated,
    onAuthEvent: (AuthEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Brand Logo & Title
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(BioEmeraldBright, Color(0xFF008744))
                    )
                )
                .border(1.5.dp, Color(0x66FFFFFF), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "Biomax Logo",
                tint = Color(0xFF042111),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "BIOMAX",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Text(
            text = "Food Waste to Clean Renewable Energy Exchange",
            color = DarkOnSurfaceVariant,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- MODE SWITCHER (STATE MACHINE TRIGGER) ---
        Text(
            text = "SELECT ACCESS CONSOLE MODE",
            color = BioEmeraldBright,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(4.dp)
                .testTag("login_mode_switcher")
        ) {
            // Option 1: Restaurant Partner Mode
            val isRest = state.selectedRole == UserRole.RESTAURANT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isRest) Modifier.background(Brush.linearGradient(listOf(BioEmeraldBright, Color(0xFF00A352))))
                        else Modifier.background(Color.Transparent)
                    )
                    .clickable {
                        onAuthEvent(AuthEvent.SelectRole(UserRole.RESTAURANT))
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .testTag("mode_restaurant_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = "Restaurant Mode",
                        tint = if (isRest) Color(0xFF042111) else DarkOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Restaurant",
                        color = if (isRest) Color(0xFF042111) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isRest) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            // Option 2: Biogas Plant Mode
            val isPlant = state.selectedRole == UserRole.BIOGAS_PLANT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isPlant) Modifier.background(Brush.linearGradient(listOf(BioCyanCyber, Color(0xFF0284C7))))
                        else Modifier.background(Color.Transparent)
                    )
                    .clickable {
                        onAuthEvent(AuthEvent.SelectRole(UserRole.BIOGAS_PLANT))
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .testTag("mode_biogas_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Factory,
                        contentDescription = "Biogas Plant Mode",
                        tint = if (isPlant) Color(0xFF042111) else DarkOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Biogas Plant",
                        color = if (isPlant) Color(0xFF042111) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isPlant) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Summary Card & Capabilities Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(
                1.dp,
                if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright.copy(alpha = 0.4f)
                else BioCyanCyber.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright.copy(alpha = 0.15f)
                                else BioCyanCyber.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.selectedRole == UserRole.RESTAURANT) Icons.Filled.EnergySavingsLeaf else Icons.Filled.ElectricBolt,
                            contentDescription = null,
                            tint = if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright else BioCyanCyber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (state.selectedRole == UserRole.RESTAURANT) "Restaurant & Kitchen Portal" else "Biogas Energy Plant Console",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (state.selectedRole == UserRole.RESTAURANT) "Post organic waste lots, monitor haulers & collect escrow" else "Procure regional feedstocks, track fleet & run digesters",
                            color = DarkOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demo Quick-Fill Profile Buttons
        Text(
            text = "DEMO CREDENTIALS SHORTCUT",
            color = DarkOnSurfaceMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onAuthEvent(AuthEvent.SelectDemoProfile(UserRole.RESTAURANT)) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("demo_marco_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright else DarkOnSurfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright else DarkBorder
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Chef Marco (Bistro)",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            OutlinedButton(
                onClick = { onAuthEvent(AuthEvent.SelectDemoProfile(UserRole.BIOGAS_PLANT)) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("demo_elena_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (state.selectedRole == UserRole.BIOGAS_PLANT) BioCyanCyber else DarkOnSurfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.selectedRole == UserRole.BIOGAS_PLANT) BioCyanCyber else DarkBorder
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Dr. Elena (Digester)",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = { onAuthEvent(AuthEvent.UpdateEmail(it)) },
            label = { Text("Corporate Email / Operator ID") },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = BioEmeraldBright)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BioEmeraldBright,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = BioEmeraldBright,
                unfocusedLabelColor = DarkOnSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.passwordInput,
            onValueChange = { onAuthEvent(AuthEvent.UpdatePassword(it)) },
            label = { Text("Encrypted Passkey / Password") },
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = BioEmeraldBright)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = DarkOnSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAuthEvent(AuthEvent.SubmitCredentials) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BioEmeraldBright,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = BioEmeraldBright,
                unfocusedLabelColor = DarkOnSurfaceVariant
            )
        )

        // Error message banner
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BioRedAlert.copy(alpha = 0.15f))
                    .border(1.dp, BioRedAlert.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = BioRedAlert,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.errorMessage,
                    color = BioRedAlert,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Remember Me & FIPS Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onAuthEvent(AuthEvent.ToggleRememberMe(!state.rememberMe))
                }
            ) {
                Checkbox(
                    checked = state.rememberMe,
                    onCheckedChange = { onAuthEvent(AuthEvent.ToggleRememberMe(it)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BioEmeraldBright,
                        checkmarkColor = Color(0xFF042111)
                    )
                )
                Text(
                    text = "Remember session",
                    color = DarkOnSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = BioEmeraldBright,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "FIPS 140-3",
                    color = BioEmeraldBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Authenticate Button
        Button(
            onClick = { onAuthEvent(AuthEvent.SubmitCredentials) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = BioEmeraldBright)
                .testTag("login_submit_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.selectedRole == UserRole.RESTAURANT) BioEmeraldBright else BioCyanCyber,
                contentColor = Color(0xFF042111)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    tint = Color(0xFF042111),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Authenticate & Launch ${if (state.selectedRole == UserRole.RESTAURANT) "Kitchen Hub" else "Plant Console"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AuthenticatingStateCard(state: AuthState.Authenticating) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .testTag("authenticating_indicator"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, BioEmeraldBright.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BioEmeraldBright.copy(alpha = 0.15f))
                    .border(2.dp, BioEmeraldBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = BioEmeraldBright,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Establishing Secure Handshake",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.stepMessage,
                color = DarkOnSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Target Scope: ${state.role.name} Console",
                color = BioEmeraldBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun MfaChallengeCard(
    state: AuthState.MfaChallenge,
    onAuthEvent: (AuthEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 440.dp)
            .testTag("mfa_challenge_screen"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, BioEmeraldBright.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BioEmeraldBright.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = BioEmeraldBright,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Two-Factor Authentication",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "FIPS 140-3 Hardware Token or Authenticator App",
                color = DarkOnSurfaceVariant,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Demo Simulation Code Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ACTIVE SIMULATED TOTP CODE",
                        color = DarkOnSurfaceMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.generatedCode,
                        color = BioEmeraldBright,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "Tap below or type the code above to verify",
                        color = DarkOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.inputCode,
                onValueChange = { if (it.length <= 6) onAuthEvent(AuthEvent.UpdateMfaInput(it)) },
                label = { Text("Enter 6-Digit TOTP Code") },
                placeholder = { Text(state.generatedCode) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onAuthEvent(AuthEvent.SubmitMfaVerification) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mfa_input_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BioEmeraldBright,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = BioEmeraldBright,
                    unfocusedLabelColor = DarkOnSurfaceVariant
                )
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.errorMessage,
                    color = BioRedAlert,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onAuthEvent(AuthEvent.CancelMfa) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOnSurfaceVariant),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Text("Back to Login", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (state.inputCode.isBlank()) {
                            onAuthEvent(AuthEvent.UpdateMfaInput(state.generatedCode))
                        }
                        onAuthEvent(AuthEvent.SubmitMfaVerification)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("mfa_verify_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BioEmeraldBright,
                        contentColor = Color(0xFF042111)
                    )
                ) {
                    Text("Verify Token", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
