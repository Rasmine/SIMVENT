package SIMVENT.INVENT.bloodpkg;

import VisualNumerics.math.Sfun;

//coop change check comment
/**
 * Patient contains the patient data.
 */
public class blood 
{
    private static final double TOLX=1.0e-16;
    private static final double TOLF=1.0e-16;
    // constructor variables
    private double ph=0; 
    private double pco2=0;
    private double po2=0;
    private double so2=0;
    private double cthb=0;
    private double fmethb=0;
    private double fcohb=0;
    private double cdpg=0;
    private double t=0;
		
	// other possible state variables used in constructor
    private double be=0;
    private double tco2=0;
    private double to2=0;
  
  	// normal values
    private double	nphp;
    private double	nphe;
    private double	nco2p;
    private double	nco2e;
    private double	nhco3p;
	private double	nhco3e;
    private double	nhnbbp;
    private double	nnbbp;
    private double	nhbo2nh2;
    private double	nhbo2nh;
    private double	nhbo2nhcoo;
	private double ntco2=0;
    private double nbb=0;
          

	// only ever calculated
    private double hco3p=0;
    private double co2p=0;
	 
    private double phe=0;
    private double hco3e=0;
    private double co2e=0;	

    private double hnbbp=0;
    private double nbbp=0;
    private double hbo2nh=0;
    private double hbo2nh2=0;
    private double hbo2nhcoo=0;
    private double hbnh=0;
    private double hbnh2=0;
    private double hbnhcoo=0;
    private double bb=0;

	
    private double fe=0;
    private double fp=0;
    private double o2kap=0;
	


   
//    private numerics numeri;

    //------------------ class constuctor
    // varying constructors for different measurements
    public blood(double stvara, double stvarb, double stvarc,double cthb_in, double fmethb_in,double fcohb_in, double cdpg_in,double t_in, char statevar)  
    {
	//	System.out.println("blood created "+statevar);
        switch (statevar)
        {
        case '1':
	//			System.out.println("got in 1");
            ph=stvara; 
            pco2=stvarb;
            po2=stvarc;
            break;
        case '2':
            be=stvara;
            pco2=stvarb;
            po2=stvarc;
				//System.out.println("got in 2");
            break;
			
        case '3':
            be=stvara;
            tco2=stvarb;
            to2=stvarc;
	//			System.out.println("got in 3");
            break;
        }
					
        cthb=cthb_in;
        fmethb=fmethb_in;
        fcohb=fcohb_in;
        cdpg=cdpg_in;
        t=t_in;
	
        fe=cthb/21;
        fp=1-fe;
       
		
		// calculation of normal conditions
		nphp=7.4;
		nphe=7.19;
		nco2p= 0.230*5.33;
		nco2e= (0.195/0.230)*nco2p;
		nhco3p= Math.pow(10,(nphp-6.1))*nco2p;
		nhco3e= Math.pow(10,(nphe-6.1))*nco2e;
		nhnbbp= 23.5353/(Math.pow(10,(nphp-6.9625))+1);
		nnbbp= 23.5353-nhnbbp;
		nhbo2nh2= 127.4499/(1+Math.pow(10,(nphe-7.4930))+(nco2e/1000)*Math.pow(10,(nphe-6.0630)));
		nhbo2nh= nhbo2nh2* Math.pow(10,(nphe-7.4930));
		nhbo2nhcoo= 127.4499 - nhbo2nh2 - nhbo2nh;
        nbb=(nhco3p+nnbbp)*fp+(nhco3e+nhbo2nh+nhbo2nhcoo)*fe;
        ntco2=(nhco3p+nco2p)*fp+(nhco3e+nco2e+nhbo2nhcoo)*fe;

	}
	
	
	//*******************************************
    // Extra functionality used on blood class
	//******************************************
 
    public final void estcrb2b()
	// solution of all blood equations when php pco2 and po2 are constructor variables
	// this is the version that is called when php pco2 and po2 are known and not guessed
	// all variables are therefore not local but refer to the class	
	
    // the only version of the estcrb2b method which can be called publically
	
