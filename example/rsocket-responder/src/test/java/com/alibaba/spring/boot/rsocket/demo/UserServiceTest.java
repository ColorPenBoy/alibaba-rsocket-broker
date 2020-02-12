package com.alibaba.spring.boot.rsocket.demo;

import com.alibaba.rsocket.metadata.GSVRoutingMetadata;
import com.alibaba.rsocket.metadata.MessageMimeTypeMetadata;
import com.alibaba.rsocket.metadata.RSocketCompositeMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.uri.UriTransportRegistry;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * user service test
 *
 * @author leijuan
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    RSocket rsocket;

    @BeforeAll
    public void setUp() throws Exception {
        rsocket = RSocketFactory.connect()
                .transport(UriTransportRegistry.clientForUri("tcp://127.0.0.1:42252"))
                .start()
                .block();
    }

    @AfterAll
    public void tearDown() throws Exception {
        rsocket.dispose();
    }

    @Test
    public void testFindById() throws Exception {
        RSocketCompositeMetadata compositeMetadata = new RSocketCompositeMetadata();
        GSVRoutingMetadata routingMetadata = new GSVRoutingMetadata("", "com.alibaba.user.UserService", "findById", "");
        compositeMetadata.addMetadata(routingMetadata);
        MessageMimeTypeMetadata dataEncodingMetadata = new MessageMimeTypeMetadata(WellKnownMimeType.APPLICATION_JSON);
        compositeMetadata.addMetadata(dataEncodingMetadata);
        rsocket.requestResponse(DefaultPayload.create(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(1)), compositeMetadata.getContent()))
                .subscribe(payload -> {
                    System.out.println(payload.getDataUtf8());
                });
        Thread.sleep(1000);
    }
}
