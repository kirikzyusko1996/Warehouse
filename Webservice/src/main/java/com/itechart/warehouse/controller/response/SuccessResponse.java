package com.itechart.warehouse.controller.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Object representing response entity to successfully handled request.
 */
@Setter
@Getter
@ToString
public class SuccessResponse {
    private String message;

    public SuccessResponse() {
    }

    public SuccessResponse(String message) {
        this.message = message;
    }
}