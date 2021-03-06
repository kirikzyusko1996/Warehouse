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
    private WarehouseDAO warehouseDAO;
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
    public void setWarehouseDAO(WarehouseDAO dao) {
        this.warehouseDAO = dao;
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
    @PreAuthorize("hasPermission(#principal.user.warehouseCompany.idWarehouseCompany, 'Warehouse', 'GET')")
    public List<IncomingInvoiceDTO> findAllIncomingInvoices(int page, int count, WarehouseCompanyUserDetails principal)
            throws IllegalParametersException, DataAccessException, ResourceNotFoundException {
        logger.info("Find all incoming invoices");

        List<IncomingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<Invoice> invoices = findIncomingInvoicesForUser(principal, page, count);
            for (Invoice invoice : invoices) {
                List<Goods> goodsForInvoice = goodsService.findGoodsForIncomingInvoice(invoice.getId(), -1, -1);

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
    @PreAuthorize("hasPermission(#principal.user.warehouseCompany.idWarehouseCompany, 'Warehouse', 'GET')")
    public List<OutgoingInvoiceDTO> findAllOutgoingInvoices(int page, int count, WarehouseCompanyUserDetails principal)
            throws IllegalParametersException, ResourceNotFoundException, DataAccessException {
        logger.info("Find all outgoing invoices");

        List<OutgoingInvoiceDTO> invoiceDTOs = new ArrayList<>();
        try {
            List<Invoice> invoices = findOutgoingInvoicesForUser(principal, page, count);
            for (Invoice invoice : invoices) {
                List<Goods> goodsForInvoice = goodsService.findGoodsForOutgoingInvoice(invoice.getId(), -1, -1);

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
    @PreAuthorize("hasPermission(#principal.user.warehouseCompany.idWarehouseCompany, 'Warehouse', 'GET')")
    public InvoicesCountDTO findInvoicesCount(WarehouseCompanyUserDetails principal)
            throws DataAccessException {
        logger.info("Find invoices count");

        try {
            InvoicesCountDTO countDTO = new InvoicesCountDTO();
            Long count = findInvoicesCountForUser(principal);
            countDTO.setCount(count);

            return countDTO;
        } catch (GenericDAOException e) {
            logger.error("Error during counting invoices: {}", e);
            throw new DataAccessException(e);
        }
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
    @PreAuthorize("hasPermission(#id, 'Invoice', 'GET')")
    public IncomingInvoiceDTO findIncomingInvoiceForCompanyById(Long id, Long idWarehouseCompany)
            throws DataAccessException, ResourceNotFoundException, IllegalParametersException, GenericDAOException {
        logger.info("Find incoming invoice by id #{}", id);

        Optional<Invoice> optional = invoiceDAO.findById(id);
        if (optional.isPresent()) {
            Invoice invoice = optional.get();
            List<Goods> goodsList = goodsService.findGoodsForIncomingInvoice(id, -1, -1);

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

        Optional<Invoice> optional = invoiceDAO.findById(id);
        if (optional.isPresent()) {
            Invoice invoice = optional.get();
            List<Goods> goodsList = goodsService.findGoodsForOutgoingInvoice(id, -1, -1);

            return convertToOutgoingDTO(invoice.getCurrentStatus(), goodsList);
        } else {
            throw new ResourceNotFoundException("Invoice not found");
        }
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
            User currentUser = principal.getUser();
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
            User currentUser = principal.getUser();
            Warehouse currentWarehouse = currentUser.getWarehouse();

            Invoice invoice = convertToOutgoingInvoice(dto, currentWarehouse);
            savedInvoice = invoiceDAO.insert(invoice);

            InvoiceStatus invoiceStatus = createStatusForNewOutgoingInvoice(savedInvoice, currentUser);
            invoiceStatusDAO.insert(invoiceStatus);

            invoice.setCurrentStatus(invoiceStatus);

            List<GoodsDTO> goodsList = dto.getGoods();
            goodsService.updateAndGetGoodsForOutgoingInvoice(savedInvoice, goodsList);

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

                updateGoodsStatuses(invoice);

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
    public Boolean invoiceExistsByNumber(String number) throws DataAccessException {
        logger.error("Determine if dto #{} exists", number);

        try {
            if (findInvoiceByNumber(number) != null) {
                return true;
            }
        } catch (GenericDAOException e) {
            logger.error("Error while determine if dto exists", e);
            throw new DataAccessException(e);
        }

        return false;
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

    private Invoice findInvoiceByNumber(String number) throws DataAccessException, GenericDAOException {
        Invoice invoice = null;

        if (StringUtils.isNotEmpty(number)) {
            DetachedCriteria criteria = DetachedCriteria.forClass(Invoice.class);
            criteria.add(Restrictions.eq("number", number));

            List<Invoice> invoices = invoiceDAO.findAll(criteria, -1, -1);
            if (CollectionUtils.isNotEmpty(invoices)) {
                invoice = invoices.get(0);
            }
        }

        return invoice;
    }

    private List<Invoice> findIncomingInvoicesForUser(WarehouseCompanyUserDetails principal, int page, int count)
            throws GenericDAOException {
        List<Invoice> invoices = new ArrayList<>();
        User user = principal.getUser();
        Long warehouseId = principal.getWarehouse().getIdWarehouse();
        if (user.hasRole("ROLE_CONTROLLER")) {
            String status = "REGISTERED_INCOMING";
            invoices.addAll(invoiceDAO.findInvoicesByWarehouseIdAndStatus(warehouseId, status, page, count));
        }
        if (user.hasRole("ROLE_MANAGER")) {
            String status = "CHECKED";
            invoices.addAll(invoiceDAO.findInvoicesByWarehouseIdAndStatus(warehouseId, status, page, count));
        }

        return invoices;
    }

    private List<Invoice> findOutgoingInvoicesForUser(WarehouseCompanyUserDetails principal, int page, int count)
            throws GenericDAOException {
        List<Invoice> invoices = new ArrayList<>();
        User user = principal.getUser();
        Long warehouseId = principal.getWarehouse().getIdWarehouse();
        if (user.hasRole("ROLE_CONTROLLER")) {
            String status = "REGISTERED_OUTGOING";
            invoices.addAll(invoiceDAO.findInvoicesByWarehouseIdAndStatus(warehouseId, status, page, count));
        }
        if (user.hasRole("ROLE_DISPATCHER")) {
            String status = "RELEASE_ALLOWED";
            invoices.addAll(invoiceDAO.findInvoicesByWarehouseIdAndStatus(warehouseId, status, page, count));
        }

        return invoices;
    }

    private Long findInvoicesCountForUser(WarehouseCompanyUserDetails principal) throws GenericDAOException {
        Long count = 0L;
        User user = principal.getUser();
        Long warehouseId = principal.getWarehouse().getIdWarehouse();
        if (user.hasRole("ROLE_CONTROLLER")) {
            count += invoiceDAO.findInvoicesCountByWarehouseIdAndStatusForController(warehouseId);
        }
        if (user.hasRole("ROLE_DISPATCHER")) {
            String status = "RELEASE_ALLOWED";
            count += invoiceDAO.findInvoicesCountByWarehouseIdAndStatus(warehouseId, status);
        }
        if (user.hasRole("ROLE_MANAGER")) {
            String status = "CHECKED";
            count += invoiceDAO.findInvoicesCountByWarehouseIdAndStatus(warehouseId, status);
        }

        return count;
    }

    private List<Invoice> retrieveAllInvoices() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Invoice.class);
        return invoiceDAO.findAll(criteria, -1, -1);
    }

    private InvoiceStatusName retrieveInvoiceStatusByName(String statusName) throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(InvoiceStatusName.class);
        criteria.add(Restrictions.eq("name", statusName));

        List<InvoiceStatusName> names = invoiceStatusNameDAO.findAll(criteria, -1, -1);
        return names.get(0);
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
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        if (invoice.getDriver() != null) {
            DriverDTO driver = mapToDto(invoice.getDriver());
            dto.setDriver(driver);
        }

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
        dto.setGoodsEntryCount(invoice.getGoodsEntryCount());

        if (invoice.getDriver() != null) {
            DriverDTO driver = mapToDto(invoice.getDriver());
            dto.setDriver(driver);
        }

        dto.setManager(invoiceStatus.getUser());
        InvoiceStatusEnum status = InvoiceStatusEnum.getStatus(invoiceStatus.getStatusName().getName());
        dto.setStatus(status.toString());
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
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany supplierCompany = customerService.findCustomerById(dto.getSupplierCompany().getId());
        invoice.setSupplierCompany(supplierCompany);
        TransportCompany transportCompany = transportService.findTransportCompanyById(dto.getTransportCompany().getId());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouse(warehouse);

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
        invoice.setGoodsEntryCount(dto.getGoodsEntryCount());

        WarehouseCustomerCompany receiverCompany = customerService.findCustomerById(dto.getReceiverCompany().getId());
        invoice.setReceiverCompany(receiverCompany);
        TransportCompany transportCompany = transportService.findTransportCompanyById(dto.getTransportCompany().getId());
        invoice.setTransportCompany(transportCompany);

        invoice.setWarehouse(warehouse);

        if (dto.getDriver() != null) {
            invoice = fillInvoiceWithDriverInfo(invoice, dto.getDriver());
        }

        return invoice;
    }

    private InvoiceStatus createStatusForNewIncomingInvoice(Invoice invoice, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = fillStatusWithInfo(invoice, user);

        InvoiceStatusName invoiceStatusName = retrieveInvoiceStatusByName(InvoiceStatusEnum.REGISTERED_INCOMING.toString());
        invoiceStatus.setStatusName(invoiceStatusName);

        return invoiceStatus;
    }

    private InvoiceStatus createStatusForNewOutgoingInvoice(Invoice invoice, User user)
            throws GenericDAOException {
        InvoiceStatus invoiceStatus = fillStatusWithInfo(invoice, user);

        InvoiceStatusName invoiceStatusName = retrieveInvoiceStatusByName(InvoiceStatusEnum.REGISTERED_OUTGOING.toString());
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

    private void updateGoodsStatuses(Invoice invoice)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        String statusName = invoice.getCurrentStatus().getStatusName().getName();
        List<Goods> goodsList;
        if (isIncoming(statusName)) {
            goodsList = invoice.getIncomingGoods();
        } else {
            goodsList = invoice.getOutgoingGoods();
        }

        if (!statusName.equals(InvoiceStatusEnum.COMPLETED.toString())) {
            GoodsStatusEnum goodsStatus = parseGoodsStatusByInvoiceStatus(statusName);
            for (Goods goods : goodsList) {
                goodsService.setGoodsStatus(goods.getId(), goodsStatus);
            }
        }
    }

    private GoodsStatusEnum parseGoodsStatusByInvoiceStatus(String statusName) {
        return GoodsStatusEnum.valueOf(statusName);
    }

    private boolean isIncoming(String statusName) {
        return statusName.equals(InvoiceStatusEnum.CHECKED.toString()) ||
                statusName.equals(InvoiceStatusEnum.COMPLETED.toString());
    }

    private List<GoodsDTO> mapToDTOs(List<Goods> goodsList) {
        List<GoodsDTO> goodsDTOs = new ArrayList<>();
        for (Goods goods : goodsList) {
            goodsDTOs.add(goodsService.mapGoodsToDTO(goods));
        }

        return goodsDTOs;
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
