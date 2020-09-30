package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.CounterpartyDao;
import com.opinta.dto.CounterpartyDto;
import com.opinta.mapper.CounterpartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class CounterpartyServiceImpl implements CounterpartyService {
    private final CounterpartyDao counterpartyDao;
    private final CounterpartyMapper counterpartyMapper;

    @Autowired
    public CounterpartyServiceImpl(CounterpartyDao counterpartyDao,
                                   CounterpartyMapper counterpartyMapper) {
        this.counterpartyDao = counterpartyDao;
        this.counterpartyMapper = counterpartyMapper;
    }

    @Override
    @Transactional
    public List<Counterparty> getAllEntities() {
        log.info("Getting all counterparties");
        return counterpartyDao.getAll();
    }

    @Override
    @Transactional
    public Counterparty getEntityById(long id) {
        log.info("Getting counterparty {}", id);
        return counterpartyDao.getById(id);
    }

    @Override
    @Transactional
    public List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool) {
        log.info("Getting counterparty by postcodePool {}", postcodePool);
        return counterpartyDao.getByPostcodePool(postcodePool);
    }

    @Override
    @Transactional
    public Counterparty saveEntity(Counterparty counterparty) {
        List<Counterparty> counterparties = getEntityByPostcodePool(counterparty.getPostcodePool());
        if (counterparties.size() != 0) {
            log.error("PostcodePool {} is already used in the VPO {}", counterparty.getPostcodePool(),
                    counterparties);
            return null;
        }
        log.info("Saving counterparty {}", counterparty);
        return counterpartyDao.save(counterparty);
    }

    @Override
    @Transactional
    public List<CounterpartyDto> getAll() {
        log.info("Getting all counterparties");
        List<Counterparty> counterparties =  counterpartyDao.getAll();
        return counterpartyMapper.toDto(counterparties);
    }

    @Override
    @Transactional
    public CounterpartyDto getById(long id) {
        log.info("Getting counterparty by id " + id);
        Counterparty counterparty = counterpartyDao.getById(id);
        return counterpartyMapper.toDto(counterparty);
    }

    @Override
    @Transactional
    public CounterpartyDto save(CounterpartyDto counterpartyDto) {
        log.info("Saving counterparty {}", counterpartyDto);
        Counterparty counterparty = counterpartyMapper.toEntity(counterpartyDto);
        return counterpartyMapper.toDto(saveEntity(counterparty));
    }

    @Override
    @Transactional
    public CounterpartyDto update(long id, CounterpartyDto counterpartyDto) {
        Counterparty source = counterpartyMapper.toEntity(counterpartyDto);
        Counterparty target = counterpartyDao.getById(id);
        if (target == null) {
            log.debug("Can't update counterparty. Counterparty doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for counterparty", e);
        }
        target.setId(id);
        log.info("Updating counterparty {}", target);
        counterpartyDao.update(target);
        return counterpartyMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Counterparty counterparty = counterpartyDao.getById(id);
        if (counterparty == null) {
            log.debug("Can't delete counterparty. Counterparty doesn't exist " + id);
            return false;
        }
        log.info("Deleting counterparty " + counterparty);
        counterpartyDao.delete(counterparty);
        return true;
    }
}
