package com.example.biomax.model

sealed interface AuthState {
    data class Unauthenticated(
        val selectedRole: UserRole = UserRole.RESTAURANT,
        val emailInput: String = "marco@grandbistro.com",
        val passwordInput: String = "biomax2026",
        val rememberMe: Boolean = true,
        val errorMessage: String? = null
    ) : AuthState

    data class Authenticating(
        val role: UserRole,
        val email: String,
        val stepMessage: String = "Validating cryptographic credentials..."
    ) : AuthState

    data class MfaChallenge(
        val user: UserAccount,
        val role: UserRole,
        val generatedCode: String,
        val inputCode: String = "",
        val errorMessage: String? = null,
        val attemptsLeft: Int = 3
    ) : AuthState

    data class Authenticated(
        val user: UserAccount,
        val role: UserRole,
        val sessionToken: String,
        val loginTimestamp: Long = System.currentTimeMillis()
    ) : AuthState
}

sealed interface AuthEvent {
    data class SelectRole(val role: UserRole) : AuthEvent
    data class UpdateEmail(val email: String) : AuthEvent
    data class UpdatePassword(val password: String) : AuthEvent
    data class ToggleRememberMe(val remember: Boolean) : AuthEvent
    data class SelectDemoProfile(val role: UserRole) : AuthEvent
    object SubmitCredentials : AuthEvent
    data class UpdateMfaInput(val code: String) : AuthEvent
    object SubmitMfaVerification : AuthEvent
    object VerifyMfa : AuthEvent
    object ResendMfaCode : AuthEvent
    object CancelMfa : AuthEvent
    object Logout : AuthEvent
}
