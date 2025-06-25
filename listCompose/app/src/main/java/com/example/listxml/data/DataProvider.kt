package com.example.listxml.data

import com.example.listCompose.R

object DataProvider {
    val filmList = listOf(
        Films(
            title = "Grave of the Fireflies",
            image = R.drawable.fireflies,
            description = "A heartbreaking tale of two siblings struggling to survive during World War II.",
            year = "1988",
            imdbUrl = "https://www.imdb.com/title/tt0095327/"
        ),
        Films(
            title = "Howl's Moving Castle",
            image = R.drawable.movingcastle,
            description = "A young woman is cursed by a witch and seeks refuge in a wizard's moving castle.",
            year = "2004",
            imdbUrl = "https://www.imdb.com/title/tt0347149/"
        ),
        Films(
            title = "Spirited Away",
            image = R.drawable.spiritedaway,
            description = "A girl enters a mysterious spirit world and must find a way to save her parents.",
            year = "2001",
            imdbUrl = "https://www.imdb.com/title/tt0245429/"
        ),
        Films(
            title = "Ponyo",
            image = R.drawable.ponyo,
            description = "A goldfish princess wants to become human after befriending a boy.",
            year = "2008",
            imdbUrl = "https://www.imdb.com/title/tt0876563/"
        ),
        Films(
            title = "From Up on Poppy Hill",
            image = R.drawable.poppyhill,
            description = "A love story set in 1960s Japan amidst student protests and school tradition.",
            year = "2011",
            imdbUrl = "https://www.imdb.com/title/tt1798188/"
        )
    )
}