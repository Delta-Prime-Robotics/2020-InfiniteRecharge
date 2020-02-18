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
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

/**
 * Camera subsystem for processing Vision on the Driver Station (w/GRIP)
 */
public class CameraSubsystemGRIP extends SubsystemBase {
  private UsbCamera m_shooterCam;

  private NetworkTable m_gripData;

  private final double[] emptyArray = new double[0];
  

  /**
   * Creates a new CameraSubsystem.
   */
  public CameraSubsystemGRIP() {
    try {
      m_shooterCam = CameraServer.getInstance().startAutomaticCapture("ShooterCam", 0);
      m_shooterCam.setResolution(VisionConstants.kImageWidth, VisionConstants.kImageHeight);
    }
    catch (Exception ex) {
      DriverStation.reportError("Error instantiating USB Camera 0" + ex.getMessage(), true);
    }

    m_gripData = NetworkTableInstance.getDefault().getTable(VisionConstants.kGripNT);
  }

  // Shuffleboard Network Table Entries (for updating values) 
  private static class SBNTE {
    public static NetworkTableEntry targetStatus;
    public static NetworkTableEntry centerX;
    public static NetworkTableEntry centerY;
    public static NetworkTableEntry offsetX;
    public static NetworkTableEntry offsetY;
  }

  /**
   * Sets up Shuffleboard for this subsystem
   * @param teleopTab The main tab used during teleop
   * @param atCompetition Whether to exclude testing info from Shuffleboard
   */
  public void setUpShuffleboard(ShuffleboardTab teleopTab, Boolean atCompetition) {

    if (!atCompetition) {
      ShuffleboardTab visionTab = Shuffleboard.getTab("Vision");

      ShuffleboardLayout targetInfo = visionTab.getLayout("Target Info", BuiltInLayouts.kList);
      
      SBNTE.targetStatus = targetInfo.add("Status", "Initializing...")
        .getEntry();
      SBNTE.centerX = targetInfo.add("Center X", 0.0)
        .getEntry();
      SBNTE.centerY = targetInfo.add("Center Y", 0.0)
        .getEntry();
      SBNTE.offsetX = targetInfo.add("Offset X", 0.0)
        .getEntry();
      SBNTE.offsetY = targetInfo.add("Offset Y", 0.0)
        .getEntry();
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    
    if (SBNTE.targetStatus != null) {
      getOffsetX(true);
      getOffsetY(true);
    }
  }

  /**
   * Get the horizontal offset from the center of the target to the center of the camera
   * @return the offset in pixels
   */
  public double getOffsetX() {
    return getOffsetX(false);
  }
  
  /**
   * Get the vertical offset from the center of the target to the vertical position of the goal range
   * @return the offset in pixels
   */
  public double getOffsetY() {
    return getOffsetY(false);
  }


  /**
   * Get the horizontal offset from the center of the target to the center of the camera
   * @param logging whether this method should log data to Shuffleboard
   * @return the offset in pixels
   */
  private double getOffsetX(Boolean logging) {
    double offsetX = 0;
    double centerX = getCenter(VisionConstants.kCenterXKey, logging);

    if (centerX >= 0) {
      offsetX = centerX - VisionConstants.kGoalX;
    }

    if (logging) {
      SBNTE.centerX.setDouble(centerX);
      SBNTE.offsetX.setDouble(offsetX);
    }

    return offsetX;
  }
  
  /**
   * Get the vertical offset from the center of the target to the vertical position of the goal range
   * @param logging whether to log status information to Shuffleboard
   * @return the offset in pixels
   */
  public double getOffsetY(Boolean logging) {
    double offsetY = 0;
    double centerY = getCenter(VisionConstants.kCenterYKey, false); // Assumes centerX already logged the target status

    if (centerY >= 0) {
      offsetY = centerY - VisionConstants.kGoalY;
    }

    if (logging) {
      SBNTE.centerY.setDouble(centerY);
      SBNTE.offsetY.setDouble(offsetY);
    }

    return offsetY;
  }

  /**
   * Get a single center value from the GRIP network table
   * @param ntKey the network table key for the array data ("centerX" or "centerY")
   * @param logTargetStatus whether to log the target status to Shuffleboard
   * @return the center of the target in pixels
   */
  private double getCenter(String ntKey, Boolean logTargetStatus) {
    double center = -1;

    double centers[] = m_gripData.getEntry(ntKey).getDoubleArray(emptyArray);
    int targetCount = centers.length;

    if (targetCount == 1) {
      center = centers[0];
    }

    if (logTargetStatus) {
      String status = "";
      switch (targetCount) {
        case 0: status = "No target found"; break;
        case 1: status = "Target found"; break;
        default: status = Integer.toString(targetCount) + " targets found"; break;
      }      
      SBNTE.targetStatus.setString(status);
    }

    return center;
  }
}
