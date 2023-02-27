package com.example.software.data.entity;

import com.example.software.data.entity.enums.Availability;
import com.example.software.data.entity.enums.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Invoice extends AbstractEntity implements Serializable {

    private String name;

    private String customer;

    //private Customer customer;



    private String product;
    private String description;

//    @ManyToMany(cascade = {
//            CascadeType.PERSIST,
//            CascadeType.MERGE
//    })
//    @JoinTable(name = "invoice_product",
//            joinColumns = @JoinColumn(name = "invoice_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    private List<Product> products = new ArrayList<>();

    private float price;

    private int amount;
    private Currency currency;

    private Boolean isOrderCompleted;

    private int total;
    public Boolean getOrderCompleted() {
        return isOrderCompleted;
    }
    public void setOrderCompleted(Boolean orderCompleted) {
        isOrderCompleted = orderCompleted;
    }
    @NotNull
    private Availability availability = Availability.COMING;

    @Override
    public String toString() {
        return "Invoice{" +
                "product='" + product + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", currency=" + currency +
                ", isOrderCompleted=" + isOrderCompleted +
                ", total=" + total +
                ", availability=" + availability +
                '}';
    }

    @Override
    public Invoice clone() {
        try {
            return (Invoice) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(
                    "The Invoices object could not be cloned.", e);
        }
    }

    public Invoice(String product, String description, float price, int amount, Currency currency, Boolean isOrderCompleted, int total, Availability availability) {
        this.product = product;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.currency = currency;
        this.isOrderCompleted = isOrderCompleted;
        this.total = total;
        this.availability = availability;
    }
}
