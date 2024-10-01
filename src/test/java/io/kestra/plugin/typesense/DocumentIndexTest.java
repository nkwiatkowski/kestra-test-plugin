package io.kestra.plugin.typesense;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.typesense.typesense.TypesenseContainer;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.typesense.model.CollectionResponse;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This test will only test the main task, this allow you to send any input
 * parameters to your task and test the returning behaviour easily.
 */
@KestraTest
class DocumentIndexTest extends TypesenseContainer {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void run() throws Exception {
        RunContext runContext = runContextFactory.of(Map.of());

        DocumentIndex task = DocumentIndex.builder()
            .document(Map.of("countryName", "France", "capital", "Paris", "gdp", 123456))
            .apiKey("test-key")
            .port("8108")
            .host("localhost")
            .collection("Countries")
            .build();

        DocumentIndex.Output runOutput = task.run(runContext);

        assertThat(runOutput.getChild().getDocument(), is(Map.of("countryName", "France", "capital", "Paris", "gdp", 123456, "id", "0")));

        Map<String, Object> country = client.collections("Countries").documents("0").retrieve();
        assertThat(country.get("countryName"), is("France"));
        assertThat(country.get("capital"), is("Paris"));
        assertThat(country.get("gdp"), is(123456));
    }
}
