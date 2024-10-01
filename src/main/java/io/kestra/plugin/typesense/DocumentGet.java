package io.kestra.plugin.typesense;

import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.resources.Node;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Get a document from a Typesense base"
)
@Plugin(
    examples = {
        @io.kestra.core.models.annotations.Example(
            title = "Index a document to a Typesense base",
            code = { "documentId: \"id of the document to get\", " +
                "host: \"Host of the typesense DB\", " +
                "port: \"Port of the typesense DB\", " +
                "apiKey: \"Api key of the typesense DB\", " +
                "collection: \"Collection of the typesense DB\", " }
        )
    }
)
public class DocumentGet extends Task implements RunnableTask<DocumentGet.Output> {
    @Schema(
        title = "The id of the document to get",
        example = "0"
    )
    @PluginProperty(dynamic = true)
    private String documentId;

    @Schema(
        title = "The host of the typsense base",
        example = "localhost"
    )
    @PluginProperty(dynamic = true)
    private String host;

    @Schema(
        title = "The port of the typsense base",
        example = "8108"
    )
    @PluginProperty(dynamic = true)
    private String port;

    @Schema(
        title = "The api key to connect to the typsense base",
        example = "my_key"
    )
    @PluginProperty(dynamic = true)
    private String apiKey;

    @Schema(
        title = "The name of the typsense collection",
        example = "my_collection"
    )
    @PluginProperty(dynamic = true)
    private String collection;


    @Override
    public DocumentGet.Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();

        String typesenseDocumentId = runContext.render(documentId);
        String typesenseHost = runContext.render(host);
        String typesensePort = runContext.render(port);
        String typesenseApiKey = runContext.render(apiKey);
        String typesenseCollection = runContext.render(collection);
        logger.debug(documentId);

        Configuration configuration = new Configuration(
            List.of(new Node("http", typesenseHost, typesensePort)),
            Duration.ofSeconds(2),typesenseApiKey);

        Client client = new Client(configuration);
        Map<String, Object> document = client.collections(typesenseCollection).documents(typesenseDocumentId).retrieve();

        return Output.builder()
            .child(new OutputChild(document))
            .build();
    }

    /**
     * Input or Output can be nested as you need
     */
    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "Short description for this output",
            description = "Full description of this output"
        )
        private final OutputChild child;
    }

    @Builder
    @Getter
    public static class OutputChild implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "Short description for this output",
            description = "Full description of this output"
        )
        private Map<String, Object> document;
    }
}
