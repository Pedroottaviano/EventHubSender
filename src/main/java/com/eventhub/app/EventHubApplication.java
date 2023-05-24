package com.eventhub.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@SpringBootApplication
public class EventHubApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHubApplication.class);
	private static final Sinks.Many<Message<String>> many = Sinks.many().unicast().onBackpressureBuffer();


	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(EventHubApplication.class, args);
	}

	@Bean
	public Supplier<Flux<Message<String>>> supply() {
		return ()->many.asFlux()
				.doOnNext(m->LOGGER.info("Manually sending message {}", m))
				.doOnError(t->LOGGER.error("Error encountered", t));
	}

	@Override
	public void run(String... args) {
		LOGGER.info("Going to add message {} to sendMessage.", "Hello Word");
		many.emitNext(MessageBuilder.withPayload("Hello Word").build(), Sinks.EmitFailureHandler.FAIL_FAST);
	}
}