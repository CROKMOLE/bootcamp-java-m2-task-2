package org.example.menu;

import org.example.config.AppConfig;
import org.example.model.*;
import org.example.payment.PaymentMethod;
import org.example.payment.PaymentMethodFactory;
import org.example.payment.PaymentProcessor;

import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final PaymentProcessor paymentProcessor = new PaymentProcessor();

    private DiscountTypeEnum discountType = DiscountTypeEnum.NONE;

    private Order currentOrder;

    public void start() {
        AppConfig config = AppConfig.getInstance();
        System.out.println("Welcome to " + config.getApplicationName());

        boolean running = true;
        while (running) {
            printMenu();

            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> createOrder();
                case 2 -> addItem();
                case 3 -> viewOrder();
                case 4 -> payOrder();
                case 5 -> getDiscount();
                case 0 -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void createOrder() {
        String customerName = inputFieldWithValidation("Customer name:");
        if (customerName.isEmpty()) {
            System.out.println("please enter valid customer name");
        } else {
            currentOrder = Order.builder()
                    .customerName(customerName)
                    .discountType(discountType)
                    .build();
            System.out.println("Order created for " + customerName);
        }
    }

    private void addItem() {
        if (currentOrder == null) {
            this.currentOrderNotExists();
            return;
        }


        String itemName = inputFieldWithValidation("Item name:");


        double price = Double.parseDouble(inputFieldWithValidation("Price:"));

        int quantity = Integer.parseInt(inputFieldWithValidation("Quantity:"));

        currentOrder.addItem(new OrderItem(itemName, price, quantity));


        System.out.println("Item added to order");
    }

    private void getDiscount() {
        String discount = inputFieldWithValidation("""
                Select discount method:
                1. None
                2. Fixed amount discount
                3. Percentage discount
                """);

        switch (discount) {
            case "1" -> discountType = DiscountTypeEnum.NONE;
            case "2" -> discountType = DiscountTypeEnum.STATIC;
            case "3" -> discountType = DiscountTypeEnum.PERCENTAGE;
            default -> System.out.println("The selected option is not exist.");
        }


    }

    private void viewOrder() {
        if (currentOrder == null) {
            this.currentOrderNotExists();
            return;
        }

        System.out.println("Customer: " + currentOrder.getCustomerName());
        System.out.println("Status: " + currentOrder.getStatus());
        System.out.println("Items:");

        for (OrderItem item : currentOrder.getItems()) {
            System.out.println("- " + item);
        }

        System.out.println("Tax: " + currentOrder.getTax());
        System.out.println("Total: " + currentOrder.calculateTotal());
    }

    private void payOrder() {
        if (currentOrder == null) {
            this.currentOrderNotExists();
            return;
        }

        System.out.println("""
                Select payment method:
                1. Credit Card
                2. PayPal
                3. Gift Card
                4. Crypto wallet
                """);
        int option = Integer.parseInt(scanner.nextLine());

        PaymentMethod paymentMethod = switch (option) {
            case 1 -> createCreditCardPayment();
            case 2 -> createPaypalPayment();
            case 3 -> createGiftCardPayment();
            case 4 -> createCryptoPayment();
            default -> throw new IllegalArgumentException("Invalid payment method");
        };

        PaymentResult result = paymentProcessor.process(currentOrder, paymentMethod);
        System.out.println(result.getMessage());
    }

    private PaymentMethod createCreditCardPayment() {
        String cardNumber = inputFieldWithValidation("Card number:");

        String cardHolderName = inputFieldWithValidation("Card holder name:");

        return PaymentMethodFactory.createCreditCardPayment(cardNumber, cardHolderName);
    }

    private PaymentMethod createPaypalPayment() {
        String email = inputFieldWithValidation("Card number:");

        return PaymentMethodFactory.createPayPalPayment(email);
    }

    private PaymentMethod createGiftCardPayment() {
        String giftCode = inputFieldWithValidation("Gift card code:");

        String giftAmountRaw = inputFieldWithValidation("Gift card amount:");

        //transform the card amount to double from string
        double giftAmount = Double.parseDouble(giftAmountRaw);

        return PaymentMethodFactory.createGiftCardPayment(giftCode, giftAmount);
    }

    private PaymentMethod createCryptoPayment() {
        String walletNumber = inputFieldWithValidation("Crypto wallet number:");
        String secret = inputFieldWithValidation("Crypto wallet secret:");

        return PaymentMethodFactory.createCryptoPayment(walletNumber, secret);

    }


    private void printMenu() {
        System.out.println("""
                1. Create order
                2. Add item to order
                3. View order
                4. Pay order
                5. Set discount type
                0. Exit
                """);
    }

    private void currentOrderNotExists() {
        System.out.println("Please create order before any actions with it.");
    }

    private String inputFieldWithValidation(String label) {
        String userInput = "";
        boolean isNotFilled = true;
        while (isNotFilled) {
            System.out.println(label);
            userInput = scanner.nextLine();

            if (userInput.isEmpty()) {
                System.out.println("That field should not be empty.");
                continue;
            }

            isNotFilled = false;
        }
        return userInput;
    }
}
