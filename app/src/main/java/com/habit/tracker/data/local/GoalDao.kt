package com.habit.tracker.data.local

import androidx.room.*
import com.habit.tracker.data.model.Goal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GoalDao {
    
    @Query("SELECT * FROM goals ORDER BY createdDate DESC")
    fun getAllGoals(): Flow<List<Goal>>
    
    @Query("SELECT * FROM goals WHERE targetYear = :year ORDER BY createdDate DESC")
    fun getGoalsByYear(year: Int): Flow<List<Goal>>
    
    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): Goal?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long
    
    @Update
    suspend fun updateGoal(goal: Goal)
    
    @Delete
    suspend fun deleteGoal(goal: Goal)
    
    @Query("UPDATE goals SET currentProgress = currentProgress + :amount, todayProgress = todayProgress + :amount, weekProgress = weekProgress + :amount, monthProgress = monthProgress + :amount, lastUpdateDate = :date WHERE id = :goalId")
    suspend fun addProgress(goalId: Long, amount: Double, date: LocalDate = LocalDate.now())
    
    @Query("UPDATE goals SET currentProgress = :progress WHERE id = :goalId")
    suspend fun setProgress(goalId: Long, progress: Double)
    
    // 重置每日进度
    @Query("UPDATE goals SET todayProgress = 0, lastUpdateDate = :date WHERE id = :goalId")
    suspend fun resetDailyProgress(goalId: Long, date: LocalDate = LocalDate.now())
    
    // 重置每周进度
    @Query("UPDATE goals SET todayProgress = 0, weekProgress = 0, lastUpdateDate = :date WHERE id = :goalId")
    suspend fun resetWeeklyProgress(goalId: Long, date: LocalDate = LocalDate.now())
    
    // 重置每月进度
    @Query("UPDATE goals SET todayProgress = 0, weekProgress = 0, monthProgress = 0, lastUpdateDate = :date WHERE id = :goalId")
    suspend fun resetMonthlyProgress(goalId: Long, date: LocalDate = LocalDate.now())
    
    // 重置年度进度（新的一年）
    @Query("UPDATE goals SET currentProgress = 0, todayProgress = 0, weekProgress = 0, monthProgress = 0, lastUpdateDate = :date, targetYear = :year WHERE id = :goalId")
    suspend fun resetYearlyProgress(goalId: Long, year: Int, date: LocalDate = LocalDate.now())
}
