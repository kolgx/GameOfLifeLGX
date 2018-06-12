package com.mindle.gameoflife;

public class StatusFactory {
    private static final int NUM = 100;

    private static final String STATUS_PULSAR = "Pulsar";
    private static final String STATUS_PENTADECATHLON = "Pentadecathlon ";
    private static final String STATUS_GLIDER = "Glider";
    private static final String STATUS_LWSS = "Lightweight spaceship";
    private static final String STATUS_GOSPER_GLIDER_GUN = "Gosper glider gun";
    private static final String STATUS_DIEHARD = "Diehard";
    private static final String STATUS_INFINITE_LINE = "Infinite line";

    public static final String[] SAMPLE_STATUS_ARRAY = {
            STATUS_PULSAR,
            STATUS_GLIDER,
            STATUS_LWSS,
            STATUS_PENTADECATHLON,
            STATUS_DIEHARD,
            STATUS_INFINITE_LINE,
            STATUS_GOSPER_GLIDER_GUN};

    private static int[][] sMatrix;


    public static int[][] getSampleStatus(String mode) {
        int[][] matrix = null;
        switch (mode) {
            case STATUS_PULSAR:
                matrix = getStatusPulsar();
                break;
            case STATUS_GLIDER:
                matrix = getGlider();
                break;
            case STATUS_DIEHARD:
                matrix = getDiehard();
                break;
            case STATUS_PENTADECATHLON:
                matrix = getPentadecathlon();
                break;
            case STATUS_GOSPER_GLIDER_GUN:
                matrix = getGosperGliderGun();
                break;
            case STATUS_INFINITE_LINE:
                matrix = getInfiniteLine();
                break;
            case STATUS_LWSS:
                matrix = getLWSS();
                break;
        }
        return matrix;
    }

    private static void initMatrix() {
        sMatrix = new int[NUM][NUM];
        for (int row = 0; row < NUM; row++) {
            for (int col = 0; col < NUM; col++) {
                sMatrix[row][col] = 0;
            }
        }
    }

    private static int[][] getGosperGliderGun() {
        initMatrix();

        sMatrix[11][2] = 1;
        sMatrix[12][2] = 1;
        sMatrix[11][3] = 1;
        sMatrix[12][3] = 1;
        sMatrix[11][12] = 1;
        sMatrix[12][12] = 1;
        sMatrix[13][12] = 1;
        sMatrix[10][13] = 1;
        sMatrix[14][13] = 1;
        sMatrix[9][14] = 1;
        sMatrix[15][14] = 1;
        sMatrix[9][15] = 1;
        sMatrix[15][15] = 1;
        sMatrix[12][16] = 1;
        sMatrix[10][17] = 1;
        sMatrix[14][17] = 1;
        sMatrix[11][18] = 1;
        sMatrix[12][18] = 1;
        sMatrix[13][18] = 1;
        sMatrix[12][19] = 1;
        sMatrix[9][22] = 1;
        sMatrix[10][22] = 1;
        sMatrix[11][22] = 1;
        sMatrix[9][23] = 1;
        sMatrix[10][23] = 1;
        sMatrix[11][23] = 1;
        sMatrix[8][24] = 1;
        sMatrix[12][24] = 1;
        sMatrix[7][26] = 1;
        sMatrix[8][26] = 1;
        sMatrix[12][26] = 1;
        sMatrix[13][26] = 1;

        sMatrix[9][36] = 1;
        sMatrix[10][36] = 1;
        sMatrix[9][37] = 1;
        sMatrix[10][37] = 1;


        return sMatrix;
    }

    private static int[][] getInfiniteLine() {
        initMatrix();

        for (int col = 35; col < 49; col++) {
            sMatrix[50][col] = 1;
        }
        sMatrix[50][43] = 0;

        sMatrix[50][52] = 1;
        sMatrix[50][53] = 1;
        sMatrix[50][54] = 1;

        for (int col = 61; col < 74; col++) {
            sMatrix[50][col] = 1;
        }
        sMatrix[50][68] = 0;

        return sMatrix;
    }

    private static int[][] getDiehard() {
        initMatrix();

        sMatrix[49][51] = 1;
        sMatrix[50][45] = 1;
        sMatrix[50][46] = 1;
        sMatrix[51][46] = 1;
        sMatrix[51][50] = 1;
        sMatrix[51][51] = 1;
        sMatrix[51][52] = 1;

        return sMatrix;
    }

    private static int[][] getPentadecathlon() {
        initMatrix();
        for (int row = 46; row < 54; row ++) {
            sMatrix[row][49] = 1;
            sMatrix[row][50] = 1;
            sMatrix[row][51] = 1;
        }
        sMatrix[47][50] = 1;
        sMatrix[52][50] = 1;

        return sMatrix;
    }

    private static int[][] getLWSS() {
        initMatrix();

        sMatrix[42][3] = 1;
        sMatrix[42][4] = 1;
        sMatrix[42][5] = 1;
        sMatrix[42][6] = 1;
        sMatrix[43][2] = 1;
        sMatrix[43][6] = 1;
        sMatrix[44][6] = 1;
        sMatrix[45][2] = 1;
        sMatrix[45][5] = 1;

        return sMatrix;
    }

    private static int[][] getGlider() {
        initMatrix();

        sMatrix[0][2] = 1;
        sMatrix[1][0] = 1;
        sMatrix[1][2] = 1;
        sMatrix[2][1] = 1;
        sMatrix[2][2] = 1;

        return sMatrix;
    }

    private static int[][] getStatusPulsar() {
        int[][] matrix = new int[NUM][NUM];
        for (int row = 0; row < NUM; row++) {
            for (int col = 0; col < NUM; col++) {
                matrix[row][col] = 0;
            }
        }
        matrix[46][44] = 1;
        matrix[47][44] = 1;
        matrix[48][44] = 1;
        matrix[52][44] = 1;
        matrix[53][44] = 1;
        matrix[54][44] = 1;

        matrix[44][46] = 1;
        matrix[49][46] = 1;
        matrix[51][46] = 1;
        matrix[56][46] = 1;

        matrix[44][47] = 1;
        matrix[49][47] = 1;
        matrix[51][47] = 1;
        matrix[56][47] = 1;

        matrix[44][48] = 1;
        matrix[49][48] = 1;
        matrix[51][48] = 1;
        matrix[56][48] = 1;

        matrix[46][49] = 1;
        matrix[47][49] = 1;
        matrix[48][49] = 1;
        matrix[52][49] = 1;
        matrix[53][49] = 1;
        matrix[54][49] = 1;

        matrix[46][51] = 1;
        matrix[47][51] = 1;
        matrix[48][51] = 1;
        matrix[52][51] = 1;
        matrix[53][51] = 1;
        matrix[54][51] = 1;

        matrix[44][52] = 1;
        matrix[49][52] = 1;
        matrix[51][52] = 1;
        matrix[56][52] = 1;

        matrix[44][53] = 1;
        matrix[49][53] = 1;
        matrix[51][53] = 1;
        matrix[56][53] = 1;

        matrix[44][54] = 1;
        matrix[49][54] = 1;
        matrix[51][54] = 1;
        matrix[56][54] = 1;

        matrix[46][56] = 1;
        matrix[47][56] = 1;
        matrix[48][56] = 1;
        matrix[52][56] = 1;
        matrix[53][56] = 1;
        matrix[54][56] = 1;

        return matrix;
    }
}
