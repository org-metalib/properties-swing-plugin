# Maven Properties Swing plugin

The plugin converts maven properties from/to yaml format. It recognizes dot (`.`) character a separator for chained property names.
To escape this feature, the escape backslash (`\`) character could be used. An escape sequence for the backslash character is the double backslash (`\\`).  

For example, the following maven properties with the prefix filter `k8s.config.`:
```xml
<properties>
    <k8s.config.apiVersion>v1</k8s.config.apiVersion>
    <k8s.config.kind>Pod</k8s.config.kind>
    <k8s.config.metadata.name>project level name</k8s.config.metadata.name>
    <k8s.config.metadata.spec.containers.1.name>mypod</k8s.config.metadata.spec.containers.1.name>
    <k8s.config.metadata.spec.containers.1.image>redis</k8s.config.metadata.spec.containers.1.image>
    <k8s.config.metadata.spec.containers.1.volumeMounts.1.name>foo</k8s.config.metadata.spec.containers.1.volumeMounts.1.name>
    <k8s.config.metadata.spec.containers.1.volumeMounts.1.mountPath>/etc/foos</k8s.config.metadata.spec.containers.1.volumeMounts.1.mountPath>
    <k8s.config.metadata.spec.containers.1.volumeMounts.1.readOnly>true</k8s.config.metadata.spec.containers.1.volumeMounts.1.readOnly>
    <k8s.config.metadata.spec.volumes.1.name>foo</k8s.config.metadata.spec.volumes.1.name>
    <k8s.config.metadata.spec.volumes.1.secret.secretName>mysecret</k8s.config.metadata.spec.volumes.1.secret.secretName>
    <k8s.config.metadata.spec.volumes.1.secret.optional>true</k8s.config.metadata.spec.volumes.1.secret.optional>
</properties>
```

will be saved as the following yaml file:
```yaml
---
metadata:
  spec:
    containers:
    - volumeMounts:
      - readOnly: true
        name: "foo"
        mountPath: "/etc/foos"
      image: "redis"
      name: "mypod"
    volumes:
    - name: "foo"
      secret:
        secretName: "mysecret"
        optional: true
  name: "project level name"
apiVersion: "v1"
kind: "Pod"
```

Here is the plugin configuration:
```xml
<plugin>
    <groupId>org.metalib.maven.plugins.yaml</groupId>
    <artifactId>properties-swing-plugin</artifactId>
    <version>${properties-swing-plugin.version}</version>
    <executions>
        <execution>
            <id>k8s-secret</id>
            <goals>
                <goal>2yaml</goal>
            </goals>
            <phase>initialize</phase>
            <configuration>
                <outputFile>${project.build.directory}/yaml/root.yaml</outputFile>
                <prefix>k8s.config.</prefix>
                <properties>
                    <k8s.config.metadata.name>plugin level name ${project.artifactId}</k8s.config.metadata.name>
                </properties>
            </configuration>
        </execution>
    </executions>
</plugin>
```

To get the output file, run the following command:
```shell
mvn prop2swing:2yaml@k8s-secret
```

## Goal `2yaml`
This goal takes maven properties and flushes them to an output file in yaml format.

## Goal `2prop`
This goal takes a file in yaml format transforms to property format and adds to maven property

## Goal `dump`
This goal dumps mavem pom properties to the output file
