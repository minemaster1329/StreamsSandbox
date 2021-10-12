package com.company.bd_datacollections;

import com.company.pub.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RangeFrequencyDistribution extends FrequencyDistributionsAbstract {

    final ArrayList<BigDecimal> _breaks = new ArrayList<>();
    final  ArrayList<BigDecimal> _mids = new ArrayList<>();
    final ArrayList<Integer> _counts = new ArrayList<>();
    BigDecimal range_width = BigDecimal.ZERO;
    int ranges_count = 0;
    boolean generated = false;

    public RangeFrequencyDistribution(int precision){
        this.precision = precision;
    }

    public void generateRangeFrequencyDistribution(){
        clearRanges();
        BigDecimal min = _dataSet.stream().reduce(BigDecimal::min).orElseThrow();
        BigDecimal max = _dataSet.stream().reduce(BigDecimal::max).orElseThrow();

        BigDecimal range = max.subtract(min);
        int ranges = 1 + (int)Math.ceil(3.3*Math.log10(_dataSet.size()));
        BigDecimal width = range.divide(BigDecimal.valueOf(ranges), precision, RoundingMode.HALF_UP);

        _breaks.add(min);
        min = min.add(width);
        for (int i = 0; i < ranges; i++){
            _breaks.add(min);
            _mids.add(_breaks.get(i).add(_breaks.get(i+1)).divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP));
            min = min.add(width);
        }
        _breaks.set(_breaks.size() - 1, max);

        for (int i = 0; i < ranges; i++){
            int finalI = i;
            ArrayList<BigDecimal> list = _dataSet.stream().filter(num -> (num.compareTo(_breaks.get(finalI))>-1) && (num.compareTo(_breaks.get(finalI + 1))<1)).collect(Collectors.toCollection(ArrayList::new));
            _counts.add(list.size());
        }
        range_width = width;
        generated = true;
        ranges_count = ranges;
    }

    @Override
    public void addNewItemToCollection(BigDecimal item) {
        super.addNewItemToCollection(item);
        generated = false;
    }

    @Override
    public BigDecimal getArithmeticAverage() {
        if (generated) {
            BigDecimal sum = BigDecimal.ZERO.setScale(precision, RoundingMode.HALF_UP);
            for (int i = 0; i < ranges_count; i++) {
                sum = sum.add(_mids.get(i).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return sum.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getQuadraticAverage() {
        if (generated){
            BigDecimal quadr_sum = BigDecimal.ZERO;
            for (int i = 0; i < ranges_count; i++){
                quadr_sum = quadr_sum.add(_mids.get(i).pow(2).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return nthRootOfBigDecimal(2, quadr_sum.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP));
        }
        else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getGeometricAverage() {
        if (generated){
            BigDecimal prod = BigDecimal.ONE;

            for (int i = 0; i < ranges_count; i++){
                prod = prod.multiply(_mids.get(i).pow(_counts.get(i)));
            }

            return nthRootOfBigDecimal(elements_count, prod);
        }
        else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getHarmonicAverage() {
        if (generated){
            BigDecimal rev_sum = BigDecimal.ZERO;

            for (int i = 0; i < ranges_count; i++){
                rev_sum = rev_sum.add(BigDecimal.valueOf(_counts.get(i)).divide(_mids.get(i), precision, RoundingMode.HALF_UP));
            }

            return BigDecimal.valueOf(elements_count).divide(rev_sum, precision, RoundingMode.HALF_UP);
        }
        else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMedian(){
        if (generated){
            double  median_pos = ((double)elements_count) / 2;
            ArrayList<Integer> cum_sums = getCountsCumSum();
            int range_index = Utils.getFirstMatchingItemIndex(cum_sums, count -> count > median_pos);
            return _breaks.get(range_index)
                    .add((BigDecimal.valueOf(median_pos - cum_sums.get(range_index - 1)))
                            .multiply(range_width)
                            .divide(BigDecimal.valueOf(_counts.get(range_index)), precision, RoundingMode.HALF_UP));
        }
        else return BigDecimal.ZERO;
    }

    public BigDecimal getMode() {
        if (generated){
            int mode_range_index = _counts.indexOf(_counts.stream().max(Integer::compare).orElseThrow());

            return _breaks.get(mode_range_index)
                    .add(BigDecimal.valueOf(_counts.get(mode_range_index))
                    .subtract(BigDecimal.valueOf(_counts.get(mode_range_index-1)))
                    .multiply(range_width)
                    .divide(BigDecimal.valueOf(_counts.get(mode_range_index))
                    .multiply(BigDecimal.valueOf(2))
                    .subtract(BigDecimal.valueOf(_counts.get(mode_range_index - 1)))
                    .subtract(BigDecimal.valueOf(_counts.get(mode_range_index + 1))), precision, RoundingMode.HALF_UP));
        }
        else return null;
    }

    @Override
    public BigDecimal getQuartile(int quartile) {
        if (generated) {
            int quartile_pos = ((elements_count) % 2 == 0 ? elements_count : elements_count + 1) * quartile / 4;
            ArrayList<Integer> cum_sums = getCountsCumSum();
            int range_index = Utils.getFirstMatchingItemIndex(cum_sums, count -> count > quartile_pos);
            return _breaks.get(range_index).add(BigDecimal.valueOf(quartile_pos - cum_sums.get(range_index - 1))
                    .multiply(range_width)
                    .divide(BigDecimal.valueOf(_counts.get(range_index)), precision, RoundingMode.HALF_UP));
        } else return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getUnbiasWariancy() {
        if (generated) {
            BigDecimal average = getArithmeticAverage();
            BigDecimal output = BigDecimal.ZERO;

            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(average).pow(2).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(BigDecimal.valueOf(elements_count - 1), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getUnbiasStandardDeviation() {
        return (generated) ? nthRootOfBigDecimal(2, getUnbiasWariancy()) : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getBiasWariancy() {
        if (generated) {
            BigDecimal average = getArithmeticAverage();
            BigDecimal output = BigDecimal.ZERO;

            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(average).pow(2).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getBiasStandardDeviation() {
        return (generated) ? nthRootOfBigDecimal(2, getBiasWariancy()) : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCommonDeviation() {
        if (generated) {
            BigDecimal average = getArithmeticAverage();
            BigDecimal output = BigDecimal.ZERO;
            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(average).abs().multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCommonDeviationFromMedian() {
        if (generated) {
            BigDecimal median = getMedian();
            BigDecimal output = BigDecimal.ZERO;
            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(median).abs().multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getQuarterDeviation() {
        if (generated) {
            return getQuartile(3).subtract(getQuartile(1)).divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCoefficentOfVariation() {
        if (generated) {
            return getUnbiasStandardDeviation().divide(getArithmeticAverage(), precision * 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCoefficentOfAssimetry() {
        if (generated) {
            BigDecimal average = getArithmeticAverage();
            BigDecimal output = BigDecimal.ZERO;

            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(average).pow(3).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(getUnbiasStandardDeviation().pow(3).multiply(BigDecimal.valueOf(elements_count)), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getPositionalCoefficentOfVariation() {
        if (generated) {
            BigDecimal quartile1 = getQuartile(1);
            BigDecimal quartile3 = getQuartile(3);

            return quartile3.subtract(quartile1).divide(quartile3.add(quartile1), precision + 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getSkewness() {
        if (generated) {
            return getQuartile(3).add(getQuartile(1))
                    .subtract(getMedian().multiply(BigDecimal.valueOf(2)))
                    .divide(BigDecimal.valueOf(2).multiply(getQuarterDeviation()), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getKurtosis() {
        if (generated) {
            BigDecimal average = getArithmeticAverage();
            BigDecimal output = BigDecimal.ZERO;
            for (int i = 0; i < ranges_count; i++) {
                output = output.add(_mids.get(i).subtract(average).pow(4).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return output.divide(getUnbiasStandardDeviation().pow(4).multiply(BigDecimal.valueOf(elements_count)), precision, RoundingMode.HALF_UP);
        } else return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getExcess() {
        return (generated) ? getKurtosis().subtract(BigDecimal.valueOf(3)) : BigDecimal.ZERO;
    }

    private void clearRanges(){
        _mids.clear();
        _counts.clear();
        _breaks.clear();
    }

    public ArrayList<Integer> getCountsCumSum(){
        if (generated){
            ArrayList<Integer> output = new ArrayList<>();
            output.add(_counts.get(0));
            for (int i = 1; i < ranges_count; i++){
                output.add(output.get(i-1) + _counts.get(i));
            }
            return output;
        }
        else return null;
    }
}
