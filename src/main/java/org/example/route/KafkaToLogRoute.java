package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaToLogRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
            .routeId("kafka-jslt-log")
            .log("Mensaje original desde Kafka: ${body}")
            .to("jslt:classpath:transformacion.jslt")
            .log("Mensaje transformado: ${body}");

    }
}
