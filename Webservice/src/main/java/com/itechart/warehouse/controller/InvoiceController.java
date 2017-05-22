package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.error.RequestHandlingError;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import com.itechart.warehouse.service.services.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping("/invoice")
@Validated
public class InvoiceController {
    private final static Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private InvoiceService invoiceService;
    private GoodsService goodsService;

    @Autowired
    public void setInvoiceService(InvoiceService service){
        this.invoiceService = service;
    }

    @Autowired
    public void setGoodsService(GoodsService service){this.goodsService = service; }

    @RequestMapping(value = "/incoming", method = RequestMethod.GET)
    public ResponseEntity<List<IncomingInvoiceDTO>> readIncomingInvoices(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "-1") int count){
        logger.info("GET on /invoice/incoming: find all registered incoming invoices");

        List<IncomingInvoiceDTO> companies;
        try{
            companies = invoiceService.findAllIncomingInvoices(page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all registered incoming invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while retrieving all registered incoming invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Invoice with specified id not found while retrieving all registered incoming invoices", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/outgoing", method = RequestMethod.GET)
    public ResponseEntity<List<OutgoingInvoiceDTO>> readOutgoingInvoices(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "-1") int count){
        logger.info("GET on /invoice/outgoing: find all registered outgoing invoices");

        List<OutgoingInvoiceDTO> companies;
        try{
            companies = invoiceService.findAllOutgoingInvoices(page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all registered outgoing invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while retrieving all registered outgoing invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Invoice with specified id not found while retrieving all registered outgoing invoices", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/incoming/{id}", method = RequestMethod.GET)
    public ResponseEntity<IncomingInvoiceDTO> readIncomingInvoice(@PathVariable Long id)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException, GenericDAOException {
        logger.info("GET on /invoice/incoming/{}: find invoice", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            WarehouseCompany company = userDetails.getCompany();
            IncomingInvoiceDTO invoice = invoiceService.findIncomingInvoiceForCompanyById(id, company.getIdWarehouseCompany());
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving invoice");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/incoming", method = RequestMethod.POST)
    public ResponseEntity<?> saveIncomingInvoice(@Valid @RequestBody IncomingInvoiceDTO invoice){
        logger.info("POST on /invoice/incoming: save new incoming invoice");

        // todo security check

        try{
            WarehouseCompanyUserDetails principal = UserDetailsProvider.getUserDetails();
            invoiceService.saveIncomingInvoice(principal, invoice);
        } catch (DataAccessException e){
            logger.error("Error while saving new incoming invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while saving new incoming invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Invoice with specified id not found while saving new incoming invoice", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/outgoing", method = RequestMethod.POST)
    public ResponseEntity<?> saveOutgoingInvoice(@Valid @RequestBody OutgoingInvoiceDTO invoice){
        logger.info("POST on /invoice/outgoing: save new outgoing invoice");

        // todo security check

        try{
            WarehouseCompanyUserDetails principal = UserDetailsProvider.getUserDetails();
            invoiceService.saveOutgoingInvoice(principal, invoice);
        } catch (DataAccessException e){
            logger.error("Error while saving new outgoing invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while saving new outgoing invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Invoice with specified id not found while saving new outgoing invoice", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/incoming/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateIncomingInvoice(@PathVariable Long id, @Valid @RequestBody IncomingInvoiceDTO invoice)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /invoice/incoming/{}: update invoice", id);

        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            Warehouse warehouse = userDetails.getWarehouse();
            invoiceService.updateIncomingInvoice(id, invoice, warehouse.getIdWarehouse());
        } else {
            logger.error("Failed to retrieve authenticated user while updating incoming invoice");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //status is sent via param
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateInvoiceStatus(@PathVariable String id, @RequestParam String status){
        logger.info("PUT on /invoice/{}?status={}: update invoice status", id);

        // todo security check

        try{
            invoiceService.updateInvoiceStatus(id, status);
        } catch (DataAccessException e){
            logger.error("Error while updating invoice status", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while updating invoice status", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Invoice with specified id not found while updating invoice status", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteInvoice(@PathVariable String id){
        logger.info("DELETE on /invoice/{}: invoice", id);

        // todo security check

        try{
            invoiceService.deleteInvoice(id);
        } catch (DataAccessException e){
            logger.error("Error while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e){
            logger.error("Invalid params specified while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            logger.error("Transport company with specified id not found while deleting transport company", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{invoiceId}/goods", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Goods>> readGoodsOfInvoice(@PathVariable Long invoiceId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "-1") int count)
            throws ResourceNotFoundException {
        logger.info("GET on /invoice/{}/goods: find all goods for specified invoice", invoiceId);

        List<Goods> goodsList;
        try{
            goodsList = goodsService.findGoodsForInvoice(invoiceId, page, count);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all goods for specified invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid params specified while getting goods for invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(goodsList, HttpStatus.OK);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public
    @ResponseBody
    RequestHandlingError handleException(DataAccessException e) {
        RequestHandlingError dataAccessError = new RequestHandlingError();
        dataAccessError.setError(e.getMessage());
        return dataAccessError;
    }

    @ExceptionHandler(IllegalParametersException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    RequestHandlingError handleException(IllegalParametersException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError illegalParametersError = new RequestHandlingError();
        illegalParametersError.setError(e.getMessage());
        return illegalParametersError;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public
    @ResponseBody
    RequestHandlingError handleException(ResourceNotFoundException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError resourceNotFoundError = new RequestHandlingError();
        resourceNotFoundError.setError(e.getMessage());
        return resourceNotFoundError;
    }

    @ExceptionHandler(RequestHandlingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public
    @ResponseBody
    RequestHandlingError handleException(RequestHandlingException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError requestHandlingError = new RequestHandlingError();
        requestHandlingError.setError(e.getMessage());
        return requestHandlingError;
    }
}

