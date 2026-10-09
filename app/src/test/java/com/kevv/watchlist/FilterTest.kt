package com.kevv.watchlist

import com.kevv.watchlist.data.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterTest {
    private val data = listOf(
        Title(1, "Inception", MediaType.MOVIE, Status.COMPLETED, rating = 9),
        Title(2, "Severance", MediaType.TV, Status.WATCHING, season = 2, episode = 4),
        Title(3, "Dune", MediaType.MOVIE, Status.WANT),
        Title(4, "Dune: Part Two", MediaType.MOVIE, Status.DROPPED)
    )

    @Test fun allAndEmptyQueryReturnsEverything() =
        assertEquals(4, filterTitles(data, "", null).size)

    @Test fun searchIsCaseInsensitiveAndTrimmed() =
        assertEquals(listOf(3L, 4L), filterTitles(data, "  DUNE ", null).map { it.id })

    @Test fun statusFilterWorks() =
        assertEquals(listOf(2L), filterTitles(data, "", Status.WATCHING).map { it.id })

    @Test fun searchAndStatusCombine() =
        assertEquals(listOf(4L), filterTitles(data, "dune", Status.DROPPED).map { it.id })

    @Test fun noMatchGivesEmpty() =
        assertEquals(0, filterTitles(data, "zzz", null).size)
}
