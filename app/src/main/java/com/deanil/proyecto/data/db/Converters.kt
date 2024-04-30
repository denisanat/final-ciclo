package com.deanil.proyecto.data.db

import androidx.room.TypeConverter
import java.util.Date

class Converters {

        @TypeConverter
        fun longToDate(value: Long): Date {
            return Date(value)
        }

        @TypeConverter
        fun dateToLong(date: Date): Long {
            return date.time.toLong()
        }
}