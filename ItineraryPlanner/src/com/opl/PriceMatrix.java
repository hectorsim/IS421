package com.opl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.ItineraryPlanner.Constants;
import com.opencsv.CSVReader;

public class PriceMatrix {

	private String valueDelimiter = " ";
	private final int totalDestinations = 21;

	private int[][] priceMatrix = null;
	private ArrayList<String> countryOrder;
	private ArrayList<String> airportDestinations;
	private int numberOfDestinations;
	private int numberOfDays;

	public PriceMatrix(ArrayList<String> airportDestinations, int numberOfDays) {
		this.numberOfDays = numberOfDays;
		this.airportDestinations = airportDestinations;
		this.numberOfDestinations = airportDestinations.size()+1;
	}

	@SuppressWarnings("serial")
	private HashMap<String, Integer> countryCost = new HashMap<String, Integer>() {
		{
			put("SIN", 93);
			put("PEK", 48);
			put("PVG", 48);
			put("HKG", 75);
			put("KIX", 80);
			put("HND", 80);
			put("ICN", 82);
			put("KHH", 57);
			put("TPE", 57);
			put("PNH", 57);
			put("REP", 57);
			put("DPS", 39);
			put("CGK", 39);
			put("KUL", 45);
			put("PEN", 45);
			put("RGN", 53);
			put("MNL", 40);
			put("BKK", 46);
			put("HKT", 46);
			put("HAN", 41);
			put("SGN", 41);
		}
	};

	@SuppressWarnings("serial")
	private HashMap<String, Integer> countryIndex = new HashMap<String, Integer>() {
		{
			put("SIN", 1);
			put("PEK", 2);
			put("PVG", 3);
			put("HKG", 4);
			put("KIX", 5);
			put("HND", 6);
			put("ICN", 7);
			put("KHH", 8);
			put("TPE", 9);
			put("PNH", 10);
			put("REP", 11);
			put("DPS", 12);
			put("CGK", 13);
			put("KUL", 14);
			put("PEN", 15);
			put("RGN", 16);
			put("MNL", 17);
			put("BKK", 18);
			put("HKT", 19);
			put("HAN", 20);
			put("SGN", 21);
		}
	};

