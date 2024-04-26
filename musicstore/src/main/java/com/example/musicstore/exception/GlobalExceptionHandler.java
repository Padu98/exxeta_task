package com.example.musicstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlbumNotPresentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleAlbumNotFoundException(final AlbumNotPresentException ex){
        return new ResponseEntity<>("Object with id "+ex.getMessage() + " does not exist.", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({RatingOutOfRangeException.class, EntityAlreadyExistsException.class, EntityNotDeletableException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleOutOfRangeException(final RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
