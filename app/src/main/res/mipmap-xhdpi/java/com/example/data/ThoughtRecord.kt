package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thought_records")
data class ThoughtRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val situation: String,
    val intrusiveThought: String,
    val beliefRating: Int,
    val emotions: String, // Comma-separated emotions (e.g., "اضطراب, ترس, غم")
    val emotionIntensity: Int,
    val supportingEvidence: String,
    val contraryEvidence: String,
    val balancedThought: String,
    val balancedBeliefRating: Int,
    val reviewText: String,
    val finalDistressRating: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val shamsiDate: String, // Calculated Shamsi date (e.g. "1405/04/03")
    val relatedDistortion: String // Linked distortion (e.g., "فاجعه‌سازی")
)

@Entity(tableName = "article_statuses")
data class ArticleStatus(
    @PrimaryKey val articleId: String,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false
)
