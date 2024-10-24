package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.SeasonMasterInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.SowingSeasonHistory;
import com.amnex.agristack.repository.SeasonMasterHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.SeasonMasterOutputDAO;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.repository.SeasonMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

@Service
public class SeasonMasterService {

	@Autowired
	SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private SeasonMasterHistoryRepository seasonMasterHistoryRepository;

	/**
	 * Retrieves the season detail list based on the provided HttpServletRequest.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseEntity {@link SowingSeason} containing the season detail list.
	 */
	public ResponseEntity<?> getSeasonDetailList(HttpServletRequest request) {
		try {
			List<SowingSeason> seasonList = seasonMasterRepository.findByIsDeletedFalseAndIsActiveTrueOrderBySeasonIdAsc();
			for (SowingSeason season: seasonList) {
				getSeasonYear(season);
				season.setCreatedIp(null);
				season.setModifiedIp(null);
				
			}
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, seasonList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the season list based on the provided HttpServletRequest.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return {@code SeasonMasterOutputDAO}A ResponseEntity containing the season list.
	 */
	public ResponseEntity<?> getSeasonList(HttpServletRequest request) {
		try {
			List<SeasonMasterOutputDAO> seasonMasterOutputList = new ArrayList<>();
			List<SowingSeason> seasonList = seasonMasterRepository.findByIsDeletedFalseAndIsActiveTrueOrderBySeasonNameAsc();
			seasonList.forEach(ele -> {
				seasonMasterOutputList.add(new SeasonMasterOutputDAO(ele.getSeasonId(), ele.getSeasonName(), ele.getSeasonCode()));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, seasonMasterOutputList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves all the season list based on the provided HttpServletRequest.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseEntity {@code SowingSeason} containing all the season list.
	 */
	public ResponseEntity<?> getAllSeasonList(HttpServletRequest request) {
		try {
			List<SowingSeason> seasonList = seasonMasterRepository.findByIsDeletedFalseOrderBySeasonIdAsc();
			for(SowingSeason season: seasonList) {
				season.setCreatedIp(null);
				season.setModifiedIp(null);
			}
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, seasonList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Adds a new season based on the provided SeasonMasterInputDAO and HttpServletRequest.
	 *
	 * @param seasonDAO The {@code SeasonMasterInputDAO} object containing the season details.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return {@link SowingSeason}A ResponseEntity indicating the result of the season addition process.
	 */
	public ResponseEntity<?> addSeason(SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			SowingSeason seasonMaster = new SowingSeason();
			BeanUtils.copyProperties(seasonDAO, seasonMaster);
			seasonMaster.setIsActive(true);
			seasonMaster.setIsDeleted(false);
			seasonMaster.setCreatedOn(new Timestamp(new Date().getTime()));
			seasonMaster.setCreatedIp(CommonUtil.getRequestIp(request));
			seasonMaster.setCreatedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			seasonMaster = seasonMasterRepository.save(seasonMaster);

			addSowingSeasonHistory(request, seasonMaster);

			generalService.updateCurrentSeason(seasonMaster);
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.SEASON_ADDED_SUCCESSFULLY, seasonMaster), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void addSowingSeasonHistory(HttpServletRequest request, SowingSeason seasonMaster) {
		SowingSeasonHistory sowingSeasonHistory = new SowingSeasonHistory();
		BeanUtils.copyProperties(seasonMaster,sowingSeasonHistory);
		sowingSeasonHistory.setCreatedBy(CustomMessages.getUserId(request,jwtTokenUtil));
		sowingSeasonHistory.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
		sowingSeasonHistory.setCreatedOn(new Timestamp(new Date().getTime()));
		sowingSeasonHistory.setModifiedOn(new Timestamp(new Date().getTime()));
		sowingSeasonHistory.setCreatedIp(CommonUtil.getRequestIp(request));
		sowingSeasonHistory.setModifiedIp(CommonUtil.getRequestIp(request));
		seasonMasterHistoryRepository.save(sowingSeasonHistory);
	}

	/**
	 * Deletes a season based on the provided seasonId and HttpServletRequest.
	 *
	 * @param seasonId The ID of the season to be deleted.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseEntity indicating the result of the season deletion process.
	 */
	public ResponseEntity<?> deleteSeason(Long seasonId, HttpServletRequest request) {
		try {
			SowingSeason seasonMaster = seasonMasterRepository.findBySeasonId(seasonId);
			seasonMaster.setIsActive(false);
			seasonMaster.setIsDeleted(true);
			seasonMaster.setModifiedOn(new Timestamp(new Date().getTime()));
			seasonMaster.setModifiedIp(CommonUtil.getRequestIp(request));
			seasonMaster.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			seasonMaster = seasonMasterRepository.save(seasonMaster);

			addSowingSeasonHistory(request, seasonMaster);

			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.DELETE_SUCCESSFULLY, seasonMaster), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the status of a season based on the provided SeasonMasterInputDAO and HttpServletRequest.
	 *
	 * @param seasonDAO The {@code SeasonMasterInputDAO} object containing the updated season details.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return {@link SowingSeason}A ResponseEntity indicating the result of the season status update process.
	 */
	public ResponseEntity<?> updateSeasonStatus(SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			SowingSeason seasonMaster = seasonMasterRepository.findBySeasonId(seasonDAO.getSeasonId());
			seasonMaster.setIsActive(seasonDAO.getIsActive());
//			seasonMaster.setIsDeleted(seasonDAO.getIsDeleted());
			seasonMaster.setModifiedOn(new Timestamp(new Date().getTime()));
			seasonMaster.setModifiedIp(CommonUtil.getRequestIp(request));
			seasonMaster.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			
			seasonMaster = seasonMasterRepository.save(seasonMaster);
			
			addSowingSeasonHistory(request, seasonMaster);
			seasonMaster.setCreatedIp(null);
			seasonMaster.setModifiedIp(null);
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.STATUS_UPDATED_SUCCESSFULLY, seasonMaster),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates a season based on the provided SeasonMasterInputDAO and HttpServletRequest.
	 *
	 * @param seasonDAO The {@code SeasonMasterInputDAO} object containing the updated season details.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return {@code SowingSeason}A ResponseEntity indicating the result of the season update process.
	 */
	public ResponseEntity<?> updateSeason(SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			SowingSeason seasonMaster = seasonMasterRepository.findBySeasonId(seasonDAO.getSeasonId());
			BeanUtils.copyProperties(seasonDAO, seasonMaster, "createdOn", "createdBy","isDeleted", "isActive","isCentralProvided");
			seasonMaster.setModifiedOn(new Timestamp(new Date().getTime()));
			seasonMaster.setModifiedIp(CommonUtil.getRequestIp(request));
			seasonMaster.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			seasonMaster = seasonMasterRepository.save(seasonMaster);

			addSowingSeasonHistory(request, seasonMaster);

			generalService.updateCurrentSeason(seasonMaster);
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.SEASON_UPDATE_SUCCESSFULLY, seasonMaster), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves a season based on the provided seasonId and HttpServletRequest.
	 *
	 * @param seasonId The ID of the season to be retrieved.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return {@code SeasonMasterOutputDAO}A ResponseEntity containing the retrieved season.
	 */
    public ResponseEntity<?> getSeason(Long seasonId, HttpServletRequest request) {
		try {
			SeasonMasterOutputDAO season = new SeasonMasterOutputDAO();
			SowingSeason seasonMaster = seasonMasterRepository.findBySeasonId(seasonId);
			BeanUtils.copyProperties(seasonMaster, season);
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, season), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	/**
	 * Retrieves the season year for the given SowingSeason.
	 *
	 * @param season The {@code SowingSeason} object for which to retrieve the season year.
	 * @return {@link SowingSeason}The SowingSeason object with the updated season year.
	 */
	public SowingSeason getSeasonYear(SowingSeason season) {
		try {
			SowingSeason cropSeason = new SowingSeason();
			Integer toYear = 0;
			Integer fromYear = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
			Integer CurrentYear = cal.get(Calendar.YEAR);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			String startingDate = season.getStartingMonth();
			String endingDate = season.getEndingMonth();

			Calendar calendarStart = Calendar.getInstance();
			Calendar calendarEnd = Calendar.getInstance();

			calendarStart.setTime(sdf.parse(startingDate));
			calendarEnd.setTime(sdf.parse(endingDate));

			Integer SFMonth = calendarStart.get(Calendar.MONTH) + 1;
			Integer STMonth = calendarEnd.get(Calendar.MONTH) + 1;

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
			season.setStartingYear(fromYear);
			season.setEndingYear(toYear);

			return season;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if the duration of a season is valid based on the provided SeasonMasterInputDAO and HttpServletRequest.
	 *
	 * @param seasonInput The {@code SeasonMasterInputDAO} object containing the season details.
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseEntity indicating if the duration of the season is valid or not.
	 */
	public ResponseEntity<?> isDurationValid(SeasonMasterInputDAO seasonInput, HttpServletRequest request) {
		try {
			final Boolean[] validSeason = {true};
			java.sql.Date startDate = generalService.getDateFromString(seasonInput.getStartingMonth(), "yyyy-MM-dd");
			java.sql.Date endDate = generalService.getDateFromString(seasonInput.getEndingMonth(), "yyyy-MM-dd");
			List<SowingSeason> seasonMaster = seasonMasterRepository.checkDurationIsValid(false, startDate, endDate);
			seasonMaster.stream().forEach(season -> {
				if(season.getSeasonId().equals(seasonInput.getSeasonId()) && seasonMaster.size() == 1) {
					validSeason[0] = true;
				} else {
					validSeason[0] = false;
				}

				System.out.println("season: " + season.getSeasonName() + " -> " + season.getStartingMonth()+" - "+season.getEndingMonth());
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, validSeason[0]), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Checks if a season name already exists.
	 *
	 * @param seasonName The name of the season to check.
	 * @return A ResponseModel object indicating if the season name exists or not.
	 */
	public ResponseModel checkSeasonNameExist(String seasonName) {
		List<SowingSeason> seasons = seasonMasterRepository.findByIsDeletedFalseAndSeasonName(seasonName);
		if (seasons.size()>0) {
			return CustomMessages.makeResponseModel(true, CustomMessages.SEASON_NAME_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR,
					CustomMessages.SUCCESS);
		} else {
			return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		}
	}
}
