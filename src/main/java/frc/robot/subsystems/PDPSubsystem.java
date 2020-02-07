/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
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

    ShuffleboardLayout values = pdpTab.getLayout("PDP Values");
    
    values.addNumber("Voltage", () -> m_pdp.getVoltage());
    values.addNumber("Temperature", () -> m_pdp.getTemperature());
    values.addNumber("Total Current", () -> m_pdp.getTotalCurrent());
    values.addNumber("Total Energy", () -> m_pdp.getTotalEnergy());
    values.addNumber("Total Power", () -> m_pdp.getTotalPower());

    ShuffleboardLayout graphs = pdpTab.getLayout("PDP Graphs");
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

    ShuffleboardLayout channels = pdpTab.getLayout("PDP Channels");
      //.withProperties(java.util.Map.of("Label position", "HIDDEN"));
    
    channels.addNumber("0", () -> m_pdp.getCurrent(0));
    channels.addNumber("1", () -> m_pdp.getCurrent(1));
    channels.addNumber("2", () -> m_pdp.getCurrent(2));
    channels.addNumber("3", () -> m_pdp.getCurrent(3));
    channels.addNumber("4", () -> m_pdp.getCurrent(4));
    channels.addNumber("5", () -> m_pdp.getCurrent(5));
    channels.addNumber("6", () -> m_pdp.getCurrent(6));
    channels.addNumber("7", () -> m_pdp.getCurrent(7));
    channels.addNumber("8", () -> m_pdp.getCurrent(8));
    channels.addNumber("9", () -> m_pdp.getCurrent(9));
    channels.addNumber("10", () -> m_pdp.getCurrent(10));
    channels.addNumber("11", () -> m_pdp.getCurrent(11));
    channels.addNumber("12", () -> m_pdp.getCurrent(12));
    channels.addNumber("13", () -> m_pdp.getCurrent(13));
    channels.addNumber("14", () -> m_pdp.getCurrent(14));
    channels.addNumber("15", () -> m_pdp.getCurrent(15));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
