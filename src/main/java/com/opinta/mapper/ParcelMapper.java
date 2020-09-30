package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.service.ParcelItemService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = ParcelItemService.class)
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {
    @Override
    @Mappings({@Mapping(target = "parcelItemIds", expression = "java(parcel.getParcelItems().stream().mapToLong(pI -> pI.getId()).toArray())"),
            @Mapping(target = "shipmentId", expression = "java(parcel.getShipment().getId())")})
    ParcelDto toDto(Parcel parcel);

    @Override
    @Mappings({@Mapping(target = "parcel.parcelItems", expression = "java(createParcelItems(parcelDto))"),
            @Mapping(target = "parcel.shipment", expression = "java(createShipmentById(parcelDto.getShipmentId()))")})
    Parcel toEntity(ParcelDto parcelDto);

    default Shipment createShipmentById(long id) {
        Shipment shipment = new Shipment();
        shipment.setId(id);
        return shipment;
    }

    default List<ParcelItem> createParcelItems (ParcelDto parcelDto) {
        List<ParcelItem> parcelItems = new ArrayList<>();
        for (long id : parcelDto.getParcelItemIds()) {
            parcelItems.add(createParcelItemById(id));
        }
        return parcelItems;
    }

    default ParcelItem createParcelItemById(long id) {
        ParcelItem parcelItem = new ParcelItem();
        parcelItem.setId(id);
        return parcelItem;
    }
}
