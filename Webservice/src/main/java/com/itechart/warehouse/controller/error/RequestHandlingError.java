package com.itechart.warehouse.controller.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing object returned by controller
 * when result occur during request handling.
 */
@Setter
@Getter
@ToString
public class RequestHandlingError {
    private String error;
}