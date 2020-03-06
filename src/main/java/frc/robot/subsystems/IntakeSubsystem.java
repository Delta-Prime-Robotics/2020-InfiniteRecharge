/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;


public class IntakeSubsystem extends SubsystemBase {

  private VictorSP m_mControl = new VictorSP(RoboRio.PwmPorts.IntakeMotor);
  /**
   * Creates a new IntakeSubsystem.
   */
  public IntakeSubsystem() {
  
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
    // This method will be called once per scheduler run
  }

  public void recharge() {
    m_mControl.setSpeed(-0.5);
  }

  public void discharge() {
    m_mControl.setSpeed(0.5);
  }

  public void setSpeed(double speed){
    m_mControl.setSpeed(speed);

  }

  public void stop() {
    m_mControl.setSpeed(0);
  }
}
