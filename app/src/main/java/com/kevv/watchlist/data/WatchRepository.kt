package com.kevv.watchlist.data

import kotlinx.coroutines.flow.Flow

class WatchRepository(private val dao: TitleDao) {
    val titles: Flow<List<Title>> = dao.observeAll()

    suspend fun save(title: Title) {
        if (title.id == 0L) dao.insert(title) else dao.update(title)
    }

    suspend fun delete(title: Title) = dao.delete(title)
}
