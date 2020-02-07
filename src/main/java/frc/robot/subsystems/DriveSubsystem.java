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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;


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

    if (m_navx != null) {
      Shuffleboard.getTab("Driving").add(m_navx);
    }
    
    Shuffleboard.getTab("Driving").add("Left Encoder", m_leftEncoder);
    Shuffleboard.getTab("Driving").add("Right Encoder", m_rightEncoder);
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
   * @param speed the forward movement
   * @param rotation the rate & direction to turn
   */
  public void arcadeDrive(double speed, double rotation) {
    m_diffDrive.arcadeDrive(speed, rotation);
  }

  public void tankDrive(double leftSpeed, double rightSpeed) {
    m_diffDrive.tankDrive(leftSpeed, rightSpeed);
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

  public double getAverageEncoderDistance() {
    return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
  }

  /**
   * Resets the gyro heading to zero
   */
  public void zeroHeading() {
    m_navx.reset();
  }

  /**
   * Returns the heading from the gyro
   * @return the robot's heading in degrees, from +180 to -180
   */
  public double getHeading() {
    return Math.IEEEremainder(m_navx.getAngle(), 360) * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  public double getTurnRate() {
    return m_navx.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }
}


