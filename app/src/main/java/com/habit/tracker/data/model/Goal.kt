package com.habit.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * 目标实体类
 * @param id 目标ID
 * @param name 目标名称（如：跑步、读书、背单词）
 * @param unit 单位（如：公里、本、个）
 * @param dailyTarget 每日目标（可选）
 * @param weeklyTarget 每周目标（可选）
 * @param monthlyTarget 每月目标（可选）
 * @param yearlyTarget 年度目标（可选）
 * @param totalTarget 总目标（可选，用于非年度目标如减肥）
 * @param currentProgress 当前完成进度（累计）
 * @param todayProgress 今日完成进度
 * @param weekProgress 本周完成进度
 * @param monthProgress 本月完成进度
 * @param lastUpdateDate 最后更新日期（用于重置周期进度）
 * @param createdDate 创建日期
 * @param targetYear 目标年份
 */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unit: String,
    val dailyTarget: Double? = null,
    val weeklyTarget: Double? = null,
    val monthlyTarget: Double? = null,
    val yearlyTarget: Double? = null,
    val totalTarget: Double? = null,
    val currentProgress: Double = 0.0,
    val todayProgress: Double = 0.0,
    val weekProgress: Double = 0.0,
    val monthProgress: Double = 0.0,
    val lastUpdateDate: LocalDate = LocalDate.now(),
    val createdDate: LocalDate = LocalDate.now(),
    val targetYear: Int = LocalDate.now().year
) {
    // 主目标（优先使用总目标，其次年度目标）
    val mainTarget: Double
        get() = totalTarget ?: yearlyTarget ?: 0.0
    
    // 主进度百分比
    val mainProgressPercentage: Float
        get() = if (mainTarget > 0) (currentProgress / mainTarget * 100).toFloat() else 0f
    
    // 年度完成百分比（兼容旧代码）
    val yearlyProgressPercentage: Float
        get() = if (yearlyTarget != null && yearlyTarget > 0) (currentProgress / yearlyTarget * 100).toFloat() 
                else mainProgressPercentage
    
    // 每日完成百分比
    val dailyProgressPercentage: Float
        get() = if (dailyTarget != null && dailyTarget > 0) 
            (todayProgress / dailyTarget * 100).toFloat() else 0f
    
    // 每周完成百分比
    val weeklyProgressPercentage: Float
        get() = if (weeklyTarget != null && weeklyTarget > 0) 
            (weekProgress / weeklyTarget * 100).toFloat() else 0f
    
    // 每月完成百分比
    val monthlyProgressPercentage: Float
        get() = if (monthlyTarget != null && monthlyTarget > 0) 
            (monthProgress / monthlyTarget * 100).toFloat() else 0f
    
    // 兼容旧代码
    val progressPercentage: Float
        get() = yearlyProgressPercentage
    
    // 计算剩余数量
    val remaining: Double
        get() = (mainTarget - currentProgress).coerceAtLeast(0.0)
    
    // 计算日均需要完成量（基于剩余天数）
    val dailyRequired: Double
        get() {
            val today = LocalDate.now()
            // 如果有年度目标，按年底计算；否则按30天估算
            val remainingDays = if (yearlyTarget != null) {
                val endOfYear = LocalDate.of(targetYear, 12, 31)
                ChronoUnit.DAYS.between(today, endOfYear).toInt()
            } else {
                30 // 默认30天
            }
            return if (remainingDays > 0) remaining / remainingDays else remaining
        }
    
    // 是否有设置主目标
    val hasMainTarget: Boolean
        get() = totalTarget != null || yearlyTarget != null
    
    // 检查是否需要重置周期进度
    fun needsReset(today: LocalDate = LocalDate.now()): ResetType {
        if (lastUpdateDate == today) return ResetType.NONE
        
        val weekFields = WeekFields.of(Locale.getDefault())
        val lastWeek = lastUpdateDate.get(weekFields.weekOfWeekBasedYear())
        val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
        val lastMonth = lastUpdateDate.monthValue
        val currentMonth = today.monthValue
        val lastYear = lastUpdateDate.year
        val currentYear = today.year
        
        return when {
            lastYear != currentYear -> ResetType.YEAR
            lastMonth != currentMonth -> ResetType.MONTH
            lastWeek != currentWeek -> ResetType.WEEK
            lastUpdateDate != today -> ResetType.DAY
            else -> ResetType.NONE
        }
    }
    
    // 是否有设置任何周期目标
    val hasPeriodicTargets: Boolean
        get() = dailyTarget != null || weeklyTarget != null || monthlyTarget != null
}

enum class ResetType {
    NONE, DAY, WEEK, MONTH, YEAR
}
