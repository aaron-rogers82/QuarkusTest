package org.acme;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class RestEndpoints {

    @Inject
    EventBus bus;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public Uni<String> greeting(@PathParam String name) {
        return bus.<String>request("greeting", name)
            .onItem().apply(Message::body);
    }

    public class MyName {
        private String name;

        public MyName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public class MyNameCodec implements MessageCodec<String, MyName> {
        @Override
        public void encodeToWire(Buffer buffer, String s) {

        }

        @Override
        public MyName decodeFromWire(int pos, Buffer buffer) {
            return null;
        }

        @Override
        public MyName transform(String name) {
            return new MyName(name);
        }

        @Override
        public String name() {
            return MyNameCodec.class.getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }

    @ConsumeEvent(value = "greeting")
    Uni<String> greeting(MyName name) {
        return Uni.createFrom().item(() -> "Hello "+name.getName());
    }
}