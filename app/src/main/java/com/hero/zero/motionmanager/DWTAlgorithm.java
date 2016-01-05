package com.hero.zero.motionmanager;

import java.util.ArrayList;

/**
 * Created by shadk on 06.12.2015.
 */
public class DWTAlgorithm {

    private static double distance(Point firstPoint, Point secondPoint) {
        return Math.sqrt(
                Math.pow((secondPoint.x - firstPoint.x), 2) +
                        Math.pow((secondPoint.y - firstPoint.y), 2) +
                        Math.pow((secondPoint.z - firstPoint.z), 2));
    }

    public static double findDistance(Motion firstMotion, Motion secondMotion) {
        int n = firstMotion.size();
        int m = secondMotion.size();

        ArrayList<Point> firstMotionPoints = firstMotion.getPointsArray();
        ArrayList<Point> secondMotionPoints = secondMotion.getPointsArray();

        double[][] euclidianDIstances = new double[n][m];
        double[][] minimalDistances = new double[n][m];

        for (int i = 1; i < n; ++i) {
            for (int j = 1; j < m; ++j) {
                euclidianDIstances[i][j] = distance(firstMotionPoints.get(i), secondMotionPoints.get(j));
            }
        }
        minimalDistances[0][0] = euclidianDIstances[0][0];
        for (int i = 1; i < n; i++)
            minimalDistances[i][0] = euclidianDIstances[i][0] +
                    minimalDistances[i - 1][0];

        for (int j = 1; j < m; j++)
            minimalDistances[0][j] = euclidianDIstances[0][j] +
                    minimalDistances[0][j - 1];
        for (int i = 1; i < n; i++)
            for (int j = 1; j < m; j++)
                if (minimalDistances[i - 1][j - 1] <= minimalDistances[i - 1][j]) {
                    if (minimalDistances[i - 1][j - 1] <= minimalDistances[i][j - 1]) {
                        minimalDistances[i][j] = euclidianDIstances[i][j] + minimalDistances[i - 1][j - 1];
                    } else
                        minimalDistances[i][j] = euclidianDIstances[i][j] + minimalDistances[i][j - 1];
                } else {
                    if (minimalDistances[i - 1][j] <= minimalDistances[i][j - 1]) {
                        minimalDistances[i][j] = euclidianDIstances[i][j] + minimalDistances[i - 1][j];
                    } else
                        minimalDistances[i][j] = euclidianDIstances[i][j] + minimalDistances[i][j - 1];
                }
        return minimalDistances[n - 1][m - 1];
    }
}
