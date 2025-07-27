package com.sillador.strecs.utility;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomTimeGenerator {

    private  RandomTimeGenerator(){

    }
    public static Time getRandomTime() {
        Random random = new Random();
        int intervals = (12 * 2) + 1; // From 6:00 to 18:00 in 30-minute steps (6:00, 6:30, ..., 18:00)
        int randomStep = random.nextInt(intervals); // Random number between 0 and 24

        // Each step is 30 minutes starting from 06:00
        LocalTime baseTime = LocalTime.of(6, 0).plusMinutes(30L * randomStep);

        return Time.valueOf(baseTime);
    }

    public static String formatToAmPm(Time time) {
        // Convert java.sql.Time to LocalTime
        LocalTime localTime = time.toLocalTime();

        // Define formatter with AM/PM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        // Format and return
        return localTime.format(formatter);
    }

    public static Time addOneHour(Time time, int hour) {
        LocalTime localTime = time.toLocalTime();
        LocalTime newTime = localTime.plusHours(hour);
        return Time.valueOf(newTime);
    }


}
