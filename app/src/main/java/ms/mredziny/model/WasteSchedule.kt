package ms.mredziny.model

import ms.mredziny.data.db.wastemanagement.WasteFraction
import java.time.LocalDate
import java.time.YearMonth

// Dla każdego dnia miesiąca zbiór frakcji, które są odbierane tego dnia.
data class WasteSchedule(
    val month: YearMonth,
    val days: Map<LocalDate, Set<WasteFraction>>,
)
