package org.acme.reservation.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Car {

    private Long id;
    private String licensePlateNumber;
    private String manufacturer;
    private String model;

}