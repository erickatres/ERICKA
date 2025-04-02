package com.example.bookyournailsmobile.Class

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class DateValidatorNoPastAndNoSundays : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val maxDate = getLastDayOfYear()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        // Allow only dates from today to Dec 31 of the current year and exclude Sundays
        return date in today..maxDate && dayOfWeek != Calendar.SUNDAY
    }

    private fun getLastDayOfYear(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentYear = cal.get(Calendar.YEAR)
        cal.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59)
        return cal.timeInMillis
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
