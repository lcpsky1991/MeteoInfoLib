/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteoinfo.math.stats;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.meteoinfo.data.ArrayMath;
import org.meteoinfo.data.ArrayUtil;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;

/**
 *
 * @author Yaqiang Wang
 */
public class StatsUtil {

    /**
     * Computes covariances for pairs of arrays or columns of a matrix.
     *
     * @param x X data
     * @param y Y data
     * @param bias If true, returned value will be bias-corrected
     * @return The covariance matrix
     */
    public static Array cov(Array x, Array y, boolean bias) {
        int m = x.getShape()[0];
        int n = 1;
        if (x.getRank() == 2)
            n = x.getShape()[1];
        double[][] aa = new double[m][n * 2];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n * 2; j++) {
                if (j < n) {
                    aa[i][j] = x.getDouble(i * n + j);
                } else {
                    aa[i][j] = y.getDouble(i * n + j - n);
                }
            }
        }
        RealMatrix matrix = new Array2DRowRealMatrix(aa, false);
        Covariance cov = new Covariance(matrix, bias);
        RealMatrix mcov = cov.getCovarianceMatrix();
        m = mcov.getColumnDimension();
        n = mcov.getRowDimension();
        Array r = Array.factory(DataType.DOUBLE, new int[]{m, n});
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                r.setDouble(i * n + j, mcov.getEntry(i, j));
            }
        }

        return r;
    }

    /**
     * Computes covariances for columns of a matrix.
     * @param a Matrix data
     * @param bias If true, returned value will be bias-corrected
     * @return Covariant matrix or value
     */
    public static Object cov(Array a, boolean bias) {
        if (a.getRank() == 1) {
            double[] ad = (double[]) ArrayUtil.copyToNDJavaArray(a);
            Covariance cov = new Covariance();
            return cov.covariance(ad, ad);
        } else {
            double[][] aa = (double[][]) ArrayUtil.copyToNDJavaArray(a);
            RealMatrix matrix = new Array2DRowRealMatrix(aa, false);
            Covariance cov = new Covariance(matrix, bias);
            RealMatrix mcov = cov.getCovarianceMatrix();
            int m = mcov.getColumnDimension();
            int n = mcov.getRowDimension();
            Array r = Array.factory(DataType.DOUBLE, new int[]{m, n});
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    r.setDouble(i * n + j, mcov.getEntry(i, j));
                }
            }

            return r;
        }
    }

    /**
     * Calculates Kendall's tau, a correlation measure for ordinal data.
     *
     * @param x X data
     * @param y Y data
     * @return Kendall's tau correlation.
     */
    public static double kendalltau(Array x, Array y) {
        double[] xd = (double[]) ArrayUtil.copyToNDJavaArray(x);
        double[] yd = (double[]) ArrayUtil.copyToNDJavaArray(y);
        KendallsCorrelation kc = new KendallsCorrelation();
        double r = kc.correlation(xd, yd);
        return r;
    }
    
    /**
     * Calculates a Pearson correlation coefficient.
     *
     * @param x X data
     * @param y Y data
     * @return Pearson correlation and p-value.
     */
    public static double[] pearsonr(Array x, Array y) {
        int m = x.getShape()[0];
        int n = 1;
        double[][] aa = new double[m][n * 2];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n * 2; j++) {
                if (j < n) {
                    aa[i][j] = x.getDouble(i * n + j);
                } else {
                    aa[i][j] = y.getDouble(i * n + j - n);
                }
            }
        }
        RealMatrix matrix = new Array2DRowRealMatrix(aa, false);
        PearsonsCorrelation pc = new PearsonsCorrelation(matrix);
        double r = pc.getCorrelationMatrix().getEntry(0, 1);
        double pvalue = pc.getCorrelationPValues().getEntry(0, 1);
        return new double[]{r, pvalue};
    }
    
    /**
     * Computes Spearman's rank correlation for pairs of arrays or columns of a matrix.
     *
     * @param x X data
     * @param y Y data
     * @return Spearman's rank correlation
     */
    public static Array spearmanr(Array x, Array y) {
        int m = x.getShape()[0];
        int n = 1;
        if (x.getRank() == 2)
            n = x.getShape()[1];
        double[][] aa = new double[m][n * 2];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n * 2; j++) {
                if (j < n) {
                    aa[i][j] = x.getDouble(i * n + j);
                } else {
                    aa[i][j] = y.getDouble(i * n + j - n);
                }
            }
        }
        RealMatrix matrix = new Array2DRowRealMatrix(aa, false);
        SpearmansCorrelation cov = new SpearmansCorrelation(matrix);
        RealMatrix mcov = cov.getCorrelationMatrix();
        m = mcov.getColumnDimension();
        n = mcov.getRowDimension();
        Array r = Array.factory(DataType.DOUBLE, new int[]{m, n});
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                r.setDouble(i * n + j, mcov.getEntry(i, j));
            }
        }

        return r;
    }

    /**
     * Computes Spearman's rank correlation for columns of a matrix.
     * @param a Matrix data
     * @return Spearman's rank correlation
     */
    public static Object spearmanr(Array a) {
        if (a.getRank() == 1) {
            double[] ad = (double[]) ArrayUtil.copyToNDJavaArray(a);
            Covariance cov = new Covariance();
            return cov.covariance(ad, ad);
        } else {
            double[][] aa = (double[][]) ArrayUtil.copyToNDJavaArray(a);
            RealMatrix matrix = new Array2DRowRealMatrix(aa, false);
            SpearmansCorrelation cov = new SpearmansCorrelation(matrix);
            RealMatrix mcov = cov.getCorrelationMatrix();
            int m = mcov.getColumnDimension();
            int n = mcov.getRowDimension();
            Array r = Array.factory(DataType.DOUBLE, new int[]{m, n});
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    r.setDouble(i * n + j, mcov.getEntry(i, j));
                }
            }

            return r;
        }
    }
    
    /**
     * Implements ordinary least squares (OLS) to estimate the parameters of a 
     * multiple linear regression model.
     * @param y Y sample data - one dimension array
     * @param x X sample data - two dimension array
     * @return Estimated regression parameters and residuals
     */
    public static Array[] mutipleLineRegress_OLS(Array y, Array x) {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        double[] yy = (double[])ArrayUtil.copyToNDJavaArray(y);
        double[][] xx = (double[][])ArrayUtil.copyToNDJavaArray(x);
        regression.newSampleData(yy, xx);
        double[] para = regression.estimateRegressionParameters();
        double[] residuals = regression.estimateResiduals();
        int k = para.length;
        int n = residuals.length;
        Array aPara = Array.factory(DataType.DOUBLE, new int[]{k});
        Array aResiduals = Array.factory(DataType.DOUBLE, new int[]{n});
        for (int i = 0; i < k; i++){
            aPara.setDouble(i, para[i]);
        }
        for (int i = 0; i < k; i++){
            aResiduals.setDouble(i, residuals[i]);
        }
        
        return new Array[]{aPara, aResiduals};
    }
    
    /**
     * Returns an estimate of the pth percentile of the values in the array.
     * @param a Input array
     * @param p The percentile value to compute
     * @return The pth percentile
     */
    public static double percentile(Array a, double p){
        double[] v = (double[])a.get1DJavaArray(Double.class);
        double r = StatUtils.percentile(v, p);
        return r;
    }
    
    /**
     * Returns an estimate of the pth percentile of the values in the array along an axis.
     * @param a Input array
     * @param p The percentile value to compute
     * @param axis The axis
     * @return The pth percentile
     * @throws InvalidRangeException 
     */
    public static Array percentile(Array a, double p, int axis) throws InvalidRangeException{
        int[] dataShape = a.getShape();
        int[] shape = new int[dataShape.length - 1];
        int idx;
        for (int i = 0; i < dataShape.length; i++) {
            idx = i;
            if (idx == axis) {
                continue;
            } else if (idx > axis) {
                idx -= 1;
            }
            shape[idx] = dataShape[i];
        }
        Array r = Array.factory(DataType.DOUBLE, shape);
        Index indexr = r.getIndex();
        int[] current;
        for (int i = 0; i < r.getSize(); i++) {
            current = indexr.getCurrentCounter();
            List<Range> ranges = new ArrayList<>();
            for (int j = 0; j < dataShape.length; j++) {
                if (j == axis) {
                    ranges.add(new Range(0, dataShape[j] - 1, 1));
                } else {
                    idx = j;
                    if (idx > axis) {
                        idx -= 1;
                    }
                    ranges.add(new Range(current[idx], current[idx], 1));
                }
            }
            Array aa = ArrayMath.section(a, ranges);
            double[] v = (double[])aa.get1DJavaArray(Double.class);
            double q = StatUtils.percentile(v, p);
            r.setDouble(i, q);
            indexr.incr();
        }

        return r;
    }
}
