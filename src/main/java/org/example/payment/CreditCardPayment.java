package org.example.payment;

import org.example.model.PaymentResult;

public class CreditCardPayment extends PaymentMethod {
    private final String cardNumber;
    private final String cardHolderName;

    public CreditCardPayment(String cardNumber, String cardHolderName) {
        super("CreditCard");
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if(amount <= 0)
            throw new IllegalStateException("The amount of payment is smaller or equal zero.");

        if(cardNumber.isEmpty())
            throw new IllegalStateException("Card number is not set.");

        if(cardHolderName.isEmpty())
            throw new IllegalStateException("Name of card holder is not exists");

        return new PaymentResult(true, "Paid " + amount + " using credit card ending with " + cardNumber.substring(cardNumber.length() - 4));
    }
}
