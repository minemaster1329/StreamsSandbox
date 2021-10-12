package com.company.bd_datacollections;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PointFrequencyDistribution extends FrequencyDistributionsAbstract {
    public PointFrequencyDistribution(int precision){
        this.precision = precision;
    }

    @Override
    public BigDecimal getArithmeticAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal sum = _dataSet.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(elements_count),precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getQuadraticAverage(){
        BigDecimal quadratic_sum = _dataSet.stream().reduce(BigDecimal.ZERO, (s, n)->s.add(n.pow(2)));
        return nthRootOfBigDecimal(2, quadratic_sum.divide(new BigDecimal(elements_count), precision, RoundingMode.HALF_UP));
    }

    @Override
    public BigDecimal getGeometricAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal product = _dataSet.stream().reduce(BigDecimal.ONE, BigDecimal::multiply).setScale(precision, RoundingMode.HALF_UP);
        product = nthRootOfBigDecimal(elements_count, product);
        return product;
    }

    @Override
    public BigDecimal getHarmonicAverage(){
        BigDecimal reverses_sum = _dataSet.stream().reduce(BigDecimal.ZERO, (s,n)->s.add(BigDecimal.ONE.divide(n, precision + 3, RoundingMode.HALF_UP)));
        BigDecimal output = new BigDecimal(elements_count);
        output = output.divide(reverses_sum,precision, RoundingMode.HALF_UP);
        return output;
    }

    @Override
    public BigDecimal getMedian(){
        _dataSet.sort(BigDecimal::compareTo);
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal output;
        int collection_size = elements_count;
        int median_index = collection_size/2;

        if (elements_count % 2 == 0){
            output = _dataSet.get(median_index).add(_dataSet.get(median_index+1)).divide(new BigDecimal(2), precision, RoundingMode.HALF_UP);
        }
        else {
            output = (BigDecimal) _dataSet.toArray()[median_index];
        }

        return output.setScale(precision, RoundingMode.HALF_UP);
    }

    public ArrayList<BigDecimal> getMode(){
        Map<BigDecimal, Integer> list2 = new HashMap<>();
        for (int i = 0; i < elements_count; i++){
            BigDecimal bd = _dataSet.get(i);
            Integer item_count = list2.get(bd);
            list2.put(bd, (item_count == null) ? 1 : item_count + 1);
        }

        Integer max_count = list2.values().stream().max(Integer::compare).orElseThrow();
        return list2.entrySet().stream().filter(tpl -> tpl.getValue().equals(max_count)).map(Map.Entry::getKey).distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    // TODO: 05.05.2021 optimize quartile function
    @Override
    public BigDecimal getQuartile(int quartile){
        _dataSet.sort(BigDecimal::compareTo);
        int data_count = elements_count;
        BigDecimal output;
        if (data_count % 2 == 0){
            int half_of_set_size = data_count/2;
            switch (quartile){
                case 1:
                    int half_of_half_of_set_size = half_of_set_size/2;
                    if (half_of_set_size % 2 == 0){
                        output = _dataSet.get(half_of_half_of_set_size - 1)
                                .add(_dataSet.get(half_of_half_of_set_size))
                                .divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP);
                    }
                    else {
                        output = _dataSet.get(half_of_half_of_set_size);
                    }
                    break;
                case 2:
                    output = _dataSet.get(half_of_set_size-1)
                            .add(_dataSet.get(half_of_set_size))
                            .divide(BigDecimal.valueOf(2),precision , RoundingMode.HALF_UP);
                    break;
                case 3:
                    int half_of_half_of_set_size_quart3 = 3 * half_of_set_size/2;
                    if (half_of_set_size % 2 == 0){
                        output = _dataSet.get(half_of_half_of_set_size_quart3 - 1)
                                .add(_dataSet.get(half_of_half_of_set_size_quart3))
                                .divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP);
                    }
                    else {
                        output = _dataSet.get(half_of_half_of_set_size_quart3);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("quartile number must be 1,2 or 3");
            }
        }
        else {
            int half_of_set = data_count/2;
            int quart_of_set = half_of_set/2;
            if (data_count % 4 == 1){
                switch (quartile){
                    case 1:
                        output = _dataSet.get(quart_of_set - 1)
                                .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP)
                                .add(_dataSet.get(quart_of_set).
                                        multiply(BigDecimal.valueOf(3))
                                        .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP));
                        break;
                    case 2:
                        output = _dataSet.get(half_of_set);
                        break;
                    case 3:
                        output = _dataSet.get(quart_of_set + half_of_set)
                                .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(3))
                                .add(_dataSet.get(quart_of_set + half_of_set+1)
                                        .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP));
                        break;
                    default:
                        throw new IllegalArgumentException("quartile number must be 1,2 or 3");
                }
            }
            else {
                switch (quartile){
                    case 1:
                        output = _dataSet.get(quart_of_set)
                                .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(3))
                                .add(_dataSet.get(quart_of_set+1)
                                        .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP));
                        break;
                    case 2:
                        output = _dataSet.get(half_of_set);
                        break;
                    case 3:
                        output = _dataSet.get(quart_of_set + half_of_set)
                                .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP)
                                .add(_dataSet.get(quart_of_set + half_of_set+1).
                                        multiply(BigDecimal.valueOf(3))
                                        .divide(BigDecimal.valueOf(4), precision, RoundingMode.HALF_UP));
                        break;
                    default:
                        throw new IllegalArgumentException("quartile number must be 1,2 or 3");
                }
            }
        }
        return output;
    }

    @Override
    public BigDecimal getUnbiasWariancy(){
        BigDecimal average = getArithmeticAverage();
        return _dataSet.stream().map(num -> num.subtract(average).pow(2)).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(elements_count-1), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getUnbiasStandardDeviation(){
        return nthRootOfBigDecimal(2, getUnbiasWariancy());
    }

    @Override
    public BigDecimal getBiasWariancy(){
        BigDecimal average = getArithmeticAverage();
        return _dataSet.stream().map(num -> num.subtract(average).pow(2)).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getBiasStandardDeviation(){
        return nthRootOfBigDecimal(2, getBiasWariancy());
    }

    @Override
    public BigDecimal getCommonDeviation(){
        BigDecimal average = getArithmeticAverage();
        return _dataSet.stream().map(num->num.subtract(average).abs()).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getCommonDeviationFromMedian(){
        BigDecimal median = getMedian();
        return _dataSet.stream().map(num->num.subtract(median).abs()).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(elements_count), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getQuarterDeviation() {
        BigDecimal quartile1 = getQuartile(1);
        BigDecimal quartile3 = getQuartile(3);
        return quartile3.subtract(quartile1).divide(BigDecimal.valueOf(2), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getCoefficentOfVariation() {
        return getUnbiasStandardDeviation().multiply(BigDecimal.valueOf(100)).divide(getArithmeticAverage(), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getCoefficentOfAssimetry() {
        BigDecimal average = getArithmeticAverage();
        return _dataSet.stream().map(num -> num.subtract(average).pow(3)).reduce(BigDecimal.ZERO, BigDecimal::add).divide(getUnbiasStandardDeviation().pow(3).multiply(BigDecimal.valueOf(elements_count)), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getPositionalCoefficentOfVariation() {
        BigDecimal quartile1 = getQuartile(1);
        BigDecimal quartile3 = getQuartile(3);

        return quartile3.subtract(quartile1).multiply(BigDecimal.valueOf(100)).divide(quartile3.add(quartile1), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getSkewness() {
        return getQuartile(1).add(getQuartile(3))
                .subtract(getMedian().multiply(BigDecimal.valueOf(2)))
                .divide(BigDecimal.valueOf(2).multiply(getQuarterDeviation()), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getKurtosis(){
        BigDecimal average = getArithmeticAverage();
        return _dataSet.stream().map(num -> num.subtract(average).pow(4)).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(elements_count), precision+5, RoundingMode.HALF_UP).divide(getUnbiasWariancy().pow(2), precision, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getExcess(){
        return getKurtosis().subtract(BigDecimal.valueOf(3));
    }
}
