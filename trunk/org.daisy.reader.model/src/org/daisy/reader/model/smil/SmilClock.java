package org.daisy.reader.model.smil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A <code>SmilClock</code> object is a wrapper for a SMIL clock value (time)
 * 
 * <pre>
 * Versions:
 * 0.1.0 (09/02/2003)
 * - Implemented string parsing
 * - Implemented both toString() methods
 * 0.1.1 (10/02/2003)
 * - Added static method to get/set tolerance for equals() and compareTo() methods
 * - Modified equals() and compareTo() to take tolerance value into account
 * 0.2.0 (10/04/2003)
 * - Added support for npt= formats
 * - Fixed bug in SmilClock(double) constructor
 * - Fixed nasty bug in SmilClock(String) constructor
 * 1.0.1 (11/01/2004)
 * - Fixed bug in milliseconds parsing in SmilClock(String s); now handles values with more/less than 3 digits
 * - Fixed bug in toString(int format) that caused milliseconds to lose leading zeroes
 * 1.0.2 (11/06/2005) Markus
 * - Added optimization: patterns compiled and static
 * 1.0.3 (21/06/2005) Markus 
 * - Added secondsValueRounded
 * 1.0.4 (10/02/2006) Linus
 * - Fixed locale bug in toString: now using DecimalFormat instead of NumberFormat
 * 1.0.5 (20/06/2006 Laurie
 * - Added HUMAN_READABLE static int toString(int)
 * 1.1.0 (14/11/2006) Linus
 * - Use BigDecimal instead of double to avoid rounding errors
 * </pre>
 * 
 * @author James Pritchett
 */
public class SmilClock implements Comparable<Object> {
    
    private static Pattern fullClockPattern = Pattern
            .compile("(npt=)?(\\d+):([0-5]\\d):([0-5]\\d)([.](\\d+))?"); //$NON-NLS-1$
    private static Pattern partialClockPattern = Pattern
            .compile("(npt=)?([0-5]\\d):([0-5]\\d)([.](\\d+))?"); //$NON-NLS-1$
    private static Pattern timecountClockPattern = Pattern
            .compile("(npt=)?(\\d+([.]\\d+)?)(h|min|s|ms)?"); //$NON-NLS-1$

    /**
     * @param s A string representation of the SMIL clock value in any accepted
     *            format
     * @throws NumberFormatException if the string is not a legal SMIL clock
     *             value format
     */
    public SmilClock(String s) throws NumberFormatException {
        Matcher m;
        BigDecimal bd;

        /*
         * This uses regular expressions to parse the given string. It tries
         * each of the three formats (full, partial, timecount) and throws an
         * exception if none of them match. It uses regular expression groupings
         * to capture the various numeric portions of the string at parse-time,
         * which it then uses to calculate the milliseconds value.
         */

        // test for timecount clock value
        m = timecountClockPattern.matcher(s.trim());
        if (m.matches()) {
            bd = new BigDecimal(m.group(2)); // Save the number (with
            // fraction)
            if (m.group(4) == null) {
                // this.msecValue = (long)(bd.longValue() * 1000);
                // //(28/11/2006)Piotr: this one truncates fraction
                this.msecValue = bd.multiply(BigDecimal.valueOf((long) 1000))
                        .longValue();
            } else if (m.group(4).equals("ms")) { //$NON-NLS-1$
                this.msecValue = bd.longValue(); // NOTE: This will truncate
                // fraction
            } else if (m.group(4).equals("s")) { //$NON-NLS-1$
                // this.msecValue = bd.multiply(new
                // BigDecimal((long)1000)).longValue(); //(28/11/2006)Piotr: the
                // construcor BigDecimal(long l) missing in java 1.4; ZedVal
                // feature
                this.msecValue = bd.multiply(BigDecimal.valueOf((long) 1000))
                        .longValue();
            } else if (m.group(4).equals("min")) { //$NON-NLS-1$
                // this.msecValue = bd.multiply(new
                // BigDecimal((long)60000)).longValue(); //(28/11/2006)Piotr: as
                // above
                this.msecValue = bd.multiply(BigDecimal.valueOf((long) 60000))
                        .longValue();
            } else if (m.group(4).equals("h")) { //$NON-NLS-1$
                // this.msecValue = bd.multiply(new
                // BigDecimal((long)3600000)).longValue(); //(28/11/2006)Piotr:
                // as above
                this.msecValue = bd
                        .multiply(BigDecimal.valueOf((long) 3600000))
                        .longValue();
            }
            return;
        }

        // test for a full clock value
        m = fullClockPattern.matcher(s.trim());
        if (m.matches()) {
            this.msecValue = (Long.parseLong(m.group(2)) * 3600000)
                    + (Long.parseLong(m.group(3)) * 60000)
                    + (Long.parseLong(m.group(4)) * 1000)
                    + ((m.group(6) != null) ? Math.round(new BigDecimal(m
                            .group(5)).multiply(BigDecimal.valueOf(1000))
                            .doubleValue()) : 0);
            return;
        }

        // test for partial clock value
        m = partialClockPattern.matcher(s.trim());
        if (m.matches()) {
            this.msecValue = (Long.parseLong(m.group(2)) * 60000)
                    + (Long.parseLong(m.group(3)) * 1000)
                    + ((m.group(5) != null) ? Math.round(new BigDecimal(m
                            .group(4)).multiply(BigDecimal.valueOf(1000))
                            .doubleValue()) : 0);
            return;
        }

        // If we got this far, s is not a legal SMIL clock value
        throw new NumberFormatException("Invalid SMIL clock value format: " //$NON-NLS-1$
                + s.trim());
    }

    /**
     * @param msec Time value in milliseconds
     */
    public SmilClock(long msec) {
        this.msecValue = msec;
    }

    /**
     * @param sec Time value in seconds
     */
    public SmilClock(double sec) {
        this.msecValue = (long) (sec * 1000);
    }

    /**
     * Returns clock value in full clock value format (default)
     * 
     * @return String in full clock value format (HH:MM:SS.mmm)
     */
    @Override
    public String toString() {
        return this.toString(SmilClock.FULL);
    }

    /**
     * Returns clock value in specified format
     * 
     * @param format Format code (FULL, PARTIAL, TIMECOUNT)
     * @return String with value in named format
     */
    public String toString(int format) {
        long hr;
        long min;
        long sec;
        long msec;
        long tmp;

        String s;

        NumberFormat nfInt = NumberFormat.getIntegerInstance();
        nfInt.setMinimumIntegerDigits(2);
        NumberFormat nfMsec = NumberFormat.getIntegerInstance();
        nfMsec.setMinimumIntegerDigits(3);
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
        dfSymbols.setDecimalSeparator('.');
        DecimalFormat dfDouble = new DecimalFormat("0.000", dfSymbols); //$NON-NLS-1$
        dfDouble.setMaximumFractionDigits(3);
        dfDouble.setGroupingUsed(false);

        // Break out all the pieces ...
        msec = this.msecValue % 1000;
        tmp = (this.msecValue - msec) / 1000;
        sec = tmp % 60;
        tmp = (tmp - sec) / 60;
        min = tmp % 60;
        hr = (tmp - min) / 60;

        switch (format) {
        case FULL:
            if (msec > 0) {
                s = hr + ":" + nfInt.format(min) + ":" + nfInt.format(sec) //$NON-NLS-1$ //$NON-NLS-2$
                        + "." + nfMsec.format(msec); //$NON-NLS-1$
            } else {
                s = hr + ":" + nfInt.format(min) + ":" + nfInt.format(sec); //$NON-NLS-1$ //$NON-NLS-2$
            }
            break;
        case PARTIAL:
            // KNOWN BUG: This will return misleading results for clock values >
            // 59:59.999
            // WORK AROUND: Caller is responsible for testing that this is an
            // appropriate format
            if (msec > 0) {
                s = nfInt.format(min) + ":" + nfInt.format(sec) + "." //$NON-NLS-1$ //$NON-NLS-2$
                        + nfMsec.format(msec);
            } else {
                s = nfInt.format(min) + ":" + nfInt.format(sec); //$NON-NLS-1$
            }
            break;
        case TIMECOUNT:
            s = dfDouble.format(BigDecimal.valueOf(this.msecValue).divide(
                    BigDecimal.valueOf(1000)));
            break;
        case TIMECOUNT_MSEC:
            s = dfDouble.format(BigDecimal.valueOf(this.msecValue)) + "ms"; //$NON-NLS-1$
            break;
        case TIMECOUNT_SEC:
            s = dfDouble.format(BigDecimal.valueOf(this.msecValue).divide(
                    BigDecimal.valueOf(1000)))
                    + "s"; //$NON-NLS-1$
            break;
        case TIMECOUNT_MIN:
            s = dfDouble.format(BigDecimal.valueOf(this.msecValue).divide(
                    BigDecimal.valueOf(60000)))
                    + "min"; //$NON-NLS-1$
            break;
        case TIMECOUNT_HR:
            s = dfDouble.format(BigDecimal.valueOf(this.msecValue).divide(
                    BigDecimal.valueOf(360000)))
                    + "h"; //$NON-NLS-1$
            break;
        case HUMAN_READABLE:
            if (hr > 0) {
                s = hr + " h " + nfInt.format(min) + " min "; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (min > 0) {
                s = nfInt.format(min) + " min " + nfInt.format(sec) + " s"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (sec > 0) {
                s = nfInt.format(sec) + " s " + nfMsec.format(msec) + " ms"; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                s = nfMsec.format(msec) + " ms"; //$NON-NLS-1$
            }
            break;
        default:
            throw new NumberFormatException("Unknown SMIL clock format code: " //$NON-NLS-1$
                    + format);
        }
        return s;
    }

    /**
     * Returns clock value in milliseconds
     * 
     * @return clock value in milliseconds
     */
    public long millisecondsValue() {
        return this.msecValue;
    }

    /**
     * Returns clock value in seconds
     * 
     * @return clock value in seconds
     */
    public double secondsValue() {
        // return new
        // BigDecimal(this.msecValue).divide(BigDecimal.valueOf(1000)).doubleValue();
        // //(28/11/2006)PK: BigDecimal#divide(BigDecimal bd) not in java 1.4;
        // ZedVal feature
        return (double) this.msecValue / 1000;
    }

    /**
     * Returns clock value in seconds, rounded to full seconds
     * 
     * @return clock value in seconds, rounded to full seconds
     */
    public long secondsValueRounded() {
        return Math.round(this.secondsValue());
    }

    // implement equals() so we can test values for equality
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject)
            return true; // Objects are identical
        if (otherObject == null)
            return false; // There ain't nuthin' like a null ...
        if (getClass() != otherObject.getClass())
            return false; // No class-mixing, either
        try {
            SmilClock other = (SmilClock) otherObject; // Cast it, then
            // compare, using
            // tolerance
            if (Math.abs(other.msecValue - this.msecValue) <= msecTolerance) {
                return true;
            }
        } catch (ClassCastException cce) {
            // do nothing
        }
        return false;
    }

    // implement Comparable interface so we can sort and compare values
    public int compareTo(Object otherObject) throws ClassCastException {
        SmilClock other = (SmilClock) otherObject; // Hope for the best!
        if (Math.abs(other.msecValue - this.msecValue) <= msecTolerance)
            return 0;
        if (this.msecValue < other.msecValue)
            return -1;
        return 1;
    }

    // Static methods

    /**
     * Sets tolerance for comparisons and equality testing.
     * <p>
     * When comparing two values, if they differ by less than the given
     * tolerance, they will be evaluated as equal to one another.
     * </p>
     * 
     * @param msec Tolerance value in milliseconds
     */
    public static void setTolerance(long msec) {
        msecTolerance = msec;
    }

    /**
     * Returns tolerance setting
     * 
     * @return Current tolerance value in milliseconds
     */
    public static long getTolerance() {
        return msecTolerance;
    }

    // Type codes for the different SMIL clock value formats
    public static final int FULL = 1;
    public static final int PARTIAL = 2;
    public static final int TIMECOUNT = 3; // Default version (no metric)
    public static final int TIMECOUNT_MSEC = 4;
    public static final int TIMECOUNT_SEC = 5;
    public static final int TIMECOUNT_MIN = 6;
    public static final int TIMECOUNT_HR = 7;
    public static final int HUMAN_READABLE = 8;

 	private long msecValue; // All values stored in milliseconds
    private static long msecTolerance;
}