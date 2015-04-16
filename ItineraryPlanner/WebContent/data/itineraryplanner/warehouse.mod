string startlocation = ...;
string endlocation = ...;
int lengthOfLocations = ...;
int biggerThanLengthOfLocation = ...;
int firstdate = ...;
int enddate = ...;
float budget = ...;
{string} Locations = ...;
{int} Dates = ...;
{int} Dates_mod = ...;
float CostOfLivingOfLocation[Locations] = ...;
float InitialSatisfactionOfLocation[Locations] = ...;
float UnitDecreaseInSatisfactionPerDay = ...;
float MinimumDaysStayAtLocation[Locations] = ...;
int PriceFromToOnDay[Locations][Locations][Dates] = ...;
dvar boolean U[Locations][Dates];
dvar boolean X[Locations][Locations][Dates];
dvar boolean A[Locations];
dvar int V[Locations];

maximize
  sum( loc in Locations ) (
    A[loc]*InitialSatisfactionOfLocation[loc] + UnitDecreaseInSatisfactionPerDay*A[loc] - 
    sum( d in Dates ) ( U[loc][d] * UnitDecreaseInSatisfactionPerDay ) 
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
		
	/** Each location (other than end location) can only be flown in & out once or none at all */
	forall( l in Locations )
	  if(l!=endlocation && l!=startlocation)
	  	sum(j in Locations, d in Dates) (X[j][l][d]) == sum(j in Locations, d in Dates) (X[l][j][d]);
	  	
	// for all locations, you cannot travel in or out more than once
	forall(l in Locations)
	  sum(j in Locations, d in Dates) (X[j][l][d]) <= 1; 
	forall(l in Locations)
	  sum(j in Locations, d in Dates) (X[l][j][d]) <= 1; 
	// start location can only be traveled out, end location can only be traveled in
	sum(j in Locations, d in Dates) X[startlocation][j][d] == 1;
	sum(j in Locations, d in Dates) X[j][startlocation][d] == 0;
	sum(j in Locations, d in Dates) X[endlocation][j][d] == 0;
	sum(j in Locations, d in Dates) X[j][endlocation][d] == 1;
	
	/** Binatize A from X */
	forall(l in Locations)
	  A[l] == sum(d in Dates, j in Locations)(X[l][j][d]);
	
	/** START, END LOCATION must be travelled to*/
	U[startlocation][firstdate] == 1;
	U[endlocation][enddate] == 1;
	
	/** Cannot be at two places in the same day */
	forall( date in Dates )
		sum( loc in Locations )
			U[loc][date] == 1;
			
	/** Total budget constraint */
	sum(d in Dates, l in Locations)( U[l][d] * CostOfLivingOfLocation[l] ) 
	+ sum(l in Locations, j in Locations, d in Dates)(X[l][j][d] * PriceFromToOnDay[l][j][d])
	<= budget;
	
	/** Subtour */
	forall(l in Locations)
	  2 <= V[l] <= lengthOfLocations;
	forall(l in Locations, j in Locations)
	  V[l]-V[j]+(lengthOfLocations-1)*(sum(d in Dates)X[l][j][d]) <= lengthOfLocations-2;
	
	/** minimum stay */
	forall(l in Locations)
	  (1-A[l])*biggerThanLengthOfLocation+sum(d in Dates)(U[l][d]) >= MinimumDaysStayAtLocation[l];
	
	/** Departure of a location shd be later than arrival except for L*/
	forall(l in Locations)
	  if(l!=startlocation&&l!=endlocation)
	  	sum(d in Dates, j in Locations)(X[l][j][d] * d) + 1 >=  sum(d in Dates, j in Locations)(X[j][l][d] * d);

}

execute new_display {
      //display to scripting log
      var after = cplex.getCplexTime();
      writeln("Result======");
      writeln("Time ", cplex.getCplexTime(), " seconds.");  
      writeln("Objective value: ",cplex.getObjValue()); 
     
      //open file and write file 
      var ofile1 = new IloOplOutputFile("result.txt",true);
      ofile1.writeln("Result======");
      var line="Objective value: "+cplex.getObjValue()
      +"Time "+cplex.getCplexTime()+" seconds.";
      ofile1.writeln(line);
     
      //write the routes
      line="";
      var flight = 0;
      var living = 0;
      for(var d=1; d<enddate;d++) { // from 1..D-1
            line=line+"Location at Day "+d+": ";  
            for(l in Locations){
            if(U[l][d]==1){ // user is at location
            	living = living + CostOfLivingOfLocation[l];
             		if(U[l][d+1]==1){ // the next day is in the same place
             		     line=line+l+" stay and incurring	daily cost "+CostOfLivingOfLocation[l];   		
             		} else { // if not same place tmr, where are you flying to?
             		     for(j in Locations){
             		     	if(U[j][d+1]==1){
             		         	line=line+"from "+l+" flying to " + j + " with ticket cost at " + PriceFromToOnDay[l][j][d];
             		         	flight = flight + PriceFromToOnDay[l][j][d];
                      		}             		         
             		     }        		
             		}
             	}           
            }
            line =line+"\n";
      }
      line = line + " Flight cost: "+ flight + " + cost of living: " + living + " = "+ (flight+living) + "\n";
      line = line + " Left Budget = " + (budget-flight-living);
      //display to scripting log
      writeln(line);
      //write file
      ofile1.writeln(line);
      //close file
      ofile1.close();
}