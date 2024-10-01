package com.decrypto.operacionescrud.controllers.mercado;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.services.MercadoService;
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
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MercadoControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Mock
    private MercadoService mercadoService;
    private MockMvc mockMvc;
    private SaveMercadoRequest saveMercadoRequest;
    private UpdateMercadoRequest updateMercadoRequest;

    private static String asJsonString(Object request) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(request);
    }

    @BeforeEach
    void setUpForEachTest() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new MercadoController(mercadoService))
            .build();

        saveMercadoRequest = SaveMercadoRequest.builder()
            .codigo("CODIGO")
            .description("DESCRIPCION")
            .pais("ARGENTINA")
            .build();

        updateMercadoRequest = UpdateMercadoRequest.builder()
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
                Either<MercadoService.Left, MercadosResponse> left = Either.left(MercadoService.Left.UNEXPECTED_ERROR);
                when(mercadoService.findAll()).thenReturn(left);

                mockMvc.perform(get("/mercados")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_MercadosResponse_empty_then_return_Respons_Entity_with_Status_ok() throws Exception {
                MercadosResponse response = MercadosResponse.builder()
                    .mercados(Collections.emptyList())
                    .build();

                Either<MercadoService.Left, MercadosResponse> right = Either.right(response);
                when(mercadoService.findAll()).thenReturn(right);

                mockMvc.perform(get("/mercados")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }

            @Test
            void with_service_return_ComitentesResponse_not_empty_then_return_Respons_Entity_with_ComitentesResponse() throws Exception {
                MercadoModel mercadoModel = MercadoModel.builder()
                    .id(1L)
                    .codigo("CODIGO")
                    .description("DESCRIPCION")
                    .pais(PaisAdmitido.ARGENTINA)
                    .build();

                MercadosResponse response = MercadosResponse.builder()
                    .mercados(Arrays.asList(mercadoModel))
                    .build();

                Either<MercadoService.Left, MercadosResponse> right = Either.right(response);
                when(mercadoService.findAll()).thenReturn(right);

                mockMvc.perform(get("/mercados")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.mercados[0].id"), is(mercadoModel.getId().intValue())))
                    .andExpect(jsonPath(("$.mercados[0].codigo"), is(mercadoModel.getCodigo())))
                    .andExpect(jsonPath(("$.mercados[0].pais"), is(mercadoModel.getPais().name())))
                    .andExpect(jsonPath(("$.mercados[0].description"), is(mercadoModel.getDescription())));

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
                Either<MercadoService.Left, MercadoModel> left = Either.left(MercadoService.Left.UNEXPECTED_ERROR);
                when(mercadoService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_MERCADO_NOT_EXIST_then_preconditionFail() throws Exception {
                Either<MercadoService.Left, MercadoModel> left = Either.left(MercadoService.Left.MERCADO_NOT_EXIST);
                when(mercadoService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_MercadoModel_then_return_Respons_Entity_with_ComitenteModel() throws Exception {
                MercadoModel mercadoModel = MercadoModel.builder()
                    .id(1L)
                    .pais(PaisAdmitido.ARGENTINA)
                    .description("DESCRIPCION")
                    .build();

                Either<MercadoService.Left, MercadoModel> right = Either.right(mercadoModel);
                when(mercadoService.findById(anyLong())).thenReturn(right);

                mockMvc.perform(get("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.id"), is(mercadoModel.getId().intValue())))
                    .andExpect(jsonPath(("$.pais"), is(mercadoModel.getPais().name())))
                    .andExpect(jsonPath(("$.description"), is(mercadoModel.getDescription())));
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
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.UNEXPECTED_ERROR);
                when(mercadoService.save(any(MercadoDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/mercados")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveMercadoRequest)))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_MERCADO_EXIST_then_preconditionFail() throws Exception {
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.MERCADO_EXIST);
                when(mercadoService.save(any(MercadoDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/mercados")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveMercadoRequest)))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_Optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<MercadoService.Left> optionalEmpty = Optional.empty();
                when(mercadoService.save(any(MercadoDTO.class))).thenReturn(optionalEmpty);

                mockMvc.perform(post("/mercados")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(saveMercadoRequest)))
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
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.UNEXPECTED_ERROR);
                when(mercadoService.update(any(MercadoDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(put("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateMercadoRequest)))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_MERCADO_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.MERCADO_NOT_EXIST);
                when(mercadoService.update(any(MercadoDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(put("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateMercadoRequest)))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<MercadoService.Left> optionalEmpty = Optional.empty();
                when(mercadoService.update(any(MercadoDTO.class))).thenReturn(optionalEmpty);

                mockMvc.perform(put("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updateMercadoRequest)))
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
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.UNEXPECTED_ERROR);
                when(mercadoService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_MERCADO_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<MercadoService.Left> optionalLeft = Optional.of(MercadoService.Left.MERCADO_NOT_EXIST);
                when(mercadoService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<MercadoService.Left> optionalEmpty = Optional.empty();
                when(mercadoService.delete(anyLong())).thenReturn(optionalEmpty);

                mockMvc.perform(delete("/mercados/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
        }
    }
}
