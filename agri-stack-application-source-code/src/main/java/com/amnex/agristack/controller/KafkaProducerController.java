package com.amnex.agristack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.service.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {
	
	@Autowired
	private KafkaProducer kafkaProducer;


//	@GetMapping("/publish")
//	public ResponseEntity<String> publish(@RequestParam("message") String message) {
//		kafkaProducer.sendMessage(message);
//		return ResponseEntity.ok("Message sent to kafka topic");
//	}

}
