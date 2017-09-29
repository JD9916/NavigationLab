// Lab2.java

package ca.mcgill.ecse211.odometerlab;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.SensorModes;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class OdometryLab {

  private static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
  
  private static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
  
  private static final Port csPort = LocalEV3.get().getPort("S1");

  public static final double WHEEL_RADIUS = 2.1;
  public static final double TRACK = 15.15;

  public static void main(String[] args) {
    int buttonChoice;

    final TextLCD t = LocalEV3.get().getTextLCD();
    Odometer odometer = new Odometer(leftMotor, rightMotor);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
   
    
    @SuppressWarnings("resource")
    EV3ColorSensor csSensor = new EV3ColorSensor(csPort);
    SampleProvider csColor = csSensor.getMode("Red");
    
    float[] csData = new float[csColor.sampleSize()];
    
    ColorSensorPoller csPoller = null;
    
    csPoller = new ColorSensorPoller(csColor,csData);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer, csPoller);

   
      // clear the display
      t.clear();

      // ask the user whether the motors should drive in a square or float
      t.drawString("                ", 0, 0);
      t.drawString("  Middle Button ", 0, 1);
      t.drawString("       To       ", 0, 2);
      t.drawString("      Begin     ", 0, 3);
      t.drawString("                ", 0, 4);
      
      buttonChoice = Button.waitForAnyPress();

      if(buttonChoice == Button.ID_ENTER){
        odometryCorrection.start();
        csPoller.start();
        odometer.start();
        odometryDisplay.start();
      }
      
      // spawn a new Thread to avoid SquareDriver.drive() from blocking
      (new Thread() {
        public void run() {
          SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
        }
      }).start();
    

    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
    
    System.exit(0);
  }
}
