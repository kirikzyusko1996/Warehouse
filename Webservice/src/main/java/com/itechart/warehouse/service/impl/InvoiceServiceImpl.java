package com.itechart.warehouse.service.impl;


import com.itechart.warehouse.constants.InvoiceStatusEnum;
import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.InvoiceDAO;
import com.itechart.warehouse.dao.InvoiceStatusDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.DriverDTO;
import com.itechart.warehouse.dto.GoodsInvoiceDTO;
import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final static Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private InvoiceDAO invoiceDAO;
    private InvoiceStatusDAO invoiceStatusDAO;
    private GoodsDAO goodsDAO;

    @Autowired
    public void setInvoiceDAO(InvoiceDAO invoiceDao) {
        this.invoiceDAO = invoiceDao;
    }

    @Autowired
    public void setInvoiceStatusDAO(InvoiceStatusDAO invoiceStatusDAO) {
        this.invoiceStatusDAO = invoiceStatusDAO;
    }

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
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

                IncomingInvoiceDTO dto = convertToDTO(invoice, goodsForInvoice);
                invoiceDTOs.add(dto);
            }

        } catch (GenericDAOException e) {
            logger.error("Error while finding all incoming invoices: ", e);
            throw new DataAccessException(e);
        }

        return invoiceDTOs;
    }

    @Override
    public List<Invoice> findAllOutgoingInvoices() throws DataAccessException {
        return null;
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

    private IncomingInvoiceDTO convertToDTO(InvoiceStatus invoiceStatus, List<Goods> goodsList){
        IncomingInvoiceDTO dto = new IncomingInvoiceDTO();

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

        dto = fillInvoiceWithDriverInfo(dto, invoice.getDriver());

        dto.setDispatcher(invoiceStatus.getUser());
        dto.setRegistrationDate(invoiceStatus.getDate());

        dto = fillInvoiceWithGoodsInfo(dto, goodsList);

        return dto;
    }

    private IncomingInvoiceDTO fillInvoiceWithDriverInfo(IncomingInvoiceDTO invoice, Driver driver){
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setFullName(driver.getFullName());
        driverDTO.setCountryCode(driver.getCountryCode());
        driverDTO.setIssueDate(driver.getIssueDate());
        driverDTO.setPassportNumber(driver.getPassportNumber());

        invoice.setDriver(driverDTO);
        return invoice;
    }

    private IncomingInvoiceDTO fillInvoiceWithGoodsInfo(IncomingInvoiceDTO invoice, List<Goods> goodsList){
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

        invoice.setGoods(goodsInvoiceDTOs);
        return invoice;
    }
}
