package com.habit.tracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.tracker.data.model.Goal
import com.habit.tracker.ui.theme.AppColors
import com.habit.tracker.ui.util.rememberFeedbackManager
import com.habit.tracker.ui.viewmodel.GoalViewModel

// 卡片背景色列表（参考VegetableOrderUI的柔和配色）
val goalCardColors = listOf(
    listOf(AppColors.navajoWhite, Color(0xFFFFF3E0)),
    listOf(AppColors.water, Color(0xFFE3F2FD)),
    listOf(AppColors.mintCream, Color(0xFFE8F5E9)),
    listOf(AppColors.lavender, Color(0xFFF3E5F5)),
    listOf(AppColors.peachPuff, Color(0xFFFFE0B2)),
    listOf(AppColors.lightBlue, Color(0xFFE1F5FE)),
)

// 进度条渐变色
val goalColors = listOf(
    listOf(Color(0xFFEE8D3C), Color(0xFFFFB74D)),
    listOf(Color(0xFF5B9BD5), Color(0xFF90CAF9)),
    listOf(Color(0xFF66BB6A), Color(0xFFA5D6A7)),
    listOf(Color(0xFFAB47BC), Color(0xFFCE93D8)),
    listOf(Color(0xFFFF7043), Color(0xFFFFAB91)),
    listOf(Color(0xFF26C6DA), Color(0xFF80DEEA)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GoalViewModel,
    onAddGoal: () -> Unit,
    onGoalClick: (Long) -> Unit,
    onDailyPlanClick: () -> Unit
) {
    val goals by viewModel.allGoals.collectAsState()
    val feedback = rememberFeedbackManager()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "习惯追踪",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            "坚持每一天，遇见更好的自己",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        feedback.lightTap()
                        onDailyPlanClick() 
                    }) {
                        Icon(
                            Icons.Default.DateRange, 
                            contentDescription = "每日计划",
                            tint = AppColors.gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    feedback.confirm()
                    onAddGoal() 
                },
                containerColor = AppColors.gold,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加目标", tint = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(AppColors.goldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 48.sp)
                    }
                    Text(
                        "还没有目标", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "点击 + 创建你的第一个目标", 
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顶部统计卡片
                item {
                    OverviewCard(goals)
                }
                
                // 今日进度（如果有设置每日目标）
                val goalsWithDaily = goals.filter { it.dailyTarget != null }
                if (goalsWithDaily.isNotEmpty()) {
                    item {
                        Text(
                            "今日进度",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(goalsWithDaily) { goal ->
                                TodayProgressCard(
                                    goal = goal, 
                                    onClick = { 
                                        feedback.lightTap()
                                        onGoalClick(goal.id) 
                                    }
                                )
                            }
                        }
                    }
                }
                
                item {
                    Text(
                        "全部目标",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                
                items(goals) { goal ->
                    val colorIndex = (goal.id % goalCardColors.size).toInt()
                    GoalCard(
                        goal = goal, 
                        cardColors = goalCardColors[colorIndex],
                        progressColors = goalColors[colorIndex],
                        onClick = { 
                            feedback.lightTap()
                            onGoalClick(goal.id) 
                        }
                    )
                }
                
                // 底部留白
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TodayProgressCard(goal: Goal, onClick: () -> Unit) {
    val percentage = goal.dailyProgressPercentage
    val isCompleted = percentage >= 100
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) AppColors.mintCream else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCompleted) "✅" else "📊",
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = goal.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.0f%%".format(percentage.coerceAtMost(100f)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) AppColors.success else AppColors.gold
            )
            Text(
                text = "%.1f / %.0f".format(goal.todayProgress, goal.dailyTarget ?: 0.0),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun OverviewCard(goals: List<Goal>) {
    val totalProgress = goals.sumOf { it.currentProgress }
    val totalTarget = goals.sumOf { it.mainTarget }
    val overallPercentage = if (totalTarget > 0) (totalProgress / totalTarget * 100) else 0.0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.goldLight.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(
                value = "${goals.size}",
                label = "目标数",
                emoji = "🎯"
            )
            StatItem(
                value = "%.0f%%".format(overallPercentage),
                label = "总进度",
                emoji = "📈"
            )
            StatItem(
                value = "${goals.count { it.mainProgressPercentage >= 100 }}",
                label = "已完成",
                emoji = "🏆"
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.gold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun GoalCard(
    goal: Goal, 
    cardColors: List<Color>,
    progressColors: List<Color>,
    onClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (goal.mainProgressPercentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "progress"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            // 渐变背景进度
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(130.dp)
                    .background(
                        Brush.horizontalGradient(cardColors.map { it.copy(alpha = 0.4f) })
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 彩色圆点
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(progressColors))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = goal.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (goal.mainProgressPercentage >= 100) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = "已完成",
                            tint = AppColors.success,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 进度条
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(progressColors))
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        val targetLabel = if (goal.totalTarget != null) "总目标" else "年目标"
                        Text(
                            text = "%.1f / %.0f ${goal.unit}".format(goal.currentProgress, goal.mainTarget),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (goal.hasMainTarget) {
                            Text(
                                text = "日均需 %.1f ${goal.unit}".format(goal.dailyRequired),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    Text(
                        text = "%.1f%%".format(goal.mainProgressPercentage),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColors[0]
                    )
                }
            }
        }
    }
}
