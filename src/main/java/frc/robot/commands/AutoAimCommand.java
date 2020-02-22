/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.subsystems.*;

import static frc.robot.Constants.*;

/**
 * 
 */
public class AutoAimCommand extends PIDCommand {
  private DriveSubsystem m_drive;
  private CameraSubsystemGRIP m_cameras;
  /**
   * Creates a new AutoAimCommand.
   */
  public AutoAimCommand(DriveSubsystem drive, CameraSubsystemGRIP cameras) {
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
    // Use addRequirements() here to declare subsystem dependencies.
    // Configure additional PID options by calling `getController` here.
    
    // Don't set the PID controller to be continuous since the output is in pixels, not degrees
    //getController().enableContinuousInput(-180, 180);

    // Set the controller tolerance - the delta tolerance ensures the robot is stationary at the
    // setpoint before it is considered as having reached the reference
    getController().setTolerance(AutoAim.kTurnTolerancePixel, AutoAim.kTurnRateTolerancePxlPerS);

    m_drive = drive;
    m_cameras = cameras;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_drive);
    addRequirements(m_cameras);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
