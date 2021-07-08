package com.challenge.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.model.CityModel;
import com.challenge.service.CityService;

@RestController
@RequestMapping
public class CityController {

	@Autowired
	CityService cityService;

	@GetMapping(value = "/suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CityModel>> GetCitySuggestionsRequest(@RequestParam("q") String name,
			@RequestParam(value = "latitude", required = false) String inputLatitude,
			@RequestParam(value = "longitude", required = false) String inputLongitude) {
		List<CityModel> suggestionsList = cityService.GetSuggestionsList(inputLatitude, inputLongitude, name);
		if (suggestionsList != null) {
			return ResponseEntity.status(HttpStatus.OK).body(suggestionsList);
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
		}
	}
}
