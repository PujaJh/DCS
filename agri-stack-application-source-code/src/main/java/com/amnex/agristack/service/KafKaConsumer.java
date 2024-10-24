package com.amnex.agristack.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.config.KafkaTopicConfig;

@Service
public class KafKaConsumer {
 
//	
//	@KafkaListener(topics = KafkaTopicConfig.DATA_TOPIC_NAME, groupId = KafkaTopicConfig.K_GROUP_ID)
//	public void consumeCropSurveyData(String message) {
//		System.out.println("Message received -> " + message);
//	}
//	
//	@KafkaListener(topics = KafkaTopicConfig.MEDIA_TOPIC_NAME, groupId = KafkaTopicConfig.K_GROUP_ID)
//	public void consumeCropSurveyFiles(List<MultipartFile> files) {
//		System.out.println("Message received -> " + files.size());
//	}

}
