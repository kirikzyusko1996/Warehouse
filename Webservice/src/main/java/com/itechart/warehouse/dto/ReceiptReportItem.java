package com.itechart.warehouse.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Alexey on 26.04.2017.
 */
public class ReceiptReportItem {
    private String userName;
    private String date;
    private String goodsName;
    private String quantity;
    private String shipperName;
    private String senderName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
