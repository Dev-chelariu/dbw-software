package com.example.software.data.entity;

import com.example.software.data.entity.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple DTO class for the inbox list to demonstrate complex object data
 */
@Getter
@Setter
@NoArgsConstructor
public class ServiceHealth {

    private Status status;

    private String service;

    private int input;

    private int output;

    private String theme;

    public ServiceHealth(Status status, String service, int input, int output) {
        this.status = status;
        this.service = service;
        this.input = input;
        this.output = output;
    }
}
