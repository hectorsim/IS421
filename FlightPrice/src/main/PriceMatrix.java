package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import util.Constants;

import com.opencsv.CSVReader;

public class PriceMatrix {

	// private boolean debug = true;
	// private int numberOfDays = 5;
	// private final int numberOfDestinations = 21;
	// private final int dashValue = 99999;

	private String valueDelimiter = " ";
	private final int totalDestinations = 21;

	private final String fileName = "./data/price_matrix.csv";
	private int[][] priceMatrix = null;
	private ArrayList<String> countryOrder;

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

	public String generatePriceDAT() throws Exception {
		if (Constants.numberOfDestinations > totalDestinations) {
			Exception ex = new Exception("Number of destinations cannot be more than specified: "
					+ totalDestinations);
			throw ex;
		}
		countryOrder = new ArrayList<String>();
		priceMatrix = new int[Constants.numberOfDestinations][Constants.numberOfDestinations];
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(new File(fileName)));
			String[] record = null;
			int lineCounter = 0;
			String currentCountry = null;
			while ((record = reader.readNext()) != null) {
				if (lineCounter == Constants.numberOfDestinations)
					break;
				for (int i = 0; i < record.length; i++) {
					if (i == 0) {
						currentCountry = record[i];
						countryOrder.add(currentCountry);
						continue;
					}
					String val = record[i].toString();
					int price = 0;
					if (val.equals("-"))
						price = Constants.dashValue;
					else
						price = Integer.parseInt(val);
					if ((i - 1) == Constants.numberOfDestinations)
						break;

					priceMatrix[lineCounter][i - 1] = price;
				}
				lineCounter++;
			}
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
		if (Constants.debug)
			displayMatrix(priceMatrix);
		String generatedPriceMatrix = generateDATPrice(priceMatrix);
		String generateCountryData = generateCountryData(countryOrder);
		String generatedDates = generateDates();
		String compiledResult = generatedDates + "\n\n" + generateCountryData + "\n\n"
				+ generatedPriceMatrix;
		return compiledResult;
	}

	private String generateDates() {
		StringBuilder dates = new StringBuilder();
		dates.append("Dates = {");
		for (int i = 0; i < Constants.numberOfDays; i++) {
			dates.append(i + 1);

			if (i + 1 < Constants.numberOfDays) {
				dates.append(",");
			}
		}
		dates.append("};");

		StringBuilder dates_mod = new StringBuilder();
		dates_mod.append("Dates_mod = {");
		for (int i = 0; i < Constants.numberOfDays - 1; i++) {
			dates_mod.append(i + 1);

			if (i + 1 < Constants.numberOfDays - 1) {
				dates_mod.append(",");
			}
		}
		dates_mod.append("};");

		StringBuilder dates_mod2 = new StringBuilder();
		dates_mod2.append("Dates_mod2 = {");
		for (int i = 1; i < Constants.numberOfDays - 2; i++) {
			dates_mod2.append(i + 1);

			if (i + 1 < Constants.numberOfDays - 2) {
				dates_mod2.append(",");
			}
		}
		dates_mod2.append("};");

		String firstDate = "firstdate = 1;";
		String endDate = "enddate = " + Constants.numberOfDays + ";";

		String results = firstDate + "\n" + endDate + "\n" + dates + "\n" + dates_mod + "\n"
				+ dates_mod2;

		return results;
	}

	private String generateCountryData(ArrayList<String> countryOrder) {
		String startCountry = null;
		StringBuilder locations = new StringBuilder(), locations2 = new StringBuilder(), costOfLiving = new StringBuilder(), satisfactionValue = new StringBuilder();
		locations.append("Locations = {");
		locations2.append("Locations_2_N_1 = {");
		costOfLiving.append("CostOfLivingOfLocation = [");
		satisfactionValue.append("InitialSatisfactionOfLocation = [");

		for (int i = 0; i < countryOrder.size(); i++) {
			String country = countryOrder.get(i);
			locations.append(country);
			costOfLiving.append(countryCost.get(country));
			satisfactionValue.append(generateSatisfationValue());
			if (i == 0)
				startCountry = country;

			if (i != 0)
				locations2.append(country);

			if (i + 1 < countryOrder.size()) {
				locations.append(",");
				costOfLiving.append(",");
				satisfactionValue.append(",");
				if (i != 0)
					locations2.append(",");
			}
		}
		locations.append("};");
		locations2.append("};");
		costOfLiving.append("];");
		satisfactionValue.append("];");
		startCountry = "startlocation = " + startCountry + ";";
		String lengthOfLocations = "lengthOfLocations = " + Constants.numberOfDestinations + ";";
		String result = startCountry + "\n" + lengthOfLocations + "\n" + locations + "\n"
				+ locations2 + "\n" + costOfLiving + "\n" + satisfactionValue;
		return result;
	}

	// f(x) = -0.65x^2+5x+2
	private int generateSatisfationValue() {
		double x = -1;
		while ( !(1 <= x && x <= 2.) && !(5.6 <= x && x <= 7)) {
			x = (Math.random() * 10);
//			System.out.println(x);
		}
		int value = (int)(((-0.65) * Math.pow(x, 2) + 5 * x + 2)*10);
//		System.out.println(x + "\t" + value);
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
				for (int i = 0; i < Constants.numberOfDays; i++) {
					// if(i!=0)
					// price
					sb.append(value);
					if (i + 1 < Constants.numberOfDays)
						sb.append(valueDelimiter);
				}
				sb.append("]");
			}
			sb.append("\n\t]");
		}
		sb.append("\n];");
		String result = sb.toString();
		if (Constants.debug)
			System.out.println(result);
		return result;
	}

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
