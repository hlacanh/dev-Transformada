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

        from("timer:hardcoded?repeatCount=1")
            .routeId("hardcoded-jslt-log")
            .setBody(constant("{" +
                    " \"body\":{" +
                    " \"customerIds\":[" +
                    "  {" +
                    "      \"customerId\":\"100103\", "+
                    "      \"customerRole\":\"benefitial owner\" "+
                    "  }"+
                    " ], "+
                    "\"properties\":[ "+
                    "    {"+
                    "        \"ACCOUNT\":{"+
                    "              \\"L.TI.GARAN\":\"20\","+
                    "              \"L.DET.GARANTIA\":\"40\","+
                    "              \"CASO.CREATIO\":\"1\","+
                    "              \"N.RESOLUCION\":\"1\","+
                    "              \"Fecha.Form\":\"20250301\","+
                    "              \"Tipo.Docto.Form\":\"6\""+
                    "      }"+
                    "    }"+
                          
                    "],"+
                    "\"productId\":\"NOMINAS.CON.CONVENIO\","+
                    "\"masterArrangementId\":\"\","+
                    "\"roleName\":\"\","+
                    "\"transactionAmount\":\"0\","+
                    "\"reason\":\"\",
                    "\"simulationRunReference\":\"\","+
                    "\"branch\":\"\","+
                    "\"lineOfBusiness\":\"\","+
                    "\"UUID\":\"123\",
                    "\"Terminal\":\"TERMINAL1\","+
                    "\"Channel\":\"1\","+
                    "\"User\":\"HLACAN1\","+
                    "\"arrangementId\":\"NEW\","+
                    "\"currencyId\":\"GTQ\","+
                    "\"activityId\":\"LENDING-NEW-ARRANGEMENT\""+
                  "} }"
                
            ))
            ("Mensaje hardCoded ${body}")
            .to("jslt:classpath:transformacion.jslt")
            .log("Mensaje hardcoded: ${body}");
    }
}
