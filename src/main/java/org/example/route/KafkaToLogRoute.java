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
                    .log("Respuesta de la api: ${body}");
        }
        catch(Exception e) {
            System.out.println("Ha ocurrido un error con la configuracion del endopint " + e.getMessage() );

        }

        
    }
}