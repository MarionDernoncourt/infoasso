package com.infoasso.api.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message, Long id) {

        super(message + " avec l'id " + id + " n'existe pas");
    }
}
