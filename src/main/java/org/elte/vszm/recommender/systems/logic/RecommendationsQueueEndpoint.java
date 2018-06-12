package org.elte.vszm.recommender.systems.logic;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.*;
import org.elte.vszm.recommender.systems.communication.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

@Component
@DependsOn("recommenderEngine")
public class RecommendationsQueueEndpoint {

	private static final Logger LOGGER = LogManager.getLogger();

	@Value("${recommendation.method}")
	private String listenerChannel;


	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RecommenderEngine engine;

	@PostConstruct
	private void startListener() throws IOException, TimeoutException {
		final Connection inboundConnection = connectionFactory.newConnection();
		final Channel inboundChannel = inboundConnection.createChannel();
		inboundChannel.queueDeclare(listenerChannel, true, false, false, null);

		LOGGER.info("Listening to incoming messages on: |{}|", listenerChannel);
		inboundChannel.basicConsume(listenerChannel, true, new DefaultConsumer(inboundChannel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
				byte[] body) throws IOException {
				Message message = objectMapper.readValue(body, Message.class);
				LOGGER.info("Received from queue: |{}| the message: |{}|", listenerChannel, message);
				if ("getRecommendations".equals(message.getMethod())) {
					List<MovieRecommendation> recommendations = engine.getMovieRecommendationsForUser(
						message.getTask().getExternalId(),
						message.getTask().getMaxResults());

					RecommendationResponse response = RecommendationResponse.builder()
						.itemCount(recommendations.size())
						.itemList(recommendations)
						.build();

					sendResponse(response, envelope, inboundChannel, properties);
				} else {
					LOGGER.warn("Ignoring message as it is not a recommendation request!");
				}
			}
		});

	}

	private void sendResponse(RecommendationResponse response, Envelope envelope,
		Channel inboundChannel,
		AMQP.BasicProperties properties) throws IOException {
		AMQP.BasicProperties replyProps = new AMQP.BasicProperties
			.Builder()
			.correlationId(properties.getCorrelationId())
			.build();

		String responseBody = objectMapper.writeValueAsString(response);
		inboundChannel.basicPublish("", properties.getReplyTo(), replyProps, responseBody.getBytes("UTF-8"));
		inboundChannel.basicAck(envelope.getDeliveryTag(), false);
	}


}
