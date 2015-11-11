package net.ralphpina.shoppertest.timer;

public class TimerUtils {

    public static String getHumanReadableTimeElapsed(long secondsElapsed) {
        long minutes = secondsElapsed / 60;
        long seconds = secondsElapsed % 60;

        String minutesStr = (minutes < 10) ? "0" + minutes : "" + minutes;
        String secondsStr = (seconds < 10) ? "0" + seconds : "" + seconds;
        return minutesStr + ":" + secondsStr;
    }
}
