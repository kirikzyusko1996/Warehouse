package com.itechart.warehouse.controller;

import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests to goods service.
 */
@RestController
@RequestMapping(value = "/goods")
@Validated
public class GoodsController {

    private static final String HEADER_X_TOTAL_COUNT = "X-total-count";
    private static final String HEADER_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    private Logger logger = LoggerFactory.getLogger(GoodsController.class);

    private GoodsService goodsService;

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @RequestMapping(value = "/{warehouseId}/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getGoods(@RequestParam(defaultValue = "-1") int page,
                                                   @RequestParam(defaultValue = "-1") int count,
                                                   @PathVariable Long warehouseId,
                                                   HttpServletResponse response) throws DataAccessException, IllegalParametersException {
        logger.info("GET on /{}/list, page: {}, count: {}", warehouseId, page, count);
        List<GoodsDTO> goods;
        if (page == -1 && count == -1) {
            goods = goodsService.findGoodsForWarehouse(warehouseId, -1, -1);
        } else {
            goods = goodsService.findGoodsForWarehouse(warehouseId, (page - 1) * count, count);
        }
        long goodsCount = goodsService.getGoodsCountForWarehouse(warehouseId);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(goodsCount));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "company/{warehouseCompanyId}/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getGoodsForCompany(@RequestParam(defaultValue = "-1") int page,
                                                   @RequestParam(defaultValue = "-1") int count,
                                                   @PathVariable Long warehouseCompanyId,
                                                   HttpServletResponse response) throws DataAccessException, IllegalParametersException {
        logger.info("GET on company/{}/list, page: {}, count: {}", warehouseCompanyId, page, count);
        List<GoodsDTO> goods;
        if (page == -1 && count == -1) {
            goods = goodsService.findGoodsForCompany(warehouseCompanyId, -1, -1);
        } else {
            goods = goodsService.findGoodsForCompany(warehouseCompanyId, (page - 1) * count, count);
        }
        long goodsCount = goodsService.getGoodsCountForCompany(warehouseCompanyId);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(goodsCount));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }


    @RequestMapping(value = "/invoice/{invoiceId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getGoodsForInvoice(@PathVariable Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /invoice/{}", invoiceId);
        List<GoodsDTO> goods = goodsService.findGoodsDTOsForInvoice(invoiceId);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    /**
     * Method for getting list of goods with current status = STORED.
     *
     * @param page        number of requested page pagination wi
     * @param count       count of goods on page pagination wise.
     * @param warehouseId id of warehouse.
     * @param response    response object.
     * @param emptyParam  empty param just to create unique method signature.
     * @return http response with list of goods and headers set.
     * @throws DataAccessException        if exception occured during access to database.
     * @throws IllegalParametersException if warehouse id is null.
     */
    @RequestMapping(value = "/{warehouseId}/stored", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getStoredGoods(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "-1") int count,
                                                         @PathVariable Long warehouseId,
                                                         HttpServletResponse response, boolean emptyParam) throws DataAccessException, IllegalParametersException {
        logger.info("GET on /{}/stored, page: {}, count: {}", warehouseId, page, count);
        List<GoodsDTO> goods = goodsService.findStoredGoodsForWarehouse(warehouseId, page, count);
        long goodsCount = goodsService.getStoredGoodsCount(warehouseId);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(goodsCount));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/{warehouseId}/act_applicable", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getStoredGoods(@RequestParam(defaultValue = "-1") int page,
                                                         @RequestParam(defaultValue = "0") int count,
                                                         @PathVariable Long warehouseId,
                                                         HttpServletResponse response, boolean emptyParam, boolean emptyParam2) throws DataAccessException, IllegalParametersException {
        logger.info("GET on /{}/act_applicable, page: {}, count: {}", warehouseId, page, count);
        List<GoodsDTO> goods = goodsService.findActApplicableGoods(warehouseId, (page - 1) * count, count);
        long goodsCount = goodsService.getActApplicableGoodsCount(warehouseId);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(goodsCount));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getAllGoods(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "-1") int count)
            throws DataAccessException, IllegalParametersException {
        logger.info("GET on /, page: {}, count: {}", page, count);
        WarehouseCompanyUserDetails userDetails = UserDetailsProvider.getUserDetails();
        if (userDetails != null) {
            Warehouse warehouse = userDetails.getWarehouse();
            List<GoodsDTO> goods = goodsService.findGoodsForWarehouse(warehouse.getIdWarehouse(), page, count);
            return new ResponseEntity<>(goods, HttpStatus.OK);
        } else {
            logger.error("Failed to retrieve authenticated user while retrieving goods");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable Long goodsId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /{}", goodsId);
        return new ResponseEntity<>(goodsService.findGoodsDTOById(goodsId), HttpStatus.OK);
    }

    @RequestMapping(value = "/status/{goodsId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsStatusDTO>> getStatusOfGoods(@PathVariable Long goodsId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("GET on /status/{}", goodsId);
        return new ResponseEntity<>(goodsService.findStatusesOfGoods(goodsId), HttpStatus.OK);
    }

    @RequestMapping(value = "/statuses", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<GoodsStatusName>> getStatusNames() throws DataAccessException {
        logger.info("GET on /statuses");
        List<GoodsStatusName> statuses = goodsService.getStatusNames();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StorageSpaceType>> getStorageTypesNames() throws DataAccessException {
        logger.info("GET on /storageTypes");
        List<StorageSpaceType> storageSpaceTypes = goodsService.getStorageSpaceTypes();
        return new ResponseEntity<>(storageSpaceTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/quant_units", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuantityUnit>> getQuantityUnitsNames() throws DataAccessException {
        logger.info("GET on /quant_units");
        List<QuantityUnit> units = goodsService.getQuantityUnits();
        return new ResponseEntity<>(units, HttpStatus.OK);
    }

    @RequestMapping(value = "/price_units", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PriceUnit>> getPriceUnitsNames() throws DataAccessException {
        logger.info("GET on /price_units");
        List<PriceUnit> units = goodsService.getPriceUnits();
        return new ResponseEntity<>(units, HttpStatus.OK);
    }

    @RequestMapping(value = "/weight_units", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<WeightUnit>> getWeightUnitsNames() throws DataAccessException {
        logger.info("GET on /price_units");
        List<WeightUnit> units = goodsService.getWeightUnits();
        return new ResponseEntity<>(units, HttpStatus.OK);
    }

    @RequestMapping(value = "/{invoiceId}/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<IdResponse> saveGoods(@PathVariable Long invoiceId, @Valid @RequestBody GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException, RequestHandlingException {
        logger.info("POST on /{}/save, request body: {}", invoiceId, goodsDTO);
        Goods savedGoods = goodsService.createGoods(invoiceId, goodsDTO);
        if (savedGoods != null) {
            return new ResponseEntity<>(new IdResponse(savedGoods.getId()), HttpStatus.CREATED);
        } else {
            throw new RequestHandlingException("Goods were not stored");
        }
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> updateGoods(@PathVariable(value = "id") Long id, @Valid @RequestBody GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("POST on /save/{}, request body: {}", id, goodsDTO);
        goodsService.updateGoods(id, goodsDTO);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> deleteGoods(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("DELETE on /delete/{}, request body: {}", id);
        goodsService.deleteGoods(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.DELETED), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/{warehouseId}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<GoodsDTO>> findGoods(@RequestParam(defaultValue = "-1") int page,
                                                    @RequestParam(defaultValue = "0") int count,
                                                    @PathVariable Long warehouseId,
                                                    @RequestBody GoodsSearchDTO searchDTO,
                                                    HttpServletResponse response) throws DataAccessException, IllegalParametersException {
        logger.info("GET on /search/{}, request body: {}", warehouseId, searchDTO);
        List<GoodsDTO> goods = goodsService.findGoodsForWarehouseByCriteria(warehouseId, searchDTO, (page - 1) * count, count);
        response.addHeader(HEADER_X_TOTAL_COUNT, String.valueOf(goodsService.getGoodsSearchResultCount(warehouseId, searchDTO)));
        response.addHeader(HEADER_EXPOSE_HEADERS, HEADER_X_TOTAL_COUNT);
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> setGoodsStatus(@PathVariable(value = "id") Long id, @RequestBody GoodsStatusDTO statusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("POST on /status/{}, request body: {}", id, statusDTO);
        goodsService.setGoodsStatus(id, GoodsStatusEnum.valueOf(statusDTO.getName()));
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/put", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> putGoodsIntoCell(@PathVariable(value = "id") Long id, @RequestBody GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /{}/put, request body: {}", id, goodsDTO);
        goodsService.putGoodsInCells(id, goodsDTO.getCells());
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> removeGoodsFromStorage(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("PUT on /remove/{}", id);
        goodsService.removeGoodsFromStorage(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }
}
