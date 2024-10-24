package com.amnex.agristack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.KafkaTopicConfig;

@Service
public class KafkaProducer {

//	@Autowired
//	private KafkaTemplate<String, String> kafkaTemplate;
//	 
//
//	public void sendMessage(String message) {
//		System.err.println("KAFKA PROD "  + message);
//				
//		kafkaTemplate.send(KafkaTopicConfig.DATA_TOPIC_NAME, message);
//	}

}
