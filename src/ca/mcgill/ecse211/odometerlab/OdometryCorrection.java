/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometerlab;

import lejos.hardware.Sound;

public class OdometryCorrection extends Thread {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private ColorSensorPoller poller;
  private int colorValue;
  
  

  // constructor
  public OdometryCorrection(Odometer odometer, ColorSensorPoller ColorSensorPoller) {   //Passes in the ColorSensorPoller class
    this.odometer = odometer;
    this.poller = ColorSensorPoller; //initiates the poller variable of this class. This is done to pass in values from the color sensor 
    
  }

  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    
    while (true) {
      correctionStart = System.currentTimeMillis();
      
      colorValue = poller.getColor();   //Assigns values form the color sensor to a variable that is later used to detect a black line
            
      
      
      
	      if ((SquareDriver.getTurns() % 2) == 1) {   //If the turn number is either 1 or 3, fix values of X only
		      if(colorValue < 30){   //A sensor reading below 30 was chosen to register a black line
		    	  Sound.beep();      //Make a beeping sound
		    	  
		    	  if(odometer.getX()>=0) {   //If the current X position is positive
			    	  if((odometer.getX() % 30.48) > 15.24) { //If the current position is closer to the next multiple of 30.48 than the previous
			    		  odometer.setX(odometer.getX() + (30.48-(odometer.getX()%30.48)));  //Correct position to the next multiple of 30.48
			    	  }
			    	  else {
			    		  odometer.setX(odometer.getX() - (odometer.getX()%30.48));  //Correct position to the previous multiple of 30.48
			    	  }
			      }
		    	  else {    //Same as above, if the current X position is negative
		    		  if((Math.abs(odometer.getX()) % 30.48) > 15.24) {
			    		  odometer.setX((Math.abs(odometer.getX()) + (30.48-(Math.abs(odometer.getX())%30.48)))*(-1));
			    	  }
			    	  else {
			    		  odometer.setX((Math.abs(odometer.getX()) - (Math.abs(odometer.getX())%30.48))*(-1));
			    	  }
		    	  }
		      }
	      }
	      else {      //If the turn number is either 0 or 2, fix values of Y only (same as above but for Y values)
	    	  if(colorValue < 30){
	    		  Sound.beep();
	    		  if(odometer.getY()>=0) {
	    			  
			    	  if((odometer.getY() % 30.48) > 15.24) {
			    		  odometer.setY(odometer.getY() + (30.48-(odometer.getY()%30.48)));
			    	  }
			    	  else {
			    		  odometer.setY(odometer.getY() - (odometer.getY()%30.48));
			    	  }
	    		  }
	    		  else {
	    			  if((Math.abs(odometer.getY()) % 30.48) > 15.24) {
			    		  odometer.setY((Math.abs(odometer.getY()) + (30.48-(Math.abs(odometer.getY())%30.48)))*(-1));
			    	  }
			    	  else {
			    		  odometer.setY((Math.abs(odometer.getY()) - (Math.abs(odometer.getY())%30.48))*(-1));
			    	  }
	    		  }
		      }
	      }
      //TODO Place correction implementation here
      

      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here because it is not
          // expected that the odometry correction will be
          // interrupted by another thread
        }
      }
    }
  }
}
