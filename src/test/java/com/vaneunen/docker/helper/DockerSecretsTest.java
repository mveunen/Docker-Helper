package com.vaneunen.docker.helper;

import com.vaneunen.docker.exception.DockerSecretException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

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
        File secretFile = new File(secretsDir, "ExistingValue");
        FileWriter writer = new FileWriter(secretFile);
        writer.write("SomeValue");
        writer.close();
        DockerSecrets.setSecretsPath(secretsDir.getAbsolutePath());

        Optional<String> value = DockerSecrets.getSecretValue("ExistingValue");
        assertEquals("SomeValue", value.get());
    }

    @Test
    public void secretsNotFoundWhileNoSecrets() throws IOException {

        File secretFile = new File(secretsDir, "testSecret");
        FileWriter writer = new FileWriter(secretFile);
        writer.write("SomeValue");
        writer.close();
        DockerSecrets.setSecretsPath(secretsDir.getAbsolutePath());

        assertEquals(Optional.empty(), DockerSecrets.getSecretValue("NonExistingValue"));
    }

    @Test(expected = DockerSecretException.class)
    public void noSecrets() {
        DockerSecrets.setSecretsPath("/non/existing/path");
    }
}
