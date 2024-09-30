package com.decrypto.operacionescrud.controllers.comitente;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.TipoIdentificador;
import com.decrypto.operacionescrud.services.ComitenteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.is;

@ExtendWith(MockitoExtension.class)
class ComitenteControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Mock
    private ComitenteService comitenteService;
    private MockMvc mockMvc;
    private SaveComitenteRequest saveComitenteRequest;
    private UpdateComitenteRequest updateComitenteRequest;

    private static String asJsonString(Object request) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(request);
    }

    @BeforeEach
    void setUpForEachTest() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new ComitenteController(comitenteService))
            .build();

        saveComitenteRequest = SaveComitenteRequest.builder()
            .nombre("NOMBRE")
            .description("DESCRIPCION")
            .tipoIdentificacion(TipoIdentificador.DNI.name())
            .identificacion("11111111")
            .mercadosIds(new HashSet<>(Arrays.asList(1L, 2L)))
            .build();

        updateComitenteRequest = UpdateComitenteRequest.builder()
            .description("UPDATE DESCRIPTION")
            .build();
    }

    @DisplayName("findAll")
    @Nested
    class FindAllTest {
        @DisplayName("when service returns left")
        @Nested
        class LeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Either<ComitenteService.Left, ComitentesResponse> left = Either.left(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.findAll()).thenReturn(left);

                mockMvc.perform(get("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_ComitentesResponse_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                ComitentesResponse response = ComitentesResponse.builder()
                    .comitentes(Collections.emptyList())
                    .build();

                Either<ComitenteService.Left, ComitentesResponse> right = Either.right(response);
                when(comitenteService.findAll()).thenReturn(right);

                mockMvc.perform(get("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }

            @Test
            void with_service_return_ComitentesResponse_not_empty_then_return_Respons_Entity_with_ComitentesResponse() throws Exception {
                ComitenteModel comitenteModel = ComitenteModel.builder()
                    .id(1L)
                    .identificacion("123")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPCION")
                    .nombre("NOMBRE")
                    .build();

                ComitentesResponse response = ComitentesResponse.builder()
                    .comitentes(Arrays.asList(comitenteModel))
                    .build();

                Either<ComitenteService.Left, ComitentesResponse> right = Either.right(response);
                when(comitenteService.findAll()).thenReturn(right);

                mockMvc.perform(get("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.comitentes[0].id"), is(comitenteModel.getId().intValue())))
                    .andExpect(jsonPath(("$.comitentes[0].identificacion"), is(comitenteModel.getIdentificacion())))
                    .andExpect(jsonPath(("$.comitentes[0].tipoIdentificacion"), is(comitenteModel.getTipoIdentificacion().name())))
                    .andExpect(jsonPath(("$.comitentes[0].description"), is(comitenteModel.getDescription())))
                    .andExpect(jsonPath(("$.comitentes[0].nombre"), is(comitenteModel.getNombre())));
            }
        }
    }

    @DisplayName("findById")
    @Nested
    class FindByIdTest {
        @DisplayName("when service returns left")
        @Nested
        class LeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Either<ComitenteService.Left, ComitenteModel> left = Either.left(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_NOT_EXIST_then_preconditionFail() throws Exception {
                Either<ComitenteService.Left, ComitenteModel> left = Either.left(ComitenteService.Left.COMITENTE_NOT_EXIST);
                when(comitenteService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_ComitenteModel_then_return_Respons_Entity_with_ComitenteModel() throws Exception {
                ComitenteModel comitenteModel = ComitenteModel.builder()
                    .id(1L)
                    .identificacion("123")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPCION")
                    .nombre("NOMBRE")
                    .build();

                Either<ComitenteService.Left, ComitenteModel> right = Either.right(comitenteModel);
                when(comitenteService.findById(anyLong())).thenReturn(right);

                mockMvc.perform(get("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.id"), is(comitenteModel.getId().intValue())))
                    .andExpect(jsonPath(("$.identificacion"), is(comitenteModel.getIdentificacion())))
                    .andExpect(jsonPath(("$.tipoIdentificacion"), is(comitenteModel.getTipoIdentificacion().name())))
                    .andExpect(jsonPath(("$.description"), is(comitenteModel.getDescription())))
                    .andExpect(jsonPath(("$.nombre"), is(comitenteModel.getNombre())));
            }
        }
    }

    @DisplayName("save")
    @Nested
    class SaveTest {
        @DisplayName("when service returns Optional<Left>")
        @Nested
        class OptionalLeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.save(any(ComitenteDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveComitenteRequest)))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.COMITENTE_EXIST);
                when(comitenteService.save(any(ComitenteDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveComitenteRequest)))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<ComitenteService.Left> optionalEmpty = Optional.empty();
                when(comitenteService.save(any(ComitenteDTO.class))).thenReturn(optionalEmpty);

                mockMvc.perform(post("/comitentes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveComitenteRequest)))
                    .andExpect(status().isOk());
            }
        }
    }

    @DisplayName("update")
    @Nested
    class UpdateTest {
        @DisplayName("when service returns Optional<Left>")
        @Nested
        class OptionalLeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.update(any(ComitenteDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateComitenteRequest)))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.COMITENTE_NOT_EXIST);
                when(comitenteService.update(any(ComitenteDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateComitenteRequest)))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<ComitenteService.Left> optionalEmpty = Optional.empty();
                when(comitenteService.update(any(ComitenteDTO.class))).thenReturn(optionalEmpty);

                mockMvc.perform(put("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateComitenteRequest)))
                    .andExpect(status().isOk());
            }
        }
    }

    @DisplayName("delete")
    @Nested
    class DeleteTest {
        @DisplayName("when service returns Optional<Left>")
        @Nested
        class OptionalLeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.COMITENTE_NOT_EXIST);
                when(comitenteService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<ComitenteService.Left> optionalEmpty = Optional.empty();
                when(comitenteService.delete(anyLong())).thenReturn(optionalEmpty);

                mockMvc.perform(delete("/comitentes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
        }
    }

    @DisplayName("saveInMercado")
    @Nested
    class SaveInMercadoTest {
        @DisplayName("when service returns Optional<Left>")
        @Nested
        class OptionalLeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.saveInMercado(anyLong(), anyString())).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.COMITENTE_NOT_EXIST);
                when(comitenteService.saveInMercado(anyLong(), anyString())).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }

            @Test
            void with_service_return_COMITENTE_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.COMITENTE_EXIST);
                when(comitenteService.saveInMercado(anyLong(), anyString())).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }

            @Test
            void with_service_return_MERCADO_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<ComitenteService.Left> optionalLeft = Optional.of(ComitenteService.Left.MERCADO_NOT_EXIST);
                when(comitenteService.saveInMercado(anyLong(), anyString())).thenReturn(optionalLeft);

                mockMvc.perform(put("/comitentes/1/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<ComitenteService.Left> optionalEmpty = Optional.empty();
                when(comitenteService.saveInMercado(anyLong(), anyString())).thenReturn(optionalEmpty);

                mockMvc.perform(put("/comitentes/1/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
        }
    }

    @DisplayName("findByMercado")
    @Nested
    class FindByMercadoTest {
        @DisplayName("when service returns left")
        @Nested
        class LeftTest {
            @Test
            void with_service_return_UNEXPECTED_ERROR_then_internalServerError() throws Exception {
                Either<ComitenteService.Left, ComitentesResponse> left = Either.left(ComitenteService.Left.UNEXPECTED_ERROR);
                when(comitenteService.findComitentesByMercado(anyString())).thenReturn(left);

                mockMvc.perform(get("/comitentes/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_COMITENTE_NOT_EXIST_then_preconditionFail() throws Exception {
                Either<ComitenteService.Left, ComitentesResponse> left = Either.left(ComitenteService.Left.MERCADO_NOT_EXIST);
                when(comitenteService.findComitentesByMercado(anyString())).thenReturn(left);

                mockMvc.perform(get("/comitentes/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_ComitenteModel_then_return_Respons_Entity_with_ComitenteModel() throws Exception {
                ComitenteModel comitenteModel = ComitenteModel.builder()
                    .id(1L)
                    .identificacion("123")
                    .tipoIdentificacion(TipoIdentificador.DNI)
                    .description("DESCRIPCION")
                    .nombre("NOMBRE")
                    .build();

                ComitentesResponse comitentesResponse = ComitentesResponse.builder()
                    .comitentes(Arrays.asList(comitenteModel))
                    .build();
                Either<ComitenteService.Left, ComitentesResponse> right = Either.right(comitentesResponse);
                when(comitenteService.findComitentesByMercado(anyString())).thenReturn(right);

                mockMvc.perform(get("/comitentes/mercado/M")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.comitentes[0].id"), is(comitenteModel.getId().intValue())))
                    .andExpect(jsonPath(("$.comitentes[0].identificacion"), is(comitenteModel.getIdentificacion())))
                    .andExpect(jsonPath(("$.comitentes[0].tipoIdentificacion"), is(comitenteModel.getTipoIdentificacion().name())))
                    .andExpect(jsonPath(("$.comitentes[0].description"), is(comitenteModel.getDescription())))
                    .andExpect(jsonPath(("$.comitentes[0].nombre"), is(comitenteModel.getNombre())));
            }
        }
    }
}
