package com.company.pub;

import java.util.ArrayList;

public abstract class DataSetClass<T> {
    ArrayList<T> _dataSet;

    public DataSetClass() {
        this._dataSet = new ArrayList<>();
    }
}
