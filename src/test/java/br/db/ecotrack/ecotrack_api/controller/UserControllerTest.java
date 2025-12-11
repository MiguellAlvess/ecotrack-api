package br.db.ecotrack.ecotrack_api.controller;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.db.ecotrack.ecotrack_api.controller.dto.user.UserRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.user.UserResponseDto;
import br.db.ecotrack.ecotrack_api.service.CurrentUserService;
import br.db.ecotrack.ecotrack_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private CurrentUserService currentUserService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
    UserResponseDto responseDto = new UserResponseDto(1L, "John Doe", "john@example.com");

    when(userService.getUserById(1L)).thenReturn(responseDto);

    mockMvc.perform(get("/api/users/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(responseDto.userId()))
        .andExpect(jsonPath("$.name").value(responseDto.name()))
        .andExpect(jsonPath("$.email").value(responseDto.email()));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  @WithMockUser
  void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
    Long id = 999L;

    when(userService.getUserById(id)).thenThrow(new EntityNotFoundException());

    mockMvc.perform(get("/api/users/{id}", id))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).getUserById(id);
  }

  @Test
  @WithMockUser
  void getCurrentUser_ShouldReturn200_WhenUpdateSuccessfully() throws Exception {
    Long id = 1L;

    UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "newPassword123");
    UserResponseDto responseDto = new UserResponseDto(id, "John Doe", "john@example.com");

    when(userService.updateUser(eq(id), any(UserRequestDto.class))).thenReturn(responseDto);

    mockMvc.perform(patch("/api/users/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(is(responseDto.userId().intValue())))
        .andExpect(jsonPath("$.name").value(responseDto.name()))
        .andExpect(jsonPath("$.email").value(responseDto.email()));

    verify(userService, times(1)).updateUser(eq(id), any(UserRequestDto.class));
  }

  @Test
  @WithMockUser
  void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
    Long id = 999L;

    UserRequestDto requestDto = new UserRequestDto("John Doe", "john@example.com", "password123");

    when(userService.updateUser(eq(id), any(UserRequestDto.class)))
        .thenThrow(new EntityNotFoundException());

    mockMvc.perform(patch("/api/users/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).updateUser(eq(id), any(UserRequestDto.class));
  }

  @Test
  @WithMockUser
  void deleteUser_ShouldReturn204_WhenDeletedSuccessfully() throws Exception {
    Long id = 1L;

    doNothing().when(userService).deleteUser(id);

    mockMvc.perform(delete("/api/users/{id}", id))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).deleteUser(id);
  }

  @Test
  @WithMockUser
  void deleteUser_ShouldReturn404_WhenUserNotFound() throws Exception {
    Long id = 999L;

    doThrow(new EntityNotFoundException()).when(userService).deleteUser(id);

    mockMvc.perform(delete("/api/users/{id}", id))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).deleteUser(id);
  }

  @Test
  @WithMockUser
  void getCurrentUser_ShouldReturn200_WhenUserExists() throws Exception {
    UserResponseDto responseDto = new UserResponseDto(10L, "John Doe", "john@example.com");

    when(currentUserService.get()).thenReturn(responseDto);

    mockMvc.perform(get("/api/users/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(is(responseDto.userId().intValue())))
        .andExpect(jsonPath("$.name").value(is(responseDto.name())))
        .andExpect(jsonPath("$.email").value(is(responseDto.email())));

    verify(currentUserService, times(1)).get();
  }

}
