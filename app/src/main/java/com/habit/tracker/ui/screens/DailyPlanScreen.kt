package com.habit.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.tracker.data.model.DailyPlan
import com.habit.tracker.data.model.Goal
import com.habit.tracker.ui.theme.AppColors
import com.habit.tracker.ui.viewmodel.GoalViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlanScreen(
    viewModel: GoalViewModel,
    onBack: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todayPlans by viewModel.todayPlans.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState()
    
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val dateFormatter = DateTimeFormatter.ofPattern("MM月dd日 EEEE")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("每日计划", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 月历卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 月份切换
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                                Icon(Icons.Default.KeyboardArrowLeft, "上月")
                            }
                            Text(
                                text = "${currentMonth.year}年${currentMonth.monthValue}月",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                                Icon(Icons.Default.KeyboardArrowRight, "下月")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 星期标题
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 日历网格
                        MonthCalendar(
                            yearMonth = currentMonth,
                            selectedDate = selectedDate,
                            onDateSelected = { viewModel.selectDate(it) }
                        )
                    }
                }
            }
            
            // 选中日期显示
            item {
                Text(
                    text = selectedDate.format(dateFormatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.gold
                )
            }
            
            // 计划列表
            if (todayPlans.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📅", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "这一天还没有计划",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                "在目标详情页设置每日计划",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(todayPlans) { plan ->
                    val goal = allGoals.find { it.id == plan.goalId }
                    if (goal != null) {
                        val colorIndex = (goal.id % goalColors.size).toInt()
                        DailyPlanCard(
                            plan = plan,
                            goal = goal,
                            colors = goalColors[colorIndex],
                            onComplete = { actualAmount ->
                                viewModel.recordProgress(plan, actualAmount)
                            }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun MonthCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    // 计算第一天是星期几（周日=0）
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    
    // 生成日历天数列表
    val days = mutableListOf<LocalDate?>()
    
    // 添加前面的空白
    repeat(firstDayOfWeek) { days.add(null) }
    
    // 添加当月天数
    var day = firstDayOfMonth
    while (!day.isAfter(lastDayOfMonth)) {
        days.add(day)
        day = day.plusDays(1)
    }
    
    // 计算需要多少行
    val rows = (days.size + 6) / 7
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val date = days.getOrNull(index)
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    date == selectedDate -> AppColors.gold
                                    date == today -> AppColors.goldLight
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (date == selectedDate || date == today) 
                                    FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    date == selectedDate -> Color.White
                                    date == today -> AppColors.gold
                                    date.monthValue != yearMonth.monthValue -> 
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyPlanCard(
    plan: DailyPlan,
    goal: Goal,
    colors: List<Color>,
    onComplete: (Double) -> Unit
) {
    var actualAmount by remember { mutableStateOf(plan.plannedAmount.toString()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(colors))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = goal.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (plan.isCompleted) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "已完成",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "已完成",
                                fontSize = 13.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "计划完成",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${plan.plannedAmount.toInt()} ${goal.unit}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (plan.isCompleted) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "实际完成",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "${plan.actualAmount.toInt()} ${goal.unit}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors[0]
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = actualAmount,
                        onValueChange = { actualAmount = it },
                        label = { Text("实际完成") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = { Text(goal.unit) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Button(
                        onClick = {
                            actualAmount.toDoubleOrNull()?.let { amount ->
                                onComplete(amount)
                            }
                        },
                        enabled = actualAmount.toDoubleOrNull() != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors[0]
                        )
                    ) {
                        Text("完成")
                    }
                }
            }
        }
    }
}
