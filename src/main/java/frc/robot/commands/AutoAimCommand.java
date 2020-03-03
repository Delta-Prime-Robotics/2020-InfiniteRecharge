/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.subsystems.*;

import static frc.robot.Constants.*;

/**
 * 
 */
public class AutoAimCommand extends PIDCommand {
  private DriveSubsystem m_drive;
  private CameraSubsystemRPi m_cameras;

  /**
   * Creates a new AutoAimCommand.
   */
  public AutoAimCommand(DriveSubsystem drive, CameraSubsystemRPi cameras) {
    super(
        // The controller that the command will use
        new PIDController(AutoAim.kP, AutoAim.kI, AutoAim.kD),
        // This should return the measurement
        cameras::getOffsetX,
        // This should return the setpoint (can also be a constant)
        () -> 0,
        // This uses the output
        (output) -> drive.autoTurn(output * AutoAim.kOffsetRatio)
        );
    // Configure additional PID options by calling `getController` here.
    
    // Don't set the PID controller to be continuous since the output is in pixels, not degrees
    //getController().enableContinuousInput(-180, 180);

    // Set the controller tolerance - the delta tolerance ensures the robot is stationary at the
    // setpoint before it is considered as having reached the reference
    getController().setTolerance(AutoAim.kTurnTolerancePxl, AutoAim.kTurnRateTolerancePxlPerS);

    m_drive = drive;
    m_cameras = cameras;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_drive);
    addRequirements(m_cameras);

    setUpPidTuning();
  }

  @Override
  public void execute() {
    super.execute();

    tunePid();
  }
  
  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }

  
  // For PID tuning
  private double m_P = 0;
  private double m_I = 0;
  private double m_D = 0;

  private void setUpPidTuning() {
    m_P = AutoAim.kP;
    m_I = AutoAim.kI;
    m_D = AutoAim.kD;

    SmartDashboard.putNumber("P Gain", m_P);
    SmartDashboard.putNumber("I Gain", m_I);
    SmartDashboard.putNumber("D Gain", m_D);
  }

  private void tunePid() {
    PIDController pidController = getController();

    SmartDashboard.putNumber("Position Error", pidController.getPositionError());
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    
    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((p != m_P)) { pidController.setP(p); m_P = p; }
    if((i != m_I)) { pidController.setI(i); m_I = i; }
    if((d != m_D)) { pidController.setD(d); m_D = d; }
  }
}
