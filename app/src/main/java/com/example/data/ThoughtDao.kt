package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ThoughtDao {
    @Query("SELECT * FROM thought_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ThoughtRecord>>

    @Query("SELECT * FROM thought_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): ThoughtRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ThoughtRecord): Long

    @Delete
    suspend fun deleteRecord(record: ThoughtRecord)

    @Query("SELECT * FROM article_statuses")
    fun getAllArticleStatuses(): Flow<List<ArticleStatus>>

    @Query("SELECT * FROM article_statuses WHERE articleId = :articleId LIMIT 1")
    suspend fun getArticleStatus(articleId: String): ArticleStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleStatus(status: ArticleStatus)
}
