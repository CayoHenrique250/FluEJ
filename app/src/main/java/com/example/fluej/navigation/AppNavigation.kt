package com.example.fluej.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fluej.data.AuthRepository
import com.example.fluej.data.Perfil
import com.example.fluej.data.Session
import com.example.fluej.ui.screens.DashboardEmBreveScreen
import com.example.fluej.ui.screens.DashboardOrientadorScreen
import com.example.fluej.ui.screens.DashboardRHScreen
import com.example.fluej.ui.screens.RegisterMemberScreen
import com.example.fluej.ui.screens.RegisterProjectScreen
import com.example.fluej.ui.screens.ReportGeneratedScreen
import com.example.fluej.ui.screens.ReportsScreen
import com.example.fluej.ui.membros.TelaMembrosScreen
import com.example.fluej.ui.screens.LoginScreen
import kotlinx.coroutines.launch
import com.example.fluej.ui.pontos.TelaPontosScreen
import com.example.fluej.ui.membros.TelaInativarMembroScreen
import com.example.fluej.ui.screens.DashboardPresidenteScreen
import com.example.fluej.ui.pontos.TelaRegistrarPontosScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fluej.ui.membros.TelaDesempenhoScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Pontos : Screen("pontos")
    object Home : Screen("home") 
    object RegisterMember : Screen("register_member")
    object RegisterProject : Screen("register_project")
    object Reports : Screen("reports")
    object ReportGenerated : Screen("report_generated")
    object Members : Screen("members")
    object InativarMembro : Screen("inativar_membro")
    object RegistrarPontos : Screen("registrar_pontos")
    object Desempenho : Screen("desempenho/{membroId}") {
    fun criarRota(membroId: String) = "desempenho/$membroId"
}
}

private object Acesso {
    val todos = Perfil.values().toSet()
    val cadastroMembro = setOf(Perfil.RH, Perfil.PRESIDENTE)
    val registroProjeto = todos - Perfil.ORIENTADOR
    val pontosPresidente = setOf(Perfil.PRESIDENTE)    
    val registroPontos = todos - Perfil.ORIENTADOR    
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val navigateBottomBar: (String) -> Unit = { route ->
        if (route == Screen.Home.route) {
            val voltou = navController.popBackStack(Screen.Home.route, inclusive = false)
            if (!voltou) {
                navController.navigate(Screen.Home.route) { launchSingleTop = true }
            }
        } else {
            navController.navigate(route) {
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val sair: () -> Unit = {
        scope.launch {
            AuthRepository.sair()
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(route = Screen.Desempenho.route, arguments = listOf(
        navArgument("membroId") {
            type = NavType.StringType
        }
    )
)      
     { entrada ->  val membroId = entrada.arguments ?.getString("membroId")

         if (membroId != null) {
            Protegida(navController, Acesso.todos) {
             TelaDesempenhoScreen(
                membroId = membroId,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateBottomBar = navigateBottomBar
            )
        }
    }
}



        composable(Screen.InativarMembro.route) {
            Protegida(navController, Acesso.cadastroMembro) {
              TelaInativarMembroScreen(
               onNavigateBottomBar = navigateBottomBar,
                 onBack = {
                  navController.popBackStack()
            }
        )
    }
}

        composable(Screen.RegistrarPontos.route) {
           Protegida(navController, Acesso.registroPontos) {
             TelaRegistrarPontosScreen(
               onNavigateBottomBar = navigateBottomBar,
                onBack = {
                 navController.popBackStack()
            }
        )
    }
}
        composable(Screen.Home.route) {
            Protegida(navController, Acesso.todos) {
                when (Session.perfil) {
                   Perfil.RH -> DashboardRHScreen(
                                 onNavigateToRegister = {
                                 navController.navigate(Screen.RegisterMember.route)
    },
                             onNavigateToRegisterProject = {
                             navController.navigate(Screen.RegisterProject.route)
    },
                             onNavigateBottomBar = navigateBottomBar,
                             onNavigateToDeactivateMember = {
                             navController.navigate(Screen.InativarMembro.route)
    },
                            onNavigateToMembers = {
                            navController.navigate(Screen.Members.route)
    },
                           onNavigateToPoints = {
                           navController.navigate(Screen.RegistrarPontos.route)
},
    onLogout = sair
)

                    Perfil.PRESIDENTE -> DashboardPresidenteScreen(
                        onNavigateToRegister = { navController.navigate(Screen.RegisterMember.route) },
                        onNavigateBottomBar = navigateBottomBar,
                        onLogout = sair
                    )
                    Perfil.PROJETOS, Perfil.COMERCIAL, Perfil.FINANCEIRO -> DashboardEmBreveScreen(
                        titulo = "Gestor de ${Session.departmentOfManager}",
                        onLogout = sair
                    )

                    Perfil.ORIENTADOR -> DashboardOrientadorScreen(
                        onNavigateBottomBar = navigateBottomBar,
                        onLogout = sair
                    )
                    null -> Unit
                }
            }
        }

       composable(Screen.Members.route) {
    Protegida(navController, Acesso.todos) {
        TelaMembrosScreen(
            onMembroClick = { membro ->
                navController.navigate(
                    Screen.Desempenho.criarRota(membro.id)
                )
            },
            onNavigateBottomBar = navigateBottomBar
        )
    }
}
        composable(Screen.RegisterMember.route) {
            Protegida(navController, Acesso.cadastroMembro) {
                RegisterMemberScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateBottomBar = navigateBottomBar
                )
            }
        }
        composable(Screen.RegisterProject.route) {
            Protegida(navController, Acesso.registroProjeto) {
                RegisterProjectScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateBottomBar = navigateBottomBar
                )
            }
        }
        composable(Screen.Reports.route) {
            Protegida(navController, Acesso.todos) {
                ReportsScreen(
                    onGenerateReport = { navController.navigate(Screen.ReportGenerated.route) },
                    onNavigateBottomBar = navigateBottomBar
                )
            }
        }
        composable(Screen.ReportGenerated.route) {
            Protegida(navController, Acesso.todos) {
                ReportGeneratedScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateBottomBar = navigateBottomBar
                )
            }
        }
        composable(Screen.Pontos.route) {
            Protegida(navController, Acesso.pontosPresidente) {
                TelaPontosScreen(
                    onNavigateBottomBar = navigateBottomBar
                )
            }
}
    }
}

@Composable
private fun Protegida(
    navController: NavHostController,
    permitidos: Set<Perfil>,
    conteudo: @Composable () -> Unit
) {
    val perfil = Session.perfil
    if (perfil != null && perfil in permitidos) {
        conteudo()
    } else {
        LaunchedEffect(Unit) {
            val destino = if (perfil == null) Screen.Login.route else Screen.Home.route
            navController.navigate(destino) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
}