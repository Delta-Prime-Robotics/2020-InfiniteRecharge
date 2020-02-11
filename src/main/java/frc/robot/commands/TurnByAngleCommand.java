/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.ProfiledPIDCommand;
import frc.robot.subsystems.DriveSubsystem;

import static frc.robot.Constants.*;

public class TurnByAngleCommand extends ProfiledPIDCommand {
  /**
   * Creates a new TurnByAngleCommand.
   * @param targetAngleDegrees The angle to turn to, in degrees
   * @param drive              The drive subsystem to use
   */
  public TurnByAngleCommand(DriveSubsystem drive, double targetAngleDegrees) {
    super(
        // The controller that the command will use
        new ProfiledPIDController(DriveConstants.kTurnP, DriveConstants.kTurnI, 
        DriveConstants.kTurnD, new TrapezoidProfile.Constraints(
          DriveConstants.kMaxTurnRateDegPerS,
          DriveConstants.kMaxTurnAccelDegPerSSquared
        )), 
        // Close loop on heading
        drive::getHeading, 
        // Set reference to target
        targetAngleDegrees, 
        // Pipe output to turn robot
        (output, setpoint) -> drive.arcadeDrive(0, output), 
        // Require the drive subsystem
        drive);

      // Set the PID controller to be continuous (because it's an angle controller)
      getController().enableContinuousInput(-180, 180);

      // Set the controller tollerance - the delta tolerance ensures the robot is stationary at the
      // setpoint before it is considered as having reached the reference
      getController().setTolerance(DriveConstants.kTurnToleranceDeg, DriveConstants.kTurnRateToleranceDegPerS);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return getController().atGoal();
  }
}