	{
		
   	    co2p=pco2*0.230;	
		co2e= (0.195/0.230)*co2p;
		hco3p= Math.pow(10,(ph-6.1))*co2p;
		so2= odc( po2,ph,pco2,cthb,fmethb,fcohb,t,cdpg);
		phe=7.19+0.77*(ph-7.4)+0.31*0.1*(1-so2);
		hco3e= Math.pow(10,(phe-6.1))*co2e;
		hnbbp= 23.5353/(Math.pow(10,(ph-6.9625))+1);
		nbbp= 23.5353-hnbbp;
		hbo2nh2= (so2*127.4499)/(1+Math.pow(10,(phe-7.4930))+(co2e/1000)*Math.pow(10,(phe-6.0630)));
		hbo2nh= hbo2nh2* Math.pow(10,(phe-7.4930));
		hbo2nhcoo= (so2*127.4499) - hbo2nh2 - hbo2nh;
		hbnh2= (127.4499-hbo2nh-hbo2nh2-hbo2nhcoo)/(1+Math.pow(10,(phe-7.6944))+((co2e/1000)*Math.pow(10,(phe-5.6024))));		
		hbnh=hbnh2*(Math.pow(10,(phe-7.6944)));
		hbnhcoo=127.4499-hbo2nh-hbo2nh2-hbo2nhcoo-hbnh-hbnh2;
		bb=((hco3p+nbbp)*fp)+((hco3e+hbnh+hbo2nh+hbnhcoo+hbo2nhcoo)*fe);
		be=bb-nbb;
		to2=0.01*po2+(cthb-fmethb*cthb-fcohb*cthb)*so2;
		tco2=(hco3p+co2p)*fp+(hco3e+co2e+hbo2nhcoo+hbnhcoo)*fe;
		
		
		
   		//	System.out.println("be "+be);
		
    }
	
	
	private double [] estcrb2b(double php, double [] function_constants)
		// the following are two versions called privately from the newton solution code i.e.
		// a) php is guessed and pco2 and po2 are known, 
		//i.e. all local variables apart from pco2 and po2, plus haemoglobins etc
		// b) php pco2 and po2 are guessed, i.e. all local variables, plus haemoglobins
		
		// the selection between versions depend on whether the function constants
		// i.e. potentially pco2 and po2 have been set 
		
    {	double [] retvar = new double[2];	
		retvar[0]= 100000000;
		retvar[1]= 100000000;
		if (function_constants[0]== 100000000)
		{
			double co2p= this.pco2*0.230;
			double co2e= (0.195/0.230)* co2p;
			double hco3p= Math.pow(10,(php-6.1))* co2p;
			double so2= odc( this.po2,php,this.pco2,this.cthb,this.fmethb,this.fcohb,this.t,this.cdpg);
			double phe=7.19+0.77*(php-7.4)+0.31*0.1*(1-so2);
			double hco3e= Math.pow(10,(phe-6.1))*co2e;
			double hnbbp= 23.5353/(Math.pow(10,(php-6.9625))+1);
			double nbbp= 23.5353-hnbbp;
			double hbo2nh2= (so2*127.4499)/(1+Math.pow(10,(phe-7.4930))+(co2e/1000)*Math.pow(10,(phe-6.0630)));
			double hbo2nh= hbo2nh2* Math.pow(10,(phe-7.4930));
			double hbo2nhcoo= (so2*127.4499) - hbo2nh2 - hbo2nh;
			double hbnh2= (127.4499-hbo2nh-hbo2nh2-hbo2nhcoo)/(1+Math.pow(10,(phe-7.6944))+((co2e/1000)*Math.pow(10,(phe-5.6024))));		
			double hbnh=hbnh2*(Math.pow(10,(phe-7.6944)));
			double hbnhcoo=127.4499-hbo2nh-hbo2nh2-hbo2nhcoo-hbnh-hbnh2;
			double bb=((hco3p+nbbp)*this.fp)+((hco3e+hbnh+hbo2nh+hbnhcoo+hbo2nhcoo)*this.fe);
			double be=bb-this.nbb;
			double tco2 = ((hco3p+co2p)*this.fp)+((hco3e+co2e+hbnhcoo+hbo2nhcoo)*this.fe);
			retvar[0]= be;
			retvar[1]= tco2; 
			return retvar;
					
		}
		else
		{
			double pco2_guess= function_constants[0];
			double po2_guess= function_constants[1];
			double co2p= pco2_guess*0.230;
			double co2e= (0.195/0.230)* co2p;
			double hco3p= Math.pow(10,(php-6.1))* co2p;
			double so2= odc( po2_guess,php,pco2_guess,this.cthb,this.fmethb,this.fcohb,this.t,this.cdpg);
			double phe=7.19+0.77*(php-7.4)+0.31*0.1*(1-so2);
			double hco3e= Math.pow(10,(phe-6.1))*co2e;
			double hnbbp= 23.5353/(Math.pow(10,(php-6.9625))+1);
			double nbbp= 23.5353-hnbbp;
			double hbo2nh2= (so2*127.4499)/(1+Math.pow(10,(phe-7.4930))+(co2e/1000)*Math.pow(10,(phe-6.0630)));
			double hbo2nh= hbo2nh2* Math.pow(10,(phe-7.4930));
			double hbo2nhcoo= (so2*127.4499) - hbo2nh2 - hbo2nh;
			double hbnh2= (127.4499-hbo2nh-hbo2nh2-hbo2nhcoo)/(1+Math.pow(10,(phe-7.6944))+((co2e/1000)*Math.pow(10,(phe-5.6024))));		
			double hbnh=hbnh2*(Math.pow(10,(phe-7.6944)));
			double hbnhcoo=127.4499-hbo2nh-hbo2nh2-hbo2nhcoo-hbnh-hbnh2;
			double bb=((hco3p+nbbp)*this.fp)+((hco3e+hbnh+hbo2nh+hbnhcoo+hbo2nhcoo)*this.fe);
			double be=bb-this.nbb;
			double tco2 = ((hco3p+co2p)*this.fp)+((hco3e+co2e+hbnhcoo+hbo2nhcoo)*this.fe);
			retvar[0]= be;
			retvar[1]= tco2; 
			return retvar;
		
		}
    }
	
	
//-------------------------------------------------------	
    public final void tio2co2c() 
    // solution of all blood equations when be pco2 and po2 are constructor variables
    // this is the version that is called when be pco2 and po2 are known and not guessed
    // all variables are therefore not local but refer to the class	
    
