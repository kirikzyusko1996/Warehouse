package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.InvoiceService;
import com.itechart.warehouse.service.services.WarehouseCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * API for trusted companies to load invoices.
 */
@RestController
@RequestMapping("/api")
public class TransportCompanyApiController {
    private static final Logger logger = LoggerFactory.getLogger(TransportCompanyApiController.class);
    private InvoiceService invoiceService;
    private WarehouseCompanyService warehouseCompanyService;

    @Autowired
    public void setWarehouseCompanyService(WarehouseCompanyService warehouseCompanyService) {
        this.warehouseCompanyService = warehouseCompanyService;
    }

    @Autowired
    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Void> saveIncomingInvoice(@Valid @RequestBody IncomingInvoiceDTO invoice) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("POST on /api: save new incoming invoice: {}", invoice);
        WarehouseCompanyUserDetails principal = UserDetailsProvider.getUserDetails();
        invoiceService.saveIncomingInvoice(principal, invoice);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<WarehouseCompany> saveIncomingInvoice(@PathVariable Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /api/{id}: get info about warehouseCompany from system");
        WarehouseCompany company = warehouseCompanyService.findWarehouseCompanyById(id);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }
}
