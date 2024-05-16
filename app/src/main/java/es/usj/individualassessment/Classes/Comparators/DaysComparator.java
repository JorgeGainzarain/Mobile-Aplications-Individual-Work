package es.usj.individualassessment.Classes.Comparators;


import java.util.Calendar;
import java.util.Comparator;

import es.usj.individualassessment.Classes.Day;

 public class DaysComparator implements Comparator<Day> {
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


