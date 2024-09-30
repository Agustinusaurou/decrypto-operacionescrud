package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.mercado.MercadoDTO;
import com.decrypto.operacionescrud.controllers.mercado.MercadoModel;
import com.decrypto.operacionescrud.controllers.mercado.MercadosResponse;
import com.decrypto.operacionescrud.entities.Mercado;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MercadoServiceTest {
    @Mock
    private MercadoRepository mercadoRepository;
    @Mock
    private PaisRepository paisRepository;
    @InjectMocks
    private MercadoService sut;

    @DisplayName("findAll")
    @Nested
    class FindAllTest {
        @DisplayName("returns left")
        @Nested
        class Left {
            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findAll_fails() {
                doThrow(new RuntimeException()).when(mercadoRepository).findAll();
                Either<MercadoService.Left, MercadosResponse> actual = sut.findAll();
                verify(mercadoRepository).findAll();

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_empty_list_when_mercadoRepository_findAll_return_empty() {
                when(mercadoRepository.findAll()).thenReturn(Collections.emptyList());
                Either<MercadoService.Left, MercadosResponse> actual = sut.findAll();
                verify(mercadoRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getMercados()).hasSize(0);
            }

            @Test
            void with_list_when_mercadoRepository_findAll_return_mercados() {
                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .comitentes(new HashSet<>())
                    .build();

                List<Mercado> mercados = Arrays.asList(mercado);

                when(mercadoRepository.findAll()).thenReturn(mercados);
                Either<MercadoService.Left, MercadosResponse> actual = sut.findAll();
                verify(mercadoRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getMercados()).hasSize(1);
                assertThat(actual.getRight().getMercados().get(0).getId()).isEqualTo(1L);
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
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(mercadoRepository).findById(anyLong());
                Either<MercadoService.Left, MercadoModel> actual = sut.findById(id);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_NOT_EXIST_when_mercadoRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.empty());
                Either<MercadoService.Left, MercadoModel> actual = sut.findById(id);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(MercadoService.Left.MERCADO_NOT_EXIST);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_MercadoModel_when_mercadoRepository_findById_optional_with_value() {
                Long id = 1L;

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DESCRIPTION")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .comitentes(new HashSet<>())
                    .build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.of(mercado));
                Either<MercadoService.Left, MercadoModel> actual = sut.findById(id);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getId()).isEqualTo(1L);
                assertThat(actual.getRight().getCodigo()).isEqualTo("CODE");
                assertThat(actual.getRight().getDescription()).isEqualTo("DESCRIPTION");
                assertThat(actual.getRight().getPais().name()).isEqualTo(PaisAdmitido.ARGENTINA.name());
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
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findByCodigo_fails() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                doThrow(new RuntimeException()).when(mercadoRepository).findByCodigo(anyString());
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_EXIST_when_mercadoRepository_findByCodigo_return_mercado() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                Mercado mercado = Mercado.builder().build();

                when(mercadoRepository.findByCodigo(anyString()))
                    .thenReturn(Optional.of(mercado));
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.MERCADO_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_paisRepository_findByNombre_fails() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
                doThrow(new RuntimeException()).when(paisRepository).findByNombre(any(PaisAdmitido.class));
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");
                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_PAIS_NOT_EXIST_when_paisRepository_findByNombre_return_optional_empty() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
                when(paisRepository.findByNombre(any(PaisAdmitido.class))).thenReturn(Optional.empty());
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");
                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.PAIS_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_save_fails() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                Pais pais = Pais.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
                when(paisRepository.findByNombre(any(PaisAdmitido.class))).thenReturn(Optional.of(pais));
                doThrow(new RuntimeException()).when(mercadoRepository).save(any(Mercado.class));
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");
                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);
                verify(mercadoRepository).save(any(Mercado.class));

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                MercadoDTO dto = MercadoDTO.builder()
                    .codigo("CODE")
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPTION")
                    .build();

                Mercado mercado = Mercado.builder()
                    .id(1L)
                    .codigo("CODE")
                    .description("DSCRIPTION")
                    .pais(Pais.builder().nombre(PaisAdmitido.ARGENTINA).build())
                    .build();

                Pais pais = Pais.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                when(mercadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
                when(paisRepository.findByNombre(any(PaisAdmitido.class))).thenReturn(Optional.of(pais));
                when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);
                Optional<MercadoService.Left> actual = sut.save(dto);

                verify(mercadoRepository).findByCodigo("CODE");
                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);
                verify(mercadoRepository).save(any(Mercado.class));

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
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findById_fails() {
                MercadoDTO dto = MercadoDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                doThrow(new RuntimeException()).when(mercadoRepository).findById(anyLong());
                Optional<MercadoService.Left> actual = sut.update(dto);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_NOT_EXIST_when_mercadoRepository_findById_return_optional_empty() {
                MercadoDTO dto = MercadoDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<MercadoService.Left> actual = sut.update(dto);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.MERCADO_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_save_fails() {
                MercadoDTO dto = MercadoDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                Mercado mercado = Mercado.builder().build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.of(mercado));
                doThrow(new RuntimeException()).when(mercadoRepository).save(any(Mercado.class));
                Optional<MercadoService.Left> actual = sut.update(dto);
                verify(mercadoRepository).findById(1L);
                verify(mercadoRepository).save(mercado);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                MercadoDTO dto = MercadoDTO.builder()
                    .id(1L)
                    .description("DESCRIPTION")
                    .build();

                Mercado mercado = Mercado.builder().build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.of(mercado));
                when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);
                Optional<MercadoService.Left> actual = sut.update(dto);
                verify(mercadoRepository).findById(1L);
                verify(mercadoRepository).save(mercado);

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
            void with_UNEXPECTED_ERROR_when_mercadoRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(mercadoRepository).findById(anyLong());
                Optional<MercadoService.Left> actual = sut.delete(id);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_MERCADO_NOT_EXIST_when_mercadoRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<MercadoService.Left> actual = sut.delete(id);
                verify(mercadoRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.MERCADO_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_mercadoRepository_delete_fails() {
                Long id = 1L;

                Mercado mercado = Mercado.builder().build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.of(mercado));
                doThrow(new RuntimeException()).when(mercadoRepository).delete(any(Mercado.class));

                Optional<MercadoService.Left> actual = sut.delete(id);
                verify(mercadoRepository).findById(1L);
                verify(mercadoRepository).delete(mercado);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(MercadoService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                Long id = 1L;

                Mercado mercado = Mercado.builder().build();

                when(mercadoRepository.findById(anyLong())).thenReturn(Optional.of(mercado));
                doNothing().when(mercadoRepository).delete(any(Mercado.class));

                Optional<MercadoService.Left> actual = sut.delete(id);
                verify(mercadoRepository).findById(1L);
                verify(mercadoRepository).delete(mercado);

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }
}