    // the only version of the tio2co2c method which can be called publically
	
	// uses a newton iterative solution of the private version of estcrb2b	
	// to find the value of ph, and then calls the public estcrb2b to 
	// set all class variables, from ph, pco2, po2

    {
	
      
		double php_high = 9.2;
		double php_low = 5.6; // bounds on ph solution
		double php_guess = 7.4; // initial guess
		char fnction = 'e'; 	// code to call within newton, in this case estcrb2b


		double [] function_constants= new double [1];
		function_constants[0]= 100000000;		// no constants to pass so a dummy value put in
		double lrts = newton (be, php_high, php_low, -0.0000001, 0.0000001,0.0000000001, 0.00001, fnction, 40, function_constants);
		
		ph = lrts;
			
	   	
        if (ph == 100000000)
		{
            tco2 = 100000000;
			so2= 100000000;
		}
		else
		{
			estcrb2b(); 		// once we have ph then call estcrb2b 
		}
    }
		
	
	
	private double [] tio2co2c(double pco2_guess,double po2_guess ) 
	// the private version of tio2co2c to solve from pco2 be and po2
	// where the pco2 and po2 are guessed. All variables are therefore
	// local.
	// returns the value of tco2 required when this function is used iteratively
	// as part of totals. 
	
    {
      	double [] retvar = new double [2]; 
		double php_high = 9.2;
		double php_low = 5.6; // bounds on ph solution
		double php_guess = 7.4; // initial guess
		char fnction = 'e'; 	// code to call within newton, in this case estcrb2b

		double function_constants[]= new double[2];
		function_constants[0]= pco2_guess;
		function_constants[1]= po2_guess;
		double lrts = newton (be, php_high, php_low, -0.0000001, 0.0000001,0.0000000001, 0.00001, fnction, 40, function_constants);
		
		php_guess = lrts;
		
		double [] pred = estcrb2b(php_guess, function_constants);
		double tco2 = pred[1];
		retvar[0]= tco2;
		retvar[1]= php_guess;
		return retvar;
			
		
    }

//-------------------------------------------------------------	
    public final void totals()
    {
		// solution when we know be, tco2, to2
		// iteratively solves the newton functions 3 times in two steps
		
			// 1) solve for O2 using guessed pco2 and ph, using newton and odc
			// 2) solved for ph, pco2 using solved o2, using newton and tio2co2c
					// each tio2co2c then solvs iteratively over a estcrb2b 
			// all the functions used in these sub newton functions are private
			// and variables local to the functions.
			
		// finally once ph, pco2 and ph found the public estcrb2b is called
		
//	System.out.println("in totals function");
		
		double php_guess = 7.4; // initial guess
		double pco2_high = 25;
		double pco2_low = 0.000001; // bounds on ph solution
		double pco2_guess = 5.33; // initial guess
		double pco2_diffstep = 0.0001;
		double po2_high = 150;
		double po2_low = 0.000001; // bounds on ph solution
		double po2_guess = 13; // initial guess
		double po2_diffstep= 0.001;
		char fnction = 't'; 	// code to call within newton, in this case tio2co2c
		
		double pco2_high_error = 0.0000001;
		double pco2_low_error = -0.0000001;
		double po2_high_error = 0.0000001;
		double po2_low_error = -0.0000001;
	
		double [] function_constants= new double [2];
		
		
		for (int cnt=0;cnt<7;cnt++)
		{
			// step 1: solve for O2 using guessed pco2 and ph
	
//	System.out.println("po2 guess "+po2_guess);
//	System.out.println("pco2 guess "+pco2_guess);
//	System.out.println("php guess "+php_guess);
	
//	System.out.println("count in totals "+cnt);
					
			function_constants[0]= php_guess;
			function_constants[1]= pco2_guess;
		
	   		po2_guess = newton (to2, po2_high, po2_low, po2_low_error, po2_high_error,0.0000000001, po2_diffstep, 'o', 40, function_constants);
	
	
			// step 2: solved for ph, pco2 using solved o2		
			
			function_constants[0]= po2_guess;
	   		pco2_guess = newton (tco2, pco2_high, pco2_low, pco2_low_error, pco2_high_error,0.0000000001, pco2_diffstep, 't', 40, function_constants);
	
		
			// now we have po2 and pco2 guessed with a known be we need to get
			// the ph_guess
		
			double [] retvar = tio2co2c( pco2_guess, po2_guess );
				
			php_guess = retvar[1];
			
			// reduce search bounds
			po2_high= po2_guess+50;
			pco2_high = pco2_guess+5;
			

			
		}
		
		// set class variables at solution, and solve for everything
		
//	System.out.println("po2 guess "+po2_guess);
//	System.out.println("pco2 guess "+pco2_guess);
//	System.out.println("php guess "+php_guess);

		po2= po2_guess;
		pco2= pco2_guess;
		ph = php_guess;
		
		estcrb2b();
			
    }

	
	
