package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessDao {
    @Query("SELECT * FROM mess_meals ORDER BY dayOfWeek ASC, id ASC")
    fun getAllMessMeals(): Flow<List<MessMealEntity>>

    @Query("SELECT * FROM mess_meals WHERE dayOfWeek = :dayOfWeek ORDER BY id ASC")
    fun getMealsForDay(dayOfWeek: Int): Flow<List<MessMealEntity>>

    @Query("SELECT * FROM mess_meals WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Long): MessMealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meals: List<MessMealEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MessMealEntity): Long

    @Update
    suspend fun update(meal: MessMealEntity)

    @Query("DELETE FROM mess_meals WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM mess_meals")
    suspend fun deleteAll()
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM class_slots ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllClassSlots(): Flow<List<ClassSlotEntity>>

    @Query("SELECT * FROM class_slots WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getClassSlotsForDay(dayOfWeek: Int): Flow<List<ClassSlotEntity>>

    @Query("SELECT * FROM class_slots WHERE id = :id LIMIT 1")
    suspend fun getClassSlotById(id: Long): ClassSlotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(slots: List<ClassSlotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(slot: ClassSlotEntity): Long

    @Update
    suspend fun update(slot: ClassSlotEntity)

    @Query("DELETE FROM class_slots WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM class_slots")
    suspend fun deleteAll()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateEpochMs ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletion(id: Long, isCompleted: Boolean)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}
