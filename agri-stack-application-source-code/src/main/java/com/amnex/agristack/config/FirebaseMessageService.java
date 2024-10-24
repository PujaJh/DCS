package com.amnex.agristack.config;

import java.util.HashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.PushNotificationRequest;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author majid.belim
 *
 */
@Service
public class FirebaseMessageService {

	private Logger logger = LoggerFactory.getLogger(FirebaseMessageService.class);

	public void sendToToken(PushNotificationRequest pushNotificationRequest) throws Exception {
		try {

			Message message = getPreconfiguredMessageWithData(new HashMap<String, String>(), pushNotificationRequest);
			String response = FirebaseMessaging.getInstance().sendAsync(message).get();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonOutput = gson.toJson(message);
			logger.info("Sent message to token. Device token: " + pushNotificationRequest.getToken() + ", " + response
					+ " msg " + jsonOutput);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("sendToToken =============================> "+e);
			logger.error("sendToToken =============================> "+e.getMessage());
		}
	}

	private Message getPreconfiguredMessageWithData(Map<String, String> data,
			PushNotificationRequest pushNotificationRequest) {
		return getPreconfiguredMessageBuilder(pushNotificationRequest).putAllData(data)
				.setToken(pushNotificationRequest.getToken()).build();
	}

	private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {

		ApnsConfig apnsConfig = getApnsConfig(null);

		return Message.builder().setApnsConfig(apnsConfig)
				.setNotification((Notification.builder().setImage(request.getThumbnailURL())
						.setTitle(request.getTitle()).setBody(request.getMessage()).build()));

	}

	private ApnsConfig getApnsConfig(String topic) {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
	}

}
