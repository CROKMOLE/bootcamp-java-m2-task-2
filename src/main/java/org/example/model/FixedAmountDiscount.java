package org.example.model;

public class FixedAmountDiscount extends Discount {
    private static double amount = 20;

    public FixedAmountDiscount() {
        super("FIXED");
    }

    @Override
    public double apply(double originalAmount) {

        return Math.max(0, originalAmount - amount);
    }
}
