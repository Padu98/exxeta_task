package com.example.musicstore.album;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AlbumRepositoryTest {

    private Album testAlbum;

    @Autowired
    private AlbumRepository albumRepository;

    @BeforeEach
    public void setUp(){
        this.testAlbum = albumRepository.save(new Album("name", "artist", LocalDate.now()));
    }

    @AfterEach
    public void tearDown(){
        albumRepository.delete(this.testAlbum);
    }

    @Test
    void findAlbumByNameAndArtist_EntityPresent() {
        Optional<Album>optional = albumRepository.findAlbumByNameAndArtist(testAlbum.getName(), testAlbum.getArtist());
        assertFalse(optional.isEmpty());
    }

    @Test
    void findAlbumByNameAndArtist_entityNotPresent() {
        Optional<Album>optional = albumRepository.findAlbumByNameAndArtist("other name", testAlbum.getArtist());
        assertTrue(optional.isEmpty());
    }
}