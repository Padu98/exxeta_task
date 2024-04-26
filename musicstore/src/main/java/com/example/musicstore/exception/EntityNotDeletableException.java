package com.example.musicstore.exception;

public class EntityNotDeletableException extends RuntimeException{
    public EntityNotDeletableException(String message){
        super(message);
    }
}