	 public final double odc(double PO2,double pHp,double PCO2,double Hb,double FMetHb,double FCOHb,double T,double cDPG)
    {
        // odc - Siggaar Andersen oxygena dissociation curve, takes values of PO2,
        // pH and PCO2 and returns the oxygen saturation. Assumes nomal concnetration
        // of DPG, and zero methly and carboxy Hb.

        //-----------------------------------------------------------------------------------
        // Siggaard-Andersen oxygen dissociation curve-------------------------------------
        //-------------------------------------------------------------------------------
		
        double a1=-0.88*(pHp-7.4);				
        double a2=0.048*Math.log(PCO2/5.3);
        double a3=-0.7*FMetHb;				// mean FMetHb
        double a4=(0.3-(0.1*0.005))*((cDPG/5)-1);	// x=cDPG parameter
        double a5=-0.25*0.005;				// mean FHbF
        double a=a1+a2+a3+a4+a5;
        double ko=0.5343;
        double b=0.055*(T-37);				// mean temp
        double yo=Math.log(0.867/(1-0.867));
        double h=3.5+a;
        double xo=a+b;

        double cHb=(1-(FMetHb+FCOHb))*Hb;			

        //calculate SO2 from pO2 at constant pH i.e. Haldane
        double p=PO2+(218*0.00023);			
        double x1=Math.log(p/7);
        double y=yo+(x1-xo)+(h*Sfun.tanh(ko*(x1-xo)));
        double tmp = Math.exp(y);
        double s= tmp/(1+tmp);
        double sO2=(Hb*(s*(1-FMetHb-FCOHb)))/cHb;	// for eryth
	
        return sO2;
    }
	
	
	
