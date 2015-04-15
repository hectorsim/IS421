package com.opl;

import com.ItineraryPlanner.Constants;

import ilog.cplex.IloCplex;
import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;

public class OPLFactory {
	
	public static void runOPL(String context) throws Exception {
		String dataDir = context + Constants.DATAPATH;
		String modString = Constants.MODSTRING;
		String datString = Constants.DATSTRING2;
		
		// Initialization of OPL Factory
		IloOplFactory.setDebugMode(false);
		IloOplFactory oplF = new IloOplFactory();
		
		// Retrieve of OPL Module generated from CPLEX
		IloOplModelSource modelSource = oplF.createOplModelSource(dataDir+modString);
		
		/*
		 * Amendment of settings for the module
		 * http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.1/ilog.odms.ide.help/refjavaopl/html/ilog/opl/IloOplSettings.html
		 */
		IloOplErrorHandler errHandler = oplF.createOplErrorHandler();
		IloOplSettings settings = oplF.createOplSettings(errHandler);
		IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource, settings);
		
		// Creation of cplex environment
		IloCplex cplex = oplF.createCplex();
		cplex.setOut(null);
		
		// Define the module in cplex
		IloOplModel opl = oplF.createOplModel(def, cplex);
		
		// Include data source into opl
		if (!datString.equalsIgnoreCase("")) {
			IloOplDataSource dataSource = oplF.createOplDataSource(dataDir+datString);
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
		
		oplF.end();
	}
}
