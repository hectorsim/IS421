import ilog.concert.*;
import ilog.cplex.*;
import ilog.opl.*;

public class ItineraryPlanner {
	static final String DATADIR = "itineraryplanner/";
	static final String MODSTRING = "warehouse.mod";
	static final String DATSTRING = "simpleWarehouse.dat";
	
	public static void  main(String[] args) throws Exception {
		
		// Initialization of OPL Factory
		IloOplFactory.setDebugMode(false);
		IloOplFactory oplF = new IloOplFactory();
		
		// Retrieve of OPL Module generated from CPLEX
		IloOplModelSource modelSource = oplF.createOplModelSource(DATADIR+MODSTRING);
		
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
		if (!DATSTRING.equalsIgnoreCase("")) {
			IloOplDataSource dataSource = oplF.createOplDataSource(DATADIR+DATSTRING);
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
