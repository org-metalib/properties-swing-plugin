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
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;
import static org.metalib.maven.plugins.yaml.PropertyToMap.jacksonYaml;
import static org.metalib.maven.plugins.yaml.PropertyToMap.traverse;

/**
 * goal reads properties from yaml file and puts them to maven properties.
 */
@Mojo(name = "2prop", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class YamlToPropertyMojo extends AbstractMojo {

    @Requirement
    Logger logger;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "prop2swing.prefix", defaultValue = "")
    String prefix;

    @Parameter(property = "prop2swing.inputFile", required = true)
    File inputFile;

    @Parameter(property = "prop2swing.noOverride")
    Boolean noOverride;

    @Override
    public void execute() throws MojoExecutionException {
        if (null == inputFile) {
            throw new MojoExecutionException("<inputFile> has not been specified.");
        }
        if (!inputFile.exists()) {
            throw new MojoExecutionException(format("<inputFile> does not exists <%s>.", inputFile.toString()));
        }
        try {
            final var properties = new Properties();
            final var input = jacksonYaml.readValue(inputFile, Object.class);
            if (input instanceof Map) {
                traverse(properties, prefix, (Map) input);
            } else if (input instanceof Collection) {
                traverse(properties, prefix, (Collection<?>) input);
            }
            final var projectProperties = project.getProperties();
            final var override = Optional.ofNullable(noOverride).filter(v -> !v).isPresent();
            properties.forEach((k,v) -> {
                final var key = k.toString();
                if (override || !projectProperties.contains(key)) {
                    projectProperties.setProperty(key, v.toString());
                }
            });
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }
}
