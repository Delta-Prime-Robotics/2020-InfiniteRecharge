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
import frc.robot.subsystems.DriveSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class DriveNoHandsCommand extends PIDCommand {
  private DriveSubsystem m_drive;
  private int m_initializeCount;

  /**
   * Creates a new DriveNOHands.
   */
  public DriveNoHandsCommand(DriveSubsystem drive, double distance) {
    super(
        // The controller that the command will use
        new PIDController(0.1, 0, 0),
        // This should return the measurement
        () -> drive.getAverageEncoderDistance(),
        // This should return the setpoint (can also be a constant)
        () -> distance,
        // This uses the output
        output -> {
          // Use the output here
          drive.driveStraight(output);
        });
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drive);
    m_drive = drive;
    // Configure additional PID options by calling `getController` here.
  }

  @Override
  public void initialize() {
    super.initialize();
    m_drive.resetEncoders();

    // temporary. To see when initialize is called
    m_initializeCount++;
    SmartDashboard.putNumber("DriveNoHandsInit", m_initializeCount);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
