package com.opinta.service;

import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService{
    private final ParcelItemDao parcelItemDao;
    private final ParcelItemMapper parcelItemMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelItemMapper = parcelItemMapper;
    }

    @Override
    @Transactional
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcelItem {}", parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAll() {
        log.info("Getting all parcelItemDtos");
        return parcelItemMapper.toDto(parcelItemDao.getAll());
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(parcelItemDao.getById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        ParcelItem parcelItem = parcelItemMapper.toEntity(parcelItemDto);
        return parcelItemMapper.toDto(parcelItemDao.save(parcelItem));
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null) {
            log.debug("Can't update productItem. ParcelItem doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for parcelItem", e);
        }
        target.setId(id);
        log.info("Updating parcelItem {}", target);
        parcelItemDao.update(target);
        return parcelItemMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem == null) {
            log.debug("Can't delete parcelItem. ParcelItem doesn't exist {}", id);
            return  false;
        }
        parcelItem.setId(id);
        log.info("Deleting parcelItem {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }
}
