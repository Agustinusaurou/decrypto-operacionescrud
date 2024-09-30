package com.decrypto.operacionescrud.services;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.controllers.pais.PaisDTO;
import com.decrypto.operacionescrud.controllers.pais.PaisModel;
import com.decrypto.operacionescrud.controllers.pais.PaisesResponse;
import com.decrypto.operacionescrud.entities.Pais;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaisServiceTest {
    @Mock
    private PaisRepository paisRepository;
    @InjectMocks
    private PaisService sut;

    @DisplayName("findAll")
    @Nested
    class FindAllTest {
        @DisplayName("returns left")
        @Nested
        class Left {
            @Test
            void with_UNEXPECTED_ERROR_when_paisRepository_findAll_fails() {
                doThrow(new RuntimeException()).when(paisRepository).findAll();
                Either<PaisService.Left, PaisesResponse> actual = sut.findAll();
                verify(paisRepository).findAll();

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_empty_list_when_paisRepository_findAll_return_empty() {
                when(paisRepository.findAll()).thenReturn(Collections.emptyList());
                Either<PaisService.Left, PaisesResponse> actual = sut.findAll();
                verify(paisRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getPaises()).hasSize(0);
            }

            @Test
            void with_list_when_paisRepository_findAll_return_mercados() {
                Pais pais = Pais.builder()
                    .id(1L)
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                List<Pais> paises = Arrays.asList(pais);

                when(paisRepository.findAll()).thenReturn(paises);
                Either<PaisService.Left, PaisesResponse> actual = sut.findAll();
                verify(paisRepository).findAll();

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getPaises()).hasSize(1);
                assertThat(actual.getRight().getPaises().get(0).getId()).isEqualTo(1L);
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
            void with_UNEXPECTED_ERROR_when_paisRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(paisRepository).findById(anyLong());
                Either<PaisService.Left, PaisModel> actual = sut.findById(id);
                verify(paisRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_PAIS_NOT_EXIST_when_paisRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(paisRepository.findById(anyLong())).thenReturn(Optional.empty());
                Either<PaisService.Left, PaisModel> actual = sut.findById(id);
                verify(paisRepository).findById(1L);

                assertThat(actual.isLeft()).isTrue();
                assertThat(actual.getLeft()).isEqualTo(PaisService.Left.PAIS_NOT_EXIST);
            }
        }

        @DisplayName("returns right")
        @Nested
        class Right {
            @Test
            void with_PaisModel_when_paisRepository_findById_optional_with_value() {
                Long id = 1L;

                Pais pais = Pais.builder()
                    .id(1L)
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                when(paisRepository.findById(anyLong())).thenReturn(Optional.of(pais));
                Either<PaisService.Left, PaisModel> actual = sut.findById(id);
                verify(paisRepository).findById(1L);

                assertThat(actual.isRight()).isTrue();
                assertThat(actual.getRight().getId()).isEqualTo(1L);
                assertThat(actual.getRight().getNombre()).isEqualTo(PaisAdmitido.ARGENTINA.name());
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
            void with_UNEXPECTED_ERROR_when_paisRepository_findByNombre_fails() {
                PaisDTO dto = PaisDTO.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                doThrow(new RuntimeException()).when(paisRepository).findByNombre(any(PaisAdmitido.class));
                Optional<PaisService.Left> actual = sut.save(dto);

                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_PAIS_EXIST_when_paisRepository_findByNombre_return_pais() {
                PaisDTO dto = PaisDTO.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                Pais pais = Pais.builder().build();

                when(paisRepository.findByNombre(any(PaisAdmitido.class)))
                    .thenReturn(Optional.of(pais));
                Optional<PaisService.Left> actual = sut.save(dto);

                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.PAIS_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_paisRepository_save_fails() {
                PaisDTO dto = PaisDTO.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                when(paisRepository.findByNombre(any(PaisAdmitido.class))).thenReturn(Optional.empty());
                doThrow(new RuntimeException()).when(paisRepository).save(any(Pais.class));
                Optional<PaisService.Left> actual = sut.save(dto);

                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);
                verify(paisRepository).save(any(Pais.class));

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                PaisDTO dto = PaisDTO.builder()
                    .nombre(PaisAdmitido.ARGENTINA)
                    .build();

                Pais pais = Pais.builder().build();

                when(paisRepository.findByNombre(any(PaisAdmitido.class))).thenReturn(Optional.empty());
                when(paisRepository.save(any(Pais.class))).thenReturn(pais);
                Optional<PaisService.Left> actual = sut.save(dto);

                verify(paisRepository).findByNombre(PaisAdmitido.ARGENTINA);
                verify(paisRepository).save(any(Pais.class));

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
            void with_UNEXPECTED_ERROR_when_paisRepository_findById_fails() {
                Long id = 1L;

                doThrow(new RuntimeException()).when(paisRepository).findById(anyLong());
                Optional<PaisService.Left> actual = sut.delete(id);
                verify(paisRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }

            @Test
            void with_PAIS_NOT_EXIST_when_paisRepository_findById_return_optional_empty() {
                Long id = 1L;

                when(paisRepository.findById(anyLong())).thenReturn(Optional.empty());
                Optional<PaisService.Left> actual = sut.delete(id);
                verify(paisRepository).findById(1L);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.PAIS_NOT_EXIST);
            }

            @Test
            void with_UNEXPECTED_ERROR_when_paisRepository_delete_fails() {
                Long id = 1L;

                Pais pais = Pais.builder().build();

                when(paisRepository.findById(anyLong())).thenReturn(Optional.of(pais));
                doThrow(new RuntimeException()).when(paisRepository).delete(any(Pais.class));

                Optional<PaisService.Left> actual = sut.delete(id);
                verify(paisRepository).findById(1L);
                verify(paisRepository).delete(pais);

                assertThat(actual.isPresent()).isTrue();
                assertThat(actual.get()).isEqualTo(PaisService.Left.UNEXPECTED_ERROR);
            }
        }

        @DisplayName("returns optional empty")
        @Nested
        class OptionalEmpty {
            @Test
            void when_never_fail() {
                Long id = 1L;

                Pais pais = Pais.builder().build();

                when(paisRepository.findById(anyLong())).thenReturn(Optional.of(pais));
                doNothing().when(paisRepository).delete(any(Pais.class));

                Optional<PaisService.Left> actual = sut.delete(id);
                verify(paisRepository).findById(1L);
                verify(paisRepository).delete(pais);

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }
}
