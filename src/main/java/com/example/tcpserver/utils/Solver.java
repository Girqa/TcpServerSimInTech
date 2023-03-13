package com.example.tcpserver.utils;

public class Solver {

    public static double rms(double[] values, double timeStep) {
        double I = 0;
        for (double v: values) {
            I += v*v;
        }
        I /= values.length;
        return Math.sqrt(I);
    }

    public static double getFx(double[] measuredValues, double w, double dt) {
        double I = 0;
        for (int i = 0; i < measuredValues.length; i++) {
            I += measuredValues[i] * Math.sin(w * i * dt);
        }
        return I * 2 / measuredValues.length;
    }

    public static double getFy(double[] measuredValues, double w, double dt) {
        double I = 0;
        for (int i = 0; i < measuredValues.length; i++) {
            I += measuredValues[i] * Math.cos(w * i * dt);
        }
        return I * 2 / measuredValues.length;
    }

    public static double getCorrectedFx(double[] measuredValues, double w, double dt) {
        double I = 0;
        for (int i = 0; i < measuredValues.length - 1; i += 2) {
            I += (measuredValues[i] + measuredValues[i + 1]) / 2 * Math.sin(w * (i + 1) * dt);
        }
        return I * 4 / measuredValues.length;
    }

    public static double getCorrectedFy(double[] measuredValues, double w, double dt) {
        double I = 0;
        for (int i = 0; i < measuredValues.length - 1; i += 2) {
            I += (measuredValues[i] + measuredValues[i + 1]) / 2 * Math.cos(w * (i + 1) * dt);
        }
        return I * 4 / measuredValues.length;
    }
}
