package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import util.Constants;

public class FlightPrice {

	private final static String folderName = "./data/";
	private final static String fileName = "travel_"+ Constants.numberOfDestinations +".dat";

	public static void main(String[] args) throws Exception {
		// RetrievePrice rp = new RetrievePrice();
		// rp.processCSV();

		PriceMatrix pm = new PriceMatrix();
		String results = pm.generatePriceDAT();

		String budget = "budget = " + Constants.budget + ";";
		String unitDecrease = "UnitDecreaseInSatisfactionPerDay = "
				+ Constants.satisfactionDecreaseStep + ";";

		results = budget + "\n" + unitDecrease + "\n\n" + results;

		System.out.println(results);
		try {
			// write to file
			File file = new File(folderName+fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(results);
			bw.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}
}
