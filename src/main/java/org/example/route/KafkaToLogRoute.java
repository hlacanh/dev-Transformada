package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.component.jackson.JacksonDataFormat;


@ApplicationScoped
public class KafkaToLogRoute extends RouteBuilder {

    @Override
    public void configure() {
        JacksonDataFormat jsonDataFormat = new JacksonDataFormat();
        jsonDataFormat.setPrettyPrint(false);

        from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
            .routeId("kafka-jslt-log")
            // Opción 1: Usar un procesador para imprimir sin formato
            .process(exchange -> {
                String rawBody = exchange.getMessage().getBody(String.class);
                System.out.println("Mensaje original desde Kafka (procesador): " + rawBody);
            })
            // Opción 2: Usar el logger con nivel específico
            .log(LoggingLevel.INFO, "Mensaje original desde Kafka (log): ${body}")
            // Opción 3: Usar marshal/unmarshal para formatear
            .marshal(jsonDataFormat)
            .log("Mensaje original sin indentación: ${body}")
            .unmarshal(jsonDataFormat)
            // Aplicar transformación JSLT
            .to("jslt:classpath:transformacion.jslt")
            // Log del mensaje transformado
            .log("Mensaje transformado: ${body}");
    }
}
