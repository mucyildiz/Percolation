/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */
// @formatter:off

import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {
    private double sampleMean;
    private double sampleVariance;
    private int numTrials;

    public PercolationStats(int n, int trials) {
        numTrials = trials; // for use in confidence interval methods
        if (n <= 0) {
            throw new IllegalArgumentException("n must be in the natural numbers");
        }
        if (trials < 0) {
            throw new IllegalArgumentException("the number of trials must be positive");
        }
        double[] trialMeans = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(
                    n); // we initialize a new Percolation object everytime the for loop iterates
            while (!perc.percolates())
            // we randomly open new spots until perc percolates
            {
                int j = StdRandom.uniform(1, n + 1);
                int k = StdRandom.uniform(1, n + 1);
                if (!perc.isOpen(j, k)) {
                    perc.open(j, k);
                }
            }
            // numberOfOpenSites is an integer and we're about to divide it so we must make it a double value to get an accurate answer
            double openSitesForDiv = (double) (perc.numberOfOpenSites());
            // this is the percentage of sites that had to be opened for the grid to percolate
            double percolationThreshold = openSitesForDiv / (n * n);
            trialMeans[i] = percolationThreshold;

            for (int r = 0; r < trials; r++) {
                // we add up all of the sample means
                sampleMean += trialMeans[r];
            }
            // we divide by the number of trials to get the sample mean
            sampleMean /= trials;

            for (int s = 0; s < trials; s++) {
                // the formula is ((x1 - x)^2 + (x2-x)^2 + ... (xt-x)^2) / T - 1, where x is the sample mean
                sampleVariance += Math.pow(trialMeans[s] - sampleMean, 2);
            }
            sampleVariance /= (trials - 1);

        }

    }

    // returns sample mean
    public double mean() {
        return sampleMean;
    }

    // returns sample stddev
    public double stddev() {
        return Math.sqrt(sampleVariance);
    }

    // returns lower endpoint of a 95% confidence interval
    public double confidenceLo() {
        // this is the formula for the lower endpoint for a 95% confidence interval
        return (sampleMean - ((1.96 * stddev()) / Math.sqrt(numTrials)));
    }

    // returns higher endpoint of a 95% confidence interval
    public double confidenceHi() {
        return (sampleMean + ((1.96 * stddev()) / Math.sqrt(numTrials)));
    }

    public static void main(String[] args) {
        PercolationStats percStats = new PercolationStats(100, 10000);
        System.out.println("The mean is " + percStats.mean());
        System.out.println("The standard deviation is " + percStats.stddev());
        System.out.println("The interval is [" + percStats.confidenceLo() + ", " + percStats.confidenceHi() + "]");
    }
}
