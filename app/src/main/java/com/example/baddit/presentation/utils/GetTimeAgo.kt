import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getTimeAgoFromUtcString(utcString: String): String {
    val utcDateTime: ZonedDateTime = ZonedDateTime.parse(utcString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val localDateTime: LocalDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    val now = LocalDateTime.now()
    val duration = Duration.between(localDateTime, now)

    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toHours() < 1 -> "${duration.toMinutes()}m"
        duration.toDays() < 1 -> "${duration.toHours()}h"
        duration.toDays() < 30 -> "${duration.toDays()}d"
        duration.toDays() < 365 -> "${duration.toDays() / 30}mo"
        else -> "${duration.toDays() / 365}y"
    }
}
