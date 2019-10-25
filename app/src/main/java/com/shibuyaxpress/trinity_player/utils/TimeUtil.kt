package com.shibuyaxpress.trinity_player.utils

class TimeUtil {

    fun millisecondsToTimer(milliseconds:Long) : String {
        var finalStringTimer = ""
        var secondsString = ""
        //Converting total duration into time
        var hours = (milliseconds / 1000 * 60 * 60).toInt()
        var minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        var seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
        //adding hours if exists
        if (hours > 0) {
            finalStringTimer = "$hours:"
        }
        //Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0$seconds"
        }else {
            secondsString = "$seconds"
        }
        finalStringTimer = finalStringTimer + "$minutes:$secondsString"
        return finalStringTimer
    }

    fun getProgressPercentage(currentDuration:Long, totalDuration: Long) : Int {
        var percentage = 0.0
        var currentSeconds = (currentDuration / 1000).toInt()
        var totalSeconds = (totalDuration / 1000).toInt()
        //calculating percentage
        percentage = (currentSeconds.toDouble() / totalSeconds) * 100
        return percentage.toInt()
    }

    fun progressToTimer(progress: Int, totalDuration: Int) : Int {
        var currentDuration = 0.0
        currentDuration = (progress.toDouble() / 100) * (totalDuration.toDouble() / 1000) * 1000
        return currentDuration.toInt()

    }
}