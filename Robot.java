/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.CameraServer.*;
import edu.wpi.cscore.UsbCamera;


//------------------------------------------------------------------------------------------------------
//---------------------------------motor tanımlanması ------------------------------
//------------------------------------------------------------------------------------------------------

public class Robot extends TimedRobot {
  //motorlar
  private final PWMVictorSPX m_leftMotor  =new PWMVictorSPX(0);
  private final PWMVictorSPX m_rightMotor = new PWMVictorSPX(1);
  private final PWMVictorSPX m_asanMotor1 = new PWMVictorSPX(2);
  private final PWMVictorSPX m_asanMotor2 = new PWMVictorSPX(3);
  private final PWMVictorSPX m_asanMotor3 = new PWMVictorSPX(4);
  private final PWMVictorSPX m_topmotor   = new PWMVictorSPX(5);
  private final PWMVictorSPX m_carkmotor  = new PWMVictorSPX(6);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
  private final Joystick m_stick = new Joystick(0); 
  private static final int kUltrasonicPort1 = 0;
  private static final int kUltrasonicPort2 = 1;
  private static final int kUltrasonicPort3 = 2;
  private final AnalogInput ultrasonic1 = new AnalogInput(kUltrasonicPort1);
  private final AnalogInput ultrasonic2 = new AnalogInput(kUltrasonicPort2);
  private final AnalogInput ultrasonic3 = new AnalogInput(kUltrasonicPort3);
  Compressor c = new Compressor();


//------------------------------------------------------------------------------------------------------
//---------------------------------renk  sensörü ve renklerin tanımlanması------------------------------
//------------------------------------------------------------------------------------------------------
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
  private final ColorMatch m_colorMatcher = new ColorMatch();
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.170, 0.530, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
  private final Color bosrenk = ColorMatch.makeColor(0.240, 0.430, 0.250);
  //------------------------------------------------------------------------------------------------------
//---------------------------------------------mesafe---------------------------------------------------
//------------------------------------------------------------------------------------------------------
DoubleSolenoid hatchExtend1 = new DoubleSolenoid(2,3); 
DoubleSolenoid hatchExtend2 = new DoubleSolenoid(0,1); 

  
  @Override
  public void robotInit() {
    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);
    m_colorMatcher.addColorMatch(bosrenk);

    CameraServer.getInstance().startAutomaticCapture(0);

  }
  @Override
  public void robotPeriodic() {
  
    Color detectedColor = m_colorSensor.getColor();
    String colorString;
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Mavi";
    } else if (match.color == kRedTarget) {
      colorString = "Kirmizi";
    } else if (match.color == kGreenTarget) {
      colorString = "Yesil"; } 
    else if (match.color == kYellowTarget) {
      colorString = "Sari";
    } else if (match.color == bosrenk) {
      colorString = "Algilanmadi.";
    } else
     {
      colorString = "Algilanmadi";
    }
    SmartDashboard.putNumber("Gelen deger:    ", match.confidence);
    SmartDashboard.putNumber("Kirmizi degeri: ", detectedColor.red);
    SmartDashboard.putNumber("Yesil degeri:   ", detectedColor.green);
    SmartDashboard.putNumber("Mavi degeri:    ", detectedColor.blue);
    SmartDashboard.putString("Algilanan renk: ", colorString);

