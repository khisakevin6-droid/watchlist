package com.kevv.watchlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType(val label: String) { MOVIE("Movie"), TV("TV Show") }

enum class Status(val label: String) {
    WANT("Want to Watch"), WATCHING("Watching"), COMPLETED("Completed"), DROPPED("Dropped")
}

@Entity(tableName = "titles")
data class Title(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: MediaType = MediaType.MOVIE,
    val status: Status = Status.WANT,
    val season: Int = 1,
    val episode: Int = 1,
    val rating: Int = 0, // 0 = unrated
    val createdAt: Long = System.currentTimeMillis()
)

/** Pure filtering logic (unit tested). status == null means "All". */
fun filterTitles(all: List<Title>, query: String, status: Status?): List<Title> {
    val q = query.trim()
    return all.filter {
        (status == null || it.status == status) &&
            (q.isEmpty() || it.name.contains(q, ignoreCase = true))
    }
}
