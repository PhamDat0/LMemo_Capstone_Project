package com.example.lmemo_capstone_project.room_database;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Integer boolToInteger(Boolean value) {
        return value?1:0;
    }

    @TypeConverter
    public static Boolean integerToBoolean(Integer value) {
        switch (value) {
            case 1:
                return true;
            case 0:
                return false;
        }
        throw new IllegalArgumentException();
    }
}