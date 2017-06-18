package com.itechart.warehouse.query;

import com.itechart.warehouse.constants.ActTypeEnum;
import com.itechart.warehouse.constants.GoodsStatusEnum;
import com.itechart.warehouse.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Constructor for queries used for searching goods by different parameters.
 */
public class GoodsSearchQueryBuilder {
    private QueryBuilder builder;
    private GoodsSearchCriteria criteria;
    private Long warehouseId;
    private Map<String, Object> parameters;
    private String query;
    private int statusRestrictionCounter = -1;
    private int currentStatusJoinNumber = -1;

    private enum Order {
        ASC, DESC;
    }

    private static final String ROOT_QUERY_LIST = "SELECT DISTINCT goods FROM Goods goods";
    private static final String ROOT_QUERY_COUNT = "SELECT count(DISTINCT goods.id) FROM Goods goods";

    public GoodsSearchQueryBuilder(Long warehouseId, GoodsSearchCriteria criteria) {
        this.warehouseId = warehouseId;
        this.criteria = criteria;
        this.parameters = new HashMap<>();
        this.builder = new QueryBuilder();
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void buildListQuery() {
        buildQuery(ROOT_QUERY_LIST, true);
    }

    public void buildCountQuery() {
        buildQuery(ROOT_QUERY_COUNT, false);
    }

    private void buildQuery(String root, boolean doOrdering) {
        if (criteria == null) {
            return;
        }

        this.builder.setRoot(new StringBuilder(root));

        this.addWarehouseRestriction(warehouseId)
                .addCurrentStatusRestriction(criteria.getCurrentStatus())
                .addNameRestriction(criteria.getName())
                .addIncomingInvoiceRestriction(criteria.getIncomingInvoiceId())
                .addOutgoingInvoiceRestriction(criteria.getOutgoingInvoiceId())
                .addQuantityRestriction(criteria.getMinQuantity(), criteria.getMaxQuantity(), criteria.getQuantityUnit())
                .addWeightRestriction(criteria.getMinWeight(), criteria.getMaxWeight(), criteria.getWeightUnit())
                .addPriceRestriction(criteria.getMinPrice(), criteria.getMaxPrice(), criteria.getPriceUnit())
                .addStorageTypeRestriction(criteria.getStorageType())
                .addIgnoreDeletedRestriction();

        if (doOrdering) {
            addOrdering(Order.DESC);
        }

        if (criteria.getActApplicable() != null && criteria.getActApplicable()) {
            addActApplicableRestriction(criteria.getActType());
        }

        if (CollectionUtils.isNotEmpty(criteria.getStatuses())) {
            criteria.getStatuses().forEach(this::addStatusRestriction);
        }

        this.query = this.builder.build();
    }

    private GoodsSearchQueryBuilder addNameRestriction(String name) {
        if (StringUtils.isNotBlank(name)) {
            builder.addRestriction("goods.name LIKE :goodsName");
            parameters.put("goodsName", "%" + name + "%");
        }
        return this;
    }

    private GoodsSearchQueryBuilder addQuantityRestriction(BigDecimal min, BigDecimal max, QuantityUnit unit) {
        if (min != null) {
            builder.addRestriction("goods.quantity >= :minGoodsQuantity");
            parameters.put("minGoodsQuantity", min);
        }
        if (max != null) {
            builder.addRestriction("goods.quantity <= :maxGoodsQuantity");
            parameters.put("maxGoodsQuantity", max);
        }
        if (unit != null) {
            builder.addRestriction("goods.quantityUnit = :goodsQuantityUnit");
            parameters.put("goodsQuantityUnit", unit);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addWeightRestriction(BigDecimal min, BigDecimal max, WeightUnit unit) {
        if (min != null) {
            builder.addRestriction("goods.weight >= :minGoodsWeight");
            parameters.put("minGoodsWeight", min);
        }
        if (max != null) {
            builder.addRestriction("goods.weight <= :maxGoodsWeight");
            parameters.put("maxGoodsWeight", max);
        }
        if (unit != null) {
            builder.addRestriction("goods.weightUnit = :goodsWeightUnit");
            parameters.put("goodsWeightUnit", unit);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addPriceRestriction(BigDecimal min, BigDecimal max, PriceUnit unit) {
        if (min != null) {
            builder.addRestriction("goods.price >= :minGoodsPrice");
            parameters.put("minGoodsPrice", min);
        }
        if (max != null) {
            builder.addRestriction("goods.price <= :maxGoodsPrice");
            parameters.put("maxGoodsPrice", max);
        }
        if (unit != null) {
            builder.addRestriction("goods.priceUnit = :goodsPriceUnit");
            parameters.put("goodsPriceUnit", unit);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addCurrentStatusRestriction(GoodsStatusName statusName) {
        if (criteria.getCurrentStatus() != null) {
            if (this.currentStatusJoinNumber == -1) {
                this.currentStatusJoinNumber = ++statusRestrictionCounter;
            }
            builder.addJoin("INNER JOIN GoodsStatus status_" + statusRestrictionCounter + " ON status_" + statusRestrictionCounter + " = goods.currentStatus");
            builder.addRestriction("status_" + statusRestrictionCounter + ".goodsStatusName = :statusName");
            parameters.put("statusName", statusName);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addActApplicableRestriction(String actType) {
        if (this.currentStatusJoinNumber == -1) {
            this.currentStatusJoinNumber = ++statusRestrictionCounter;
            builder.addJoin("INNER JOIN GoodsStatus status_" + statusRestrictionCounter + " ON status_" + statusRestrictionCounter + " = goods.currentStatus");
            builder.addJoin("INNER JOIN GoodsStatusName statusName ON status_" + currentStatusJoinNumber + ".goodsStatusName = statusName");
        }

        String QUERY_BEGIN = "(statusName.name = ";
        String QUERY_MIDDLE = " OR statusName.name = ";
        String QUERY_END = ")";

        if (StringUtils.isNotBlank(criteria.getActType())) {
            switch (ActTypeEnum.valueOf(actType)) {
                case ACT_OF_LOSS:
                case ACT_OF_THEFT:
                case WRITE_OFF_ACT:
                    builder.addRestriction(QUERY_BEGIN + getStatusName(GoodsStatusEnum.STORED) + QUERY_MIDDLE + getStatusName(GoodsStatusEnum.WITHDRAWN) + QUERY_END);
                    break;
                case MISMATCH_ACT:
                    builder.addRestriction(QUERY_BEGIN + getStatusName(GoodsStatusEnum.REGISTERED) + QUERY_END);
                    break;
                default:
                    break;
            }
        } else {
            builder.addRestriction(QUERY_BEGIN + getStatusName(GoodsStatusEnum.REGISTERED) + QUERY_MIDDLE + getStatusName(GoodsStatusEnum.STORED) + QUERY_MIDDLE + getStatusName(GoodsStatusEnum.WITHDRAWN) + QUERY_END);
            builder.addRestriction("statusName.name IS NOT NULL");
        }
        return this;
    }

    private String getStatusName(GoodsStatusEnum status) {
        Assert.notNull(status, "Status is null");
        return "'" + status.toString() + "'";
    }

    private GoodsSearchQueryBuilder addWarehouseRestriction(Long warehouseId) {
        builder.addJoin("INNER JOIN Warehouse warehouse ON goods.warehouse = warehouse");
        builder.addRestriction("warehouse.idWarehouse = :warehouseId");
        parameters.put("warehouseId", warehouseId);
        return this;
    }


    private GoodsSearchQueryBuilder addStatusRestriction(GoodsStatusSearchCriteria statusCriteria) {
        if (statusCriteria.getName() == null) {
            return this;
        }
        statusRestrictionCounter++;
        builder.addJoin("INNER JOIN GoodsStatus status_" + statusRestrictionCounter + " ON status_" + statusRestrictionCounter + ".goods = goods");
        if (statusCriteria.getName() != null) {
            builder.addRestriction("status_" + statusRestrictionCounter + ".goodsStatusName = :statusName_" + statusRestrictionCounter);
            parameters.put("statusName_" + statusRestrictionCounter, statusCriteria.getName());
        }
        if (statusCriteria.getFromDate() != null) {
            builder.addRestriction("status_" + statusRestrictionCounter + ".date >= :statusFromDate_" + statusRestrictionCounter);
            parameters.put("statusFromDate_" + statusRestrictionCounter, statusCriteria.getFromDate());
        }
        if (statusCriteria.getToDate() != null) {
            builder.addRestriction("status_" + statusRestrictionCounter + ".date <= :statusToDate_" + statusRestrictionCounter);
            parameters.put("statusToDate_" + statusRestrictionCounter, new Timestamp(new DateTime(statusCriteria.getToDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate().getTime()));
        }
        if (StringUtils.isNotBlank(statusCriteria.getUserFirstName()) || StringUtils.isNotBlank(statusCriteria.getUserLastName()) || StringUtils.isNotBlank(statusCriteria.getUserPatronymic())) {
            builder.addJoin("INNER JOIN User user_" + statusRestrictionCounter + " ON status_" + statusRestrictionCounter + ".user = user_" + statusRestrictionCounter);
        }
        if (StringUtils.isNotBlank(statusCriteria.getUserFirstName())) {
            builder.addRestriction("user_" + statusRestrictionCounter + ".firstName LIKE :statusUserFirstName_" + statusRestrictionCounter);
            parameters.put("statusUserFirstName_" + statusRestrictionCounter, "%" + statusCriteria.getUserFirstName() + "%");
        }
        if (StringUtils.isNotBlank(statusCriteria.getUserLastName())) {
            builder.addRestriction("user_" + statusRestrictionCounter + ".lastName LIKE :statusUserLastName_" + statusRestrictionCounter);
            parameters.put("statusUserLastName_" + statusRestrictionCounter, "%" + statusCriteria.getUserLastName() + "%");
        }
        if (StringUtils.isNotBlank(statusCriteria.getUserPatronymic())) {
            builder.addRestriction("user_" + statusRestrictionCounter + ".patronymic LIKE :statusUserPatronymic_" + statusRestrictionCounter);
            parameters.put("statusUserPatronymic_" + statusRestrictionCounter, "%" + statusCriteria.getUserPatronymic() + "%");
        }
        return this;
    }

    private GoodsSearchQueryBuilder addIncomingInvoiceRestriction(Long invoiceId) {
        if (invoiceId != null) {
            builder.addJoin("INNER JOIN Invoice incomingInvoice ON goods.incomingInvoice = incomingInvoice");
            builder.addRestriction("incomingInvoice.id = :incomingInvoiceId");
            parameters.put("incomingInvoiceId", invoiceId);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addOutgoingInvoiceRestriction(Long invoiceId) {
        if (invoiceId != null) {
            builder.addRestriction("outgoingInvoice.id = :outgoingInvoice");
            builder.addJoin("INNER JOIN Invoice outgoingInvoice ON goods.outgoingInvoice = outgoingInvoice");
            parameters.put("outgoingInvoice", invoiceId);
        }
        return this;
    }

    private GoodsSearchQueryBuilder addOrdering(Order order) {
        builder.addOrderBy("ORDER BY goods.id " + order.toString());
        return this;
    }


    private GoodsSearchQueryBuilder addIgnoreDeletedRestriction() {
        builder.addRestriction("goods.deleted IS NULL");
        return this;
    }

    private GoodsSearchQueryBuilder addStorageTypeRestriction(StorageSpaceType type) {
        if (type != null) {
            builder.addRestriction("goods.storageType = :goodsStorageType");
            parameters.put("goodsStorageType", criteria.getStorageType());
        }
        return this;
    }
}
