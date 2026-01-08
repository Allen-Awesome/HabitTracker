# 习惯追踪 (HabitTracker)

一款简洁的 Android 习惯追踪应用，帮助你设定目标、记录进度、养成好习惯。

## ✨ 功能特点

- 🎯 **灵活的目标设置** - 支持每日、每周、每月、年度目标，也可设置总目标（如减肥10公斤）
- 📊 **多维度进度追踪** - 实时查看各周期的完成进度
- 📅 **月历视图** - 直观的日历界面，轻松规划每日任务
- ⚡ **快速记录** - 一键记录完成量，支持快捷按钮
- 🎨 **精美 UI** - Material Design 3 设计，温暖的配色方案
- 📱 **纯本地存储** - 数据保存在本地，保护隐私

## 📸 截图

*待添加*

## 🛠️ 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose
- **架构**: MVVM
- **数据库**: Room
- **导航**: Navigation Compose
- **最低支持**: Android 8.0 (API 26)

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK 34

### 构建步骤

```bash
# 克隆项目
git clone https://github.com/Allen-Awesome/HabitTracker.git

# 进入目录
cd HabitTracker

# 构建 Debug APK
./gradlew assembleDebug
```

APK 输出位置: `app/build/outputs/apk/debug/app-debug.apk`

## 📁 项目结构

```
app/src/main/java/com/habit/tracker/
├── data/
│   ├── local/          # Room 数据库
│   ├── model/          # 数据模型
│   └── repository/     # 数据仓库
├── ui/
│   ├── navigation/     # 导航配置
│   ├── screens/        # 页面组件
│   ├── theme/          # 主题配置
│   └── viewmodel/      # ViewModel
└── MainActivity.kt
```

## 📝 使用说明

1. **创建目标** - 点击首页右下角 + 按钮，填写目标信息
2. **记录进度** - 点击目标卡片进入详情页，使用快速记录功能
3. **查看计划** - 点击右上角日历图标，查看月度计划
4. **设置计划** - 在目标详情页设置明日计划

## 📄 开源协议

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！
