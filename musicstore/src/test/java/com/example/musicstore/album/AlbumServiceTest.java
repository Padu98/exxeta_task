package com.example.musicstore.album;

import com.example.musicstore.exception.AlbumNotPresentException;
import com.example.musicstore.exception.EntityAlreadyExistsException;
import com.example.musicstore.exception.EntityNotDeletableException;
import com.example.musicstore.exception.RatingOutOfRangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    AlbumRepository albumRepositoryMock;

    private Album testAlbum;

    private AlbumService albumService;

    @BeforeEach
    public void setUp(){
        albumService = new AlbumService(albumRepositoryMock);
        this.testAlbum = new Album("test", "test", LocalDate.now());
    }

    @Test
    public void getAllAlbums() {
        albumService.getAllAlbums();
        verify(albumRepositoryMock).findAll();
    }

    @Test
    public void addNewAlbum_success() {
        when(albumRepositoryMock.findAlbumByNameAndArtist(any(), any())).thenReturn(Optional.empty());
        albumService.addNewAlbum(this.testAlbum);
        verify(albumRepositoryMock).save(any());
    }
    @Test
    public void addNewAlbum_duplicateEntity() {
        when(albumRepositoryMock.findAlbumByNameAndArtist(any(), any())).thenReturn(Optional.of(this.testAlbum));
        Exception exception = assertThrows(EntityAlreadyExistsException.class, ()->{
            this.albumService.addNewAlbum(testAlbum);
        });
        String expectedMessage = "Album with name: '"+ testAlbum.getName() + "' and artist: '" +testAlbum.getArtist() + "' already present";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteAlbum_albumNotPresent() {
        Long testId = 1L;
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(AlbumNotPresentException.class, ()->{
            this.albumService.deleteAlbum(testId);
        });
        assertEquals(testId.toString(), exception.getMessage());
    }
    @Test
    public void deleteAlbum_RatingAboveLimit_MoreThanTenVotes() {
        this.testAlbum.setRating(4.5);
        this.testAlbum.setRatingCount(11);
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        Exception exception = assertThrows(EntityNotDeletableException.class, ()->{
            this.albumService.deleteAlbum(1L);
        });
        assertEquals("Album is not deletable!", exception.getMessage());
    }
    @Test
    public void deleteAlbum_RatingAboveLimit_LessThanTenVotes() {
        Long testId = 1L;
        this.testAlbum.setRating(4.5);
        this.testAlbum.setRatingCount(8);
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        this.albumService.deleteAlbum(testId);
        verify(this.albumRepositoryMock).deleteById(testId);
    }

    @Test
    public void updateAlbum_UpdateName(){
        String newName = "newName";
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        Album updatedAlbum = this.albumService.modifyAlbum(this.testAlbum.getId(), newName, null, null);
        assertEquals(updatedAlbum.getName(), newName);
        assertEquals(updatedAlbum.getArtist(), this.testAlbum.getArtist());
        assertEquals(updatedAlbum.getRelease(), this.testAlbum.getRelease());
    }
    @Test
    public void updateAlbum_UpdateArtist(){
        String newArtist = "newArtist";
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        Album updatedAlbum = this.albumService.modifyAlbum(this.testAlbum.getId(), null, newArtist, null);
        assertEquals(updatedAlbum.getName(), this.testAlbum.getName());
        assertEquals(updatedAlbum.getArtist(), newArtist);
        assertEquals(updatedAlbum.getRelease(), this.testAlbum.getRelease());
    }
    @Test
    public void updateAlbum_UpdateRelease(){
        LocalDate newRelease = LocalDate.of(2024, 4, 26);
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        Album updatedAlbum = this.albumService.modifyAlbum(this.testAlbum.getId(), null, null, newRelease);
        assertEquals(updatedAlbum.getName(), this.testAlbum.getName());
        assertEquals(updatedAlbum.getArtist(), this.testAlbum.getArtist());
        assertEquals(updatedAlbum.getRelease(), newRelease);
    }
    @Test
    public void updateAlbum_albumNotPresent() {
        Long testId = 1L;
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(AlbumNotPresentException.class, ()->{
            this.albumService.modifyAlbum(testId, "", "", LocalDate.now());
        });
        assertEquals(testId.toString(), exception.getMessage());
    }



    @Test
    public void rateAlbum_ratingIsBelowLimit(){
        Exception exception = assertThrows(RatingOutOfRangeException.class, ()->{
           this.albumService.rateAlbum(1L, 0);
        });
        assertEquals("Rating has to be between 1 and 5.", exception.getMessage());
    }

    @Test
    public void rateAlbum_ratingIsAboveLimit(){
        Exception exception = assertThrows(RatingOutOfRangeException.class, ()->{
            this.albumService.rateAlbum(1L, 6);
        });
        assertEquals("Rating has to be between 1 and 5.", exception.getMessage());
    }

    @Test
    public void rateAlbum_success(){
        int newRating = 4;
        when(albumRepositoryMock.findById(any())).thenReturn(Optional.of(this.testAlbum));
        Album updatedAlbum = albumService.rateAlbum(1L, newRating);
        verify(albumRepositoryMock).findById(1L);
        assertEquals(updatedAlbum.getRating(), newRating);
    }

}