/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;


public class ColorWheelSubsystem extends SubsystemBase {

  private  VictorSP m_controlPanelMotor  = new VictorSP(RoboRio.PwmPort.ControlPanelMotor);
  /**
   * A Rev Color Sensor V3 object is constructed with an I2C port as a parameter. 
   * The device will be automatically initialized with default parameters.
   */
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

  /**
   * A Rev Color Match object is used to register and detect known colors. This can 
   * be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  private final ColorMatch m_colorMatcher = new ColorMatch();

  /**
   * Note: Any example colors should be calibrated as the user needs, these
   * are here as a basic example.
   */
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.43, 0.36, 0.20);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

  // Keep track of how many times we've moved onto a blue segment
  private int m_blueCounter = 0;
  private Color m_lastColor = kBlueTarget;

  /**
   * Creates a new ColorWheelSubsystem.
   */
  public ColorWheelSubsystem() {
    addChild("Motor", m_controlPanelMotor);

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);
  }

  /**
   * Sets up Shuffleboard for this subsystem
   * @param atCompetition Whether to exclude testing info from Shuffleboard
   */
  public void setUpShuffleboard(Boolean atCompetition) {
    // ToDo: add specific info about this subsystem
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    Color detectedColor = m_colorSensor.getColor();

    /**
     * Run the color match algorithm on our detected color
     */
    String colorString, setting;
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
      setting = "Green";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
      setting = "Yellow";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
      setting = "Blue";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
      setting = "Red";
    } else {
      colorString = "Unknown";
      setting = "Unknown";
    }

    if (match.color == kBlueTarget && m_lastColor != kBlueTarget) {
      m_blueCounter++;
    } 
    m_lastColor = match.color;
    
    // Put sensor info on SmartDashboard
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    
    SmartDashboard.putString("Detected Color", colorString);
    SmartDashboard.putString("Set Color", setting);
    SmartDashboard.putNumber("Blue counter", m_blueCounter);

    SmartDashboard.putNumber("Confidence", match.confidence);

    double IR = m_colorSensor.getIR();
    SmartDashboard.putNumber("IR", IR);

    int proximity = m_colorSensor.getProximity();
    SmartDashboard.putNumber("Proximity", proximity);
  }

  /**
   * Spin the motor for the control panel
   */
  public void spinControlPanel() {
    this.m_controlPanelMotor.setSpeed(.5);
  }

 /**
   * Stop the motor for the control panel
   */
  public void stopControlPanel() {
    this.m_controlPanelMotor.stopMotor();
  }

  /**
   * Spin the motor for the control panel at the specified speed
   * @param speed
   */
  public void lightTravel(double speed) {
    // Set a deadzone for the OI control
    if (Math.abs(speed) < ColorWheelConstants.kDeadzone) {
      speed = 0.0;
    }

    this.m_controlPanelMotor.setSpeed(speed * ColorWheelConstants.kSpeedScaling);
  }

  public void startCounting(){
    m_blueCounter = 0;
  }
}
