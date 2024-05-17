package es.usj.individualassessment.Classes.Comparators;

import java.util.Comparator;

import es.usj.individualassessment.Classes.Comparators.CalendarComparator;
import es.usj.individualassessment.Classes.City;

public class CityComparator implements Comparator<City> {

    @Override
    public int compare(City c1, City c2) {
        return 0;
    }

    public int compareAddress(City c1, City c2) {
        // Compare country names
        int countryComparison = c1.getCountry().compareTo(c2.getCountry());
        if (countryComparison != 0) {
            return countryComparison;
        }

        // If provinces are not null, compare them
        if (c1.getProvince() != null && c2.getProvince() != null) {
            int provinceComparison = c1.getProvince().compareTo(c2.getProvince());
            if (provinceComparison != 0) {
                return provinceComparison;
            }
        } else if (c1.getProvince() == null && c2.getProvince() != null) {
            // If c1 has no province but c2 has, c1 comes first
            return -1;
        } else if (c1.getProvince() != null && c2.getProvince() == null) {
            // If c2 has no province but c1 has, c2 comes first
            return 1;
        }

        // Compare city names
        return c1.getName().compareTo(c2.getName());
    }

    public int compareTime(City c1, City c2) {
        return new CalendarComparator().compareTime(c1.getCalendar(), c2.getCalendar());
    }

    public int compareTimeZone(City c1, City c2) {
        return c1.getTzOffset() - c2.getTzOffset();
    }
    public int compareWeather(City c1, City c2) {
        return c1.getToday().getIcon().compareTo(c2.getToday().getIcon());
    }
    public int compareIsDay(City c1, City c2) {
        return Boolean.compare(c2.isDay(), c1.isDay());
    }


}
