package com.gavoza.backend.domain.search.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class SearchLogDto {
    private String query;
    private Date searchTime;
}
