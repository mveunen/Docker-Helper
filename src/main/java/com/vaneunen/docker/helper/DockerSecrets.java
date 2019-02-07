package com.vaneunen.docker.helper;

import com.vaneunen.docker.exception.DockerSecretException;
import com.vaneunen.docker.exception.LoadDockerSecretException;
import com.vaneunen.docker.exception.RetrieveDockerSecretException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.readAllBytes;

public class DockerSecrets {

    private static String dockerSecretsPath = "/run/secrets/";
    private static final Map<String, String> secrets = new HashMap<>();
    private static boolean initialized = false;

    /**
     * This method will return the value for the secretName in a Docker environment.
     *
     * @param secretName is the name of the secret in Docker
     * @return the value of the secret
     * @throws DockerSecretException if there was an error retrieving the secret
     */
    public static String getSecretValue(String secretName) throws DockerSecretException {
        if (!initialized) {
            loadSecrets();
        }

        final String secretValue = secrets.get(secretName);
        if(secretValue == null) {
            throw new RetrieveDockerSecretException(secretName);
        }
        return secretValue;
    }

    private static void loadSecrets() throws LoadDockerSecretException {

        final File dockerSecretsDir = new File(dockerSecretsPath);
        if (!dockerSecretsDir.exists()) {
            throw new LoadDockerSecretException("No secrets directory found: " + dockerSecretsDir.getAbsolutePath());
        }

        final File[] secretFiles = dockerSecretsDir.listFiles();
        if (secretFiles == null || secretFiles.length == 0) {
            throw new LoadDockerSecretException("No secrets found in: " + dockerSecretsDir.getAbsolutePath());
        }

        Arrays.asList(secretFiles).forEach(file -> {
            try {
                final String secretName = file.getName();
                final String secretValue = new String(readAllBytes(file.toPath()));
                secrets.put(secretName, secretValue);
            } catch (IOException e) {
                throw new LoadDockerSecretException("Error while reading the secrets in: " + dockerSecretsDir.getAbsolutePath());
            }
        });

        initialized = true;
    }

    /**
     * This method is only here to be able to unit test the code. It is not advisable to use this in production code.
     *
     * @param path is the path in which the Docker secrets are located
     */
    static final void setSecretsPath(String path) {
        dockerSecretsPath = path;
        initialized = false;
    }
}