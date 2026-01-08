package com.habit.tracker.ui.util

import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * 交互反馈工具类
 * 提供触觉反馈和点击音效
 */
class FeedbackManager(private val view: View, private val audioManager: AudioManager?) {
    
    /**
     * 轻触反馈 - 用于普通按钮点击
     */
    fun lightTap() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
    }
    
    /**
     * 确认反馈 - 用于重要操作确认（如添加进度）
     */
    fun confirm() {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
    }
    
    /**
     * 拒绝/撤回反馈 - 用于删除或撤回操作
     */
    fun reject() {
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, 1.0f)
    }
    
    /**
     * 长按反馈
     */
    fun longPress() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
    
    /**
     * 仅触觉反馈（无声音）
     */
    fun hapticOnly() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }
}

/**
 * Composable 中获取 FeedbackManager
 */
@Composable
fun rememberFeedbackManager(): FeedbackManager {
    val view = LocalView.current
    val context = LocalContext.current
    val audioManager = remember {
        context.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager
    }
    return remember { FeedbackManager(view, audioManager) }
}
