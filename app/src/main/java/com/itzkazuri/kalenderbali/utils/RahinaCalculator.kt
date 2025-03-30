package com.itzkazuri.kalenderbali.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object RahinaCalculator {

    /**
     * Menghitung tanggal Nyepi berdasarkan 1 Penanggal Sasih Kadasa (Sasih ke-10 dalam kalender Saka).
     */
    fun getNyepiDate(year: Int): String {
        val nyepiDate = SakaCalendarHelper.calculateNyepi(year)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(nyepiDate.time)
    }

    /**
     * Mengembalikan daftar hari raya (Nyepi, Siwaratri, Saraswati) berdasarkan tanggal Masehi.
     */
    fun getRerahinan(tanggal: Int, bulan: Int, tahun: Int): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(tahun, bulan - 1, tanggal)
        }

        // Ambil tahun Saka, Sasih, dan Penanggal
        val (_, sasih, penanggal) = SakaCalendarHelper.gregorianToSaka(calendar)
        val isPangelong = SakaCalendarHelper.isPangelong(calendar)

        val rerahinanList = mutableListOf<String>()

        // 1. Nyepi: 1 Penanggal Sasih Kadasa (Sasih 10)
        if (sasih == 10 && penanggal == 1 && !isPangelong) {
            rerahinanList.add("Nyepi")
        }

        // 2. Siwaratri: 14 Pangelong Sasih Kapitu (Sasih 7)
        if (sasih == 7 && penanggal == 14 && isPangelong) {
            rerahinanList.add("Siwaratri")
        }

        // 3. Saraswati: Sabtu Umanis Wuku Watugunung
        val wukuName = SakaCalendarHelper.getWukuName(calendar)
        val pasaran = SakaCalendarHelper.getPasaran(calendar)
        if (wukuName == "Watugunung" && pasaran == "Umanis" &&
            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            rerahinanList.add("Saraswati")
        }

        // Tambahkan Purnama dan Tilem jika diperlukan
        val purnamaTilemList = SakaCalendarHelper.getImportantDates(tahun)
            .filter {
                it.first.get(Calendar.DAY_OF_MONTH) == tanggal &&
                        it.first.get(Calendar.MONTH) + 1 == bulan &&
                        (it.second == "Purnama" || it.second == "Tilem")
            }
            .map { it.second }

        rerahinanList.addAll(purnamaTilemList)

        return rerahinanList
    }
}