//------------------------------------------------------------------------------------------------------
//---------------------------------------------çark çevirme---------------------------------------------
//------------------------------------------------------------------------------------------------------
//-------------------     tam bitirilmedi---------------------------
String gameData;
gameData = DriverStation.getInstance().getGameSpecificMessage();
SmartDashboard.putString("Gamedata:        ", gameData);
if(m_stick.getRawButton(8))
{   
  m_robotDrive.arcadeDrive(0.1,0.1);  // Çark dönerken verimli döndürmesi için baskı uygulaması lazım
if(gameData.length() > 0)
{
  switch (gameData.charAt(0))
  {
    case 'B' :
    
      m_carkmotor.set(0.4);
      if(match.color == kRedTarget)
      {
        m_carkmotor.set(0);
      }
      break;
    case 'G' :
    m_carkmotor.set(0.4);
    if(match.color == kYellowTarget)
    {
      m_carkmotor.set(0);
    }
      break;
    case 'R' :
    m_carkmotor.set(0.4);
    if(match.color == kBlueTarget)
    {
      m_carkmotor.set(0);
    }
      break;
    case 'Y' :
    m_carkmotor.set(0.4);
    if(match.color == kGreenTarget)
    {
      m_carkmotor.set(4);
    }
      break;
    default :
      m_carkmotor.set(0);
      break;
  }
}
 else {
  m_carkmotor.set(0);
}
}

  }
  /*if(m_stick.getRawButton(8))
{   
/*  m_robotDrive.arcadeDrive(0.1,0.1);  // Çark dönerken verimli döndürmesi için baskı uygulaması lazım
if(gameData.length() > 0)
{
  switch (gameData.charAt(0))
  {
    case 'B' :
    
      m_carkmotor.set(0.2);
      if(match.color == kBlueTarget)
      {
        m_carkmotor.set(0);
      }
      break;
    case 'G' :
    m_carkmotor.set(0.2);
    if(match.color == kGreenTarget)
    {
      m_carkmotor.set(0);
    }
      break;
    case 'R' :
    m_carkmotor.set(0.2);
    if(match.color == kRedTarget)
    {
      m_carkmotor.set(0);
    }
      break;
    case 'Y' :
    m_carkmotor.set(0.2);
    if(match.color == kYellowTarget)
    {
      m_carkmotor.set(0);
    }
      break;
    default :
      m_carkmotor.set(0);
      break;
  }
}
 else {
  m_carkmotor.set(0);
}
}
  }*/
  double baszaman;

  @Override
  public void autonomousInit() 
  {
       baszaman=Timer.getFPGATimestamp();

  }
  

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    double zaman =Timer.getFPGATimestamp();
    c.setClosedLoopControl(false);

  SmartDashboard.putNumber("ZAMAN", zaman); 

  if (zaman - baszaman > 10 && zaman - baszaman <12) {   
       m_rightMotor.set(0.4);
       m_leftMotor.set(-0.4);
 }

 if (zaman - baszaman > 12 && zaman - baszaman <14) {   

  m_rightMotor.set(-0.4);
  m_leftMotor.set(0.4);


 }

 if (zaman - baszaman > 1 && zaman - baszaman <10)
 {

  m_asanMotor1.set(-0.8);
  m_asanMotor2.set(0.6);

 }
 
 if (zaman-baszaman > 4 && zaman-baszaman<10)
 {
m_asanMotor3.set(-0.5);
m_topmotor.set(0.5);

 }

 if (zaman-baszaman >= 15)
 {

  m_asanMotor1.set(0);
  m_asanMotor2.set(0);
  m_asanMotor3.set(0);
 }









  }


  




  //------------------------------------------------------------------------------------------------------
//-------------------------------------------------hareket------------------------------------------------
//--------------------------------------------------------------------------------------------------------

  @Override
  public void teleopPeriodic() {
 
 
   //ileri-geri-sag-sol
    m_robotDrive.arcadeDrive(m_stick.getY(),m_stick.getX()/1.4);
   //m_robotDrive.arcadeDrive(0,0.5);
   //ayarlanabilir hiz

   if(m_stick.getRawButton(5))
   {
    m_asanMotor1.set(0.1);
    m_asanMotor2.set(-0.1);
   }
//----------------------------------------------------------------------------------------------------
//---------------------------------------------firlatan motor----------------------------------------- 
//----------------------------------------------------------------------------------------------------
if (m_stick.getRawButton(6) )
   { 

 
    m_asanMotor1.set(-0.5);
    m_asanMotor2.set(0.5);
}     
else
{
  m_asanMotor1.set(0);
  m_asanMotor2.set(0);
}//------------------------------------------------------------------------------------------------------
//----------------------------------------topu yukarı çeken motor----------------------------------------
//------------------------------------------------------------------------------------------------------

if(m_stick.getPOV()==0)
{
m_asanMotor3.set(-0.6);
}  
else if(m_stick.getPOV()==180)
{
m_asanMotor3.set(0.4);
}
else
{
  m_asanMotor3.set(0.0);
}
//------------------------------------------------------------------------------------------------------
//-------------------------------------------autonom piston kaldırma---------------------------------------------------
//------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------


if(m_stick.getRawButton(3))
{
  if(m_stick.getPOV()==0)
      {
        hatchExtend1.set(DoubleSolenoid.Value.kForward);
        hatchExtend2.set(DoubleSolenoid.Value.kForward);
      }
      else if(m_stick.getPOV()==180)
      {
        hatchExtend1.set(DoubleSolenoid.Value.kReverse);
        hatchExtend2.set(DoubleSolenoid.Value.kReverse);
      }


}
  if (m_stick.getRawButton(1) )
  { 

 m_topmotor.set(0.5);
 
}
else if (m_stick.getRawButton(7) )
{ 
m_topmotor.set(-0.2);
} 
else
{m_topmotor.set(0);}







if(m_stick.getRawButton(2))
{
  if(m_stick.getPOV(0)==45)
c.setClosedLoopControl(true);

else if(m_stick.getPOV(0)==315)
c.setClosedLoopControl(false);
}




if(m_stick.getRawButton(4))
{
if(m_stick.getPOV(0)==180)
  hatchExtend1.set(DoubleSolenoid.Value.kReverse);

else if(m_stick.getPOV(0)==0)
  hatchExtend1.set(DoubleSolenoid.Value.kForward);

else if(m_stick.getPOV(0)==270)
hatchExtend2.set(DoubleSolenoid.Value.kForward);

else if(m_stick.getPOV(0)==90)
  hatchExtend2.set(DoubleSolenoid.Value.kReverse);

  
}



}}

