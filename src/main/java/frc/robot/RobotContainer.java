/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.GamePad;
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
  private final CameraSubsystemRPi m_cameraSubsystem = new CameraSubsystemRPi();
  private final ColorWheelSubsystem m_colorWheelSubsystem = new ColorWheelSubsystem();
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  //private final PDPSubsystem m_pdpSubsystem = new PDPSubsystem();
  private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem(); //m_maverick);
  private final MonkeySpiritSubsystem m_monkeySpiritSubsystem = new MonkeySpiritSubsystem();

  SendableChooser<Command> m_autonomousChooser = new SendableChooser<>();

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
   
    // Drive Straight
    new JoystickButton(m_maverick, 1)
      .whenHeld(new DriveStraightCommand(m_driveSubsystem, 
        () -> -m_gamePad.getRawAxis(JoystickConstants.Axis.FightFlight)
      )
    );

    // Reset drive system encoders
    new JoystickButton(m_gamePad, GamePad.Buttons.RB)
      .whenPressed(() -> m_driveSubsystem.resetEncoders()
    );

    // Reset drive system gyro
    new JoystickButton(m_gamePad, GamePad.Buttons.LB)
      .whenPressed(() -> m_driveSubsystem.zeroHeading()
    );

    // Auto-Aim
    new JoystickButton(m_gamePad, GamePad.Buttons.B)
      .whenPressed(new AutoAimCommand(m_driveSubsystem, m_cameraSubsystem)
      .withTimeout(3)
    );

    // Turn the LED Ring On
    new JoystickButton(m_maverick, 2)
      .whenPressed(new InstantCommand(() -> m_cameraSubsystem.toggleLight())
    );

    // testing... POV buttons
    new POVButton(m_gamePad, 180)
      .whenPressed(new InstantCommand(()->m_shooterSubsystem.stop())
    );
    new POVButton(m_gamePad, 90).whenPressed(new InstantCommand(() -> m_shooterSubsystem.setRPM(2000)));
    new POVButton(m_gamePad, 0).whenPressed(new InstantCommand(() -> m_shooterSubsystem.setRPM(4000)));
    new POVButton(m_gamePad, 270).whenPressed(new PrintCommand("POV 270"));

    // testing... driving to distance via encoders
    new JoystickButton(m_gamePad, GamePad.Buttons.Start)
      .whenPressed(new DriveNoHandsCommand(m_driveSubsystem, 30)
      .withTimeout(0.5)
    );

    // Run the climber
    new JoystickButton(m_gamePad, GamePad.Buttons.A)
      .whenHeld(new RunCommand(() -> m_monkeySpiritSubsystem.Climb(), m_monkeySpiritSubsystem)
    );

    // Reset the counter for the color wheel
    new JoystickButton(m_gamePad, GamePad.Buttons.X)
      .whenPressed(new InstantCommand(() -> m_colorWheelSubsystem.startCounting())
    );
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

    m_intakeSubsystem.setDefaultCommand(
      new RunCommand(()->m_intakeSubsystem.setSpeed(m_gamePad.getRawAxis(GamePad.Axis.LeftStickUpDown)), 
      m_intakeSubsystem)
    );
    
    m_colorWheelSubsystem.setDefaultCommand(
      new RunCommand(()->m_colorWheelSubsystem.lightTravel(m_gamePad.getRawAxis(GamePad.Axis.RightStickUpDown)), 
      m_colorWheelSubsystem)
    );

    // Used to determine the minimum output needed for the robot to turn
    // m_driveSubsystem.setDefaultCommand(
    //   new RunCommand(() -> m_driveSubsystem.arcadeDrive(0, m_maverik.getRawAxis(JoystickConstants.Axis.Throttle)), m_driveSubsystem)
    // );
  }

  /**
   * Sets up Shuffleboard, deferring to each subsystem to add their components
   */
  private void setUpShuffleboard() {
    setUpAutonomousChooser();

    m_cameraSubsystem.setUpShuffleboard(m_atCompetition);
    m_colorWheelSubsystem.setUpShuffleboard(m_atCompetition);
    m_driveSubsystem.setUpShuffleboard(m_atCompetition);
    m_intakeSubsystem.setUpShuffleboard(m_atCompetition);
    //m_pdpSubsystem.setUpShuffleboard(m_atCompetition);
    m_shooterSubsystem.setUpShuffleboard(m_atCompetition);
  }

  /**
   * Set up the Chooser on the 
   */
  private void setUpAutonomousChooser() {
    Command planCDistance = new DriveNoHandsCommand(m_driveSubsystem, 30)
      .withTimeout(0.5);

    Command planCTimeout = new StartEndCommand(
        () -> m_driveSubsystem.arcadeDrive(-0.6, 0), 
        () -> m_driveSubsystem.stop(),
        m_driveSubsystem)
      .withTimeout(1.0);

    Command combinedTest = 
      new SequentialCommandGroup(
        new ParallelDeadlineGroup(
          new WaitCommand(4),
          new RunCommand(() -> m_shooterSubsystem.setRPM(400)),
          new SequentialCommandGroup(
            new WaitCommand(3),
            new InstantCommand(() -> m_cameraSubsystem.lightOn())
          )
        ),
        new InstantCommand(() -> m_shooterSubsystem.stop()),
        new InstantCommand(() -> m_cameraSubsystem.lightOff())
      )      ;
    
    m_autonomousChooser.setDefaultOption("Plan C - Distance", planCDistance);
    m_autonomousChooser.addOption("Plan C - Timeout", planCTimeout);
    m_autonomousChooser.addOption("Do Nothing", null);
    m_autonomousChooser.addOption("Combined", combinedTest);
    SmartDashboard.putData("Autonomous", m_autonomousChooser);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_autonomousChooser.getSelected();
  }
}
