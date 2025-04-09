package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaToLogRoute extends RouteBuilder {

    JsonDataFormat jsonDataFormat = new JsonDataFormat();
    jsonDataFormat.setPrettyPrint(false);

    @Override
    public void configure() {
        from("kafka:my-topic10?brokers=cluster-nonprod01-kafka-bootstrap.amq-streams-kafka:9092")
            .routeId("kafka-jslt-log")
            .process(exchange -> {
                String rawBody = exchange.getMessage().getBody(String.class);
                System.out.println("Mensaje original desde kafka (procesador):" + rawBody);
        
            })
            .log(LoggingLevel.INFO, "Mensaje original desde kafka (log): $ {body}")
            .marshal(jsonDataFormat)
            .log("Mensaje original sin identacion: ${body}")
            .unmarshal(jsonDataFormat)
            .to("jslt:classpath:transformacion.jslt")
            .log("Mensaje transformado: ${body}");
    }
}
