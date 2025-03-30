package com.itzkazuri.kalenderbali.utils

import java.util.*

object SakaCalendarHelper {
    // Constants for moon phases and holidays
    private const val FULL_MOON = "Purnama"
    private const val NEW_MOON = "Tilem"
    private const val NYEPI = "Hari Raya Nyepi"
    private const val SARASWATI = "Hari Raya Saraswati"
    private const val SIWARATRI = "Hari Suci Siwaratri"

    private val pasaranList = listOf("Umanis", "Paing", "Pon", "Wage", "Kliwon")
    private val wukuNames = listOf(
        "Sinta", "Landep", "Ukir", "Kulantir", "Tolu",
        "Gumbreg", "Wariga", "Warigadian", "Julungwangi", "Sungsang",
        "Dungulan", "Kuningan", "Langkir", "Medangsia", "Pujut",
        "Pahang", "Krulut", "Merakih", "Tambir", "Madangkungan",
        "Maktal", "Uye", "Manail", "Prangbakat", "Bala",
        "Ugu", "Wayang", "Kelawu", "Dukut", "Watugunung"
    )

    /**
     * Gets important dates in Balinese calendar for a given year
     */
    fun getImportantDates(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            addAll(getPurnamaTilem(year))
            add(calculateNyepi(year) to NYEPI)
            calculateSiwaratri(year)?.let { add(it to SIWARATRI) }
            addAll(getAllSaraswatiInYear(year))
        }.sortedBy { it.first.timeInMillis }
    }

    /**
     * Calculates all full moon (Purnama) and new moon (Tilem) dates in a year
     */
    private fun getPurnamaTilem(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL)
                val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)

                when {
                    penanggal == 15 && !isPangelong -> add(calendar.clone() as Calendar to FULL_MOON)
                    penanggal == 15 && isPangelong -> add(calendar.clone() as Calendar to NEW_MOON)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Calculates Nyepi date (1 Penanggal Sasih Kadasa)
     */
    fun calculateNyepi(year: Int): Calendar {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.MARCH, 1) }
        val endDate = Calendar.getInstance().apply { set(year, Calendar.APRIL, 1) }

        while (calendar < endDate) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 10 &&
                saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 1 &&
                !saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)) {
                return calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return Calendar.getInstance() // Fallback
    }

    /**
     * Calculates Siwaratri date (14 Pangelong Sasih Kapitu)
     */
    fun calculateSiwaratri(year: Int): Calendar? {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
        val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

        while (calendar <= endDate) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 7 &&
                saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 14 &&
                saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)) {
                return calendar.clone() as Calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return null
    }

    /**
     * Gets all Saraswati dates in a year (Saturday Umanis Wuku Watugunung)
     */
    fun getAllSaraswatiInYear(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                if (saka.getWuku(SakaCalendar.NO_WUKU) == 30 &&
                    saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 1 &&
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    add(calendar.clone() as Calendar to SARASWATI)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Converts Gregorian date to Saka date (year, month, day)
     */
    fun gregorianToSaka(calendar: Calendar): Triple<Int, Int, Int> {
        val saka = createSakaCalendar(calendar)
        return Triple(
            saka.getSakaCalendar(SakaCalendar.TAHUN_SAKA),
            saka.getSakaCalendar(SakaCalendar.NO_SASIH),
            saka.getSakaCalendar(SakaCalendar.PENANGGAL)
        )
    }

    /**
     * Gets Wuku name for a date
     */
    fun getWukuName(calendar: Calendar): String {
        val saka = createSakaCalendar(calendar)
        return wukuNames.getOrElse(saka.getWuku(SakaCalendar.NO_WUKU) - 1) { "Unknown" }
    }

    /**
     * Gets Pasaran name for a date
     */
    fun getPasaran(calendar: Calendar): String {
        val saka = createSakaCalendar(calendar)
        return pasaranList.getOrElse(saka.getPancawara(SakaCalendar.NO_PANCAWARA) - 1) { "Unknown" }
    }

    /**
     * Checks if date is in Pangelong phase
     */
    fun isPangelong(calendar: Calendar): Boolean {
        return createSakaCalendar(calendar).getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)
    }

    /**
     * Gets Wuku names list
     */
    fun getWukuNames(): List<String> = wukuNames

    /**
     * Formats important dates for a year as string
     */
    fun formatImportantDates(year: Int): String {
        return buildString {
            append("📅 Kalender Bali Tahun $year:\n\n")
            getImportantDates(year).forEach { (date, event) ->
                val sakaDate = gregorianToSaka(date)
                append("${formatDate(date)} - $event\n")
                append("   (Tahun Saka ${sakaDate.first}, Sasih ${sakaDate.second}, Penanggal ${sakaDate.third})\n")
                append("   Wuku ${getWukuName(date)}, ${getPasaran(date)}\n\n")
            }
        }
    }

    /**
     * Helper to create SakaCalendar instance
     */
    private fun createSakaCalendar(calendar: Calendar): SakaCalendar {
        return SakaCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Formats date as dd/MM/yyyy
     */
    private fun formatDate(date: Calendar): String {
        return date.run {
            "${get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/" +
                    "${(get(Calendar.MONTH) + 1).toString().padStart(2, '0')}/" +
                    get(Calendar.YEAR)
        }
    }
}