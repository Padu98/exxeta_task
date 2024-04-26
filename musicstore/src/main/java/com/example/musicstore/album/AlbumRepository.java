package com.example.musicstore.album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a WHERE a.name = ?1 AND a.artist = ?2")
    Optional<Album> findAlbumByNameAndArtist(String name, String artist);
}
