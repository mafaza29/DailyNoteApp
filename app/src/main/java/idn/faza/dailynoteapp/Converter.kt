package idn.faza.dailynoteapp

import androidx.room.TypeConverter
import idn.faza.dailynoteapp.model.Priority

class Converter {
    @TypeConverter
    fun fromPriority(priority: Priority) : String{
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String) : Priority {
        return Priority.valueOf(priority)
    }
}