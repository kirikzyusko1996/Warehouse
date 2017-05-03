package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.constants.InvoiceStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.*;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final static Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private InvoiceDAO invoiceDAO;
    private InvoiceStatusDAO invoiceStatusDAO;
    private InvoiceStatusNameDAO invoiceStatusNameDAO;
    private UnitDAO unitDAO;
    private StorageSpaceTypeDAO storageDAO;
    private WarehouseCustomerCompanyDAO customerDAO;
    private TransportCompanyDAO transportDAO;
    private DriverDAO driverDAO;
    private GoodsService goodsService;
    private WarehouseCustomerCompanyService customerService;
    private TransportCompanyService transportService;
    private UserService userService;

    @Autowired
    public void setInvoiceDAO(InvoiceDAO invoiceDao) {
        this.invoiceDAO = invoiceDao;
    }

    @Autowired
    public void setInvoiceStatusDAO(InvoiceStatusDAO invoiceStatusDAO) {
        this.invoiceStatusDAO = invoiceStatusDAO;
    }

    @Autowired
    public void setInvoiceStatusNameDAO(InvoiceStatusNameDAO dao) {
        this.invoiceStatusNameDAO = dao;
    }

    @Autowired
    public void setUnitDAO(UnitDAO dao) {
        this.unitDAO = dao;
    }

    @Autowired
    public void setStorageDAO(StorageSpaceTypeDAO dao) {
        this.storageDAO = dao;
    }

    @Autowired
    public void setCustomerDAO(WarehouseCustomerCompanyDAO dao) {
        this.customerDAO = dao;
    }

    @Autowired
    public void setTransportDAO(TransportCompanyDAO dao) {
        this.transportDAO = dao;
    }

    @Autowired
    public void setDriverDAO(DriverDAO dao) {
        this.driverDAO = dao;
    }

    @Autowired
    public void setGoodsService(GoodsService service) {
        this.goodsService = service;
    }

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service) {
        this.customerService = service;
    }

    @Autowired
    public void setTransportService(TransportCompanyService service) {
        this.transportService = service;
    }

    @Autowired
    public void setUserService(UserService service){
        this.userService = service;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAllInvoices() throws DataAccessException {
        logger.info("Find all invoices");

        List<Invoice> invoices;
        try {
            invoices = retrieveAllInvoices();
        } catch (GenericDAOException e) {
            logger.error("Error while finding all invoices: ", e);
            throw new DataAccessException(e);
        }

        return invoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomingInvoiceDTO> findAllIncomingInvoices()
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find all incoming invoices");

        List<IncomingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<InvoiceStatus> invoices = retrieveAllInvoicesStatuses();
            List<InvoiceStatus> incomingInvoices = parseIncomingInvoices(invoices);

            for (InvoiceStatus invoiceStatus : incomingInvoices) {
                Invoice invoice = invoiceStatus.getInvoice();

                //todo specify firstResult, maxResult
                List<Goods> goodsForInvoice = goodsService.findGoodsForInvoice(invoice.getId(), -1, -1);

                IncomingInvoiceDTO dto = convertToIncomingDTO(invoiceStatus, goodsForInvoice);
                invoiceDTOs.add(dto);
            }

        } catch (GenericDAOException e) {
            logger.error("Error while finding all incoming invoices: ", e);
            throw new DataAccessException(e);
        }

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingInvoiceDTO> findAllOutgoingInvoices()
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find all outgoing invoices");

        List<OutgoingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<InvoiceStatus> invoices = retrieveAllInvoicesStatuses();
            List<InvoiceStatus> incomingInvoices = parseOutgoingInvoices(invoices);

            for (InvoiceStatus invoiceStatus : incomingInvoices) {
                Invoice invoice = invoiceStatus.getInvoice();

                //todo specify firstResult, maxResult
                List<Goods> goodsForInvoice = goodsService.findGoodsForInvoice(invoice.getId(), -1, -1);

                OutgoingInvoiceDTO dto = convertToOutgoingDTO(invoiceStatus, goodsForInvoice);
                invoiceDTOs.add(dto);
            }

        } catch (GenericDAOException e) {
            logger.error("Error while finding all outgoing invoices: ", e);
            throw new DataAccessException(e);
        }

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findInvoiceById(Long id) throws DataAccessException {
        logger.info("Find invoice by id #{}", id);

        Invoice invoice = null;
        try {
            Optional<Invoice> optional = invoiceDAO.findById(id);
            if (optional.isPresent()) {
                invoice = optional.get();
            }
        } catch (GenericDAOException e) {
            logger.error("Error while finding invoice by id: ", e);
            throw new DataAccessException(e);
        }

        return invoice;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findInvoiceByNumber(String number) throws DataAccessException {
        logger.info("Find invoice by number {}", number);

        Invoice invoice = null;

        if (StringUtils.isNotEmpty(number)) {
            try {
                DetachedCriteria criteria = DetachedCriteria.forClass(Invoice.class);
                criteria.add(Restrictions.eq("number", number));

                List<Invoice> invoices = invoiceDAO.findAll(criteria, -1, -1);
                if (CollectionUtils.isNotEmpty(invoices)) {
                    invoice = invoices.get(0);
                }
            } catch (GenericDAOException e) {
                logger.error("Error while finding invoice by number: ", e);
                throw new DataAccessException(e);
            }
        }

        return invoice;
    }

    @Override
    @Transactional
    public Invoice saveInvoice(Invoice invoice) throws DataAccessException {
        logger.info("Save invoice: {}", invoice);

        Invoice savedInvoice;
        try {
            savedInvoice = invoiceDAO.insert(invoice);
        } catch (GenericDAOException e) {
            logger.error("Error while saving invoice: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    public Invoice saveIncomingInvoice(WarehouseCompanyUserDetails principal, IncomingInvoiceDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Save incoming invoice: {}", dto);

        Invoice savedInvoice;
        try {
            User currentUser = userService.findUserById(principal.getUserId());
            WarehouseCompany currentCompany = principal.getCompany();

            Invoice invoice = convertToIncomingInvoice(dto, currentCompany);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForIncomingInvoice(invoice, dto, currentUser);
            invoiceStatusDAO.insert(invoiceStatus);

            List<GoodsDTO> goodsList = dto.getGoods();
            goodsService.createGoodsBatch(savedInvoice.getId(), goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error while saving incoming invoice: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    public Invoice saveOutgoingInvoice(WarehouseCompanyUserDetails principal, OutgoingInvoiceDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Save outgoing invoice: {}", dto);

        Invoice savedInvoice;
        try {
            User currentUser = userService.findUserById(principal.getUserId());
            WarehouseCompany currentCompany = principal.getCompany();

            Invoice invoice = convertToOutgoingInvoice(dto, currentCompany);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForOutgoingInvoice(invoice, dto, currentUser);
            invoiceStatusDAO.insert(invoiceStatus);

            // todo add logic for outgoing goods
        } catch (GenericDAOException e) {
            logger.error("Error while saving invoice: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    public Invoice updateInvoice(Invoice invoice) throws DataAccessException {
        logger.info("Update invoice: {}", invoice);

        Invoice updatedInvoice;
        try {
            updatedInvoice = invoiceDAO.update(invoice);
        } catch (GenericDAOException e) {
            logger.error("Error while updating invoice: ", e);
            throw new DataAccessException(e);
        }

        return updatedInvoice;
    }

    @Override
    @Transactional
    public InvoiceStatus updateInvoiceStatus(String id, String status)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update invoice's with id {} status", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        if (StringUtils.isEmpty(status)) {
            throw new IllegalParametersException("Invalid status value");
        }

        InvoiceStatus updatedInvoice;
        try {
            Long invoiceId = Long.valueOf(id);
            Optional<InvoiceStatus> optional = invoiceStatusDAO.findById(invoiceId);
            if (optional.isPresent()) {
                InvoiceStatus invoiceStatus = optional.get();

                InvoiceStatusName statusName = retrieveStatusByName(status);
                invoiceStatus.setStatusName(statusName);

                updatedInvoice = invoiceStatusDAO.update(invoiceStatus);
            } else {
                logger.error("Invoice with id {} not found", invoiceId);
                throw new ResourceNotFoundException("Invoice not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating invoice' status: ", e);
            throw new DataAccessException(e);
        }

        return updatedInvoice;
    }

    @Override
    @Transactional
    public void deleteInvoice(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete invoice by id #{}", id);

        if (!NumberUtils.isNumber(id)) {
            throw new IllegalParametersException("Invalid id param");
        }

        try {
            Long invoiceId = Long.valueOf(id);
            Optional<Invoice> optionalInvoice = invoiceDAO.findById(invoiceId);
            if (optionalInvoice.isPresent()) {
                Invoice invoice = optionalInvoice.get();
                invoiceDAO.delete(invoice);
            } else {
                logger.error("Invoice with id {} not found", invoiceId);
                throw new ResourceNotFoundException("Invoice not found");
            }

            Optional<InvoiceStatus> optionalInvoiceStatus = invoiceStatusDAO.findById(invoiceId);
            if (optionalInvoiceStatus.isPresent()) {
                InvoiceStatus status = optionalInvoiceStatus.get();
                invoiceStatusDAO.delete(status);
            } else {
                logger.error("Invoice status with id {} not found", invoiceId);
                throw new ResourceNotFoundException("Invoice status not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting invoice: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean invoiceExists(Invoice invoice) throws DataAccessException {
        logger.error("Determine if invoice #{} exists", invoice.getId());

        try {
            return invoiceDAO.isExistsEntity(invoice.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if invoice exists", e);
            throw new DataAccessException(e);
        }
    }

    private List<Invoice> retrieveAllInvoices() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Invoice.class);
        return invoiceDAO.findAll(criteria, -1, -1);
    }

    private List<InvoiceStatus> retrieveAllInvoicesStatuses() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatus.class);
        return invoiceStatusDAO.findAll(criteria, -1, -1);
    }

    private StorageSpaceType retrieveStorageSpaceTypeByName(String storageTypeName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);
        criteria.add(Restrictions.eq("name", storageTypeName));

        List<StorageSpaceType> types = storageDAO.findAll(criteria, -1, -1);
        return types.get(0);
    }

    private WarehouseCustomerCompany retrieveCustomerByName(String supplierName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(WarehouseCustomerCompany.class);
        criteria.add(Restrictions.eq("name", supplierName));

        List<WarehouseCustomerCompany> suppliers = customerDAO.findAll(criteria, -1, -1);
        return suppliers.get(0);
    }

    private TransportCompany retrieveTransportCompanyByName(String transportCompanyName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(TransportCompany.class);
        criteria.add(Restrictions.eq("name", transportCompanyName));

        List<TransportCompany> companies = transportDAO.findAll(criteria, -1, -1);
        return companies.get(0);
    }

    private InvoiceStatusName retrieveStatusByName(String statusName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatusName.class);
        criteria.add(Restrictions.eq("name", statusName));

        List<InvoiceStatusName> names = invoiceStatusNameDAO.findAll(criteria, -1, -1);
        return names.get(0);
    }

    private Unit retrieveUnitByName(String unitName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Unit.class);
        criteria.add(Restrictions.eq("name", unitName));

        List<Unit> units = unitDAO.findAll(criteria, -1, -1);
        return units.get(0);
    }

    private List<InvoiceStatus> parseIncomingInvoices(List<InvoiceStatus> invoices) {
        List<InvoiceStatus> incomingInvoices = new ArrayList<>();
        for (InvoiceStatus invoice : invoices) {
            String statusName = invoice.getStatusName().getName();
            if (statusName.equals(InvoiceStatusEnum.REGISTERED.getName())
                    || statusName.equals(InvoiceStatusEnum.CHECKED.getName())
                    || statusName.equals(InvoiceStatusEnum.COMPLETED.getName())) {
                incomingInvoices.add(invoice);
            }
        }

        return incomingInvoices;
    }

    private List<InvoiceStatus> parseOutgoingInvoices(List<InvoiceStatus> invoices) {
        List<InvoiceStatus> outgoingInvoices = new ArrayList<>();
        for (InvoiceStatus invoice : invoices) {
            String statusName = invoice.getStatusName().getName();
            if (statusName.equals(InvoiceStatusEnum.RELEASE_ALLOWED.getName())
                    || statusName.equals(InvoiceStatusEnum.MOVED_OUT.getName())) {
                outgoingInvoices.add(invoice);
            }
        }

        return outgoingInvoices;
    }

    private IncomingInvoiceDTO convertToIncomingDTO(InvoiceStatus invoiceStatus, List<Goods> goodsList) {
        IncomingInvoiceDTO dto = new IncomingInvoiceDTO();

        // todo maybe use model mapper

        Invoice invoice = invoiceStatus.getInvoice();
        dto.setId(invoice.getId());
        dto.setNumber(invoice.getNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setSupplierCompany(invoice.getSupplierCompany());
        dto.setTransportCompany(invoice.getTransportCompany());
        dto.setTransportNumber(invoice.getTransportNumber());
        dto.setTransportName(invoice.getTransportName());
        dto.setDescription(invoice.getDescription());
        dto.setGoodsQuantity(invoice.getGoodsQuantity());
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        dto.setDispatcher(invoiceStatus.getUser());
        dto.setRegistrationDate(invoiceStatus.getDate());

        dto = fillIncomingInvoiceWithDriverInfo(dto, invoice.getDriver());
        dto = fillIncomingInvoiceWithGoodsInfo(dto, goodsList);

        return dto;
    }

    private OutgoingInvoiceDTO convertToOutgoingDTO(InvoiceStatus invoiceStatus, List<Goods> goodsList) {
        OutgoingInvoiceDTO dto = new OutgoingInvoiceDTO();

        Invoice invoice = invoiceStatus.getInvoice();
        dto.setId(invoice.getId());
        dto.setNumber(invoice.getNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setReceiverCompany(invoice.getReceiverCompany());
        dto.setTransportCompany(invoice.getTransportCompany());
        dto.setTransportNumber(invoice.getTransportNumber());
        dto.setTransportName(invoice.getTransportName());
        dto.setDescription(invoice.getDescription());
        dto.setGoodsQuantity(invoice.getGoodsQuantity());
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        dto.setManager(invoiceStatus.getUser());
        dto.setRegistrationDate(invoiceStatus.getDate());

        dto = fillOutgoingInvoiceWithDriverInfo(dto, invoice.getDriver());
        dto = fillOutgoingInvoiceWithGoodsInfo(dto, goodsList);


        return dto;
    }

    private Invoice convertToIncomingInvoice(IncomingInvoiceDTO dto, WarehouseCompany company) throws GenericDAOException, DataAccessException {
        Invoice invoice = new Invoice();
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

//        WarehouseCustomerCompany supplierCompany = retrieveCustomerByName(dto.getSupplierCompany());
        WarehouseCustomerCompany supplierCompany =
                customerService.findWarehouseCustomerCompanyByName(dto.getSupplierCompany());
        invoice.setSupplierCompany(supplierCompany);
//        TransportCompany transportCompany = retrieveTransportCompanyByName(dto.getTransportCompany());
        TransportCompany transportCompany =
                transportService.findTransportCompanyByName(dto.getTransportName());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouseCompany(company);
        invoice = fillInvoiceWithUnitsInfo(invoice, dto.getGoodsQuantityUnit(), dto.getGoodsEntryCountUnit());
        invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());

        return invoice;
    }

    private Invoice convertToOutgoingInvoice(OutgoingInvoiceDTO dto, WarehouseCompany company)
            throws GenericDAOException, DataAccessException {
        Invoice invoice = new Invoice();
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

//        WarehouseCustomerCompany receiverCompany = retrieveCustomerByName(dto.getReceiverCompany());
        WarehouseCustomerCompany receiverCompany =
                customerService.findWarehouseCustomerCompanyByName(dto.getReceiverCompany());
        invoice.setSupplierCompany(receiverCompany);
//        TransportCompany transportCompany = retrieveTransportCompanyByName(dto.getTransportCompany());
        TransportCompany transportCompany =
                transportService.findTransportCompanyByName(dto.getTransportName());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouseCompany(company);
        invoice = fillInvoiceWithUnitsInfo(invoice, dto.getGoodsQuantityUnit(), dto.getGoodsEntryCountUnit());
        invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());

        return invoice;
    }

    private InvoiceStatus createStatusForIncomingInvoice(Invoice invoice, IncomingInvoiceDTO dto, User user)
            throws GenericDAOException {
        return fillStatusWithInfo(invoice, dto.getRegistrationDate(), dto.getStatus(), user);
    }

    private InvoiceStatus createStatusForOutgoingInvoice(Invoice invoice, OutgoingInvoiceDTO dto, User user)
            throws GenericDAOException {
        return fillStatusWithInfo(invoice, dto.getRegistrationDate(), dto.getStatus(), user);
    }

    private InvoiceStatus fillStatusWithInfo(Invoice invoice, Timestamp registrationDate, String status, User user)
            throws GenericDAOException{
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setId(invoice.getId());
        invoiceStatus.setInvoice(invoice);
        invoiceStatus.setDate(registrationDate);

        InvoiceStatusName invoiceStatusName = retrieveStatusByName(status);
        invoiceStatus.setStatusName(invoiceStatusName);

        invoiceStatus.setUser(user);

        return invoiceStatus;
    }

    private IncomingInvoiceDTO fillIncomingInvoiceWithDriverInfo(IncomingInvoiceDTO invoice, Driver driver) {
        invoice.setDriver(mapDriver(driver));
        return invoice;
    }

    private OutgoingInvoiceDTO fillOutgoingInvoiceWithDriverInfo(OutgoingInvoiceDTO invoice, Driver driver) {
        invoice.setDriver(mapDriver(driver));
        return invoice;
    }

    private Invoice fillInvoiceWithUnitsInfo(Invoice invoice, String quantityUnitName, String entryCountUnitName)
            throws GenericDAOException {
        Unit goodsQuantityUnit = retrieveUnitByName(quantityUnitName);
        invoice.setGoodsQuantityUnit(goodsQuantityUnit);
        Unit goodsEntryCountUnit = retrieveUnitByName(entryCountUnitName);
        invoice.setGoodsEntryCountUnit(goodsEntryCountUnit);

        return invoice;
    }

    private Invoice fillInvoiceWithDriverInfo(Invoice invoice, DriverDTO dto) throws GenericDAOException {
        String driverId = dto.getId();
        if (StringUtils.isNotEmpty(driverId)) {
            Long validDriverId = Long.valueOf(driverId);
            Optional<Driver> optional = driverDAO.findById(validDriverId);
            if (optional.isPresent()) {
                Driver driver = optional.get();
                invoice.setDriver(driver);
            } else {
                Driver driver = parseDriverFromDto(dto);
                driver = driverDAO.insert(driver);
                invoice.setDriver(driver);
            }
        } else {
            Driver driver = parseDriverFromDto(dto);
            driver = driverDAO.insert(driver);
            invoice.setDriver(driver);
        }

        return invoice;
    }

    private Driver parseDriverFromDto(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setFullName(dto.getFullName());
        driver.setCountryCode(dto.getCountryCode());
        driver.setIssueDate(dto.getIssueDate());
        driver.setPassportNumber(dto.getPassportNumber());

        return driver;
    }

    private DriverDTO mapDriver(Driver driver) {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setId(String.valueOf(driver.getId()));
        driverDTO.setFullName(driver.getFullName());
        driverDTO.setCountryCode(driver.getCountryCode());
        driverDTO.setIssueDate(driver.getIssueDate());
        driverDTO.setPassportNumber(driver.getPassportNumber());

        return driverDTO;
    }

    private IncomingInvoiceDTO fillIncomingInvoiceWithGoodsInfo(IncomingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private OutgoingInvoiceDTO fillOutgoingInvoiceWithGoodsInfo(OutgoingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private List<GoodsDTO> mapToDTOs(List<Goods> goodsList) {
        List<GoodsDTO> goodsInvoiceDTOs = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setName(goods.getName());
            goodsDTO.setQuantity(goods.getQuantity());
            goodsDTO.setPrice(goods.getPrice());
            goodsDTO.setWeight(goods.getWeight());

            goodsDTO.setStorageTypeName(goods.getStorageType().getName());
            goodsDTO.setPriceUnitName(goods.getPriceUnit().getName());
            goodsDTO.setQuantityUnitName(goods.getQuantityUnit().getName());
            goodsDTO.setWeightUnitName(goods.getWeightUnit().getName());

            goodsInvoiceDTOs.add(goodsDTO);
        }

        return goodsInvoiceDTOs;
    }
}
