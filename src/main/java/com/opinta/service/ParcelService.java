package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;

import java.util.List;

public interface ParcelService {
    Parcel saveEntity(Parcel parcel);

    ParcelDto save(ParcelDto parcelDto);

    List<Parcel> getAllEntities();

    List<ParcelDto> getAll();

    Parcel getEntityById(long id);

    ParcelDto getById(long id);
    
    ParcelDto update(long id, ParcelDto parcelDto);
    
    boolean delete(long id);
}
