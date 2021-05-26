package SIMVENT.INVENT;

/**
 * @(#)Prediction.java
 *
 */
//package invent;

import SIMVENT.INVENT.bloodpkg.*;
import VisualNumerics.math.Sfun;

/**
 * This class contains the "main" model function, i.e. the function that makes
 * calls down into the blood pkg and o2/co2 models. for a description of the
 * algorithms refer to the comments in the source code - sorry for the
 * inconvenience.
 * 
 * First Author: 02gr1029
 * 
 * @author %$Author: dska02 $
 * @version %$Revision: 1.3 $
 */

public final class Prediction implements IO2Constants {
	private boolean debug = false;
	private boolean calculationCancel = false;
	private int progress;
	private PatientState presentState;
	private PatientState nextSteadyState;
	private Settings presentSettings;
	
	private static final double Patm = 95; //Barometric pressure
	private static final double JUST = 41.56; //Conversion factor to convert ltr/min to mmol/min.
	private static final double FFB = 4.5 / (4.5 + 9.5 + 14); //Assuming Fraction of total fluid/tissue which is blood
	private static final double FFI = 9.5 / (4.5 + 9.5 + 14); //Assuming Fraction of total fluid/tissue which is interstitial fluid
	private static final double FFT = 14 / (4.5 + 9.5 + 14); //Assuming fraction of total fluid/tissue which is tissue

	private static final char STATEVARA = '1'; //Determines what function to use when creating blood object
	private static final double FPAS2 = 0.9; //Fraction of total blood flow, through the lung capillary in the second compartment
	private static final double ERRTOL = 0.01; //0.0001; // tollarance in error of bisected be
	private static final double step_amount = 1; // highest level for varying be
	private static final int gradient = -1; // -1 for negative slope +1 for positive, assumed to be negative

	private static final double PKHCO3 = 6.1; //Dissociation constant bicarbonate
	private static final double SOLUB_PLASMA = 0.230; //Solubility coefficient plasma
	private static final double SOLUB_INTERSTITIAL = 0.2056431657; //Solubility coefficient interstitial fluid
	private static final double PHINTNOR = 7.42; //Normal ph in interstitial fluid
	private static final double PKNBBP = 6.9625; //Dissociation constant non-bicarbonate buffer in plasma
	private static final double TOTAL_NBBI = 7.8451; //Total non-bicarbonate buffer interstitial fluid
	private static final double SOLUB_TISSUES = 0.218; //Solubility coefficient muscle tissue
	private static final double HCO3TISNOR = 8.02196283; //Normal total bicarbonate conc in tissues
	private static final double PHTISNOR = 6.8804943; //Normal ph in tissues
	private static final double BUFFCAPTISS = 4.78; //Buffer capacity in tissues

	//input values
	private double vco2; //CO2 flow
	private double fresp; //Respiration frequency in minutes
	private double vt; //Tidal volume
	private double vd; //Dead space
	private double hb; //Heamoglobin
	private double fmethb; //Fraction of methamoglobin
	private double fcohb; //Fraction of
	private double q; //Cardiac output
	private double fs; //Shunt fraction
	private double phpmvti; //ph mixed venous
	private double pco2mvti; //Partial pressure CO2 mixed venous
	private double temp; //Temperature
	private double fo2i; //Fraction of O2 inspiration
	private double vo2; //O2 flow
	private double fa2; //Fraction of ventilation to second lung compartment
	private double comp; //Compliance
	private double peep; //Positive end expiratory pressure
	private double ie; //Inspiration/expiration time relation
	private double resis; //Resistance
	private double po2mv; //Partial pressure O2 mixed venous
	//    private double hco3tis ; //Bicarbonate (HCO3) in tissue !!ONLY USED IN
	// DYNAMIC MODEL

	private double dpg; //2,3-diphosphoglycerate
	//Local values
	private double be; //Base excess
	private double va; //Alveolar ventilation
	private double fo2e; //Fraction of O2 expiration
	private double betotal; //base excess in combined blood, interstitial fluid and tissue
	private double nbbmvti; //Non-bicarbonate buffer mixed venous + interstitial fluid
	private blood preda; //predicted artery
	private blood predmv; //predicted mixed venous
	private blood initialmv; //Initial mixed venous
	private vqco2model vqco2model; //Ventilation perfusion CO2 model
	private vqo2model vqo2model; //Ventilation perfusion O2 model

