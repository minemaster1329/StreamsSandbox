package com.company;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    try {
            File data1 = new File("data2.txt");
            //File data2 = new File("data2.txt");

            Scanner file_1_scanner = new Scanner(data1);
            //Scanner file_2_scanner = new Scanner(data2);

            StatisticalDataCollection data_1_collection = new StatisticalDataCollection(5);

            while (file_1_scanner.hasNext()) data_1_collection.addNewItemToCollection(file_1_scanner.nextBigDecimal());

            System.out.printf("Arithmetic average: %s\n",data_1_collection.getArithmeticAverage().toString());
            System.out.printf("Quadratic average: %s\n",data_1_collection.getQuadraticAverage().toString());
            System.out.printf("Geometric average: %s\n",data_1_collection.getGeometricAverage().toString());
            System.out.printf("Harmonic average: %s\n",data_1_collection.getHarmonicAverage().toString());
            System.out.printf("Median: %s\n",data_1_collection.getMedian().toString());
            ArrayList<BigDecimal> modes = data_1_collection.getMode();
            System.out.println("Modes: "+modes);
            System.out.println("Quartile Q1: "+data_1_collection.getQuartile(1));
            System.out.println("Quartile Q2: "+data_1_collection.getQuartile(2));
            System.out.println("Quartile Q3: "+data_1_collection.getQuartile(3));
	    }
	    catch (Exception e){
	        System.out.println(e.getMessage());
        }
    }
}
