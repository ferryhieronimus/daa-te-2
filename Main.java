import java.io.*;
import java.util.*;

public class Main {

    public static class WeightsValues {
        public int[] w;
        public int[] v;

        public WeightsValues(int[] w, int[] v) {
            this.w = w;
            this.v = v;
        }
    }

    public static WeightsValues readArraysFromFile(String fileName, int size) {
        int[] w = new int[size];
        int[] v = new int[size];
        int index = 0;

        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextInt() && index < size) {
                int value1 = scanner.nextInt();
                w[index] = value1;
                index++;
            }

            index = 0;
            while (scanner.hasNextInt() && index < size) {
                int value2 = scanner.nextInt();
                v[index] = value2;
                index++;
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new WeightsValues(w, v);
    }

    // sanity check
    public static void test() {
        WeightsValues wv1 = readArraysFromFile("datasets/small.txt", 100);
        WeightsValues wv2 = readArraysFromFile("datasets/medium.txt", 1000);
        WeightsValues wv3 = readArraysFromFile("datasets/large.txt", 10000);

        BnBUnboundedKnapsack BnB1 = new BnBUnboundedKnapsack(10000, wv1.w, wv1.v);
        BnBUnboundedKnapsack BnB2 = new BnBUnboundedKnapsack(10000, wv2.w, wv2.v);
        BnBUnboundedKnapsack BnB3 = new BnBUnboundedKnapsack(10000, wv3.w, wv3.v);
        DPUnboundedKnapsack DP1 = new DPUnboundedKnapsack(10000, wv1.w, wv1.v);
        DPUnboundedKnapsack DP2 = new DPUnboundedKnapsack(10000, wv2.w, wv2.v);
        DPUnboundedKnapsack DP3 = new DPUnboundedKnapsack(10000, wv3.w, wv3.v);

        if (BnB1.z_hat != DP1.maxVal) {
            throw new AssertionError("Test failed");
        }
        if (BnB2.z_hat != DP2.maxVal) {
            throw new AssertionError("Test failed");
        }
        if (BnB3.z_hat != DP3.maxVal) {
            throw new AssertionError("Test failed");
        }

        System.out.println("Test successful");
    }

    // somehow looping the test doesn't work
    // so i have to rerun the test for each dataset
    // and technique
    public static void main(String[] args) {
        // pilih salah satu untuk dataset
        WeightsValues wv = readArraysFromFile("datasets/small.txt", 100);
        // WeightsValues wv = readArraysFromFile("datasets/medium.txt", 1000);
        // WeightsValues wv = readArraysFromFile("datasets/large.txt", 10000);

        long startTime = System.nanoTime();
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // pilih metode
        new BnBUnboundedKnapsack(10000, wv.w, wv.v);
        // new BnBUnboundedKnapsack(10000, wv.w, wv.v);
        // new BnBUnboundedKnapsack(10000, wv.w, wv.v);
        // new DPUnboundedKnapsack(10000, wv.w, wv.v);
        // new DPUnboundedKnapsack(10000, wv.w, wv.v);
        // new DPUnboundedKnapsack(10000, wv.w, wv.v);

        long endTime = System.nanoTime();
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time (milliseconds): " + elapsedTime / 1000000.0);

        long actualMemUsed = afterUsedMem - beforeUsedMem;
        System.out.println("Memory used: " + actualMemUsed);

        test();
    }
}
