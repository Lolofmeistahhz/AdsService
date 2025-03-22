package com.example.springgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingGatewayFilter extends AbstractGatewayFilterFactory<LoggingGatewayFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(LoggingGatewayFilter.class);

    public LoggingGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Incoming request: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Response status: {}", exchange.getResponse().getStatusCode());
            }));
        };
    }

    public static class Config {
    }
}