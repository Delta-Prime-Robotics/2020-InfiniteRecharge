/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.subsystems.DriveSubsystem;

import static frc.robot.Constants.*;


public class TurnByAngleCommand extends PIDCommand {
  private DriveSubsystem m_drive;
  //private DoubleSupplier m_targetAngleDegrees;

  /**
   * Creates a new TurnByAngleCommand.
   * @param targetAngleDegrees The angle to turn to, in degrees
   * @param drive              The drive subsystem to use
   */
  public TurnByAngleCommand(DriveSubsystem drive, DoubleSupplier targetAngleDegrees) {
    super(
        // The controller that the command will use
        new PIDController(TurnByAngle.kTurnP, TurnByAngle.kTurnI, TurnByAngle.kTurnD), 
        // Close loop on heading
        drive::getHeading, 
        // Set reference to target
        targetAngleDegrees, 
        // Pipe output to turn robot
        (output) -> drive.autoTurn(output), 
        // Require the drive subsystem
        drive);

      m_drive = drive;
      //m_targetAngleDegrees = targetAngleDegrees;
      
      // Set the PID controller to be continuous (because it's an angle controller)
      getController().enableContinuousInput(-180, 180);

      // Set the controller tolerance - the delta tolerance ensures the robot is stationary at the
      // setpoint before it is considered as having reached the reference
      getController().setTolerance(TurnByAngle.kTurnToleranceDeg, TurnByAngle.kTurnRateToleranceDegPerS);
      
    // Use addRequirements() here to declare subsystem dependencies.
      addRequirements(m_drive);
  }

  @Override
  public void initialize() {
    super.initialize();
    //getController().setSetpoint(m_drive.getHeading() + m_targetAngleDegrees.getAsDouble());
    SmartDashboard.putNumber("TurnByAngle-init-heading", m_drive.getHeading());
    SmartDashboard.putNumber("TurnByAngle-init-setpoint", getController().getSetpoint());
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    SmartDashboard.putNumber("TurnByAngle-final-heading", m_drive.getHeading());
    SmartDashboard.putNumber("TurnByAngle-final-setpoint", getController().getSetpoint());
    return getController().atSetpoint();
  }
}
