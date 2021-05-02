package com.company;

import java.io.File;
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

            System.out.println(data_1_collection.getArithmeticAverage());
            System.out.println(data_1_collection.getQuadraticAverage());
            System.out.println(data_1_collection.getGeometricAverage());
	    }
	    catch (Exception e){
	        System.out.println(e.getMessage());
        }
    }
}
