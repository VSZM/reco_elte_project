package org.elte.vszm.recommender.systems.logic;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.*;
import org.elte.vszm.recommender.systems.communication.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

@Component
public class MainLogic extends Logic {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String REGISTRATION_QUEUE_NAME = "ReCoEngineRegistry";
	private static final String SERVICE_QUEUE_NAME = "KnowledgeBaseService#ml_1m";
	private static final String QUEUE_NAME = "dbalan";

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ConnectionFactory factory;
	private final Registration registration = new Registration();

	private void register() throws Exception {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(REGISTRATION_QUEUE_NAME, true, false, false, null);

		final String message = objectMapper.writeValueAsString(registration);
		LOGGER.info("Sending message |{}| to Queue |{}|", message, REGISTRATION_QUEUE_NAME);
		channel.basicPublish("", REGISTRATION_QUEUE_NAME, null
			, message.getBytes("UTF-8"));

		channel.close();
		connection.close();
	}

	private void pullData() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
				byte[] body)
				throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

	@Override
	public void executeBusinessLogic() throws Exception {
		pullData();
	}


}
