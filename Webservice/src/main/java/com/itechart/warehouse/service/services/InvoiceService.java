package com.itechart.warehouse.service.services;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAllInvoices() throws DataAccessException;

    List<IncomingInvoiceDTO> findAllIncomingInvoices(int page, int count)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<OutgoingInvoiceDTO> findAllOutgoingInvoices(int page, int count)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<IncomingInvoiceDTO> findAllIncomingInvoicesForWarehouse(int page, int count, Long idWarehouse) throws IllegalParametersException, DataAccessException, ResourceNotFoundException;

    List<OutgoingInvoiceDTO> findAllOutgoingInvoicesForWarehouse(int page, int count, Long idWarehouse) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Invoice findInvoiceById(Long id) throws DataAccessException;

    Invoice findInvoiceByNumber(String number) throws DataAccessException;

    IncomingInvoiceDTO findIncomingInvoiceForCompanyById(Long id, Long idWarehouseCompany) throws DataAccessException, ResourceNotFoundException, IllegalParametersException, GenericDAOException;

    OutgoingInvoiceDTO findOutgoingInvoiceForCompanyById(Long id, Long idWarehouseCompany) throws GenericDAOException, DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Invoice saveInvoice(Invoice invoice) throws DataAccessException;

    Invoice saveIncomingInvoice(WarehouseCompanyUserDetails principal, IncomingInvoiceDTO invoice)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Invoice saveOutgoingInvoice(WarehouseCompanyUserDetails principal, OutgoingInvoiceDTO invoice)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void updateIncomingInvoice(Long id, IncomingInvoiceDTO invoice, Long idWarehouse) throws DataAccessException, ResourceNotFoundException, IllegalParametersException;

    void updateOutgoingInvoice(Long id, OutgoingInvoiceDTO invoice, Long idWarehouse) throws DataAccessException, ResourceNotFoundException, IllegalParametersException;

    Invoice updateInvoice(Invoice invoice) throws DataAccessException;

    InvoiceStatus updateInvoiceStatus(String id, String status)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteInvoice(String id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean invoiceExists(Invoice invoice) throws DataAccessException;

    Warehouse findWarehouseByInvoiceId(Long invoiceId) throws IllegalParametersException, DataAccessException, ResourceNotFoundException;
}
