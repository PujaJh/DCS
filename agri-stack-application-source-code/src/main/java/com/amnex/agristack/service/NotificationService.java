/**
 * 
 */
package com.amnex.agristack.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.ActivityStatusEnum;
import com.amnex.agristack.Enum.ActivityTypeEnum;
import com.amnex.agristack.Enum.MasterTableName;
import com.amnex.agristack.Enum.MessageType;
import com.amnex.agristack.Enum.NotificationSourceType;
import com.amnex.agristack.Enum.NotificationType;
import com.amnex.agristack.config.FirebaseMessageService;
import com.amnex.agristack.dao.MobileNotificationDAO;
import com.amnex.agristack.dao.NotificationMasterDao;
import com.amnex.agristack.dao.PushNotificationRequest;
import com.amnex.agristack.dao.ReceiverDAO;
import com.amnex.agristack.entity.ActivityStatus;
import com.amnex.agristack.entity.ActivityType;
import com.amnex.agristack.entity.MessageConfigurationMaster;
import com.amnex.agristack.entity.NotificationMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

import reactor.core.publisher.Mono;

/**
 * @author majid.belim
 *
 */
@Service
public class NotificationService {

	@Autowired
	private FirebaseMessageService firebaseMessageService;

	@Autowired
	private NotificationMasterRepository notificationMasterRepository;
    @Autowired
    private MessageConfigurationRepository messageConfigurationRepository;
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    private ActivityStatusRepository activityStatusRepository;
    @Autowired
    private UserLandAssignmentRepository userLandAssignmentRepository;
    @Autowired
    UserMasterRepository userMasterRepository;
    
    @Autowired
    MobileNotificationRepository mobileNotificationRepository;

	/**
	 * Handles the token for push notifications.
	 *
	 * @param request The HttpServletRequest object.
	 * @param pushNotificationRequest The PushNotificationRequest object containing the push notification details.
	 * @throws Exception if an error occurs while handling the token.
	 */
	public void token(HttpServletRequest request,PushNotificationRequest pushNotificationRequest) throws Exception {
		List<String> receiverIds= userLandAssignmentRepository.getUserListByFilter(2022, 2023,3l, 20568l);
    	if(receiverIds!=null && receiverIds.size()>0) {
          sendNotification(request, MessageType.SURVEY_COMPLETED, NotificationType.WEB, ActivityTypeEnum.CROP_SURVEY, ActivityStatusEnum.SURVEY_COMPLETED, 1400l, receiverIds, 1400l, MasterTableName.LAND_PARCEL_SURVEY_MASTER);    		
    	}
//		firebaseMessageService.sendToToken(pushNotificationRequest);
	}
	
	   void sendNotification(HttpServletRequest request,MessageType messageType,NotificationType notificationType,ActivityTypeEnum activityTypeEnum,ActivityStatusEnum activityStatusEnum,Long senderId,List<String> receiverIds,Long masterTableId,MasterTableName masterTableName) {
	    	Optional<MessageConfigurationMaster> messageop=messageConfigurationRepository.findByMessageType(messageType);
	    	NotificationMasterDao notificationMasterDao=new NotificationMasterDao();
	    	if(messageop.isPresent()) {
	    		MessageConfigurationMaster messageConfigurationMaster=messageop.get();
	    		notificationMasterDao.setNotificationTitle(messageConfigurationMaster.getEmailSubject());
	    		notificationMasterDao.setMessage(messageConfigurationMaster.getTemplate());
	    	}
	    	notificationMasterDao.setNotificationType(notificationType);
	    	
	    	
	    	
	    	Optional<ActivityType> activityTypeOP=activityTypeRepository.findByActivityTypeName(activityTypeEnum.CROP_SURVEY.getValue());
	    	if(activityTypeOP.isPresent()) {
	    		notificationMasterDao.setActivityTypeId(activityTypeOP.get().getActivityTypeId());	
	    	}
	    	Optional<ActivityStatus> activityStatusOP=activityStatusRepository.findByActivityStatusName(activityStatusEnum.SURVEY_COMPLETED.getValue());
	    	if(activityStatusOP.isPresent()) {
	    		notificationMasterDao.setActivityStatusId(activityStatusOP.get().getActivityStatusId());
	    	}
	    	 List<ReceiverDAO> receiverList=new ArrayList<ReceiverDAO>();
	    	 if(receiverIds!=null ) {
	    		 List<Long> ids=receiverIds.stream().map(m->Long.parseLong(m)).collect(Collectors.toList());
	    		 List<UserMaster> userList=userMasterRepository.findByUserIdInAndIsDeletedAndIsActive(ids,false,true);
	    		 userList.forEach(action->{
	        		 ReceiverDAO receiverDAO=new ReceiverDAO();
	        		 receiverDAO.setDeviceToken(action.getDeviceToken());
	        		 receiverDAO.setReceiverId(action.getUserId());
	        		 receiverList.add(receiverDAO);
	        		 notificationMasterDao.setReceiverList(receiverList);
	    		 });

	    	 }
	    	notificationMasterDao.setSenderId(senderId);
	    	notificationMasterDao.setMasterTableId(masterTableId);
	    	notificationMasterDao.setMasterTableName(masterTableName);
	    	
	    	sendNotification(request, notificationMasterDao);
	    }

