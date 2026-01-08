package com.habit.tracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 每日计划实体类
 * @param id 计划ID
 * @param goalId 关联的目标ID
 * @param date 计划日期
 * @param plannedAmount 计划完成量
 * @param actualAmount 实际完成量
 * @param isCompleted 是否完成
 */
@Entity(
    tableName = "daily_plans",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId"), Index("date")]
)
data class DailyPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val date: LocalDate,
    val plannedAmount: Double,
    val actualAmount: Double = 0.0,
    val isCompleted: Boolean = false
) {
    // 计算完成率
    val completionRate: Float
        get() = if (plannedAmount > 0) (actualAmount / plannedAmount * 100).toFloat() else 0f
}
