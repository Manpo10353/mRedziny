package ms.mredziny.model

data class TimeT(
    val minute: Int,
    val annotation: String,
    val description: String? = null,
)

data class Schedule(
    val times: Map<Int, List<TimeT>>,
)
