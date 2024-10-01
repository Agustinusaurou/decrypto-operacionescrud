package com.decrypto.operacionescrud.controllers.stats;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.services.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private StatsService statsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUpForEachTest() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new StatsController(statsService))
            .build();
    }

    @DisplayName("getStats")
    @Nested
    class GetStatsTest {
        @DisplayName("when service returns left")
        @Nested
        class LeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Either<StatsService.Left, List<CountryStatsResponse>> left = Either.left(StatsService.Left.UNEXPECTED_ERROR);
                when(statsService.getComitenteStats()).thenReturn(left);

                mockMvc.perform(get("/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_THERE_ARE_NOT_MERCADOS_then_preconditionFail() throws Exception {
                Either<StatsService.Left, List<CountryStatsResponse>> left = Either.left(StatsService.Left.MERCADOS_NOT_EXIST);
                when(statsService.getComitenteStats()).thenReturn(left);

                mockMvc.perform(get("/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_ComitentesResponse_not_empty_then_return_Respons_Entity_with_ComitentesResponse() throws Exception {
                MarketStatsResponse statsCodeA = MarketStatsResponse.builder()
                    .percentage("24,50")
                    .build();
                MarketStatsResponse statsCodeB = MarketStatsResponse.builder()
                    .percentage("50,50")
                    .build();
                MarketStatsResponse statsCodeC = MarketStatsResponse.builder()
                    .percentage("25,00")
                    .build();

                Map<String, MarketStatsResponse> mapMarketStats = new HashMap<>();
                mapMarketStats.put("A", statsCodeA);
                mapMarketStats.put("B", statsCodeB);
                mapMarketStats.put("C", statsCodeC);

                CountryStatsResponse countryStatsResponse = CountryStatsResponse.builder()
                    .country(PaisAdmitido.ARGENTINA.getDescription())
                    .market(Arrays.asList(mapMarketStats))
                    .build();

                List<CountryStatsResponse> responseList = new ArrayList<>();
                responseList.add(countryStatsResponse);

                Either<StatsService.Left, List<CountryStatsResponse>> right = Either.right(responseList);
                when(statsService.getComitenteStats()).thenReturn(right);

                mockMvc.perform(get("/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.[0].country"), is(PaisAdmitido.ARGENTINA.getDescription())))
                    .andExpect(jsonPath(("$.[0].market[0].A.percentage"), is("24,50")))
                    .andExpect(jsonPath(("$.[0].market[0].B.percentage"), is("50,50")))
                    .andExpect(jsonPath(("$.[0].market[0].C.percentage"), is("25,00")));
            }
        }
    }
}
