package com.habit.tracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habit.tracker.data.local.AppDatabase
import com.habit.tracker.data.model.Goal
import com.habit.tracker.data.model.DailyPlan
import com.habit.tracker.data.model.ResetType
import com.habit.tracker.data.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = GoalRepository(database.goalDao(), database.dailyPlanDao())
    
    val allGoals: StateFlow<List<Goal>> = repository.getAllGoals()
        .onEach { goals -> checkAndResetProgress(goals) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    val todayPlans: StateFlow<List<DailyPlan>> = _selectedDate
        .flatMapLatest { date -> repository.getPlansByDate(date) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    // 检查并重置周期进度
    private fun checkAndResetProgress(goals: List<Goal>) {
        val today = LocalDate.now()
        viewModelScope.launch {
            goals.forEach { goal ->
                when (goal.needsReset(today)) {
                    ResetType.YEAR -> repository.resetYearlyProgress(goal.id, today.year)
                    ResetType.MONTH -> repository.resetMonthlyProgress(goal.id)
                    ResetType.WEEK -> repository.resetWeeklyProgress(goal.id)
                    ResetType.DAY -> repository.resetDailyProgress(goal.id)
                    ResetType.NONE -> { /* 不需要重置 */ }
                }
            }
        }
    }
    
    // 创建新目标（支持多周期）
    fun createGoal(
        name: String, 
        unit: String, 
        yearlyTarget: Double? = null,
        totalTarget: Double? = null,
        dailyTarget: Double? = null,
        weeklyTarget: Double? = null,
        monthlyTarget: Double? = null
    ) {
        viewModelScope.launch {
            val goal = Goal(
                name = name,
                unit = unit,
                yearlyTarget = yearlyTarget,
                totalTarget = totalTarget,
                dailyTarget = dailyTarget,
                weeklyTarget = weeklyTarget,
                monthlyTarget = monthlyTarget
            )
            repository.insertGoal(goal)
        }
    }
    
    // 更新目标
    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }
    
    // 删除目标
    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
    
    // 设置明日计划
    fun setDailyPlan(goalId: Long, date: LocalDate, plannedAmount: Double) {
        viewModelScope.launch {
            val existingPlan = repository.getPlanByGoalAndDate(goalId, date)
            if (existingPlan != null) {
                repository.updatePlan(existingPlan.copy(plannedAmount = plannedAmount))
            } else {
                val plan = DailyPlan(
                    goalId = goalId,
                    date = date,
                    plannedAmount = plannedAmount
                )
                repository.insertPlan(plan)
            }
        }
    }
    
    // 记录完成进度
    fun recordProgress(plan: DailyPlan, actualAmount: Double) {
        viewModelScope.launch {
            repository.completeDailyPlan(plan.id, actualAmount, plan.goalId)
        }
    }
    
    // 直接添加进度（不通过计划）
    fun addQuickProgress(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.addProgress(goalId, amount)
        }
    }
    
    // 减少进度（撤回）
    fun subtractQuickProgress(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.subtractProgress(goalId, amount)
        }
    }
    
    // 选择日期
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
