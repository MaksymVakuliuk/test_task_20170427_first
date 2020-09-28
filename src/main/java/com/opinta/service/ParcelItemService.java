package com.opinta.service;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;

import java.util.List;

public interface ParcelItemService {

    ParcelItem saveEntity(ParcelItem parcelItem);
    
    List<ParcelItemDto> getAll();

    ParcelItemDto getById(long id);
    
    ParcelItemDto save(ParcelItemDto parcelItemDto);
    
    ParcelItemDto update(long id, ParcelItemDto parcelItemDto);
    
    boolean delete(long id);
}
