package com.habit.tracker.data.local

import androidx.room.*
import com.habit.tracker.data.model.DailyPlan
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyPlanDao {
    
    @Query("SELECT * FROM daily_plans WHERE date = :date ORDER BY id DESC")
    fun getPlansByDate(date: LocalDate): Flow<List<DailyPlan>>
    
    @Query("SELECT * FROM daily_plans WHERE goalId = :goalId ORDER BY date DESC")
    fun getPlansByGoal(goalId: Long): Flow<List<DailyPlan>>
    
    @Query("SELECT * FROM daily_plans WHERE goalId = :goalId AND date = :date")
    suspend fun getPlanByGoalAndDate(goalId: Long, date: LocalDate): DailyPlan?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DailyPlan): Long
    
    @Update
    suspend fun updatePlan(plan: DailyPlan)
    
    @Delete
    suspend fun deletePlan(plan: DailyPlan)
    
    @Query("UPDATE daily_plans SET actualAmount = :amount, isCompleted = :isCompleted WHERE id = :planId")
    suspend fun updateProgress(planId: Long, amount: Double, isCompleted: Boolean)
    
    @Query("SELECT * FROM daily_plans WHERE date BETWEEN :startDate AND :endDate")
    fun getPlansBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyPlan>>
}
