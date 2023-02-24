package org.metalib.maven.plugins.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.metalib.maven.plugins.yaml.PropertyToMap.traverse;

public class PropertyToMapTest {

    static final ObjectMapper jacksonYaml = new ObjectMapper(new YAMLFactory());

    @Test
    public void toMap0() throws JsonProcessingException {
        final var prop = new Properties();
        prop.setProperty("root.p0.s0", "v0");
        prop.setProperty("root.p0.s0", "v1");
        prop.setProperty("root0", "root0-v1");
        prop.setProperty("root1", "root1-v1");
        prop.setProperty("root.p1", "root-p1-v1");
        prop.setProperty("root.p2.1", "root-p2-1");
        prop.setProperty("root.p2.2", "root-p2-2");
        prop.setProperty("root.p2.3", "root-p2-3");
        prop.setProperty("root.p2.4", "root-p2-4");
        final var result = PropertyToMap.transform(prop);
        final var yaml = jacksonYaml.writeValueAsString(result);
        assertNotNull(yaml);
    }

    @Test
    public void toMap1() throws JsonProcessingException {
        final var prop = new Properties();
        prop.setProperty("root.a.1.name", "a name that is first");
        prop.setProperty("root.a.2.name", "a name that is second");
        final var result = PropertyToMap.transform(prop);
        final var yaml = jacksonYaml.writeValueAsString(result);
        assertNotNull(yaml);
    }

    @Test
    public void toProperties() throws IOException, URISyntaxException {
        final var input = jacksonYaml.readValue(new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("input.yaml")).toURI()), Object.class);
        final var properties = new Properties();
        final var prefix = "";
        if (input instanceof Map) {
            traverse(properties, prefix, (Map) input);
        } else if (input instanceof Collection) {
            traverse(properties, prefix, (Collection<?>) input);
        }
        assertTrue(0 < properties.size());
    }

}