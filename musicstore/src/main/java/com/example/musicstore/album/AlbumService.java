package com.example.musicstore.album;

import com.example.musicstore.exception.AlbumNotPresentException;
import com.example.musicstore.exception.EntityAlreadyExistsException;
import com.example.musicstore.exception.EntityNotDeletableException;
import com.example.musicstore.exception.RatingOutOfRangeException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    public AlbumService(AlbumRepository albumRepository){
        this.albumRepository = albumRepository;
    }
    public List<Album> getAllAlbums(){
        return this.albumRepository.findAll();
    }

    public Album addNewAlbum(Album album){
        Optional<Album> albumOptional = this.albumRepository.findAlbumByNameAndArtist(album.getName(), album.getArtist());
        if(albumOptional.isPresent()){
            throw new EntityAlreadyExistsException("Album with name: '"+ album.getName() + "' and artist: '" +album.getArtist() + "' already present");
        }
        return this.albumRepository.save(album);
    }

    public void deleteAlbum(Long id) {
        Album album = this.albumRepository.findById(id)
                .orElseThrow(()-> new AlbumNotPresentException(id));

        if(album.getRating()!=null && album.getRating() > 4 && album.getRatingCount()>=10){
            throw new EntityNotDeletableException("Album is not deletable!");
        }
        this.albumRepository.deleteById(id);
    }

    @Transactional
    public Album modifyAlbum(Long id, String name, String artist, LocalDate release){
        Album presentAlbum = this.albumRepository.findById(id)
                .orElseThrow(()-> new AlbumNotPresentException(id));
        if(name!=null && !name.isEmpty() && !presentAlbum.getName().equals(name)){
            presentAlbum.setName(name);
        }
        if(artist!=null && !artist.isEmpty() &&!presentAlbum.getArtist().equals(artist)){
            presentAlbum.setArtist(artist);
        }
        if(release!=null && !presentAlbum.getRelease().equals(release)){
            presentAlbum.setRelease(release);
        }
        return presentAlbum;
    }

    @Transactional
    public Album rateAlbum(Long id, Integer rating){
        if(rating < 1 || rating > 5){
            throw new RatingOutOfRangeException("Rating has to be between 1 and 5.");
        }
        Album presentAlbum = this.albumRepository.findById(id)
                .orElseThrow(()-> new AlbumNotPresentException(id));
        int oldRatingCount = presentAlbum.getRatingCount() != null ? presentAlbum.getRatingCount() : 0;
        double oldRatingAverage = presentAlbum.getRating() != null ? presentAlbum.getRating() : 0;
        int newRatingCount = oldRatingCount + 1;
        double newRating = (oldRatingAverage * oldRatingCount + rating)/newRatingCount;

        presentAlbum.setRating(newRating);
        presentAlbum.setRatingCount(newRatingCount);
        return presentAlbum;
    }
}
