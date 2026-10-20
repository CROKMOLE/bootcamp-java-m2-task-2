package org.example.payment;

import org.example.model.Order;
import org.example.model.PaymentResult;

public class PaymentProcessor {
    public PaymentResult process(Order order, PaymentMethod paymentMethod){
        if(order.getItems().isEmpty())
            return new PaymentResult(false, "The order is empty.");

        if(order.isPaid())
            return new PaymentResult(false, "The order is already paid.");


        PaymentResult result = paymentMethod.pay(order.calculateTotal());

        if(result.isSuccessful()){
            order.markAsPaid();
        }

        return result;
    }
}
