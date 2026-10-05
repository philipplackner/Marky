package com.plcoding.marky.feature.notes.presentation.util

import androidx.compose.runtime.Composable
import com.plcoding.marky.feature.notes.domain.TimeSinceEdit
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.date_day_month
import com.plcoding.marky.feature.notes.presentation.generated.resources.date_day_month_year
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_1
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_10
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_11
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_12
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_2
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_3
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_4
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_5
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_6
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_7
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_8
import com.plcoding.marky.feature.notes.presentation.generated.resources.month_short_9
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_days
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_hours
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_just_now
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_minutes
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_weeks
import com.plcoding.marky.feature.notes.presentation.generated.resources.time_yesterday
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimeSinceEdit.asString(): String = when (this) {
    TimeSinceEdit.JustNow -> stringResource(Res.string.time_just_now)
    is TimeSinceEdit.Minutes -> stringResource(Res.string.time_minutes, minutes)
    is TimeSinceEdit.Hours -> stringResource(Res.string.time_hours, hours)
    TimeSinceEdit.Yesterday -> stringResource(Res.string.time_yesterday)
    is TimeSinceEdit.Days -> stringResource(Res.string.time_days, days)
    is TimeSinceEdit.Weeks -> stringResource(Res.string.time_weeks, weeks)
    is TimeSinceEdit.Date -> {
        val monthName = stringResource(MonthNames[month - 1])
        val year = year
        if (year == null) {
            stringResource(Res.string.date_day_month, day, monthName)
        } else {
            stringResource(Res.string.date_day_month_year, day, monthName, year)
        }
    }
}

private val MonthNames = listOf(
    Res.string.month_short_1,
    Res.string.month_short_2,
    Res.string.month_short_3,
    Res.string.month_short_4,
    Res.string.month_short_5,
    Res.string.month_short_6,
    Res.string.month_short_7,
    Res.string.month_short_8,
    Res.string.month_short_9,
    Res.string.month_short_10,
    Res.string.month_short_11,
    Res.string.month_short_12,
)
