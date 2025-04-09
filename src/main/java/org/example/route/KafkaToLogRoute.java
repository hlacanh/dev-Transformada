package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.component.jackson.JacksonDataFormat;
import con.fasterxml.jackson.daabind.ObjectMapper;

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
            // Opción 3: Mostrar JSON compacto sin indentación
            .process(exchange -> {
                try {
                    String jsonBody = exchange.getMessage().getBody(String.class);
                    // Usar ObjectMapper para compactar el JSON
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(jsonBody, Object.class);
                    String compactJson = mapper.writeValueAsString(json);
                    
                    System.out.println("JSON compacto sin indentación: " + compactJson);
                    // No modificamos el body para no afectar el flujo
                } catch (Exception e) {
                    System.err.println("Error al procesar JSON: " + e.getMessage());
                }
            })
            // Aplicar transformación JSLT
            .to("jslt:classpath:transformacion.jslt")
            // Log del mensaje transformado
            .log("Mensaje transformado: ${body}");
    }
}
