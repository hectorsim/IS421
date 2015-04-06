/*********************************************
 * OPL 12.6.0.0 Model
 * Author: Leon
 * Creation Date: Mar 25, 2015 at 6:18:24 PM
 *********************************************/
string startlocation = ...;
int lengthOfLocations = ...;
int firstdate = ...;
int enddate = ...;
float budget = ...;
{string} Locations = ...;
{string} Locations_2_N_1 = ...;
{int} Dates = ...;
{int} Dates_mod = ...;
{int} Dates_mod2 = ...;
float CostOfLivingOfLocation[Locations] = ...;
float InitialSatisfactionOfLocation[Locations] = ...;
float UnitDecreaseInSatisfactionPerDay = ...;
int PriceFromToOnDay[Locations][Locations][Dates] = ...;

dvar boolean U[Locations][Dates];
dvar boolean X[Locations][Locations][Dates];
dvar boolean A[Locations];
dvar int V[Locations];

maximize
  sum( loc in Locations ) (
    A[loc]*InitialSatisfactionOfLocation[loc] + UnitDecreaseInSatisfactionPerDay*A[loc] - 
    sum( d in Dates )
		U[loc][d] * UnitDecreaseInSatisfactionPerDay
  );
  
subject to {
	// Sum of every location per day = 1
	forall( date in Dates )
		sum( loc in Locations )
		  U[loc][date] == 1;
	
	// Sum of flight cost and cost of living within total budget
	sum(d in Dates, l in Locations)(
		U[l][d] * CostOfLivingOfLocation[l] +
		sum(j in Locations)
			X[l][j][d] * PriceFromToOnDay[l][j][d]
	) <= budget;

	// start and end location is the same
	1 == sum(l in Locations) (  X[startlocation][l][firstdate]) == sum(l in Locations) (  X[l][startlocation][enddate]);
	
	// Connectivity of tour
	forall( l in Locations )
	  sum(j in Locations, d in Dates) (X[j][l][d]) == sum(j in Locations, d in Dates) (X[l][j][d]);
	forall(l in Locations)
	  sum(j in Locations, d in Dates) (X[j][l][d]) <= 1; 
	
	// Subtour elimination - INCOMPLETE
	forall(l in Locations)
	  2 <= V[l] <= lengthOfLocations;
	forall(l in Locations, j in Locations_2_N_1)
	  V[l]-V[j]+(lengthOfLocations-1)*(sum(d in Dates)X[l][j][d]) <= lengthOfLocations-2;
	
	// Whether a location was visited = whether departed from the location
	forall(l in Locations)
	  A[l] == sum(d in Dates, j in Locations)(X[l][j][d]);
	
	// Departure of a location visited must be later than arrival
	forall(l in Locations)
	  sum(d in Dates, j in Locations)(X[l][j][d] * d) <= 1 + sum(d in Dates, j in Locations)(X[j][l][d] * d);
	
	// Location visited for first day is at location L
	U[startlocation][firstdate]==1;	
	
	// Ensure Xljd = 1 if location l is travelled to location j at day d - INCOMPLETE
	forall(l in Locations, j in Locations, d in Dates, d2 in Dates_mod2)
		U[l][d]+U[j][d2] - 1 <= X[l][j][d];
	forall(l in Locations, j in Locations, d in Dates, d2 in Dates_mod2){
		if(l!=j){
			U[l][d]+U[j][d2]/2 >= X[l][j][d]; 
		}
	}
	
	/*
	forall(l in Locations, j in Locations, d in Dates)
		U[l][d]+U[j][d+1] - 1 <= X[l][j][d];
	forall(l in Locations, j in Locations, d in Dates_mod){
		if(l!=j){
			U[l][d]+U[j][d+1]/2 >= X[l][j][d]; 
		}
	}
	
	*/
	
	// No flight from and to same location on any day
	forall(l in Locations, d in Dates)
	  X[l][l][d] == 0;
}

/*
// 1.	User must be in only one location at any day.
	forall( date in Dates )
		sum(location in Locations)
		  	X[location][date] == 1;
		  	
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
*/