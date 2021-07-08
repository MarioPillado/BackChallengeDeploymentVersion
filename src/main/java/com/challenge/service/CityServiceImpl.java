package com.challenge.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.challenge.model.CityModel;

@Service
public class CityServiceImpl implements CityService {
	public List<CityModel> CitiesList;

	@PostConstruct
	public void init() throws FileNotFoundException {
		CitiesList = new ArrayList<>();

		File citiesFile = new File("src/main/resources/cities_canada-usa.tsv");
		Scanner fileScanner = new Scanner(citiesFile);

		while (fileScanner.hasNext()) {
			String cityLine = fileScanner.nextLine();
			String[] cityInfo = cityLine.split("\t");

			AddCityToList(cityInfo);
		}
	}

	@Override
	public List<CityModel> GetSuggestionsList(String inputLatitude, String inputLongitude, String name) {
		List<CityModel> suggestionsList = CitiesList.stream().filter(c -> c.getName().startsWith(name))
				.collect(Collectors.toList());

		suggestionsList = EvaluateAndSetScores(suggestionsList, inputLatitude, inputLongitude, name);

		suggestionsList = suggestionsList.stream().sorted(Comparator.comparing(CityModel::getScore).reversed())
				.collect(Collectors.toList());

		return suggestionsList;
	}

	private void AddCityToList(String[] cityInfo) {
		CityModel city = new CityModel();
		city.setName(cityInfo[1] + ", " + cityInfo[10] + ", " + cityInfo[8]);
		city.setLatitude(cityInfo[4]);
		city.setLongitude(cityInfo[5]);
		CitiesList.add(city);
	}

	private List<CityModel> EvaluateAndSetScores(List<CityModel> suggestionsList, String inputLatitude,
			String inputLongitude, String name) {
		// Evaluate score RELATIVE to the NAME (Name Score = 100% of the TOTAL SCORE)
		if (inputLatitude == null && inputLongitude == null) {
			suggestionsList = EvaluateNameScore(suggestionsList, name, 1);
		}
		// Evaluate score RELATIVE to the NAME and LONGITUDE(Name Score = 80%, and
		// Longitude Score = 20% of the TOTAL SCORE)
		else if (inputLatitude == null && inputLongitude != null) {
			suggestionsList = EvaluateNameScore(suggestionsList, name, 0.8f);
			suggestionsList = EvaluateLongitudeScore(suggestionsList, inputLongitude, 0.2f);
		}
		// Evaluate score RELATIVE to the NAME and LATITUDE(Name Score = 80%, and
		// Latitude Score = 20% of the TOTAL SCORE)
		else if (inputLatitude != null && inputLongitude == null) {
			suggestionsList = EvaluateNameScore(suggestionsList, name, 0.8f);
			suggestionsList = EvaluateLatitudeScore(suggestionsList, inputLatitude, 0.2f);
		}
		// Evaluate score RELATIVE to ALL PARAMETERS (Name Score = 60%, Longitude Score
		// = 20% Latitude Score = 20% of the TOTAL SCORE)
		else if (inputLatitude != null && inputLongitude != null) {
			suggestionsList = EvaluateNameScore(suggestionsList, name, 0.6f);
			suggestionsList = EvaluateLongitudeScore(suggestionsList, inputLongitude, 0.2f);
			suggestionsList = EvaluateLatitudeScore(suggestionsList, inputLatitude, 0.2f);
		}

		return suggestionsList;
	}

	private List<CityModel> EvaluateNameScore(List<CityModel> list, String inputName, float multiplier) {
		list.stream().forEach(c -> c.setScore(GetNameScoreByRemainingCharacters(c.getName(), inputName) * multiplier));
		return list;
	}

	private float GetNameScoreByRemainingCharacters(String name, String input) {
		String[] nameSplit = name.split(", ");
		long charDifference = nameSplit[0].chars().count() - input.chars().count();

		switch ((int) charDifference) {
		case 0:
			return 1;
		case 1:
			return 0.9f;
		case 2:
			return 0.8f;
		case 3:
		case 4:
			return 0.6f;
		case 5:
		case 6:
			return 0.4f;

		default:
			return 0.2f;
		}
	}

	private List<CityModel> EvaluateLongitudeScore(List<CityModel> list, String inputDegree, float multiplier) {
		list.stream().forEach(
				c -> c.setScore(c.getScore() + ((GetDegreeScoreByMembershipFunction(Float.parseFloat(c.getLongitude()),
						Float.parseFloat(inputDegree))) * multiplier)));
		return list;
	}

	private List<CityModel> EvaluateLatitudeScore(List<CityModel> list, String inputDegree, float multiplier) {
		list.stream().forEach(
				c -> c.setScore(c.getScore() + ((GetDegreeScoreByMembershipFunction(Float.parseFloat(c.getLatitude()),
						Float.parseFloat(inputDegree))) * multiplier)));
		return list;
	}

	private float GetDegreeScoreByMembershipFunction(float cityData, float inputData) {
		float Ax = inputData - 5f;
		float Ay = 0;// "Ay" is always 0
		float Bx = inputData;
		float By = 1;// "By" is always 1
		float Cx = inputData + 5f;
		float Cy = 0; // "Cy" is always 0
		float Zx = cityData;

		if (cityData == Bx) {
			return 1;
		} else if (cityData > Ax && cityData < Bx) {
			return MembershipFunction(Ax, Ay, Bx, By, Zx);

		} else if (cityData > Bx && cityData < Cx) {
			return MembershipFunction(Bx, By, Cx, Cy, Zx);
		}
		return 0;
	}

	private float MembershipFunction(float Ax, float Ay, float Bx, float By, float Zx) {
		float M = (By - Ay) / (Bx - Ax);
		return M * (Zx - Ax) + Ay;
	}
}