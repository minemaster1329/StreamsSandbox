package com.company.datacollections;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.company.pub.Utils;

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
    public BigDecimal getArithmeticAverage() throws OperationNotSupportedException {
        if (generated){
            BigDecimal sum = BigDecimal.ZERO.setScale(precision, RoundingMode.HALF_UP);
            for (int i = 0; i < ranges_count; i++){
                sum = sum.add(_mids.get(i).multiply(BigDecimal.valueOf(_counts.get(i))));
            }

            return sum.divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
        }
        else throw new OperationNotSupportedException("Cannot use methods until range series not generated.");
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

    // TODO: 08.05.2021 implement quartile function for range frequency distribution
    @Override
    public BigDecimal getQuartile(int quartile) {
        return null;
    }

    // TODO: 09.05.2021 implement UB variancy function for range frequency distribution
    @Override
    public BigDecimal getUnbiasWariancy() {
        return null;
    }

    // TODO: 09.05.2021 implement UB standard deviation for range frequency distribution
    @Override
    public BigDecimal getUnbiasStandardDeviation() {
        return null;
    }

    // TODO: 09.05.2021 implement B variancy for range frequency distribution
    @Override
    public BigDecimal getBiasWariancy() {
        return null;
    }

    // TODO: 09.05.2021 implement B standard deviation for range frequency distribution
    @Override
    public BigDecimal getBiasStandardDeviation() {
        return null;
    }

    // TODO: 09.05.2021 implement common deviation for range frequency distribution
    @Override
    public BigDecimal getCommonDeviation() {
        return null;
    }

    // TODO: 09.05.2021 implement common deviation from median for range frequency distribution
    @Override
    public BigDecimal getCommonDeviationFromMedian() {
        return null;
    }

    // TODO: 09.05.2021 implement quarter deviation for range frequency distribution
    @Override
    public BigDecimal getQuarterDeviation() {
        return null;
    }

    // TODO: 09.05.2021 implement coefficent of variation for range frequency distribution
    @Override
    public BigDecimal getCoefficentOfVariation() {
        return null;
    }

    // TODO: 09.05.2021 implement positional coefficent of variation for range frequency distribution
    @Override
    public BigDecimal getPositionalCoefficentOfVariation() {
        return null;
    }

    // TODO: 09.05.2021 implement skewness for range frequency distribution
    @Override
    public BigDecimal getSkewness() {
        return null;
    }

    // TODO: 09.05.2021 implement kurtosis for range frequency distribution
    @Override
    public BigDecimal getKurtosis() {
        return null;
    }

    // TODO: 09.05.2021 implement excess for range frequency distribution
    @Override
    public BigDecimal getExcess() {
        return null;
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
