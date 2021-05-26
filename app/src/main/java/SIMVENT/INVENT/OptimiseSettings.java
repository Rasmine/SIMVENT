package SIMVENT.INVENT;

/**
 * @(#)OptimiseSettings.java
 */

//package invent;

import java.util.Date;

/**
 * This class contains the algorithm/function for calculating the optimal ventilator settings.
 * the calculation is carried out in the method optimiseSets().
 * By Nlandu Kamavuako & Mogens Nielsen */



public final class OptimiseSettings
{
    private boolean debug = false; //detailed debug information written to std out
    private boolean debug_overview = false; //debug summary information written
    private PatientState current;
    private PatientState pState;
    private Settings pSettings;
    private Penalties presentPen;
    private Prediction prediction;
    
    boolean calculationCancel = false;
    
    Penalties tmpPenalty = new Penalties();
        
    public OptimiseSettings(Prediction prediction, PatientState current,PatientState pState, Settings pSettings)
    {
        this.prediction = prediction;
        this.pState=pState;
        this.pSettings=pSettings;
        this.current = current;
    }

    /**
     * The algorithm in this method is based on a function with a vertical assymptote
	 * og en oblique assymptote on the user settings to optimize the minute volume.
	 * this is done because the minute volume is decisive and each calculation of 
	 * penalties invokes a state prediction and takes a considerable amount of time 
	 * to execute.)
     * When the minute volume (MV) (with constant frequency) is calculated by fitting
	 * 4 points to the following function (ax^2 +bx +c)/x+d, 
	 * the computed MV is corrected using the gradietn method.
	 * This is then used as the starting point to investigate all frequencies 
	 * between 5 and 30 (step=1).
	 * For each frequency and based on the MV with lowest penalty, a prediction step 
	 * is taken to predict the coresponding VT, which is then corrected using the
	 * gradient. Typically the correction requires almost 5 iterations to converge
	 * to the real value with a step of 0.001.
	 * 
	 * The total penalty is calculated with 4 ciffers after comma and the MV with the 
	 * lowest penalty and the corresponding F og VT is retained as the best combination.
	 * 
	 * Then a parabolic assymption is used to compute the best FIO2 and this is again 
	 * corrected, not with the gradient, but by considering the neighbouring points.
	 * The the algorithm terminates.	  
     */
	 
	 
    public Settings optimiseSets()
    {
        	Date startdate = new Date();
                this.pSettings.setFIO2(this.current.getFiO2());
		
		Settings backup = new Settings(this.pSettings);
		
		double volumeOne = 0;
		double volumeTwo = 0;
		double volumeThree = 0;
		double volumeStart = 0;
		Settings result = new Settings();
		
		//System.out.println(" Minute volumes "+ volumeStart+" "+volumeOne+" "+volumeTwo+" "+volumeThree);
		Settings fres = optimizeVT(this.pSettings.getF());
               
                result = gradientONVT(0.08,fres);
                
                double minVol = (result.getVT()-this.pState.getVd())*result.getF();
                PatientState resultState = this.prediction.calculatePrediction(this.pState,result);
                
		if (minVol<2 || minVol>12 || resultState==null)
		{
                    
                    fres = optimizeVT(this.current.getFreq());
                    backup = gradientONVT(0.08,fres);
                    minVol = (backup.getVT()-this.pState.getVd())*backup.getF();
                    if (debug) System.out.println ("Freq   "+this.current.getFreq());
		}
                else
                {
                    double predictVT = minVol/(this.current.getFreq()) + this.pState.getVd();
                    result.setF(this.current.getFreq());
                    result.setVT(predictVT);
                    backup = gradientONVT(0.008,result);
                    minVol = (backup.getVT()-this.pState.getVd())*backup.getF();
                    System.out.println ("Freq  "+this.current.getFreq());
	
                }
                
		// Minimum volume is found, then step prediction strts here
		
		double finish = 8;
		result = new Settings(backup);
		resultState = this.prediction.calculatePrediction(this.pState,result);
		Penalties resultPen = new Penalties(resultState);
		double minPen = resultPen.getTotal();
		
		Settings resultTwo = result;
		Settings stepSettings = new Settings(result);
                System.out.println ("STARTING HERE");
		while (!calculationCancel && finish<=30)
		{
			if (debug) System.out.println ("Prediction volume:  "+minVol);
			double predictVT = minVol/finish + this.pState.getVd();
			if (debug) System.out.println ("Prediction step:  "+predictVT);
			
			stepSettings.setF(finish);
			stepSettings.setVT(predictVT);
			
			Settings finSet = gradientONVT(0.008,stepSettings);  /////////
			if (debug) 
			{
				System.out.println ("Prediction step:  "+finSet.getVT());
				
			}
			
			resultState = this.prediction.calculatePrediction(this.pState,finSet);
			resultPen = new Penalties(resultState);
			double r = resultPen.getTotal();
                        
                        //System.out.println("Frequency " +finSet.getF());
                        //System.out.println("vt " +finSet.getVT());
                        //System.out.println("Penalty " +r);
                        
			
			if (debug) 
			{
				System.out.println ("r:  "+r+0.0001);
				System.out.println ("Prediction pen:  "+minPen);
				
			}
			
			if (r+0.00045<minPen)
			{
				result = finSet;
				minPen = r;
				if (debug) 
				{
					System.out.println ("minimum pen:  "+minPen);
					System.out.println ("minimum pen frequency: "+result.getF());
				}
                                
			}
			
			System.out.println ("Frequency  "+finish);
			
			minVol =(result.getVT()-this.pState.getVd())*result.getF();
			finish = finish+1;
		}
		
		 System.out.println ("FINISHING HERE");				
		Settings minSettings = result;
				
		if (debug)
		{
			System.out.println("backup F  "+backup.getF());
			System.out.println("backup Fio2  "+backup.getFIO2());
			System.out.println("backup VT  "+backup.getVT());
		}
		
		/// fio2 
            if (!calculationCancel)
            {
		Settings firstStrategy = new Settings(minSettings); // start position
		Settings secondStrategy =  new Settings(minSettings); // position 2 on the gradient
		Settings thirdStrategy =  new Settings(minSettings);  // position 3 at the zero of the tangent.
		Settings dfio2Set = new Settings(minSettings); // derivatives to determine the gradient
		    
		// make a parabola out of those 3 points   just a guess
		double fio2 = this.pSettings.getFIO2();
		firstStrategy.setFIO2(fio2);
			
		double delta = 0.003;
		double dFio2 = fio2+delta;
					
		PatientState firstState = this.prediction.calculatePrediction(this.pState,firstStrategy);
        if (firstState==null)
		{
			firstStrategy.setFIO2(0.25);
			firstState = this.prediction.calculatePrediction(this.pState,firstStrategy);  
		}
		double firstPenalty = this.presentPen.calcPenalties(firstState);
		dfio2Set.setFIO2(dFio2);
			
		PatientState dfio2State = this.prediction.calculatePrediction(this.pState,dfio2Set);
        if (dfio2State==null)
		{
			dfio2Set.setFIO2(0.30+delta);
		    dfio2State = this.prediction.calculatePrediction(this.pState,dfio2Set);
	        
		}
		double dfio2Penalty = this.presentPen.calcPenalties(dfio2State) - firstPenalty;
			
		double fio2Grad = dfio2Penalty/delta; // fio2 gradient;
		if (debug) System.out.println("fio2 grad  "+fio2Grad);
			
		double stepFio2 = 0.05*fio2Grad/Math.abs(fio2Grad);
		double fio2Inter = fio2-firstPenalty/fio2Grad;
			
		if (debug) System.out.println("fio2 inter  "+fio2Inter);
		if (fio2Inter<=0.21) fio2Inter = fio2-0.03;
		if (fio2Inter>= 0.9) fio2Inter = fio2+0.1;
		if (fio2Inter>= fio2 && fio2Inter<=fio2-stepFio2) fio2Inter = fio2-stepFio2+0.15;
 		
                
		secondStrategy.setFIO2(fio2-stepFio2);
		thirdStrategy.setFIO2(fio2Inter);
				
		if (debug) System.out.println("first strategy  "+firstStrategy.getFIO2());
		if (debug) System.out.println("second strategy  "+secondStrategy.getFIO2());
		if (debug) System.out.println("third strategy  "+thirdStrategy.getFIO2());
	    
		// Penalties for the 2 next positions
		PatientState secondState = this.prediction.calculatePrediction(this.pState,secondStrategy);
        if (secondState==null)
		{
			secondStrategy.setFIO2(0.35);
		    secondState = this.prediction.calculatePrediction(this.pState,secondStrategy);
		}
		double secondPenalty = this.presentPen.calcPenalties(secondState);
		
		PatientState thirdState = this.prediction.calculatePrediction(this.pState,thirdStrategy);
        if (thirdState==null)
		{
			thirdStrategy.setFIO2(0.5);
		    thirdState = this.prediction.calculatePrediction(this.pState,thirdStrategy);
		}
		double thirdPenalty = this.presentPen.calcPenalties(thirdState);
		
		if (debug) System.out.println("first penalty  "+firstPenalty);
		if (debug) System.out.println("second penalty  "+secondPenalty);
		if (debug) System.out.println("third penalty  "+thirdPenalty);
		
		// Parabola definition fo FIO2 udfoeres paa baggrund af disse punkter
		// med punkterne (x,y), (t,k),(w,z))
							
		 double x =  firstStrategy.getFIO2();
		 double t = 	secondStrategy.getFIO2();
		 double w =	thirdStrategy.getFIO2();
		
		 double y = firstPenalty;
		 double k = secondPenalty;
		 double z = thirdPenalty;
		
		 double a1 = -(-x*z+x*k-t*y-w*k+t*z+w*y)/(t*Math.pow(x,2)+x*Math.pow(w,2)-t*Math.pow(w,2)-w*Math.pow(x,2)+w*Math.pow(t,2)-x*Math.pow(t,2));
		 double b1 = (-Math.pow(x,2)*z+Math.pow(x,2)*k+z*Math.pow(t,2)-Math.pow(w,2)*k+Math.pow(w,2)*y-y*Math.pow(t,2))/(t*Math.pow(x,2)+x*Math.pow(w,2)-t*Math.pow(w,2)-w*Math.pow(x,2)+w*Math.pow(t,2)-x*Math.pow(t,2));
		 double c1 = (-Math.pow(x,2)*w*k+Math.pow(x,2)*t*z-x*z*Math.pow(t,2)+x*Math.pow(w,2)*k-Math.pow(w,2)*t*y+w*y*Math.pow(t,2))/(t*Math.pow(x,2)+x*Math.pow(w,2)-t*Math.pow(w,2)-w*Math.pow(x,2)+w*Math.pow(t,2)-x*Math.pow(t,2)); 
		
		 if (debug) System.out.println("a  "+a1);
		 if (debug) System.out.println("b  "+b1);
		 if (debug) System.out.println("c  "+c1);
		
	     double 	minFIO2 = -b1/(2*a1);
         double presentPenVal = 900;
		
		 if (minFIO2<0.21 || minFIO2>0.99 || b1==0 || minFIO2 == Double.NaN)
		 {   
             //if (debug) System.out.println("minFIO2  a1    "+minFIO2+"   a1 " +a1);
             minFIO2 = fio2WasWrong(minSettings,0.22,0.9); // see function definition
             minSettings.setFIO2(minFIO2);
		     System.out.println("OUR SETTINGS  "+minSettings.getF()+" "+minSettings.getVT()+" "+minSettings.getFIO2());
		                
         }
		
         else
         {
                    if (debug) System.out.println("minimum fraction  "+minFIO2);
                   					
					if (minFIO2<0.29)
					{
						minFIO2 = fio2WasWrong(minSettings,0.22,0.4); // see function definition
					}
					else
					{
						minFIO2 = fio2WasWrong(minSettings,minFIO2-0.08,minFIO2+0.2); // see function definition
					}
					
                    minSettings.setFIO2(minFIO2);
                    
        }
                    // Now we shall use the gradient to get back to the real minimum in case 
                    // we got it wrong.

		 resultState = this.prediction.calculatePrediction(this.pState,minSettings);
		 resultPen = new Penalties(resultState);
		 double setPen = resultPen.getTotal(); 
		 
		 if (setPen<minPen)
		 {
		 	this.pSettings = minSettings;
			minPen = setPen;
		 }
		 else
		 {
		 	this.pSettings = result;
		 }
		 
		
		//this.pSettings = minSettings;		
		this.pState = this.prediction.calculatePrediction(this.pState,this.pSettings);
		this.presentPen = new Penalties(this.pState);
		
		Date slutdate = new Date();
		int startday = startdate.getDay();
		int slutday = slutdate.getDay();
		int startHour = startdate.getHours();
		int startMin = startdate.getMinutes();
		int startsec = startdate.getSeconds();
		int slutHour = slutdate.getHours();
		int slutMin = slutdate.getMinutes();
		int slutsec = slutdate.getSeconds();
		
		System.out.println("startHour  "+startHour);
		System.out.println("stlutHour  "+slutHour);
		System.out.println("startMinute  "+startMin);
		System.out.println("slutMinute  "+slutMin);
		System.out.println("startSeconds  "+startsec); 
                System.out.println("slutSeconds  "+slutsec); 
                
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println("xxxxxx           OPTIMIZATION COMPLETED             xxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                System.out.println("OUR SETTINGS  nr 2"+this.pSettings.getF()+" "+this.pSettings.getVT()+" "+this.pSettings.getFIO2());
		
		
        
            }				
       	if (calculationCancel){this.pSettings = backup; System.out.println("Optimization cancelled");} 
        return this.pSettings;	
    }
	
	
	
    /* The next function "optimizeVT" is used to optimize the minut volume 
	 * for the given frequency, that means finding the VT with the minimum penalty
	 * This method or function is analog to OptimizeSets() for tha parabola definition
	 * and finding the position.
	 */
	public Settings optimizeVT(double F)
	{   
	
		this.pSettings.setF(F);
		Settings settingsPre = new Settings(this.pSettings);
		Settings settingsUp = new Settings(this.pSettings);
		Settings settingsNed = new Settings(this.pSettings);
		Settings settingsExtra = new Settings(this.pSettings);
		Settings settingsLast = new Settings(this.pSettings);
		
		Settings setTest = new Settings(this.pSettings);
		
		this.presentPen = new Penalties(this.pState);
		
		// Optimizing the minute volume from tidal volum
		
		double x,y,s,t,p,q,m,n;
		double a,b,c,d;
		double vt ;
		
		vt = this.pState.getVd()*2;
		
		
		double vtUP = vt +0.1;
		double vtNED = vt - 0.1;
		
		settingsUp.setVT(vtUP);
		settingsNed.setVT(vtNED);
		settingsPre.setVT(vt);
		
		PatientState statePRE = this.prediction.calculatePrediction(this.pState,settingsPre);
        double prePenalty = this.presentPen.calcPenalties(statePRE);
				
		System.out.println(" prePenalty "+ prePenalty);
		
		System.out.println(" vt er "+ settingsPre.getVT() +" "+ settingsNed.getVT()+" "+settingsUp.getVT());
		
		PatientState stateUP = this.prediction.calculatePrediction(this.pState,settingsUp);
		Penalties pen = new Penalties(stateUP);
        double upPenalty = pen.getTotal();
		
		System.out.println(" upPenalty "+ upPenalty);
		
		PatientState stateNED = this.prediction.calculatePrediction(this.pState,settingsNed);
        Penalties pen1 = new Penalties(stateNED);
	    double nedPenalty = pen1.getTotal();
		
		System.out.println(" nedPenalty "+ nedPenalty);
		
		if (statePRE==null || stateNED==null)
		{
			vt = this.pState.getVd()*3;
			vtUP = vt +0.1;
			vtNED = vt - 0.1;
			
			settingsUp.setVT(vtUP);
			settingsNed.setVT(vtNED);
			settingsPre.setVT(vt);
			
			statePRE = this.prediction.calculatePrediction(this.pState,settingsPre);
	        prePenalty = this.presentPen.calcPenalties(statePRE);
					
			System.out.println(" prePenalty 2"+ prePenalty);
			
			System.out.println(" vt er "+ settingsPre.getVT() +" "+ settingsNed.getVT()+" "+settingsUp.getVT());
			
			stateUP = this.prediction.calculatePrediction(this.pState,settingsUp);
			pen = new Penalties(stateUP);
	        upPenalty = pen.getTotal();
			
			System.out.println(" upPenalty 2"+ upPenalty);
			
			stateNED = this.prediction.calculatePrediction(this.pState,settingsNed);
	        pen1 = new Penalties(stateNED);
		    nedPenalty = pen1.getTotal();
			
			System.out.println(" nedPenalty "+ nedPenalty);
		}  
		
		// Comparing different penalties
		
		if ((upPenalty < nedPenalty && upPenalty < prePenalty) || nedPenalty>15 )
		{
			double lastVt;
			double extraVt;
			
			if (vt<=0.7) 
			{
				extraVt = vt + 0.3;
				lastVt = 1.2;
			}
			else
			{
				extraVt = vt+0.2;
				lastVt = vt+0.4;
			}
			
			settingsExtra.setVT(extraVt);
			settingsLast.setVT(lastVt);
			
			PatientState stateEXTRA = this.prediction.calculatePrediction(this.pState,settingsExtra);
        	double extraPenalty = this.presentPen.calcPenalties(stateEXTRA);
			
			System.out.println(" extraPenalty "+ extraPenalty);
		
			PatientState stateLAST = this.prediction.calculatePrediction(this.pState,settingsLast);
        	double lastPenalty = this.presentPen.calcPenalties(stateLAST);
			System.out.println(" lastPenalty "+ lastPenalty);
			
			x = vt;
			y = prePenalty;
			s = vtUP;
			t = upPenalty;
			p = extraVt;
			q = extraPenalty;
			m = lastVt;
			n = lastPenalty; 
			
			System.out.println(" OP "+ x + s + p + m);
					
		}
		
		else if ( (nedPenalty < upPenalty && nedPenalty < prePenalty) || upPenalty>15)
		{
			double lastVt =0;
			double extraVt;
			
			double assumeVt = this.pState.getVd() + 0.05;
			boolean stop = false;
			
			while (!stop)
			{
				setTest.setVT(assumeVt);
										
				PatientState test = this.prediction.calculatePrediction(this.pState,setTest);
								
				if (test!=null)
				{
					lastVt = assumeVt;
					double lastPenalty = this.presentPen.calcPenalties(test);
					stop = true;
				}
				else
				{
					assumeVt = assumeVt + 0.05;
				}
        		
			
			}
			
				
			if (vt <= 0.8) 
			{
				if (this.pSettings.getF()<=20)
				{
					extraVt = 1.2;
				}
				else
				{
					extraVt = lastVt+0.1;
				}
			}
			else
			{
				if (this.pSettings.getF()<=20)
				{
					extraVt = lastVt+0.2;
				}
				else
				{
					extraVt = lastVt+0.1;
				}
				
				
			}
			
			if (extraVt== vtNED) 
			{
				extraVt = vtNED + 0.2;
			}
			
					
			settingsExtra.setVT(extraVt);
			settingsLast.setVT(lastVt);
			
			PatientState stateEXTRA = this.prediction.calculatePrediction(this.pState,settingsExtra);
        	double extraPenalty = this.presentPen.calcPenalties(stateEXTRA);
				System.out.println(" extraPenalty "+ extraPenalty);
		
			PatientState stateLAST = this.prediction.calculatePrediction(this.pState,settingsLast);
        	double lastPenalty = this.presentPen.calcPenalties(stateLAST);
			
			System.out.println(" lastPenalty "+ lastPenalty);
			
			
			x = vt;
			y = prePenalty;
			s = vtNED;
			t = nedPenalty;
			p = extraVt;
			q = extraPenalty;
			m = lastVt;
			n = lastPenalty; 
			System.out.println(" Ned "+ x + s + p + m);
					
		}
		else
		{
		
			double extraVt = vt + 0.3;
			settingsExtra.setVT(extraVt);
			
			PatientState stateEXTRA = this.prediction.calculatePrediction(this.pState,settingsExtra);
        	double extraPenalty = this.presentPen.calcPenalties(stateEXTRA);
			
			System.out.println(" extraPenalty "+ extraPenalty);
			
			
			x = vt;
			y = prePenalty;
			s = vtUP;
			t = upPenalty;
			p = extraVt;
			q = extraPenalty;
			m = vtNED;
			n = nedPenalty; 
			System.out.println(" Midten " + x +" "+ s + " " + p + " " + m);
		}
		
		// Fitting the data to the equation
		
		/**********************/
		
		double xq = Math.pow(x,2);
		double yq = Math.pow(y,2);
		double sq = Math.pow(s,2);
		double tq = Math.pow(t,2);
		double pq = Math.pow(p,2);
		double qq = Math.pow(q,2);
		double mq = Math.pow(m,2);
		double nq = Math.pow(n,2);
		
		
		d = -(xq*p*t*s-mq*x*q*p+sq*m*y*x-xq*s*q*p+mq*p*y*x+xq*s*n*m+sq*x*q*p-m*xq*t*s+mq*x*t*s+pq*m*t*s-sq*p*y*x+sq*p*n*m-mq*s*y*x-sq*x*n*m+pq*s*y*x-pq*s*n*m-sq*m*q*p+m*xq*q*p+mq*s*q*p-mq*p*t*s-pq*m*y*x-pq*x*t*s+pq*x*n*m-xq*p*n*m)/(m*xq*q+xq*s*n-xq*s*q+sq*x*q-sq*p*y+sq*p*n-sq*m*q+sq*m*y-mq*x*q+mq*x*t-m*xq*t+mq*s*q-mq*s*y-sq*x*n+pq*m*t-pq*m*y-pq*x*t+pq*s*y-xq*p*n+xq*p*t-mq*p*t+mq*p*y+pq*x*n-pq*s*n);
		c = (-s*n*m*xq*q+m*t*xq*s*q-xq*s*n*p*t+xq*n*s*q*p-xq*t*m*q*p+xq*m*t*p*n-x*sq*n*q*p+x*n*pq*t*s-x*t*mq*s*q+x*n*sq*m*q-x*t*pq*n*m+x*n*sq*p*y-x*n*pq*s*y+s*y*mq*x*q-m*y*sq*x*q+x*t*mq*q*p+x*t*pq*m*y-x*t*mq*p*y+s*y*pq*n*m-m*t*pq*s*y-m*y*sq*p*n-s*y*mq*q*p+m*y*sq*q*p+t*s*mq*p*y)/(m*xq*q+xq*s*n-xq*s*q+sq*x*q-sq*p*y+sq*p*n-sq*m*q+sq*m*y-mq*x*q+mq*x*t-m*xq*t+mq*s*q-mq*s*y-sq*x*n+pq*m*t-pq*m*y-pq*x*t+pq*s*y-xq*p*n+xq*p*t-mq*p*t+mq*p*y+pq*x*n-pq*s*n);
		b = (sq*y*n*m-x*mq*q*y-xq*m*n*t+mq*t*s*q-sq*n*m*q+xq*m*n*q+t*pq*s*y+t*pq*n*m-n*pq*t*s+xq*t*s*n+y*sq*x*q-xq*t*s*q-x*t*pq*y+xq*t*q*p-y*x*sq*n-sq*y*q*p+sq*n*q*p-xq*n*q*p+n*pq*y*x-n*m*pq*y+x*mq*y*t+mq*y*q*p-t*mq*s*y-t*mq*q*p)/(m*xq*q+xq*s*n-xq*s*q+sq*x*q-sq*p*y+sq*p*n-sq*m*q+sq*m*y-mq*x*q+mq*x*t-m*xq*t+mq*s*q-mq*s*y-sq*x*n+pq*m*t-pq*m*y-pq*x*t+pq*s*y-xq*p*n+xq*p*t-mq*p*t+mq*p*y+pq*x*n-pq*s*n);
		a = (-x*m*n*q+x*m*n*t-x*m*y*t+x*m*q*y-x*t*q*p+x*n*q*p-x*y*p*n+x*y*p*t-x*y*s*q+x*t*s*q-x*t*s*n+x*y*s*n-t*s*m*q+s*y*m*t-m*y*q*p-m*y*s*n-m*t*p*n+m*y*p*n+t*m*q*p+n*m*s*q+s*y*q*p-n*s*q*p+s*n*p*t-s*y*p*t)/(m*xq*q+xq*s*n-xq*s*q+sq*x*q-sq*p*y+sq*p*n-sq*m*q+sq*m*y-mq*x*q+mq*x*t-m*xq*t+mq*s*q-mq*s*y-sq*x*n+pq*m*t-pq*m*y-pq*x*t+pq*s*y-xq*p*n+xq*p*t-mq*p*t+mq*p*y+pq*x*n-pq*s*n);

		
		/*********************/
		
		double r1 = 0.5/a*(-2*a*d+2*Math.sqrt(Math.pow(a,2)*Math.pow(d,2)-a*d*b+a*c));
		double r2 = 0.5/a*(-2*a*d-2*Math.sqrt(Math.pow(a,2)*Math.pow(d,2)-a*d*b+a*c));
		
		System.out.println(" coefficineter "+ a + b + c + d + r1);
		System.out.println(" Roots "+ r1+"  "+r2);
		
		
		if (r1>=3.0 && r1 <1.3)
		{
			this.pSettings.setVT(r1);
		}
		else
		{
			this.pSettings = settingsPre;
		}
		
		if (debug) System.out.println("Min VT 1:    "+r1);
        if (debug) System.out.println("fio2 settings :    "+this.pSettings.getFIO2());
		
		return this.pSettings;
	}
	
		
	public Settings gradientONVT(double step,Settings setToGrade)
	{

        Settings newSet = new Settings(setToGrade);
		
		PatientState statePre = this.prediction.calculatePrediction(this.pState,newSet);
        this.presentPen = new Penalties(statePre);
		double penslow = this.presentPen.getTotal();
		
		        
        double vtSlope = findVTSlope(newSet); // see function definition
        boolean slut = false;
        double stepSize = step;
       
        // verify if we have got the real minimum.
        int i=0;
        while (!calculationCancel && !slut)
            
        {
        	Settings slopeSet = new Settings(newSet);
            slopeSet.setVT(newSet.getVT()-(vtSlope*stepSize/Math.abs(vtSlope)));

           if (debug) System.out.println("VTslopeSet.getVT(): " + slopeSet.getVT());
           if (debug) System.out.println("VTslopeSet.getF(): " + slopeSet.getF());
           if (debug) System.out.println("VTslopeSet.getFIO2(): " + slopeSet.getFIO2());

           PatientState slopePat = this.prediction.calculatePrediction(this.pState,slopeSet);
           //Penalties slopePen = new Penalties(slopePat,this.lambda);
           Penalties slopePen = new Penalties(slopePat);
           double slopePenVal = slopePen.getTotal();
           i++;
           if (debug) System.out.println("slopePenalty: " + slopePenVal);

           if (slopePenVal<=penslow)
		   {    
            	newSet = new Settings(slopeSet);
                penslow = slopePenVal;
                vtSlope = findVTSlope(newSet);
                if (debug) System.out.println("presentPenalty: " + penslow);
                if (debug) System.out.println("slopevt: " + vtSlope);			
            }

            else 
            {				
	            if (debug) System.out.println("Pen is now big");
                stepSize = stepSize/2;
            }

            if (debug) System.out.println("iteration nr: " + i+"step vvvvvvvvvvvvvvv  "+stepSize);
            if (i==10 || newSet.getVT()<0.3 || newSet.getVT()>1.3 || stepSize<0.001) slut =true;			
        }	    	 

        //double minVT = newSet.getVT();	
                
		
		if (debug) System.out.println("Meilleur  vt   "+newSet.getVT()); 
            if (calculationCancel){newSet = setToGrade;}
            return newSet;
	}
	
	
	/* The next function "optimiseSettings()" calculate the values of
	 * gradients for VT, fio2 and F
	 */
 

	/* the next method "findVTSlope()" find the slope in one dimension for a given vt
	 */
	
	
	
	public double findVTSlope(Settings newSet)
	{  		
		double delta = -0.003;
		double dashVT = newSet.getVT()+delta;
		
		Settings dashSet = new Settings(newSet);
		dashSet.setVT(dashVT);
		
		PatientState state = this.prediction.calculatePrediction(this.pState,newSet);
		//double pen = this.presentPen.calcPenalties(state,this.lambda);
                double pen = this.presentPen.calcPenalties(state);
		
		PatientState dashState = this.prediction.calculatePrediction(this.pState,dashSet);
		//double dashpen = this.presentPen.calcPenalties(dashState,this.lambda) - pen;
                double dashpen = this.presentPen.calcPenalties(dashState) - pen;
		if (debug) System.out.println("vtPenalty: " + pen);
		
		double vtGradient = dashpen/delta;
			
		return vtGradient;
	}
	
			
   
    /**
     * Get the present Penalties.
     *
     * @return a <code>Penalties</code> value
     */
	 
	 
    public Penalties getPresentPen()
    {
    	return this.presentPen;
    }
    
    /**
     * Get the present PatientState
     *
     * @return a <code>PatientState</code> value
     */
    public PatientState getPatientState()
    {
    		
		return this.pState;
    }
	
	
	public double fio2WasWrong(Settings mine, double miF, double maF) // handling hvis parabol ikke virker
    {    
                if (debug)
                {
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                    System.out.println("xxxxxx    FIO2 COULD NOT BE FOUND WITH THE PARABOLA   xxxxxxxxxxxxxxxxxxxxxxxx");
                    System.out.println("xxxxxx    A LONG METHOD IS BEING APPLIED NOW        xxxxxxxxxxxxxxxxxxxxxxxx");
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                }	
		 double fio2Start = miF;
		 double fio2Slut = maF;
		 double fio2Step = 0.01;
		 double minPenalty = 999.0;
		 double minFio2 = mine.getFIO2(); 
		 
		 Settings solution = new Settings(mine);
		 int control =0;
		 		 
		 while(fio2Start<=fio2Slut)
		 {   
		 	solution.setFIO2(fio2Start);
				       	 		
			PatientState myPatient = this.prediction.calculatePrediction(this.pState,solution);			       	 												
			//double presentPenVal = this.presentPen.calcPenalties(myPatient,this.lambda);
            double presentPenVal = this.presentPen.calcPenalties(myPatient);
						 
			if (myPatient==null)
			{
				presentPenVal = 20.0;
			}
										 
			if (debug)
			{	System.out.println("Penalty:   " +presentPenVal);	
			    System.out.println("Penalty:   " +presentPenVal);
				System.out.println("mininimim nnn    Penalty:   " +minPenalty);
				System.out.println("Fio2:   " +solution.getFIO2());
				System.out.println("vt:   " +solution.getVT());				
				System.out.println("freq:   " +solution.getF());
			}
	     			                    
			if (presentPenVal < minPenalty)   
			{
				minFio2 = fio2Start;
				minPenalty = presentPenVal;
				control=0;
			}  
			else
			{
				control++;
			}
			if (control>=20) 
			{
				fio2Start = fio2Slut;
				
			}
			fio2Start = fio2Start + fio2Step;			
						 					
		}
		System.out.println("Our FIO2  "+minFio2);				
		return minFio2;
    }
	
	
	/**
	public Settings optimizeFreq(double startF, double slutF)
	{
		double start = startF;
		double slut = slutF;
		double stepF =1;
		Settings minimumSet = this.pSettings; 
		double minFreq = this.pSettings.getF();
		double minTidal = this.pSettings.getVT();
		double[] aray = new double[2];
		PatientState myPatient = this.prediction.calculatePrediction(this.pState,this.pSettings);
              
    	double minPenalty = this.presentPen.calcPenalties(myPatient);
		double penBackup = minPenalty;
		int k=0;
		
		while (start<=slut)
		{
			aray = optimizeVT(start);
			double vt = aray[0];
			double pen = aray[1];
			k++;
			if (pen<minPenalty)
			{
				minTidal = vt;
				minPenalty = pen;
				minFreq = start;
			}
			start = start+stepF;
			if (debug)
		{
			System.out.println("optimizing Frequency  "+k);
		}
			
		}
		
		if (debug)
		{
			System.out.println("optimize minTidal  "+minTidal);
			System.out.println("optimize freq  "+minFreq);
			System.out.println("optimize minpenalty  "+minPenalty);
			System.out.println("optimize pen backup  "+penBackup);
		}
		
		minimumSet.setF(minFreq);
		minimumSet.setVT(minTidal);
		
		return minimumSet;
	}
	**/
        
         public void setCalculationCancel(boolean  v) 
    {
        this.calculationCancel = v;
    }

}//end class



///**
// * @(#)OptimiseSettings.java
// */
//
//package icumatic.client.icuprocess.inventOld.functions;
//
//import icumatic.client.icuprocess.inventOld.containers.PatientState;
//import icumatic.client.icuprocess.inventOld.containers.Penalties;
//import icumatic.client.icuprocess.inventOld.containers.Settings;
//
///**
// * This class contains the algorithm/function for calculating the optimal
// * ventilator settings. the calculation is carried out in the method
// * optimiseSets(). By Nlandu Kamavuako & Mogens Nielsen
// */
//
//public final class OptimiseSettings {
//	private boolean debug = true; //detailed debug information written to std
//								  // out
//
//	private boolean debug_overview = true; //debug summary information written
//
//	private PatientState pState;
//
//	private Settings pSettings;
//
//	private Penalties presentPen;
//
//	private Prediction prediction;
//
//	//public Lambda lambda;
//
//	PatientState forslag;
//
//	double punish;
//
//	boolean stopThread = false;
//
//	Penalties tmpPenalty = new Penalties();
//
//	/**
//	 * Creates a new <code>OptimiseSettings</code> instance.
//	 * 
//	 * @param prediction
//	 *            a <code>Prediction</code> value
//	 * @param pState
//	 *            a <code>PatientState</code> value
//	 * @param pSettings
//	 *            a <code>Settings</code> value
//	 */
//	//public OptimiseSettings(Prediction prediction, PatientState pState,
//	// Settings pSettings,Lambda lambda)
//	//{
//	//   this.prediction = prediction;
//	//    this.pState=pState;
//	//    this.pSettings=pSettings;
//	//    this.lambda = lambda;
//	//}
//	public OptimiseSettings(Prediction prediction, PatientState pState,
//			Settings pSettings) {
//		this.prediction = prediction;
//		this.pState = pState;
//		this.pSettings = pSettings;
//	}
//
//	/**
//	 * The algorithm in this method is based on parabolic assumptions algorithm
//	 * is as follows: (Each calculation of penalties invokes a state prediction
//	 * and takes a considerable amount of time to execute.)
//	 * 
//	 * the methode is as followed: 3 frequencies are choosen and the
//	 * corresponding VT i.e with lowest penalty are calculated with the method
//	 * optimizeVT(); optimizeVT() takes 3 different VT with the samme frequency
//	 * and make a parabola. minimum of this parabola is our pressumed best VT.
//	 * 
//	 * After finding the corresponding VT, we now have 3 points with differents
//	 * frequencies we than make a parabola to determine the best F. this best F
//	 * is than optimized to find the corresponding best VT.
//	 * 
//	 * The combination of this gives the pressumed best minut volume. the best
//	 * minut volum is therefore used to optimize FIO2. We now have a pressumed
//	 * best setting.
//	 * 
//	 * From this point, we begin to search for the real minimum, by going with
//	 * the direction of the gradient of each parameter. until the real minimum
//	 * is found.
//	 * 
//	 * @return a <code>Settings</code> value
//	 */
//
//	public Settings optimiseSets()
//
//	{
//		Settings backup = this.pSettings;
//		this.presentPen = new Penalties();
//		PatientState startState = this.prediction.calculatePrediction(
//				this.pState, this.pSettings);
//
//		double presentPenVal = this.presentPen.calcPenalties(startState);
//
//		double start = this.pSettings.getF();
//		double startminus = start - 5;
//		double startplus = start + 5;
//
//		Settings minSettings = newOptimiseSets(start);
//
//		PatientState secondstate = this.prediction.calculatePrediction(
//				this.pState, minSettings);
//
//		double secondpen = this.presentPen.calcPenalties(secondstate);
//
//		System.out.println("present pen   " + presentPenVal);
//		System.out.println("second pen   " + secondpen);
//
//		if (presentPenVal < secondpen) {
//			this.pSettings = backup;
//
//		} else {
//			if (minSettings.getF() < start) {
//				Settings secondMinSettings = newOptimiseSets(startminus);
//				if (secondMinSettings.getF() < startminus) {
//					this.pSettings = secondMinSettings;
//				} else
//					this.pSettings = newOptimiseSets((minSettings.getF() + secondMinSettings
//							.getF()) / 2);
//			} else if (minSettings.getF() > start) {
//				Settings secondMinSettings = newOptimiseSets(startplus);
//				if (secondMinSettings.getF() > startplus)
//					this.pSettings = secondMinSettings;
//				else
//					this.pSettings = newOptimiseSets((minSettings.getF() + secondMinSettings
//							.getF()) / 2);
//			} else
//				this.pSettings = minSettings;
//
//		}
//
//		boolean slut = false;
//		double[] slopeGradient = optimiseSettings(); // see function definition
//		if (slopeGradient[0] == -10 || slopeGradient[1] == -10
//				|| slopeGradient[2] == -10) {
//			slut = true;
//		}
//
//		if (debug)
//			System.out.println("slopes  " + slopeGradient[0] + "  "
//					+ slopeGradient[1] + "   " + slopeGradient[2]);
//		double step = 0.1;
//
//		if (debug)
//			System.out.println("minmuSet.getVT(): " + this.pSettings.getVT());
//		if (debug)
//			System.out.println("Set.getF(): " + this.pSettings.getF());
//		if (debug)
//			System.out.println("Set.getFIO2(): " + this.pSettings.getFIO2());
//		if (debug)
//			System.out.println("presentPenalty: " + presentPenVal);
//
//		int i = 0;
//		while (!slut) {
//			Settings slopeSet = new Settings(this.pSettings);
//			slopeSet.setVT(this.pSettings.getVT() - (slopeGradient[0] * step));
//			slopeSet.setF(this.pSettings.getF()
//					- (2 * slopeGradient[1] * step * 33));
//			slopeSet.setFIO2(this.pSettings.getFIO2()
//					- (slopeGradient[2] * step));
//
//			if (debug)
//				System.out.println("slopeSet.getVT(): " + slopeSet.getVT());
//			if (debug)
//				System.out.println("slopeSet.getF(): " + slopeSet.getF());
//			if (debug)
//				System.out.println("slopeSet.getFIO2(): " + slopeSet.getFIO2());
//
//			PatientState slopePat = this.prediction.calculatePrediction(
//					this.pState, slopeSet);
//			//Penalties slopePen = new Penalties(slopePat,this.lambda);
//			Penalties slopePen = new Penalties(slopePat);
//			double slopePenVal = slopePen.getTotal();
//
//			if (debug)
//				System.out.println("slopePenalty: " + slopePenVal);
//			i++;
//			if (slopePenVal <= presentPenVal) {
//				this.pSettings = new Settings(slopeSet);
//				PatientState tryState = this.prediction.calculatePrediction(
//						this.pState, this.pSettings);
//				//this.presentPen = new Penalties(tryState,this.lambda);
//				this.presentPen = new Penalties(tryState);
//				if (tryState == null)
//					break;
//				else
//					this.pState = tryState;
//				presentPenVal = presentPen.getTotal();
//
//				if (debug)
//					System.out.println("presentPenalty: " + presentPenVal);
//				//step=step;
//				slopeGradient = optimiseSettings();
//				if (slopeGradient[0] == -10 || slopeGradient[1] == -10
//						|| slopeGradient[2] == -10) {
//					slut = true;
//				}
//
//			} else {
//				step = step / 2;
//
//			}
//
//			if (debug)
//				System.out.println("step    " + step);
//			if (debug)
//				System.out.println("final iteration    " + i);
//			if (step <= 0.003 || i == 30)
//				slut = true;
//			//if (i==40) slut = true;
//
//		}
//
//		return this.pSettings;
//
//	}
//
//	public Settings newOptimiseSets(double fresp) {
//		Settings backup = this.pSettings;
//		System.out
//				.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		System.out
//				.println("xxxxxx           OPTIMIZATION IS RUNNING            xxxxxxxxxxxxxxxxxxxxxxxx");
//		System.out
//				.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//
//		if (this.pSettings.getFIO2() < 0.21) {
//			this.pSettings.setFIO2(0.25);
//		}
//
//		if (this.pSettings.getFIO2() > 0.9) {
//			this.pSettings.setFIO2(0.7);
//		}
//
//		// defining parameters
//		System.out.println("fio2 settings   " + this.pSettings.getFIO2());
//		double preVT = optimizeVT(fresp); // optimize 15 this is done to make
//										  // the process bar appear.
//
//		Settings minSettings = new Settings(pSettings); //object of the best
//														// minut volume
//		Settings newSet1 = new Settings(this.pSettings); //is used to contain
//														 // the next object
//														 // following the
//														 // gradient
//		Settings newSet = new Settings(this.pSettings); // is used to contain
//														// the start object
//		Settings newSet2 = new Settings(this.pSettings); // the point where the
//														 // tangent has zeroes
//		Settings dnewSet = new Settings(this.pSettings); // the derivatives
//
//		double pen1 = 10, pen2 = 10, pen = 10;
//		double preF = fresp; //this is what we find to be a convenient
//							 // frequency to begin with. prefered frequency
//		double delta = 0.005;
//		double dF = preF - delta;
//		double minPenalty = 1000; // just to initialize to something;
//
//		//double presentPenVal = 99.9;
//		this.presentPen = new Penalties();
//		this.prediction = new Prediction();
//
//		double minVT = 0.0;
//		double minF = 0.0;
//		double stepSize = 0.1;
//		double upF, nedF, upVT, nedVT; // next frequency values
//		double x, y, t, k, w, z; //used to find the parabola
//		double a, b, c; //parabola coefficients
//
//		double dVT = preVT;
//
//		// put the new values in the right places.
//		newSet.setVT(preVT);
//		dnewSet.setVT(dVT);
//		newSet.setF(preF);
//		dnewSet.setF(dF);
//
//		//Finding the derivatives and the gradient
//
//		PatientState state = this.prediction.calculatePrediction(this.pState,
//				newSet);
//		//pen = this.presentPen.calcPenalties(state,this.lambda);
//		pen = this.presentPen.calcPenalties(state);
//
//		PatientState dstate = this.prediction.calculatePrediction(this.pState,
//				dnewSet);
//		//double dpen = this.presentPen.calcPenalties(dstate,this.lambda) -
//		// pen;
//		double dpen = this.presentPen.calcPenalties(dstate) - pen;
//
//		double fGrad = dpen / delta;
//		if (debug)
//			System.out.println("fgrad:   " + fGrad);
//
//		double stepF = fGrad / Math.abs(fGrad); // how far we go from the
//												// starting point
//		upF = preF - stepF; // the second point is found
//
//		double intersec = preF - pen / fGrad; // zero of the tangent
//
//		if (intersec <= 6)
//			intersec = 13;
//		if (intersec >= 30)
//			intersec = 17;
//		if (intersec <= preF && intersec >= upF)
//			intersec = upF - 3 * stepF;
//		if (intersec >= preF && intersec <= upF)
//			intersec = upF - 3 * stepF;
//
//		if (debug)
//			System.out.println("stepfreq   intersec:   " + stepF + "   "
//					+ intersec);
//		if (debug)
//			System.out.println("upF   intersec:   " + upF + "   " + intersec);
//
//		nedF = intersec;
//
//		if (nedF == upF)
//			nedF = nedF + 0.1;
//		if (upF == preF)
//			preF = preF + 0.1;
//		if (nedF == preF)
//			nedF = nedF + 0.1;
//
//		upVT = optimizeVT(upF); // optimize for upF
//		nedVT = optimizeVT(nedF); // optimize for nedF
//
//		newSet1.setF(nedF);
//		newSet2.setF(upF);
//		newSet1.setVT(nedVT);
//		newSet2.setVT(upVT);
//
//		// Finding penalties for the 2 found points.
//
//		PatientState state1 = this.prediction.calculatePrediction(this.pState,
//				newSet1);
//		//pen1 = this.presentPen.calcPenalties(state1,this.lambda);
//		pen1 = this.presentPen.calcPenalties(state1);
//
//		PatientState state2 = this.prediction.calculatePrediction(this.pState,
//				newSet2);
//		//pen2 = this.presentPen.calcPenalties(state2,this.lambda);
//		pen2 = this.presentPen.calcPenalties(state2);
//
//		// Find parabola koefficient a,b,c for F
//		x = preF;
//		t = nedF;
//		w = upF;
//
//		y = pen;
//		k = pen1;
//		z = pen2;
//
//		a = -(-x * z + x * k - t * y - w * k + t * z + w * y)
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		b = (-Math.pow(x, 2) * z + Math.pow(x, 2) * k + z * Math.pow(t, 2)
//				- Math.pow(w, 2) * k + Math.pow(w, 2) * y - y * Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		c = (-Math.pow(x, 2) * w * k + Math.pow(x, 2) * t * z - x * z
//				* Math.pow(t, 2) + x * Math.pow(w, 2) * k - Math.pow(w, 2) * t
//				* y + w * y * Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//
//		minF = -b / (2 * a);
//
//		if (minF <= 5 || minF >= 30 || a == 0 || b == 0 || minF == Double.NaN) {
//			minSettings = freqWasWrong();
//		}
//
//		else {
//			minVT = optimizeVT(minF);
//			if (debug)
//				System.out.println("minvtttt:   " + minVT);
//			minSettings.setVT(minVT);
//			minSettings.setF(minF);
//		}
//
//		boolean slut = false;
//
//		PatientState minState = this.prediction.calculatePrediction(
//				this.pState, minSettings);
//		//double tjekPenalty =
//		// this.presentPen.calcPenalties(minState,this.lambda);
//		double tjekPenalty = this.presentPen.calcPenalties(minState);
//		System.out.println("thjekpenalty 111:   " + tjekPenalty);
//		if (minState == null)
//			tjekPenalty = 20;
//
//		if (pen < pen1 && pen < pen2 && pen < tjekPenalty) {
//			if (debug)
//				System.out.println(" NewSet has a better penalty value");
//			if (debug)
//				System.out.println(" pen  " + pen + "  tjekpene  "
//						+ tjekPenalty);
//			if (debug)
//				System.out.println("vt:   " + minSettings.getVT());
//			if (debug)
//				System.out.println("freq:   " + minSettings.getF());
//
//			minSettings = newSet;
//		}
//		if (pen1 < pen && pen1 < pen2 && pen1 < tjekPenalty) {
//			if (debug)
//				System.out.println(" NewSet1 has a better penalty value");
//			if (debug)
//				System.out.println(" pen1  " + pen1 + "tjekpene  "
//						+ tjekPenalty);
//			if (debug)
//				System.out.println("vt:   " + minSettings.getVT());
//			if (debug)
//				System.out.println("freq:   " + minSettings.getF());
//			minSettings = newSet1;
//		}
//		if (pen2 < pen && pen2 < pen1 && pen2 < tjekPenalty) {
//			if (debug)
//				System.out.println(" NewSet2 has a better penalty value");
//			if (debug)
//				System.out.println(" pen2  " + pen2 + "tjekpene  "
//						+ tjekPenalty);
//			if (debug)
//				System.out.println("vt:   " + minSettings.getVT());
//			if (debug)
//				System.out.println("freq:   " + minSettings.getF());
//			minSettings = newSet2;
//		}
//
//		if (debug)
//			System.out.println("Fio2 settings optimalleeellelelelele:   "
//					+ minSettings.getFIO2());
//		if (debug)
//			System.out.println("vt:   " + minSettings.getVT());
//		//if (debug) System.out.println("minvtttt: " +minVT);
//		if (debug)
//			System.out.println("freq:   " + minSettings.getF());
//
//		double minutVolume = (minSettings.getVT() - this.pState.getVd())
//				* minSettings.getF();
//
//		if (debug)
//			System.out.println("minutvolume:  " + minutVolume);
//
//		// following object definition are used to find
//		// the parabola for fiO2.
//
//		Settings firstStrategy = new Settings(minSettings); // start position
//		Settings secondStrategy = new Settings(minSettings); // position 2 on
//															 // the gradient
//		Settings thirdStrategy = new Settings(minSettings); // position 3 at the
//															// zero of the
//															// tangent.
//		Settings dfio2Set = new Settings(minSettings); // derivatives to
//													   // determine the gradient
//
//		// make a parabola out of those 3 points just a guess
//		double fio2 = this.pSettings.getFIO2();
//		firstStrategy.setFIO2(fio2);
//
//		delta = 0.003;
//		double dFio2 = fio2 + delta;
//
//		PatientState firstState = this.prediction.calculatePrediction(
//				this.pState, firstStrategy);
//		if (firstState == null) {
//			firstStrategy.setFIO2(0.25);
//			firstState = this.prediction.calculatePrediction(this.pState,
//					firstStrategy);
//		}
//		//double firstPenalty =
//		// this.presentPen.calcPenalties(firstState,this.lambda);
//		double firstPenalty = this.presentPen.calcPenalties(firstState);
//
//		PatientState dfio2State = this.prediction.calculatePrediction(
//				this.pState, dfio2Set);
//		if (dfio2State == null) {
//			dfio2Set.setFIO2(25 + delta);
//			dfio2State = this.prediction.calculatePrediction(this.pState,
//					dfio2Set);
//
//		}
//		//double dfio2Penalty =
//		// this.presentPen.calcPenalties(dfio2State,this.lambda) - firstPenalty;
//		double dfio2Penalty = this.presentPen.calcPenalties(dfio2State)
//				- firstPenalty;
//
//		double fio2Grad = dfio2Penalty / delta; // fio2 gradient;
//		if (debug)
//			System.out.println("fio2 grad  " + fio2Grad);
//
//		double stepFio2 = 0.05 * fio2Grad / Math.abs(fio2Grad);
//		double fio2Inter = fio2 - firstPenalty / fio2Grad;
//
//		if (debug)
//			System.out.println("fio2 inter  " + fio2Inter);
//		if (fio2Inter <= 0.21)
//			fio2Inter = fio2 - 0.03;
//		if (fio2Inter >= 0.9)
//			fio2Inter = fio2 + 0.1;
//		if (fio2Inter >= fio2 && fio2Inter <= fio2 - stepFio2)
//			fio2Inter = fio2 - stepFio2 + 0.15;
//
//		secondStrategy.setFIO2(fio2 - stepFio2);
//		thirdStrategy.setFIO2(fio2Inter);
//
//		if (debug)
//			System.out.println("first startegy  " + firstStrategy.getFIO2());
//		if (debug)
//			System.out.println("second startegy  " + secondStrategy.getFIO2());
//		if (debug)
//			System.out.println("third startegy  " + thirdStrategy.getFIO2());
//
//		// Penalties for the 2 next positions
//		PatientState secondState = this.prediction.calculatePrediction(
//				this.pState, secondStrategy);
//		if (secondState == null) {
//			secondStrategy.setFIO2(0.35);
//			secondState = this.prediction.calculatePrediction(this.pState,
//					secondStrategy);
//		}
//		//double secondPenalty =
//		// this.presentPen.calcPenalties(secondState,this.lambda);
//		double secondPenalty = this.presentPen.calcPenalties(secondState);
//
//		PatientState thirdState = this.prediction.calculatePrediction(
//				this.pState, thirdStrategy);
//		if (thirdState == null) {
//			thirdStrategy.setFIO2(0.5);
//			thirdState = this.prediction.calculatePrediction(this.pState,
//					thirdStrategy);
//		}
//		//double thirdPenalty =
//		// this.presentPen.calcPenalties(thirdState,this.lambda);
//		double thirdPenalty = this.presentPen.calcPenalties(thirdState);
//
//		if (debug)
//			System.out.println("first penalty  " + firstPenalty);
//		if (debug)
//			System.out.println("second penalty  " + secondPenalty);
//		if (debug)
//			System.out.println("third penalty  " + thirdPenalty);
//
//		// Parabola definition fo FIO2 udf�res p� baggrunfd af disse punkter
//		// med punkterne (x,y), (t,k),(w,z))
//
//		x = firstStrategy.getFIO2();
//		t = secondStrategy.getFIO2();
//		w = thirdStrategy.getFIO2();
//
//		y = firstPenalty;
//		k = secondPenalty;
//		z = thirdPenalty;
//
//		double a1 = -(-x * z + x * k - t * y - w * k + t * z + w * y)
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		double b1 = (-Math.pow(x, 2) * z + Math.pow(x, 2) * k + z
//				* Math.pow(t, 2) - Math.pow(w, 2) * k + Math.pow(w, 2) * y - y
//				* Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		double c1 = (-Math.pow(x, 2) * w * k + Math.pow(x, 2) * t * z - x * z
//				* Math.pow(t, 2) + x * Math.pow(w, 2) * k - Math.pow(w, 2) * t
//				* y + w * y * Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//
//		if (debug)
//			System.out.println("a  " + a1);
//		if (debug)
//			System.out.println("b  " + b1);
//		if (debug)
//			System.out.println("c  " + c1);
//
//		double minFIO2 = -b1 / (2 * a1);
//		double presentPenVal = 900;
//
//		if (minFIO2 < 0.21 || minFIO2 > 0.99 || a1 <= 0 || b1 == 0
//				|| minFIO2 == Double.NaN) {
//			if (debug)
//				System.out.println("minFIO2  a1    " + minFIO2 + "   a1 " + a1);
//			minFIO2 = fio2WasWrong(minSettings.getF(), minSettings.getVT()); // see
//																			 // function
//																			 // definition
//			minSettings.setFIO2(minFIO2);
//			PatientState tryState = this.prediction.calculatePrediction(
//					this.pState, this.pSettings);
//			//this.presentPen = new Penalties(tryState,this.lambda);
//			this.presentPen = new Penalties(tryState);
//			presentPenVal = presentPen.getTotal();
//			if (tryState == null) {
//				System.out.println("something is very very wrong");
//				this.pSettings = backup;
//			} else
//				pState = tryState;
//
//		}
//
//		else {
//			if (debug)
//				System.out
//						.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv  ");
//			if (debug)
//				System.out.println("minimum fraction  " + minFIO2);
//			if (debug)
//				System.out
//						.println("jjdffffdfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfjfjf ");
//
//			minSettings.setFIO2(minFIO2);
//			//lastly:
//			//debug = false;
//			this.pSettings = new Settings(minSettings);
//			PatientState tryState = this.prediction.calculatePrediction(
//					this.pState, this.pSettings);
//			//this.presentPen = new Penalties(tryState,this.lambda);
//			this.presentPen = new Penalties(tryState);
//			presentPenVal = presentPen.getTotal();
//			if (tryState == null) {
//				System.out.println("something is very very wrong");
//				this.pSettings = backup;
//
//			} else
//				pState = tryState;
//		}
//		// Now we shall use the gradient to get back to the real minimum in case
//		// we got it wrong.
//
//		System.out
//				.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		System.out
//				.println("xxxxxx           OPTIMIZATION COMPLETED             xxxxxxxxxxxxxxxxxxxxxxxx");
//		System.out
//				.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		return this.pSettings;
//	}
//
//	/*
//	 * The next function "optimizeVT" is used to optimize the minut volume for
//	 * the given frequency, that means finding the VT with the minimum penalty
//	 * This method or function is analog to OptimizeSets() for tha parabola
//	 * definition and finding the position.
//	 */
//	public double optimizeVT(double F) {
//
//		this.presentPen = new Penalties();
//
//		//3 points to make the parabola of VT
//
//		double midMinVol = 5.0; // With the minut volume of 5, the model finds a
//								// solution
//		double delta = 0.01;
//		double dminVol = midMinVol + delta;
//
//		double dVT = dminVol / F + this.pState.getVd();
//		double vt = midMinVol / F + this.pState.getVd();
//		double x, y, t, k, w, z; //used to find the parabola
//		double a, b, c; //parabola coefficients
//
//		Settings newSet1 = new Settings(this.pSettings); // the same method as
//														 // for the frequency
//		Settings dnewSet = new Settings(this.pSettings);
//		Settings newSet = new Settings(this.pSettings);
//		Settings newSet2 = new Settings(this.pSettings);
//
//		newSet.setVT(vt);
//		dnewSet.setVT(dVT);
//		newSet.setF(F);
//		dnewSet.setF(F);
//
//		// the gradient
//		PatientState state = this.prediction.calculatePrediction(this.pState,
//				newSet);
//		//double pen = this.presentPen.calcPenalties(state,this.lambda);
//		double pen = this.presentPen.calcPenalties(state);
//
//		PatientState dstate = this.prediction.calculatePrediction(this.pState,
//				dnewSet);
//		//double dpen = this.presentPen.calcPenalties(dstate,this.lambda) -
//		// pen;
//		double dpen = this.presentPen.calcPenalties(dstate) - pen;
//
//		if (debug)
//			System.out.println("pen :  " + pen);
//		//if (debug) System.out.println("dpen : "+dpen+"
//		// "+this.presentPen.calcPenalties(dstate,this.lambda));
//		if (debug)
//			System.out.println("dpen :  " + dpen + "   "
//					+ this.presentPen.calcPenalties(dstate));
//		double penGrad = dpen / (delta);
//		if (debug)
//			System.out.println("pen Grad :  " + penGrad);
//
//		double stepMinVol = 0.2 * penGrad / (Math.abs(penGrad)); //stepSize
//		double fVol1 = midMinVol - stepMinVol; // next minut volume
//
//		double intersec = midMinVol - pen / penGrad; // intersection with
//													 // x-axes.
//		if (intersec <= 2.5)
//			intersec = 4;
//		if (intersec >= 10)
//			intersec = 6;
//		if (intersec <= midMinVol && intersec >= fVol1)
//			intersec = fVol1 - 2 * stepMinVol;
//		if (intersec >= midMinVol && intersec <= fVol1)
//			intersec = fVol1 - 2 * stepMinVol;
//		if (debug)
//			System.out.println("intersec    " + intersec);
//
//		double fVol2 = intersec;
//
//		newSet1.setF(F);
//		newSet1.setVT(fVol1 / F + this.pState.getVd());
//		newSet2.setVT(fVol2 / F + this.pState.getVd());
//		newSet2.setF(F);
//
//		if (debug)
//			System.out.println("vt er   " + (fVol1 / F + this.pState.getVd())
//					+ "   " + (fVol2 / F + this.pState.getVd()));
//		if (debug)
//			System.out.println("minVoluneeee    " + fVol1 + "   " + fVol2);
//
//		if (debug)
//			System.out.println("vt er   " + (fVol1 / F + this.pState.getVd())
//					+ "   " + (fVol2 / F + this.pState.getVd()));
//		if (debug)
//			System.out
//					.println("jjdffffdfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfjfjf ");
//
//		// penalties of the 2 next positions
//
//		PatientState state1 = this.prediction.calculatePrediction(this.pState,
//				newSet1);
//		if (debug)
//			System.out
//					.println("jjdffffdfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfjfjf ");
//		//double pen1 = this.presentPen.calcPenalties(state1,this.lambda);
//		double pen1 = this.presentPen.calcPenalties(state1);
//		if (debug)
//			System.out
//					.println("jjdffffdfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfjfjf ");
//
//		PatientState state2 = this.prediction.calculatePrediction(this.pState,
//				newSet2);
//		//double pen2 = this.presentPen.calcPenalties(state2,this.lambda);
//		double pen2 = this.presentPen.calcPenalties(state2);
//		if (debug)
//			System.out
//					.println("jjdffffdfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfjfjf ");
//		// Parabola for VT
//
//		x = midMinVol / F + this.pState.getVd();
//		t = fVol1 / F + this.pState.getVd();
//		w = fVol2 / F + this.pState.getVd();
//
//		if (x == t)
//			t = t + 0.001;
//		if (t == w)
//			w = w + 0.001;
//		if (x == w)
//			x = x + 0.001;
//
//		y = pen;
//		k = pen1;
//		z = pen2;
//
//		// Find parabola koefficient a,b,c
//
//		a = -(-x * z + x * k - t * y - w * k + t * z + w * y)
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		b = (-Math.pow(x, 2) * z + Math.pow(x, 2) * k + z * Math.pow(t, 2)
//				- Math.pow(w, 2) * k + Math.pow(w, 2) * y - y * Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//		c = (-Math.pow(x, 2) * w * k + Math.pow(x, 2) * t * z - x * z
//				* Math.pow(t, 2) + x * Math.pow(w, 2) * k - Math.pow(w, 2) * t
//				* y + w * y * Math.pow(t, 2))
//				/ (t * Math.pow(x, 2) + x * Math.pow(w, 2) - t * Math.pow(w, 2)
//						- w * Math.pow(x, 2) + w * Math.pow(t, 2) - x
//						* Math.pow(t, 2));
//
//		if (debug)
//			System.out.println("a  " + a);
//		if (debug)
//			System.out.println("b  " + b);
//		if (debug)
//			System.out.println("c  " + c);
//
//		double minVT = -b / (2 * a);
//
//		if (minVT < 0.3 || minVT > 1.3 || state == null || state1 == null
//				|| state2 == null || a == 0 || minVT == Double.NaN) {
//			System.out.println("Coefficient a:  " + a);
//			System.out.println("minimum VT:  " + minVT);
//			minVT = vtWasWrong(F);
//			newSet.setVT(minVT);
//			if (debug)
//				System.out.println("Min VT:    " + minVT);
//		} else {
//			if (debug)
//				System.out.println("Min VT 1:    " + minVT);
//			if (debug)
//				System.out.println("fio2 settings :    "
//						+ this.pSettings.getFIO2());
//
//			newSet.setVT(minVT);
//			if (debug)
//				System.out.println("Min VT 2:    " + minVT);
//			double vtSlope = findVTSlope(newSet); // see function definition
//			boolean slut = false;
//			double stepSize = 0.025;
//			if (debug)
//				System.out.println("Min VT 3:    " + minVT);
//			// verify if we have got the real minimum.
//			int i = 0;
//			while (!slut) {
//				Settings slopeSet = new Settings(newSet);
//				slopeSet.setVT(newSet.getVT()
//						- (vtSlope * stepSize / Math.abs(vtSlope)));
//
//				if (debug)
//					System.out.println("VTslopeSet.getVT(): "
//							+ slopeSet.getVT());
//				if (debug)
//					System.out.println("VTslopeSet.getF(): " + slopeSet.getF());
//				if (debug)
//					System.out.println("VTslopeSet.getFIO2(): "
//							+ slopeSet.getFIO2());
//
//				PatientState slopePat = this.prediction.calculatePrediction(
//						this.pState, slopeSet);
//				//Penalties slopePen = new Penalties(slopePat,this.lambda);
//				Penalties slopePen = new Penalties(slopePat);
//				double slopePenVal = slopePen.getTotal();
//				i++;
//				if (debug)
//					System.out.println("slopePenalty: " + slopePenVal);
//
//				if (slopePenVal <= pen) {
//					newSet = new Settings(slopeSet);
//					pen = slopePenVal;
//					vtSlope = findVTSlope(newSet);
//
//					if (debug)
//						System.out.println("presentPenalty: " + pen);
//					if (debug)
//						System.out.println("slopevt: " + vtSlope);
//				}
//
//				else {
//					if (debug)
//						System.out.println("Pen is now big");
//					slut = true;
//				}
//
//				if (debug)
//					System.out.println("iteration nr: " + i);
//				if (i == 10 || newSet.getVT() < 0.325 || newSet.getVT() > 1.275)
//					slut = true;
//			}
//
//			minVT = newSet.getVT();
//		}
//
//		if (debug)
//			System.out.println("best vt   " + minVT); // 	
//		return minVT;
//	}
//
//	/*
//	 * The next function "optimiseSettings()" calculate the values of gradients
//	 * for VT, fio2 and F
//	 */
//
//	public double[] optimiseSettings() {
//
//		double delta = -0.003;//because! 0.003
//		double adjustF = 33; // FIO2_interval = 1.0 - 0.21 = 0.79 and F = 26
//							 // derfor AdjustF = 26/0.79 = 33
//		double stepSize = 0.1;
//		double[] penGradient = new double[3];
//		double[] slopeGradient = new double[3];
//		double lengthGradient;
//
//		Settings newSet;
//		System.out.println("pState  " + this.pState);
//		PatientState tryState = this.prediction.calculatePrediction(
//				this.pState, this.pSettings);
//		//this.presentPen = new Penalties(tryState,this.lambda);
//		this.presentPen = new Penalties(tryState);
//		double presentPenVal = presentPen.getTotal();
//
//		if (tryState == null) {
//			slopeGradient[0] = -10;
//			slopeGradient[1] = -10;
//			slopeGradient[2] = -10;
//			return slopeGradient;
//		} else {
//			this.pState = tryState;
//			Penalties tmpPenalty = new Penalties();
//			double dashVT, dashF, dashFIO2; //For a reason for the names, e.g.
//											// "dash", refer to Steves
//											// documentation
//			double dVTpen, dFpen, dFIO2pen;
//			double dashPenalty;
//			PatientState dashState;
//			Settings dashSet;
//
//			double a, b, c;
//			double minPenalty;
//			double minStep;
//			PatientState minState;
//			Settings minSet;
//
//			double strategicStep;
//			PatientState strategicState;
//			Settings strategicSet = this.pSettings;//just to initialise it to
//												   // something
//			double strategicPenalty;
//
//			int i;
//
//			if (debug_overview) {
//				System.out.println("Optimise start: ");
//				System.out.println("VT: " + this.pSettings.getVT() + " F: "
//						+ this.pSettings.getF() + " FIO2: "
//						+ this.pSettings.getFIO2());
//			}
//
//			i = 0;
//
//			dashVT = (this.pSettings.getVT()) + (delta);
//			dashF = (this.pSettings.getF()) + (delta * adjustF);
//			dashFIO2 = (this.pSettings.getFIO2()) + (delta);
//
//			if (debug) {
//				System.out.println("Optimise iteration start: ");
//				System.out.println("VT: " + this.pSettings.getVT() + " F: "
//						+ this.pSettings.getF() + " FIO2: "
//						+ this.pSettings.getFIO2());
//				System.out.println("dashVT: " + dashVT + " dashF: " + dashF
//						+ " dashFIO2: " + dashFIO2);
//			}
//
//			newSet = new Settings(pSettings); //clone the present Settings
//
//			//Calculate the difference between previous and dash setting:
//			newSet.setVT(dashVT);
//			//dVTpen=tmpPenalty.calcPenalties(prediction.calculatePrediction(pState,newSet),this.lambda)-presentPenVal;
//			dVTpen = tmpPenalty.calcPenalties(prediction.calculatePrediction(
//					pState, newSet))
//					- presentPenVal;
//			newSet = new Settings(pSettings); //back to the present Settings
//
//			newSet.setF(dashF);
//			//dFpen=tmpPenalty.calcPenalties(prediction.calculatePrediction(pState,newSet),this.lambda)-presentPenVal;
//			dFpen = tmpPenalty.calcPenalties(prediction.calculatePrediction(
//					pState, newSet))
//					- presentPenVal;
//			newSet = new Settings(pSettings);
//
//			newSet.setFIO2(dashFIO2);
//			//dFIO2pen=tmpPenalty.calcPenalties(prediction.calculatePrediction(pState,newSet),this.lambda)-presentPenVal;
//			dFIO2pen = tmpPenalty.calcPenalties(prediction.calculatePrediction(
//					pState, newSet))
//					- presentPenVal;
//			newSet = new Settings(pSettings); //reset newSet
//
//			if (debug)
//				System.out.println("presentPenVal: " + presentPenVal
//						+ ", dVTpen: " + dVTpen + ", dFpen: " + dFpen
//						+ ", dFIO2pen: " + dFIO2pen);
//
//			penGradient[0] = dVTpen / (delta);
//			//penGradient[1]=dFpen/(delta*adjustF); // giver alt for lille
//			// step. The direction is the most important
//			penGradient[1] = dFpen / (delta);
//			penGradient[2] = dFIO2pen / (delta);
//
//			if (debug)
//				System.out.println("Pengradient: " + penGradient[0] + ", "
//						+ penGradient[1] + ", " + penGradient[2]);
//
//			lengthGradient = Math.sqrt((Math.pow(penGradient[0], 2))
//					+ (Math.pow(penGradient[1], 2))
//					+ (Math.pow(penGradient[2], 2)));
//			slopeGradient[0] = penGradient[0] / lengthGradient;
//			slopeGradient[1] = penGradient[1] / lengthGradient;
//			slopeGradient[2] = penGradient[2] / lengthGradient;
//
//			return slopeGradient;
//		}
//	}
//
//	/*
//	 * In case the parabola assumptions does not work properly we do it the hard
//	 * way, that means finding the approxymations of minVT
//	 */
//	public double vtWasWrong(double F) {
//
//		if (debug) {
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    VT COULD NOT BE FOUND WITH THE PARABOLA   xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    A LONG METHOD IS BEING APPLIED NOW        xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		}
//		double vtStart = 0.3;
//		double vtSlut = 1.3;
//		double vtStep = 0.05;
//		double minPenalty = 999.0;
//		double minVT = this.pSettings.getVT();
//
//		if (debug)
//			System.out.println("fio2 settings " + this.pSettings.getFIO2());
//
//		Settings solution = new Settings(this.pSettings);
//		solution.setF(F);
//
//		while (vtStart <= vtSlut) {
//			double volume = (vtStart - pState.getVd()) * F;
//			// minut volume under 2 L/min or above 11 L/ min are discard
//
//			if (volume <= 2.0) {
//				vtStart = vtStart + vtStep;
//			} else if (volume >= 11) {
//				vtStart = vtSlut + vtStep;
//			} else {
//
//				solution.setVT(vtStart);
//				PatientState myPatient = this.prediction.calculatePrediction(
//						this.pState, solution);
//				//double presentPenVal =
//				// this.presentPen.calcPenalties(myPatient,this.lambda);
//				double presentPenVal = this.presentPen.calcPenalties(myPatient);
//
//				if (myPatient == null) {
//					presentPenVal = 20.0;
//				}
//
//				if (debug) {
//					System.out.println("Fio2:   " + solution.getFIO2());
//					System.out.println("vt:   " + solution.getVT());
//					System.out.println("freq:   " + solution.getF());
//					System.out.println("Penalty:   " + presentPenVal);
//					System.out.println("mininimim nnn    Penalty:   "
//							+ minPenalty);
//
//				}
//
//				if (presentPenVal < minPenalty) {
//					minVT = vtStart;
//					minPenalty = presentPenVal;
//				} else {
//				}
//				vtStart = vtStart + vtStep;
//
//			}
//
//		}
//
//		return minVT;
//	}
//
//	/*
//	 * the next method "findVTSlope()" find the slope in one dimension for a
//	 * given vt
//	 */
//
//	public double findVTSlope(Settings newSet) {
//		double delta = -0.003;
//		double dashVT = newSet.getVT() + delta;
//
//		Settings dashSet = new Settings(newSet);
//		dashSet.setVT(dashVT);
//
//		PatientState state = this.prediction.calculatePrediction(this.pState,
//				newSet);
//		//double pen = this.presentPen.calcPenalties(state,this.lambda);
//		double pen = this.presentPen.calcPenalties(state);
//
//		PatientState dashState = this.prediction.calculatePrediction(
//				this.pState, dashSet);
//		//double dashpen = this.presentPen.calcPenalties(dashState,this.lambda)
//		// - pen;
//		double dashpen = this.presentPen.calcPenalties(dashState) - pen;
//		if (debug)
//			System.out.println("vtPenalty: " + pen);
//
//		double vtGradient = dashpen / delta;
//
//		return vtGradient;
//	}
//
//	/*
//	 * The next function "fio2WasWrong()" is analog to "vtWasWrong()" and do the
//	 * same job, just for fio2
//	 */
//	public double fio2WasWrong(double minF, double minVT) // handling hvis
//														  // parabol ikke virker
//	{
//		if (debug) {
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    FIO2 COULD NOT BE FOUND WITH THE PARABOLA   xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    A LONG METHOD IS BEING APPLIED NOW        xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		}
//		double fio2Start = 0.21;
//		double fio2Slut = 0.99;
//		double fio2Step = 0.05;
//		double minPenalty = 999.0;
//		double minFio2 = this.pSettings.getFIO2();
//
//		Settings solution = new Settings(this.pSettings);
//		solution.setF(minF);
//		solution.setVT(minVT);
//
//		while (fio2Start <= fio2Slut) {
//			solution.setFIO2(fio2Start);
//
//			PatientState myPatient = this.prediction.calculatePrediction(
//					this.pState, solution);
//			//double presentPenVal =
//			// this.presentPen.calcPenalties(myPatient,this.lambda);
//			double presentPenVal = this.presentPen.calcPenalties(myPatient);
//
//			if (myPatient == null) {
//				presentPenVal = 20.0;
//			}
//
//			if (debug) {
//				System.out.println("Penalty:   " + presentPenVal);
//				System.out.println("Penalty:   " + presentPenVal);
//				System.out.println("mininimim nnn    Penalty:   " + minPenalty);
//				System.out.println("Fio2:   " + solution.getFIO2());
//				System.out.println("vt:   " + solution.getVT());
//				System.out.println("freq:   " + solution.getF());
//			}
//
//			if (presentPenVal < minPenalty) {
//				minFio2 = fio2Start;
//				minPenalty = presentPenVal;
//			} else {
//			}
//			fio2Start = fio2Start + fio2Step;
//
//		}
//
//		return minFio2;
//	}
//
//	public Settings freqWasWrong() {
//		if (debug) {
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    FREQUENCY COULD NOT BE FOUND WITH THE PARABOLA   xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxx    A LONG METHOD IS BEING APPLIED NOW        xxxxxxxxxxxxxxxxxxxxxxxx");
//			System.out
//					.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		}
//		double freqStart = 5;
//		double freqSlut = 30; //25;
//		double vtStart = 0.3;
//		double vtSlut = 1.3;//1.2;
//		double vtStep = 0.1;
//		double freqStep = 1;
//		double freqBegin = 5;
//		double vtBegin = 0.3;
//		double minPenalty = 1000;
//		Settings solution = new Settings(this.pSettings);
//		Settings minSettings = new Settings(this.pSettings);
//
//		//double presentPenVal = 99.9;
//		this.presentPen = new Penalties();
//		//this.prediction= new Prediction();
//
//		double minVT = 0.0;
//		double minF = 0.0;
//
//		while (freqStart <= freqSlut) {
//			while (vtStart <= vtSlut) {
//				double volume = (vtStart - pState.getVd()) * freqStart;
//				if (volume <= 2.0) {
//					vtStart = vtStart + vtStep;
//				} else if (volume >= 11) {
//					vtStart = vtSlut + vtStep;
//				}
//
//				else {
//					solution.setVT(vtStart);
//					solution.setF(freqStart);
//
//					if (debug) {
//						System.out.println("fre  " + solution.getF());
//						System.out.println("fio2  " + solution.getFIO2());
//						System.out.println("vt  " + solution.getVT());
//					}
//
//					PatientState myPatient = this.prediction
//							.calculatePrediction(this.pState, solution);
//					//double presentPenVal =
//					// this.presentPen.calcPenalties(myPatient,this.lambda);
//					double presentPenVal = this.presentPen
//							.calcPenalties(myPatient);
//
//					if (debug) {
//						System.out.println("this pstate  " + this.pState);
//						System.out.println("myPatient  " + myPatient);
//						System.out.println("presentPen  " + presentPenVal);
//						System.out.println("pres objaect  " + this.presentPen);
//					}
//					if (myPatient == null) {
//						presentPenVal = 20.0;
//					}
//
//					if (debug) {
//						System.out.println("Penalty:   " + presentPenVal);
//						System.out.println("mininimim nnn    Penalty:   "
//								+ minPenalty);
//						System.out.println("Fio2:   " + solution.getFIO2());
//						System.out.println("vt:   " + solution.getVT());
//						System.out.println("freq:   " + solution.getF());
//						System.out
//								.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//					}
//					if (presentPenVal < minPenalty) {
//						minVT = vtStart;
//						minF = freqStart;
//						minPenalty = presentPenVal;
//					}
//
//					vtStart = vtStart + vtStep;
//				}
//
//			}
//			vtStart = vtBegin;
//			freqStart = freqStart + freqStep;
//		}
//
//		if (debug) {
//			System.out.println("minVTTTTTTTTTTTTTTTTTTTTTTTT:   " + minVT);
//			System.out.println("minFFFFFFFTTTTTTTTTTTTTTTTTTTTTTTT:   " + minF);
//			System.out.println("mininimim nnn    Penalty:   " + minPenalty);
//		}
//		minSettings = new Settings(solution);
//		minSettings.setVT(minVT);
//		minSettings.setF(minF);
//
//		return minSettings;
//	}
//
//	/**
//	 * Get the present Penalties.
//	 * 
//	 * @return a <code>Penalties</code> value
//	 */
//	public Penalties getPresentPen() {
//		return this.presentPen;
//	}
//
//	/**
//	 * Get the present PatientState
//	 * 
//	 * @return a <code>PatientState</code> value
//	 */
//	public PatientState getPatientState() {
//
//		return this.pState;
//	}
//
//	/**
//	 * Denne metode finder max tal af en array
//	 */
//
//}//end class
