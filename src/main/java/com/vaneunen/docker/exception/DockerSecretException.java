package com.vaneunen.docker.exception;

public abstract class DockerSecretException extends RuntimeException {
    public DockerSecretException(String message) {
        super(message);
    }
}
