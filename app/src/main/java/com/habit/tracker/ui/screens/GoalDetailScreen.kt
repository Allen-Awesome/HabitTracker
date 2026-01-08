package com.habit.tracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.tracker.ui.theme.AppColors
import com.habit.tracker.ui.util.rememberFeedbackManager
import com.habit.tracker.ui.viewmodel.GoalViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalViewModel,
    goalId: Long,
    onBack: () -> Unit
) {
    val goals by viewModel.allGoals.collectAsState()
    val goal = goals.find { it.id == goalId }
    
    var quickAddAmount by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tomorrowPlan by remember { mutableStateOf("") }
    
    val feedback = rememberFeedbackManager()
    
    if (goal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppColors.gold)
        }
        return
    }
    
    val colorIndex = (goal.id % goalColors.size).toInt()
    val colors = goalColors[colorIndex]
    
    val animatedProgress by animateFloatAsState(
        targetValue = (goal.mainProgressPercentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "progress"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        feedback.lightTap()
                        onBack() 
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        feedback.lightTap()
                        showDeleteDialog = true 
                    }) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 圆形进度卡片（年度）
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 圆形进度
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(160.dp),
                            strokeWidth = 12.dp,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            color = colors[0]
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f%%".format(goal.mainProgressPercentage),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors[0]
                            )
                            Text(
                                text = if (goal.totalTarget != null) "总进度" else "年度进度",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 年度数据统计
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailStatItem(
                            value = "%.1f".format(goal.currentProgress),
                            unit = goal.unit,
                            label = "已完成"
                        )
                        DetailStatItem(
                            value = "%.0f".format(goal.mainTarget),
                            unit = goal.unit,
                            label = if (goal.totalTarget != null) "总目标" else "年度目标"
                        )
                        DetailStatItem(
                            value = "%.1f".format(goal.remaining),
                            unit = goal.unit,
                            label = "剩余"
                        )
                    }
                }
            }
            
            // 周期进度卡片（如果设置了周期目标）
            if (goal.hasPeriodicTargets) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "周期进度",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // 每日进度
                        goal.dailyTarget?.let { daily ->
                            ProgressRow(
                                label = "今日",
                                current = goal.todayProgress,
                                target = daily,
                                unit = goal.unit,
                                percentage = goal.dailyProgressPercentage,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        
                        // 每周进度
                        goal.weeklyTarget?.let { weekly ->
                            ProgressRow(
                                label = "本周",
                                current = goal.weekProgress,
                                target = weekly,
                                unit = goal.unit,
                                percentage = goal.weeklyProgressPercentage,
                                color = Color(0xFF2196F3)
                            )
                        }
                        
                        // 每月进度
                        goal.monthlyTarget?.let { monthly ->
                            ProgressRow(
                                label = "本月",
                                current = goal.monthProgress,
                                target = monthly,
                                unit = goal.unit,
                                percentage = goal.monthlyProgressPercentage,
                                color = Color(0xFF9C27B0)
                            )
                        }
                    }
                }
            }
            
            // 日均提示
            if (goal.hasMainTarget) {
                Surface(
                    color = colors[0].copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 每天完成 ",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "%.1f ${goal.unit}".format(goal.dailyRequired),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors[0]
                        )
                        Text(
                            text = " 即可达成目标",
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // 快速记录
            ActionCard(
                title = "快速记录",
                icon = Icons.Outlined.Add,
                iconColor = Color(0xFF4CAF50)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickAddAmount,
                        onValueChange = { quickAddAmount = it },
                        label = { Text("完成数量") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = { Text(goal.unit) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // 减少按钮
                    FilledTonalButton(
                        onClick = {
                            quickAddAmount.toDoubleOrNull()?.let { amount ->
                                if (amount > 0) {
                                    feedback.reject()
                                    viewModel.subtractQuickProgress(goal.id, amount)
                                    quickAddAmount = ""
                                }
                            }
                        },
                        enabled = quickAddAmount.toDoubleOrNull() != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text("−", fontSize = 20.sp, color = Color(0xFFE53935))
                    }
                    
                    // 增加按钮
                    Button(
                        onClick = {
                            quickAddAmount.toDoubleOrNull()?.let { amount ->
                                if (amount > 0) {
                                    feedback.confirm()
                                    viewModel.addQuickProgress(goal.id, amount)
                                    quickAddAmount = ""
                                }
                            }
                        },
                        enabled = quickAddAmount.toDoubleOrNull() != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Outlined.Done, contentDescription = "增加")
                    }
                }
                
                // 快捷按钮 - 增加
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "快速增加",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 5, 10).forEach { num ->
                        AssistChip(
                            onClick = { 
                                feedback.confirm()
                                viewModel.addQuickProgress(goal.id, num.toDouble())
                            },
                            label = { Text("+$num") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // 快捷按钮 - 减少（撤回）
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "快速撤回",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 5, 10).forEach { num ->
                        AssistChip(
                            onClick = { 
                                feedback.reject()
                                viewModel.subtractQuickProgress(goal.id, num.toDouble())
                            },
                            label = { Text("-$num") },
                            modifier = Modifier.weight(1f),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFFFEBEE),
                                labelColor = Color(0xFFE53935)
                            )
                        )
                    }
                }
            }
            
            // 设置明日计划
            ActionCard(
                title = "设置明日计划",
                icon = Icons.Outlined.DateRange,
                iconColor = Color(0xFF2196F3)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tomorrowPlan,
                        onValueChange = { tomorrowPlan = it },
                        label = { Text("明日目标") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = { Text(goal.unit) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Button(
                        onClick = {
                            tomorrowPlan.toDoubleOrNull()?.let { amount ->
                                if (amount > 0) {
                                    feedback.confirm()
                                    viewModel.setDailyPlan(
                                        goal.id,
                                        LocalDate.now().plusDays(1),
                                        amount
                                    )
                                    tomorrowPlan = ""
                                }
                            }
                        },
                        enabled = tomorrowPlan.toDoubleOrNull() != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("设置")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除目标") },
            text = { Text("确定要删除「${goal.name}」吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun ProgressRow(
    label: String,
    current: Double,
    target: Double,
    unit: String,
    percentage: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (percentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "row_progress"
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "%.1f / %.0f $unit (%.0f%%)".format(current, target, percentage),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun DetailStatItem(value: String, unit: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " $unit",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
