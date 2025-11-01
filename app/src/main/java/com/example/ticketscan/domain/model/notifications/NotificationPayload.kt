package com.example.ticketscan.domain.model.notifications

import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface NotificationPayload : Serializable {
    val type: NotificationType

    fun toMap(): Map<String, String>

    data class WeeklyStats(
        val totalSpent: BigDecimal,
        val averagePerTicket: BigDecimal,
        val previousTotal: BigDecimal,
        val ticketCount: Int,
        val topCategory: String?
    ) : NotificationPayload {
        override val type: NotificationType = NotificationType.WEEKLY_STATS

        override fun toMap(): Map<String, String> = buildMap {
            put("type", type.id)
            put("totalSpent", totalSpent.stripTrailingZeros().toPlainString())
            put("averagePerTicket", averagePerTicket.stripTrailingZeros().toPlainString())
            put("previousTotal", previousTotal.stripTrailingZeros().toPlainString())
            put("ticketCount", ticketCount.toString())
            topCategory?.let { put("topCategory", it) }
        }
    }

    data class MonthlyComparison(
        val currentTotal: BigDecimal,
        val previousTotal: BigDecimal
    ) : NotificationPayload {
        override val type: NotificationType = NotificationType.MONTHLY_COMPARISON

        val difference: BigDecimal = currentTotal.subtract(previousTotal)
        val variationPercent: BigDecimal = if (previousTotal.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else {
            difference.divide(previousTotal, 4, RoundingMode.HALF_UP) * BigDecimal.valueOf(100)
        }

        override fun toMap(): Map<String, String> = mapOf(
            "type" to type.id,
            "currentTotal" to currentTotal.stripTrailingZeros().toPlainString(),
            "previousTotal" to previousTotal.stripTrailingZeros().toPlainString(),
            "difference" to difference.stripTrailingZeros().toPlainString(),
            "variationPercent" to variationPercent.stripTrailingZeros().toPlainString()
        )
    }

    data class WeeklyInactivity(
        val daysWithoutTicket: Long,
        val lastTicketDate: LocalDate?
    ) : NotificationPayload {
        override val type: NotificationType = NotificationType.WEEKLY_INACTIVITY

        override fun toMap(): Map<String, String> = buildMap {
            put("type", type.id)
            put("daysWithoutTicket", daysWithoutTicket.toString())
            lastTicketDate?.let { put("lastTicketDate", ISO_DATE.format(it)) }
        }
    }

    companion object {
        private val ISO_DATE: DateTimeFormatter = DateTimeFormatter.ISO_DATE

        fun fromMap(data: Map<String, String>): NotificationPayload? {
            val typeKey = data["type"] ?: return null
            val type = NotificationType.fromId(typeKey) ?: return null
            return when (type) {
                NotificationType.WEEKLY_STATS -> data.toWeeklyStats()
                NotificationType.MONTHLY_COMPARISON -> data.toMonthlyComparison()
                NotificationType.WEEKLY_INACTIVITY -> data.toWeeklyInactivity()
            }
        }

        private fun Map<String, String>.toWeeklyStats(): WeeklyStats? {
            val total = get("totalSpent")?.toBigDecimalOrNull() ?: return null
            val average = get("averagePerTicket")?.toBigDecimalOrNull() ?: return null
            val previousTotal = get("previousTotal")?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val ticketCount = get("ticketCount")?.toIntOrNull() ?: 0
            val topCategory = get("topCategory")
            return WeeklyStats(total, average, previousTotal, ticketCount, topCategory)
        }

        private fun Map<String, String>.toMonthlyComparison(): MonthlyComparison? {
            val current = get("currentTotal")?.toBigDecimalOrNull() ?: return null
            val previous = get("previousTotal")?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            return MonthlyComparison(currentTotal = current, previousTotal = previous)
        }

        private fun Map<String, String>.toWeeklyInactivity(): WeeklyInactivity? {
            val days = get("daysWithoutTicket")?.toLongOrNull() ?: return null
            val lastTicket = get("lastTicketDate")?.let { runCatching { LocalDate.parse(it, ISO_DATE) }.getOrNull() }
            return WeeklyInactivity(daysWithoutTicket = days, lastTicketDate = lastTicket)
        }
    }
}
