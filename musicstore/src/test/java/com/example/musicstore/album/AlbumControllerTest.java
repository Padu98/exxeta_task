package com.example.musicstore.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import({AlbumController.class, AlbumService.class})
class AlbumControllerTest {

    private Album testAlbum;
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AlbumRepository albumRepositoryMock;

    @BeforeEach
    public void setUp(){
        this.testAlbum = new Album("test", "test", LocalDate.now());
        this.testAlbum.setId(1L);
        this.objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    void getAlbums() throws Exception {
        mockMvc.perform(get("/api/album"))
                .andExpect(status().isOk());
    }
    @Test
    void createAlbum_success() throws Exception {
        when(albumRepositoryMock.findAlbumByNameAndArtist(any(), any())).thenReturn(Optional.empty());
        when(albumRepositoryMock.save(any())).thenReturn(this.testAlbum);
        mockMvc.perform(post("/api/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.testAlbum)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(this.testAlbum.getId()));
    }
    @Test
    void updateAlbum_updateNameAndArtist_success() throws Exception {
        String newName = "newName";
        String newArtist = "newArtist";
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        mockMvc.perform(put("/api/album/" + this.testAlbum.getId())
                        .param("name", newName)
                        .param("artist", newArtist))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(newName))
                .andExpect(jsonPath("artist").value(newArtist));
    }

    @Test
    void rateAlbum_success() throws Exception {
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        mockMvc.perform(put("/api/album/rate/"+ this.testAlbum.getId())
                        .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("ratingCount").value(1));
    }

    @Test
    void rateAlbum_albumNotFound() throws Exception {
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/album/rate/"+ this.testAlbum.getId())
                        .param("rating", "5"))
                .andExpect(status().isNotFound());
    }
}