package io.kestra.plugin.typesense.typesense;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;
import org.typesense.resources.Node;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TypesenseContainer {

    private static GenericContainer<?> typesenseContainer;
    protected static Client client;

    @BeforeAll
    public static void setUp() throws Exception {
        typesenseContainer = new FixedHostPortGenericContainer<>("typesense/typesense:0.24.0")
            .withExposedPorts(8108)
            .withFixedExposedPort(8108, 8108)
            .withEnv("TYPESENSE_API_KEY", "test-key")
            .withEnv("TYPESENSE_DATA_DIR", "/tmp");

        typesenseContainer.start();

        Configuration configuration = new Configuration(
            List.of(new Node("http", "localhost", "8108")),
            Duration.ofSeconds(2),"test-key");

        client = new Client(configuration);

        List<Field> fields = new ArrayList<>();
        fields.add(new Field().name("countryName").type(FieldTypes.STRING));
        fields.add(new Field().name("capital").type(FieldTypes.STRING));
        fields.add(new Field().name("gdp").type(FieldTypes.INT32).facet(true).sort(true));

        CollectionSchema collectionSchema = new CollectionSchema();
        collectionSchema.name("Countries").fields(fields).defaultSortingField("gdp");

        client.collections().create(collectionSchema);

    }

    @AfterAll
    public static void tearDown() {
        // Stop the container after tests
        typesenseContainer.stop();
    }
}
