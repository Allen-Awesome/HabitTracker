package com.habit.tracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habit.tracker.data.model.Goal
import com.habit.tracker.data.model.DailyPlan

@Database(
    entities = [Goal::class, DailyPlan::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun goalDao(): GoalDao
    abstract fun dailyPlanDao(): DailyPlanDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        // 数据库迁移：版本1 -> 版本2（添加多周期目标字段）
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE goals ADD COLUMN dailyTarget REAL")
                db.execSQL("ALTER TABLE goals ADD COLUMN weeklyTarget REAL")
                db.execSQL("ALTER TABLE goals ADD COLUMN monthlyTarget REAL")
                db.execSQL("ALTER TABLE goals ADD COLUMN todayProgress REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE goals ADD COLUMN weekProgress REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE goals ADD COLUMN monthProgress REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE goals ADD COLUMN lastUpdateDate TEXT NOT NULL DEFAULT '${java.time.LocalDate.now()}'")
            }
        }
        
        // 数据库迁移：版本2 -> 版本3（年度目标改为可选，添加总目标）
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE goals ADD COLUMN totalTarget REAL")
            }
        }
        
        // 数据库迁移：版本3 -> 版本4（修复yearlyTarget nullable）
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 重建表以支持yearlyTarget为nullable
                db.execSQL("""
                    CREATE TABLE goals_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        unit TEXT NOT NULL,
                        dailyTarget REAL,
                        weeklyTarget REAL,
                        monthlyTarget REAL,
                        yearlyTarget REAL,
                        totalTarget REAL,
                        currentProgress REAL NOT NULL DEFAULT 0.0,
                        todayProgress REAL NOT NULL DEFAULT 0.0,
                        weekProgress REAL NOT NULL DEFAULT 0.0,
                        monthProgress REAL NOT NULL DEFAULT 0.0,
                        lastUpdateDate TEXT NOT NULL,
                        createdDate TEXT NOT NULL,
                        targetYear INTEGER NOT NULL
                    )
                """)
                
                db.execSQL("""
                    INSERT INTO goals_new SELECT * FROM goals
                """)
                
                db.execSQL("DROP TABLE goals")
                db.execSQL("ALTER TABLE goals_new RENAME TO goals")
            }
        }
        
        // 从版本1直接到版本4的迁移（覆盖所有情况）
        private val MIGRATION_1_4 = object : Migration(1, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 重建表，添加所有新字段
                db.execSQL("""
                    CREATE TABLE goals_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        unit TEXT NOT NULL,
                        dailyTarget REAL,
                        weeklyTarget REAL,
                        monthlyTarget REAL,
                        yearlyTarget REAL,
                        totalTarget REAL,
                        currentProgress REAL NOT NULL DEFAULT 0.0,
                        todayProgress REAL NOT NULL DEFAULT 0.0,
                        weekProgress REAL NOT NULL DEFAULT 0.0,
                        monthProgress REAL NOT NULL DEFAULT 0.0,
                        lastUpdateDate TEXT NOT NULL DEFAULT '${java.time.LocalDate.now()}',
                        createdDate TEXT NOT NULL,
                        targetYear INTEGER NOT NULL
                    )
                """)
                
                db.execSQL("""
                    INSERT INTO goals_new (id, name, unit, yearlyTarget, currentProgress, createdDate, targetYear)
                    SELECT id, name, unit, yearlyTarget, currentProgress, createdDate, targetYear FROM goals
                """)
                
                db.execSQL("DROP TABLE goals")
                db.execSQL("ALTER TABLE goals_new RENAME TO goals")
            }
        }
        
        // 从版本2直接到版本4
        private val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE goals_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        unit TEXT NOT NULL,
                        dailyTarget REAL,
                        weeklyTarget REAL,
                        monthlyTarget REAL,
                        yearlyTarget REAL,
                        totalTarget REAL,
                        currentProgress REAL NOT NULL DEFAULT 0.0,
                        todayProgress REAL NOT NULL DEFAULT 0.0,
                        weekProgress REAL NOT NULL DEFAULT 0.0,
                        monthProgress REAL NOT NULL DEFAULT 0.0,
                        lastUpdateDate TEXT NOT NULL,
                        createdDate TEXT NOT NULL,
                        targetYear INTEGER NOT NULL
                    )
                """)
                
                db.execSQL("""
                    INSERT INTO goals_new (id, name, unit, dailyTarget, weeklyTarget, monthlyTarget, 
                        yearlyTarget, currentProgress, todayProgress, weekProgress, monthProgress,
                        lastUpdateDate, createdDate, targetYear)
                    SELECT id, name, unit, dailyTarget, weeklyTarget, monthlyTarget,
                        yearlyTarget, currentProgress, todayProgress, weekProgress, monthProgress,
                        lastUpdateDate, createdDate, targetYear FROM goals
                """)
                
                db.execSQL("DROP TABLE goals")
                db.execSQL("ALTER TABLE goals_new RENAME TO goals")
            }
        }
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_tracker_db"
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                        MIGRATION_1_4, MIGRATION_2_4  // 跳跃迁移
                    )
                    .build()  // 移除 fallbackToDestructiveMigration，迁移失败会报错而不是删数据
                INSTANCE = instance
                instance
            }
        }
    }
}
