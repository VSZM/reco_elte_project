package org.elte.vszm.recommender.systems.logic;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.*;
import org.elte.vszm.recommender.systems.communication.Emit;
import org.springframework.beans.factory.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

//@Component
@Deprecated// Not gonna use the QUEUE
public class QueueDataBaseInitializer {

	private static final Logger LOGGER = LogManager.getLogger();

	// add TrueRatingService#ml_1m to get the ratings as well, Same emit message, but an additional itemCount is there as
// well.
	// LSTM solution reco
	@Value("#{systemProperties['java.io.tmpdir']} + $document.store.folder}")
	private String documentStoreFolder;
	@Value("${request.nodes.data.channel.name}")
	private String nodesRequestDataChannel;
	@Value("${request.ratings.data.channel.name}")
	private String ratingsRequestDataChannel;
	@Value("${nodes.data.accepting.channel.name}")
	private String nodesDataAcceptingChannelName;
	@Value("${ratings.data.accepting.channel.name}")
	private String ratingsDataAcceptingChannelName;
	@Value("${data.itemcount}")
	private Integer itemCount;


	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private Database db;
	@Autowired
	private ObjectMapper objectMapper;

	CountDownLatch latch;


	@PostConstruct
	private void initializeDataBase() throws InterruptedException {
		LOGGER.info("Intitializing Database");
		latch = new CountDownLatch(0);
		if (!Files.isDirectory(Paths.get(documentStoreFolder))) {
			latch = new CountDownLatch(2);
			requestDataFromQueue();
			LOGGER.info("Waiting for incoming queue messages to be consumed");
		}

		latch.await();
		loadDBFromFileCache();
	}

	private void requestDataFromQueue() {
		LOGGER.info("Requesting database from server");

		try {
			requestDataFromChannel(nodesRequestDataChannel, nodesDataAcceptingChannelName, itemCount);
			requestDataFromChannel(ratingsRequestDataChannel, ratingsDataAcceptingChannelName, itemCount);

			// Let's give some time for the server to fill the queue
			Thread.sleep(1000);

			startInboundListener(nodesDataAcceptingChannelName);
			startInboundListener(ratingsDataAcceptingChannelName);
		} catch (IOException | TimeoutException | InterruptedException e) {
			LOGGER.fatal("Failed to load data from queue", e);
			throw new RuntimeException(e);
		}
	}

	private void requestDataFromChannel(String queueName, String dataChannel, Integer itemCount) throws IOException,
		TimeoutException {
		LOGGER.info("Requesting Data from Queue |{}| to Queue |{}|", queueName, dataChannel);

		final Connection outboundConnection = connectionFactory.newConnection();
		final Channel outboundChannel = outboundConnection.createChannel();
		outboundChannel.queueDeclare(queueName, true, false, false, null);

		Emit emit = new Emit(dataChannel, itemCount);

		String jsonEmitMessage = objectMapper.writeValueAsString(emit);
		outboundChannel.basicPublish("", queueName, null, jsonEmitMessage.getBytes("UTF-8"));
		LOGGER.info("Sent request |{}| to server queue |{}|", jsonEmitMessage, queueName);

		outboundChannel.close();
		outboundConnection.close();
	}

	private void startInboundListener(String queueName) throws IOException, TimeoutException {
		final Connection inboundConnection = connectionFactory.newConnection();
		final Channel inboundChannel = inboundConnection.createChannel();
		inboundChannel.queueDeclare(queueName, true, false, false, null);

		LOGGER.info("Listening to incoming messages on: |{}|", queueName);
		inboundChannel.basicConsume(queueName, true, new DefaultConsumer(inboundChannel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
				byte[] body) throws IOException {
				String jsonData = new String(body, "UTF-8");
				LOGGER.info("Received from queue: |{}| the message: |{}|", queueName, jsonData);
				if (inboundChannel.messageCount(queueName) == 0) {
					latch.countDown();
				}
			}
		});

	}

	private void loadDBFromFileCache() {
		LOGGER.info("Building Database from cached files");




	}


}
