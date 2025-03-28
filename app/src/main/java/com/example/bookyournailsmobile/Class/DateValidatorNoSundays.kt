package com.example.bookyournailsmobile.Class

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class DateValidatorNoPastAndNoSundays : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = today
        cal.add(Calendar.DAY_OF_MONTH, 10) // Calculate max allowed date (5 days ahead)
        val maxDate = cal.timeInMillis

        cal.timeInMillis = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        // Allow only future dates (today or later), exclude Sundays, and limit to 5 days ahead
        return date in today..maxDate && dayOfWeek != Calendar.SUNDAY
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    companion object CREATOR : Parcelable.Creator<DateValidatorNoPastAndNoSundays> {
        override fun createFromParcel(parcel: Parcel): DateValidatorNoPastAndNoSundays {
            return DateValidatorNoPastAndNoSundays()
        }

        override fun newArray(size: Int): Array<DateValidatorNoPastAndNoSundays?> {
            return arrayOfNulls(size)
        }
    }
}
