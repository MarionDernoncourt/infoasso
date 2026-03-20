package com.infoasso.api.exceptions;

public class RessourceNotFoundException extends RuntimeException {
    public RessourceNotFoundException(String message, Long id) {

        super(message + " avec l'id " + id + " n'existe pas");
    }
}
