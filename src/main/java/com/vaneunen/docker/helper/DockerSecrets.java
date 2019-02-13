package com.vaneunen.docker.helper;

import com.vaneunen.docker.exception.DockerSecretException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.Files.readAllBytes;

public class DockerSecrets {

    private static String dockerSecretsPath = "/run/secrets/";
    private static Map<String, String> secrets;

    /**
     * This method will return the value for the secretName in a Docker environment.
     *
     * @param secretName is the name of the secret in Docker
     * @return the value of the secret
     * @throws DockerSecretException if there was an error retrieving the secret
     */
    public static Optional<String> getSecretValue(String secretName) throws DockerSecretException {
        if (secrets == null) {
            loadSecrets();
        }

        return Optional.ofNullable(secrets.get(secretName));
    }

    private static void loadSecrets() throws DockerSecretException {
        secrets = new HashMap<>();

        final File dockerSecretsDir = new File(dockerSecretsPath);
        if (!dockerSecretsDir.exists()) {
            throw new DockerSecretException("No secrets directory found: " + dockerSecretsDir.getAbsolutePath());
        }

        final File[] secretFiles = dockerSecretsDir.listFiles();
        Arrays.asList(secretFiles).forEach(file -> {
            try {
                final String secretName = file.getName();
                final String secretValue = new String(readAllBytes(file.toPath()));
                secrets.put(secretName, secretValue);
            } catch (IOException e) {
                throw new DockerSecretException("Error while reading the secrets in: " + dockerSecretsDir.getAbsolutePath());
            }
        });
    }

    /**
     * This method is only here to be able to unit test the code. It is not advisable to use this in production code.
     *
     * @param path is the path in which the Docker secrets are located
     */
    static final void setSecretsPath(String path) {
        dockerSecretsPath = path;
        loadSecrets();
    }
}