package com.itechart.warehouse.controller;

import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import com.itechart.warehouse.service.services.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @RequestMapping(value = "/outgoing", method = RequestMethod.GET)
    public ResponseEntity<List<OutgoingInvoiceDTO>> readOutgoingInvoices(){
        logger.info("GET on /invoice/outgoing: find all registered outgoing invoices");

        List<OutgoingInvoiceDTO> companies;
        try{
            companies = invoiceService.findAllOutgoingInvoices();
        } catch (DataAccessException e){
            logger.error("Error while retrieving all registered outgoing invoices", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @RequestMapping(value = "/incoming", method = RequestMethod.POST)
    public ResponseEntity<?> saveIncomingInvoice(@Valid @RequestBody IncomingInvoiceDTO invoice){
        logger.info("POST on /invoice/incoming: save new incoming invoice");

        // todo security check

        try{
            invoiceService.saveIncomingInvoice(invoice);
        } catch (DataAccessException e){
            logger.error("Error while saving new incoming invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/outgoing", method = RequestMethod.POST)
    public ResponseEntity<?> saveOutgoingInvoice(@Valid @RequestBody OutgoingInvoiceDTO invoice){
        logger.info("POST on /invoice/outgoing: save new outgoing invoice");

        // todo security check

        try{
            invoiceService.saveOutgoingInvoice(invoice);
        } catch (DataAccessException e){
            logger.error("Error while saving new outgoing invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // no need to update invoice, just update it's status

    // todo status sends via param or with json(now)
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<?> updateInvoiceStatus(@PathVariable String id, @Valid @RequestBody String status){
        logger.info("PUT on /invoice/{}/status: update invoice status", id);

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

        return new ResponseEntity<>(HttpStatus.CREATED);
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

    @RequestMapping(value = "/{invoiceId}/goods", method = RequestMethod.GET)
    public ResponseEntity<List<Goods>> readGoodsOfInvoice(@PathVariable String invoiceId){
        logger.info("GET on /invoice/{}/goods: find all goods for specified invoice", invoiceId);

        List<Goods> goodsList;
        try{
            // todo edit maxResult and limit params
            // todo edit check invoiceId
            Long id = Long.valueOf(invoiceId);
            goodsList = goodsService.findGoodsForInvoice(id, -1, -1);
        } catch (DataAccessException e){
            logger.error("Error while retrieving all goods for specified invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalParametersException e) {
            logger.error("Invalid params specified while getting goods for invoice", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(goodsList, HttpStatus.OK);
    }
}
