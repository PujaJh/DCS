package com.amnex.agristack.scheduler;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.DepartmentEnum;
import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.service.GeneralService;
import com.amnex.agristack.service.MessageConfigurationService;
import com.amnex.agristack.service.SurveyorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SeasonChangeScheduler {

	@Value("${jwt.token.validity}")
	public long JWT_TOKEN_VALIDITY;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private GeneralService generalService;


	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private RoleMasterRepository roleMasterRepository;

	@Autowired
	MessageConfigurationService messageConfigurationService;

	@Autowired
	YearRepository yearRepository;

	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;
	
	@Autowired
	private SurveyVerificationConfigurationMasterRepository surveyVerificationConfigurationMasterRepository;

	@Autowired
	private  ConfigurationRepository configurationRepository;


	@Autowired
	private SurveyorAvailabilityService surveyorAvailabilityService;

	@Scheduled(cron = "0 21 20 * * *", zone = "IST") // execute at every day 12:30 AM
	public void seasonChangeScheduler() {
		try {
			Boolean sendNotification = false;
			System.out.println("<=========== Season Change scheduler start at : "+Calendar.getInstance().getTime()+" =================>");
			SowingSeason currentSeason = generalService.getCurrentSeason();
			SowingSeason yesterdaySeason = getYesterdaySeason();
			YearMaster yearMaster = generalService.getCurrentYear();
			YearMaster lastYear = yearRepository.findByIsCurrentYearIsTrue();
			// check if current year and last year is same or not
			if(!lastYear.getId().equals(yearMaster.getId())) {
				// set current year as is_current_Year and make last Year as null
				yearMaster.setIsCurrentYear(true);
				yearRepository.save(yearMaster);

				// Set year to null
				lastYear.setIsCurrentYear(null);
				yearRepository.save(lastYear);

			}
			if(!currentSeason.getSeasonId().equals(yesterdaySeason.getSeasonId())) {
				System.out.println("<=========== current season is :"+ currentSeason.getSeasonName()+" =================>");
				System.out.println("<=========== yesterday season was :"+ yesterdaySeason.getSeasonName()+" =================>");
				System.out.println("<=========== Season has changed current season is :"+ currentSeason.getSeasonName()+" =================>");

				generalService.updateCurrentSeason(currentSeason);


				// add availability for government Surveyor and verifier
                surveyorAvailabilityService.addCurrentSeasonAvailabilityForGovEmp(currentSeason);

                Optional<RoleMaster> role = roleMasterRepository.findByCode(RoleEnum.SURVEYOR.toString());
				List<UserMaster> users = userMasterRepository.findAllActiveUserByRole(role.get(), true);

				DepartmentMaster department =  departmentRepository.findByDepartmentType(DepartmentEnum.PRIVATE_RESIDENT.getValue()).orElse(null);
				List<UserMaster> prs = users.stream().filter(x-> x.getDepartmentId().equals(department)).collect(Collectors.toList());
				ConfigurationMaster configurationMaster = configurationRepository.findByConfigKey(ConfigCode.SEND_SEASON_CHANGE_NOTIFICATION.name()).orElse(null);
				sendNotification = !Objects.isNull(configurationMaster) ? Boolean.valueOf(configurationMaster.getConfigValue()) : false;
				if(prs.size()> 0 && sendNotification) {
					messageConfigurationService.sendEmailToSurveyors(prs,currentSeason.getSeasonName(), yearMaster.getYearValue());
					messageConfigurationService.sendMessageToSurveyor(prs,currentSeason.getSeasonName(), yearMaster.getYearValue());
					/* push notification to do */
				}

			} else {
				System.out.println("<=========== Season has not changed. =================>");
				System.out.println("<=========== current season is :"+ currentSeason.getSeasonName()+" =================>");
				System.out.println("<=========== yesterday season was :"+ yesterdaySeason.getSeasonName()+" =================>");
			}
			System.out.println("<=========== Season Change scheduler end  at :"+Calendar.getInstance().getTime()+" =================>");


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SowingSeason getYesterdaySeason(){
		try {
			SowingSeason cropSeason = new SowingSeason();
			List<SowingSeason> seasons = seasonMasterRepository.findAllByIsDeletedFalse();
			Integer toYear = 0;
			Integer fromYear = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
			Integer date = cal.get(Calendar.DAY_OF_MONTH) - 1;
			Integer CurrentYear = cal.get(Calendar.YEAR);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			for (SowingSeason season : seasons) {
				String startingDate = season.getStartingMonth();
				String endingDate = season.getEndingMonth();

				Calendar calendarStart = Calendar.getInstance();
				Calendar calendarEnd = Calendar.getInstance();

				calendarStart.setTime(sdf.parse(startingDate));
				calendarEnd.setTime(sdf.parse(endingDate));

				Integer SFMonth = calendarStart.get(Calendar.MONTH) + 1;
				Integer STMonth = calendarEnd.get(Calendar.MONTH) + 1;

				Integer SFDate = calendarStart.get(Calendar.DAY_OF_MONTH);
				Integer STDate = calendarEnd.get(Calendar.DAY_OF_MONTH);

				if (SFMonth > STMonth) {
					if (SFMonth == CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (STMonth == CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					} else if (SFMonth < CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (STMonth > CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					} else if (SFMonth > CurrentMonth && STMonth < CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (SFMonth > CurrentMonth && STMonth > CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					}
				} else if (SFMonth <= STMonth) {
					fromYear = CurrentYear;
					toYear = CurrentYear;
				}

				LocalDate currentDate = LocalDate.of(CurrentYear, CurrentMonth, date);

				LocalDate fromDate = LocalDate.of(fromYear, SFMonth, SFDate);

				LocalDate toDate = LocalDate.of(toYear, STMonth, STDate);
				if ((currentDate.isAfter(fromDate) || currentDate.isEqual(fromDate))
						&& (currentDate.isBefore(toDate) || currentDate.isEqual(toDate))) {
					cropSeason = season;

					if (CurrentMonth < (Calendar.JULY + 1)) {
						cropSeason.setStartingYear(fromYear - 1);
						cropSeason.setEndingYear(toYear);
					} else if (CurrentMonth >= (Calendar.JULY + 1)) {
						cropSeason.setStartingYear(fromYear);
						cropSeason.setEndingYear(toYear + 1);
					}

					break;
				}
			}
			return cropSeason;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Scheduled(cron="0 1 1 * * *")
//	Based on season active new and deactive old configuration
	public void changeVerifierConfigStatusForAllState() {
		List<StateLgdMaster> stateList=stateLgdMasterRepository.findAll();
		SowingSeason currentSeason = generalService.getCurrentSeason();
		if(stateList!=null && stateList.size()>0 && currentSeason!=null) {
			List<Long> codes=stateList.stream().map(x->x.getStateLgdCode()).collect(Collectors.toList());
			surveyVerificationConfigurationMasterRepository.deActivateSurveyVerificationConfiguration(codes, currentSeason.getSeasonId());
			surveyVerificationConfigurationMasterRepository.activateSurveyVerificationConfiguration(codes, currentSeason.getSeasonId());
		}
	}
}
