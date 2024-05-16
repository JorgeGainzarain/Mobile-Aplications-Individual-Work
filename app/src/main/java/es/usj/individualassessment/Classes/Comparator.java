package es.usj.individualassessment.Classes;


import java.util.Calendar;
import java.util.Comparator;

class DaysComparator implements Comparator<Day> {
    @Override
    public int compare(Day a, Day b) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(a.getDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(b.getDate());

        if (cal1.after(cal2)) return 1;
        else if (cal1.before(cal2)) return -1;
        else return 0;
    }
}

class CalendarDateComparator implements Comparator<Calendar> {

    @Override
    public int compare(Calendar cal1, Calendar cal2) {
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

class CalendarTimeComparator implements Comparator<Calendar> {

    @Override
    public int compare(Calendar cal1, Calendar cal2) {
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
}