	@SuppressWarnings("unchecked")
	public String generateDAT() throws Exception {
		countryOrder = new ArrayList<String>();
		priceMatrix = new int[numberOfDestinations][numberOfDestinations];
		CSVReader reader = null;
		try {
			InputStream is = this.getClass().getResourceAsStream(
					Constants.PRICEMATRIXCSV);
			InputStreamReader isr = new InputStreamReader(is);
			reader = new CSVReader(isr);
			String[] record = null;
			int row = 0;
			String currentCountry = null;
			while ((record = reader.readNext()) != null) {
				// determining the country
				currentCountry = record[0];
				if (airportDestinations.contains(currentCountry)) {
					row = airportDestinations.indexOf(currentCountry);
					int col = 0;
					for (String codes : airportDestinations) {
						int index = countryIndex.get(codes);
						String val = record[index].toString();
						int price = 0;
						if (val.equals("-"))
							price = Constants.dashValue;
						else
							price = Integer.parseInt(val);
						if (col == numberOfDestinations)
							break;
						priceMatrix[row][col] = price;
						col++;
					}
					priceMatrix[row][col] = priceMatrix[row][0];
				}
			}
			priceMatrix[priceMatrix.length - 1] = priceMatrix[0];
			countryOrder = (ArrayList<String>) airportDestinations.clone();
			countryOrder.add(countryOrder.get(0));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		String generatedPriceMatrix = generateDATPrice(priceMatrix);
		String generateCountryData = generateCountryData(countryOrder);
		String generatedDates = generateDates();
		String compiledResult = generatedDates + "\n\n" + generateCountryData
				+ "\n\n" + generatedPriceMatrix;
		return compiledResult;
	}

	private String generateDates() {
		StringBuilder dates = new StringBuilder();
		dates.append("Dates = {");
		for (int i = 0; i < numberOfDays; i++) {
			dates.append(i + 1);

			if (i + 1 < numberOfDays) {
				dates.append(",");
			}
		}
		dates.append("};");

		StringBuilder dates_mod = new StringBuilder();
		dates_mod.append("Dates_mod = {");
		for (int i = 0; i < numberOfDays - 1; i++) {
			dates_mod.append(i + 1);

			if (i + 1 < numberOfDays - 1) {
				dates_mod.append(",");
			}
		}
		dates_mod.append("};");

		String firstDate = "firstdate = 1;";
		String endDate = "enddate = " + numberOfDays + ";";

		String results = firstDate + "\n" + endDate + "\n" + dates + "\n"
				+ dates_mod;
		return results;
	}

	private String generateCountryData(ArrayList<String> countryOrder) {
		String startCountry = null;
		String endCountry = null;
		StringBuilder locations = new StringBuilder(), costOfLiving = new StringBuilder(), satisfactionValue = new StringBuilder(), minCountryStay = new StringBuilder();
		locations.append("Locations = {");
		costOfLiving.append("CostOfLivingOfLocation = [");
		satisfactionValue.append("InitialSatisfactionOfLocation = [");
		minCountryStay.append("MinimumDaysStayAtLocation = [");
		for (int i = 0; i < countryOrder.size(); i++) {
			String country = countryOrder.get(i);

			if (i == 0) {
				startCountry = country;
				locations.append(country);
				minCountryStay.append(1);
			} else if (i == countryOrder.size() - 1) {
				endCountry = country + "TWO";
				locations.append(endCountry);
				minCountryStay.append(1);
			} else {
				locations.append(country);
				minCountryStay.append(generateMinStay());
			}
			costOfLiving.append(countryCost.get(country));
			satisfactionValue.append(generateSatisfationValue());

			if (i + 1 < countryOrder.size()) {
				locations.append(",");
				costOfLiving.append(",");
				satisfactionValue.append(",");
				minCountryStay.append(",");
			}
		}

		locations.append("};");
		costOfLiving.append("];");
		satisfactionValue.append("];");
		minCountryStay.append("];");
		startCountry = "startlocation = " + startCountry + ";";
		endCountry = "endlocation = " + endCountry + ";";
		String lengthOfLocations = "lengthOfLocations = "
				+ numberOfDestinations + ";";
		String biggerThanLengthOfLocation = "biggerThanLengthOfLocation = "
				+ (numberOfDestinations + 1) + ";";
		String result = startCountry + "\n" + endCountry + "\n"
				+ lengthOfLocations + "\n" + biggerThanLengthOfLocation + "\n"
				+ locations + "\n" + costOfLiving + "\n" + satisfactionValue
				+ "\n" + minCountryStay;
		return result;
	}

	private int generateMinStay() {
		int range = (5 - 3) + 1;
		return (int) (Math.random() * range) + 3;
	}

	// f(x) = -0.65x^2+5x+2
	private int generateSatisfationValue() {
		double x = -1;
		while (!(1 <= x && x <= 2.) && !(5.6 <= x && x <= 7)) {
			x = (Math.random() * 10);
			// System.out.println(x);
		}
		int value = (int) (((-0.65) * Math.pow(x, 2) + 5 * x + 2) * 10);
		// System.out.println(x + "\t" + value);
		return value;
	}

	private String generateDATPrice(int[][] priceMatrix) {
		StringBuilder sb = new StringBuilder();
		sb.append("PriceFromToOnDay = [");
		for (int row = 0; row < priceMatrix.length; row++) {
			sb.append("\n\t[");
			for (int col = 0; col < priceMatrix[row].length; col++) {
				int value = priceMatrix[row][col];
				sb.append("\n\t\t[");
				for (int i = 0; i < numberOfDays; i++) {
					// if(i!=0)
					// price
					sb.append(value);
					if (i + 1 < numberOfDays)
						sb.append(valueDelimiter);
				}
				sb.append("]");
			}
			sb.append("\n\t]");
		}
		sb.append("\n];");
		String result = sb.toString();
		return result;
	}

	@SuppressWarnings("unused")
	private void displayMatrix(int[][] priceMatrix) {
		for (String country : countryOrder) {
			System.out.print(country + "\t");
		}
		System.out.println();

		for (int row = 0; row < priceMatrix.length; row++) {
			for (int col = 0; col < priceMatrix[row].length; col++) {
				System.out.print(priceMatrix[row][col] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

}