	 private double newton(double error_var, double val_1, double val_2, double  low_err_lim, double high_err_lim, double err_lim, double diffstep,char fnction, int max_iter, double [] function_constants)
		
    {
		// Newton-Raphson and Bisection solution  p 366 in numerical methods, single vatiable monotonic function, no local minnima
		// It was necessary to have three versions of this for when the newton function 
		// was called to solve either estcrb2b, odc or tio2co2. This could be eradicated 
		// if a function name could be evaluated from a string as can be done in the MATLAB feval command
		  
		// when functions are called from the newton solution it is to the private versions.
	
		double [] pred= new double[2];	//results of function called within newton
		double f1;		// error for input var_1
		double f2;		// error for input var_2
		double xl;		// low value bound of var
		double xh;		// high value bound of var
		double fl;		// error of low value bound
		double fh;		// error of high value bound
		double f;		// error at guess
		double fs;		// error after the small step to find derivative
		double rts;		// value guessed, when correct, value returned
		double dxold; 	// difference between high and low bounds, last iteration
		double dx;		// difference between high and low bounds, last iteration
		double df;		// derivative
		double j;		// count over iterations
		
		rts=100000000; 	// default as error
		
		
		switch (fnction)
        {
			// first version using estcrb2b as the evaluation function
        
		case 'e':
			
			// var 1 = php_high, var_2= php_low
			pred = estcrb2b (val_1,function_constants);
			f1 = pred[0] - error_var;
			
			pred = estcrb2b (val_2,function_constants);
			f2 = pred[0] - error_var;
			
			// orient the search so that xl gives a negative error
			if (f1 < 0)
			{
				xl=val_1;
				xh=val_2;
				fl=f1;
				fh=f2;
			}
			else
			{
				xl=val_2;
				xh=val_1;
				fl=f2;
				fh=f1;
			}
			
			rts = 0.5*(xl+xh); 	// guess inbetween bounds
			dxold = (val_2-val_1);
			dx=dxold;
			
			// solution at initial guess
			pred = estcrb2b (rts,function_constants);
			f = pred[0] - error_var;
			
			// small step to calculate derivative
			pred = estcrb2b (rts+diffstep,function_constants);
			fs = pred[0] - error_var;
			
			df= (fs-f)/diffstep;
			
			// iterative solution
			
			j=1;
		//	System.out.println("f "+f);
		//	System.out.println("j "+j);
		//	System.out.println("low_err_lim "+low_err_lim);

			while ((j<max_iter) & ((fl< low_err_lim) | (fh > high_err_lim)) & (Math.abs(f) > err_lim))
			{
				if ((((rts-xh)*df-f)*((rts-xl)*df-f)>0) | (Math.abs(2*f) > Math.abs (dxold*df)))
				{
					dxold=dx;
					dx=0.5*(xh-xl);
					rts=xl+dx;
				}
				else
				{
					//use newton result
					dxold=dx;
					dx=f/df;
					rts=rts-dx;
				}
				
				// function evaluation within loop
				pred = estcrb2b (rts,function_constants);
				f = pred[0] - error_var;
			
				// small step to calculate derivative
				pred = estcrb2b (rts+diffstep,function_constants);
				fs = pred[0] - error_var;
			
				df= (fs-f)/diffstep;
				
				// maintain the bounds on the solution
				
				if (f<0)
				{
					xl=rts;
					fl=f;
				}
				else
				{
					xh=rts;
					fh=f;
				}
				j= j+1;
			}
			
		//	System.out.println("j "+j);
		//	System.out.println("max_iter "+max_iter);

			if (j== max_iter)
			{
				rts=100000000;
			}
			
			break;
        
		
		
		
		//----------------------------
		// second case when function used is odc
		case 'o':

			pred[0] = odc (val_1,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);
	
//	System.out.println("so2 upper limit "+pred[0]);	
			f1 = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+val_1*0.01) - error_var;
//	System.out.println("to2 "+error_var);	
//	System.out.println("error in upper limit "+f1);	
	
	
			pred[0] = odc (val_2,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);;
//	System.out.println("so2 lower limit "+pred[0]);	

			f2 = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+val_2*0.01) - error_var;
//	System.out.println("error in lower limit "+f2);		
			// orient the search so that xl gives a negative error
			if (f1 < 0)
			{
				xl=val_1;
				xh=val_2;
				fl=f1;
				fh=f2;
			}
			else
			{
				xl=val_2;
				xh=val_1;
				fl=f2;
				fh=f1;
			}
			
