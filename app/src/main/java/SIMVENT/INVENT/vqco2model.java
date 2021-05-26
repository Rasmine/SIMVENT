package SIMVENT.INVENT;

//package invent;

import SIMVENT.INVENT.bloodpkg.*;

// two different applications of this code

// 1) Used when we have an arterial blood sample, in the non-invasive
// cardiac estimation routine

// takes alpe generated model parameters, fs, fA2
// the calculated oxgen fractions in each of the lung compartments, fo2e1, fo2e2
// and the blood characteuristics from a measured arterial sample
// ph,pco2,po2,so2,hb,fmethb,fcohb,cdpg,t,st;
// and solves the co2 vq system,
// returning lung capillary,and venous blood predictions
// the last of the inputs art estpred is = 1 if we have an arterial sample
// in this case VCO2 is a dummy value 1000


// 2) Used when we know any measurement of blood from which can be caluculated
// the state variables, and when we know the VCO2 
// from a previous steady state
// i.e. used as part of pred function in the INVENT system
// (we do not have an arterial blood sample)
    
// takes alpe generated model parameters, fs, fA2
// the calculated oxgen fractions in each of the lung compartments, fo2e1, fo2e2, and
// the total oygen in arterial blood (at previously guessed ph) artto2
// and the values of state variable for any sample of blood 
// and solves the co2 vq system,
// returning lung capillary, arterial and venous blood predictions
// (estpred is = 0 as we don't have an arterial sample)
// FECO2try is set as dummy value to 1000


// No equilibration of the tissue stores are made as yet
// see the comment in the MATLAB code INVENT 22/06/01
 
public class vqco2model 
{
    private static final boolean debug = false;
   // private static final double SOLUB_PLASMA =0.225; //Solubility coefficient plasma
    private static final char st = '1'; //determine constructor int blood class

    private double tCO2c2;
    private double VCO22;
    private double VCO21i;
    private double FCO2E;
    private double FECO2;
    private double FCO2E2;

    private double  flg;
    private double tCO2c;
    
    private double PCO2alv;
    private double tCO2ci;
    private double [] retvar= new double [3]; //return variable

