package com.company.datacollections;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RangeFrequencyDistribution extends FrequencyDistributionsAbstract {

    ArrayList<BigDecimal> _breaks;
    ArrayList<BigDecimal> _mids;
    ArrayList<Integer> _counts;
    boolean generated;

    public RangeFrequencyDistribution(int precision){
        this.precision = precision;
        _dataSet = new ArrayList<>();
        _breaks = new ArrayList<>();
        _mids = new ArrayList<>();
        _counts = new ArrayList<>();
        generated = false;
    }

    public void generateRangeFrequencyDistribution(){
        clearRanges();
        BigDecimal min = _dataSet.stream().reduce(BigDecimal::min).orElseThrow();
        BigDecimal max = _dataSet.stream().reduce(BigDecimal::max).orElseThrow();

        BigDecimal range = max.subtract(min);
        int elements_count = 1 + (int)Math.ceil(3.3*Math.log10(_dataSet.size()));
        BigDecimal width = range.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);

        _breaks.add(min);
        min = min.add(width);
        for (int i = 0; i < elements_count; i++){
            _breaks.add(min);
            _mids.add(_breaks.get(i).add(_breaks.get(i+1)).divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP));
            min = min.add(width);
        }
        _breaks.set(_breaks.size() - 1, max);

        for (int i = 0; i < elements_count; i++){
            int finalI = i;
            ArrayList<BigDecimal> list = _dataSet.stream().filter(num -> (num.compareTo(_breaks.get(finalI))>-1) && (num.compareTo(_breaks.get(finalI + 1))<1)).collect(Collectors.toCollection(ArrayList::new));
            _counts.add(list.size());
        }
        generated = true;
    }

    public ArrayList<Integer> get_counts() {
        return _counts;
    }

    public ArrayList<BigDecimal> get_breaks() {
        return _breaks;
    }

    public ArrayList<BigDecimal> get_mids() {
        return _mids;
    }

    @Override
    public void addNewItemToCollection(BigDecimal item) {
        super.addNewItemToCollection(item);
        generated = false;
    }

    @Override
    public BigDecimal getArithmeticAverage() throws OperationNotSupportedException {
        if (generated){
            BigDecimal sum = BigDecimal.ZERO.setScale(precision, RoundingMode.HALF_UP);
            for (int i = 0; i < _counts.size(); i++){
                sum = sum.add(_mids.get(i).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return sum.divide(BigDecimal.valueOf(_dataSet.size()), precision, RoundingMode.HALF_UP);
        }
        else throw new OperationNotSupportedException("Cannot use methods until range series not generated.");
    }

    @Override
    public BigDecimal getQuadraticAverage() {
        return null;
    }

    @Override
    public BigDecimal getGeometricAverage() {
        return null;
    }

    @Override
    public BigDecimal getHarmonicAverage() {
        return null;
    }

    @Override
    public BigDecimal getMedian() {
        return null;
    }

    @Override
    public ArrayList<BigDecimal> getMode() {
        return null;
    }

    @Override
    public BigDecimal getQuartile(int quartile) {
        return null;
    }

    @Override
    public BigDecimal getUnbiasWariancy() {
        return null;
    }

    @Override
    public BigDecimal getUnbiasStandardDeviation() {
        return null;
    }

    @Override
    public BigDecimal getBiasWariancy() {
        return null;
    }

    @Override
    public BigDecimal getBiasStandardDeviation() {
        return null;
    }

    @Override
    public BigDecimal getCommonDeviation() {
        return null;
    }

    @Override
    public BigDecimal getCommonDeviationFromMedian() {
        return null;
    }

    @Override
    public BigDecimal getQuarterDeviation() {
        return null;
    }

    @Override
    public BigDecimal getCoefficentOfVariation() {
        return null;
    }

    @Override
    public BigDecimal getPositionalCoefficentOfVariation() {
        return null;
    }

    @Override
    public BigDecimal getSkewness() {
        return null;
    }

    @Override
    public BigDecimal getKurtosis() {
        return null;
    }

    @Override
    public BigDecimal getExcess() {
        return null;
    }

    private void clearRanges(){
        _mids.clear();
        _counts.clear();
        _breaks.clear();
    }
}
