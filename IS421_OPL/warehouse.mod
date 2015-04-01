/*********************************************
 * OPL 12.6.0.0 Model
 * Author: Leon
 * Creation Date: Mar 25, 2015 at 6:18:24 PM
 *********************************************/
int Fixed = ...;
int FixedBudget = ...;
{string} Warehouses = ...;
int NbStores = ...;
range Stores = 0..NbStores-1;
int Capacity[Warehouses] = ...;
int SupplyCost[Stores][Warehouses] = ...;
dvar boolean Open[Warehouses];
dvar boolean Supply[Stores][Warehouses];

minimize
  sum( w in Warehouses )
    Fixed * Open[w] +
	sum( w in Warehouses, s in Stores )
	  SupplyCost[s][w] * Supply[s][w];
	  
subject to {
	forall( s in Stores )
	  	ctEachStoreHasOneWarehouse:
	  	sum ( w in Warehouses )
	    	Supply[s][w] == 1;
	
	forall ( w in Warehouses, s in Stores )
	  	ctUseOpenWarehouses:
	  	Supply[s][w] <= Open[w];
  	
  	forall ( w in Warehouses )
		ctMaxUseOfWarehouse:
    	sum( s in Stores )
      		Supply[s][w] <= Capacity[w];
}

{int} Storesof[w in Warehouses] = { s | s in Stores : Supply[s][w] == 1 };

execute DISPLAY_RESULTS {
	writeln("Open=",Open);
	writeln("Storesof=", Storesof);
}