package com.example.devotio.data

import java.util.Date

class TempleRepository {
    // TODO: Replace with actual API calls
    fun getTemples(): List<Temple> {
        return listOf(
            Temple(
                id = 1,
                name = "Sri Venkateswara Temple",
                address = "123 Temple Street, Tirupati",
                latitude = 13.6288,
                longitude = 79.4192,
                openingTime = "04:00 AM",
                closingTime = "09:00 PM",
                description = "One of the most visited temples in India, dedicated to Lord Venkateswara.",
                imageUrl = "https://example.com/tirupati.jpg",
                deity = "Lord Venkateswara",
                timing = "4:00 AM - 9:00 PM",
                events = listOf(
                    TempleEvent(
                        id = 1,
                        templeId = 1,
                        name = "Annual Brahmotsavam",
                        description = "Nine-day festival celebrating Lord Venkateswara",
                        date = Date(),
                        startTime = "06:00 AM",
                        endTime = "09:00 PM"
                    ),
                    TempleEvent(
                        id = 2,
                        templeId = 1,
                        name = "Vaikunta Ekadasi",
                        description = "Special day for devotees to seek salvation",
                        date = Date(),
                        startTime = "04:00 AM",
                        endTime = "09:00 PM"
                    )
                ),
                isNearby = true,
                isFavorite = false
            ),
            Temple(
                id = 2,
                name = "Meenakshi Temple",
                address = "456 Temple Road, Madurai",
                latitude = 9.9197,
                longitude = 78.1194,
                openingTime = "05:00 AM",
                closingTime = "10:00 PM",
                description = "Ancient temple dedicated to Goddess Meenakshi and Lord Sundareswarar.",
                imageUrl = "https://example.com/meenakshi.jpg",
                deity = "Goddess Meenakshi and Lord Sundareswarar",
                timing = "5:00 AM - 10:00 PM",
                events = listOf(
                    TempleEvent(
                        id = 3,
                        templeId = 2,
                        name = "Chithirai Festival",
                        description = "Annual festival celebrating the divine marriage",
                        date = Date(),
                        startTime = "07:00 AM",
                        endTime = "10:00 PM"
                    )
                ),
                isNearby = false,
                isFavorite = false
            )
        )
    }

    fun getTempleById(id: Int): Temple? {
        return getTemples().find { it.id == id }
    }

    fun toggleFavorite(templeId: Int): Boolean {
        // TODO: Implement actual favorite toggling logic
        return true
    }

    fun getFavoriteTemples(): List<Temple> {
        // TODO: Implement actual favorite temples retrieval
        return getTemples().filter { it.isFavorite }
    }
} 