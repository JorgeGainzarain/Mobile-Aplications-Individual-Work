package es.usj.individualassessment.Classes.Comparators;

import java.util.Calendar;
import java.util.Comparator;

public class CalendarComparator implements Comparator<Calendar> {

    @Override
    public int compare(Calendar cal1, Calendar cal2) {
        return cal1.compareTo(cal2);
    }

    public int compareTime(Calendar cal1, Calendar cal2) {
        // Compare the hours
        int hourComparison = Integer.compare(cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY));
        if (hourComparison != 0) {
            return hourComparison;
        }

        // Compare the minutes
        int minuteComparison = Integer.compare(cal1.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE));
        if (minuteComparison != 0) {
            return minuteComparison;
        }

        // Compare the seconds
        return Integer.compare(cal1.get(Calendar.SECOND), cal2.get(Calendar.SECOND));
    }

    public int compareDate(Calendar cal1, Calendar cal2) {
        // Compare the years
        int yearComparison = Integer.compare(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
        if (yearComparison != 0) {
            return yearComparison;
        }

        // Compare the months
        int monthComparison = Integer.compare(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
        if (monthComparison != 0) {
            return monthComparison;
        }

        // Compare the days
        return Integer.compare(cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
    }

}
