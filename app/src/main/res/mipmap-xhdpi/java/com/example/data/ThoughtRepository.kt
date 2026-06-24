package com.example.data

import kotlinx.coroutines.flow.Flow

class ThoughtRepository(private val thoughtDao: ThoughtDao) {
    val allRecords: Flow<List<ThoughtRecord>> = thoughtDao.getAllRecords()
    val allArticleStatuses: Flow<List<ArticleStatus>> = thoughtDao.getAllArticleStatuses()

    suspend fun getRecordById(id: Long): ThoughtRecord? {
        return thoughtDao.getRecordById(id)
    }

    suspend fun insertRecord(record: ThoughtRecord): Long {
        return thoughtDao.insertRecord(record)
    }

    suspend fun deleteRecord(record: ThoughtRecord) {
        thoughtDao.deleteRecord(record)
    }

    suspend fun getArticleStatus(articleId: String): ArticleStatus? {
        return thoughtDao.getArticleStatus(articleId)
    }

    suspend fun insertArticleStatus(status: ArticleStatus) {
        thoughtDao.insertArticleStatus(status)
    }
}
