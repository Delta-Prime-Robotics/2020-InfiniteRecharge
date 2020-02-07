/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

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
  // The robot's subsystems and commands are defined here...
  @SuppressWarnings("unused") //The PDP subsystem is intentionally unused, it just displays PDP data
  private final PDPSubsystem m_pdpSubsystem = new PDPSubsystem();
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  //private final CameraSubsystem m_cameraSubsystem = new CameraSubsystem();

  // OI controllers are defined here...
  private final Joystick m_gamePad = new Joystick(Laptop.UsbPorts.GamePad);


  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
    // Configure default commands
    configureDefaultCommands();
    // Set up Shuffleboard for the robot
    setUpShuffleboard();

    // Remove this once we hook up the CameraSubsystem
    CameraServer.getInstance().startAutomaticCapture();
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(m_gamePad, GamePad.Buttons.A)
      .whenPressed(() -> m_driveSubsystem.resetEncoders());
  }

  /**
   * Use this method to set the default commands for subsystems
   * Default commands can be explicit command classes, inline or use one of the
   * "convenience" subclasses of command (e.g. {@link edu.wpi.first.wpilibj2.command.InstantCommand})
   */
  private void configureDefaultCommands(){

    // Set Arcade Drive as default
    m_driveSubsystem.setDefaultCommand(
      new ArcadeDriveCommand(m_driveSubsystem, 
        () -> -m_gamePad.getRawAxis(GamePad.Axis.RightStick.UpDown),
        () -> m_gamePad.getRawAxis(GamePad.Axis.RightStick.LeftRight))
    );

    // // Set Tank Drive as default
    // m_driveSubsystem.setDefaultCommand(
    //   new TankDriveCommand(m_driveSubsystem, 
    //     () -> -m_gamePad.getRawAxis(GamePad.Axis.LeftStick.UpDown),
    //     () -> -m_gamePad.getRawAxis(GamePad.Axis.RightStick.UpDown))
    // );
  }

  private void setUpShuffleboard() {
    // The main tab while driving. Each subsystem may have its own tab too
    ShuffleboardTab drivingTab = Shuffleboard.getTab("Driving");
    Shuffleboard.selectTab("Driving");

    m_driveSubsystem.setUpShuffleboard(drivingTab);

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null;
  }
}
