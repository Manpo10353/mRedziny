package ms.mredziny.data.repository

import ms.mredziny.data.db.communication.CommunicationDao
import ms.mredziny.data.db.communication.DayType
import ms.mredziny.model.Schedule
import ms.mredziny.model.TimeT
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusScheduleRepository @Inject constructor(
    private val dao: CommunicationDao,
) {
    suspend fun dayTypeFor(date: LocalDate): DayType = when {
        date.dayOfWeek == DayOfWeek.SUNDAY || dao.isHoliday(date.toString()) -> DayType.SUNDAY_HOLIDAY
        date.dayOfWeek == DayOfWeek.SATURDAY -> DayType.SATURDAY
        date.month.value in 7..8 -> DayType.WORKDAY_SUMMER
        else -> DayType.WORKDAY_SCHOOL
    }

    suspend fun schedule(stopId: Long, dayType: DayType, line: String?): Schedule {
        val times = dao.departuresForStop(stopId, dayType.dbValue)
            .asSequence()
            .filter { line == null || it.line == line }
            .mapNotNull { row ->
                parseTime(row.time)?.let { (hour, minute) ->
                    hour to TimeT(minute, row.line, row.description)
                }
            }
            .groupBy({ it.first }, { it.second })
            .toSortedMap()
        return Schedule(times)
    }

    private fun parseTime(value: String): Pair<Int, Int>? {
        val parts = value.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return null
        return hour to minute
    }
}
