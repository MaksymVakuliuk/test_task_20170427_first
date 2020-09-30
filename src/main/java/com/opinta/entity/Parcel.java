package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcel {
    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    @OneToMany
    private List<ParcelItem> parcelItems;
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice, List<ParcelItem> parcelItems) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.parcelItems = parcelItems;
    }
}
