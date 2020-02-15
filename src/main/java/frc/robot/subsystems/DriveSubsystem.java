/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

import java.util.Map;


public class DriveSubsystem extends SubsystemBase {

  // Make the motor controllers class variables so they show up on LiveWindow
  private  VictorSP rightFrontMotor = new VictorSP(RoboRio.PwmPorts.RightFrontMotor);
  private  VictorSP rightRearMotor = new VictorSP(RoboRio.PwmPorts.RightRearMotor);      
  private  SpeedControllerGroup rightGroup = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);
  
  private  VictorSP leftFrontMotor = new VictorSP(RoboRio.PwmPorts.LeftFrontMotor);
  private  VictorSP leftRearMotor = new VictorSP(RoboRio.PwmPorts.LeftRearMotor);      
  private  SpeedControllerGroup leftGroup = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);

  private DifferentialDrive m_diffDrive;

  private Encoder m_leftEncoder = new Encoder(RoboRio.DioPorts.LeftEncoderA, 
                                              RoboRio.DioPorts.LeftEncoderB, 
                                              DriveConstants.kLeftEncoderReversed,
                                              EncodingType.k4X);
  private Encoder m_rightEncoder = new Encoder(RoboRio.DioPorts.RightEncoderA, 
                                              RoboRio.DioPorts.RightEncoderB, 
                                              DriveConstants.kRightEncoderReversed,
                                              EncodingType.k4X);
  // The gyro sensor
  private AHRS m_navx;
  
  private NetworkTableEntry m_rotationBar;

  /**
   * Creates a new DriveSubsystem.
   */
  public DriveSubsystem() { 
    m_diffDrive = new DifferentialDrive(leftGroup, rightGroup);
    
    m_leftEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);
    m_rightEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);
    
    
    try {
      m_navx = new AHRS(SPI.Port.kMXP);
    }
    catch (RuntimeException ex) {
      DriverStation.reportError("Error instantiating navX MSP: " + ex.getMessage(), true);
    }
  }

  /**
   * 
   */
  public void setUpShuffleboard(ShuffleboardTab teleopTab) {
    ShuffleboardTab driveTab = Shuffleboard.getTab("Drive");

    if (m_navx != null) {
      driveTab.add(m_navx);
    }
    
    driveTab.add("Left Encoder", m_leftEncoder);
    driveTab.add("Right Encoder",m_rightEncoder);

    driveTab.addNumber("turnScale Value", () -> this.turnScale());

    driveTab.add(this);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // SmartDashboard.putNumber("Left Encoder Distance", m_leftEncoder.getRaw());
    // SmartDashboard.putNumber("Right Encoder Distance", m_rightEncoder.getRaw());
    // SmartDashboard.putNumber("Garbage Number", 11.4);
    // SmartDashboard.putNumber("Gyro Heading", getHeading());
  }

  /**
   * Stops the drive subystem
   */
  public void stop() {
    m_diffDrive.arcadeDrive(0, 0);
  }

  /**
   * Drives the robot using arcade controls
   * 
   * @param forward the forward movement speed
   * @param rotation the rate & direction to turn
   */
  public void arcadeDrive(double forward, double rotation) {
    m_diffDrive.arcadeDrive(forward, rotation);
  }

  /**
   * Drives the robot using tank controls
   * @param leftSpeed the left motor speed
   * @param rightSpeed the right motor speed
   */
  public void tankDrive(double leftSpeed, double rightSpeed) {
    m_diffDrive.tankDrive(leftSpeed, rightSpeed);
  }

  public void autoTurn(double rotation) {
    SmartDashboard.putNumber("Passed Rotation", rotation);

    if (rotation > 0.01 && rotation < 0.51) {
      rotation = 0.51;
    }
    if (rotation < -0.01 && rotation > -0.51) {
      rotation = -0.51;
    }
    SmartDashboard.putNumber("Calc Rotation", rotation);

    m_diffDrive.arcadeDrive(0, rotation);
  }

  /**
   * Initialize drive straight
   * (either encoder or gyro depending on how we're driving straight)
   */
  public void initDriveStraight() {
    this.resetEncoders();
  }
  /**
   * Drives the robot in a straight line
   * @param forward the forward movement speed
   */
  public void driveStraight(double forward) {
    double rotation = 0;
    // Calculate the rotation to keep the robot going straight

    m_diffDrive.arcadeDrive(forward, rotation);
  }

  /**
   * Sets the max output of the drive.  Useful for scaling the drive to drive more slowly.
   *
   * @param maxOutput the maximum output to which the drive will be constrained
   */
  public void setMaxOutput(double maxOutput) {
    m_diffDrive.setMaxOutput(maxOutput);
  }
  
  /**
   * Resets the drive encoders to zero
   */
  public void resetEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
  }

  /**
   * Get the distance from the left encoder
   */
  public double leftDistance() {
    return m_leftEncoder.getDistance();    
  }

  /**
   * Get the distance reading from the right encoder
   */
  public double rightDistance() {
    return m_rightEncoder.getDistance();
  }

  /**
   * Get the average distance from the two encoders
   */
  public double getAverageEncoderDistance() {
    return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
  }

  /**
   * @return rate of turn for 
   */
  public double turnScale(){
    double rate = leftDistance()-rightDistance();
    if (rate == 0.0)
      return rate;

    double sign = rate/Math.abs(rate);
    if (Math.abs(rate)>10)
      return sign*0.1;

    return rate/100.0;
  }

  /**
   * Resets the gyro heading to zero
   */
  public void zeroHeading() {
    m_navx.reset();
  }

  /**
   * @return the heading from the gyro in degrees, from +180 to -180
   */
  public double getHeading() {
    return Math.IEEEremainder(m_navx.getAngle(), 360) * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  /**
   * @return the rate of turn from the gyro
   */
  public double getTurnRate() {
    return m_navx.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }
}


