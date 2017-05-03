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
public class StatusResponse {
    private StatusEnum status;

    public StatusResponse() {
    }

    public StatusResponse(StatusEnum status) {
        this.status = status;
    }
}