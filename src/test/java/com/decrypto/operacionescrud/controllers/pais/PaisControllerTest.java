package com.decrypto.operacionescrud.controllers.pais;

import com.decrypto.operacionescrud.Utils.Either;
import com.decrypto.operacionescrud.entities.PaisAdmitido;
import com.decrypto.operacionescrud.services.PaisService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaisControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Mock
    private PaisService paisService;
    private MockMvc mockMvc;
    private SavePaisRequest savePaisRequest;

    private static String asJsonString(Object request) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(request);
    }

    @BeforeEach
    void setUpForEachTest() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new PaisController(paisService))
            .build();

        savePaisRequest = SavePaisRequest.builder()
            .nombre(PaisAdmitido.ARGENTINA)
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
                Either<PaisService.Left, PaisesResponse> left = Either.left(PaisService.Left.UNEXPECTED_ERROR);
                when(paisService.findAll()).thenReturn(left);

                mockMvc.perform(get("/paises")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_PaisesResponse_empty_then_return_Respons_Entity_with_Status_ok() throws Exception {
                PaisesResponse response = PaisesResponse.builder()
                    .paises(Collections.emptyList())
                    .build();

                Either<PaisService.Left, PaisesResponse> right = Either.right(response);
                when(paisService.findAll()).thenReturn(right);

                mockMvc.perform(get("/paises")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }

            @Test
            void with_service_return_PaisesResponse_not_empty_then_return_Respons_Entity_with_PaisesResponse() throws Exception {
                PaisModel paisModel = PaisModel.builder()
                    .id(1L)
                    .nombre(PaisAdmitido.URUGUAY.name())
                    .build();

                PaisesResponse response = PaisesResponse.builder()
                    .paises(Arrays.asList(paisModel))
                    .build();

                Either<PaisService.Left, PaisesResponse> right = Either.right(response);
                when(paisService.findAll()).thenReturn(right);

                mockMvc.perform(get("/paises")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.paises[0].id"), is(paisModel.getId().intValue())))
                    .andExpect(jsonPath(("$.paises[0].nombre"), is(paisModel.getNombre())));


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
                Either<PaisService.Left, PaisModel> left = Either.left(PaisService.Left.UNEXPECTED_ERROR);
                when(paisService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_PAIS_NOT_EXIST_then_preconditionFail() throws Exception {
                Either<PaisService.Left, PaisModel> left = Either.left(PaisService.Left.PAIS_NOT_EXIST);
                when(paisService.findById(anyLong())).thenReturn(left);

                mockMvc.perform(get("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns right")
        @Nested
        class RightTest {
            @Test
            void with_service_return_PaisModel_then_return_Respons_Entity_with_PaisModel() throws Exception {
                PaisModel paisModel = PaisModel.builder()
                    .id(1L)
                    .nombre(PaisAdmitido.URUGUAY.name())
                    .build();

                Either<PaisService.Left, PaisModel> right = Either.right(paisModel);
                when(paisService.findById(anyLong())).thenReturn(right);

                mockMvc.perform(get("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(("$.id"), is(paisModel.getId().intValue())))
                    .andExpect(jsonPath(("$.nombre"), is(paisModel.getNombre())));
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
                Optional<PaisService.Left> optionalLeft = Optional.of(PaisService.Left.UNEXPECTED_ERROR);
                when(paisService.save(any(PaisDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/paises")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(savePaisRequest)))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_PAIS_EXIST_then_preconditionFail() throws Exception {
                Optional<PaisService.Left> optionalLeft = Optional.of(PaisService.Left.PAIS_EXIST);
                when(paisService.save(any(PaisDTO.class))).thenReturn(optionalLeft);

                mockMvc.perform(post("/paises")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(savePaisRequest)))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_Optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<PaisService.Left> optionalEmpty = Optional.empty();
                when(paisService.save(any(PaisDTO.class))).thenReturn(optionalEmpty);

                mockMvc.perform(post("/paises")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(savePaisRequest)))
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
                Optional<PaisService.Left> optionalLeft = Optional.of(PaisService.Left.UNEXPECTED_ERROR);
                when(paisService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
            }

            @Test
            void with_service_return_PAIS_NOT_EXIST_then_preconditionFail() throws Exception {
                Optional<PaisService.Left> optionalLeft = Optional.of(PaisService.Left.PAIS_NOT_EXIST);
                when(paisService.delete(anyLong())).thenReturn(optionalLeft);

                mockMvc.perform(delete("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed());
            }
        }

        @DisplayName("when service returns Optional empty")
        @Nested
        class OptionalEmptyTest {
            @Test
            void with_service_return_optional_empty_then_return_Respons_Entity_with_status_ok() throws Exception {
                Optional<PaisService.Left> optionalEmpty = Optional.empty();
                when(paisService.delete(anyLong())).thenReturn(optionalEmpty);

                mockMvc.perform(delete("/paises/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
        }
    }
}
