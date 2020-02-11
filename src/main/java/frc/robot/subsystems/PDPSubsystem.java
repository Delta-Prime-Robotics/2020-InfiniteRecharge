/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PDPSubsystem extends SubsystemBase {
  PowerDistributionPanel m_pdp = new PowerDistributionPanel(0);

  /**
   * Creates a new PDPSubsystem.
   */
  public PDPSubsystem() {
    ShuffleboardTab pdpTab = Shuffleboard.getTab("PDP");
    
    pdpTab.add(m_pdp);

    ShuffleboardLayout values = pdpTab.getLayout("PDP Values", BuiltInLayouts.kList);
    
    //values.addNumber("Voltage", () -> m_pdp.getVoltage());
    values.addNumber("Temperature", () -> m_pdp.getTemperature());
    //values.addNumber("Total Current", () -> m_pdp.getTotalCurrent());
    values.addNumber("Total Energy", () -> m_pdp.getTotalEnergy());
    values.addNumber("Total Power", () -> m_pdp.getTotalPower());

    ShuffleboardLayout graphs = pdpTab.getLayout("PDP Graphs", BuiltInLayouts.kList);
    //.withProperties(java.util.Map.of("Label position", "HIDDEN"));
    
    graphs.addNumber("Voltage", () -> m_pdp.getVoltage())
      .withWidget(BuiltInWidgets.kGraph);
    graphs.addNumber("Temperature", () -> m_pdp.getTemperature())
    .withWidget(BuiltInWidgets.kGraph);
    graphs.addNumber("Total Current", () -> m_pdp.getTotalCurrent())
    .withWidget(BuiltInWidgets.kGraph);
    graphs.addNumber("Total Energy", () -> m_pdp.getTotalEnergy())
    .withWidget(BuiltInWidgets.kGraph);
    graphs.addNumber("Total Power", () -> m_pdp.getTotalPower())
    .withWidget(BuiltInWidgets.kGraph);

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