	//start - values that is initialised in setInitCO2values()
	private double phv; //ph
	private double pco2v; //CO2
	private double so2v; //Saturation O2
	private double tco2v; //total CO2 venous
	private double tco2c; //total CO2 capilary
	private double po2v; //Partial pressure O2 venous
	private double co2v; //Concentration O2 venous
	private double hco3v; //Bicarbonate venous
	private double co2i; //Interstitial fluid CO2
	private double phi; //Interstitial fluid ph
	private double hnbbi; //Non-bicarbonate buffer acid Interstitial fluid
	private double nbbi; //Non-bicarbonate buffer Interstitial fluid
	private double co2t; //Tissue CO2
	private double pht; //Tissue ph
	//    private double pco2v ; // Tissue pco2 -defined on line 95
	private double pco2t; // Tissue pco2
	private double dnbbt;
	private double hco3t; // Tissue hco3
	private double tco2t;
	private double bet;
	private double be_low; // Lowest possible be
	private double be_high; // Highest possible be
	private double beother; // Second solution
	private double be_bisect;// Middle between be_low and be_high
	private boolean nosol; // Variable describing if a numeriacl solution is still possible
	private boolean foundflg;// if a numerical solution has been found
	// normal interstitial
	private double norhco3i;
	private double norhnbbi;
	private double nornbbi;
	private double norbbi;

	//end

	public Prediction() {
		vqco2model = new vqco2model();
		vqo2model = new vqo2model();
	}

	public PatientState getPredPatient() {
		return this.nextSteadyState;
	}

