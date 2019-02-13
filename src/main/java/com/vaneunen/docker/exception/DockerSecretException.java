package com.vaneunen.docker.exception;

public class DockerSecretException extends RuntimeException {
    public DockerSecretException(String message) {
        super(message);
    }
}
