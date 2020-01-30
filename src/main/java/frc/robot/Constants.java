/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public final class Constants {
    public static final class RoboRio {
        public final class PwmPorts {
            public static final int RightRearMotor = 0;
            public static final int RightFrontMotor = 1;
            public static final int LeftRearMotor = 2;
            public static final int LeftFrontMotor = 3;
           
        }
        public final class DioPorts {
            public static final int RightEncoderA = 0;
            public static final int RightEncoderB = 1;
            public static final int LeftEncoderA = 2;
            public static final int LeftEncoderB = 3;
        }
    }

    public static final class DriverStation {
        public final class UsbPorts {
            public static final int GamePad = 2;
        }
    }

    public static final class GamePad {
        public final class Axis {
            public final class LeftStick {
                public static final int LeftRight = 0;
                public static final int UpDown = 1;
            }
            public final class RightStick {
                public static final int LeftRight = 2;
                public static final int UpDown = 3;
            }
        }
        public final class Buttons {
            public static final int X = 1;
            public static final int A = 2;
            public static final int B = 3;
            public static final int Y = 4;
            // public static final int LB = 5;
            // public static final int RB = 6;
            // public static final int LT = 7;
            // public static final int RT = 8;
            // public static final int Back = 9;
            // public static final int Start = 10;
            // public static final int LeftJoyStickClick = 11;
            // public static final int RightJoyStickClick = 12;
        }
    }

    public static final class DriveConstants {
        public static final boolean kLeftEncoderReversed = false;
        public static final boolean kRightEncoderReversed = true;
        
        public static final int kEncoderCPR = 2048;
        public static final int kEncoderCPR2 = 2048;
        public static final double kWheelDiameterInches = 6;
        public static final double kEncoderDistancePerPulse = 
            (kWheelDiameterInches * Math.PI) / (double) kEncoderCPR;
        public static final double kEncoderDistancePerPulse2 = 
            (kWheelDiameterInches * Math.PI) / (double) kEncoderCPR2;
            // Assumes the encoders are mounted directly on the wheel shafts

        public static final boolean kGyroReversed = false;

        // PID constants for turning the robot
        public static final double kTurnP = 0.055;
        public static final double kTurnI = 0;
        public static final double kTurnD = 0;

        public static final double kTurnToleranceDeg = 2;           // degrees
        public static final double kTurnRateToleranceDegPerS = 10;  // degrees per second

        public static final double kMaxTurnRateDegPerS = 100;
        public static final double kMaxTurnAccelDegPerSSquared = 300;    }

}

