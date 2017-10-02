package ca.mcgill.ecse211.navigationlab;

import lejos.robotics.SampleProvider;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Sound;

public class Navigation extends Thread{
	
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	double leftRadius;
	double rightRadius;
	double width;
	
	private static final int FORWARD_SPEED = 100;
	private static final int ROTATE_SPEED = 50;
	private static final int MINIMUM_DISTANCE = 25;
	private static final double WHEEL_RADIUS = 2.1;
	private double toTheta;
	private Odometer odometer;
	private boolean navigating = false;
	public double x, y;
	private double points[][] = {{30.48,30.48},{0,60.96},{60.96, 60.96},{60.96, 30.48},{30.48, 0.0}};;
	
	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width) {
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
			      Thread.sleep(1000);
			    } catch (InterruptedException e) {
			      
		    }
			x = p[0];
			y = p[1];
			this.travelTo(p[0], p[1]);
			
		}
		
	leftMotor.stop();
    rightMotor.stop();
	
		
	}
	
	
	
	
	//public double points[][] = {{0.0,2.0},{1.0,1.0},{2.0,2.0},{2.0,1.0},{1.0,0.0}};
	//public double Start[] = {0.0,0.0};
	
	public void travelTo(double x, double y) {
		
		toTheta = this.getAngle(x, y) - odometer.getTheta();
		
		if(toTheta > 180){
			toTheta -= 360.0;
		}else if(toTheta < -180){
			toTheta += 360.0;
		}
		
		if((this.odometer.getTheta() <= (this.getAngle(x, y) -1)) || (this.odometer.getTheta() >= (this.getAngle(x, y) + 1)) ){

		turnTo(toTheta);
		
		}
		
			while(this.stop(x, y) == 0){
			
			if(UltrasonicPoller.getDistance() < MINIMUM_DISTANCE){
				
				//Method#1
				/*
				while(1==1){
					
					leftMotor.rotate(convertAngle(leftRadius, width, 90), true);
				    rightMotor.rotate(-convertAngle(rightRadius, width, 90), false);
				    leftMotor.setSpeed(FORWARD_SPEED);
				    rightMotor.setSpeed(FORWARD_SPEED);
				    leftMotor.forward();
				    rightMotor.forward();
				    leftMotor.rotate(convertDistance(leftRadius, 30), true);
				    rightMotor.rotate(convertDistance(rightRadius, 30), false);
				    leftMotor.rotate(-convertAngle(leftRadius, width, 90), true);
				    rightMotor.rotate(convertAngle(rightRadius, width, 90), false);
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
				}*/
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
		
	    this.navigating = true;
		
		leftMotor.setSpeed(ROTATE_SPEED);
	    rightMotor.setSpeed(ROTATE_SPEED);

	    leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	    rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	      //leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      //rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	    
	    //leftMotor.stop();
	    //rightMotor.stop();
	    
	    this.navigating = false;
	    
	    /*try {
		      Thread.sleep(500);
		    } catch (InterruptedException e) {
		      
		    }*/
	      
		
	}
	
	private double getAngle(double x, double y){
		
		double theta;
		double deltaX = x - this.odometer.getX();
		double deltaY = y - this.odometer.getY();
		
		
		
		theta = 90-(Math.atan(deltaY/deltaX)*(180/Math.PI));
		
		if(deltaX < 0) {
			theta += 180;
		}
		
		//System.out.println(theta);
		
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
	
	
	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width){
	
	
	}
	
	/*private boolean isNavigating() {
		
	}*/
	
}
