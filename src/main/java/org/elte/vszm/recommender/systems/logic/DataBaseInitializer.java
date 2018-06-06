package org.elte.vszm.recommender.systems.logic;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.*;
import org.elte.vszm.recommender.systems.communication.Emit;
import org.elte.vszm.recommender.systems.data.DataBase;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

@Component
public class DataBaseInitializer {

	private static final Logger LOGGER = LogManager.getLogger();


	@Value("#{systemProperties['java.io.tmpdir']} + $document.store.folder}")
	private String documentStoreFolder;
	@Value("${request.data.channel.name}")
	private String requestDataChannel;
	@Value("${data.accepting.channel.name}")
	private String dataAcceptingChannelName;


	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private DataBase db;
	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	private void initializeDataBase() {
		LOGGER.info("Intitializing Database");
		if (Files.isDirectory(Paths.get(documentStoreFolder))) {
			loadDBFromFileCache();
		} else {
			loadDBFromQueue();
		}
	}

	private void loadDBFromQueue() {
		objectMapper.setDefaultPropertyInclusion(Include.ALWAYS);
		LOGGER.info("Requesting database from server");

		try {
			final Connection outboundConnection = connectionFactory.newConnection();
			final Connection inboundConnection = connectionFactory.newConnection();
			final Channel outboundChannel = outboundConnection.createChannel();
			final Channel inboundChannel = inboundConnection.createChannel();
			outboundChannel.queueDeclare(requestDataChannel, true, false, false, null);
			inboundChannel.queueDeclare(dataAcceptingChannelName, true, false, false, null);

			final String corrId = UUID.randomUUID().toString();

			AMQP.BasicProperties props = new AMQP.BasicProperties
				.Builder()
				.correlationId(corrId)
				.replyTo(dataAcceptingChannelName)
				.build();

			Emit emit = new Emit(dataAcceptingChannelName);

			LOGGER.info("Listening to incoming messages on: |{}|", dataAcceptingChannelName);
			inboundChannel.basicConsume(dataAcceptingChannelName, true, new DefaultConsumer(inboundChannel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
					if (corrId.equals(properties.getCorrelationId())) {
						String jsonData = new String(body, "UTF-8");
						LOGGER.info("Received message: |{}|", jsonData);
					} else {
						LOGGER.warn("Correlation ID mismatch! Expected: |{}|, Got: |{}|", corrId,
							properties.getCorrelationId());
					}
				}
			});

			String jsonEmitMessage = objectMapper.writeValueAsString(emit);
			outboundChannel.basicPublish("", requestDataChannel, props, jsonEmitMessage.getBytes("UTF-8"));
			LOGGER.info("Sent request |{}| to server queue |{}|", jsonEmitMessage, requestDataChannel);

			outboundChannel.close();
			outboundConnection.close();
		} catch (IOException | TimeoutException e) {
			LOGGER.fatal("Failed to load data from queue", e);
			throw new RuntimeException(e);
		}

		objectMapper.setDefaultPropertyInclusion(Include.NON_EMPTY);
	}

	private void loadDBFromFileCache() {
		LOGGER.info("Loading cached data from files");

	}


}
