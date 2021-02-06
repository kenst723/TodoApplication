package app.sato.ken.todoapp_with_fragment.Functions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class GetDateFunc{
    fun getTomorrow(): String{
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        println("current: ${df.format(cal.time)}")

        cal.add(Calendar.DATE, 1)
        return  df.format(cal.time)
    }

    fun getTomorrowNotY(): String{
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("MM/dd")
        println("current: ${df.format(cal.time)}")

        cal.add(Calendar.DATE, 1)
        return  df.format(cal.time)
    }

    fun getNextWeek(): String{
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        println("current: ${df.format(cal.time)}")

        cal.add(Calendar.DATE, 7)
        return  df.format(cal.time)
    }

    fun getToday(): String{
        val date = Date()
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return format.format(date)
    }
}