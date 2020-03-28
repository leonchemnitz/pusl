package de.bp2019.pusl.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Basic Utility class, containing some handy utility functions
 * 
 * @author Leon Chemnitz
 */
public final class Utils {

    /**
     * Checks whether a class implements a given interface
     * 
     * @param clazz
     * @param interfaze
     * @return
     * @author Leon Chemnitz
     */
    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaze) {
        for (Class<?> c : clazz.getInterfaces()) {
            if (c.equals(interfaze)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a collection contains any Elements of another collection
     * 
     * @param <T>
     * @param col1
     * @param col2
     * @return
     * @author Leon Chemnitz
     */
    public static <T> boolean containsAny(Collection<T> col1, Collection<T> col2) {
        for (T item : col2) {
            if (col1.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate a random Date between two dates
     * 
     * @param start
     * @param end
     * @return
     * @author Leon Chemnitz
     */
    public static LocalDate randomDateBetween(LocalDate start, LocalDate end) {
        long startSeconds = start.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long endSeconds = end.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long random = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds);

        return LocalDate.ofInstant(Instant.ofEpochSecond(random), ZoneId.systemDefault());
    }

    /**
     * Converts a LocalDate to a Date
     * 
     * @param localDate
     * @return
     * @author Leon Chemnitz
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Checks wether a given String is a valid MatrikelNumber with the validation
     * algorithm of TU Darmstadt
     * 
     * @param m
     * @return
     * @author Leon Chemnitz
     */
    public static boolean isMatrNumber(String m) {
        if (m == null || !NumberUtils.isDigits(m)) {
            return false;
        }
        if (m.length() == 6) {
            m = "0" + m;
        }
        if (m.length() == 7) {
            int[] weight = new int[] { 9, 7, 3, 9, 7, 3 };
            int result = 0;
            for (int i = 0; i < m.length() - 1; i++) {
                result += (Integer.valueOf(m.charAt(i)) - 48) * weight[i];
            }
            return result % 10 == Integer.valueOf(m.charAt(m.length() - 1)) - 48;
        }

        return false;
    }

    public static boolean isMatrNumber(int m) {
        return isMatrNumber(Integer.toString(m));
    }
}