package org.metalib.maven.plugins.yaml;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;

import static org.metalib.maven.plugins.yaml.PropertyToMap.jacksonYaml;

/**
 * goal produces an output of maven properties to the output yaml file.
 */
@Mojo(name = "dump", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DumpMojo extends AbstractMojo {

    @Requirement
    Logger logger;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "prop2swing.properties")
    Properties properties;

    @Parameter(property = "prop2swing.prefix", defaultValue = "")
    String prefix;

    @Parameter(property = "prop2swing.outputFile", defaultValue = "${project.build.directory}/output.properties", required = true)
    File outputFile;

    @Override
    public void execute() throws MojoExecutionException {
        final var prefixSize = null == prefix? 0 : prefix.length();
        final var projectProperties = new Properties();
        final var propertyCollector = (BiConsumer<? super Object, ? super Object>) (k, v) -> {
            final var key = k.toString();
            if (prefixSize < key.length() && (null == prefix || k.toString().startsWith(prefix))) {
                final var value = v.toString();
                projectProperties.setProperty(key.substring(prefixSize), value);
            }
        };
        project.getProperties().forEach(propertyCollector);
        if (null != properties) {
            properties.forEach(propertyCollector);
        }
        try {
            Files.createDirectories(outputFile.getParentFile().toPath());
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
        try(final var outputStream = new FileOutputStream(outputFile)) {
            projectProperties.store(outputStream, "POM " + Optional.ofNullable(prefix).orElse(""));
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }
}
