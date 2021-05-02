package com.company;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StatisticalDataCollection {
    TreeSet<BigDecimal> _dataSet;
    int precision;

    public StatisticalDataCollection(int precision){
        _dataSet = new TreeSet<>();
        this.precision = precision;
    }

    public void addNewItemToCollection(BigDecimal item){
        item = item.setScale(precision, RoundingMode.HALF_UP);
        _dataSet.add(item);
    }

    public BigDecimal getArithmeticAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal sum = _dataSet.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(_dataSet.size()),precision, RoundingMode.HALF_UP);
    }

    public BigDecimal getQuadraticAverage(){
        BigDecimal quadratic_sum = new BigDecimal(0);
        quadratic_sum.setScale(precision, RoundingMode.HALF_UP);
        quadratic_sum = _dataSet.stream().reduce(BigDecimal.ZERO, (s, n)->s.add(n.pow(2)));
        return nthRootOfBigDecimal(2, quadratic_sum.divide(new BigDecimal(_dataSet.size()), precision, RoundingMode.HALF_UP));
    }

    public BigDecimal getGeometricAverage(){
        if (_dataSet.isEmpty()) return new BigDecimal(0);
        BigDecimal product = _dataSet.stream().reduce(BigDecimal.ONE, BigDecimal::multiply).setScale(precision, RoundingMode.HALF_UP);
        product = nthRootOfBigDecimal(_dataSet.size(), product);
        return product;
    }

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
