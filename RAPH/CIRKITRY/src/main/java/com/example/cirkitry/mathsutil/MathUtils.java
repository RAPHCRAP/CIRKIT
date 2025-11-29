package com.example.cirkitry.mathsutil;

public class MathUtils {
    
    // Calculate what percentage 'part' is of 'whole'
    public static double percentageOf(double part, double whole) {
        if (whole == 0) return 0;
        return (part / whole) * 100;
    }
    
    // Calculate 'percentage'% of 'value'
    public static double percentageValue(double percentage, double value) {
        return (percentage / 100) * value;
    }
    
    // Calculate percentage change from oldValue to newValue
    public static double percentageChange(double oldValue, double newValue) {
        if (oldValue == 0) return 0;
        return ((newValue - oldValue) / oldValue) * 100;
    }
    
    // Apply percentage increase to value
    public static double increaseByPercent(double value, double percentage) {
        return value * (1 + percentage / 100);
    }
    
    // Apply percentage decrease to value
    public static double decreaseByPercent(double value, double percentage) {
        return value * (1 - percentage / 100);
    }
}