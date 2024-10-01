package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.stats.CountryStatsResponse;
import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import com.decrypto.operacionescrud.reposiroties.ComitenteRepository;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
import com.decrypto.operacionescrud.reposiroties.PaisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @Mock
    private ComitenteRepository comitenteRepository;
    @Mock
    private MercadoRepository mercadoRepository;
    @Mock
    private PaisRepository paisRepository;
    @InjectMocks
    private StatsService sut;

    @DisplayName("getComitenteStats")
    @Nested
    class GetComitenteStatsTest {
        @DisplayName("returns left")
        @Nested
        class Left {


            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findAll_fails() {
                doThrow(new RuntimeException()).when(mercadoRepository).findAll();
                Either<StatsService.Left, List<CountryStatsResponse>> actual = sut.getComitenteStats();
                verify(mercadoRepository).findAll();

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(StatsService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADOS_NOT_EXIST_when_mercadoRepository_findAll_return_empty_list() {
                when(mercadoRepository.findAll()).thenReturn(Collections.emptyList());
                Either<StatsService.Left, List<CountryStatsResponse>> actual = sut.getComitenteStats();
                verify(mercadoRepository).findAll();

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(StatsService.Left.MERCADOS_NOT_EXIST);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_stats_when_never_fail() {
                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .nombre("NOMBRE")
                    .identificacion("1234")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPTION")
                    .build();
                List<Comitente> comitentes = Arrays.asList(comitente);

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DSCRIPTION")
                    .pais(Pais.builder().id(1L).nombre(PaisAdmitido.ARGENTINA).build())
                    .comitentes(new HashSet<>(comitentes))
                    .build();
                List<Mercado> mercados = Arrays.asList(mercado);

                when(mercadoRepository.findAll()).thenReturn(mercados);

                Either<StatsService.Left, List<CountryStatsResponse>> actual = sut.getComitenteStats();
                verify(mercadoRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight()).hasSize(1);
                assertThat(actual.getRight().get(0).getCountry()).isEqualTo(PaisAdmitido.ARGENTINA.getDescription());
                assertThat(actual.getRight().get(0).getMarket()).hasSize(1);
                assertThat(actual.getRight().get(0).getMarket().get(0).get("CODE").getPercentage()).isEqualTo("100,00");
            }
        }
    }
}
