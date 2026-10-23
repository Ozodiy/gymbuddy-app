package com.ozodbek.gymbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozodbek.gymbuddy.ui.auth.AuthViewModel
import com.ozodbek.gymbuddy.ui.auth.LoginScreen
import com.ozodbek.gymbuddy.ui.auth.RegisterScreen
import com.ozodbek.gymbuddy.ui.home.HomeScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    // Auto-login: decided once, from the state at app start.
    val startDestination = remember { if (isLoggedIn) Routes.HOME else Routes.LOGIN }

    // Navigation reacts to auth state, and clears the back stack on every switch.
    LaunchedEffect(isLoggedIn) {
        val target = if (isLoggedIn) Routes.HOME else Routes.LOGIN
        val current = navController.currentDestination?.route
        val onAuthFlow = current == Routes.LOGIN || current == Routes.REGISTER
        val alreadyThere = if (isLoggedIn) current == Routes.HOME else onAuthFlow
        if (!alreadyThere) {
            authViewModel.resetForm()
            navController.clearAndGo(target)
        }
    }

    NavHost(navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(authViewModel, onGoToRegister = {
                authViewModel.resetForm()
                navController.navigate(Routes.REGISTER)
            })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(authViewModel, onGoToLogin = {
                authViewModel.resetForm()
                navController.popBackStack()
            })
        }
        composable(Routes.HOME) {
            HomeScreen(email = authViewModel.currentEmail, onLogout = authViewModel::logout)
        }
    }
}

private fun NavController.clearAndGo(route: String) = navigate(route) {
    popUpTo(graph.id) { inclusive = true }
    launchSingleTop = true
}
