package com.intuit.ordermanagementservice.enitities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders_item")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String name;
    private Double mrp;
    private Double discountedPrice;
    private Date expectedDeliveryDate;
    //private String metadata;

    @ManyToOne
    @JoinColumn(name = "orders_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order orders;


}