			rts = 0.5*(xl+xh); 	// guess inbetween bounds
//	System.out.println("new po2 guess "+rts);
			
			dxold = (val_2-val_1);
			dx=dxold;
			
			// solution at initial guess
					
			pred[0] = odc (rts,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);
			f = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+rts*0.01) - error_var;
//	System.out.println("error at new po2 guess "+f);
			
			
			// small step to calculate derivative
			
					
			pred[0] = odc (rts+diffstep,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);
			fs = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+(rts+diffstep)*0.01) - error_var;
			
			
			df= (fs-f)/diffstep;
			
			// iterative solution
			
			j=1;
		

			while ((j<max_iter) & ((fl< low_err_lim) | (fh > high_err_lim)) & (Math.abs(f) > err_lim))
			{
				
//				System.out.println("j "+j);
				if ((((rts-xh)*df-f)*((rts-xl)*df-f)>0) | (Math.abs(2*f) > Math.abs (dxold*df)))
				{
					dxold=dx;
//System.out.println("xh "+xh);
//System.out.println("xl "+xl);				
					dx=0.5*(xh-xl);
					rts=xl+dx;
				}
				else
				{
					//use newton result
//System.out.println("newton result used");
					dxold=dx;
					dx=f/df;
					rts=rts-dx;
				}
				
				// function evaluation within loop
		
//	System.out.println("new po2 guess "+rts);
				pred[0] = odc (rts,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);
			
				f = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+rts*0.01) - error_var;
//	System.out.println("error at new po2 guess "+f);
			
				// small step to calculate derivative
		
				pred[0] = odc (rts+diffstep,function_constants[0],function_constants[1],cthb,fmethb,fcohb,t,cdpg);
				fs = ((pred[0]*(cthb-cthb*fmethb-cthb*fcohb))+(rts+diffstep)*0.01) - error_var;
			
		
				df= (fs-f)/diffstep;
				
				// maintain the bounds on the solution
				
				if (f<0)
				{
					xl=rts;
					fl=f;
				}
				else
				{
					xh=rts;
					fh=f;
				}
				j= j+1;
			}
			
//			System.out.println("j "+j);
//			System.out.println("max_iter "+max_iter);

			if (j== max_iter)
			{
				rts=100000000;
			}
	
//			System.out.println("po2 guess newton return"+rts);	
            break;
        
		
		//---------------------------------
		// tio2co2c is the function used
		
		case 't':
//System.out.println("newton t");	

//System.out.println("pco2 high "+val_1);	
			pred = tio2co2c (val_1,function_constants[0]);
//System.out.println("tco2 prediction high "+pred[0]);
//System.out.println("tco2 "+error_var);
			f1 = pred[0] - error_var;
//System.out.println("error high "+f1);	

			
//System.out.println("pco2 low "+val_2);	
			pred = tio2co2c (val_2,function_constants[0]);
			f2 = pred[0] - error_var;
//System.out.println("error low "+f2);	

			
			// orient the search so that xl gives a negative error
			if (f1 < 0)
			{
				xl=val_1;
				xh=val_2;
				fl=f1;
				fh=f2;
			}
			else
			{
				xl=val_2;
				xh=val_1;
				fl=f2;
				fh=f1;
			}
			
			rts = 0.5*(xl+xh); 	// guess inbetween bounds
			dxold = (val_2-val_1);
			dx=dxold;
			

			
			// solution at initial guess
			pred = tio2co2c (rts,function_constants[0]);
			f = pred[0] - error_var;
			
