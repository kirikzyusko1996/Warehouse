package com.itechart.warehouse.service.services;


import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.service.exception.DataAccessException;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAllInvoices() throws DataAccessException;

    Invoice findInvoiceById(Long id) throws DataAccessException;

    Invoice saveInvoice(Invoice company) throws DataAccessException;

    Invoice updateInvoice(Invoice company) throws DataAccessException;

    void deleteInvoice(Invoice company) throws DataAccessException;

    boolean invoiceExists(Invoice company) throws DataAccessException;
}
