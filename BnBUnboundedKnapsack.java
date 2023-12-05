// source: https://www.tandfonline.com/doi/pdf/10.1057/palgrave.jors.2601698
// with some modifications

import java.util.*;

public class BnBUnboundedKnapsack {
    int W; // capacity
    int[] w; // weights
    int[] v; // values
    long[][] M; // matrix
    ArrayList<int[]> N; // set of items [w, v]

    long[] x_hat; // current best solution
    long[] x; // current feasible solution
    long z_hat; // current best solution value

    public BnBUnboundedKnapsack(int W, int[] w, int[] v) {
        this.W = W;
        this.w = w;
        this.v = v;
        this.N = new ArrayList<>();
        for (int i = 0; i < v.length; i++) {
            this.N.add(new int[] { w[i], v[i] });
        }
        this.initialize();
    }

    public long calculateU(long WPrime, long VN, int i) {
        if (v.length <= i + 2) {
            return VN;
        } else {
            int v1 = v[i];
            int v2 = v[i + 1];
            int v3 = v[i + 2];
            int w1 = w[i];
            int w2 = w[i + 1];
            int w3 = w[i + 2];

            long zPrime = VN + (int) (Math.floor(WPrime / w2) * v2);
            long WDoublePrime = WPrime - (int) (Math.floor(WPrime / w2) * w2);
            long UPrime = zPrime + (int) (Math.floor(WDoublePrime * (v3 / w3)));

            long UDoublePrime = zPrime + (int) (Math.floor(
                    ((WDoublePrime + (int) (Math.ceil((1.0 / w1) * (w2 - WDoublePrime)) * w1)) * (v2 / w2))
                            - (Math.ceil((1.0 / w1) * (w2 - WDoublePrime)) * v1)));

            return Math.max(UPrime, UDoublePrime);
        }
    }

    // step 1
    public void initialize() {
        // eliminate dominated items according to Procedure 1
        for (int j = 0; j < N.size() - 1; j++) {
            for (int k = j + 1; k < N.size(); k++) {
                int wj = N.get(j)[0];
                int wk = N.get(k)[0];
                int vj = N.get(j)[1];
                int vk = N.get(k)[1];

                if (Math.floorDiv(wk, wj) * vj >= vk) {
                    N.remove(k);
                } else if (Math.floorDiv(wj, wk) * vk >= vj) {
                    N.remove(j);
                    k = N.size(); // break
                }
            }
        }

        // sort the non-dominated items according to decreasing vi/wi ratios
        Collections.sort(N, Comparator.comparingDouble(arr -> (double) -(arr[1] / arr[0])));

        v = N.stream().mapToInt(i -> i[1]).toArray();
        w = N.stream().mapToInt(i -> i[0]).toArray();

        // x_hat = [0, ..., 0]; x = [0, ..., 0]; z_hat = 0
        x_hat = new long[N.size()];
        x = new long[N.size()];
        z_hat = 0;

        // initialize empty sparse matrix
        M = new long[N.size()][W];

        // calculate x1, V(N), W'
        x[0] = Math.floorDiv(W, w[0]);
        long VN = x[0] * v[0];
        long WPrime = W - x[0] * w[0];

        // calculate U
        long U = calculateU(WPrime, VN, 0);

        // Find mi = min{wj:j>i} for all i = 1, 2, ..., n'
        ArrayList<Long> m = new ArrayList<>();
        for (int i = 0; i < N.size(); i++) {
            long minW = Long.MAX_VALUE;
            for (int j = 0; j < N.size(); j++) {
                if (j > i && w[j] < minW) {
                    minW = w[j];
                }
            }
            m.add(minW);
        }

        // initialization complete, go to step 2
        develop(WPrime, m, VN, 0, U);
    }

