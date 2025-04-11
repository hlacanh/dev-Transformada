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

        try {
            from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
                .routeId("kafka-jslt-log")
                // Opción 1: Mostrar mensaje original usando procesador
                .process(exchange -> {
                    String rawBody = exchange.getMessage().getBody(String.class);
                    System.out.println("Mensaje original desde Kafka (procesador): " + rawBody);
                })
                // Aquí usamos el procesador JsltProcessor en lugar de to()
                .to("jslt:classpath:transformacion.jslt")
                .log("JSON de entrada: ${body}")
                .setHeader("Content-Type", constant("application/vnd.kafka.json.v2+json"))
                .setHeader("Accept", constant("application/json"))
                .setHeader("user_key", constant("bdc96a4bd17bbe1f7c1960b0912e68a0"))
                .to("https://prdct-transact-env0-test-3scale-apicast-staging.apps.os-nonprod.domain.net/CreateLoan?httpMethod=POST")
    
                .log("Respuesta de la api: ${body}")
                .log("Mensaje transformado: ${body}");
                } catch(Exception e) {
                    System.err.println("Error al configurar la ruta: " + e.getMessage());
                    e.printStackTrace();
                }
        }
    }