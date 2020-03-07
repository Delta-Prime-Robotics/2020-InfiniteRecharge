/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

/**
 * Camera subsystem for processing Vision on the Driver Station (w/GRIP)
 */
public class CameraSubsystemRPi extends SubsystemBase {
  @SuppressWarnings("unused") //The intake camera is just sending a stream. It doesn't need to be "used" in the code
  private UsbCamera m_intakeCam;

  private NetworkTable m_piData;

  private Solenoid m_lightSwitch = new Solenoid(RoboRio.CanIDs.PCM, VisionConstants.kLightSwitch);

  /**
   * Creates a new CameraSubsystem.
   */
  public CameraSubsystemRPi() {
    try {
      m_intakeCam = CameraServer.getInstance().startAutomaticCapture("ShooterCam", 0);
      //m_intakeCam.setResolution(VisionConstants.kImageWidth, VisionConstants.kImageHeight);
    }
    catch (Exception ex) {
      DriverStation.reportError("Error instantiating USB Camera 0" + ex.getMessage(), true);
    }

    m_piData = NetworkTableInstance.getDefault().getTable(VisionConstants.kGripNT);
  }

  // Shuffleboard Network Table Entries (for updating values) 
  private static class SBNTE {
    public static NetworkTableEntry targetStatus;
    public static NetworkTableEntry targetCount;
    public static NetworkTableEntry centerX;
    public static NetworkTableEntry centerY;
    public static NetworkTableEntry offsetX;
    public static NetworkTableEntry offsetY;
    public static NetworkTableEntry angleX;
    public static NetworkTableEntry angleY;
  }

  /**
   * Sets up Shuffleboard for this subsystem
   * @param atCompetition Whether to exclude testing info from Shuffleboard
   */
  public void setUpShuffleboard(Boolean atCompetition) {

    if (!atCompetition) {
      ShuffleboardTab visionTab = Shuffleboard.getTab("Vision");

      ShuffleboardLayout targetInfo = visionTab.getLayout("Target Info", BuiltInLayouts.kList);
      
      SBNTE.targetStatus = targetInfo.add("Status", "Initializing...")
        .getEntry();
      SBNTE.centerX = targetInfo.add("Target Count", 0.0)
        .getEntry();
      SBNTE.centerX = targetInfo.add("Center X", 0.0)
        .getEntry();
      SBNTE.centerY = targetInfo.add("Center Y", 0.0)
        .getEntry();
      SBNTE.offsetX = targetInfo.add("Offset X", 0.0)
        .getEntry();
      SBNTE.offsetY = targetInfo.add("Offset Y", 0.0)
        .getEntry();
      SBNTE.angleX = targetInfo.add("Angle X", 0.0)
        .getEntry();
      SBNTE.angleY = targetInfo.add("Angle Y", 0.0)
        .getEntry();
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    
    if (SBNTE.targetStatus != null) {
      Number targetCount = m_piData.getEntry("ct").getNumber(0);
      putTargetStatus(targetCount.intValue());
      SBNTE.targetCount.setNumber(targetCount);
      SBNTE.centerX.setNumber(m_piData.getEntry("cx").getNumber(0));
      SBNTE.centerY.setNumber(m_piData.getEntry("cy").getNumber(0));
      SBNTE.offsetX.setNumber(m_piData.getEntry("nx").getNumber(0));
      SBNTE.offsetY.setNumber(m_piData.getEntry("ny").getNumber(0));
      SBNTE.angleX.setNumber(m_piData.getEntry("tx").getNumber(0));
      SBNTE.angleY.setNumber(m_piData.getEntry("ty").getNumber(0));
    }
  }

  public void lightOn() {
    m_lightSwitch.set(true);
  }
  public void lightOff() {
    m_lightSwitch.set(false);
  }
  public void toggleLight() {
    m_lightSwitch.set(!m_lightSwitch.get());
  }

  private void putTargetStatus(Integer targetCount) {
    String status = "";
    switch (targetCount) {
      case 0: status = "No target found"; break;
      case 1: status = "Target found"; break;
      default: status = Integer.toString(targetCount) + " targets found"; break;
    }      
    SBNTE.targetStatus.setString(status);
  }

  public double getOffsetX() {
    return m_piData.getEntry("nx").getDouble(0);
  }
}
