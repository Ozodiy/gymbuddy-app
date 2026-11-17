package com.ozodbek.gymbuddy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodbek.gymbuddy.data.AppException
import com.ozodbek.gymbuddy.data.AuthRepository
import com.ozodbek.gymbuddy.domain.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Navigation reacts to this value; screens never decide where to go after login. */
    val isLoggedIn: StateFlow<Boolean> = repository.observeLoggedIn()
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.isLoggedIn)

    val currentEmail: String? get() = repository.currentEmail

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null, generalError = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, passwordError = null, generalError = null) }
    fun onConfirmChange(value: String) = _uiState.update { it.copy(confirmPassword = value, confirmError = null, generalError = null) }

    fun resetForm() { _uiState.value = AuthUiState() }

    fun login() {
        val s = _uiState.value
        val emailError = AuthValidator.validateEmail(s.email)
        val passwordError = AuthValidator.validatePassword(s.password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        submit { repository.login(s.email.trim(), s.password) }
    }

    fun register() {
        val s = _uiState.value
        val emailError = AuthValidator.validateEmail(s.email)
        val passwordError = AuthValidator.validatePassword(s.password)
        val confirmError = AuthValidator.validatePasswordConfirmation(s.password, s.confirmPassword)
        if (emailError != null || passwordError != null || confirmError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError, confirmError = confirmError) }
            return
        }
        submit { repository.register(s.email.trim(), s.password) }
    }

    fun logout() = repository.logout()

    private fun submit(call: suspend () -> Result<Unit>) {
        _uiState.update { it.copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            val result = call()
            _uiState.update {
                it.copy(isLoading = false, generalError = result.exceptionOrNull()?.let(::friendlyMessage))
            }
        }
    }

    /** The data layer already turned backend errors into messages that are safe to show. */
    private fun friendlyMessage(e: Throwable): String =
        (e as? AppException)?.message ?: "Something went wrong. Please try again"
}
