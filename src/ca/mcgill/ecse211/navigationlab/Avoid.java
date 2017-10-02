package ca.mcgill.ecse211.navigationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Avoid extends Thread{
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	private Odometer odometer;
	//private UltrasonicPoller us;
	private Navigation navigation;
	private static double x;
	private static double y;
	private static final int MINIMUM_DISTANCE = 10;
	private static final double WHEEL_RADIUS = 2.1;
	private static final int FORWARD_SPEED = 100;
	
	
	public Avoid(Odometer odometer, Navigation navigation,EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor){
		this.odometer = odometer;
		this.navigation = navigation;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	@Override
	public void run(){
	      
			
	      if(UltrasonicPoller.getDistance() < MINIMUM_DISTANCE){
				leftMotor.stop();
				rightMotor.stop();
				x = navigation.x;
				y = navigation.y;
				avoidObject();
				navigation.travelTo(x, y);
			}
			try { Thread.sleep(20); } catch(Exception e){}
	}
	
	public void avoidObject(){
		navigation.turnTo(90);
		leftMotor.setSpeed(FORWARD_SPEED);
	    rightMotor.setSpeed(FORWARD_SPEED);

	    leftMotor.rotate(convertDistance(WHEEL_RADIUS, 20), true);   
	    rightMotor.rotate(convertDistance(WHEEL_RADIUS, 20), false);
	    
	    leftMotor.stop();
		navigation.turnTo(-90);
		if(UltrasonicPoller.getDistance() < MINIMUM_DISTANCE){
			avoidObject();
		}
	}
	 
	private static int convertDistance(double radius, double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * radius));
		  }
}
