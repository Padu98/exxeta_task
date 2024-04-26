package com.example.musicstore.exception;

public class AlbumNotPresentException extends RuntimeException{
    public AlbumNotPresentException(Long message) {
        super(message.toString());
    }
}
