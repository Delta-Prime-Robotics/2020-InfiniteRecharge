/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.GamePad;

import static frc.robot.Constants.*;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


public class ShooterSubsystem extends SubsystemBase {
  private CANSparkMax m_lMotor;
 // private CANSparkMax m_rMotor;
  private CANPIDController m_lPidController;
 // private CANPIDController m_rPidController;
  private CANEncoder m_lEncoder;
  //private CANEncoder m_rEncoder;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;
  Joystick m_gamePad;
  /**
   * Creates a new ShooterSubsystem.
   */
  public ShooterSubsystem(Joystick gamePad) {
    m_lMotor = new CANSparkMax(RoboRio.CanIDs.LeftsparkMax, MotorType.kBrushless);
    //m_rMotor = new CANSparkMax(RoboRio.CanIDs.RightsparkMax, MotorType.kBrushless);
    m_gamePad = gamePad;
    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration parameters
     * in the SPARK MAX to their factory default state. If no argument is passed, these
     * parameters will not persist between power cycles
     */
    m_lMotor.restoreFactoryDefaults();
  //  m_rMotor.restoreFactoryDefaults();

    /**
     * In order to use PID functionality for a controller, a CANPIDController object
     * is constructed by calling the getPIDController() method on an existing
     * CANSparkMax object
     */
    m_lPidController = m_lMotor.getPIDController();
   // m_rPidController = m_rMotor.getPIDController();

    // Encoder object created to display position values
    m_lEncoder = m_lMotor.getEncoder();
    //m_rEncoder = m_rMotor.getEncoder();

    // PID coefficients
    kP = 5e-5; 
    kI = 1e-6;
    kD = 0; 
    kIz = 0; 
    kFF = 0; 
    kMaxOutput = 1; 
    kMinOutput = -1;
    maxRPM = 5700;

     // set PID coefficients
     m_lPidController.setP(kP);
     m_lPidController.setI(kI);
     m_lPidController.setD(kD);
     m_lPidController.setIZone(kIz);
     m_lPidController.setFF(kFF);
     m_lPidController.setOutputRange(kMinOutput, kMaxOutput);

    //  m_rPidController.setP(kP);
    //  m_rPidController.setI(kI);
    //  m_rPidController.setD(kD);
    //  m_rPidController.setIZone(kIz);
    //  m_rPidController.setFF(kFF);
    //  m_rPidController.setOutputRange(kMinOutput, kMaxOutput);

    // display PID coefficients on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);
  }
  
  /**
   * Sets up Shuffleboard for this subsystem
   * @param teleopTab The main tab used during teleop
   * @param atCompetition Whether to exclude testing info from Shuffleboard
   */
  public void setUpShuffleboard(ShuffleboardTab teleopTab, Boolean atCompetition) {
    // ToDo: add specific info about this subsystem
  }

  @Override
  public void periodic() {
    // read PID coefficients from SmartDashboard
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);

    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((p != kP)) { m_lPidController.setP(p); kP = p; }
    if((i != kI)) { m_lPidController.setI(i); kI = i; }
    if((d != kD)) { m_lPidController.setD(d); kD = d; }
    if((iz != kIz)) { m_lPidController.setIZone(iz); kIz = iz; }
    if((ff != kFF)) { m_lPidController.setFF(ff); kFF = ff; }
    if((max != kMaxOutput) || (min != kMinOutput)) { 
      m_lPidController.setOutputRange(min, max); 
      kMinOutput = min; kMaxOutput = max;
    }
    // This method will be called once per scheduler run

    double setPoint = m_gamePad.getRawAxis(GamePad.Axis.RightStickUpDown)*maxRPM;
    m_lPidController.setReference(setPoint, ControlType.kVelocity);

    SmartDashboard.putNumber("SetPoint", setPoint);
    SmartDashboard.putNumber("ProcessVariable", m_lEncoder.getVelocity());
  }
}
