package org.example.payment;

import org.example.model.PaymentResult;

public class CryptoPayment extends PaymentMethod {
    private final String walletNumber;
    private final String secret;

    public CryptoPayment(String walletNumber, String secret) {
        super("CreditCard");
        this.walletNumber = walletNumber;
        this.secret = secret;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if(amount <= 0)
            throw new IllegalStateException("The amount of payment is smaller or equal zero.");

        if(walletNumber.isEmpty())
            throw new IllegalStateException("Wallet number is not set.");

        if(secret.isEmpty())
            throw new IllegalStateException("wallet secret is not exists");

        return new PaymentResult(true, "Paid " + amount + " using crypto wallet ***" + walletNumber.substring(walletNumber.length() - 4));
    }
}