//System.out.println("pco2 guess "+rts);
//System.out.println("error "+f);			
			// small step to calculate derivative
			pred = tio2co2c (rts+diffstep,function_constants[0]);
			fs = pred[0] - error_var;
			
			df= (fs-f)/diffstep;
			
			// iterative solution
			
			j=1;
		//	System.out.println("f "+f);
		//	System.out.println("j "+j);
		//	System.out.println("low_err_lim "+low_err_lim);

			while ((j<max_iter) & ((fl< low_err_lim) | (fh > high_err_lim)) & (Math.abs(f) > err_lim))
			{
				if ((((rts-xh)*df-f)*((rts-xl)*df-f)>0) | (Math.abs(2*f) > Math.abs (dxold*df)))
				{
					dxold=dx;
					dx=0.5*(xh-xl);
					rts=xl+dx;
				}
				else
				{
					//use newton result
					dxold=dx;
					dx=f/df;
					rts=rts-dx;
				}
				
				// function evaluation within loop
				pred = tio2co2c (rts,function_constants[0]);
				f = pred[0] - error_var;

//System.out.println("pco2 guess "+rts);
//System.out.println("error "+f);			

				// small step to calculate derivative
				pred = tio2co2c (rts+diffstep,function_constants[0]);
				fs = pred[0] - error_var;
			
				df= (fs-f)/diffstep;
				
				// maintain the bounds on the solution
				
				if (f<0)
				{
					xl=rts;
					fl=f;
				}
				else
				{
					xh=rts;
					fh=f;
				}
				j= j+1;
			}
			
//			System.out.println("j "+j);
//			System.out.println("max_iter "+max_iter);

			if (j== max_iter)
			{
				rts=100000000;
			}
			
			break;
        
        }
							
		return rts;
    }
			  
	//-----------------------------------------------
	// Get and set statements
					
    /**
   * Gets the value of ph.
   * @return the comments.
   */
    public double getph()
    {
        return ph;
    }		
	
    /* Gets the value of pco2.
   * @return the comments.
   */
    public double getpco2()
    {
        return pco2;
    }	
		
    /* Gets the value of so2.
   * @return the comments.
   */
    public double getso2()
    {
        return so2;
    }	
	
    /* Gets the value of po2.
   */
    public double getpo2()
    {
        return po2;
    }	
    /* Gets the value of be.
   */
    public double getbe()
    {
        return be;
    }
	
   /* Gets the value of cthb.
   */
    public double getcthb()
    {
        return cthb;
    }
    /* Gets the value of fmethb.
   */
    public double getfmethb()
    {
        return fmethb;
    }
    /* Gets the value of fcohb.
   */
    public double getfcohb()
    {
        return fcohb;
    }		
    /* Gets the value of t.
   */
    public double gett()
    {
        return t;
    }
    /* Gets the value of cdpg.
   */
    public double getcdpg()
    {
        return cdpg;
    }
	
    /* Gets the value of co2p.
   */
    public double getco2p()
    {
        return co2p;
    }
    /* Gets the value of tco2.
   */
    public double gettco2()
    {
        return tco2;
    }
		
    /* Gets the value of to2.
   */
    public double getto2()
    {
        return to2;
    }

    public double getntco2()
    {
        return ntco2;
    }
    public double getnbb()
    {
        return nbb;
    }		
    
	public double gethbo2nhcoo()
    {
        return hbo2nhcoo;
    }
    
	public double gethbnhcoo()
    {
        return hbnhcoo;
    }
		
    public void setph(double stph)
    {
        ph=stph;
    }		
	
    public void setpco2(double stpco2)
    {
        pco2=stpco2;
    }	
		
    public void setso2(double stso2)
    {
        so2=stso2;
    }	
	
    public void setpo2(double stpo2)
    {
        po2=stpo2;
    }	

    public void setbe(double stbe)
    {
        be=stbe;
    }
	
    public void setcthb(double stcthb)
    {
        cthb=stcthb;
    }

    public void setfmethb(double stfmethb)
    {
        fmethb=stfmethb;
    }

    public void setfcohb(double stfcohb)
    {
        fcohb=stfcohb;
    }		

    public void sett(double stt)
    {
        t=stt;
    }

    public void setcdpg(double stcdpg)
    {
        cdpg=stcdpg;
    }
	
    public void setco2p(double stco2p)
    {
        co2p=stco2p;
    }

    public void settco2(double sttco2)
    {
        tco2=sttco2;
    }
    public void setto2(double stto2)
    {
        to2=stto2;
    }
	public void sethbo2nhcoo(double sthbo2nhcoo)
    {
        hbo2nhcoo=sthbo2nhcoo;
    }
    public void sethbnhcoo(double sthbnhcoo)
    {
        hbnhcoo=sthbnhcoo;
    }
	

}
