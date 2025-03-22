package com.example.adsservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsDto {
    private Integer id;
    private String title;
    private String description;
    private Double price;
    private Integer userId;
    private LocalDateTime createdAt;
}