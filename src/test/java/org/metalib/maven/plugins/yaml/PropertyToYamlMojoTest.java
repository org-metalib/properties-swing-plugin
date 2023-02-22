package org.metalib.maven.plugins.yaml;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class PropertyToYamlMojoTest {
    @Test
    public void test() throws MojoExecutionException {
        final var mojo = new PropertyToYamlMojo();
        mojo.project = new MavenProject();
        mojo.prefix = "yaml.";
        mojo.outputFile = new File("target//input.yaml");
        mojo.outputFile.delete();
        final var projectProperties = mojo.project.getProperties();
        projectProperties.setProperty("a1", "a1 value");
        projectProperties.setProperty("yaml.p0\\.s0", "p0 s0 value");
        projectProperties.setProperty("yaml.p1", "p1 value");
        projectProperties.setProperty("yaml.p2.0", "p2.0 value");
        projectProperties.setProperty("yaml.p2.1", "p2.1 value");
        projectProperties.setProperty("yaml.p2.2", "p2.2 value");
        projectProperties.setProperty("yaml.p2.3", "p2.3-value");
        projectProperties.setProperty("yaml.p3.1", "p3.1-value");
        projectProperties.setProperty("yaml.p3.a", "p3.a value");
        projectProperties.setProperty("yaml.p3.2", "p3.3-value");
        mojo.execute();
        assertTrue(mojo.outputFile.exists());
    }
}
