package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentDao shipmentDao;
    private final ClientDao clientDao;
    private final ShipmentMapper shipmentMapper;
    private final BarcodeInnerNumberService barcodeInnerNumberService;
    private final ParcelService parcelService;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientDao clientDao,
                               ShipmentMapper shipmentMapper, BarcodeInnerNumberService barcodeInnerNumberService, ParcelService parcelService) {
        this.shipmentDao = shipmentDao;
        this.clientDao = clientDao;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.parcelService= parcelService;
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntities() {
        log.info("Getting all shipments");
        return shipmentDao.getAll();
    }

    @Override
    @Transactional
    public Shipment getEntityById(long id) {
        log.info("Getting shipment by id {}", id);
        return shipmentDao.getById(id);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment) {
        log.info("Saving shipment {}", shipment);
        return shipmentDao.save(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll() {
        return shipmentMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByClientId(long clientId) {
        Client client = clientDao.getById(clientId);
        if (client == null) {
            log.debug("Can't get shipment list by client. Client {} doesn't exist", clientId);
            return null;
        }
        log.info("Getting all shipments by client {}", client);
        return shipmentMapper.toDto(shipmentDao.getAllByClient(client));
    }

    @Override
    @Transactional
    public ShipmentDto getById(long id) {
        return shipmentMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto) {
        Client existingClient = clientDao.getById(shipmentDto.getSenderId());
        Counterparty counterparty = existingClient.getCounterparty();
        PostcodePool postcodePool = counterparty.getPostcodePool();
        BarcodeInnerNumber newBarcode = barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool);
        postcodePool.getBarcodeInnerNumbers().add(newBarcode);
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        shipment.setBarcode(newBarcode);
        log.info("Saving shipment with assigned barcode", shipmentMapper.toDto(shipment));
        shipment.setSender(clientDao.getById(shipment.getSender().getId()));
        shipment.setRecipient(clientDao.getById(shipment.getRecipient().getId()));
        shipment.setPrice(calculatePrice(shipment));
        return shipmentMapper.toDto(shipmentDao.save(shipment));
    }

    @Override
    @Transactional
    public ShipmentDto update(long id, ShipmentDto shipmentDto) {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = shipmentDao.getById(id);
        if (target == null) {
            log.debug("Can't update shipment. Shipment doesn't exist {}", id);
            return null;
        }
        source.setPrice(calculatePrice(source));
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        target.setId(id);
        log.info("Updating shipment {}", target);
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Shipment shipment = shipmentDao.getById(id);
        if (shipment == null) {
            log.debug("Can't delete shipment. Shipment doesn't exist {}", id);
            return false;
        }
        shipment.setId(id);
        log.info("Deleting shipment {}", shipment);
        shipmentDao.delete(shipment);
        return true;
    }

    @Override
    public BigDecimal getDeclaredPrice(Shipment shipment) {
        BigDecimal declaredPrice = BigDecimal.ZERO;
        for (Parcel parcel : shipment.getParcels()) {
            declaredPrice.add(parcel.getDeclaredPrice());
        }
        return declaredPrice;
    }

    @Override
    public float getWeight(Shipment shipment) {
       float weight = 0.0f;
        for (Parcel parcel : shipment.getParcels()) {
            weight += parcel.getWeight();
        }
        return weight;
    }

    private BigDecimal calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);
        BigDecimal price = BigDecimal.ZERO;

        if (shipment.getParcels() == null) {
            return price;
        }

        for (Parcel parcel : shipment.getParcels()) {
           price.add(calculateParcelPrice(parcel, shipment));
        }

        return price;
    }

    private BigDecimal calculateParcelPrice(Parcel parcel, Shipment shipment) {
        ParcelDto parcelDtoById = parcelService.getById(parcel.getId());
        parcelDtoById.setShipmentId(shipment.getId());
        ParcelDto updatedParcelDto = parcelService.update(parcelDtoById.getId(), parcelDtoById);
        return updatedParcelDto.getPrice();
    }


}
