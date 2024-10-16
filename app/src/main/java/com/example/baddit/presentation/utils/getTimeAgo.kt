import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

fun getTimeAgoFromUtcString(utcString: String): String {
    val date: LocalDateTime = OffsetDateTime.parse(utcString).toLocalDateTime();
    val now = LocalDateTime.now()
    val duration = Duration.between(date, now)

    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toHours() < 1 -> "${duration.toMinutes()}m"
        duration.toDays() < 1 -> "${duration.toHours()}h"
        duration.toDays() < 30 -> "${duration.toDays()}d"
        duration.toDays() < 365 -> "${duration.toDays() / 30}mo"
        else -> "${duration.toDays() / 365}y"
    }
}
