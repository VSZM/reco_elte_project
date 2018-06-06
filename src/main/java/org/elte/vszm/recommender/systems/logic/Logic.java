package org.elte.vszm.recommender.systems.logic;

import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.ConnectionFactory;

public abstract class Logic {

	@Autowired
	private ConnectionFactory factory;

	public abstract void executeBusinessLogic() throws Exception;

}
