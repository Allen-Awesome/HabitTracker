package com.habit.tracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habit.tracker.ui.screens.*
import com.habit.tracker.ui.viewmodel.GoalViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddGoal : Screen("add_goal")
    object GoalDetail : Screen("goal_detail/{goalId}") {
        fun createRoute(goalId: Long) = "goal_detail/$goalId"
    }
    object DailyPlan : Screen("daily_plan")
}

@Composable
fun HabitNavigation() {
    val navController = rememberNavController()
    val viewModel: GoalViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onGoalClick = { goalId -> 
                    navController.navigate(Screen.GoalDetail.createRoute(goalId))
                },
                onDailyPlanClick = { navController.navigate(Screen.DailyPlan.route) }
            )
        }
        
        composable(Screen.AddGoal.route) {
            AddGoalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.GoalDetail.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId") ?: 0L
            GoalDetailScreen(
                viewModel = viewModel,
                goalId = goalId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.DailyPlan.route) {
            DailyPlanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
