package com.example.software.data.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

        private int id;
        private String country;
        private String city;
        private String place;
        private double latitude;
        private double longitude;
}
