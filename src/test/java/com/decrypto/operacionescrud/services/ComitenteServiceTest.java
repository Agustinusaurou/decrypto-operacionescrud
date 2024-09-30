package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.comitente.ComitenteDTO;
import com.decrypto.operacionescrud.controllers.comitente.ComitenteModel;
import com.decrypto.operacionescrud.controllers.comitente.ComitentesResponse;
import com.decrypto.operacionescrud.entities.Comitente;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import com.decrypto.operacionescrud.reposiroties.ComitenteRepository;
import com.decrypto.operacionescrud.reposiroties.MercadoRepository;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComitenteServiceTest {
    @Mock
    private ComitenteRepository comitenteRepository;
    @Mock
    private MercadoRepository mercadoRepository;
    @InjectMocks
    private ComitenteService sut;

    @DisplayName("findAll")
    @Nested
    class FindAllTest {
        @DisplayName("returns left")
        @Nested
        class Left {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findAll_fails() {
                doThrow(new RuntimeException()).when(comitenteRepository).findAll();
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findAll();
                verify(comitenteRepository).findAll();

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_empty_list_when_comitenteRepository_findAll_return_empty() {
                when(comitenteRepository.findAll()).thenReturn(Collections.emptyList());
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findAll();
                verify(comitenteRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getComitentes()).hasSize(0);
            }

            @Test
            void with_list_when_comitenteRepository_findAll_return_comitentes() {
                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .nombre("NOMBRE")
                    .build();

                List<Comitente> comitentes = Arrays.asList(comitente);

                when(comitenteRepository.findAll()).thenReturn(comitentes);
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findAll();
                verify(comitenteRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getComitentes()).hasSize(1);
                assertThat(actual.getRight().getComitentes().get(0).getId()).isEqualTo(1L);
            }
        }
    }

    @DisplayName("save")
    @Nested
    class saveTest {
        @DisplayName("returns optional left")
        @Nested
        class OptionalLeft {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findByIdentificacionAndAndTipoIdentificacion_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .build();

                doThrow(new RuntimeException()).when(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class));
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_COMITENTE_EXIST_when_comitenteRepository_findByIdentificacionAndAndTipoIdentificacion_return_comitente() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .build();

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.of(comitente));
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.COMITENTE_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findMercadosByIdIn_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
                    .build();

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.empty());
                doThrow(new RuntimeException()).when(mercadoRepository).findMercadosByIdIn(anySet());
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);
                verify(mercadoRepository).findMercadosByIdIn(dto.getMercadosIds());

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_save_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
                    .build();

                Comitente comitente = Comitente.builder().build();

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DSCRIPTION")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .build();

                Set<Mercado> mercados = new HashSet<>(Arrays.asList(mercado));

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.empty());
                when(mercadoRepository.findMercadosByIdIn(anySet()))
                    .thenReturn(mercados);
                when(comitenteRepository.save(any(Comitente.class))).thenThrow(new RuntimeException());
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);
                verify(mercadoRepository).findMercadosByIdIn(dto.getMercadosIds());
                verify(comitenteRepository).save(any(Comitente.class));

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_saveAll_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
                    .build();

                Comitente comitente = Comitente.builder().build();

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DSCRIPTION")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .comitentes(new HashSet<>())
                    .build();

                Set<Mercado> mercados = new HashSet<>(Arrays.asList(mercado));

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.empty());
                when(mercadoRepository.findMercadosByIdIn(anySet()))
                    .thenReturn(mercados);
                when(comitenteRepository.save(any(Comitente.class)))
                    .thenReturn(comitente);
                when(mercadoRepository.saveAll(anySet())).thenThrow(new RuntimeException());
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);
                verify(mercadoRepository).findMercadosByIdIn(dto.getMercadosIds());
                verify(comitenteRepository).save(any(Comitente.class));
                verify(mercadoRepository).saveAll(anySet());

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_mercadoRepository_findMercadosByIdIn_return_empty_set_and_never_fail() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
                    .build();

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.empty());
                when(mercadoRepository.findMercadosByIdIn(anySet()))
                    .thenReturn(new HashSet<>(Collections.emptyList()));
                when(comitenteRepository.save(any(Comitente.class)))
                    .thenReturn(comitente);
                when(mercadoRepository.saveAll(anySet())).thenReturn(Collections.emptyList());
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);
                verify(mercadoRepository).findMercadosByIdIn(dto.getMercadosIds());
                verify(comitenteRepository).save(any(Comitente.class));
                verify(mercadoRepository).saveAll(anySet());

                assertThat(actual.isPresent()).isFalse();
            }

            @Test
            void with_list_when_comitenteRepository_findAll_return_comitentes_and_never_fail() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .identificacion("1234")
                    .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
                    .build();

                Comitente comitente = Comitente.builder().build();

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DSCRIPTION")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .comitentes(new HashSet<>())
                    .build();

                Set<Mercado> mercados = new HashSet<>(Arrays.asList(mercado));

                when(comitenteRepository.findByIdentificacionAndAndTipoIdentificacion(anyString(), any(TipoIdentificador.class)))
                    .thenReturn(Optional.empty());
                when(mercadoRepository.findMercadosByIdIn(anySet()))
                    .thenReturn(mercados);
                when(comitenteRepository.save(any(Comitente.class)))
                    .thenReturn(comitente);
                when(mercadoRepository.saveAll(anySet())).thenReturn(Collections.emptyList());
                Optional<ComitenteService.Left> actual = sut.save(dto);

                verify(comitenteRepository).findByIdentificacionAndAndTipoIdentificacion("1234", TipoIdentificador.DNI);
                verify(mercadoRepository).findMercadosByIdIn(dto.getMercadosIds());
                verify(comitenteRepository).save(any(Comitente.class));
                verify(mercadoRepository).saveAll(anySet());

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }

    @DisplayName("update")
    @Nested
    class UpdateTest {
        @DisplayName("returns optional left")
        @Nested
        class OptionalLeft {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findById_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                doThrow(new RuntimeException()).when(comitenteRepository).findById(anyLong());
                Optional<ComitenteService.Left> actual = sut.update(dto);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_COMITENTE_NOT_EXIST_when_comitenteRepository_findById_return_optional_empty() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<ComitenteService.Left> actual = sut.update(dto);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.COMITENTE_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_save_fails() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                doThrow(new RuntimeException()).when(comitenteRepository).save(any(Comitente.class));

                Optional<ComitenteService.Left> actual = sut.update(dto);
                verify(comitenteRepository).findById(1L);
                verify(comitenteRepository).save(comitente);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                ComitenteDTO dto = ComitenteDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                when(comitenteRepository.save(any(Comitente.class))).thenReturn(comitente);

                Optional<ComitenteService.Left> actual = sut.update(dto);
                verify(comitenteRepository).findById(1L);
                verify(comitenteRepository).save(comitente);

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }

    @DisplayName("delete")
    @Nested
    class DeleteTest {
        @DisplayName("returns optional left")
        @Nested
        class OptionalLeft {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(comitenteRepository).findById(anyLong());
                Optional<ComitenteService.Left> actual = sut.delete(id);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_COMITENTE_NOT_EXIST_when_comitenteRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<ComitenteService.Left> actual = sut.delete(id);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.COMITENTE_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_delete_fails() {
                Long id = 1L;

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                doThrow(new RuntimeException()).when(comitenteRepository).delete(any(Comitente.class));

                Optional<ComitenteService.Left> actual = sut.delete(id);
                verify(comitenteRepository).findById(1L);
                verify(comitenteRepository).delete(comitente);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                Long id = 1L;

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                doNothing().when(comitenteRepository).delete(any(Comitente.class));

                Optional<ComitenteService.Left> actual = sut.delete(id);
                verify(comitenteRepository).findById(1L);
                verify(comitenteRepository).delete(comitente);

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }

    @DisplayName("findById")
    @Nested
    class FindByIdTest {
        @DisplayName("returns left")
        @Nested
        class Left {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(comitenteRepository).findById(anyLong());
                Either<ComitenteService.Left, ComitenteModel> actual = sut.findById(id);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_COMITENTE_NOT_EXIST_when_comitenteRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.empty());
                Either<ComitenteService.Left, ComitenteModel> actual = sut.findById(id);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(ComitenteService.Left.COMITENTE_NOT_EXIST);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_ComitenteModel_when_comitenteRepository_findById_optional_with_value() {
                Long id = 1L;

                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .nombre("NOMBRE")
                    .identificacion("1234")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPTION")
                    .build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                Either<ComitenteService.Left, ComitenteModel> actual = sut.findById(id);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getId()).isEqualTo(1L);
                assertThat(actual.getRight().getNombre()).isEqualTo("NOMBRE");
                assertThat(actual.getRight().getDescription()).isEqualTo("DESCRIPTION");
                assertThat(actual.getRight().getIdentificacion()).isEqualTo("1234");
                assertThat(actual.getRight().getTipoIdentificacion()).isEqualTo(TipoIdentificador.DNI);
            }
        }
    }

    @DisplayName("findComitentesByMercado")
    @Nested
    class FindComitentesByMercadoTest {
        @DisplayName("returns left")
        @Nested
        class Left {
            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findByCodigo_fails() {
                String code = "CODE";

                doThrow(new RuntimeException()).when(mercadoRepository).findByCodigo(anyString());
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findComitentesByMercado(code);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_NOT_EXIST_when_mercadoRepository_findByCodigo_return_optional_empty() {
                String code = "CODE";

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findComitentesByMercado(code);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(ComitenteService.Left.MERCADO_NOT_EXIST);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_empty_list_when_never_fail_and_mercado_has_not_comitentes() {
                String code = "CODE";

                Mercado mercado = Mercado.builder()
                    .comitentes(new HashSet<>())
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mercado));
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findComitentesByMercado(code);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getComitentes()).hasSize(0);
            }

            @Test
            void with_comitente_list_when_never_fail_and_mercado_has_comitentes() {
                String code = "CODE";

                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .nombre("NOMBRE")
                    .identificacion("1234")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPTION")
                    .build();

                Set<Comitente> comitentes = new HashSet<>(Arrays.asList(comitente));

                Mercado mercado = Mercado.builder()
                    .comitentes(comitentes)
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mercado));
                Either<ComitenteService.Left, ComitentesResponse> actual = sut.findComitentesByMercado(code);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getComitentes()).hasSize(1);
                assertThat(actual.getRight().getComitentes().get(0).getId()).isEqualTo(1L);
                assertThat(actual.getRight().getComitentes().get(0).getNombre()).isEqualTo("NOMBRE");
                assertThat(actual.getRight().getComitentes().get(0).getIdentificacion()).isEqualTo("1234");
                assertThat(actual.getRight().getComitentes().get(0).getTipoIdentificacion()).isEqualTo(TipoIdentificador.DNI);
                assertThat(actual.getRight().getComitentes().get(0).getDescription()).isEqualTo("DESCRIPTION");
            }
        }
    }

    @DisplayName("saveInMercado")
    @Nested
    class SaveInMercadoTest {
        @DisplayName("returns optional left")
        @Nested
        class OptionalLeft {
            @Test
            void with_UNEXPECTED_ERROR_when_comitenteRepository_findById_fails() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                doThrow(new RuntimeException()).when(comitenteRepository).findById(anyLong());
                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_COMITENTE_NOT_EXIST_when_comitenteRepository_findById_return_optional_empty() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.COMITENTE_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findByCodigo_fails() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                doThrow(new RuntimeException()).when(mercadoRepository).findByCodigo(anyString());

                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_NOT_EXIST_when_mercadoRepository_findByCodigo_return_optional_empty() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                Comitente comitente = Comitente.builder().build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());

                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.MERCADO_NOT_EXIST);
            }

            @Test
            void with_COMITENTE_EXIST_when_mercado_contain_comitente() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .build();

                Mercado mercado = Mercado.builder()
                    .id(2L)
                    .comitentes(new HashSet<>(Arrays.asList(comitente)))
                    .build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mercado));

                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);
                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.COMITENTE_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_save_fails() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .build();

                Mercado mercado = Mercado.builder()
                    .id(2L)
                    .comitentes(new HashSet<>())
                    .build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mercado));
                doThrow(new RuntimeException()).when(mercadoRepository).save(any(Mercado.class));

                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);
                verify(mercadoRepository).findByCodigo("CODE");
                verify(mercadoRepository).save(mercado);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(ComitenteService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                Long idComitente = 1L;
                String codeMercado = "CODE";

                Comitente comitente = Comitente.builder()
                    .id(1L)
                    .build();

                Mercado mercado = Mercado.builder()
                    .id(2L)
                    .comitentes(new HashSet<>())
                    .build();

                when(comitenteRepository.findById(anyLong())).thenReturn(Optional.of(comitente));
                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mercado));
                when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);

                Optional<ComitenteService.Left> actual = sut.saveInMercado(idComitente, codeMercado);
                verify(comitenteRepository).findById(1L);
                verify(mercadoRepository).findByCodigo("CODE");
                verify(mercadoRepository).save(mercado);

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }
}
