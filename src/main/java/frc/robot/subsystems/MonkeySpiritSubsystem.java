/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;

public class MonkeySpiritSubsystem extends SubsystemBase {
  private VictorSP m_motor = new VictorSP(RoboRio.PwmPorts.ClimberMotor);
  
  /**
   * Creates a new MonkeySpiritSubsystem.
   */
  public MonkeySpiritSubsystem() {

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void Climb() {
    this.m_motor.setSpeed(.5);
  }
}
