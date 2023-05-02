package com.arash.altafi.chatgptsimple.ext

import androidx.appcompat.app.AppCompatActivity
import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.Month
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import saman.zamani.persiandate.PersianDate
import java.util.*

fun PersianDate.getClockString(withSecond: Boolean = false): String {
    val secondString: String = if (withSecond) "::$second" else ""
    return if (withSecond) "%02d:%02d:%02d".applyValue(hour, minute, secondString.toInt())
        else "%02d:%02d".applyValue(hour, minute)
}

fun PersianDate.getDateString(): String {
    val year = shYear
    val month = if (shMonth < 10) "0${shMonth}" else shMonth
    val day = if (shDay < 10) "0${shDay}" else shDay

    return ("$year/$month/$day")
}

fun PersianDate.getDateStringWithClock(withSecond: Boolean = false): String {
    val year = shYear
    val month = if (shMonth < 10) "0${shMonth}" else shMonth
    val day = if (shDay < 10) "0${shDay}" else shDay

    val secondString: String = if (withSecond) ":$second" else ""
    return ("$year/$month/$day $hour:$minute$secondString")
}

fun PersianDate.getTimeString(withSecond: Boolean = false): String {
    val secondString: String = if (withSecond) ":$second" else ""
    return ("$hour:$minute$secondString")
}

private fun getPersianWeekDayName(index: Int): String = when (index) {
    0 -> "شنبه"
    1 -> "یک شنبه"
    2 -> "دو شنبه"
    3 -> "سه شنبه"
    4 -> "چهار شنبه"
    5 -> "پنج شنبه"
    else -> "جمعه"
}

private fun getPersianMonthName(index: Int): String = when (index) {
    1 -> "فروردین"
    2 -> "اردیبهشت"
    3 -> "خرداد"
    4 -> "تیر"
    5 -> "مرداد"
    6 -> "شهریور"
    7 -> "مهر"
    8 -> "آبان"
    9 -> "آذر"
    10 -> "دی"
    11 -> "بهمن"
    else -> "اسفند"
}

fun PersianDate.getDateClassified(): String {
    val todayCalendar = Calendar.getInstance()
    val targetCalendar = Calendar.getInstance().apply { timeInMillis = this@getDateClassified.time }

    return when {
        //today > (HH:MM) 23:59
        this.dayUntilToday == 0L -> {
            "%02d:%02d".applyValue(hour, minute)
        }
        //more than 1 year > (yy.MM.DD) 01.12.31
        todayCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR) -> {
            "%02d.%02d.%02d".applyValue(shYear % 100, shMonth, shDay)
        }
        //same week > (week-day name) جمعه
        todayCalendar.get(Calendar.WEEK_OF_YEAR) == targetCalendar.get(Calendar.WEEK_OF_YEAR) -> {
            getPersianWeekDayName(dayOfWeek())
        }
        //same year > (dd month-name) 29 اسفند
        else -> {
            "%02d %s".applyValue(
                shDay,
                getPersianMonthName(shMonth)
            )
        }
    }
}

/**
 * @return =>
 *  in this year: 25 شهریور
 *  past(or feature) years:  9 مهر 1401
 */
fun PersianDate.getDateClassifiedByDayMothYear(): String {
    val todayCalendar = Calendar.getInstance()
    val targetCalendar =
        Calendar.getInstance().apply { timeInMillis = this@getDateClassifiedByDayMothYear.time }

    return when {
        //this year > (yy.MM.DD) 9 مهر 1401
        todayCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR) -> {
            "%02d.%02d.%02d".applyValue(shYear % 100, shMonth, shDay)
        }
        //same year > (dd month-name) 29 اسفند
        else -> {
            "%02d %s".applyValue(
                shDay,
                getPersianMonthName(shMonth)
            )
        }
    }
}

fun AppCompatActivity.showCalendarDialog(
    fromNow: Boolean = false,
    listener: (timestamp: Long?) -> Unit
) {
    val calendar = PersianCalendar()
    calendar.setPersian(1358, Month.FARVARDIN, 1)
    val start = calendar.timeInMillis
    calendar.setPersian(1500, Month.ESFAND, 29)
    val end = calendar.timeInMillis

    val openAt = PersianCalendar.getToday().timeInMillis
    val constraints = CalendarConstraints.Builder()
        .setStart(if (fromNow) openAt else start)
        .setEnd(end)
        .setOpenAt(openAt)

    if (fromNow)
        constraints.setValidator(DateValidatorPointForward.from(openAt))


    MaterialDatePicker.Builder
        .datePicker()
        .setTitleText("select date")
        .setCalendarConstraints(constraints.build()).build().apply {
            addOnPositiveButtonClickListener {
                listener(selection)
            }
        }
        .show(supportFragmentManager, "MaterialDatePicker")
}