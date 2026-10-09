package com.kevv.watchlist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TitleDao {
    @Query("SELECT * FROM titles ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Title>>

    @Insert suspend fun insert(title: Title): Long
    @Update suspend fun update(title: Title)
    @Delete suspend fun delete(title: Title)
}
