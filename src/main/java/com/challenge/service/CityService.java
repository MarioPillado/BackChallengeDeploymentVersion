package com.challenge.service;

import java.util.List;

import com.challenge.model.CityModel;

public interface CityService {

	public List<CityModel> GetSuggestionsList(String inputLatitude, String inputLongitude, String name);
}
