package ca.mcgill.ecse211.navigationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Sound;

public class Navigation extends Thread{
	
  EV3LargeRegulatedMotor leftMotor;
  EV3LargeRegulatedMotor rightMotor;
	
  double leftRadius;                    //Holds the left wheel radius (cm)
  double rightRadius;                   //Holds the right wheel radius (cm)
  double width;                         //Holds the length of the wheel base (cm)
	
  private static final int FORWARD_SPEED = 100;     //Default drive speed (deg/sec)
  private static final int ROTATE_SPEED = 50;       //Default rotate speed (deg/sec)
  private static final int MINIMUM_DISTANCE = 25;   //Minimum distance to activate avoidance (cm)
  private boolean isNavigating = false;             //Checks if travelTo() or turnTo() is being called.
  private double toTheta;               //Used to correct the robot's orientation (in degrees)
  private Odometer odometer;
  private boolean navigating = false;   //Initially, the robot is not navigating
  public double x, y;                   //The desired x and y coordinates of the next checkpoint.
  private double points[][] = {{30.48,30.48},{0,60.96},{60.96, 60.96},{60.96, 30.48},{30.48, 0.0}};;
	
   public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width) {
     //Here, objects instantiated in NavigationLab are passed to the Navigation Class.
     this.odometer = odo;
     this.leftMotor = leftMotor;
     this.rightMotor = rightMotor;
     this.leftRadius = leftRadius;
     this.rightRadius = rightRadius;
     this.width = width;
	 }
	
  public void run(){
    for (double[] p : points){
	  try {
	    Thread.sleep(1000);        //Wait 1s before navigating (after button is pushed)
	  } catch (InterruptedException e) { 		  
	  }                        
	  x = p[0];                    //Load the next checkpoint from
	  y = p[1];                    //points into x and y
	  this.isNavigating = true;    //The robot will now be navigating.
	  this.travelTo(p[0], p[1]);   //Travels to the next point
	  this.isNavigating = false;   //The robot has finished navigating
      }
    leftMotor.stop();              //Stops motors after end is reached.
    rightMotor.stop();
	}

  public void travelTo(double x, double y) {
    
    //toTheta will store the angle the robot must orient itself to point in the direction
    //of the next checkpoint
    toTheta = this.getAngle(x, y) - odometer.getTheta();
	  if(toTheta > 180){           //The correction angle must always be the acute angle.
	    toTheta -= 360.0;          //If it is not, then it is turned into one.
	  }else if(toTheta < -180){
	    toTheta += 360.0;
	  }
      if((this.odometer.getTheta() <= (this.getAngle(x, y) -1)) 
        || (this.odometer.getTheta() >= (this.getAngle(x, y) + 1)) ){
      //checks to see if the robot is not facing the right way
        turnTo(toTheta);            //Corrects the bot's orientation
	  }
	  while(this.stop(x, y) == 0){  //While the robot has not reached its next checkpoint.
	    if(UltrasonicPoller.getDistance() < MINIMUM_DISTANCE){
		  double beforeTheta;
		  double afterTheta;
		  double deltaTheta;
		  double correction;
		  double distanceFromWall;
		  distanceFromWall = UltrasonicPoller.getDistance();
		  while (1==1){
		    beforeTheta = odometer.getTheta();
		    while(UltrasonicPoller.getDistance() < 40){
			  leftMotor.rotate(convertAngle(leftRadius, width, 5), true);
			  rightMotor.rotate(-convertAngle(rightRadius, width, 5), false);
			}
			leftMotor.rotate(convertAngle(leftRadius, width, 10), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, 10), false);
			afterTheta = odometer.getTheta();
            deltaTheta = Math.abs(beforeTheta-afterTheta);
            correction = distanceFromWall/Math.cos(deltaTheta*((Math.PI)/(180)));
            leftMotor.rotate(convertDistance(leftRadius, correction), true);
            rightMotor.rotate(convertDistance(rightRadius, correction), false);
            leftMotor.rotate(convertDistance(leftRadius, 8), true);
            rightMotor.rotate(convertDistance(rightRadius, 8), false);
            leftMotor.rotate(-convertAngle(leftRadius, width, 180-2*deltaTheta), true);
            rightMotor.rotate(convertAngle(rightRadius, width, 180-2*deltaTheta), false);
            leftMotor.rotate(convertDistance(leftRadius, correction), true);
            rightMotor.rotate(convertDistance(rightRadius, correction), false);
            toTheta = this.getAngle(x, y) - odometer.getTheta();
            if(toTheta > 180){
			  toTheta -= 360.0;
			}else if(toTheta < -180){
			  toTheta += 360.0;
			}
            if((this.odometer.getTheta() <= (this.getAngle(x, y) -1)) || (this.odometer.getTheta() >= (this.getAngle(x, y) + 1)) ){
              turnTo(toTheta);
            }
            break;
		  }	
	    }
		leftMotor.setSpeed(FORWARD_SPEED);
	    rightMotor.setSpeed(FORWARD_SPEED);
	    leftMotor.forward();
	    rightMotor.forward();
      }	
	  Sound.beep();      //Make a beeping sound
	}
	
    public void turnTo(double theta) {
	  this.navigating = true;              //Sets the robot to navigation mode
      leftMotor.setSpeed(ROTATE_SPEED);    //Sets the motors to rotation speed  
      rightMotor.setSpeed(ROTATE_SPEED);
      
      //Turns both motors by the correction angle of theta (in degrees).
      leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
      rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
      this.navigating = false;              //Turns navigation mode off
    }
	
	private double getAngle(double x, double y){
      double theta;
      //Calculates the angle the robot must orient itself in to face the checkpoint.
      //This is done using the robot's current position, the checkpoint position,
      //and simple trigonometry.
	  double deltaX = x - this.odometer.getX();
	  double deltaY = y - this.odometer.getY();
	  theta = 90-(Math.atan(deltaY/deltaX)*(180/Math.PI)); //The angle must be converted first.
	  if(deltaX < 0) {
	    theta += 180;
	  }
    return theta;
	}
	
	private int stop(double x, double y) {
      int stop = 1;  //true
      if(this.odometer.getX() < x - 2 || this.odometer.getX() > x + 2){
      stop = 0;
      }
      if(this.odometer.getY() < y - 2 || this.odometer.getY() > y + 2){
        stop = 0;
      }
      return stop;
	  }
	
	private static int convertAngle(double radius, double width, double angle) {
	  return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	private static int convertDistance(double radius, double distance) {
	  return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private boolean isNavigating() {
	  return this.isNavigating;
	}
}
