package ca.mcgill.ecse211.navigationlab;

import lejos.robotics.SampleProvider;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread{
	
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	double leftRadius;
	double rightRadius;
	double width;
	
	private static final int FORWARD_SPEED = 100;
	private static final int ROTATE_SPEED = 50;
	Odometer odometer;
	
	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width) {
	    this.odometer = odo;
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;
	    this.leftRadius = leftRadius;
	    this.rightRadius = rightRadius;
	    this.width = width;
	  }
	
	public void run(){
	//this.travelTo(5,5);
	this.travelTo(60.96,30.48);
		
	}
	
	
	
	
	public double points[][] = {{0.0,2.0},{1.0,1.0},{2.0,2.0},{2.0,1.0},{1.0,0.0}};
	public double Start[] = {0.0,0.0};
	
	public void travelTo(double x, double y) {
		
		while(this.stop(x, y) == 0){
		
		if(this.odometer.getTheta() <= this.getAngle(x, y) -3 || this.odometer.getTheta() >= this.getAngle(x, y) + 3 ){

			
		turnTo(this.getAngle(x, y));
		
		}
		else{
		
		
		
		leftMotor.setSpeed(FORWARD_SPEED);
	    rightMotor.setSpeed(FORWARD_SPEED);
	    leftMotor.forward();
	    rightMotor.forward();
	    
	    try {
		      Thread.sleep(2000);
		    } catch (InterruptedException e) {
		      
		    }
	    
		}
		}
		
		leftMotor.stop();
	    rightMotor.stop();
 
		
	}
	
	private void turnTo(double theta) {
		
	      leftMotor.setSpeed(ROTATE_SPEED);
	      rightMotor.setSpeed(ROTATE_SPEED);

	      leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	      //leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      //rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	      try {
		      Thread.sleep(2000);
		    } catch (InterruptedException e) {
		      
		    }
	      
		
	}
	
	private double getAngle(double x, double y){
		
		double theta;
		double deltaX = x - this.odometer.getX();
		double deltaY = y - this.odometer.getY();
		System.out.println(90-Math.atan(deltaY/deltaX)*(180/Math.PI));
		theta = 90-Math.atan(deltaY/deltaX)*(180/Math.PI);
		
		return theta;
	}
	
	private int stop(double x, double y) {
	    
		int stop = 1;  //true
		
		
		if(this.odometer.getX() < x - 1 || this.odometer.getX() > x + 1){
			stop = 0;
		}
		
		if(this.odometer.getY() < y - 1 || this.odometer.getY() > y + 1){
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
	
	
	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width){
	
	
	}
	
	/*private boolean isNavigating() {
		
	}*/
	
}
