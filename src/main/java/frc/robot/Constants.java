/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public final class Constants {
    // Connections to the RoboRio
    public static final class RoboRio {
        public final class PwmPorts {
            public static final int RightRearMotor = 0;
            public static final int RightFrontMotor = 1;
            public static final int LeftRearMotor = 2;
            public static final int LeftFrontMotor = 3;
            public static final int ControlPanelMotor = 4;

            public static final int IntakeMotor = 5;

            public static final int ClimberMotor = 6;
        }
        public final class DioPorts {
            public static final int RightEncoderA = 0;
            public static final int RightEncoderB = 1;
            public static final int LeftEncoderA = 2;
            public static final int LeftEncoderB = 3;
        }
        public final class CanIDs {
            public static final int LeftsparkMax = 11;
            public static final int RightsparkMax = 12;
            public static final int PCM = 0;
        }
    }

    // Connections to the Drivers' Station Laptop
    public static final class Laptop {
        public final class UsbPorts {
            public static final int GamePad = 2;
            public static final int Joystick = 3;
        }
    }

    // Logitech Game Pad
    public static final class GamePad {
        public final class Axis {
            public static final int LeftStickLeftRight = 0;
            public static final int LeftStickUpDown = 1;
            public static final int RightStickLeftRight = 2;
            public static final int RightStickUpDown = 3;
        }
        public final class Buttons {
            public static final int X = 1;
            public static final int A = 2;
            public static final int B = 3;
            public static final int Y = 4;
            public static final int LB = 5;
            public static final int RB = 6;
            public static final int LT = 7;
            public static final int RT = 8;
            // public static final int Back = 9;
            public static final int Start = 10;
            // public static final int LeftJoyStickClick = 11;
            // public static final int RightJoyStickClick = 12;
        }
    }

    // 3D Joystick
    public static final class JoystickConstants{
        public final class Axis {
            public static final int LeftRight = 0;
            public static final int FightFlight = 1;
            public static final int TurnNeck = 2;
            public static final int Throttle = 3;
        }
    }

    // Drive Constants
    public static final class DriveConstants {
        public static final double kMaxDriveOutput = 0.75;
        public static final double kMinTurnValue = 0.51;

        public static final boolean kLeftEncoderReversed = false;
        public static final boolean kRightEncoderReversed = true;
        
        public static final int kEncoderCPR = 360;
        public static final double kWheelDiameterInches = 6;
        public static final double kEncoderDistancePerPulse = 
            (kWheelDiameterInches * Math.PI) / (double) kEncoderCPR;

        public static final boolean kGyroReversed = false;
    }

    public static final class TurnByAngle {
        // PID constants for turning the robot
        public static final double kTurnP = 0.0535;
        public static final double kTurnI = 0.0;
        public static final double kTurnD = 0;

        public static final double kTurnToleranceDeg = 1;           // degrees
        public static final double kTurnRateToleranceDegPerS = 10;  // degrees per second

        public static final double kMaxTurnRateDegPerS = 100;
        public static final double kMaxTurnAccelDegPerSSquared = 300;    
    }

    public static final class AutoAim {
        public static final double kP = 0.0535;// * 0.45;
        public static final double kI = 0.0; //0.0535 * 0.54  / 1.3;
        public static final double kD = 0;

        public static final double kOffsetRatio = 1;

        public static final double kTurnToleranceDeg = 3;           // degrees
        public static final double kTurnRateToleranceDegPerS = 10;  // degrees per second

        public static final double kMaxTurnRateDegPerS = 100;
        public static final double kMaxTurnAccelDegPerSecSqd = 300;  

        public static final double kPxlOffsetRatio = .1;

        public static final double kTurnTolerancePxl = 3;           // pixels
        public static final double kTurnRateTolerancePxlPerS = 10;  // pixels per second

        public static final double kMaxTurnRatePxlPerS = 100;
        public static final double kMaxTurnAccelPxlPerSecSqd = 300;  
    }

    // Vision Constants
    public static final class VisionConstants {
        public static final int kImageWidth = 320;
        public static final int kImageHeight = 240;

        public static final double kGoalX = (double)kImageWidth / 2.0;
        public static final double kGoalY = (double)kImageHeight / 2.0;

        public static final String kGripNT = "GRIP/myContoursReport";
        public static final String kCenterXKey = "centerX";
        public static final String kCenterYKey = "centerY";

        public static final int kLightSwitch = 0;
    }

    public static final class ShooterConstants {
        // PID values for the shooter's closed loop velocity PID 
        public static final double kP = 5e-5;
        public static final double kI = 1e-6;
        public static final double kD = 0;

        public static final double kMinOutput = -1;
        public static final double kMaxOutput = 1;

        public static final double kMaxRPM = 5700;

        public static final double kDeadzone = 0.1;
    }

    public static final class ColorWheelConstants {
        public static final double kDeadzone = 0.1;
        public static final double kSpeedScaling = 0.5;
    }
}

