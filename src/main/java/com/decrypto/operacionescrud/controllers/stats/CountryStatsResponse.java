package com.decrypto.operacionescrud.controllers.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class CountryStatsResponse {
    private String country;
    List<Map<String,MarketStatsResponse>> market;
}
