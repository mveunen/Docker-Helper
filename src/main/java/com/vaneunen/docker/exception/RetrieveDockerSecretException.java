package com.vaneunen.docker.exception;

public class RetrieveDockerSecretException extends DockerSecretException {
    public RetrieveDockerSecretException(String secretName) {
        super("Unable to find Docker secret with the name: " + secretName);
    }
}
