/*********************************************
 * OPL 12.6.0.0 Model
 * Author: Leon
 * Creation Date: Mar 25, 2015 at 6:18:24 PM
 *********************************************/
string startlocation = ...;
int lengthOfLocations = ...;
int biggerThanLengthOfLocation = ...;
int firstdate = ...;
int enddate = ...;
float budget = ...;
{string} Locations = ...;
{string} Locations_2_N_1 = ...;
{int} Dates = ...;
{int} Dates_mod = ...;
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
	/** Binding Constraint binatize X from U */
	forall(l in Locations, j in Locations, d in Dates_mod){
		if(l!=j){
			U[l][d]+U[j][d+1] - 1 <= X[l][j][d];	
		}
	}
	forall(l in Locations, j in Locations, d in Dates_mod){
		if(l!=j){
			U[l][d]+U[j][d+1]/2 >= X[l][j][d]; 
		}
	}
	// Force all last X variable to be 0
	sum(l in Locations, j in Locations)
	  X[l][j][enddate] == 0;
	// No flight from and to same location on any day
	forall(l in Locations, d in Dates)
		X[l][l][d] == 0;
		
	/** Each location can only be flown in & out once or none at all */
	forall( l in Locations )
	  sum(j in Locations, d in Dates) (X[j][l][d]) == sum(j in Locations, d in Dates) (X[l][j][d]);
	forall(l in Locations)
	  sum(j in Locations, d in Dates) (X[j][l][d]) <= 1; 
	  
	/** Binatize A from X */
	forall(l in Locations)
	  A[l] == sum(d in Dates, j in Locations)(X[l][j][d]);
	
	/** START = END LOCATION */
	U[startlocation][firstdate] == 1;
	U[startlocation][enddate] == 1;
	
	/** Cannot be at two places in the same day */
	forall( date in Dates )
		sum( loc in Locations )
			U[loc][date] == 1;
	/** Total budget constraint */
	sum(d in Dates, l in Locations)( U[l][d] * CostOfLivingOfLocation[l] ) 
	+ sum(l in Locations, j in Locations, d in Dates)(X[l][j][d] * PriceFromToOnDay[l][j][d])
	<= budget;
	
	/** Subtour BUG HERE */
	forall(l in Locations)
	  2 <= V[l] <= lengthOfLocations;
	forall(l in Locations, j in Locations)
	  V[l]-V[j]+(lengthOfLocations-1)*(sum(d in Dates)X[l][j][d]) <= lengthOfLocations-2;
	
	
	/** Departure of a location shd be later than arrival except for L*/
	forall(l in Locations)
	  if(l!=startlocation)
	  	sum(d in Dates, j in Locations)(X[l][j][d] * d) <= 1 + sum(d in Dates, j in Locations)(X[j][l][d] * d);
	sum(d in Dates, j in Locations)(X[startlocation][j][d] * d)+1 >= sum(d in Dates, j in Locations)(X[j][startlocation][d] * d);

	/* OLD CODES
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
	1 == sum(l in Locations) (  X[startlocation][l][firstdate]) == sum(l in Locations) (  X[l][startlocation][enddate-1]);
	
	// Connectivity of tour
	forall( l in Locations )
	  sum(j in Locations, d in Dates) (X[j][l][d]) == sum(j in Locations, d in Dates) (X[l][j][d]);
	forall(l in Locations)
	  sum(j in Locations, d in Dates) (X[j][l][d]) <= 1; 
	
	
	//forall(l in Locations)
	//  (sum(d in Dates, j in Locations) (X[l][j][d]*d)-sum(d in Dates, j in Locations)(X[j][l][d]*d)) == sum(d in Dates)U[l][d];
    
	
	// Subtour elimination - INCOMPLETE
	forall(l in Locations)
	  2 <= V[l] <= lengthOfLocations;
	forall(l in Locations, j in Locations)
	  V[l]-V[j]+(lengthOfLocations-1)*(sum(d in Dates)X[l][j][d]) <= lengthOfLocations-2;
	
	// Whether a location was visited = whether departed from the location
	forall(l in Locations)
	  A[l] == sum(d in Dates, j in Locations)(X[l][j][d]);
	
	// Departure of a location visited must be later than arrival
	forall(l in Locations)
	  sum(d in Dates, j in Locations)(X[l][j][d] * d) <= 1 + sum(d in Dates, j in Locations)(X[j][l][d] * d);
	
	// Ensure Xljd = 1 if location l is travelled to location j at day d
	forall(l in Locations, j in Locations, d in Dates, d2 in Dates_mod2)
		U[l][d]+U[j][d2] - 1 <= X[l][j][d];
	forall(l in Locations, j in Locations, d in Dates, d2 in Dates_mod2){
		if(l!=j){
			U[l][d]+U[j][d2]/2 >= X[l][j][d]; 
		}
	}
	
	//forall(l in Locations, j in Locations, d in Dates)
	//	U[l][d]+U[j][d+1] - 1 <= X[l][j][d];
	//forall(l in Locations, j in Locations, d in Dates_mod){
	//	if(l!=j){
	//		U[l][d]+U[j][d+1]/2 >= X[l][j][d]; 
	//	}
	//}
	
	// No flight from and to same location on any day
	forall(l in Locations, d in Dates)
	  X[l][l][d] == 0;
	  
	  
	// Location visited for first day is at location L
	U[startlocation][firstdate]==1;		
	*/
}