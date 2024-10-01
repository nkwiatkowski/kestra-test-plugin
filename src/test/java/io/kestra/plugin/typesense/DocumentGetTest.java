package io.kestra.plugin.typesense;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.typesense.typesense.TypesenseContainer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This test will only test the main task, this allow you to send any input
 * parameters to your task and test the returning behaviour easily.
 */
@KestraTest
class DocumentGetTest extends TypesenseContainer {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void run() throws Exception {
        Map<String, Object> document = Map.of("countryName", "France", "capital", "Paris", "gdp", 123456);
        client.collections("Countries").documents().upsert(document);

        RunContext runContext = runContextFactory.of(Map.of());

        DocumentGet task = DocumentGet.builder()
            .documentId("0")
            .apiKey("test-key")
            .port("8108")
            .host("localhost")
            .collection("Countries")
            .build();

        DocumentGet.Output runOutput = task.run(runContext);

        assertThat(runOutput.getChild().getDocument().get("countryName"), is("France"));
        assertThat(runOutput.getChild().getDocument().get("capital"), is("Paris"));
        assertThat(runOutput.getChild().getDocument().get("gdp"), is(123456));
    }
}
