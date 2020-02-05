/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.vision.VisionPipeline;

/**
 * Vision Pipeline for finding the upper Power Port target for Infinite Recharge (2020)
 */
public class PowerPortPipeline implements VisionPipeline {
  //Processing Constants
  private static class VisionConstants {
    private static final double ImageWidth = 320.0;
    private static final double ImageHeight = 240.0;

    private static final double[] HsvThresholdHue = {66.0, 100.0};
    private static final double[] HsvThresholdSaturation = {66.0, 240.0};
    private static final double[] HsvThresholdValue = {123.0, 255.0};

    private static final double FilterContoursMinArea = 20.0;
		private static final double FilterContoursMinPerimeter = 20.0;
		private static final double FilterContoursMinWidth = 20.0;
		private static final double FilterContoursMinHeight = 20.0;
		private static final double[] FilterContoursSolidity = {0, 60.0};
		private static final double FilterContoursMinVertices = 0.0;
		private static final double FilterContoursMinRatio = 0.0;
  }
  
  private boolean m_suspendProcessing;

  //Outputs
  private Mat m_resizeImageOutput = new Mat();  
  private Mat m_hsvThresholdOutput = new Mat();
  private ArrayList<MatOfPoint> m_findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> m_filterContoursOutput = new ArrayList<MatOfPoint>();
  
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  /**
   * This is the primary method that runs the entire pipeline and updates the outputs.
   */
  @Override public void process(Mat source0) {
    // Step Resize_Image0:
    Mat resizeImageInput = source0;
    double resizeImageWidth = VisionConstants.ImageWidth;
    double resizeImageHeight = VisionConstants.ImageHeight;
    int resizeImageInterpolation = Imgproc.INTER_LINEAR;
    resizeImage(resizeImageInput, resizeImageWidth, resizeImageHeight, resizeImageInterpolation, m_resizeImageOutput);

    if (m_suspendProcessing) {
      // Don't waste cycles trying to locate the target if we don't need/expect one
      m_hsvThresholdOutput = m_resizeImageOutput;
      m_findContoursOutput.clear();
      m_filterContoursOutput.clear();
      return;
    }

    // Step HSV_Threshold0:
    Mat hsvThresholdInput = m_resizeImageOutput;
    double[] hsvThresholdHue = VisionConstants.HsvThresholdHue;
    double[] hsvThresholdSaturation = VisionConstants.HsvThresholdSaturation;
    double[] hsvThresholdValue = VisionConstants.HsvThresholdValue;
    hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, m_hsvThresholdOutput);

		// Step Find_Contours0:
		Mat findContoursInput = m_hsvThresholdOutput;
		boolean findContoursExternalOnly = false;
    findContours(findContoursInput, findContoursExternalOnly, m_findContoursOutput); 
    
    		// Step Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursInput = m_findContoursOutput;
		double filterContoursMinArea = VisionConstants.FilterContoursMinArea;
		double filterContoursMinPerimeter = VisionConstants.FilterContoursMinPerimeter;
		double filterContoursMinWidth = VisionConstants.FilterContoursMinWidth;
		double filterContoursMinHeight = VisionConstants.FilterContoursMinHeight;
		double[] filterContoursSolidity = VisionConstants.FilterContoursSolidity;
		double filterContoursMinVertices = VisionConstants.FilterContoursMinVertices;
		double filterContoursMinRatio = VisionConstants.FilterContoursMinRatio;
    filterContours(filterContoursInput, 
      filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMinHeight, 
      filterContoursSolidity, filterContoursMinVertices, filterContoursMinRatio, 
      m_filterContoursOutput);
  }

  /**
   * Returns whether processing has been suspended
   * @return
   */
  public boolean isProcessingSuspended() {
    return m_suspendProcessing;
  }

  /**
   * Set whether to suspend processing of the images (other than resizing)
   */
  public void suspendProcessing(boolean setting) {
    m_suspendProcessing = setting;
  }
  
  /**
   * This method is a generated getter for the output of a Resize_Image.
   * @return Mat output from Resize_Image.
   */
  public Mat resizeImageOutput() {
    return m_resizeImageOutput;
  }

  /**
	 * This method is a generated getter for the output of a Filter_Contours.
	 * @return ArrayList<MatOfPoint> output from Filter_Contours.
	 */
	public ArrayList<MatOfPoint> filterContoursOutput() {
		return m_filterContoursOutput;
	}
  

  /**
   * Scales and image to an exact size.
   * @param input The image on which to perform the Resize.
   * @param width The width of the output in pixels.
   * @param height The height of the output in pixels.
   * @param interpolation The type of interpolation.
   * @param output The image in which to store the output.
   */
  private void resizeImage(Mat input, double width, double height, int interpolation, Mat output) {
    Imgproc.resize(input, output, new Size(width, height), 0.0, 0.0, interpolation);
  }

  /**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param val The min and max value
	 * @param output The image in which to store the output.
	 */
	private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val, Mat output) {
    Imgproc.cvtColor(input, output, Imgproc.COLOR_BGR2HSV);
    Core.inRange(output, new Scalar(hue[0], sat[0], val[0]),
                         new Scalar(hue[1], sat[1], val[1]), output);
  }

  /**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {
    Mat hierarchy = new Mat();
    contours.clear();

    int mode;
    if (externalOnly) {
      mode = Imgproc.RETR_EXTERNAL;
    }
    else {
      mode = Imgproc.RETR_LIST;
    }

    int method = Imgproc.CHAIN_APPROX_SIMPLE;
    Imgproc.findContours(input, contours, hierarchy, mode, method);
  }

	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param minHeight minimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param minRatio minimum ratio of width to height
	 */
  private void filterContours(List<MatOfPoint> inputContours, 
    double minArea, double minPerimeter, double minWidth, double minHeight, 
    double[] solidity, double minVertexCount, double minRatio, 
    List<MatOfPoint> output) {

		final MatOfInt hull = new MatOfInt();
    output.clear();
    
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
      final MatOfPoint contour = inputContours.get(i);
      
      // Filter by Width & Height
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth) continue;
      if (bb.height < minHeight) continue;
      
      // Filter by Area & Perimeter
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
      if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
      
      // Filter by Solidity
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
      if (solid < solidity[0] || solid > solidity[1]) continue;
      
      // Filter by number of vertices
      if (contour.rows() < minVertexCount)	continue;
      
      // Filter by ratio
			final double ratio = bb.width / (double)bb.height;
      if (ratio < minRatio) continue;
      
      // ToDo: Filter by Convex (includes calculating centroid)

			output.add(contour);
    }
  }
}
