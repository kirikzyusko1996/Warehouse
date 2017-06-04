package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.constants.GoodsStatusEnum;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final static Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private InvoiceDAO invoiceDAO;
    private InvoiceStatusDAO invoiceStatusDAO;
    private InvoiceStatusNameDAO invoiceStatusNameDAO;
    private GoodsStatusDAO goodsStatusDAO;
    private GoodsStatusNameDAO goodsStatusNameDAO;
    private UnitDAO unitDAO;
    private StorageSpaceTypeDAO storageDAO;
    private WarehouseCustomerCompanyDAO customerDAO;
    private WarehouseDAO warehouseDAO;
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
    public void setGoodsStatusDAO(GoodsStatusDAO dao) {
        this.goodsStatusDAO = dao;
    }

    @Autowired
    public void setGoodsStatusNameDAO(GoodsStatusNameDAO dao) {
        this.goodsStatusNameDAO = dao;
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
    public void setWarehouseDAO(WarehouseDAO dao) {
        this.warehouseDAO = dao;
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
    @Lazy
    public void setTransportService(TransportCompanyService service) {
        this.transportService = service;
    }

    @Autowired
    @Lazy
    public void setUserService(UserService service) {
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
    public List<IncomingInvoiceDTO> findAllIncomingInvoices(int page, int count)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find all incoming invoices");

        List<IncomingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<InvoiceStatus> invoices = retrieveAllInvoicesStatuses();
            List<InvoiceStatus> incomingInvoices = parseIncomingInvoices(invoices);

            for (InvoiceStatus invoiceStatus : incomingInvoices) {
                Invoice invoice = invoiceStatus.getInvoice();

                //todo specify page, count
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
    public List<OutgoingInvoiceDTO> findAllOutgoingInvoices(int page, int count)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find all outgoing invoices");

        List<OutgoingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<InvoiceStatus> invoices = retrieveAllInvoicesStatuses();
            List<InvoiceStatus> incomingInvoices = parseOutgoingInvoices(invoices);

            for (InvoiceStatus invoiceStatus : incomingInvoices) {
                Invoice invoice = invoiceStatus.getInvoice();

                //todo specify page, count
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
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<IncomingInvoiceDTO> findAllIncomingInvoicesForWarehouse(int page, int count, Long warehouseId)
            throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Find all incoming invoices");

        if (warehouseId == null) {
            throw new IllegalParametersException("Warehouse id is null");
        }

        List<IncomingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<Invoice> invoices = invoiceDAO.findInvoicesByWarehouseId(warehouseId, page, count);
            List<Invoice> incomingInvoices = parseIncoming(invoices);

            for (Invoice invoice : incomingInvoices) {
                List<Goods> goodsForInvoice = goodsService.findGoodsForInvoice(invoice.getId(), -1, -1);

                IncomingInvoiceDTO dto = convertToIncomingDTO(invoice.getCurrentStatus(), goodsForInvoice);
                invoiceDTOs.add(dto);
            }

        } catch (GenericDAOException e) {
            logger.error("Error during searching for incoming invoices: {}", e);
            throw new DataAccessException(e);
        }

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#warehouseId, 'Warehouse', 'GET')")
    public List<OutgoingInvoiceDTO> findAllOutgoingInvoicesForWarehouse(int page, int count, Long warehouseId)
            throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find all outgoing invoices");

        if (warehouseId == null) {
            throw new IllegalParametersException("Warehouse id is null");
        }

        List<OutgoingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<Invoice> invoices = invoiceDAO.findInvoicesByWarehouseId(warehouseId, page, count);
            List<Invoice> incomingInvoices = parseOutgoing(invoices);

            for (Invoice invoice : incomingInvoices) {
                List<Goods> goodsForInvoice = goodsService.findGoodsForInvoice(invoice.getId(), -1, -1);

                OutgoingInvoiceDTO dto = convertToOutgoingDTO(invoice.getCurrentStatus(), goodsForInvoice);
                invoiceDTOs.add(dto);
            }
        } catch (GenericDAOException e) {
            logger.error("Error during searching for incoming invoices: {}", e);
            throw new DataAccessException(e);
        }

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findInvoiceById(Long id) throws DataAccessException {
        logger.info("Find dto by id #{}", id);

        Invoice invoice = null;
        try {
            Optional<Invoice> optional = invoiceDAO.findById(id);
            if (optional.isPresent()) {
                invoice = optional.get();
            }
        } catch (GenericDAOException e) {
            logger.error("Error while finding dto by id: ", e);
            throw new DataAccessException(e);
        }

        return invoice;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findInvoiceByNumber(String number) throws DataAccessException {
        logger.info("Find dto by number {}", number);

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
                logger.error("Error while finding dto by number: ", e);
                throw new DataAccessException(e);
            }
        }

        return invoice;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'Invoice', 'GET')")
    public IncomingInvoiceDTO findIncomingInvoiceForCompanyById(Long id, Long idWarehouseCompany)
            throws DataAccessException, ResourceNotFoundException, IllegalParametersException, GenericDAOException {
        logger.info("Find incoming invoice by id #{}", id);

        Optional<Invoice> optional = invoiceDAO.findById(id);
        if (optional.isPresent()) {
            Invoice invoice = optional.get();
            List<Goods> goodsList = goodsService.findGoodsForInvoice(id, -1, -1);

            return convertToIncomingDTO(invoice.getCurrentStatus(), goodsList);
        } else {
            throw new ResourceNotFoundException("Invoice not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'Invoice', 'GET')")
    public OutgoingInvoiceDTO findOutgoingInvoiceForCompanyById(Long id, Long idWarehouseCompany)
            throws GenericDAOException, DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Find outgoing invoice by id #{}", id);

        InvoiceStatus invoiceStatus = invoiceStatusDAO.findStatusForInvoice(id);
        List<Goods> goodsList = goodsService.findGoodsForInvoice(id, -1, -1);

        return convertToOutgoingDTO(invoiceStatus, goodsList);
    }

    @Override
    @Transactional
    public Invoice saveInvoice(Invoice invoice) throws DataAccessException {
        logger.info("Save dto: {}", invoice);

        Invoice savedInvoice;
        try {
            savedInvoice = invoiceDAO.insert(invoice);
        } catch (GenericDAOException e) {
            logger.error("Error while saving dto: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    //todo @PreAuthorize("hasPermission('WarehouseCustomerCompany', 'WRITE')")
    public Invoice saveIncomingInvoice(WarehouseCompanyUserDetails principal, IncomingInvoiceDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Save incoming dto: {}", dto);

        Invoice savedInvoice;
        try {
            User currentUser = userService.findUserById(principal.getUserId());
            Warehouse currentWarehouse = currentUser.getWarehouse();

            Invoice invoice = convertToIncomingInvoice(dto, currentWarehouse);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForNewIncomingInvoice(savedInvoice, currentUser);
            invoiceStatusDAO.insert(invoiceStatus);

            invoice.setCurrentStatus(invoiceStatus);

            List<GoodsDTO> goodsList = dto.getGoods();
            goodsService.createGoodsBatch(savedInvoice.getId(), goodsList);
        } catch (GenericDAOException e) {
            logger.error("Error while saving incoming dto: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    //todo @PreAuthorize("hasPermission('WarehouseCustomerCompany', 'WRITE')")
    public Invoice saveOutgoingInvoice(WarehouseCompanyUserDetails principal, OutgoingInvoiceDTO dto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Save outgoing dto: {}", dto);

        Invoice savedInvoice;
        try {
            User currentUser = userService.findUserById(principal.getUserId());
            Warehouse currentWarehouse = currentUser.getWarehouse();

            Invoice invoice = convertToOutgoingInvoice(dto, currentWarehouse);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForNewOutgoingInvoice(savedInvoice, currentUser);
            invoiceStatusDAO.insert(invoiceStatus);

            invoice.setCurrentStatus(invoiceStatus);

            processGoodsForOutgoingInvoice(dto.getGoods(), savedInvoice, principal.getUser());

        } catch (GenericDAOException e) {
            logger.error("Error while saving outgoing invoice: ", e);
            throw new DataAccessException(e);
        }

        return savedInvoice;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Invoice', 'UPDATE')")
    public void updateIncomingInvoice(Long id, IncomingInvoiceDTO dto, Long idWarehouse)
            throws DataAccessException, ResourceNotFoundException, IllegalParametersException {
        logger.info("Update incoming invoice: {}", dto);

        try {
            dto.setId(id);

            Optional<Warehouse> optional = warehouseDAO.findById(idWarehouse);
            if (optional.isPresent()) {
                Warehouse warehouse = optional.get();
                Invoice invoice = convertToIncomingInvoice(dto, warehouse);
                invoice.setWarehouse(warehouse);

                invoiceDAO.update(invoice);
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating invoice: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'Invoice', 'UPDATE')")
    public void updateOutgoingInvoice(Long id, OutgoingInvoiceDTO dto, Long idWarehouse)
            throws DataAccessException, ResourceNotFoundException, IllegalParametersException {
        logger.info("Update outgoing dto: {}", dto);

        try {
            dto.setId(id);

            Optional<Warehouse> optional = warehouseDAO.findById(idWarehouse);
            if (optional.isPresent()) {
                Warehouse warehouse = optional.get();
                Invoice invoice = convertToOutgoingInvoice(dto, warehouse);
                invoice.setWarehouse(warehouse);

                invoiceDAO.update(invoice);
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating invoice: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional
    public Invoice updateInvoice(Invoice invoice) throws DataAccessException {
        logger.info("Update dto: {}", invoice);

        Invoice updatedInvoice;
        try {
            updatedInvoice = invoiceDAO.update(invoice);
        } catch (GenericDAOException e) {
            logger.error("Error while updating dto: ", e);
            throw new DataAccessException(e);
        }

        return updatedInvoice;
    }

    @Override
    @Transactional
    public Invoice updateInvoiceStatus(Long invoiceId, String statusName, User user)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Update dto's with id {} statusName", invoiceId);

        if (StringUtils.isEmpty(statusName)) {
            throw new IllegalParametersException("Invalid statusName value");
        }

        Invoice invoice;
        try {
            Optional<Invoice> optional = invoiceDAO.findById(invoiceId);
            if (optional.isPresent()) {
                invoice = optional.get();
                InvoiceStatus status = createStatusForInvoice(invoice, statusName, user);

                InvoiceStatus createdStatusInvoice = invoiceStatusDAO.insert(status);
                invoice.setCurrentStatus(createdStatusInvoice);
                invoiceDAO.update(invoice);

            } else {
                logger.error("Invoice with id {} not found", invoiceId);
                throw new ResourceNotFoundException("Invoice not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while updating dto' statusName: ", e);
            throw new DataAccessException(e);
        }

        return invoice;
    }

    @Override
    @Transactional
    public void deleteInvoice(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Delete dto by id #{}", id);

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
                logger.error("Invoice statusName with id {} not found", invoiceId);
                throw new ResourceNotFoundException("Invoice statusName not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error while deleting dto: ", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean invoiceExists(Invoice invoice) throws DataAccessException {
        logger.error("Determine if dto #{} exists", invoice.getId());

        try {
            return invoiceDAO.isExistsEntity(invoice.getId());
        } catch (GenericDAOException e) {
            logger.error("Error while determine if dto exists", e);
            throw new DataAccessException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseByInvoiceId(Long invoiceId)
            throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Find warehouse of dto with id {}", invoiceId);

        if (invoiceId == null) {
            throw new IllegalParametersException("Invoice id is null");
        }

        Invoice invoice = findInvoiceById(invoiceId);
        if (invoice == null) {
            throw new ResourceNotFoundException("Invoice with such id was not found");
        }

        return invoice.getWarehouse();
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

    private InvoiceStatusName retrieveInvoiceStatusByName(String statusName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatusName.class);
        criteria.add(Restrictions.eq("name", statusName));

        List<InvoiceStatusName> names = invoiceStatusNameDAO.findAll(criteria, -1, -1);
        return names.get(0);
    }

    private GoodsStatusName retrieveGoodsStatusByName(String statusName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(GoodsStatusName.class);
        criteria.add(Restrictions.eq("name", statusName));

        List<GoodsStatusName> names = goodsStatusNameDAO.findAll(criteria, -1, -1);
        return names.get(0);
    }

    private InvoiceStatus retrieveStatusByInvoiceId(Long invoiceId) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatus.class);
        criteria.add(Restrictions.eq("id_invoice", invoiceId));

        List<InvoiceStatus> statuses = invoiceStatusDAO.findAll(criteria, -1, -1);
        return statuses.get(0);
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
            if (statusName.equals(InvoiceStatusEnum.REGISTERED.toString())
                    || statusName.equals(InvoiceStatusEnum.CHECKED.toString())
                    || statusName.equals(InvoiceStatusEnum.COMPLETED.toString())) {
                incomingInvoices.add(invoice);
            }
        }

        return incomingInvoices;
    }

    private List<InvoiceStatus> parseOutgoingInvoices(List<InvoiceStatus> invoices) {
        List<InvoiceStatus> outgoingInvoices = new ArrayList<>();
        for (InvoiceStatus invoice : invoices) {
            String statusName = invoice.getStatusName().getName();
            if (statusName.equals(InvoiceStatusEnum.RELEASE_ALLOWED.toString())
                    || statusName.equals(InvoiceStatusEnum.MOVED_OUT.toString())) {
                outgoingInvoices.add(invoice);
            }
        }

        return outgoingInvoices;
    }

    private List<Invoice> parseIncoming(List<Invoice> invoices) {
        List<Invoice> incomingInvoices = new ArrayList<>();
        for (Invoice invoice : invoices) {
            String statusName = invoice.getCurrentStatus().getStatusName().getName();
            if (statusName.equals(InvoiceStatusEnum.REGISTERED.toString())
                    || statusName.equals(InvoiceStatusEnum.CHECKED.toString())
                    || statusName.equals(InvoiceStatusEnum.COMPLETED.toString())) {
                incomingInvoices.add(invoice);
            }
        }

        return incomingInvoices;
    }

    private List<Invoice> parseOutgoing(List<Invoice> invoices) {
        List<Invoice> outgoingInvoices = new ArrayList<>();
        for (Invoice invoice : invoices) {
            String statusName = invoice.getCurrentStatus().getStatusName().getName();
            if (statusName.equals(InvoiceStatusEnum.RELEASE_ALLOWED.toString())
                    || statusName.equals(InvoiceStatusEnum.MOVED_OUT.toString())) {
                outgoingInvoices.add(invoice);
            }
        }

        return outgoingInvoices;
    }

    private IncomingInvoiceDTO convertToIncomingDTO(InvoiceStatus invoiceStatus, List<Goods> goodsList) {
        IncomingInvoiceDTO dto = new IncomingInvoiceDTO();

        Invoice invoice = invoiceStatus.getInvoice();
        dto.setId(invoice.getId());
        dto.setNumber(invoice.getNumber());
        dto.setIssueDate(invoice.getIssueDate());
        WarehouseCustomerCompanyDTO customer = customerService.mapToDto(invoice.getSupplierCompany());
        dto.setSupplierCompany(customer);
        TransportCompanyDTO transport = transportService.mapToDto(invoice.getTransportCompany());
        dto.setTransportCompany(transport);
        dto.setTransportNumber(invoice.getTransportNumber());
        dto.setTransportName(invoice.getTransportName());
        dto.setDescription(invoice.getDescription());
        dto.setGoodsQuantity(invoice.getGoodsQuantity());
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        if (invoice.getDriver() != null) {
            DriverDTO driver = mapToDto(invoice.getDriver());
            dto.setDriver(driver);
        }

        dto.setGoodsEntryCountUnit(invoice.getGoodsEntryCountUnit());
        dto.setGoodsQuantityUnit(invoice.getGoodsQuantityUnit());

        dto.setDispatcher(invoiceStatus.getUser());
        InvoiceStatusEnum status = InvoiceStatusEnum.getStatus(invoiceStatus.getStatusName().getName());
        dto.setStatus(status.toString());
        dto.setRegistrationDate(invoiceStatus.getDate());

        dto = fillIncomingInvoiceWithGoodsInfo(dto, goodsList);

        return dto;
    }

    private OutgoingInvoiceDTO convertToOutgoingDTO(InvoiceStatus invoiceStatus, List<Goods> goodsList) {
        OutgoingInvoiceDTO dto = new OutgoingInvoiceDTO();

        Invoice invoice = invoiceStatus.getInvoice();
        dto.setId(invoice.getId());
        dto.setNumber(invoice.getNumber());
        dto.setIssueDate(invoice.getIssueDate());
        WarehouseCustomerCompanyDTO customer = customerService.mapToDto(invoice.getReceiverCompany());
        dto.setReceiverCompany(customer);
        TransportCompanyDTO transport = transportService.mapToDto(invoice.getTransportCompany());
        dto.setTransportCompany(transport);
        dto.setTransportNumber(invoice.getTransportNumber());
        dto.setTransportName(invoice.getTransportName());
        dto.setDescription(invoice.getDescription());
        dto.setGoodsQuantity(invoice.getGoodsQuantity());
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        if (invoice.getDriver() != null) {
            DriverDTO driver = mapToDto(invoice.getDriver());
            dto.setDriver(driver);
        }

        dto.setGoodsEntryCountUnit(invoice.getGoodsEntryCountUnit());
        dto.setGoodsQuantityUnit(invoice.getGoodsQuantityUnit());

        dto.setManager(invoiceStatus.getUser());
        InvoiceStatusEnum status = InvoiceStatusEnum.getStatus(invoiceStatus.getStatusName().getName());
        dto.setStatus(status.getName());
        dto.setRegistrationDate(invoiceStatus.getDate());

        dto = fillOutgoingInvoiceWithGoodsInfo(dto, goodsList);

        return dto;
    }

    private Invoice convertToIncomingInvoice(IncomingInvoiceDTO dto, Warehouse warehouse)
            throws GenericDAOException, DataAccessException, ResourceNotFoundException, IllegalParametersException {
        Invoice invoice = new Invoice();
        if (dto.getId() != null) {
            invoice.setId(dto.getId());
        }
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany supplierCompany = customerService.findCustomerById(dto.getSupplierCompany().getId());
        invoice.setSupplierCompany(supplierCompany);
        TransportCompany transportCompany = transportService.findTransportCompanyById(dto.getTransportCompany().getId());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouse(warehouse);

        invoice = fillInvoiceWithUnitsInfo(invoice, dto.getGoodsQuantityUnit(), dto.getGoodsEntryCountUnit());

        if (dto.getDriver() != null) {
            invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());
        }

        return invoice;
    }

    private Invoice convertToOutgoingInvoice(OutgoingInvoiceDTO dto, Warehouse warehouse)
            throws GenericDAOException, DataAccessException, ResourceNotFoundException, IllegalParametersException {
        Invoice invoice = new Invoice();
        if (dto.getId() != null) {
            invoice.setId(dto.getId());
        }
        invoice.setNumber(dto.getNumber());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setTransportNumber(dto.getTransportNumber());
        invoice.setTransportName(dto.getTransportName());
        invoice.setDescription(dto.getDescription());
        invoice.setGoodsQuantity(dto.getGoodsQuantity());
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany receiverCompany = customerService.findCustomerById(dto.getReceiverCompany().getId());
        invoice.setReceiverCompany(receiverCompany);
        TransportCompany transportCompany = transportService.findTransportCompanyById(dto.getTransportCompany().getId());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouse(warehouse);

        invoice = fillInvoiceWithUnitsInfo(invoice, dto.getGoodsQuantityUnit(), dto.getGoodsEntryCountUnit());

        if (dto.getDriver() != null) {
            invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());
        }

        return invoice;
    }

    private InvoiceStatus createStatusForNewIncomingInvoice(Invoice invoice, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = fillStatusWithInfo(invoice, user);

        InvoiceStatusName invoiceStatusName = retrieveInvoiceStatusByName(InvoiceStatusEnum.REGISTERED.toString());
        invoiceStatus.setStatusName(invoiceStatusName);

        return invoiceStatus;
    }

    private InvoiceStatus createStatusForNewOutgoingInvoice(Invoice invoice, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = fillStatusWithInfo(invoice, user);

        InvoiceStatusName invoiceStatusName = retrieveInvoiceStatusByName(InvoiceStatusEnum.MOVED_OUT.toString());
        invoiceStatus.setStatusName(invoiceStatusName);

        return invoiceStatus;
    }

    private InvoiceStatus fillStatusWithInfo(Invoice invoice, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice);

        Timestamp now = new Timestamp(new Date().getTime());
        invoiceStatus.setDate(now);
        invoiceStatus.setUser(user);

        return invoiceStatus;
    }

    private InvoiceStatus createStatusForInvoice(Invoice invoice, String statusName, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice);

        InvoiceStatusName invoiceStatusName = retrieveInvoiceStatusByName(statusName);
        invoiceStatus.setStatusName(invoiceStatusName);

        Timestamp now = new Timestamp(new Date().getTime());
        invoiceStatus.setDate(now);
        invoiceStatus.setUser(user);

        return invoiceStatus;
    }

    private Invoice fillInvoiceWithUnitsInfo(Invoice invoice, String quantityUnitName, String entryCountUnitName)
            throws GenericDAOException {
        Unit goodsQuantityUnit = retrieveUnitByName(quantityUnitName);
        invoice.setGoodsQuantityUnit(goodsQuantityUnit);
        Unit goodsEntryCountUnit = retrieveUnitByName(entryCountUnitName);
        invoice.setGoodsEntryCountUnit(goodsEntryCountUnit);

        return invoice;
    }

    private Invoice fillInvoiceWithDriverInfo(Invoice invoice, DriverDTO driverDTO) throws GenericDAOException {
        Driver driverForInvoice;
        String driverId = String.valueOf(driverDTO.getId());
        if (NumberUtils.isNumber(driverId)) {
            Long validDriverId = Long.valueOf(driverId);
            Optional<Driver> optional = driverDAO.findById(validDriverId);
            if (optional.isPresent()) {
                driverForInvoice = optional.get();
            } else {
                Driver driverToSave = mapToDriver(driverDTO);
                driverForInvoice = driverDAO.insert(driverToSave);
            }
        } else {
            Driver driverToSave = mapToDriver(driverDTO);
            driverForInvoice = driverDAO.insert(driverToSave);
        }

        invoice.setDriver(driverForInvoice);

        return invoice;
    }

    private IncomingInvoiceDTO fillIncomingInvoiceWithGoodsInfo(IncomingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private OutgoingInvoiceDTO fillOutgoingInvoiceWithGoodsInfo(OutgoingInvoiceDTO invoice, List<Goods> goodsList) {
        invoice.setGoods(mapToDTOs(goodsList));
        return invoice;
    }

    private void processGoodsForOutgoingInvoice(List<GoodsDTO> goodsList, Invoice invoice, User user)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException, GenericDAOException {
        for (GoodsDTO goodsToChange : goodsList) {
            Goods initGoods = goodsService.findGoodsById(goodsToChange.getId());
            BigDecimal leftQuantity = initGoods.getQuantity().subtract(goodsToChange.getQuantity());
            if (leftQuantity.compareTo(BigDecimal.ZERO) == 1) {
                processGoodsSeparation(initGoods, goodsToChange, invoice, user);
                logger.info("goods separation");
            } else if (leftQuantity.compareTo(BigDecimal.ZERO) == 0) {
                setGoodsStatusForOutgoingInvoice(initGoods, user);
                processGoodsRemoving(goodsList, invoice);
                logger.info("goods removing");
            } else {
                throw new IllegalParametersException("Quantity of goods can't be more than available");
            }
        }
    }

    private void processGoodsSeparation(Goods goodsToChange, GoodsDTO goodsChangeParamsDto, Invoice invoice, User user)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException, GenericDAOException {
        Goods goodsForInvoice = reduceGoodsQuantity(goodsToChange, goodsChangeParamsDto);
        Goods savedGoods = saveGoodsForOutgoingInvoice(goodsForInvoice, goodsChangeParamsDto, invoice);
        setGoodsStatusForOutgoingInvoice(savedGoods, user);
    }

    private void processGoodsRemoving(List<GoodsDTO> goodsList, Invoice invoice)
            throws GenericDAOException, DataAccessException, IllegalParametersException, ResourceNotFoundException {
        List<Long> goodsListIds = parseIdFromGoods(goodsList);
        goodsService.setOutgoingInvoice(goodsListIds, invoice.getId());
    }

    private Goods reduceGoodsQuantity(Goods goodsToChange, GoodsDTO goodsChangeParamsDto)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        BigDecimal leftQuantity = goodsToChange.getQuantity().subtract(goodsChangeParamsDto.getQuantity());
        goodsToChange.setQuantity(leftQuantity);
        GoodsDTO dto = goodsService.mapToDto(goodsToChange);
        return goodsService.updateGoods(goodsToChange.getId(), dto);
    }

    private Goods saveGoodsForOutgoingInvoice(Goods leftGoods, GoodsDTO goodsChangeParamsDto, Invoice invoice)
            throws GenericDAOException {
        Goods goodsForInvoice = new Goods();
        goodsForInvoice.setName(leftGoods.getName());
        goodsForInvoice.setWeight(leftGoods.getWeight());
        goodsForInvoice.setPrice(leftGoods.getPrice());
        goodsForInvoice.setStorageType(leftGoods.getStorageType());
        goodsForInvoice.setQuantityUnit(leftGoods.getQuantityUnit());
        goodsForInvoice.setWeightUnit(leftGoods.getWeightUnit());
        goodsForInvoice.setPriceUnit(leftGoods.getPriceUnit());
        goodsForInvoice.setIncomingInvoice(leftGoods.getIncomingInvoice());
        // todo maybe statusName history and acts

        goodsForInvoice.setQuantity(goodsChangeParamsDto.getQuantity());
        goodsForInvoice.setOutgoingInvoice(invoice);

        return goodsService.saveGoodsForOutgoingInvoice(goodsForInvoice);
    }

    private Goods updateGoodsForOutgoingInvoice(Goods goodsToChange, Invoice invoice)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        goodsToChange.setOutgoingInvoice(invoice);
        GoodsDTO dto = goodsService.mapToDto(goodsToChange);
        return goodsService.updateGoods(goodsToChange.getId(), dto);
    }

    private void setGoodsStatusForOutgoingInvoice(Goods savedGoods, User user) throws GenericDAOException {
        GoodsStatus status = new GoodsStatus();
        status.setGoods(savedGoods);

        GoodsStatusName statusName = retrieveGoodsStatusByName(GoodsStatusEnum.MOVED_OUT.toString());
        status.setGoodsStatusName(statusName);
        Timestamp now = new Timestamp(new Date().getTime());
        status.setDate(now);
        status.setUser(user);

        goodsStatusDAO.insert(status);
    }

    private List<GoodsDTO> mapToDTOs(List<Goods> goodsList) {
        List<GoodsDTO> goodsInvoiceDTOs = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setName(goods.getName());
            goodsDTO.setQuantity(goods.getQuantity());
            goodsDTO.setPrice(goods.getPrice());
            goodsDTO.setWeight(goods.getWeight());

            goodsDTO.setStorageType(goods.getStorageType());
            goodsDTO.setPriceUnit(goods.getPriceUnit());
            goodsDTO.setQuantityUnit(goods.getQuantityUnit());
            goodsDTO.setWeightUnit(goods.getWeightUnit());

            goodsInvoiceDTOs.add(goodsDTO);
        }

        return goodsInvoiceDTOs;
    }

    private List<Long> parseIdFromGoods(List<GoodsDTO> goodsDTOs) {
        List<Long> ids = new ArrayList<>();
        for (GoodsDTO dto : goodsDTOs) {
            ids.add(dto.getId());
        }

        return ids;
    }

    private Driver mapToDriver(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setPassportNumber(dto.getPassportNumber());
        driver.setCountryCode(dto.getCountryCode());
        driver.setIssueDate(dto.getIssueDate());
        driver.setIssuedBy(dto.getIssuedBy());
        driver.setFullName(dto.getFullName());
        TransportCompany transport = transportService.mapToCompany(dto.getTransportCompany());
        driver.setTransportCompany(transport);

        return driver;
    }

    private DriverDTO mapToDto(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setPassportNumber(driver.getPassportNumber());
        dto.setCountryCode(driver.getCountryCode());
        dto.setIssueDate(driver.getIssueDate());
        dto.setIssuedBy(driver.getIssuedBy());
        dto.setFullName(driver.getFullName());
        TransportCompanyDTO transport = transportService.mapToDto(driver.getTransportCompany());
        dto.setTransportCompany(transport);

        return dto;
    }
}
