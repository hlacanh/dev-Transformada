package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaToLogRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
            .process(exchange -> {
                String rawBody = exchange.getMessage().getBody(String.class);
                System.out.println("Mensaje original desde kafka (procesador):" + rawBody);
        
            })
            .routeId("kafka-jslt-log")
            .log("Mensaje original desde Kafka: ${body}")
            .to("jslt:classpath:transformacion.jslt")
            .log("Mensaje transformado: ${body}");
    }
}
