package com.example.musicstore.exception;

public class RatingOutOfRangeException extends RuntimeException{
    public RatingOutOfRangeException(String message){
        super(message);
    }
}
