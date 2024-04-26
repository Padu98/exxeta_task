package com.example.musicstore.album;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Album> getAlbums(){return this.albumService.getAllAlbums();}

    @PostMapping()
    @ApiResponse(responseCode = "403", description = "Element already exists in the database. Combination of name and artist has to be unique.",
            content = @Content)
    @ResponseStatus(HttpStatus.CREATED)
    public Album createAlbum(@RequestBody Album album){
        return this.albumService.addNewAlbum(album);
    }

    @ApiResponse(responseCode = "404", description = "The album is not present in the database.",
            content = @Content)
    @PutMapping(path = "/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public Album updateAlbum(@PathVariable Long albumId,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String artist,
                            @RequestParam(required = false) LocalDate release){
        return this.albumService.modifyAlbum(albumId, name, artist, release);
    }

    @ApiResponse(responseCode = "404", description = "The album is not present in the database.",
            content = @Content)
    @ApiResponse(responseCode = "403", description = "If album rating is above 4 it is not deletable except there are less than 10 ratings in total.",
            content = @Content)
    @DeleteMapping(path = "/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long albumId){
        this.albumService.deleteAlbum(albumId);
    }


    @ApiResponse(responseCode = "403", description = "Rating is not in Range between 1 and 5",
            content = @Content)
    @PutMapping(path = "/rate/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public Album rateAlbum(@PathVariable Long albumId,
                          @RequestParam Integer rating){
        return this.albumService.rateAlbum(albumId, rating);
    }
}
