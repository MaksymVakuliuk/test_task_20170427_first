package com.opinta.dto;

import java.math.BigDecimal;

import com.opinta.constraint.EnumString;
import com.opinta.entity.DeliveryType;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentDto {
    private long id;
    private long senderId;
    private long recipientId;
    @EnumString(source = DeliveryType.class)
    private DeliveryType deliveryType;
    private BigDecimal price;
    private BigDecimal postPay;
    @Size(max = 255)
    private String description;
    private long[] parcelIds;
}
