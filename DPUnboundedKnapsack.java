// source: https://www.geeksforgeeks.org/unbounded-knapsack-repetition-items-allowed/
// with some modifications

public class DPUnboundedKnapsack {
    int W; // capacity
    int[] w; // weights
    int[] v; // values
    long maxVal; // maximum value

    private static long max(long i, long j) {
        return (i > j) ? i : j;
    }

    public DPUnboundedKnapsack(int W, int[] w, int[] v) {
        this.W = W;
        this.w = w;
        this.v = v;

        this.maxVal = unboundedKnapsack(W, w, v, w.length);
    }

    static long unboundedKnapsack(int W, int w[], int v[], int n) {
        long dp[] = new long[W + 1];

        // Fill dp[] using above recursive formula
        for (int i = 0; i <= W; i++) {
            for (int j = 0; j < n; j++) {
                if (w[j] <= i) {
                    dp[i] = max(dp[i], dp[i - w[j]] + v[j]);
                }
            }
        }
        return dp[W];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Maximum value: ").append(maxVal);

        return sb.toString();
    }

    public static void main(String args[]) {
        // example usage
        DPUnboundedKnapsack knapsack = new DPUnboundedKnapsack(
                15, new int[] { 1, 4, 6, 8 }, new int[] { 10, 40, 60, 80 });

        System.out.println(knapsack);
    }
}
