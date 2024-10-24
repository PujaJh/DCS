package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.SeasonMasterInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.service.SeasonMasterService;

/**
 * Controller class for managing season master data.
 */
@RestController
@RequestMapping("/admin/season")
public class SeasonMasterController {

	@Autowired
	SeasonMasterService seasonMasterService;

	/**
	 * Retrieves the list of season details.
	 *
	 * @param request the HTTP servlet request
	 * @return the response entity containing the season detail list
	 */
	@GetMapping(value = "/getSeasonDetailList")
	public ResponseEntity<?> getSeasonDetailList(HttpServletRequest request) {
		try {
			return seasonMasterService.getSeasonDetailList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of all seasons.
	 *
	 * @param request the HTTP servlet request
	 * @return the response entity containing the season list
	 */
	@GetMapping(value = "/getSeasonList")
	public ResponseEntity<?> getAllSeasonList(HttpServletRequest request) {
		try {
			return seasonMasterService.getAllSeasonList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of seasons for public mobile access.
	 *
	 * @param request the HTTP servlet request
	 * @return the response entity containing the season list
	 */
	@GetMapping(value = "/mobile/public/getSeasonList")
	public ResponseEntity<?> getSeasonMasterList(HttpServletRequest request) {
		try {
			return seasonMasterService.getSeasonList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a new season.
	 *
	 * @param seasonDAO the season input data
	 * @param request   the HTTP servlet request
	 * @return the response entity containing the result of the addition
	 */
	@PostMapping(value = "/addSeason")
	public ResponseEntity<?> addSeason(@RequestBody SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			return seasonMasterService.addSeason(seasonDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates an existing season.
	 *
	 * @param seasonDAO the updated season data
	 * @param request   the HTTP servlet request
	 * @return the response entity containing the result of the update
	 */
	@PostMapping(value = "/updateSeason")
	public ResponseEntity<?> updateSeason(@RequestBody SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			return seasonMasterService.updateSeason(seasonDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of a season.
	 *
	 * @param seasonDAO the season data containing the updated status
	 * @param request   the HTTP servlet request
	 * @return the response entity containing the result of the status update
	 */
	@PostMapping(value = "/updateSeasonStatus")
	public ResponseEntity<?> updateSeasonStatus(@RequestBody SeasonMasterInputDAO seasonDAO, HttpServletRequest request) {
		try {
			return seasonMasterService.updateSeasonStatus(seasonDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes a season by its ID.
	 *
	 * @param seasonId the ID of the season to delete
	 * @param request  the HTTP servlet request
	 * @return the response entity containing the result of the deletion
	 */
	@DeleteMapping(value = "/deleteSeason/{seasonId}")
	public ResponseEntity<?> deleteSeason(@PathVariable("seasonId") Long seasonId, HttpServletRequest request) {
		try {
			return seasonMasterService.deleteSeason(seasonId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves a season by its ID.
	 *
	 * @param seasonId the ID of the season to retrieve
	 * @param request  the HTTP servlet request
	 * @return the response entity containing the season
	 */
	@GetMapping(value = "/getSeason/{seasonId}")
	public ResponseEntity<?> getSeasonById(@PathVariable("seasonId") Long seasonId, HttpServletRequest request) {
		try {
			return seasonMasterService.getSeason(seasonId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if the season duration is valid.
	 *
	 * @param seasonMasterInput the season input data
	 * @param request           the HTTP servlet request
	 * @return the response entity containing the result of the duration validation
	 */
	@PostMapping(value = "/isDurationValid")
	public ResponseEntity<?> isDurationValid(@RequestBody SeasonMasterInputDAO seasonMasterInput, HttpServletRequest request) {
		return seasonMasterService.isDurationValid(seasonMasterInput, request);
	}

	/**
	 * Checks if a season name already exists.
	 *
	 * @param request           the HTTP servlet request
	 * @param seasonMasterInput the season input data containing the season name
	 * @return the response model containing the result of the name existence check
	 */
	@PostMapping(value = "/checkSeasonNameExist")
	public ResponseModel checkSeasonNameExist(HttpServletRequest request, @RequestBody SeasonMasterInputDAO seasonMasterInput) {
		return seasonMasterService.checkSeasonNameExist(seasonMasterInput.getSeasonName());
	}
	 
}