   public final double[] vco2solv(blood measart,double q,double fo2e1,double fo2e2, double fa2, double fs,double fpas2,double just, double VAn, double VO2n, double FECO2try, double Patm, int estpred, double artto2, double VCO2) 
    {	
	//	System.out.println("vco2solv used");
        // variables used for bisection
        double errh;	// upper error
        double errl;	// lower error
        double errb;	// bisected error
        double FCO2E1h;	// upper FCO2E
        double FCO2E1l; // lower FCO2E
        double FCO2E1b; // bisected FCO2E
        double FCO2Eminpos; 
        double FCO2EmaxNaN;
        double FCO2EminNaN;
        double FCO2Emaxneg;
        double FCO2Eother;
        double FCO2Etst;
        boolean lowerFound;	
        boolean nosol;
        boolean notfound; 
        double stepsize;
        double chk;
        double [] resh= new double [10];
        double [] resl= new double [10];
        double [] resb= new double [10];
        double [] retvarco2= new double [11];//return variable
        
        // results (not sure if needed)
        double FECO2out;
        double Rqout;
        double Rqn;
        double VCO2out;
        double VCO2n;

        // local variables describing lung cappilary blood
        // in the two compartments
        blood predcap1;
        blood predcap2;	
        blood predven;

        FCO2E1h=0.9;	// upper FCO2E
        FCO2E1l=0.0001; // lower FCO2E
        FCO2E1b=0.0001; // bisected FCO2E
        FCO2Eminpos=0.9; 
        FCO2EmaxNaN=0.9;
        FCO2EminNaN=0;
        FCO2Emaxneg=0.9;
        FCO2Eother=0.9;
        FCO2Etst=0;
        lowerFound=false;	
        nosol=false;
        tCO2c=1000;
	
        // set up the predictions of lung capilary and venous blood
        // as having the same as the input sample
          
		predcap1= new blood(measart.getph(),measart.getpco2(),measart.getpo2(),measart.getcthb(),measart.getfmethb(),measart.getfcohb(),measart.getcdpg(),measart.gett(),st);
        predcap2= new blood(measart.getph(),measart.getpco2(),measart.getpo2(),measart.getcthb(),measart.getfmethb(),measart.getfcohb(),measart.getcdpg(),measart.gett(),st);
        predven= new blood(measart.getph(),measart.getpco2(),measart.getpo2(),measart.getcthb(),measart.getfmethb(),measart.getfcohb(),measart.getcdpg(),measart.gett(),st);
        // only the base excess will remain the same the pco2 and po2 will be set to those 
        // in the respective compartments at a later time

        /* FIND THE SOLUTION TO LUNG EQUATIONS 
           Done by iteratively selecting a FCO2E in the first compartment and then solving
           All lung equations (geterr2) in a bisection formulae, until a correct
           FCO2E1 is found
           At this point all values are calculated*/
		
        while (!lowerFound && !nosol)
        {
            if (debug)System.out.println("entering bisection ");
            FCO2Etst=FCO2Etst+0.005;
            resh=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2Etst,Patm,measart,just,VAn,estpred,VCO2);
            errh=resh[0];
            if (FCO2Etst>0.13)
            {
                nosol=true;
            }
              	
            if ( !Double.isNaN(errh)  && errh>0) //&& imag(errh)==0 image same as NaN in java
            {
                if (debug)System.out.println("numeric solution found ");
                lowerFound=true;
                FCO2E1h=FCO2Etst;
      	      	FCO2Eminpos=FCO2Etst;
                FCO2EmaxNaN=0;
                notfound=true;
                stepsize=0.005;
                while (notfound && !nosol)
                {
                    FCO2Eother=FCO2E1h-stepsize;
                    resh=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2Eother,Patm,measart,just,VAn,estpred,VCO2);
                    errh=resh[0];
                    if (Double.isNaN(errh) || Math.abs(errh) ==Double.POSITIVE_INFINITY )
                    {
                        stepsize=stepsize/2;
                        if (FCO2Eother>FCO2EmaxNaN)
                        {
                            FCO2EmaxNaN=FCO2Eother;
                        }
                    }
                    else if ( !Double.isNaN(errh) && errh<0)
                    {
                        notfound=false;
                        FCO2E1l=FCO2Eother;
                    }
                    else
                    {
                        stepsize=stepsize*1.25;
                        if (FCO2Eother<FCO2Eminpos)
                        {
                            FCO2Eminpos=FCO2Eother;
                        }
                    }
                    if (FCO2Eminpos-FCO2EmaxNaN<=0.0001)
                    {
                        nosol=true;
                    }
                }
            }
            else if ( !Double.isNaN(errh) && errh<=0) //&& imag(errh)==0 
            {
                lowerFound=true;
                FCO2E1l=FCO2Etst;
                FCO2Emaxneg=FCO2Etst;
                FCO2EminNaN=0.9;
                notfound=true;
                stepsize=0.005;
                while (notfound && !nosol)
                {
                    FCO2Eother=FCO2E1l+stepsize;
                    resh=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2Eother,Patm,measart,just,VAn,estpred,VCO2);
                    errh=resh[0];
                    if (Double.isNaN(errh) || Math.abs(errh) ==Double.POSITIVE_INFINITY )
                    {
                        stepsize=stepsize/2;
                        if (FCO2Eother<FCO2EminNaN)
                        {
                            FCO2EminNaN=FCO2Eother;
                        }
                    }
                    else if ( !Double.isNaN(errh) && errh>0) 
                    {
                        notfound=false;
                        FCO2E1h=FCO2Eother;
                    }
                    else
                    {
                        stepsize=stepsize*1.25; 
                        if (FCO2Eother>FCO2Emaxneg)
                        {
                            FCO2Emaxneg=FCO2Eother;
                        }
                    }
                    if (FCO2EminNaN-FCO2Emaxneg<=0.0001)
                    {
                        nosol=true;
                    }
                }
            }
        }
		
        if (!nosol)
        {
            resl=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2E1l,Patm,measart,just,VAn,estpred,VCO2);
            errl=resl[0];
            resh=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2E1h,Patm,measart,just,VAn,estpred,VCO2);
            errh=resh[0];
            chk=1000;  
		
            // once upper and lower limits found 
            // then bisect to find the correct soltion
			
            FCO2E1b=((FCO2E1h-FCO2E1l)/2)+FCO2E1l;
            while (Math.abs(chk) > 0.001) // Math.abs(FCO2E1l-FCO2E1h)>0.0001), previous cond rem as to loose
            {
                resb=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2E1b,Patm,measart,just,VAn,estpred,VCO2);
                errb=resb[0];
                if (errb < 0)
                { 
                    if ( errh < 0)
                    {
                        FCO2E1h = FCO2E1b;
                        errh=errb;
                    }
                    else if ( errl < 0)
                    {
                        FCO2E1l = FCO2E1b;
                        errl= errb;
                    }
                }
                else if (errb > 0)
                {
                    if ( errh > 0)
                    {
                        FCO2E1h = FCO2E1b;
                        errh=errb;
                    }
                    else if ( errl > 0)
                    {
                        FCO2E1l = FCO2E1b;
                        errl=errb;
                    }
                }
                chk=errh-errl;
		
                if (Double.isNaN(errb) || Math.abs(errb) ==Double.POSITIVE_INFINITY ){
                    FCO2E1b=FCO2E1b+0.0001;}
                else {
                    FCO2E1b=((FCO2E1h-FCO2E1l)/2)+FCO2E1l;}
            }
            resb=geterr2(predcap1,predcap2,predven,q,fo2e1,fo2e2,fa2,fs,fpas2,FCO2E1b,Patm,measart,just,VAn,estpred,VCO2);
            errb=resb[0];
            flg=resb[1];
        }
        else
    	{
            flg=1;
    	}
    
    	if (flg==0)
    	{
            if ( estpred == 1)
            {
				// Once correct FCO2E1 found solve the complete system
                predcap1.setpco2(Patm*FCO2E1b);
                predcap1.tio2co2c();
                VCO21i=VAn*(1-fa2)*FCO2E1b;
                predven.settco2(predcap1.gettco2()+((VCO21i*just)/((1-fs)*(1-fpas2)*q)));      
	  			
                tCO2c=(measart.gettco2()-(fs*predven.gettco2()))/(1-fs);
                predcap2.settco2((tCO2c-(predcap1.gettco2()*(1-fpas2)))/fpas2);
                VCO22=((predven.gettco2()-predcap2.gettco2())*((1-fs)*fpas2*q))/just;
                FCO2E2=VCO22/(VAn*fa2);
                predcap2.setpco2(Patm*FCO2E2);
                predcap2.setbe(measart.getbe());
                predcap2.setpo2(Patm*fo2e2);
                predcap2.tio2co2c();
						
                FECO2out=(FCO2E1b*(1-fa2))+(FCO2E2*fa2);
                VCO2out=FECO2out*VAn;
                VCO2n=FECO2try*VAn;
                Rqout=VCO2out/VO2n;
                Rqn=VCO2n/VO2n;
                predven.setbe(measart.getbe());
                predven.setto2(measart.getto2()-((VO2n*just)/q));
                predven.totals();
            }
            else
            {
                predcap1.setpco2(Patm*FCO2E1b);
                predcap1.tio2co2c();
                VCO21i=VAn*(1-fa2)*FCO2E1b;
                predven.settco2(predcap1.gettco2()+((VCO21i*just)/((1-fs)*(1-fpas2)*q)));

                FECO2= VCO2/VAn;
                VCO22=VCO2-VCO21i;
                tCO2c2=predven.gettco2()-((VCO22*just)/((1-fs)*fpas2*q));
                FCO2E2=(FECO2-(FCO2E1b*(1-fa2)))/fa2;
                predcap2.setpco2(Patm*FCO2E2);
                predcap2.setpo2(Patm*fo2e2);
                predcap2.setbe(measart.getbe());
                predcap2.tio2co2c();
		
                tCO2c= (predcap1.gettco2()*(1-fpas2))+(predcap2.gettco2()*fpas2);
		
                FECO2out=(FCO2E1b*(1-fa2))+(FCO2E2*fa2);
                VCO2out=FECO2out*VAn;
                VCO2n=FECO2*VAn;
                Rqout=VCO2out/VO2n;
                Rqn=VCO2n/VO2n;
                predven.setbe(measart.getbe());
 				
				// arterial total oxygen set as input from
				// previous solution on oxygen model, venous calculated
				
                predven.setto2(artto2-((VO2n*just)/q));
                predven.totals();
            }
    	}
        else
        {
            // if there is no solution set results to be ridiculous
            predven.setph(1000);
            predven.setpco2(1000);
            predven.setso2(1000);
            FECO2out=1000;
            Rqout=1000;
            Rqn=1000;
            VCO2out=1000;
            VCO2n=1000;
        }
        // return the variables
	//	double [] retvarco2= new double [10];
    	retvarco2[0]=predven.getph();
    	retvarco2[1]=predven.getpco2();
    	retvarco2[2]=predven.getso2();
    	retvarco2[3]=FECO2out;
    	retvarco2[4]=Rqout;
    	retvarco2[5]=Rqn;
        retvarco2[6]=VCO2out;
    	retvarco2[7]=VCO2n;
        retvarco2[8]=predven.gettco2();
        retvarco2[9]=tCO2c;
        retvarco2[10]=predven.getpo2();
    	return retvarco2;
    }

    // Function used to solve all the lung equations and return the bisection error
    private final double [] geterr2(blood predcap1, blood predcap2,blood predven, double q, double fo2e1,double fo2e2, double fa2, double fs,double fpas2, double FCO2E1, double Patm, blood measart,double just, double VA, int estpred, double VCO2)
    {
        if (debug)System.out.println("running through vq CO equations with FCO2E1= "+FCO2E1);
			
        // equations to use for parameter estimation
        double res=1000; 

        if ( estpred == 1)
        {
            flg=0;
            predcap1.setpo2(Patm*fo2e1);
            predcap1.setbe(measart.getbe());
            predcap1.setpco2(Patm*FCO2E1);
            predcap1.tio2co2c();
            VCO21i=VA*(1-fa2)*FCO2E1;
					
            predven.settco2(predcap1.gettco2()+((VCO21i*just)/((1-fs)*(1-fpas2)*q)));
			
            tCO2c=(measart.gettco2()-(fs*predven.gettco2()))/(1-fs);
            tCO2c2=(tCO2c-(predcap1.gettco2()*(1-fpas2)))/fpas2;
            VCO22=((predven.gettco2()-tCO2c2)*((1-fs)*fpas2*q))/just;
            FCO2E2=VCO22/(VA*fa2);

            predcap2.setpco2(Patm*FCO2E2);
            predcap2.setpo2(Patm*fo2e2);
            predcap2.setbe(measart.getbe());
            predcap2.tio2co2c();
		
            tCO2ci=predcap1.gettco2()*(1-fpas2)+predcap2.gettco2()*fpas2;
            if (tCO2ci>(measart.gettco2()+2) || Math.abs(tCO2ci)>10000 || Double.isNaN(tCO2ci) || tCO2ci==Double.NEGATIVE_INFINITY) 
                // if ECO2ci is bigger that ECO2a, then a re-calculation of shunt will be negative
   				// not plausible so set a flag so as to modify FCO2e and try again.
            {
                flg=1;
            }

            if (fs==1) //|  put in blood? (errlo==1) | (errhi==1)
            {
                flg=1;
            }

            FECO2=(FCO2E1*(1-fa2))+(FCO2E2*fa2);
            PCO2alv=FECO2*Patm;
        }
		
        else
        {
            // equations to use for prediction
            flg=0;
            predcap1.setpo2(Patm*fo2e1);
            predcap1.setbe(measart.getbe());
            predcap1.setpco2(Patm*FCO2E1);
	        predcap1.tio2co2c();
            VCO21i=VA*(1-fa2)*FCO2E1;
            predven.settco2(predcap1.gettco2()+((VCO21i*just)/((1-fs)*(1-fpas2)*q)));

            FCO2E= VCO2/VA;
            VCO22=VCO2-VCO21i;
            tCO2c2=predven.gettco2()-((VCO22*just)/((1-fs)*fpas2*q));
            FCO2E2=(FCO2E-(FCO2E1*(1-fa2)))/fa2;
			
            predcap2.setpco2(Patm*FCO2E2);
            predcap2.setpo2(Patm*fo2e2);
            predcap2.setbe(measart.getbe());
            predcap2.tio2co2c();
            PCO2alv=FCO2E*Patm;
        }
		
        // set return variables
        if (estpred==1){
            res=predcap2.gettco2()-tCO2c2;
        } 
        else if (estpred==0){
            res=tCO2c2-predcap2.gettco2();
        }
			
	if (debug)System.out.println("err tCO2c2 "+res);
        retvar[0]=res;
        retvar[1]=flg;
        retvar[2]=PCO2alv;
        return retvar;
    }
}
