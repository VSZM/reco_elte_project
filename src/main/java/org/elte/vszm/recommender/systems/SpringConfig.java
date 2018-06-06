package org.elte.vszm.recommender.systems;

import org.elte.vszm.recommender.systems.logic.MainLogic;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import com.rabbitmq.client.ConnectionFactory;

@SpringBootApplication
@PropertySource({"classpath:application.properties"})
@ComponentScan(basePackages = {"org.elte.vszm.recommender.systems"})
public class SpringConfig {



	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}


	@Value("${mq.host}")
	private String mqHost;
	@Value("${mq.port}")
	private int mqPort;

	@Autowired
	private MainLogic mainLogic;

	@Bean
	public ConnectionFactory createConnectionFactory(){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(mqHost);
		factory.setPort(mqPort);

		return factory;
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			mainLogic.executeBusinessLogic();
		};
	}


}
