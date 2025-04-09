package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.component.jackson.JacksonDataFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class KafkaToLogRoute extends RouteBuilder {

    @Override
    public void configure() {
        JacksonDataFormat jsonDataFormat = new JacksonDataFormat();
        jsonDataFormat.setPrettyPrint(false);

        from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
            .routeId("kafka-jslt-log")
            // Opción 1: Mostrar mensaje original usando procesador
            .process(exchange -> {
                String rawBody = exchange.getMessage().getBody(String.class);
                System.out.println("Mensaje original desde Kafka (procesador): " + rawBody);
            })
            // Opción 2: Log estándar
            .log(LoggingLevel.INFO, "Mensaje original desde Kafka (log): ${body}")
            .process(exchange -> {
                try {
                    String jsonBody = exchange.getMessage().getBody(String.class);
                    // Usar ObjectMapper para compactar el JSON
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    
                    Object json = mapper.readValue(jsonBody, Object.class);
                    String compactJson = mapper.writeValueAsString(json);
                    
                    System.out.println("JSON compacto sin indentación: " + compactJson);
                    // Modificamos el body para que se envíe el JSON compacto
                    exchange.getMessage().setBody(compactJson);
                } catch (Exception e) {
                    System.err.println("Error al procesar JSON: " + e.getMessage());
                    // No interrumpimos el flujo en caso de error
                }
            })

           .log("JSON que va a ser transformado por JSLT: ${body}")
            // Aplicar transformación JSLT
            .to("jslt:classpath:transformacion.jslt")
    }
}
