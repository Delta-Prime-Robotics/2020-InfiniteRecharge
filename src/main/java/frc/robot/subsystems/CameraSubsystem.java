/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.vision.*;

public class CameraSubsystem extends SubsystemBase {
  private UsbCamera m_shooterCam;

  private final Object m_visionLock = new Object();
  private VisionThread m_visionThread;
  private CvSource m_outputStream;
  
  private final Scalar kContourColor = new Scalar(255,0,255);//new Scalar(70,200,150);
  private final Scalar kRotRectColor = new Scalar(0,255,0);//new Scalar(70,200,150);

  @SuppressWarnings("unused") //This variable is used in the VisionThread listener lambda, the compiler is wrong
  private int m_targetCount;
  
  private boolean m_suspendProcessing;

  private NetworkTable m_gripData;
  
  /**
   * Creates a new CameraSubsystem.
   */
  public CameraSubsystem() {
    try {
      m_shooterCam = CameraServer.getInstance().startAutomaticCapture("ShooterCam", 0);
      m_shooterCam.setResolution(320, 240);
    }
    catch (Exception ex) {
      DriverStation.reportError("Error instantiating USB Camera 0" + ex.getMessage(), true);
    }

    m_gripData = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");

    //m_outputStream = CameraServer.getInstance().putVideo("Gandalf", 320, 240);
    
    m_suspendProcessing = true;
    //startVisionThread();
  }

  private void startVisionThread() {
    // Bail if the USB camera isn't connected
    if (m_shooterCam == null) return;

    m_visionThread = new VisionThread(m_shooterCam, new PowerPortPipeline(), pipeline -> {
      // Default to the resized image
      Mat outputImg = pipeline.resizeImageOutput();

      int targetCount = 0;
      String targetMessage = "Processing suspended";
      Point center = new Point(0,0);
      Point offset = new Point(0,0);

      if (!pipeline.isProcessingSuspended()) {
        ArrayList<MatOfPoint> contours = pipeline.filterContoursOutput();

        targetCount  = contours.size();
        if (targetCount > 0) {
          // Draw the contours
          Imgproc.drawContours(outputImg, contours, -1, kContourColor);

          if (targetCount == 1) {
            targetMessage = "Target found";
          
            MatOfPoint contour = contours.get(0);

            // Get the coordinates to the center of the contour
            center = pipeline.findCenter(contour);
            // Get the offset from the center of the image to the center of the contour
            offset = pipeline.findOffset(center);
  
            //Draw a rotated rectangle around the target
            RotatedRect rotRect = pipeline.findMinAreaRect(contour);
            Point[] vertices = new Point[4];
            rotRect.points(vertices);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(outputImg, vertices[i], vertices[(i+1)%4], kRotRectColor, 2);
            }
          }
          else { // targetCount > 1
            targetMessage = Integer.toString(targetCount) + " targets found";
          }
        }
        else { // targetCount <= 0
          targetMessage = "Target not found";
        }
      }

      // Synchronize the threads before accessing any class variables
      synchronized (m_visionLock) {
        m_targetCount = targetCount;

        SBNTE.targetStatus.setString(targetMessage);
        SBNTE.centerX.setDouble(center.x);
        SBNTE.centerY.setDouble(center.y);
        SBNTE.offsetX.setDouble(offset.x);
        SBNTE.offsetY.setDouble(offset.y);

        // SmartDashboard.putString("Target Status", targetMessage);
        // SmartDashboard.putNumber("Center of Target X", center.x);
        // SmartDashboard.putNumber("Center of Target Y", center.y);
        // SmartDashboard.putNumber("Offset to Target X", offset.x);
        // SmartDashboard.putNumber("Offset to Target Y", offset.y);

        m_outputStream.putFrame(outputImg);

        // Pass along the setting for whether to suspend processing images
        pipeline.suspendProcessing(m_suspendProcessing);
        SBNTE.isSuspended.setBoolean(pipeline.isProcessingSuspended());
        //SmartDashboard.putBoolean("Vision Has Been Suspended", pipeline.isProcessingSuspended());
      }
    });
    m_visionThread.start();
  }


  private static class SBNTE {
    public static NetworkTableEntry targetStatus;
    public static NetworkTableEntry centerX;
    public static NetworkTableEntry centerY;
    public static NetworkTableEntry offsetX;
    public static NetworkTableEntry offsetY;
    public static NetworkTableEntry isSuspended;
    public static NetworkTableEntry toBeSuspended;

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
      
      ShuffleboardLayout procInfo = visionTab.getLayout("Processing Info", BuiltInLayouts.kList);
        
      SBNTE.isSuspended = procInfo.add("Is Suspended", false)
        .withWidget(BuiltInWidgets.kBooleanBox)
        .getEntry();
      
      SBNTE.toBeSuspended = procInfo.add("To Be Suspended", false)
        .withWidget(BuiltInWidgets.kBooleanBox)
        .getEntry();

      procInfo.add("Toggle Processing", new InstantCommand(this::toggleVisionProcessing, this));
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SBNTE.toBeSuspended.setBoolean(m_suspendProcessing);
    //SmartDashboard.putBoolean("Vision Will Be Suspended", m_suspendProcessing);
    displayGripData();
  }

  private double[] defaultArray = new double[0];
  private void displayGripData() {
    double centersX[] = m_gripData.getEntry("centerX").getDoubleArray(defaultArray);

    double centerX = 0;

    if (centersX.length == 0) {
      SBNTE.targetStatus.setString("No target found");
    }
    else if (centersX.length > 1) {
      SBNTE.targetStatus.setString("Many targets found");
    }
    else {
      SBNTE.targetStatus.setString("One target found");
      centerX = centersX[0] - 160;
    }

    SBNTE.centerX.setDouble(centerX);
    

  }

  /**
   * Suspend vision processing (other than resizing the image)
   */
  public void suspendVisionProcessing() {
    m_suspendProcessing = true;
  }

  /**
   * Resume vision procesing
   */
  public void resumeVisionProcessing() {
    m_suspendProcessing = false;
  }

  /**
   * Toggle vision processing
   */
  public void toggleVisionProcessing() {
    m_suspendProcessing = !m_suspendProcessing;
  }
}
