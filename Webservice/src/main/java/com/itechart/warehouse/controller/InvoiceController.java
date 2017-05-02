package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.services.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoice")
@Validated
public class InvoiceController {
    private final static Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private InvoiceService invoiceService;

    @Autowired
    public void setInvoiceService(InvoiceService service){
        this.invoiceService = service;
    }

    @RequestMapping(value = "/incoming", method = RequestMethod.GET)
    public ResponseEntity<List<IncomingInvoiceDTO>> readIncomingInvoices(){
        logger.info("GET on /invoice/incoming: find all registered incoming invoices");

        List<IncomingInvoiceDTO> companies;
        try{
            companies = invoiceService.findAllIncomingInvoices();
        } catch (DataAccessException e){
            logger.error("Error while retrieving all registered incoming invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }
}
