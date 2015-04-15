package opl;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import util.Constants;
import controller.PriceMatrix;

public class ItineraryPlanner {

	private static final ArrayList<String> ALL_DESTINATIONS = new ArrayList<String>() {
		{
			add("SIN");
			add("PEK");
			add("PVG");
			add("HKG");
			add("KIX");
			add("HND");
			add("ICN");
			add("KHH");
			add("TPE");
			add("PNH");
			add("REP");
			add("DPS");
			add("CGK");
			add("KUL");
			add("PEN");
			add("RGN");
			add("MNL");
			add("BKK");
			add("HKT");
			add("HAN");
			add("SGN");
		}
	};

	public static void main(String[] args) throws Exception {
		int tripLength = 0;
		String budget = null;
		String startDestination = null;
		ArrayList<String> selectedDestination = null;

		// get args input
		for (int i = 0; i < args.length; i++) {
			String parameter = args[i];
			switch (parameter) {
			case "-tripLength":
				i++;
				tripLength = Integer.parseInt(args[i]);
				break;
			case "-budget":
				i++;
				budget = args[i];
				break;
			case "-destinations":
				i++;
				String destinations = args[i];
				String[] destArray = destinations.split(",");
				selectedDestination = new ArrayList<String>(
						Arrays.asList(destArray));
				break;
			case "-startDestination":
				i++;
				startDestination = args[i];
				break;
			}
		}

		String errorMessage = "";
		boolean hasError = false;
		if (tripLength < 3) {
			errorMessage += "Please specify Trip Length from 3 - 10 days\n";
			hasError = true;
		}

		if (budget == null) {
			errorMessage += "Please specify budget\n";
			hasError = true;
		}

		if (startDestination == null) {
			errorMessage += "Please specify Start Destination\n";
			hasError = true;
		}

		if (selectedDestination == null) {
			selectedDestination = ALL_DESTINATIONS;
		} else if (selectedDestination.size() < 3) {
			errorMessage += "There must be at least 3 destinations\n";
			hasError = true;
		} else if (!selectedDestination.contains(startDestination)) {
			errorMessage += "Start Destination must be an existing destination\n";
			hasError = true;
		}

		if (hasError) {
			System.out.println(errorMessage);
		} else {
			ItineraryPlanner iPlanner = new ItineraryPlanner();
			File datFile = iPlanner.generateDat(tripLength, budget,
					selectedDestination, startDestination);
			System.out.println(datFile.getName());
			iPlanner.runOPL(datFile.getName());
			iPlanner.cleanup(datFile);
		}
	}

	private void cleanup(File datFile) {
		if (datFile.delete()) {
			System.out.println(datFile.getName() + " is removed!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}

	private File generateDat(int tripLength, String budget,
			ArrayList<String> selectedDestination, String startDestination) {
		System.out.println(tripLength + "\t" + budget + "\t"
				+ selectedDestination.size() + "\t" + startDestination);
		PriceMatrix pm = new PriceMatrix(selectedDestination, tripLength);
		String results;
		try {
			results = pm.generateDAT();

			String budgetString = "budget = " + budget + ";";
			String unitDecrease = "UnitDecreaseInSatisfactionPerDay = "
					+ Constants.satisfactionDecreaseStep + ";";

			results = budgetString + "\n" + unitDecrease + "\n\n" + results;

			// System.out.println(results);

			// write to file
			// final String fileName = "/travel_" + airportCodes.size() +
			// ".dat";
			// File file = new File(saveDirectory + fileName);
			Calendar cal = Calendar.getInstance();
			String date = cal.get(Calendar.DAY_OF_MONTH) + ""
					+ cal.get(Calendar.MONTH) + "" + cal.get(Calendar.YEAR);
			String time = cal.get(Calendar.HOUR_OF_DAY) + "_"
					+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);
			String datString = "travel-" + date + "T" + time + ".dat";
			File file = new File(Constants.DATADIR + datString);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(results);
			bw.close();
			return file;
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void runOPL(String datFile) {
		// Initialization of OPL Factory
		IloOplFactory.setDebugMode(false);
		IloOplFactory oplF = new IloOplFactory();
		try {

			// Retrieve of OPL Module generated from CPLEX
			IloOplModelSource modelSource = oplF
					.createOplModelSource(Constants.DATADIR
							+ Constants.MODSTRING);

			/*
			 * Amendment of settings for the module
			 * http://www-01.ibm.com/support/knowledgecenter
			 * /SSSA5P_12.6.1/ilog.odms.
			 * ide.help/refjavaopl/html/ilog/opl/IloOplSettings.html
			 */
			IloOplErrorHandler errHandler = oplF.createOplErrorHandler();
			IloOplSettings settings = oplF.createOplSettings(errHandler);
			IloOplModelDefinition def = oplF.createOplModelDefinition(
					modelSource, settings);

			// Creation of cplex environment
			IloCplex cplex = oplF.createCplex();
			cplex.setOut(null);

			// Define the module in cplex
			IloOplModel opl = oplF.createOplModel(def, cplex);

			// Include data source into opl
			// if (!Constants.DATSTRING.equalsIgnoreCase("")) {
			// IloOplDataSource dataSource = oplF
			// .createOplDataSource(Constants.DATADIR
			// + Constants.DATSTRING);
			// opl.addDataSource(dataSource);
			// }

			if (datFile != null) {
				IloOplDataSource dataSource = oplF
						.createOplDataSource(Constants.DATADIR + datFile);
				opl.addDataSource(dataSource);
			}

			// Generate results for opl
			opl.generate();

			// Print out results
			if (cplex.solve()) {
				System.out.println("OBJECTIVE:" + opl.getCplex().getObjValue());
				opl.postProcess();
				opl.printSolution(System.out);
			} else {
				System.out.println("No Solution");
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			oplF.end();
		}
	}
}
