package com.opinta.mapper;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Client;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(source = "sender.id", target = "senderId"),
            @Mapping(source = "recipient.id", target = "recipientId"),
            @Mapping(target = "parcelIds", expression = "java(shipment.getParcels().stream().mapToLong(p -> p.getId()).toArray())")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderId()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientId()))"),
            @Mapping(target = "parcels", expression = "java(createParcels(shipmentDto))")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(long id) {
        Client client = new Client();
        client.setId(id);
        return client;
    }

    default List<Parcel> createParcels (ShipmentDto shipmentDto) {
        List<Parcel> parcel = new ArrayList<>();

        for (long id : shipmentDto.getParcelIds()) {
            parcel.add(createParcelById(id));
        }
        return parcel;
    }

    default Parcel createParcelById(long id) {
        Parcel parcel = new Parcel();
        parcel.setId(id);
        return parcel;
    }
}
