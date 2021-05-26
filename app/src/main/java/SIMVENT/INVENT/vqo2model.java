package SIMVENT.INVENT;

//package invent;

import VisualNumerics.math.Sfun;

public class vqo2model 
{

    //  Conversion factor to convert ltr/min to mmol/min.
    private static final double JUST= 41.56;
    private double FO2e1,FO2e2;
    private double VO22;
    private double VO21;
    private double PO2c1;
    private double PO2c2;
    private double sO21;
    private double cO2c1;
    private double sO22;
    private double cO2c2;
    private double cO2v1; 
    private double cO2v2;

    public final double[] cpsavq (double Patm, double averageVO2, double fo2e, double q,double cdpg,double pco2,double hbmet,double hbco,double hb,double ph,double temp,double shuntftest,double fa2test,double fpas2, double averageVA) 

    {
        double sO2a;
        double cO2v;
        double cO2a;
        
        double a,a1,a2,a3,a4,a5;
        double ko,b,yo;
        double h;
        double xo;
        double ceHb;
        double FO2ellim;
        double step=0.09;
        double errl;
        double FO2eulim;
        double erru;
        int solflg;
        double stepsize;
        double FO2eubefore;
        double errubefore;
        
        double FO2el;
        double FO2eu;
        double chk;
        double FO2eb;	
        double errb;
        
        double pO2al;
        double pO2au;
        
        double fpO2au;
        double fpO2al;
        int flg;
        boolean flgb;
        double pO2ab;
        double fpO2ab;
        
        double pO2a;
        double p,x1,y,s;       	
        
        sO2a=1000;
        cO2v=100;
        cO2a=100;

        // model equations
		
        // calculate sa constants
        // assume FHbF=0.005
        // calculate parameters of sa odc curve
  	a1=-0.88*(ph-7.4);				
  	a2=0.048*(Math.log(pco2/5.33));		
        a3=-0.7*hbmet;
  	a4=(0.3-(0.1*0.005))*((cdpg/5)-1);	
        a5=-0.25*0.005;	
        a=a1+a2+a3+a4+a5;
        ko=0.5343;
        b=0.055*(temp-37);	
        yo=Math.log(0.867/(1-0.867));
        h=3.5+a;
        xo=a+b;
        ceHb=(1-(hbmet+hbco))*hb;		
        FO2e1=1000;
        FO2e2=1000;
	
        //Initialise FO2E1
	
        FO2ellim=0.01;
  
        step=0.09;

        errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
        //System.out.println("errl "+errl);				
        while ( (Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY) && FO2ellim<1 )
        {
            FO2ellim= FO2ellim+step;
            errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
			
            //	System.out.println("FO2ellim "+FO2ellim);
            //	System.out.println("errl "+errl);
            //	System.out.println("step "+step);
        }
	  
        //System.out.println("FO2ellim "+FO2ellim);
        //System.out.println("errl "+errl);
        //System.out.println("step "+step);
	  
	  
        if (Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY)
        {
            step=0.04;
            FO2ellim=0.01;
            while ((Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY) && FO2ellim<1 )
            {
                FO2ellim= FO2ellim+step;
                errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
                //System.out.println("FO2ellim "+FO2ellim);
                //System.out.println("errl "+errl);
                //System.out.println("step "+step);
            }
        }
	
        //System.out.println("FO2ellim "+FO2ellim);
        //System.out.println("errl "+errl);
        //System.out.println("step "+step);

	  
        if (Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY)
        {
            step=0.01;
            FO2ellim=0.01;
            while ((Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY) && FO2ellim<1 )
            {
                FO2ellim= FO2ellim+step;
                errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
                //System.out.println("FO2ellim "+FO2ellim);
                //System.out.println("errl "+errl);
                //System.out.println("step "+step);
            }
        }	
		
        // if the lower limit comes out positive take a step away and search step space again	
	
        if (errl >0)	
        {
            FO2ellim= FO2ellim-step;
            //System.out.println("FO2ellim "+FO2ellim);

            errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
            //System.out.println("errl "+errl);				
	  
            while ((Double.isNaN(errl) || Math.abs(errl) == Double.POSITIVE_INFINITY) && FO2ellim<1 )
            {
                FO2ellim= FO2ellim+0.001;
                errl=geterr(FO2ellim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
                //	System.out.println("FO2ellim "+FO2ellim);
                //	System.out.println("errl "+errl);
                //	System.out.println("step "+step);
            }	
        }
	  
        // System.out.println("FO2ellim final "+FO2ellim);
        // System.out.println("errl final"+errl);
	  

        // strategy
        // decrease from 0.99 in steps of 0.9
        // either 1) a solution of different sign to the lower will be found.. end
        // or even the highes solution will give a solution the same sign .. end
        // there will be a step where we go from not a solution to 
        // a solution the same sign as the lower
        // if this is found then search within this new space, reducing the step size
        // accordingly
        // if we get to a step size 0.001 with there no positive solution then give up
		

        FO2eulim=0.99;
	//	System.out.println("FO2eulim "+FO2eulim);
        erru=geterr(FO2eulim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
	//	System.out.println("erru "+erru);
		
        solflg=0;
        stepsize=0.09;
		
        if ((erru == erru) && ((erru <0 && errl >0) ||(erru >0 && errl <0)))
        {
            solflg=1;
            //		System.out.println("solflg solution diff sign "+solflg);
            //a solution of different sign to the lower will be found.. end
        }
		
		
        if ((erru == erru) && ((erru >0 && errl >0) ||(erru <0 && errl <0)))
        {
            solflg=2;
            //		System.out.println("solflg highest same as lowest quit "+solflg);
            //the highes solution will give a solution the same sign .. end
        }
		
        while ((Double.isNaN(erru) || Math.abs(errl) == Double.POSITIVE_INFINITY) && FO2eulim>0 && solflg==0 && stepsize>0.0005)
        {
            FO2eubefore=FO2eulim;
            errubefore= erru;
            FO2eulim= FO2eulim-stepsize;
            //		System.out.println("FO2eulim "+FO2eulim);
            erru=geterr(FO2eulim,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
            //		System.out.println("erru "+erru);
            if ((erru == erru) && ((erru <0 && errl >0) ||(erru >0 && errl <0)))
            {
                solflg=1;
                //			System.out.println("solflg solution diff sign "+solflg);
				//a solution of different sign to the lower will be found.. end
            }
            if ((erru == erru) && ((erru >0 && errl >0) ||(erru <0 && errl <0)))
            {
				//a solution the same sign as the lower has been found
				// search within this new space, reducing the step size
                //			System.out.println("solution same sign red space ");
                stepsize=(FO2eubefore-FO2eulim)/10;
                //			System.out.println("stepsize "+stepsize);
                FO2eulim=FO2eubefore;
                //			System.out.println("FO2eulim "+FO2eulim);
                erru=errubefore;
                //			System.out.println("erru "+erru);
            }
		  		 		
        }

	//System.out.println("FO2eulim "+FO2eulim);

        FO2el=	FO2ellim;
        FO2eu=	FO2eulim;
	
        chk=FO2eu-FO2el;
	
		
        int flgs=0;

        while (chk > 0.0001)
        {
            if (erru <0 && errl >0 && solflg==1){
                flgs=1;}
	
            if (erru >0 && errl <0 && solflg==1){
                flgs=1;}
		
            //		System.out.println("flgs "+flgs);
            if (flgs == 1)
            {
                FO2eb= ((FO2eu-FO2el)/2)+FO2el;
                errb=geterr(FO2eb,fa2test,fo2e,averageVA,averageVO2,Patm,yo,xo,h,ko,hbmet,hbco,ceHb,JUST,fpas2,q,hb,shuntftest);
				
                //			System.out.println("FO2eb "+FO2eb);
                //			System.out.println("errb "+errb);
	   		
                if (errb < 0 && erru < 0){
                    FO2eu = FO2eb;
                    erru=errb;}
      
                else if (errb > 0 && erru > 0){
                    FO2eu = FO2eb;
                    erru=errb;}
         
                else if (errb > 0 && errl > 0){
                    FO2el = FO2eb;
   		    errl=errb;}
         
                else if (errb < 0 && errl < 0){
                    FO2el = FO2eb;
                    errl=errb;}
            }	
	    
            else
            {
				//	disp('bisection method does not work')
                flgs=0;			
                break;
            }
	
            //		System.out.println("FO2el "+FO2el);
            //		System.out.println("FO2eu "+FO2eu);
            chk=FO2eu-FO2el;
        }

        //System.out.println("chk "+chk);
        if (flgs==1)
        {
            FO2e1= ((FO2eu-FO2el)/2)+FO2el;
            //System.out.println("FO2e1 "+FO2e1);
            FO2e2 =(-(FO2e1*(1-fa2test))+(fo2e))/fa2test;
            //System.out.println("FO2e2 "+FO2e2);
            VO22= (fa2test*averageVA)*(((averageVO2/averageVA)+(fo2e))-FO2e2);
            VO21= -VO22+averageVO2;
            PO2c1= Patm*FO2e1;
            PO2c2= Patm*FO2e2;
            sO21=((hb*((Math.exp(yo+((Math.log((PO2c1+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c1+(218*0.00023))/7))-xo))))/(1+Math.exp(yo+((Math.log((PO2c1+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c1+(218*0.00023))/7))-xo))))))*(1-hbmet)-hbco))/ceHb);
            cO2c1=PO2c1*0.01+sO21*ceHb;
            sO22=((hb*((Math.exp(yo+((Math.log((PO2c2+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c2+(218*0.00023))/7))-xo))))/(1+Math.exp(yo+((Math.log((PO2c2+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c2+(218*0.00023))/7))-xo))))))*(1-hbmet)-hbco))/ceHb);	
            cO2c2=PO2c2*0.01+sO22*ceHb;
            cO2v= -((VO21*JUST)/((1-shuntftest)*(1-fpas2)*q))+cO2c1;
            cO2a= (cO2c1*(1-fpas2)*(1-shuntftest))+(cO2c2*(1-shuntftest)*fpas2)+(cO2v*shuntftest);
   
   
            // Find solution to PO2 using bisection method	

            // initial estimates of bounds%

            pO2al= 1;
            pO2au=150;
		
            fpO2au =((hb*(Math.exp(yo+(Math.log((pO2au+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2au+0.0501)/7)-xo))))/(1+Math.exp(yo+(Math.log((pO2au+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2au+0.0501)/7)-xo)))))*(1-hbmet)-hbco))/ceHb)*ceHb+(pO2au*0.01)-cO2a;
            fpO2al =((hb*(Math.exp(yo+(Math.log((pO2al+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2al+0.0501)/7)-xo))))/(1+Math.exp(yo+(Math.log((pO2al+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2al+0.0501)/7)-xo)))))*(1-hbmet)-hbco))/ceHb)*ceHb+(pO2al*0.01)-cO2a;
            chk=pO2au-pO2al;
	
            flg=0;
            flgb=false;
	
            while (chk > 0.05)
            {
                if (fpO2au <0 && fpO2al >0){
                    flg=1;}
			
                if (fpO2au >0 && fpO2al <0){
                    flg=1;}
		
                if (flg == 1)
                {
                    pO2ab= ((pO2au-pO2al)/2)+pO2al;
                    fpO2ab =((hb*(Math.exp(yo+(Math.log((pO2ab+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2ab+0.0501)/7)-xo))))/(1+Math.exp(yo+(Math.log((pO2ab+0.0501)/7)-xo)+h*(Sfun.tanh(ko*(Math.log((pO2ab+0.0501)/7)-xo)))))*(1-hbmet)-hbco))/ceHb)*ceHb+(pO2ab*0.01)-cO2a;
			
                    if (fpO2ab < 0 && fpO2au < 0){
   	  	  	pO2au = pO2ab;
                        fpO2au=fpO2ab;
                    }
      		
                    else if (fpO2ab > 0 && fpO2au > 0)
                    {
                        pO2au = pO2ab;
                        fpO2au=fpO2ab;
                    }

                    else if (fpO2ab > 0 && fpO2al > 0)
                    {
                        pO2al = pO2ab;
                        fpO2al=fpO2ab;
                    }

                    else if (fpO2ab < 0 && fpO2al < 0)
                    {
                        pO2al = pO2ab;
                        fpO2al=fpO2ab;
                    }
                }	
                else
                {
                    //			disp('bisection method does not work')
                    flgb=true;
                    break;
                }
                chk=pO2au-pO2al;
            }

            if (!flgb)
            {
                pO2a= ((pO2au-pO2al)/2)+pO2al;
				//System.out.println("pO2a "+pO2a);
		
				// calculate SO2 from pO2
                p=pO2a+(218*0.00023);			
                x1=Math.log(p/7);
                y=yo+(x1-xo)+(h*Sfun.tanh(ko*(x1-xo)));
                s=Math.exp(y)/(1+Math.exp(y));		
                sO2a=(hb*(s*(1-hbmet)-hbco))/ceHb;
            }
        }	
	
        else 
        {
            sO2a=10000;
            //	System.out.println("sO2a "+sO2a);
   		
        }

        double [] resvar= new double [5];	
        resvar[0]=sO2a;
        resvar[1]=FO2e1;
        resvar[2]=FO2e2;
        resvar[3]=cO2v;
        resvar[4]=cO2a;
        return resvar;
    }


    private final double geterr (double FO2e1,double fa2val,double FO2E,double averageVA,double averageVO2,double Patm,double yo,double xo,double h,double ko,double hbmet,double hbco,double ceHb,double JUST,double fpas2,double q,double hb,double shuntfval) 
    {
   
        /*	System.out.println("Into geterr");
                System.out.println("FO2e1 "+FO2e1);
		System.out.println("fa2val "+fa2val);
		System.out.println("FO2E"+FO2E);
		System.out.println("averageVA"+averageVA);
		System.out.println("Patm "+Patm);
		System.out.println("averageVO2 "+averageVO2);
		System.out.println("fpas2"+fpas2);
		System.out.println("q "+q);
		System.out.println("shuntfval "+shuntfval);*/


  	
  	
  	FO2e2=(-(FO2e1*(1-fa2val))+FO2E)/fa2val;
        //	System.out.println("FO2e2 "+FO2e2);
  	VO22= (fa2val*averageVA)*(((averageVO2/averageVA)+FO2E)-FO2e2);
        //		System.out.println("VO22 "+VO22);
  	VO21= -VO22+averageVO2;
        //	System.out.println("VO21 "+VO21);
        PO2c1= Patm*FO2e1;
        //		System.out.println("PO2c1 "+PO2c1);
        PO2c2= Patm*FO2e2;
        //		System.out.println("PO2c2 "+PO2c2);
		
        sO21=((hb*((Math.exp(yo+((Math.log((PO2c1+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c1+(218*0.00023))/7))-xo))))/(1+Math.exp(yo+((Math.log((PO2c1+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c1+(218*0.00023))/7))-xo))))))*(1-hbmet)-hbco))/ceHb);
        //		System.out.println("sO21 "+sO21);
        cO2c1=PO2c1*0.01+sO21*ceHb;
	
        sO22=((hb*((Math.exp(yo+((Math.log((PO2c2+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c2+(218*0.00023))/7))-xo))))/(1+Math.exp(yo+((Math.log((PO2c2+(218*0.00023))/7))-xo)+(h*Sfun.tanh(ko*((Math.log((PO2c2+(218*0.00023))/7))-xo))))))*(1-hbmet)-hbco))/ceHb);	
        //	System.out.println("sO22 "+sO22);
        cO2c2=PO2c2*0.01+sO22*ceHb;
	
        cO2v1= -((VO21*JUST)/((1-shuntfval)*(1-fpas2)*q))+cO2c1;
        cO2v2= -((VO22*JUST)/((1-shuntfval)*fpas2*q))+cO2c2;
        
  	return (cO2v1-cO2v2);
    }
  
	
}