	public final PatientState calculatePrediction(PatientState pState,
			Settings pSettings) {
		this.presentState = pState;
		this.presentSettings = pSettings;

		this.vco2 = presentState.getVCO2();
		this.fresp = presentSettings.getF();
		this.vt = presentSettings.getVT();
		this.vd = presentState.getVd();
		this.hb = presentState.getHb();
		this.fmethb = presentState.getMetHb();
		this.fcohb = presentState.getCOHb();
		this.q = presentState.getQ();
		this.fs = presentState.getShunt();
		//this.phpmvti = presentState.getPHmv();
		//this.pco2mvti = presentState.getPmvCO2();
		this.temp = presentState.getTemperature();
		this.fo2i = presentSettings.getFIO2();
		this.vo2 = presentState.getVO2();
		this.fa2 = presentState.getFA2();
		this.comp = presentState.getCompliance();
		this.peep = presentSettings.getPEEP();
		this.ie = presentSettings.getIE();
		this.resis = presentState.getResistance();
		//this.po2mv = presentState.getPmvO2();
		this.dpg = presentState.getDPG();

		//------------------------------------------------------------
		// Prediction is performed in three steps
		//	1) The oxygen model is equilibrated with normal acid base
		//	   parameters phv=7.36, pha=7.4, PCO2a=5.33, PCO2v=6.2, phplc=7.41
		//	2) The co2 model is then solved using PO2a, PO2v and PO2alv
		//	   given by the oxygen model.
		//	3) The oxygen model is then re-solved at the correct acid base
		//	   parameters
		// In my previous matlab version I have performed some checks to make
		// sure that this is sufficent
		// to produce equilibrated o2 and co2 systems, it seems to be so

		// O2 model update - initialise at normal acid base
		// set up variables appropriate for all

		va = fresp * (vt - vd);
		double feco2 = vco2 / va; //Fraction of CO2 expiration
//		if (debug)
			//System.out.println(fresp + " " + vt + " " + vd + " " + vco2 + " "
			//		+ vo2 + " " + fo2i);
		fo2e = fo2i - (vo2 / va);

		//	this.phpmvti = presentState.getPHmv();
		//	this.pco2mvti = presentState.getPmvCO2();
		//	this.po2mv = presentState.getPmvO2();
			
			// set up a blood sample which is the initial venous blood
			// for which the php pco2v po2v will be the same in interstitial
			// fluid, assuming a steady state at this initial point.

			// so2mv is guessed this will come out correct later on after blood
			// equib	
		//	initialmv = new blood(phpmvti, pco2mvti, po2mv, hb, fmethb, fcohb, dpg,
		//		temp, STATEVARA);
			// System.out.println("phpmvti "+phpmvti+" pco2mvti "+pco2mvti+" po2mv
			// "+po2mv);
		//	initialmv.estcrb2b();
			//	System.out.println("initial venous be "+initialmv.getbe());
		
		blood artToMv_art = new blood(7.4, 5.33, 12, hb, fmethb, fcohb, dpg,
				temp, '1');
			
		artToMv_art.estcrb2b();
                                  
        double artToMv_tO2mv = artToMv_art.getto2() - (vo2*JUST)/q;
        double artToMv_tCO2mv = artToMv_art.gettco2() + (vco2*JUST)/q;
        initialmv = new blood(artToMv_art.getbe(), artToMv_tCO2mv, artToMv_tO2mv, hb, fmethb, fcohb, dpg,
                temp, '3');
            
        initialmv.totals();
        this.phpmvti = initialmv.getph();
        this.pco2mvti = initialmv.getpco2();
        this.po2mv = initialmv.getpo2();			
		
		//initial VENOUS blood characteuristics
		double bbmvti; //buffer base mixed venous + interstitial fluid

		nbbmvti = initialmv.getnbb();
		bbmvti = initialmv.getbe() + nbbmvti;

		// assuming initial equilibrium and PCO2iti=PCO2mvti

		//initial INTERSTITIAL variables
		double hco3iti, hco3pmvti; //bicarbonate interstitial fluid and mixed
								   // venous/interstitial fluid
		double bbiti; //buffer base initial interstitial fluid
		hco3iti = hco3pmvti = pco2mvti * SOLUB_PLASMA
				* (Math.pow(10, (phpmvti - PKHCO3)));

		double phiti = PKHCO3
				+ Sfun.log10(hco3iti / (pco2mvti * SOLUB_INTERSTITIAL)); //ph,
																		 // initial
																		 // interstitial
																		 // fluid
		double hnbbiti = TOTAL_NBBI / (Math.pow(10, (phiti - PKNBBP)) + 1); //Non-bicarbonate
																			// buffer
																			// acid,
																			// initial
																			// interstitial
																			// fluid
		double nbbiti = TOTAL_NBBI - hnbbiti; // Non-bicarbonate buffer, initial
											  // interstitial fluid
		bbiti = hco3iti + nbbiti;

		// normal interstitial values
		double norhco3i = 6.1 * SOLUB_INTERSTITIAL
				* (Math.pow(10, (PHINTNOR - 6.1)));
		norhco3i = 6.1 * SOLUB_INTERSTITIAL * (Math.pow(10, (PHINTNOR - 6.1)));
		norhnbbi = TOTAL_NBBI / (Math.pow(10, (PHINTNOR - PKNBBP)) + 1);
		nornbbi = TOTAL_NBBI - norhnbbi;
		norbbi = norhco3i + nornbbi;

		double beiti = bbiti - norbbi;

		//	System.out.println("phiti "+phiti+" hco3iti "+hco3iti+"beiti
		// "+beiti);

		double pco2tisi = pco2mvti;
		double co2tisi = pco2tisi * SOLUB_TISSUES;
		double phtisi = PHTISNOR + phiti - PHINTNOR;
		double dnbbtisi = BUFFCAPTISS * (phtisi - PHTISNOR);
		double hco3tisi = Math.pow(10, (phtisi - PKHCO3)) * co2tisi;
		double tco2tisi = co2tisi + hco3tisi;
		double betisi = hco3tisi - HCO3TISNOR + dnbbtisi;

		// sum over whole base excess in blood, int and tissue
		betotal = initialmv.getbe() * FFB + beiti * FFI + betisi * FFT; // this
		
		//	System.out.println("phtisi "+phtisi+"betisi "+betisi+"betotal
		// "+betotal);
		//	System.out.println("phtisi "+phtisi+"betisi "+betisi+"betotal
		// "+betotal);

		// set up predicted arterial and venous blood classes
		preda = new blood(7.4, 5.33, 12, hb, fmethb, fcohb, dpg, temp,
				STATEVARA);
		predmv = new blood(phpmvti, pco2mvti, po2mv, hb, fmethb, fcohb, dpg,
				temp, STATEVARA);

		// first solve the oxygen model at normal acid base charactuerustics
		/*
		 * if ((types == 1) | (types == 2) | (types == 3) | (types == 4)) //this
		 * means rdiff type { System.out.println("rdiff "+rdiff); don't bother
		 * implementing the Rdiff model for now TB: HAR UDKLIPPET MATLAB KODEN
		 * DER STOD UDKOMMENTERET HER - kom fra initrdiff.m }
		 * 
		 * include the parameterest package use the vqo2model class- This class
		 * this class will have to be modified slightly as at the moment it JUST
		 * returns FO2e1, FO2e2 and sO2a, but there is no reason it can return
		 * all the necessary variables.
		 * 
		 * if ((types == 5) | (types == 6) | (types == 7) | (types == 8)) //this
		 * means V/Q type {
		 */
		// SOLUTION OF OXYGEN MODEL
		//1) The oxygen model is equilibrated with normal acid base
		//	 parameters phv=7.36, pha=7.4, PCO2a=5.33, PCO2v=6.2, phplc=7.41
		// leave until Co2 model sorted out
		nextSteadyState = null;
		if (progress > 132) //to stop the progressbar from disapering
			progress = 110;

		if (!calculationCancel && solveModels()) //speedup idea - continue only
												 // if there is a solution
		{
                    	// SOLVE OXYGEN MODEL
			//3) The oxygen model is then re-solved at the correct acid base
			//   parameters

			if (!calculationCancel && solveModels()) {

				double it = 60 / fresp * ie;
				double tc = resis * comp;
				double peip = (vt / (comp * (1 - Math.exp(-it / (tc)))));

				nextSteadyState = presentState;
				nextSteadyState.setFreq(presentSettings.getF());
				nextSteadyState.setVt(presentSettings.getVT());
				nextSteadyState.setFiO2(presentSettings.getFIO2());
				nextSteadyState.setPeep(presentSettings.getPEEP());
				//nextSteadyState.setIE(presentSettings.getIE());

				nextSteadyState.setBaseEx(be_bisect);
				nextSteadyState.setPHmv(predmv.getph());
				nextSteadyState.setPmvCO2(predmv.getpco2());
				nextSteadyState.setPmvO2(predmv.getpo2());
				nextSteadyState.setSmvO2(predmv.getso2());
				nextSteadyState.setFO2et(feco2);
                                nextSteadyState.setFCO2et(fo2e);
				nextSteadyState.setPHa(preda.getph());
				nextSteadyState.setPaCO2(preda.getpco2());
				nextSteadyState.setSaO2(preda.getso2());
				nextSteadyState.setPaO2(preda.getpo2());
				nextSteadyState.setPip(peip + peep);// peip == difference between peep and pip
			}
		}

		//        System.out.println("final be "+be);
		//        System.out.println("best solution "+predmv.getph()+"
		// "+predmv.getso2()+" "+predmv.getpco2()+" "+predmv.getpo2()+"
		// "+preda.getph()+" "+preda.getpco2()+" "+preda.getso2()+"
		// "+preda.getpo2()+" "+feco2+" "+peip);

		return (nextSteadyState);

	}

