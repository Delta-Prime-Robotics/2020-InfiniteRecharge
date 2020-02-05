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
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.vision.*;

public class CameraSubsystem extends SubsystemBase {
  private UsbCamera m_shooterCam;

  private final Object m_visionLock = new Object();
  private VisionThread m_visionThread;
  private CvSource m_outputStream;
  
  private final Scalar kContourColor = new Scalar(255,0,255);//new Scalar(70,200,150);
  
  private boolean m_suspendProcessing;
  private boolean m_isProcessingSuspended;
  
  /**
   * Creates a new CameraSubsystem.
   */
  public CameraSubsystem() {
    UsbCamera m_shooterCam = CameraServer.getInstance().startAutomaticCapture("ShooterCam", 0);

    m_outputStream = CameraServer.getInstance().putVideo("VisionCam", 320, 240);

    startVisionThread();
  }

  private void startVisionThread() {
    m_visionThread = new VisionThread(m_shooterCam, new PowerPortPipeline(), pipeline -> {
      // Default to the resized image
      Mat outputImg = pipeline.resizeImageOutput();

      if (!pipeline.isProcessingSuspended()) {
        ArrayList<MatOfPoint> contours = pipeline.filterContoursOutput();
        if (!pipeline.filterContoursOutput().isEmpty()) {
          
          Imgproc.drawContours(outputImg, contours, -1, kContourColor);
        }
      }

      // Synchronize the threads before accessing any class variables
      synchronized (m_visionLock) {
        m_outputStream.putFrame(outputImg);

        // Pass along the setting for whether to suspend processing images
        pipeline.suspendProcessing(m_suspendProcessing);
        m_isProcessingSuspended = pipeline.isProcessingSuspended();
      }
    });
    m_visionThread.start();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putBoolean("Vision Has Been Suspended", m_isProcessingSuspended);
    SmartDashboard.putBoolean("Vision Will Be Suspended", m_suspendProcessing);
  }

  /**
   * Suspend vision processing (other than resizing the image)
   */
  public void SuspendVisionProcessing() {
    m_suspendProcessing = true;
  }

  /**
   * Resume vision procesing
   */
  public void ResumeVisionProcessing() {
    m_suspendProcessing = false;
  }

  /**
   * Toggle vision processing
   */
  public void ToggleVisionProcessing() {
    m_suspendProcessing = !m_suspendProcessing;
  }
}
