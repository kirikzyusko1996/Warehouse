package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.controller.error.RequestHandlingError;
import com.itechart.warehouse.controller.error.ValidationError;
import com.itechart.warehouse.controller.error.ValidationErrorBuilder;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

/**
 * REST controller for handling requests to goods service.
 */
@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping(value = "/goods")
@Validated
public class GoodsController {
    private GoodsService goodsService;
    private Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @RequestMapping(value = "/{warehouseId}/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsDTO>> getGoods(@RequestParam(defaultValue = "-1") int page,
                                                   @RequestParam(defaultValue = "0") int count,
                                                   @PathVariable Long warehouseId,
                                                   HttpServletResponse response) throws DataAccessException, IllegalParametersException {
        logger.info("Handling request for list of goods in warehouse with id {}, page: {}, count: {}", warehouseId, page, count);
        List<GoodsDTO> goods = null;
        goods = goodsService.findGoodsForWarehouse(warehouseId, (page - 1) * count, count);
        long goodsCount = goodsService.getGoodsCount(warehouseId);
        response.addHeader("X-total-count", String.valueOf(goodsCount));
        response.addHeader("Access-Control-Expose-Headers", "X-total-count");
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable Long goodsId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for goods with id {}", goodsId);
        return new ResponseEntity<>(goodsService.findGoodsDTOById(goodsId), HttpStatus.OK);
    }

    @RequestMapping(value = "status/{goodsId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoodsStatusDTO>> getStatusOfGoods(@PathVariable Long goodsId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for status of goods with id {}", goodsId);
        return new ResponseEntity<>(goodsService.findStatusesOfGoods(goodsId), HttpStatus.OK);
    }

    @RequestMapping(value = "/statuses", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<GoodsStatusName>> getStatusNames() throws DataAccessException {
        logger.info("Handling request for roles list");
        List<GoodsStatusName> statuses = goodsService.getStatusNames();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StorageSpaceType>> getStorageTypesNames() throws DataAccessException {
        logger.info("Handling request for storage space types list");
        List<StorageSpaceType> storageSpaceTypes = goodsService.getStorageSpaceTypes();
        return new ResponseEntity<>(storageSpaceTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/units", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Unit>> getUnitsNames() throws DataAccessException {
        logger.info("Handling request for units list");
        List<Unit> units = goodsService.getUnits();
        return new ResponseEntity<>(units, HttpStatus.OK);
    }

    @RequestMapping(value = "/{invoiceId}/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<IdResponse> saveGoods(@PathVariable Long invoiceId, @Valid @RequestBody GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException, RequestHandlingException {
        logger.info("Handling request for saving new goods using DTO: {} for invoice with id {}", goodsDTO, invoiceId);
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
        logger.info("Handling request for updating goods with id: {} by DTO: {}", id, goodsDTO);
        goodsService.updateGoods(id, goodsDTO);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> deleteGoods(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for deleting user with id: {}", id);
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
                                                    HttpServletResponse response) throws DataAccessException, IllegalParametersException, GenericDAOException {
        logger.info("Handling request for searching list of goods by DTO: {}, page: {}, count: {}", searchDTO, page, count);

        List<GoodsDTO> goods = goodsService.findGoodsForWarehouseByCriteria(warehouseId, searchDTO, (page - 1) * count, count);

        response.addHeader("X-total-count", String.valueOf(goodsService.getGoodsSearchResultCount(warehouseId, searchDTO)));
        response.addHeader("Access-Control-Expose-Headers", "X-total-count");

        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> setGoodsStatus(@PathVariable(value = "id") Long id, @RequestBody GoodsStatusDTO statusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for setting status {} to goods with id: {}", statusDTO, id);
        goodsService.setGoodsStatus(id, statusDTO);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/put", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> putGoodsIntoCell(@PathVariable(value = "id") Long id, @RequestBody GoodsDTO goods) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for putting goods {} with id {} into storage cells", goods, id);
        goodsService.putGoodsInCells(id, goods.getCells());
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatusResponse> removeGoodsFromStorage(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for removing goods with id {} from storage", id);
        goodsService.removeGoodsFromStorage(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    ValidationError handleException(MethodArgumentNotValidException e) {
        return createValidationError(e);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    RequestHandlingError handleException(HttpMessageNotReadableException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError illegalParametersError = new RequestHandlingError();
        illegalParametersError.setError("Message is syntactically incorrect");
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

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public
    @ResponseBody
    RequestHandlingError handleException(AccessDeniedException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError requestHandlingError = new RequestHandlingError();
        requestHandlingError.setError(e.getMessage());
        return requestHandlingError;
    }

}
