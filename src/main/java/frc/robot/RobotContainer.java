/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.commands.*;
import frc.robot.subsystems.*;

import static frc.robot.Constants.*;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Set to true when we're at a competition to cut down on info on Shuffleboard
  private final Boolean m_atCompetition = false;

  // OI controllers are defined here...
  private final Joystick m_gamePad = new Joystick(Laptop.UsbPorts.GamePad);
  private final Joystick m_maverick = new Joystick(Laptop.UsbPorts.Joystick);

  // The robot's subsystems and commands are defined here...
  private final PDPSubsystem m_pdpSubsystem = new PDPSubsystem();
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final CameraSubsystemGRIP m_cameraSubsystem = new CameraSubsystemGRIP();
  private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  //private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem(m_maverick);

  /**
   * The container for the robot. Pulls together subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    m_driveSubsystem.setMaxOutput(DriveConstants.kMaxDriveOutput);

    configureButtonBindings();
    
    configureDefaultCommands();
    
    setUpShuffleboard();
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
   
    // Reset drive system encoders
    new JoystickButton(m_gamePad, GamePad.Buttons.A)
       .whenPressed(() -> m_driveSubsystem.resetEncoders());

    // Reset drive system gyro
    new JoystickButton(m_gamePad, GamePad.Buttons.Y)
    .whenPressed(() -> m_driveSubsystem.zeroHeading());

    // Testing... Drive Straight
    new JoystickButton(m_maverick, 1)
      .whenHeld(new DriveStraightCommand(m_driveSubsystem, 
        () -> -m_gamePad.getRawAxis(JoystickConstants.Axis.FightFlight)));

    // Auto-Aim
    new JoystickButton(m_gamePad, GamePad.Buttons.B)
      .whenPressed(new AutoAimCommand(m_driveSubsystem, 
      m_cameraSubsystem)
      //.withTimeout(3)
      );

    // Move intake system forward
    new JoystickButton(m_gamePad, GamePad.Buttons.LT)
      .whenHeld(new RunCommand(() -> m_intakeSubsystem.recharge(), m_intakeSubsystem));

    // Move intake system backward
    new JoystickButton(m_gamePad, GamePad.Buttons.RT)
      .whenHeld(new RunCommand(() -> m_intakeSubsystem.discharge(), m_intakeSubsystem));
  }

  /**
   * Use this method to set the default commands for subsystems
   * Default commands can be explicit command classes, inline or use one of the
   * "convenience" subclasses of command (e.g. {@link edu.wpi.first.wpilibj2.command.InstantCommand})
   */
  private void configureDefaultCommands(){

    // Set Arcade Drive as the default
    m_driveSubsystem.setDefaultCommand(
      new ArcadeDriveCommand(m_driveSubsystem,
      () -> -m_maverick.getRawAxis(JoystickConstants.Axis.FightFlight),
      () -> m_maverick.getRawAxis(JoystickConstants.Axis.TurnNeck))
    );

    // Used to determine the minimum output needed for the robot to turn
    // m_driveSubsystem.setDefaultCommand(
    //   new RunCommand(() -> m_driveSubsystem.arcadeDrive(0, m_maverik.getRawAxis(JoystickConstants.Axis.Throttle)), m_driveSubsystem)
    // );

    // // Set Tank Drive as the default
    // m_driveSubsystem.setDefaultCommand(
    //   new TankDriveCommand(m_driveSubsystem, 
    //     () -> -m_gamePad.getRawAxis(GamePad.Axis.LeftStick.UpDown),
    //     () -> -m_gamePad.getRawAxis(GamePad.Axis.RightStick.UpDown))
    // );
  }

  /**
   * Sets up Shuffleboard, deferring to each subsystem to add their components
   */
  private void setUpShuffleboard() {
    // The main tab during teleop. Each subsystem may have its own tab too
    ShuffleboardTab teleopTab = Shuffleboard.getTab("Teleop");

    if (m_atCompetition) {
      Shuffleboard.selectTab("Teleop");
    }

    m_cameraSubsystem.setUpShuffleboard(teleopTab, m_atCompetition);
    m_driveSubsystem.setUpShuffleboard(teleopTab, m_atCompetition);
    m_intakeSubsystem.setUpShuffleboard(teleopTab, m_atCompetition);
    m_pdpSubsystem.setUpShuffleboard(teleopTab, m_atCompetition);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    //To do: replace with autonomous command or value from chooser if > 1
    return null;
  }
}
