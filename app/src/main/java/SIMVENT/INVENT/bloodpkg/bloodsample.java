package SIMVENT.INVENT.bloodpkg;

/*
/**
 * Patient contains the patient data.
 */
public class bloodsample 
{
	static char statevar='1';
//	static char statevar='2';
//	 static char statevar='3';
	
	// test inputs for estcrb2b
	static double phi=7.419;
	static double pco2i = 5.56454;
	static double po2i=4.35;
	
	// test inputs for tio2co2c
//	static double bei=1.593;
//	static double pco2i= 4.823;
//	static double po2i=9.312;
	
	 // test inputs for totals
//	static double bei=-17.33168235645946;
//	static double tco2i= 11.70529845045141;
//	static double to2i=0.95023108681151*9.3+0.01*13;


	static double cthbi=6.7;
	static double fmethbi=0.004;
	static double fcohbi=0.009;
	static double cdpgi=4.662;
	static double ti=37;
	
// ----------------  Main
	
	static public void main(String[] args)
	{
			System.out.println(ti);	
			
			bloodsample bl = new bloodsample();
	
			
	}
	
	public bloodsample()
	{
    
     // call for blood with state variables ph, pco2, po2 (estcrb2b)
		
  blood sample = new blood(phi,pco2i,po2i,cthbi,fmethbi,fcohbi,cdpgi,ti,statevar);
	  	System.out.println(sample.getph());	
		System.out.println(sample.getpco2());
		System.out.println(sample.getpo2());
		System.out.println(sample.getcthb());
		System.out.println(sample.getfmethb());
		System.out.println(sample.getfcohb());
		System.out.println(sample.getcdpg());
		System.out.println(sample.getco2p());
		System.out.println(sample.getntco2());
		System.out.println(sample.getnbb());
	
    	
  	
    // call for blood with state variables be, pco2, po2 (tio2co2c)
  /*  blood sample = new blood(bei,pco2i,po2i,cthbi,fmethbi,fcohbi,cdpgi,ti,statevar);
    	System.out.println("be"+sample.getbe());	
		System.out.println("pco2"+sample.getpco2());
		System.out.println("po2"+sample.getpo2());
		System.out.println(sample.getcthb());
		System.out.println(sample.getfmethb());
		System.out.println(sample.getfcohb());
		System.out.println(sample.getcdpg());
		System.out.println(sample.getco2p());
		System.out.println(sample.getntco2());
		System.out.println(sample.getnbb());*/
		
      
		// call for blood with state variables be, eco2, to2 (allbcr3b)
  /*  blood sample = new blood(bei,tco2i,to2i,cthbi,fmethbi,fcohbi,cdpgi,ti,statevar);
		System.out.println("tco2 "+sample.gettco2());
		System.out.println("to2 "+sample.getto2());
		System.out.println("be "+sample.getbe());
		System.out.println(sample.getcthb());
		System.out.println(sample.getfmethb());
		System.out.println(sample.getfcohb());
		System.out.println(sample.getcdpg());
		System.out.println(sample.getco2p());
		System.out.println(sample.getntco2());
		System.out.println(sample.getnbb());*/

    		System.out.println("start of estcrb2b");	
			sample.estcrb2b();
	
//			System.out.println("start of tio2co2c");	
//			sample.tio2co2c();
			
//			System.out.println("start of totals");	
//			sample.totals();
			
		
			System.out.println("ph "+sample.getph());	
			System.out.println("pco2 "+sample.getpco2());
			System.out.println("po2 "+sample.getpo2());
			System.out.println("hb "+sample.getcthb());
			System.out.println("met "+sample.getfmethb());
			System.out.println("co "+sample.getfcohb());
			System.out.println("cdpg "+sample.getcdpg());
     		System.out.println("so2 "+sample.getso2());
   			System.out.println("be "+sample.getbe());
   			System.out.println("to2 "+sample.getto2());
			System.out.println("tco2 "+sample.gettco2());
	   		System.out.println("co2p "+sample.getco2p());
			System.out.println("ntco2 "+sample.getntco2());
			System.out.println("nbb "+sample.getnbb());
			System.out.println("hbo2nhcoo "+sample.gethbo2nhcoo());
	 		System.out.println("hbnhcoo "+sample.gethbnhcoo());

			
	 }

}