		/**
		 * Sends a notification.
		 *
		 * @param request The HttpServletRequest object.
		 * @param notificationMasterDao The NotificationMasterDao object containing the notification details.
		 * @return The ResponseModel object indicating the success of sending the notification.
		 */
	public ResponseModel sendNotification(HttpServletRequest request, NotificationMasterDao notificationMasterDao) {
		ResponseModel responseModel = null;
		try {
			List<NotificationMaster> finalMasterList = new ArrayList<>();

			notificationMasterDao.getReceiverList().forEach(action -> {
				NotificationMaster notificationMaster = new NotificationMaster();
				notificationMaster.setNotificationTitle(notificationMasterDao.getNotificationTitle());
				notificationMaster.setMessage(notificationMasterDao.getMessage());
				notificationMaster.setNotificationType(notificationMasterDao.getNotificationType());
				notificationMaster.setActivityTypeId(notificationMasterDao.getActivityTypeId());
				notificationMaster.setActivityStatusId(notificationMasterDao.getActivityStatusId());
				notificationMaster.setIsRead(false);
				notificationMaster.setIsSend(false);
				notificationMaster.setSenderId(notificationMasterDao.getSenderId());
				notificationMaster.setReceiverId(action.getReceiverId());
				notificationMaster.setCcList(notificationMasterDao.getCcList());
				notificationMaster.setMasterTableId(notificationMasterDao.getMasterTableId());
				notificationMaster.setMasterTableName(notificationMasterDao.getMasterTableName());

				notificationMaster.setCreatedIp(CommonUtil.getRequestIp(request));
				notificationMaster.setIsActive(Boolean.TRUE);
				notificationMaster.setIsDeleted(Boolean.FALSE);
				finalMasterList.add(notificationMaster);
			});

			if (finalMasterList != null && finalMasterList.size() > 0) {
				notificationMasterRepository.saveAll(finalMasterList);
			}

//			new Thread() {
//
//				public void run() {
//					try {

			if (notificationMasterDao.getNotificationType().equals(NotificationType.WEB)
					|| notificationMasterDao.getNotificationType().equals(NotificationType.MOBILE)) {
				if (notificationMasterDao.getReceiverList() != null
						&& notificationMasterDao.getReceiverList().size() > 0) {
					notificationMasterDao.getReceiverList().forEach(action -> {
						if (action.getDeviceToken() != null) {
							PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
							pushNotificationRequest.setTitle(notificationMasterDao.getNotificationTitle());
							pushNotificationRequest.setMessage(notificationMasterDao.getMessage());
							pushNotificationRequest.setThumbnailURL(notificationMasterDao.getThumbnailURL());
							pushNotificationRequest.setToken(action.getDeviceToken());
							try {
								firebaseMessageService.sendToToken(pushNotificationRequest);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}

//					} catch (Exception e) {
//
//					}
//				}
//			};

			return CustomMessages.makeResponseModel(finalMasterList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

			public ResponseModel getMobileNotificationDetails(HttpServletRequest request) {
				try {
					
				List<MobileNotificationDAO>	notifictionCardList = mobileNotificationRepository.findfindByIsActiveTrueAndIsDeletedFalseOrderByCreatedOnDesc(NotificationSourceType.MOBILE);
				
				return new ResponseModel(notifictionCardList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
				}catch(Exception e) {
					return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
							CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
				}
			}
		
		
}
