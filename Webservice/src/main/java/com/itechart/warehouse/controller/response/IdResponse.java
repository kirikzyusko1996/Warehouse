package com.itechart.warehouse.controller.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Object representing response entity which includes only one field - id of created entity.
 *
 */
@Setter
@Getter
@ToString
public class IdResponse {
    private Long id;

    public IdResponse() {
    }

    public IdResponse(Long id) {
        this.id = id;
    }
}
