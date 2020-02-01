/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

public class DriveSubsystem extends SubsystemBase {
  DifferentialDrive m_diffDrive;
  Encoder m_leftEncoder = new Encoder(RoboRio.DioPorts.LeftEncoderA, RoboRio.DioPorts.LeftEncoderB, false);
  Encoder m_rightEncoder = new Encoder(RoboRio.DioPorts.RightEncoderA, RoboRio.DioPorts.RightEncoderB, false);

  // The gyro sensor
  //private AHRS m_navx;

  /**
   * Creates a new DriveSubsystem.
   */
  public DriveSubsystem() { 
    VictorSP rightFrontMotor = new VictorSP(RoboRio.PwmPorts.RightFrontMotor);
    VictorSP rightRearMotor = new VictorSP(RoboRio.PwmPorts.RightRearMotor);      
    SpeedControllerGroup rightGroup = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);
    
    VictorSP leftFrontMotor = new VictorSP(RoboRio.PwmPorts.LeftFrontMotor);
    VictorSP leftRearMotor = new VictorSP(RoboRio.PwmPorts.LeftRearMotor);      
    SpeedControllerGroup leftGroup = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);

    m_diffDrive = new DifferentialDrive(leftGroup, rightGroup);
  
    m_leftEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);
    m_rightEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);
    
    // try {
    //   m_navx = new AHRS(SPI.Port.kMXP);
    // }
    // catch (RuntimeException ex) {
    //   DriverStation.reportError("Error instantiating navX MSP: " + ex.getMessage(), true);
    // }

  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Left Encoder Distance", m_leftEncoder.getDistance());
    SmartDashboard.putNumber("Right Encoder Distance", m_rightEncoder.getDistance());
    // This method will be called once per scheduler run
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

  // public double getAverageEncoderDistance() {
  //   return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
  // }

  /**
   * Resets the gyro heading to zero
   */
  // public void zeroHeading() {
  //   m_navx.reset();
  // }

  // /**
  //  * Returns the heading from the gyro
  //  * @return the robot's heading in degrees, from +180 to -180
  //  */
  // public double getHeading() {
  //   return Math.IEEEremainder(m_navx.getAngle(), 360) * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  // }

  // public double getTurnRate() {
  //   return m_navx.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  // }
}


