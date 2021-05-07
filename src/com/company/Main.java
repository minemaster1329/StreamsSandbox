package com.company;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

import com.company.datacollections.*;

public class Main {

    public static void main(String[] args) {
	    try {
            File data1 = new File("data2.txt");
            //File data2 = new File("data2.txt");

            Scanner file_1_scanner = new Scanner(data1);
            //Scanner file_2_scanner = new Scanner(data2);

            FrequencyDistributionsAbstract data_1_collection = new PointFrequencyDistribution(10);
            RangeFrequencyDistribution data_1_collection_range = new RangeFrequencyDistribution(10);

            ArrayList<BigDecimal> data_list = new ArrayList<>();

            while (file_1_scanner.hasNext()) data_list.add(file_1_scanner.nextBigDecimal());

            for (BigDecimal bd : data_list){
                data_1_collection.addNewItemToCollection(bd);
                data_1_collection_range.addNewItemToCollection(bd);
            }

            //System.out.printf("Elements count: %d\n", data_1_collection.getDataSet().size());
            System.out.printf("Arithmetic average: %s\n",data_1_collection.getArithmeticAverage().toString());
            /*System.out.printf("Quadratic average: %s\n",data_1_collection.getQuadraticAverage().toString());
            System.out.printf("Geometric average: %s\n",data_1_collection.getGeometricAverage().toString());
            System.out.printf("Harmonic average: %s\n",data_1_collection.getHarmonicAverage().toString());
            System.out.printf("Median: %s\n",data_1_collection.getMedian().toString());
            ArrayList<BigDecimal> modes = data_1_collection.getMode();
            System.out.println("Modes: "+modes);
            System.out.println("Quartile Q1: "+data_1_collection.getQuartile(1));
            System.out.println("Quartile Q2: "+data_1_collection.getQuartile(2));
            System.out.println("Quartile Q3: "+data_1_collection.getQuartile(3));
            System.out.println("Variancy UB: "+data_1_collection.getUnbiasWariancy());
            System.out.println("Variancy B: "+data_1_collection.getBiasWariancy());
            System.out.println("Standard Deviation UB: "+data_1_collection.getUnbiasStandardDeviation());
            System.out.println("Standard Deviation B: "+data_1_collection.getBiasStandardDeviation());
            System.out.println("Common Deviation: "+data_1_collection.getCommonDeviation());
            System.out.println("Common Deviation From Median: "+data_1_collection.getCommonDeviationFromMedian());
            System.out.println("Quarter Deviation: "+data_1_collection.getQuarterDeviation());
            System.out.println("Percent coefficient of variation: "+data_1_collection.getCoefficentOfVariation());
            System.out.println("Percent positional coefficient of variation: "+data_1_collection.getPositionalCoefficentOfVariation());
            System.out.println("Skewness: "+data_1_collection.getSkewness());
            System.out.println("Kurtosis: "+data_1_collection.getKurtosis());
            System.out.println("Excess: "+data_1_collection.getExcess());
            */
            data_1_collection_range.generateRangeFrequencyDistribution();
            System.out.println(data_1_collection_range.getArithmeticAverage());
	    }
	    catch (Exception e){
	        System.out.println(e.getMessage());
        }
    }
}
