package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.Enum.ActivityCodeEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.FarmerGrievanceDTO;
import com.amnex.agristack.dao.FarmerGrievanceWithMediaRequestDTO;
import com.amnex.agristack.dao.ReviewSurveyInputDAO;
import com.amnex.agristack.dao.ReviewSurveyOutputDAO;
import com.amnex.agristack.dao.SqlData;
import com.amnex.agristack.dao.SurveyVerificationConfigurationMasterDAO;
import com.amnex.agristack.entity.FarmerGrievance;
import com.amnex.agristack.entity.FarmerGrievanceMediaMapping;
import com.amnex.agristack.entity.LandParcelSurveyMaster;
import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.repository.FarmerGrievanceMediaMappingRepository;
import com.amnex.agristack.repository.FarmerGrievanceRepository;
import com.amnex.agristack.repository.LandParcelSurveyMasterRespository;
import com.amnex.agristack.repository.SurveyVerificationConfigurationMasterRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class FarmerGrievanceService {
    @Autowired
    FarmerGrievanceRepository farmerGrievanceRepository;

    @Autowired
    FarmerGrievanceMediaMappingRepository farmerGrievanceMediaMappingRepository;

    @Autowired
    MediaMasterService mediaMasterService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SurveyReviewService surveyReviewService;

    @Autowired
    LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

    @Value("${media.folder.image}")
    private String folderImage;

    @Autowired
    SurveyVerificationConfigurationMasterRepository surveyVerificationConfigurationMasterRepository;
    @Autowired
    DBUtils dbUtils;

    public FarmerGrievance addFarmerGrievance(FarmerGrievanceWithMediaRequestDTO payload, HttpServletRequest request)
            throws Exception {
        String userId = CustomMessages.getUserId(request, jwtTokenUtil);
        userId = (userId != null && !userId.isEmpty()) ? userId : "0";
        ObjectMapper mapper = new ObjectMapper();
        FarmerGrievance farmerGrievance = mapper.readValue(payload.getData(), FarmerGrievance.class);
        farmerGrievance.setCreatedBy(userId);
        farmerGrievance.setUserId(Long.parseLong(userId));
        String refId = farmerGrievance.getLpsmId() + getAlphaNumericString(5);
        farmerGrievance.setRefId(refId);
        farmerGrievance.setStatus(StatusEnum.PENDING.getValue().longValue());
        farmerGrievance = farmerGrievanceRepository.save(farmerGrievance);
        List<FarmerGrievanceMediaMapping> grivanceList = new ArrayList<>();

        if (payload.getMedia() != null) {
            for (MultipartFile media : payload.getMedia()) {
                MediaMaster mediaMaster = mediaMasterService.storeFile(media, "agristack", folderImage,
                        ActivityCodeEnum.FARMER_GRIEVANCE.getValue());
                FarmerGrievanceMediaMapping grievanceMedia = new FarmerGrievanceMediaMapping();
                grievanceMedia.setFarmerGrievanceId(farmerGrievance.getId());
                grievanceMedia.setMediaId(mediaMaster.getMediaId());
                farmerGrievanceMediaMappingRepository.save(grievanceMedia);
                grievanceMedia.setMediaMaster(mediaMaster);
                grivanceList.add(grievanceMedia);
            }
        }

        farmerGrievance.setFarmerGrievanceMedias(grivanceList);
        return farmerGrievance;
    }

    String getAlphaNumericString(int n) {
        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public FarmerGrievance updateFarmerGrievanceStatus(FarmerGrievance payload, HttpServletRequest request) {
        Optional<FarmerGrievance> farmerGrievanceOptional = farmerGrievanceRepository.findById(payload.getId());
        FarmerGrievance farmerGrievance = null;
        if (farmerGrievanceOptional.isPresent()) {
            String userId = CustomMessages.getUserId(request, jwtTokenUtil);
            userId = (userId != null && !userId.isEmpty()) ? userId : "0";
            farmerGrievance = farmerGrievanceOptional.get();
            if (payload.getIsApproved()) {
                farmerGrievance.setStatus(StatusEnum.APPROVED.getValue().longValue());
                try {
                    Optional<LandParcelSurveyMaster> landParcelSurveyOptionalMaster = landParcelSurveyMasterRespository
                            .findById(farmerGrievance.getLpsmId());
                    if (landParcelSurveyOptionalMaster.isPresent()) {
                        LandParcelSurveyMaster landParcelSurveyMaster = landParcelSurveyOptionalMaster.get();
                        Long stateCode = 0L;
                        try {
                            stateCode = landParcelSurveyMaster.getParcelId().getVillageLgdMaster()
                                    .getStateLgdCode().getStateLgdCode();
                            if (stateCode != null && stateCode != 0) {
                                SurveyVerificationConfigurationMasterDAO surveyVerificationConfigurationMasterDAO = surveyVerificationConfigurationMasterRepository
                                        .findByStateLgdCode(stateCode);
                                if (surveyVerificationConfigurationMasterDAO
                                        .getObjectionRaisedByFarmerAndMarkedBySupervisor() != null
                                        && surveyVerificationConfigurationMasterDAO
                                                .getObjectionRaisedByFarmerAndMarkedBySupervisor()) {
                                    surveyReviewService.autoAssignVerifierForTheParcel(landParcelSurveyMaster,
                                            VerifierReasonOfAssignmentEnum.FARMER_OBJECTION);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            } else {
                farmerGrievance.setStatus(StatusEnum.REJECTED.getValue().longValue());
            }
            farmerGrievance.setRejectionRemark(payload.getRejectionRemark());
            farmerGrievance.setModifiedBy(userId);
            farmerGrievance.setResolvedBy(Long.valueOf(userId));
            farmerGrievance = farmerGrievanceRepository.save(farmerGrievance);
            farmerGrievance.setStatusName(StatusEnum.get(farmerGrievance.getStatus().intValue()).toString());
        }
        return farmerGrievance;
    }

    public Object getAllGrievancesByUserId(ReviewSurveyInputDAO inputDao, HttpServletRequest request) throws Exception {
        String userId = CustomMessages.getUserId(request, jwtTokenUtil);
        userId = (userId != null && !userId.isEmpty()) ? userId : "0";
        // List<Object[]> farmerGrievance = farmerGrievanceRepository
        // .findAllByUserSeasonAndYear(Long.parseLong(userId), seasonId, startYear,
        // endYear);

        String fnName = "agri_stack.fn_get_all_farmer_grievance";
        SqlData[] params = { new SqlData(1, userId, "int"), new SqlData(2, inputDao.getSeasonId(), "int"),
                new SqlData(3, inputDao.getStartYear(), "int"), new SqlData(4, inputDao.getEndYear(), "int") };
        String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeFactory typeFactory = mapper.getTypeFactory();
        List<FarmerGrievanceDTO> resultObject = mapper.readValue(response,
                typeFactory.constructCollectionType(List.class, FarmerGrievanceDTO.class));

        return resultObject;
    }

    public ResponseModel getSurveySummaryForFarmer(ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        try {

            String userId = CustomMessages.getUserId(request, jwtTokenUtil);
            userId = (userId != null && !userId.isEmpty()) ? userId : "0";

            String fnName = "agri_stack.fn_get_farmer_land_survey_summary_details";
            SqlData[] params = { new SqlData(1, inputDao.getUserId(), "int"),
                    new SqlData(2, inputDao.getSeasonId(), "int"), new SqlData(3, inputDao.getStartYear(), "int"),
                    new SqlData(4, inputDao.getEndYear(), "int") };
            String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeFactory typeFactory = mapper.getTypeFactory();
            List<ReviewSurveyOutputDAO> resultObject = mapper.readValue(response,
                    typeFactory.constructCollectionType(List.class, ReviewSurveyOutputDAO.class));

            // e.printStackTrace();
            return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
                    CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

        } catch (Exception e) {

            // e.printStackTrace();
            return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
        }
    }

    public ResponseModel getSurveySummaryForSupervisor(ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        try {

            String userId = CustomMessages.getUserId(request, jwtTokenUtil);
            userId = (userId != null && !userId.isEmpty()) ? userId : "0";

            String subDistrictLgdCode = getJoinedValuefromList(inputDao.getSubDistrictLgdCodes());
            String villageLgdCode = getJoinedValuefromList(inputDao.getVillageLgdCodes());
            String fnName = "agri_stack.fn_get_farmer_grievance_supervisor";
            SqlData[] params = {
                    new SqlData(1, userId, "int"),
                    new SqlData(2, inputDao.getSeasonId(), "int"),
                    new SqlData(3, inputDao.getStartYear(), "int"),
                    new SqlData(4, inputDao.getEndYear(), "int"),
                    new SqlData(5, subDistrictLgdCode, "string"),
                    new SqlData(6, villageLgdCode, "string") };
            String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeFactory typeFactory = mapper.getTypeFactory();
            List<FarmerGrievanceDTO> resultObject = mapper.readValue(response,
                    typeFactory.constructCollectionType(List.class, FarmerGrievanceDTO.class));

            return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
                    CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

        } catch (Exception e) {

            // e.printStackTrace();
            return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
        }
    }

    private String getJoinedValuefromList(List<Long> list) {
        try {
            return list.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "";
        }
    }
}