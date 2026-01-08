package com.habit.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
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
import com.habit.tracker.ui.viewmodel.GoalViewModel

data class GoalTemplate(
    val name: String,
    val unit: String,
    val dailyTarget: String?,
    val weeklyTarget: String?,
    val monthlyTarget: String?,
    val yearlyTarget: String?,
    val totalTarget: String?,
    val emoji: String,
    val colors: List<Color>
)

val templates = listOf(
    GoalTemplate("跑步", "公里", "3", "20", "80", "1000", null, "🏃", listOf(Color(0xFF667eea), Color(0xFF764ba2))),
    GoalTemplate("读书", "本", null, "1", "4", "50", null, "📚", listOf(Color(0xFFf093fb), Color(0xFFf5576c))),
    GoalTemplate("背单词", "个", "15", "100", "400", "5000", null, "📝", listOf(Color(0xFF4facfe), Color(0xFF00f2fe))),
    GoalTemplate("减肥", "公斤", null, null, "2", null, "10", "🏋️", listOf(Color(0xFFff6b6b), Color(0xFFfeca57))),
    GoalTemplate("冥想", "分钟", "10", "70", "300", "3650", null, "🧘", listOf(Color(0xFFfa709a), Color(0xFFfee140))),
    GoalTemplate("喝水", "杯", "8", "56", "240", "2920", null, "💧", listOf(Color(0xFF89f7fe), Color(0xFF66a6ff))),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: GoalViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var dailyTarget by remember { mutableStateOf("") }
    var weeklyTarget by remember { mutableStateOf("") }
    var monthlyTarget by remember { mutableStateOf("") }
    var yearlyTarget by remember { mutableStateOf("") }
    var totalTarget by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建目标", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 输入表单
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 目标名称和单位
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("目标名称") },
                            placeholder = { Text("跑步") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("单位") },
                            placeholder = { Text("公里") },
                            modifier = Modifier.weight(0.5f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text(
                        text = "设置周期目标（可选填）",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    // 每日和每周目标
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = dailyTarget,
                            onValueChange = { dailyTarget = it },
                            label = { Text("每日目标") },
                            placeholder = { Text("可选") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        OutlinedTextField(
                            value = weeklyTarget,
                            onValueChange = { weeklyTarget = it },
                            label = { Text("每周目标") },
                            placeholder = { Text("可选") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    // 每月和年度目标
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = monthlyTarget,
                            onValueChange = { monthlyTarget = it },
                            label = { Text("每月目标") },
                            placeholder = { Text("可选") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        OutlinedTextField(
                            value = yearlyTarget,
                            onValueChange = { yearlyTarget = it },
                            label = { Text("年度目标") },
                            placeholder = { Text("可选") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    // 总目标（用于非年度目标）
                    OutlinedTextField(
                        value = totalTarget,
                        onValueChange = { totalTarget = it },
                        label = { Text("总目标") },
                        placeholder = { Text("如减肥10公斤，不按年计算") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        supportingText = { Text("年度目标和总目标至少填一个") }
                    )
                    
                    val hasValidTarget = yearlyTarget.toDoubleOrNull() != null || totalTarget.toDoubleOrNull() != null
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank() && unit.isNotBlank() && hasValidTarget) {
                                viewModel.createGoal(
                                    name = name.trim(),
                                    unit = unit.trim(),
                                    yearlyTarget = yearlyTarget.toDoubleOrNull(),
                                    totalTarget = totalTarget.toDoubleOrNull(),
                                    dailyTarget = dailyTarget.toDoubleOrNull(),
                                    weeklyTarget = weeklyTarget.toDoubleOrNull(),
                                    monthlyTarget = monthlyTarget.toDoubleOrNull()
                                )
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = name.isNotBlank() && unit.isNotBlank() && hasValidTarget,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("创建目标", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            
            // 快捷模板
            Text(
                text = "快捷模板",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                templates.chunked(2).forEach { rowTemplates ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTemplates.forEach { template ->
                            TemplateCard(
                                template = template,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    name = template.name
                                    unit = template.unit
                                    dailyTarget = template.dailyTarget ?: ""
                                    weeklyTarget = template.weeklyTarget ?: ""
                                    monthlyTarget = template.monthlyTarget ?: ""
                                    yearlyTarget = template.yearlyTarget ?: ""
                                    totalTarget = template.totalTarget ?: ""
                                }
                            )
                        }
                        // 如果是奇数个，补一个空白
                        if (rowTemplates.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TemplateCard(
    template: GoalTemplate,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(template.colors)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = template.emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = template.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            
            if (template.yearlyTarget != null) {
                Text(
                    text = "${template.yearlyTarget} ${template.unit}/年",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            } else if (template.totalTarget != null) {
                Text(
                    text = "总目标 ${template.totalTarget} ${template.unit}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            if (template.dailyTarget != null) {
                Text(
                    text = "${template.dailyTarget} ${template.unit}/日",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                )
            }
        }
    }
}
