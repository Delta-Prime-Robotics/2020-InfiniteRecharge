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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

import java.util.Map;


public class DriveSubsystem extends SubsystemBase {

  // Make the motor controllers class variables so they show up on LiveWindow
  private  VictorSP m_rightFrontMotor = new VictorSP(RoboRio.PwmPorts.RightFrontMotor);
  private  VictorSP m_rightRearMotor  = new VictorSP(RoboRio.PwmPorts.RightRearMotor);      
  private  SpeedControllerGroup m_rightGroup = new SpeedControllerGroup(m_rightFrontMotor, m_rightRearMotor);
  
  private  VictorSP m_leftFrontMotor = new VictorSP(RoboRio.PwmPorts.LeftFrontMotor);
  private  VictorSP m_leftRearMotor  = new VictorSP(RoboRio.PwmPorts.LeftRearMotor);      
  private  SpeedControllerGroup m_leftGroup = new SpeedControllerGroup(m_leftFrontMotor, m_leftRearMotor);

  private DifferentialDrive m_diffDrive;

  // Encoders
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

  // Class variable for collision detection
  private double m_lastLinearAccelY;
  
  /**
   * Creates a new DriveSubsystem.
   */
  public DriveSubsystem() { 
    m_diffDrive = new DifferentialDrive(m_leftGroup, m_rightGroup);
    
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
   * Sets up Shuffleboard for this subsystem
   * @param teleopTab The main tab used during teleop
   * @param atCompetition Whether to exclude testing info from Shuffleboard
   */
  public void setUpShuffleboard(ShuffleboardTab teleopTab, Boolean atCompetition) {

    if (!atCompetition) {
      ShuffleboardTab driveTab = Shuffleboard.getTab("Drive");

      if (m_navx != null) {
        driveTab.add(m_navx)
        .withSize(5,5).withPosition(5, 0);
      }
      
      ShuffleboardLayout encList = driveTab.getLayout("Encoders", BuiltInLayouts.kList)
        .withSize(5, 8).withPosition(0,0)
        .withProperties(Map.of("LabelPosition", "TOP"));

      encList.add("Left Encoder", m_leftEncoder);
      encList.add("Right Encoder",m_rightEncoder);

      driveTab.addNumber("turnScale Value", () -> this.turnScale())
        .withSize(3,2).withPosition(10, 0);

      driveTab.add(this)
        .withSize(8,2).withPosition(15, 0);
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    detectCollision();
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

  /**
   * Turns the robot only (no forward motion)
   * Sets a minimum value to support being called from a PID controller
   * @param rotation The rotation rate around the z-axis. Clockwise is positive.
   */
  public void autoTurn(double rotation) {
    SmartDashboard.putNumber("Passed Rotation", rotation);

    if (rotation > 0.01 && rotation < 0.51) {
      rotation = 0.51;
    }
    if (rotation < -0.01 && rotation > -0.51) {
      rotation = -0.51;
    }
    SmartDashboard.putNumber("Calc'ed Rotation", rotation);

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
    // To do: Calculate the rotation to keep the robot going straight

    m_diffDrive.arcadeDrive(forward, rotation);
  }

  /**
   * Sets the max output of the drive.  Useful for scaling the drive to drive more slowly.
   * @param maxOutput the maximum output to which the drive will be constrained
   */
  public void setMaxOutput(double maxOutput) {
    m_diffDrive.setMaxOutput(maxOutput);
  }
  
  /**
   * Resets both drive encoders to zero
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
   * @return rate of turn for driving straight via encoders
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

  public void detectCollision() {
    final double COLLISION_THRESHOLD_DELTA_G = 0.5;

    boolean collisionDetectedY = false;
    
    double currentLinearAccelY = m_navx.getWorldLinearAccelY();
    double currentJerkY = currentLinearAccelY - m_lastLinearAccelY;
    m_lastLinearAccelY = currentLinearAccelY;

    if (Math.abs(currentJerkY) > COLLISION_THRESHOLD_DELTA_G) {
      collisionDetectedY = true;
    }

    SmartDashboard.putBoolean("Collision Detected Y", collisionDetectedY);

  }
}