	private final void setInitCO2Values() {
		// initial guesses for true "t" values
		phv = 1000;
		pco2v = 1000;
		so2v = 1000;
		tco2v = 1000;
		tco2c = 1000;
		po2v = 1000;
		co2v = 1000;
		hco3v = 1000;
		co2i = 1000;
		phi = 1000;
		hnbbi = 1000;
		nbbi = 1000;

		be_low = -10; // lowest possible be
		be_high = 10; // highest possible be
		be_bisect = 1000;
		nosol = false; //variable describing if a numeriacl solution is still
					   // possible
		foundflg = false; // if a numerical solution has been found
		// for be solution, negative obtained from trial and error
		// it might be a good idea to meake the bisection function
		// more general
	}

	private final boolean solveModels() {
		double so2a; //result from o2 model
		double fo2e1; //result from o2 model
		double fo2e2; //result from o2 model
		double to2v; //result from o2 model
		double to2a; //result from o2 model

		double betotalsmatch; //Predicted concentation of base excess combined
							  // blood, interstitial fluid and tissues
		double err;
		double errl;
		double errh;

		double beminpos; // the minimum value of be which gives a positive
						 // solution is the current be
		double bemaxNaN; // the maximum value of be which gives NaN solution is
						 // be_low
		double bemaxneg; // maximum value of be which gives a negative solution
		double beminNaN; // minumum value of be which gives NaN solution
		boolean foundLow; // flag which is set to true when the Low bisection
						  // point is found
		boolean foundHigh; // flag which is set to true when the High bisection
						   // point is found
		double stepsize; // stepsize recorded when this solution found
		double tco2a; //total CO2 artery

		double[] reso2 = new double[5]; //result O2 model
		double[] resco2 = new double[11]; //result CO2 model

		reso2 = vqo2model.cpsavq(Patm, vo2, fo2e, q, dpg, 5.33, fmethb, fcohb,
				hb, 7.4, temp, fs, fa2, FPAS2, va);
		so2a = reso2[0];
		fo2e1 = reso2[1];
		fo2e2 = reso2[2];
		to2v = reso2[3];
		to2a = reso2[4];

		progress++;
		//System.out.println("output from first iteration of o2 model");
		if (so2a == 10000)//DEFAULT_SO2 ) // dvs. if solvemodel succedes
			return (false);

		// CO2 MODEL SOLUTION
		//	 2) The co2 model is then solved using PO2a, PO2v and PO2alv
		//          given by the oxygen model.
		//          and the initial venous blood charactgeuristics

		// Solution of CO2 model is performed by guessing a BE value
		// the problem can then be expressed as a bisection problem

		// initial guesses for true "t" values
		setInitCO2Values();

		// Algorithm is as follows
		// The variable used in the bisection (be) is set to the lowest possible
		// value
		// If is then varied, adding a step amount until a numeric solution to
		// the
		// bisection equations is found.
		// This numeric solution can be either positive or negative or no
		// solution

		//		If positive, we have an upper boundary to the solution be_h
		//		Then we know the solution lies at a be which is a step against the
		// gradient
		//		i.e. -ve gradient + be step
		//		A step in be (stepsize is taken) against the gradient is then taken
		// and a bisection error calculated
		//			If NaN: then we have stepped too far, the stepsize is reduced
		//			If positive: then we have not stepped far enough, the stepsize is
		// increased
		//			If negative we have an lower boundary to the solution be_l

		//		If negative we have an lower boundary to the solution be_l
		//		Then we know the solution lies at a be a step with the gradient away
		// this
		//		A step in be (stepsize is taken) with the gradient is taken and a
		// bisection error calculated
		//			If NaN: then we have stepped too far, the stepsize is reduced
		//			If negative: then we have not stepped far enough, the stepsize is
		// increased
		//			If positive we have an upper boundary to the solution be_h

		//	After be_l and be_h are found bisection can proceed
		//	Bivar_b is found as the midpoint between be_h and be_l, and a new err
		// (errb) calc
		//		If this error is positive replace be_h with be_b
		//		If this error is negative replace be_l with be_b
		//	Calculate the difference between the error at be_l and be_h
		// 	when this is inside the tollerance then stop.

		// sometimes there are discontinuities in the error space in this
		// bisection
		// which cause it to fail if the initial limits are set to broad.
		// some extra work is required to recognise this and modify the limits
		// accordingly

		be = be_low; // The variable used in the bisection (be) is set to the
					 // lowest possible value

		while (!calculationCancel && !foundflg && !nosol) {
			be += step_amount; //If is then varied, adding a step amount
			if (be > be_high) {
				nosol = true; // if we cover the complete range bestep=1 then
							  // there is no solution
				break;
			}

			// reset the blood input BE to that in the loop

			initialmv.setbe(be);

			//	System.out.println("q "+q+" fo2e1 "+fo2e1+" fo2e2 "+fo2e2+" fa2
			// "+fa2+" fs "+fs+" FPAS2 "+FPAS2+" JUST"+JUST+" va "+va+" vo2
			// "+vo2+" Patm "+Patm+" to2a "+to2a+" vco2 "+vco2);

			// solve the Co2 model
			resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2, fa2, fs,
					FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a, vco2);

			// outputs from solution
			phv = resco2[0];
			pco2v = resco2[1];
			so2v = resco2[2];
			tco2v = resco2[8];
			tco2c = resco2[9];
			po2v = resco2[10];

			// solve for interstitial fluid
			co2v = pco2v * SOLUB_PLASMA;
			hco3v = co2v * Math.pow(10, (phv - PKHCO3));
			co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
			phi = PKHCO3 + Sfun.log10(hco3v / co2i);
			hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
			nbbi = TOTAL_NBBI - hnbbi;

			/*
			 * // normal interstitial values double norhco3i=
			 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1)); double
			 * norhnbbi = TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
			 * nornbbi= TOTAL_NBBI-norhnbbi; double norbbi=norhco3i+nornbbi; not
			 * sure if this needs re-calculation
			 */

			pco2t = pco2v;
			co2t = pco2t * SOLUB_TISSUES;
			pht = PHTISNOR + phi - PHINTNOR;
			dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
			hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
			tco2t = co2t + hco3t;
			bet = hco3t - HCO3TISNOR + dnbbt;

			betotalsmatch = be * FFB + (hco3v + nbbi - norbbi) * FFI + bet
					* FFT;

			// initial conditions BBfti should be same as predicted otherwise
			// solution not correct
			// try another base excess to give zero bisection
			err = betotal - betotalsmatch;

			//		System.out.println("bevguess "+be+" betotalsmatch
			// "+betotalsmatch+" err "+err);
			//		System.out.println("phv "+phv);

			if (debug)
				System.out.println("1. bisection error difference between bbf "
						+ err);
			if (debug)
				System.out.println("phv " + phv);

			if (!Double.isNaN(err) && err > 0 && phv != 1000) // if no solution
															  // phv is set to
															  // 1000
			{
				if (debug)
					System.out.println("numeric solution high found " + be);
				foundflg = true; // a numeric solution is now possible without
								 // stepping further
				be_high = be; // the high bisection be level is set

				beminpos = be; // the minimum value of be which gives a positive
							   // solution is the current be
				bemaxNaN = be_low; // the maximum value of be which gives NaN
								   // solution is be_low
				foundLow = false; // flag which is set to true when the low
								  // bisection point is found
				stepsize = step_amount; // stepsize recorded when this solution
										// found i.e. the solution lies <
										// stepsize lower than this

				while (!calculationCancel && !foundLow && !nosol) {
					beother = be_high - (stepsize * gradient); // start point
															   // for finding
															   // the low
															   // solution

					// reset the blood input BE to that in the loop
					initialmv.setbe(beother);

					// solve the Co2 model
					if (debug)
						System.out.println("1. solution co2 model be "
								+ beother);
					resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2,
							fa2, fs, FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a,
							vco2);

					// outputs from solution
					phv = resco2[0];
					pco2v = resco2[1];
					so2v = resco2[2];
					tco2v = resco2[8];
					tco2c = resco2[9];
					po2v = resco2[10];

					// solve for intertistitial fluid
					co2v = pco2v * SOLUB_PLASMA;
					hco3v = co2v * Math.pow(10, (phv - PKHCO3));
					co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
					phi = PKHCO3 + Sfun.log10(hco3v / co2i);
					hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
					nbbi = TOTAL_NBBI - hnbbi;

					/*
					 * // normal interstitial values double norhco3i=
					 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1));
					 * double norhnbbi =
					 * TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
					 * nornbbi= TOTAL_NBBI-norhnbbi; double
					 * norbbi=norhco3i+nornbbi; not sure if this needs
					 * re-calculation
					 */

					pco2t = pco2v;
					co2t = pco2t * SOLUB_TISSUES;
					pht = PHTISNOR + phi - PHINTNOR;
					dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
					hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
					tco2t = co2t + hco3t;
					bet = hco3t - HCO3TISNOR + dnbbt;

					betotalsmatch = beother * FFB + (hco3v + nbbi - norbbi)
							* FFI + bet * FFT;

					err = betotal - betotalsmatch;

					//	System.out.println("bevguess "+beother+" betotalsmatch
					// "+betotalsmatch+" err "+err);
					//	System.out.println("phv "+phv);
					if (debug)
						System.out
								.println("2. bisection error difference between bbf "
										+ err);

					if (Double.isNaN(err) || phv == 1000
							|| err == Double.POSITIVE_INFINITY) {
						stepsize = stepsize / 2;
						if (beother > bemaxNaN) {
							bemaxNaN = beother; // the maximum be which gives
												// solution NaN is now
						}
					} else if (!Double.isNaN(err) && phv != 1000 && err < 0) {
						foundLow = true;
						be_low = beother; // low bisection point found
						if (debug)
							System.out.println("numeric solution low found "
									+ be_low);
					} else {
						stepsize = stepsize * 1.25;
						if (beother < beminpos) {
							beminpos = beother; // the minimum value of be which
												// gives a positive solution is
												// reset
						}
					}
					if (beminpos - bemaxNaN <= 0.0001) {
						// this is a check if the minimum positive solution
						// reaches the max
						//  NaN solution then no negative solution exists and
						// nosol is set to true
						nosol = true;
					}
				}// end - while (!foundLow && !nosol)
			}// end - if ( !Double.isNaN(err) && err>0 && phv!=1000)

