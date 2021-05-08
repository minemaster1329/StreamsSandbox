package com.company.datacollections;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public abstract class FrequencyDistributionsAbstract {
    protected int precision;
    protected int elements_count = 0;
    final protected ArrayList<BigDecimal> _dataSet = new ArrayList<>();

    public ArrayList<BigDecimal> getDataSet(){
        return _dataSet;
    }

    public void addNewItemToCollection(BigDecimal item){
        _dataSet.add(item.setScale(precision, RoundingMode.HALF_UP));
        _dataSet.sort(BigDecimal::compareTo);
        ++elements_count;
    }

    public abstract BigDecimal getArithmeticAverage() throws OperationNotSupportedException;

    public abstract BigDecimal getQuadraticAverage();

    public abstract BigDecimal getGeometricAverage();

    public abstract BigDecimal getHarmonicAverage();

    public abstract BigDecimal getMedian();

    public abstract BigDecimal getQuartile(int quartile);

    public abstract BigDecimal getUnbiasWariancy();

    public abstract BigDecimal getUnbiasStandardDeviation();

    public abstract BigDecimal getBiasWariancy();

    public abstract BigDecimal getBiasStandardDeviation();

    public abstract BigDecimal getCommonDeviation();

    public abstract BigDecimal getCommonDeviationFromMedian();

    public abstract BigDecimal getQuarterDeviation();

    public abstract BigDecimal getCoefficentOfVariation();

    public abstract BigDecimal getPositionalCoefficentOfVariation();

    public abstract BigDecimal getSkewness();

    public abstract BigDecimal getKurtosis();

    public abstract BigDecimal getExcess();

    public BigDecimal nthRootOfBigDecimal(int n, BigDecimal num){
        BigDecimal pre = new BigDecimal(1);
        pre = pre.setScale(precision + 1, RoundingMode.HALF_UP);
        for (int i = 1; i < precision; i++) pre = pre.divide(new BigDecimal(10), precision, RoundingMode.HALF_UP);
        num = num.setScale(precision, RoundingMode.HALF_UP);
        BigDecimal output = num;
        BigDecimal prev = output;
        while (num.subtract(output.pow(n)).abs().setScale(precision + 1, RoundingMode.HALF_UP).compareTo(pre) > 0) {
            output = output.multiply(new BigDecimal(n - 1)).add(num.divide(output.pow(n - 1),precision, RoundingMode.HALF_UP)).divide(new BigDecimal(n), precision, RoundingMode.HALF_UP);
            if (prev.compareTo(output) == 0) break;
            prev = output;
        }

        return output.setScale(precision, RoundingMode.HALF_UP);
    }
}
