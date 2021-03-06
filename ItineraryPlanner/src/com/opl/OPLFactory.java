package com.opl;

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
import java.util.Calendar;

import org.json.simple.JSONObject;

import com.ItineraryPlanner.Constants;
import com.entity.User;
import com.heuristic.Solutions;

public class OPLFactory {

	public static File generateDat(int tripLength, String budget,
			ArrayList<String> selectedDestination, ArrayList<Integer> satisfactionArray, 
			String startDestination) {
		
		System.out.println(tripLength + "\t" + budget + "\t"
				+ selectedDestination.size() + "\t" + startDestination);
		
		PriceMatrix pm = new PriceMatrix(selectedDestination, tripLength, satisfactionArray);
		String results;
		try {
			results = pm.generateDAT();

			String budgetString = "budget = " + budget + ";";
			// String unitDecrease = "UnitDecreaseInSatisfactionPerDay = "
			// 		+ Constants.satisfactionDecreaseStep + ";";

			// results = budgetString + "\n" + unitDecrease + "\n\n" + results;
			results = budgetString + "\n\n" + results;

			//System.out.println(results);

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
			String outputPath = OPLFactory.class.getResource(
					Constants.OPL_DATADIR).getPath()
					+ "/" + datString;
			File file = new File(outputPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(results);
			bw.close();
			//System.out.println("@@@@@"+file.getAbsolutePath());
			return file;
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject runOPL(User user, File datFile) throws Exception {
		JSONObject json_Results = new JSONObject();
		
		// Initialization of OPL Factory
		IloOplFactory.setDebugMode(false);
		IloOplFactory oplF = new IloOplFactory();
		try {

			// Retrieve of OPL Module generated from CPLEX
			String path = OPLFactory.class.getResource(
					Constants.OPL_DATADIR).getPath()
					+ "/" + Constants.OPL_MODSTRING;
			File file = new File(path);
			path = file.getAbsolutePath();
			
			IloOplModelSource modelSource = oplF.createOplModelSource(path);

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
						.createOplDataSource(datFile.getAbsolutePath());
				opl.addDataSource(dataSource);
			}

			// Generate results for opl
			opl.generate();

			// Print out results
			if (cplex.solve()) {
				System.out.println("OBJECTIVE:" + opl.getCplex().getObjValue());
				opl.postProcess();
				opl.printSolution(System.out);
				Solutions opl_solution = user.processOPL(opl);
				
				json_Results = user.generateOPL(opl_solution);
				System.out.println(json_Results);

			} else {
				json_Results.put("Error", "No Optimal Solution Found!");
			}
		} catch (IloException e) {
			e.printStackTrace();
		} finally {
			oplF.end();
		}
		
		return json_Results;
	}

	public static void cleanup(File datFile) {
		if (datFile.delete()) {
			System.out.println(datFile.getName() + " is removed!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}
}
