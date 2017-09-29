package ca.mcgill.ecse211.navigationlab;

import lejos.hardware.Button;
	import lejos.hardware.ev3.LocalEV3;
	import lejos.hardware.lcd.TextLCD;
	import lejos.hardware.motor.EV3LargeRegulatedMotor;
	import lejos.hardware.port.MotorPort;
	import lejos.hardware.port.Port;
	import lejos.hardware.sensor.SensorModes;
	import lejos.hardware.sensor.EV3ColorSensor;
	import lejos.hardware.sensor.EV3UltrasonicSensor;
	import lejos.robotics.SampleProvider;
	import lejos.utility.Delay;

	
public class NavigationLab {
	
	public double points[][] = {{0.0,2.0},{1.0,1.0},{2.0,2.0},{2.0,1.0},{1.0,0.0}};
	public double Start[] = {0.0,0.0};

	  private static final EV3LargeRegulatedMotor leftMotor =
	      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	  
	  private static final EV3LargeRegulatedMotor rightMotor =
	      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	  
	  private static final Port csPort = LocalEV3.get().getPort("S1");
	  private static final Port usPort = LocalEV3.get().getPort("S2");

	  public static final double WHEEL_RADIUS = 2.1;
	  public static final double TRACK = 15.0;
	  
	  public static void main(String[] args) {
		  
		  @SuppressWarnings("resource")
		  EV3ColorSensor csSensor = new EV3ColorSensor(csPort);
		  SampleProvider csColor = csSensor.getMode("Red");
		  float[] csData = new float[csColor.sampleSize()];
		  
		  ColorSensorPoller csPoller = new ColorSensorPoller(csColor,csData);
		  csPoller.start();
		  
		  
		  @SuppressWarnings("resource")
		  SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		  SampleProvider usDistance = usSensor.getMode("Distance"); 
		  float[] usData = new float[usDistance.sampleSize()];
		  
		  UltrasonicPoller usPoller = new UltrasonicPoller(usDistance,usData);
		  usPoller.start();
		  
		  
		  (new Thread() {
		        public void run() {
		          //Navigation.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
		        }
		      }).start();

		  while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		  System.exit(0);
		  
	  }
}