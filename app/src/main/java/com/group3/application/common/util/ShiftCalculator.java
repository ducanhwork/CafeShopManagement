package com.group3.application.common.util;

import java.math.BigDecimal;

/**
 * Utility class for shift-related calculations
 */
public class ShiftCalculator {
    
    /**
     * Calculate expected cash at end of shift
     * @param openingCash Cash at start of shift
     * @param cashIn Total cash received
     * @param cashOut Total cash paid out
     * @param refunds Total refunds given
     * @return Expected closing cash
     */
    public static BigDecimal calculateExpectedCash(BigDecimal openingCash, 
                                                   BigDecimal cashIn, 
                                                   BigDecimal cashOut, 
                                                   BigDecimal refunds) {
        if (openingCash == null) openingCash = BigDecimal.ZERO;
        if (cashIn == null) cashIn = BigDecimal.ZERO;
        if (cashOut == null) cashOut = BigDecimal.ZERO;
        if (refunds == null) refunds = BigDecimal.ZERO;
        
        return openingCash.add(cashIn).subtract(cashOut).subtract(refunds);
    }
    
    /**
     * Calculate discrepancy between expected and actual cash
     * @param expectedCash Expected closing cash
     * @param actualCash Actual closing cash
     * @return Discrepancy amount (positive = surplus, negative = shortage)
     */
    public static BigDecimal calculateDiscrepancy(BigDecimal expectedCash, BigDecimal actualCash) {
        if (expectedCash == null) expectedCash = BigDecimal.ZERO;
        if (actualCash == null) actualCash = BigDecimal.ZERO;
        
        return actualCash.subtract(expectedCash);
    }
    
    /**
     * Get color indicator for discrepancy
     * @param discrepancy Discrepancy amount
     * @return Color code (0 = green, 1 = yellow, 2 = red)
     */
    public static int getDiscrepancyColorCode(BigDecimal discrepancy) {
        if (discrepancy == null) {
            return 0; // Green - no discrepancy
        }
        
        BigDecimal absDiscrepancy = discrepancy.abs();
        
        if (absDiscrepancy.compareTo(BigDecimal.ZERO) == 0) {
            return 0; // Green - perfect match
        } else if (absDiscrepancy.compareTo(new BigDecimal("50000")) < 0) {
            return 1; // Yellow - small discrepancy (< 50,000 VND)
        } else {
            return 2; // Red - large discrepancy (>= 50,000 VND)
        }
    }
    
    /**
     * Format discrepancy with sign
     */
    public static String formatDiscrepancy(BigDecimal discrepancy) {
        if (discrepancy == null || discrepancy.compareTo(BigDecimal.ZERO) == 0) {
            return "0 ₫";
        }
        
        String formatted = CurrencyUtils.format(discrepancy.abs());
        return discrepancy.compareTo(BigDecimal.ZERO) > 0 
            ? "+" + formatted + " (Surplus)" 
            : "-" + formatted + " (Shortage)";
    }
    
    /**
     * Calculate shift duration in minutes
     */
    public static long calculateShiftDuration(long startTime, long endTime) {
        return (endTime - startTime) / (1000 * 60); // Convert ms to minutes
    }
    
    /**
     * Format shift duration
     */
    public static String formatShiftDuration(long durationMinutes) {
        long hours = durationMinutes / 60;
        long minutes = durationMinutes % 60;
        return String.format("%d hours %d minutes", hours, minutes);
    }
}
