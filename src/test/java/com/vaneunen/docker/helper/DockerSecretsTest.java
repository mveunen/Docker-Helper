package com.vaneunen.docker.helper;

import com.vaneunen.docker.exception.LoadDockerSecretException;
import com.vaneunen.docker.exception.RetrieveDockerSecretException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DockerSecretsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File secretsDir;

    @Before
    public void setup() throws IOException {
        secretsDir = folder.newFolder("run", "secrets");
    }

    @Test
    public void secretFound() throws IOException {
        DockerSecrets.setSecretsPath(secretsDir.getAbsolutePath());

        File secretFile = new File(secretsDir, "ExistingValue");
        FileWriter writer = new FileWriter(secretFile);
        writer.write("SomeValue");
        writer.close();

        String value = DockerSecrets.getSecretValue("ExistingValue");
        assertEquals("SomeValue", value);
    }

    @Test(expected = RetrieveDockerSecretException.class)
    public void secretsNotFoundWhileNoSecrets() throws IOException {
        DockerSecrets.setSecretsPath(secretsDir.getAbsolutePath());

        File secretFile = new File(secretsDir, "testSecret");
        FileWriter writer = new FileWriter(secretFile);
        writer.write("SomeValue");
        writer.close();

        DockerSecrets.getSecretValue("NonExistingValue");
    }

    @Test(expected = LoadDockerSecretException.class)
    public void noSecrets() {
        DockerSecrets.setSecretsPath("/non/existing/path");

        DockerSecrets.getSecretValue("NonExistingValue");
    }
}