    // step 2
    public void develop(long WPrime, ArrayList<Long> m, long VN, int i, long U) {
        if (WPrime < m.get(i)) {
            if (z_hat < VN) {
                z_hat = VN;
                x_hat = Arrays.copyOf(x, x.length);
                if (z_hat == U) {
                    // then go to Step 5
                    finish();
                }
            }
            // then go to Step 3.
            backtrack(WPrime, m, VN, i, U);
        } else {
            // Find min j such that j > i and wj < W'
            int minJ = -1;
            for (int j = i + 1; j < N.size(); j++) {
                if (w[j] <= WPrime) {
                    minJ = j;
                    break;
                }
            }
            if (VN + calculateU(WPrime, VN, minJ) <= z_hat) {
                // then go to Step 3.
                backtrack(WPrime, m, VN, i, U);
            } else if (M[i][(int) WPrime] >= VN) {
                // then go to Step 3.
                backtrack(WPrime, m, VN, i, U);
            } else {
                x[minJ] = (int) Math.floor(WPrime / w[minJ]);
                VN += v[minJ] * x[minJ];
                WPrime -= w[minJ] * x[minJ];
                M[i][(int) WPrime] = VN;
                i = minJ;
                // Go to Step 2.
                develop(WPrime, m, VN, i, U);
            }
        }
    }

    // step 3
    public void backtrack(long WPrime, ArrayList<Long> m, long VN, int i, long U) {
        // find max j such that j <= i and xj > 0.
        int maxJ = -1;
        for (int j = 0; j <= i; j++) {
            if (x[j] > 0) {
                maxJ = j;
            }
        }
        if (maxJ == -1) {
            // then go to Step 5
            finish();
        } else {
            // i = j, xi = xi - 1
            i = maxJ;
            x[i] = x[i] - 1;

            // V(N) = V(N) - vi, W' = W' + wi
            VN = VN - v[i];
            WPrime = WPrime + w[i];

            if (WPrime < m.get(i)) {
                // then go to Step 3.
                backtrack(WPrime, m, VN, i, U);
            } else if (VN + (int) Math.floor(WPrime * (double) (v[i + 1] / w[i + 1])) <= z_hat) {
                VN -= v[i] * x[i];
                WPrime += w[i] * x[i];
                x[i] = 0;
                // then go to Step 3.
                backtrack(WPrime, m, VN, i, U);
            } else if (WPrime >= m.get(i)) {
                // then go to Step 2.
                develop(WPrime, m, VN, i, U);
            } else {
                replace(WPrime, m, VN, i, U);
            }
        }
    }

    // step 4
    public void replace(long WPrime, ArrayList<Long> m, long VN, int i, long U) {
        int j = i;
        int h = j + 1;
        if (z_hat >= VN + (int) Math.floor(WPrime * (v[h] / w[h]))) {
            // then go to Step 3.
            backtrack(WPrime, m, VN, i, U);
        }

        if (w[h] >= w[j]) {
            if (w[h] == w[j] || w[h] > WPrime || z_hat >= VN + v[h]) {
                h++;
                // Go to Step 4.
                replace(WPrime, m, VN, i, U);
            } else {
                z_hat = VN + v[h];
                x_hat = Arrays.copyOf(x, x.length);
                x[h] = 1;
                if (z_hat == U) {
                    // then go to Step 5.
                    finish();
                }
                j = h;
                h++;
                // Go to Step 4.
                replace(WPrime, m, VN, i, U);
            }
        } else {
            if (WPrime - w[h] < m.get(h - 1)) {
                h++;
                // Go to Step 4.
                replace(WPrime, m, VN, i, U);
            } else {
                i = h;
                VN += v[i] * x[i];
                WPrime -= w[i] * x[i];
                // Go to Step 2.
                develop(WPrime, m, VN, i, U);
            }
        }
    }

    // step 5
    public void finish() {
        return;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Best solution: ").append(Arrays.toString(x_hat)).append("\n");
        sb.append("Maximum value: ").append(z_hat);
        return sb.toString();
    }

    public static void main(String[] args) {
        // example usage
        BnBUnboundedKnapsack BnB = new BnBUnboundedKnapsack(
                15, new int[] { 1, 4, 6, 8 }, new int[] { 10, 40, 60, 80 });

        System.out.println(BnB);
    }
}