			else if (!Double.isNaN(err) && err <= 0 && phv != 1000) {
				if (debug)
					System.out.println("numeric solution low found " + be);
				be_low = be; // low bisection point found and set to be
				bemaxneg = be; // maximum value of be which gives a negative
							   // solution
				beminNaN = be_high; // minumum value of be which gives NaN
									// solution

				foundHigh = false; // flag which is set to true when the high
								   // bisection point is found
				stepsize = step_amount;
				while (!calculationCancel && !foundHigh && !nosol) {
					beother = be_low + (stepsize * gradient);

					// reset the blood input BE to that in the loop
					initialmv.setbe(beother);

					// solve the Co2 model
					if (debug)
						System.out.println("2. solution co2 model be "
								+ beother);
					resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2,
							fa2, fs, FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a,
							vco2);

					// outputs from solution
					phv = resco2[0];
					pco2v = resco2[1];
					so2v = resco2[2];
					tco2v = resco2[8];
					tco2c = resco2[9];
					po2v = resco2[10];

					// solve for intistitial fluid
					co2v = pco2v * SOLUB_PLASMA;
					hco3v = co2v * Math.pow(10, (phv - PKHCO3));
					co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
					phi = PKHCO3 + Sfun.log10(hco3v / co2i);
					hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
					nbbi = TOTAL_NBBI - hnbbi;

					/*
					 * // normal interstitial values double norhco3i=
					 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1));
					 * double norhnbbi =
					 * TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
					 * nornbbi= TOTAL_NBBI-norhnbbi; double
					 * norbbi=norhco3i+nornbbi; not sure if this needs
					 * re-calculation
					 */

					pco2t = pco2v;
					co2t = pco2t * SOLUB_TISSUES;
					pht = PHTISNOR + phi - PHINTNOR;
					dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
					hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
					tco2t = co2t + hco3t;
					bet = hco3t - HCO3TISNOR + dnbbt;

					betotalsmatch = beother * FFB + (hco3v + nbbi - norbbi)
							* FFI + bet * FFT;

					err = betotal - betotalsmatch;

					//System.out.println("bevguess "+beother+" betotalsmatch
					// "+betotalsmatch+" err "+err);
					//  System.out.println("phv "+phv);

					if (debug)
						System.out
								.println("3. bisection error difference between bbf "
										+ err);

					if (Double.isNaN(err) || phv == 1000
							|| err == Double.NEGATIVE_INFINITY) {
						stepsize = stepsize / 2;
						if (beother < beminNaN) {
							beminNaN = beother; // minimum be NaN reset
						}
					} else if (!Double.isNaN(err) && err > 0 && phv != 1000) {
						foundHigh = true; // high solution found
						be_high = beother;
						if (debug)
							System.out.println("numeric solution high found "
									+ be_high);
					} else {
						stepsize = stepsize * 1.25; //tbsr
						if (beother > bemaxneg) {
							bemaxneg = beother; // max be negative reset
						}
					}
					if (beminNaN - bemaxneg <= 0.0001) {
						// if max neg be and min NaN be are the
						// same then there is no +ve solution
						nosol = true;
					}
				}//end - while (!foundHigh && !nosol)
			}//end - else if ( !Double.isNaN(err) && err<=0 && phv!=1000)
		}//end - while (!foundflg && !nosol)

		progress++;

		if (!calculationCancel && !nosol) // if a solution found then start the
										  // bisection
		{
			if (debug)
				System.out.println("bisection started between be= " + be_low
						+ " and " + be_high);

			// reset the blood input BE to that in the loop
			initialmv.setbe(be_low);

			// solve the Co2 model
			if (debug)
				System.out.println("3. solution co2 model be " + be_low);
			//	System.out.println("vco2solv usedfoo");
			resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2, fa2, fs,
					FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a, vco2);

			progress++;
			// outputs from solution
			phv = resco2[0];
			pco2v = resco2[1];
			so2v = resco2[2];
			tco2v = resco2[8];
			tco2c = resco2[9];
			po2v = resco2[10];

			// solve for intertistitial fluid
			co2v = pco2v * SOLUB_PLASMA;
			hco3v = co2v * Math.pow(10, (phv - PKHCO3));
			co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
			phi = PKHCO3 + Sfun.log10(hco3v / co2i);
			hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
			nbbi = TOTAL_NBBI - hnbbi;

			/*
			 * // normal interstitial values double norhco3i=
			 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1)); double
			 * norhnbbi = TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
			 * nornbbi= TOTAL_NBBI-norhnbbi; double norbbi=norhco3i+nornbbi; not
			 * sure if this needs re-calculation
			 */

			pco2t = pco2v;
			co2t = pco2t * SOLUB_TISSUES;
			pht = PHTISNOR + phi - PHINTNOR;
			dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
			hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
			tco2t = co2t + hco3t;
			bet = hco3t - HCO3TISNOR + dnbbt;

			betotalsmatch = be_low * FFB + (hco3v + nbbi - norbbi) * FFI + bet
					* FFT;

			errl = betotal - betotalsmatch;

			//		System.out.println("bevguess_low "+be_low+" betotalsmatch
			// "+betotalsmatch+" errl "+errl);
			//System.out.println("phv "+phv);

			if (debug)
				System.out.println("4. bisection error difference between bbf "
						+ errl);

			// reset the blood input BE to that in the loop
			initialmv.setbe(be_high);

			// solve the Co2 model
			if (debug)
				System.out.println("4. solution co2 model be " + be_high);
			resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2, fa2, fs,
					FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a, vco2);

			// outputs from solution
			phv = resco2[0];
			pco2v = resco2[1];
			so2v = resco2[2];
			tco2v = resco2[8];
			tco2c = resco2[9];
			po2v = resco2[10];

			// solve for intistitial fluid
			co2v = pco2v * SOLUB_PLASMA;
			hco3v = co2v * Math.pow(10, (phv - PKHCO3));
			co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
			phi = PKHCO3 + Sfun.log10(hco3v / co2i);
			hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
			nbbi = TOTAL_NBBI - hnbbi;

			/*
			 * // normal interstitial values double norhco3i=
			 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1)); double
			 * norhnbbi = TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
			 * nornbbi= TOTAL_NBBI-norhnbbi; double norbbi=norhco3i+nornbbi; not
			 * sure if this needs re-calculation
			 */

			pco2t = pco2v;
			co2t = pco2t * SOLUB_TISSUES;
			pht = PHTISNOR + phi - PHINTNOR;
			dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
			hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
			tco2t = co2t + hco3t;
			bet = hco3t - HCO3TISNOR + dnbbt;

			betotalsmatch = be_high * FFB + (hco3v + nbbi - norbbi) * FFI + bet
					* FFT;

			errh = betotal - betotalsmatch;
			//		System.out.println("bevguessh "+be_high+" betotalsmatch
			// "+betotalsmatch+" errh "+errh);
			//System.out.println("phv "+phv);
			if (debug)
				System.out.println("4. bisection error difference between bbf "
						+ errh);

			double chk = 1000;

			// once upper and lower limits found
			// then bisect to find the correct soltion

			be_bisect = ((be_high - be_low) / 2) + be_low; //middle of high and
														   // low
			if (debug)
				System.out.println("be_bisect " + be_bisect);

			double errb = 1000;
			while (!calculationCancel && Math.abs(chk) > ERRTOL) {
				initialmv.setbe(be_bisect);

				// solve the Co2 model
				if (debug)
					System.out.println("5. solution co2 model be " + be_bisect);
				resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2, fa2,
						fs, FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a, vco2);

				// outputs from solution
				phv = resco2[0];
				pco2v = resco2[1];
				so2v = resco2[2];
				tco2v = resco2[8];
				tco2c = resco2[9];
				po2v = resco2[10];

				// solve for intistitial fluid
				co2v = pco2v * SOLUB_PLASMA;
				hco3v = co2v * Math.pow(10, (phv - PKHCO3));
				co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
				phi = PKHCO3 + Sfun.log10(hco3v / co2i);
				hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
				nbbi = TOTAL_NBBI - hnbbi;

				/*
				 * // normal interstitial values double norhco3i=
				 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1)); double
				 * norhnbbi = TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1);
				 * double nornbbi= TOTAL_NBBI-norhnbbi; double
				 * norbbi=norhco3i+nornbbi; not sure if this needs
				 * re-calculation
				 */

				pco2t = pco2v;
				co2t = pco2t * SOLUB_TISSUES;
				pht = PHTISNOR + phi - PHINTNOR;
				dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
				hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
				tco2t = co2t + hco3t;
				bet = hco3t - HCO3TISNOR + dnbbt;

				betotalsmatch = be_bisect * FFB + (hco3v + nbbi - norbbi) * FFI
						+ bet * FFT;

				errb = betotal - betotalsmatch;
				//		System.out.println("bevguessb "+be_bisect+" betotalsmatch
				// "+betotalsmatch+" errb "+errb);
				//System.out.println("phv "+phv);

				if (debug)
					System.out.println("errbisect " + errb);

				if (errb < 0) {
					if (errh < 0) {
						be_high = be_bisect;
						errh = errb;
					} else if (errl < 0) {
						be_low = be_bisect;
						errl = errb;
					}
				} else if (errb > 0) {
					if (errh > 0) {
						be_high = be_bisect;
						errh = errb;
					} else if (errl > 0) {
						be_low = be_bisect;
						errl = errb;
					}
				}
				chk = errh - errl;
				//((x < 0.0) ? -x : x;) == Math.abs()
				if (Double.isNaN(errb)
						|| Math.abs(errb) == Double.POSITIVE_INFINITY) {
					be_bisect = be_bisect + 0.0001;
				} else {
					be_bisect = ((be_high - be_low) / 2) + be_low;
				}
			}//end - while (Math.abs(chk) > ERRTOL)

			initialmv.setbe(be_bisect);

			// solve the Co2 model
			if (debug)
				System.out.println("final solution ");
			if (debug)
				System.out.println("6. solution co2 model be " + be_bisect);
			resco2 = vqco2model.vco2solv(initialmv, q, fo2e1, fo2e2, fa2, fs,
					FPAS2, JUST, va, vo2, 1000, Patm, 0, to2a, vco2);

			// outputs from solution
			phv = resco2[0];
			pco2v = resco2[1];
			so2v = resco2[2];
			tco2v = resco2[8];
			tco2c = resco2[9];
			po2v = resco2[10];

			// solve for intistitial fluid
			co2v = pco2v * SOLUB_PLASMA;
			hco3v = co2v * Math.pow(10, (phv - PKHCO3));
			co2i = co2v * (SOLUB_INTERSTITIAL / SOLUB_PLASMA);
			phi = PKHCO3 + Sfun.log10(hco3v / co2i);
			hnbbi = TOTAL_NBBI / ((Math.pow(10, (phi - PKNBBP))) + 1);
			nbbi = TOTAL_NBBI - hnbbi;

			/*
			 * // normal interstitial values double norhco3i=
			 * 6.1*SOLUB_INTERSTITIAL*(Math.pow(10,(PHINTNOR-6.1)); double
			 * norhnbbi = TOTAL_NBBI/(Math.pow(10,(PHINTNOR-PKNBBP))+1); double
			 * nornbbi= TOTAL_NBBI-norhnbbi; double norbbi=norhco3i+nornbbi; not
			 * sure if this needs re-calculation
			 */

			pco2t = pco2v;
			co2t = pco2t * SOLUB_TISSUES;
			pht = PHTISNOR + phi - PHINTNOR;
			dnbbt = BUFFCAPTISS * (pht - PHTISNOR);
			hco3t = Math.pow(10, (pht - PKHCO3)) * co2t;
			tco2t = co2t + hco3t;
			bet = hco3t - HCO3TISNOR + dnbbt;

			betotalsmatch = be_bisect * FFB + (hco3v + nbbi - norbbi) * FFI
					+ bet * FFT;

			errb = betotal - betotalsmatch;
			//	System.out.println("bevguessb "+be_bisect+" betotalsmatch
			// "+betotalsmatch+" errb "+errb);

			//		System.out.println("phv "+phv);
			predmv.setph(phv);
			predmv.setpo2(po2v);
			predmv.setpco2(pco2v);

			// reset state variables for venous blood and run equilibrium
			predmv.estcrb2b(); // perform blood equib from ph, pco2, co2p

			// calculate arterial state variables
			tco2a = tco2c * (1 - fs) + predmv.gettco2() * fs;
			preda.settco2(tco2a);
			preda.setbe(be_bisect);
			preda.setto2(to2a);

			preda.totals(); // equilibration from eco2,to2,be
			if (calculationCancel)
				return (false);

			return (true);

		} else //end - if (!nosol)
		{
			System.out.println("FAILED to find correct base excess ");
			return (false);

		} // end of else
	}

	/**
	 * Get the value of progress.
	 * 
	 * @return value of progress.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * reset the progress variable (set progress to zero).
	 */
	public void resetProgress() {
		progress = 0;
	}

	/**
	 * Set the value of calculationCancel.
	 * 
	 * @param v
	 *            Value to assign to calculationCancel.
	 */
	public void setCalculationCancel(boolean v) {
		this.calculationCancel = v;
	}
}