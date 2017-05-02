package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.constants.InvoiceStatusEnum;
import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.DriverDTO;
import com.itechart.warehouse.dto.GoodsInvoiceDTO;
import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.InvoiceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
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
    private GoodsDAO goodsDAO;
    private StorageSpaceTypeDAO storageDAO;
    private WarehouseCustomerCompanyDAO customerDAO;
    private TransportCompanyDAO transportDAO;

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
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
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

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAllInvoices() throws DataAccessException {
        logger.info("Find all invoices");

        List<Invoice> invoices;
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(Invoice.class);
            invoices = invoiceDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error while finding all invoices: ", e);
            throw new DataAccessException(e);
        }

        return invoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomingInvoiceDTO> findAllIncomingInvoices() throws DataAccessException {
        logger.info("Find all incoming invoices");

        List<IncomingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatus.class);
            List<InvoiceStatus> invoices = invoiceStatusDAO.findAll(criteria, -1, -1);

            List<InvoiceStatus> incomingInvoices = parseIncomingInvoices(invoices);

            for (InvoiceStatus invoice : incomingInvoices) {
                DetachedCriteria goodsCriteria = DetachedCriteria.forClass(Goods.class);
                goodsCriteria.add(Restrictions.eq("id_incoming_invoice", invoice.getId()));
                List<Goods> goodsForInvoice = goodsDAO.findAll(goodsCriteria, -1, -1);

                IncomingInvoiceDTO dto = convertToIncomingDTO(invoice, goodsForInvoice);
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
    public List<OutgoingInvoiceDTO> findAllOutgoingInvoices() throws DataAccessException {
        logger.info("Find all outgoing invoices");

        List<OutgoingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatus.class);
            List<InvoiceStatus> invoices = invoiceStatusDAO.findAll(criteria, -1, -1);

            List<InvoiceStatus> incomingInvoices = parseOutgoingInvoices(invoices);

            for (InvoiceStatus invoice : incomingInvoices) {
                DetachedCriteria goodsCriteria = DetachedCriteria.forClass(Goods.class);
                goodsCriteria.add(Restrictions.eq("id_outgoing_invoice", invoice.getId()));
                List<Goods> goodsForInvoice = goodsDAO.findAll(goodsCriteria, -1, -1);

                OutgoingInvoiceDTO dto = convertToOutgoingDTO(invoice, goodsForInvoice);
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
    public Invoice saveIncomingInvoice(IncomingInvoiceDTO dto) throws DataAccessException {
        logger.info("Save incoming invoice: {}", dto);

        Invoice savedInvoice;
        try {
            Invoice invoice = convertToIncomingInvoice(dto);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForIncomingInvoice(invoice, dto);
            invoiceStatusDAO.insert(invoiceStatus);

            // todo for each goods set incoming invoice
        } catch (GenericDAOException e) {
            logger.error("Error while saving incoming invoice: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    public Invoice saveOutgoingInvoice(OutgoingInvoiceDTO dto) throws DataAccessException {
        logger.info("Save outgoing invoice: {}", dto);

        Invoice savedInvoice;
        try {
            Invoice invoice = convertToOutgoingInvoice(dto);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForOutgoingInvoice(invoice, dto);
            invoiceStatusDAO.insert(invoiceStatus);

            // todo for each goods set outgoing invoice
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
    public void deleteInvoice(Invoice invoice) throws DataAccessException {
        logger.info("Delete invoice by id #{}", invoice.getId());

        try {
            invoiceDAO.delete(invoice);
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

        // todo retrieve all goods
        dto = fillOutgoingInvoiceWithGoodsInfo(dto, goodsList);


        return dto;
    }

    private Invoice convertToIncomingInvoice(IncomingInvoiceDTO dto) throws GenericDAOException {
        Invoice invoice = new Invoice();
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany supplierCompany = retrieveCustomerByName(dto.getSupplierCompany());
        invoice.setSupplierCompany(supplierCompany);
        TransportCompany transportCompany = retrieveTransportCompanyByName(dto.getTransportCompany());
        invoice.setTransportCompany(transportCompany);

        // todo set warehouse company (retrieve for current user)
        // todo set units

        // todo check driver for existence
        invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());

        invoice = fillIncomingInvoiceWithGoodsInfo(invoice, dto.getGoods());

        return invoice;
    }

    private Invoice convertToOutgoingInvoice(OutgoingInvoiceDTO dto) throws GenericDAOException {
        Invoice invoice = new Invoice();
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany receiverCompany = retrieveCustomerByName(dto.getReceiverCompany());
        invoice.setSupplierCompany(receiverCompany);
        TransportCompany transportCompany = retrieveTransportCompanyByName(dto.getTransportCompany());
        invoice.setTransportCompany(transportCompany);

        // todo set warehouse company (retrieve for current user)
        // todo set units

        // todo check driver for existence
        invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());

        // todo edit logic for outgoing goods
        invoice = fillOutgoingInvoiceWithGoodsInfo(invoice, dto.getGoods());

        return invoice;
    }

    private InvoiceStatus createStatusForIncomingInvoice(Invoice invoice, IncomingInvoiceDTO dto)
            throws GenericDAOException {
        return createStatusForInvoice(invoice, dto.getRegistrationDate(), dto.getStatus());
    }

    private InvoiceStatus createStatusForOutgoingInvoice(Invoice invoice, OutgoingInvoiceDTO dto)
            throws GenericDAOException{
        return createStatusForInvoice(invoice, dto.getRegistrationDate(), dto.getStatus());
    }

    private InvoiceStatus createStatusForInvoice(Invoice invoice, Timestamp registrationDate, String statusName)
            throws GenericDAOException {
        InvoiceStatus status = new InvoiceStatus();
        status.setInvoice(invoice);
        status.setDate(registrationDate);

        InvoiceStatusName invoiceStatusName = retrieveStatusByName(statusName);
        status.setStatusName(invoiceStatusName);

        //todo retrieve user

        return status;
    }

    private IncomingInvoiceDTO fillIncomingInvoiceWithDriverInfo(IncomingInvoiceDTO invoice, Driver driver) {
        invoice.setDriver(mapDriver(driver));
        return invoice;
    }

    private OutgoingInvoiceDTO fillOutgoingInvoiceWithDriverInfo(OutgoingInvoiceDTO invoice, Driver driver) {
        invoice.setDriver(mapDriver(driver));
        return invoice;
    }

    private Invoice fillInvoiceWithDriverInfo(Invoice invoice, DriverDTO driverDTO) {
        invoice.setDriver(mapDriver(driverDTO));
        return invoice;
    }

    private DriverDTO mapDriver(Driver driver) {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setFullName(driver.getFullName());
        driverDTO.setCountryCode(driver.getCountryCode());
        driverDTO.setIssueDate(driver.getIssueDate());
        driverDTO.setPassportNumber(driver.getPassportNumber());

        return driverDTO;
    }

    private Driver mapDriver(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setFullName(dto.getFullName());
        driver.setCountryCode(dto.getCountryCode());
        driver.setIssueDate(dto.getIssueDate());
        driver.setPassportNumber(dto.getPassportNumber());

        return driver;
    }

    private IncomingInvoiceDTO fillIncomingInvoiceWithGoodsInfo(IncomingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private OutgoingInvoiceDTO fillOutgoingInvoiceWithGoodsInfo(OutgoingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private Invoice fillIncomingInvoiceWithGoodsInfo(Invoice invoice, List<GoodsInvoiceDTO> goodsList) throws GenericDAOException {
        invoice.setIncomingGoods(mapToGoods(goodsList));
        return invoice;
    }

    private Invoice fillOutgoingInvoiceWithGoodsInfo(Invoice invoice, List<GoodsInvoiceDTO> goodsList) throws GenericDAOException {
        invoice.setOutgoingGoods(mapToGoods(goodsList));
        return invoice;
    }

    private List<Goods> mapToGoods(List<GoodsInvoiceDTO> goodsList) throws GenericDAOException {
        List<Goods> goodsForInvoice = new ArrayList<>();
        for (GoodsInvoiceDTO goods : goodsList) {
            Goods goodsToSave = new Goods();
            goodsToSave.setName(goods.getName());
            goodsToSave.setWeight(goods.getWeight());
            goodsToSave.setQuantity(goods.getQuantity());
            goodsToSave.setPrice(goods.getPrice());

            StorageSpaceType storageType = retrieveStorageSpaceTypeByName(goods.getStorageTypeName());
            goodsToSave.setStorageType(storageType);

            // todo set units

            goodsForInvoice.add(goodsToSave);
        }

        return goodsForInvoice;
    }

    private List<GoodsInvoiceDTO> mapToDTOs(List<Goods> goodsList) {
        List<GoodsInvoiceDTO> goodsInvoiceDTOs = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsInvoiceDTO goodsDTO = new GoodsInvoiceDTO();
            goodsDTO.setName(goods.getName());
            goodsDTO.setQuantity(goods.getQuantity());
            goodsDTO.setPrice(goods.getPrice());
            goodsDTO.setWeight(goods.getWeight());
            goodsDTO.setStorageTypeName(goods.getStorageType().getName());

            goodsInvoiceDTOs.add(goodsDTO);
        }

        return goodsInvoiceDTOs;
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
}
