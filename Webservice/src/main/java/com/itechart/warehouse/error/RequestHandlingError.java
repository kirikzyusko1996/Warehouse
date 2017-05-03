package com.itechart.warehouse.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing object returned by controller
 * when exception occur during request handling.
 */
@Setter
@Getter
@ToString
public class RequestHandlingError {
    private String error;
}