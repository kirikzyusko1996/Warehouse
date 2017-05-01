package com.itechart.warehouse.service.services;


import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAllInvoices() throws DataAccessException;

    List<IncomingInvoiceDTO> findAllIncomingInvoices() throws DataAccessException;

    List<Invoice> findAllOutgoingInvoices() throws DataAccessException;

    Invoice findInvoiceById(Long id) throws DataAccessException;

    Invoice findInvoiceByNumber(String number) throws DataAccessException;

    Invoice saveInvoice(Invoice invoice) throws DataAccessException;

    Invoice updateInvoice(Invoice invoice) throws DataAccessException;

    void deleteInvoice(Invoice invoice) throws DataAccessException;

    boolean invoiceExists(Invoice invoice) throws DataAccessException;
}
