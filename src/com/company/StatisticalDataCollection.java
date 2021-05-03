package com.company;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: 02.05.2021 Implement common deviation 
// TODO: 02.05.2021 Implement common deviation from median 
// TODO: 02.05.2021 Implement common deviation from median 
// TODO: 02.05.2021 Implement quarter deviation 
// TODO: 02.05.2021 Implement coefficent of variation
// TODO: 02.05.2021 Implement positional coefficent of variation
// TODO: 02.05.2021 Implement skewness
// TODO: 02.05.2021 Implement kurtosis
// TODO: 02.05.2021 Implement excess

public class StatisticalDataCollection {
    ArrayList<BigDecimal> _dataSet;
    int precision;

    public StatisticalDataCollection(int precision){
        _dataSet = new ArrayList<>();
        this.precision = precision;
    }

    public void addNewItemToCollection(BigDecimal item){
        item = item.setScale(precision, RoundingMode.HALF_UP);
        _dataSet.add(item);
        _dataSet.sort(BigDecimal::compareTo);
    }

    public BigDecimal getArithmeticAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal sum = _dataSet.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(_dataSet.size()),precision, RoundingMode.HALF_UP);
    }

    public BigDecimal getQuadraticAverage(){
        BigDecimal quadratic_sum = _dataSet.stream().reduce(BigDecimal.ZERO, (s, n)->s.add(n.pow(2)));
        return nthRootOfBigDecimal(2, quadratic_sum.divide(new BigDecimal(_dataSet.size()), precision, RoundingMode.HALF_UP));
    }

    public BigDecimal getGeometricAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal product = _dataSet.stream().reduce(BigDecimal.ONE, BigDecimal::multiply).setScale(precision, RoundingMode.HALF_UP);
        product = nthRootOfBigDecimal(_dataSet.size(), product);
        return product;
    }

    public BigDecimal getHarmonicAverage(){
        BigDecimal reverses_sum = _dataSet.stream().reduce(BigDecimal.ZERO, (s,n)->s.add(BigDecimal.ONE.divide(n, precision + 3, RoundingMode.HALF_UP)));
        BigDecimal output = new BigDecimal(_dataSet.size());
        output = output.divide(reverses_sum,precision, RoundingMode.HALF_UP);
        return output;
    }

    public BigDecimal getMedian(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal output;
        int collection_size = _dataSet.size();
        int median_index = collection_size/2;

        if (_dataSet.size() % 2 == 0){
            output = _dataSet.get(median_index).add(_dataSet.get(median_index+1)).divide(new BigDecimal(2), precision, RoundingMode.HALF_UP);
        }
        else {
            output = (BigDecimal) _dataSet.toArray()[median_index];
        }

        return output.setScale(precision, RoundingMode.HALF_UP);
    }

    public ArrayList<BigDecimal> getMode(){
        Map<BigDecimal, Integer> list2 = new HashMap<>();
        for (int i = 0; i < _dataSet.size(); i++){
            BigDecimal bd = _dataSet.get(i);
            Integer item_count = list2.get(bd);
            list2.put(bd, (item_count == null) ? 1 : item_count + 1);
        }

        Integer max_count = list2.values().stream().max(Integer::compare).orElseThrow();
        return list2.entrySet().stream().filter(tpl -> tpl.getValue() == max_count).map(Map.Entry::getKey).distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    // TODO: 03.05.2021 finish implementing quartile function
    public BigDecimal getQuartile(int quartile){
        Integer quartile_pos = 0;
        BigDecimal output;
        if (_dataSet.size() % 4 == 0){
            quartile_pos = _dataSet.size()/4*quartile+1;
            output = _dataSet.get(quartile_pos).add(_dataSet.get(quartile_pos + 1)).divide(new BigDecimal(2), precision, RoundingMode.HALF_UP);
        }
        else {
            if (_dataSet.size() % 2 == 0 && quartile == 2){
                quartile_pos = _dataSet.size()/2+1;
                output = _dataSet.get(quartile_pos).add(_dataSet.get(quartile_pos + 1)).divide(new BigDecimal(2), precision, RoundingMode.HALF_UP);
            }
            else {
                quartile_pos = _dataSet.size()/4*quartile+1;
                output = _dataSet.get(quartile_pos);
            }
        }
        _dataSet.sort(BigDecimal::compareTo);
        return output;
    }

    // TODO: 02.05.2021 Implement Unbias Wariancy function
    public BigDecimal getUnbiasWariancy(){
        throw new UnsupportedOperationException();
    }

    // TODO: 02.05.2021 Implement Standard Deviation Unbias Calculation function
    public BigDecimal getUnbiasStandardDeviation(){
        throw new UnsupportedOperationException();
    }

    // TODO: 02.05.2021 Implement Bias Wariancy function
    public BigDecimal getBiasWariancy(){
        throw new UnsupportedOperationException();
    }

    // TODO: 02.05.2021 Implement Bias Standard Deviation function
    public BigDecimal getBiasStandardDeviation(){
        throw new UnsupportedOperationException();
    }

    private BigDecimal nthRootOfBigDecimal(int n, BigDecimal num){
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
