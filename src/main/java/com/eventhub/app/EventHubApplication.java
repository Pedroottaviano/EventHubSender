package com.eventhub.app;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.SendOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@SpringBootApplication
public class EventHubApplication implements CommandLineRunner {
	private EventHubProducerClient producer;

	public void EventHubMessageProducer(String connectionString, String eventHubName) {
		this.producer = new EventHubClientBuilder()
				.connectionString(connectionString, eventHubName)
				.buildProducerClient();
	}

	public void sendMessages(Flux<Message<GoldenGateEvent>> messageFlux) {
		messageFlux
				.map(convertToEventData())
				.map(eventData -> new SendOptions().setPartitionId(eventData.getPartitionKey()))
				.flatMap(eventData -> producer.send(eventData))
				.blockLast();
		producer.close();
	}

	private Function<Message<GoldenGateEvent>, EventData> convertToEventData() {
		return message -> {
			byte[] body = message.getPayload().getBytes(StandardCharsets.UTF_8);
			EventData eventData = new EventData(body);
			eventData.getProperties().put("timestamp", Instant.now().toString());
			return eventData;
		};
	}
	public static void main(String[] args) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();

		final String connectionString = "Endpoint=sb://oeegoldengate-uw-ehns-q.servicebus.windows.net/;SharedAccessKeyName=writing;SharedAccessKey=LCqqs/mJOA/M9cJGC8oG2I3grG612dP6a+AEhLltMr4=;EntityPath=patrondb-uw-eh-q";
		final String eventHubName = "patrondb-uw-eh-q";

		GoldenGateEvent testedEvent1 = new GoldenGateEvent();
		GoldenGateEvent testedEvent2 = new GoldenGateEvent();

		testedEvent1.setTable("1st Event Table");
		testedEvent1.setOpType("I");
		testedEvent1.setOpTimestamp(new Date());
		testedEvent1.setCurrentTimestamp(new Date());
		testedEvent1.setPos("1st Event Pos");
		testedEvent1.setBefore(new HashMap<>());
		testedEvent1.setAfter(new HashMap<>());

		testedEvent2.setTable("2st Event Table");
		testedEvent2.setOpType("U");
		testedEvent2.setOpTimestamp(new Date());
		testedEvent2.setCurrentTimestamp(new Date());
		testedEvent2.setPos("2st Event Pos");
		testedEvent2.setBefore(new HashMap<>());
		testedEvent2.setAfter(new HashMap<>());

		// create a producer using the namespace connection string and event hub name
		EventHubProducerClient producer = new EventHubClientBuilder()
				.connectionString(connectionString, eventHubName)
				.buildProducerClient();

		// prepare a batch of events to send to the event hub
		EventDataBatch batch = producer.createBatch();
		batch.tryAdd(new EventData(objectMapper.writeValueAsString(testedEvent1)));
		batch.tryAdd(new EventData(testedEvent2.toString()));
		batch.tryAdd(new EventData("Third event"));
		batch.tryAdd(new EventData("Fourth event"));
		batch.tryAdd(new EventData("Fifth event"));

		// send the batch of events to the event hub
		producer.send(batch);
		// close the producer
		producer.close();
	}

	@Override
	public void run(String... args) throws Exception {

	}
}