package com.amnex.agristack.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FCMInitializer {
	@Value("${app.firebase-configuration-file}")
	private String firebaseConfigPath;
	

	@Value("${app.firebase.enabled}")
	private Boolean firebaseEnabled;

	Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

	@PostConstruct
	public void initialize() throws IOException {

		if(firebaseEnabled.equals(Boolean.TRUE)) {
			try {
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(
								GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream()))
						.build();
				if (FirebaseApp.getApps().isEmpty()) {
					FirebaseApp.initializeApp(options);
				}
			} catch (Exception e) {
				logger.info("Message=========> " + e.getMessage());
				logger.info("e===============> " + e);
			} 
		}
	}
}
