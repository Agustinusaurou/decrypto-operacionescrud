package com.decrypto.operacionescrud.controllers.stats.mapper;

import com.decrypto.operacionescrud.controllers.stats.MarketStatsResponse;
import com.decrypto.operacionescrud.entities.Mercado;
import org.springframework.stereotype.Component;

@Component
public class StatsMapper {
    public static MarketStatsResponse createModel(Mercado mercado, Integer totalComitentes) {
        return MarketStatsResponse.builder()
            .percentage(String.format("%.2f", ((mercado.getComitentes().size() * 100.0) / totalComitentes)))
            .build();
    }
}
