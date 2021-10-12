package com.company.dp_datacollections;

import java.util.List;

public abstract class DoublePrecisionFrequencyDistributionAbstract {
    List<Double> values;
    public long getValuesCount() {return values.size();}

    public void addElementToCollection(Double value) {values.add(value);}
}
