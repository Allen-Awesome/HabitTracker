package com.habit.tracker.data.repository

import com.habit.tracker.data.local.GoalDao
import com.habit.tracker.data.local.DailyPlanDao
import com.habit.tracker.data.model.Goal
import com.habit.tracker.data.model.DailyPlan
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GoalRepository(
    private val goalDao: GoalDao,
    private val dailyPlanDao: DailyPlanDao
) {
    // 目标相关操作
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    
    fun getGoalsByYear(year: Int): Flow<List<Goal>> = goalDao.getGoalsByYear(year)
    
    suspend fun getGoalById(id: Long): Goal? = goalDao.getGoalById(id)
    
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
    
    suspend fun addProgress(goalId: Long, amount: Double) = goalDao.addProgress(goalId, amount)
    
    suspend fun subtractProgress(goalId: Long, amount: Double) = goalDao.subtractProgress(goalId, amount)
    
    // 周期进度重置
    suspend fun resetDailyProgress(goalId: Long) = goalDao.resetDailyProgress(goalId)
    
    suspend fun resetWeeklyProgress(goalId: Long) = goalDao.resetWeeklyProgress(goalId)
    
    suspend fun resetMonthlyProgress(goalId: Long) = goalDao.resetMonthlyProgress(goalId)
    
    suspend fun resetYearlyProgress(goalId: Long, year: Int) = goalDao.resetYearlyProgress(goalId, year)
    
    // 每日计划相关操作
    fun getPlansByDate(date: LocalDate): Flow<List<DailyPlan>> = dailyPlanDao.getPlansByDate(date)
    
    fun getPlansByGoal(goalId: Long): Flow<List<DailyPlan>> = dailyPlanDao.getPlansByGoal(goalId)
    
    suspend fun getPlanByGoalAndDate(goalId: Long, date: LocalDate): DailyPlan? = 
        dailyPlanDao.getPlanByGoalAndDate(goalId, date)
    
    suspend fun insertPlan(plan: DailyPlan): Long = dailyPlanDao.insertPlan(plan)
    
    suspend fun updatePlan(plan: DailyPlan) = dailyPlanDao.updatePlan(plan)
    
    suspend fun deletePlan(plan: DailyPlan) = dailyPlanDao.deletePlan(plan)
    
    suspend fun completeDailyPlan(planId: Long, actualAmount: Double, goalId: Long) {
        dailyPlanDao.updateProgress(planId, actualAmount, true)
        goalDao.addProgress(goalId, actualAmount)
    }
}
