package org.metalib.maven.plugins.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Property to Map/Collection Helper Class
 */
public class PropertyToMap {

    static final ObjectMapper jacksonYaml = new ObjectMapper(new YAMLFactory());

    static final String ESCAPE_BACKSLASH_CHAR = ""+14;
    static final String ESCAPE_DOT_CHAR = ""+15;

    private PropertyToMap() {
        // Empty
    }

    /**
     * Convert plain properties to a yaml tree.
     * @param properties properties collection
     * @return either {@link LinkedHashMap} or {@link java.util.Collection} instance
     */
    public static Object transform(Properties properties) {
        final var result = new LinkedHashMap<String, Object>();
        properties.forEach((k, v) -> {
            final var subkeys = ((String) k)
                    .replace("\\\\", ESCAPE_BACKSLASH_CHAR)
                    .replace("\\.", ESCAPE_DOT_CHAR)
                    .split("\\.");
            final var lastI = subkeys.length - 1;
            var subkey = (String) null;
            var value  = (Object) null;
            var current = result;
            for (var i=0; i<subkeys.length; i++) {
                subkey = subkeys[i];
                value = current.get(subkey);
                if (i == lastI) {
                    // Empty
                } else if (value instanceof LinkedHashMap) {
                    current = (LinkedHashMap) value;
                } else {
                    final var c = new LinkedHashMap<String, Object>();
                    current.put(subkey.replace(ESCAPE_DOT_CHAR, ".").replace(ESCAPE_BACKSLASH_CHAR, "\\"), c);
                    current = c;
                }
            }
            if (null != subkey) {
                current.put(subkey.replace(ESCAPE_DOT_CHAR, ".").replace(ESCAPE_BACKSLASH_CHAR, "\\"), v);
            }
        });
        return transformIfList(result);
    }

    static Object transformIfList(LinkedHashMap<String, Object> input) {
        final var map = input.keySet().stream().filter(v -> {try {
            parseInt(v);
            return true;
        } catch (Exception e) {
            return false;
        }}).collect(Collectors.toMap(Integer::parseInt, v -> v));
        input.forEach((k, v) -> {
            if (v instanceof LinkedHashMap) {
                input.put(k, transformIfList((LinkedHashMap<String, Object>) v));
            }
        });
        if (0 == input.size()) {
            return null;
        } else if (map.size() == input.size()) {
            return map.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
        } else {
            return input;
        }
    }

    static void traverse(Properties properties, String prefix, Map<String, Object> map) {
        map.forEach((k,v) -> {
            final var key = k.replace("\\", "\\\\").replace(".", "\\.");
            if (v instanceof Map) {
                traverse(properties, prefix + key + ".", (Map) v);
            } else if (v instanceof Collection) {
                traverse(properties, prefix + key + ".", (Collection) v);
            } else {
                properties.setProperty(prefix + key, v.toString());
            }
        });
    }

    static void traverse(Properties properties, String key, Collection<?> collection) {
        int i = 0;
        for (final var value : collection) {
            final var k = key + i;
            if (value instanceof Map) {
                traverse(properties, k + ".", (Map) value);
            } else if (value instanceof Collection) {
                traverse(properties, k + ".", (Collection) value);
            } else {
                properties.setProperty(key + i, value.toString());
            }
            i++;
        }
    }
}
