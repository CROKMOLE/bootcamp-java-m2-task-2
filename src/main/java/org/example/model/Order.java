package org.example.model;

import org.example.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Discount discount;

    public Order(Builder builder) {
        this.customerName = builder.customerName;
        this.items = builder.items;
        this.status = OrderStatus.NEW;

        if (builder.discount.equals(DiscountTypeEnum.NONE))
            discount = new NoDiscount();
        if (builder.discount.equals(DiscountTypeEnum.PERCENTAGE))
            discount = new PercentageDiscount("common", 0.20);
        if (builder.discount.equals(DiscountTypeEnum.STATIC))
            discount = new FixedAmountDiscount();

        discount = switch (builder.discount) {
            case PERCENTAGE -> new PercentageDiscount("common", 0.20);
            case STATIC -> new FixedAmountDiscount();
            default -> new NoDiscount();
        };

    }

    public void addItem(OrderItem item) {
        if (status.equals(OrderStatus.PAID)) {
            System.out.println("Current order is already paid.");
        } else {
            items.add(item);
        }
    }

    private double calculateNetPrice() {
        double priceTotal = 0;

        //ge prices from each item of order
        for (OrderItem item : items)
            priceTotal += item.calculateTotal();
        return priceTotal;
    }

    public double calculateTotal() {

        double netPrice = calculateNetPrice();
        //calculate tax based on total price
        double tax = getTax(netPrice);

        return discount.apply(netPrice) + tax;
    }

    public double getTax(double netPrice) {
        AppConfig config = AppConfig.getInstance();
        return netPrice * config.getTaxRate();
    }

    public double getTax() {
        return (getTax(calculateNetPrice()));
    }

    public void markAsPaid() {
        if (!items.isEmpty())
            this.status = OrderStatus.PAID;
        else
            System.out.println("the order is empty!");
    }

    public void applyDiscount(Discount discount) {
        this.discount = discount;
    }

    public boolean isPaid() {
        return this.status == OrderStatus.PAID;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String customerName;
        private DiscountTypeEnum discount;
        private List<OrderItem> items = new ArrayList<>();

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder discountType(DiscountTypeEnum discountType) {
            this.discount = discountType;
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public Order build() {
            if (this.customerName.isEmpty()) {
                throw new IllegalStateException("customer name shouldn't be empty.");
            }
            return new Order(this);
        }
    }
}
