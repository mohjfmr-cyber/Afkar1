package com.example.data

import java.util.Calendar

object JalaliCalendar {
    fun getTodayJalali(): String {
        val calendar = Calendar.getInstance()
        val gYear = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) + 1
        val gDay = calendar.get(Calendar.DAY_OF_MONTH)
        return gregorianToJalali(gYear, gMonth, gDay)
    }

    fun gregorianToJalali(gYear: Int, gMonth: Int, gDay: Int): String {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gy = gYear
        var gm = gMonth
        val gd = gDay

        val isLeap = (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)
        if (isLeap) {
            gDaysInMonth[2] = 29
        }

        var gDayCount = 0
        for (i in 1 until gm) {
            gDayCount += gDaysInMonth[i]
        }
        gDayCount += gd

        var jy: Int
        var jm: Int
        var jd: Int

        val jDayNo: Int
        val gDayNo = gDayCount + (gy - 1) * 365 + (gy - 1) / 4 - (gy - 1) / 100 + (gy - 1) / 400

        if (gDayNo > 79) {
            val gDayNoDiff = gDayNo - 79
            val jNum = gDayNoDiff / 12053
            var jDayNoRemaining = gDayNoDiff % 12053
            jy = 979 + 33 * jNum + 4 * (jDayNoRemaining / 1461)
            jDayNoRemaining %= 1461
            if (jDayNoRemaining >= 366) {
                jy += (jDayNoRemaining - 1) / 365
                jDayNoRemaining = (jDayNoRemaining - 1) % 365
            }
            jDayNo = jDayNoRemaining
        } else {
            val jDayNoDiff = gDayNo + 120155
            val jNum = jDayNoDiff / 12053
            var jDayNoRemaining = jDayNoDiff % 12053
            jy = 33 * jNum + 4 * (jDayNoRemaining / 1461)
            jDayNoRemaining %= 1461
            if (jDayNoRemaining >= 366) {
                jy += (jDayNoRemaining - 1) / 365
                jDayNoRemaining = (jDayNoRemaining - 1) % 365
            }
            jy += 159
            jDayNo = jDayNoRemaining
        }

        var i = 1
        var jdTemp = jDayNo
        while (i <= 12) {
            val daysInJm = jDaysInMonth[i]
            val isJLeap = isJalaliLeap(jy)
            val actualDaysInJm = if (i == 12 && isJLeap) 30 else daysInJm
            if (jdTemp < actualDaysInJm) {
                break
            }
            jdTemp -= actualDaysInJm
            i++
        }
        jm = i
        jd = jdTemp + 1

        return String.format("%04d/%02d/%02d", jy, jm, jd)
    }

    private fun isJalaliLeap(jy: Int): Boolean {
        val r = (jy - 979) % 33
        return r == 0 || r == 4 || r == 8 || r == 12 || r == 16 || r == 20 || r == 24 || r == 28 || r == 32
    }
}
