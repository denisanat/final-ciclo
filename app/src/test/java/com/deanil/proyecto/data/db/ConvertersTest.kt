package com.deanil.proyecto.data.db

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun testLongToDate() {
        val timestamp = 0L
        val expectedDate = Date(timestamp)

        val resultDate = converters.longToDate(timestamp)

        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun testDateToLong() {
        val date = Date(1627840200000L)
        val expectedTimestamp = 1627840200000L

        val resultTimestamp = converters.dateToLong(date)

        assertEquals(expectedTimestamp, resultTimestamp)
    }

    @Test
    fun testLongToDateWithZero() {
        val timestamp = 0L
        val expectedDate = Date(timestamp)

        val resultDate = converters.longToDate(timestamp)

        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun testDateToLongWithEpoch() {
        val date = Date(0L)
        val expectedTimestamp = 0L

        val resultTimestamp = converters.dateToLong(date)

        assertEquals(expectedTimestamp, resultTimestamp)
    }
}
