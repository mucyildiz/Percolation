/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int[] grid; // this will be our 1D representation of our 2D grid
    private WeightedQuickUnionUF uf;
    private int numSites;
    private int width;
    private int numOpenSites;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be in the natural numbers");
        }
        width = n;
        // initialize the number of sites as n*n since it's a square grid with width n
        numSites = n * n;
        // we model the grid with +2 for the virtual top and bottom nodes we will use to see if the top and bottom are connected
        uf = new WeightedQuickUnionUF(numSites + 2);
        grid = new int[numSites]; // we initialize the grid with n*n indices
        // I made the first index 1 so that no value i for grid[i] is equal to 0. Then I'm gonna open a spot by saying grid[i] = 0 and that a spot is open iff grid[i] = 0.
        grid[0] = 1;
        for (int i = 1; i < numSites; i++) {
            // each index except for 0 is initialized as itself
            grid[i] = i;
        }


    }

    // use this method to ensure that row and col are within ranges that make sense
    private void checkBounds(int row, int col) {
        if (row < 1 || col < 1 || row > width || col > width) {
            throw new IllegalArgumentException(
                    "Rows and Columns must be between 1 and " + width + ". ");
        }
    }

    // we model opening by connecting a newly opened site to all of its adjacent open sites meaning we check left, right, up or down is open, and then we union with them if they are
    private int rowColTo1D(int row, int col) {
        return ((row - 1) * width + (col - 1));
    }

    public void open(int row, int col) {
        checkBounds(row, col);
        int i = rowColTo1D(row, col);
        // we established that a site was open if its value at its index was 0
        grid[i] = 0;
        if (row == 1) {
            // if the site being opened is in the first row, then we connect it to the top node. This becomes useful in checking if the site is full or not, since a site is full if it connects to the top row.
            uf.union(i, numSites);
        }
        // width is the value n, and the nth row is the last row. Thus, if the site being opened is in the last row, we connect it to numSites + 1. This will be useful for seeing if the system percolates or not, since it percolates iff the bottom row is connected to the top row.
        if (row == width) {
            uf.union(i, numSites + 1);
        }
        // we use this variable to keep track of the number of open sites, this will be important in calculating the percolation coefficient
        numOpenSites++;

        if (row > 1) {
            // has to start at the second row because there's no sites above the site if the site exists in the first row
            if (isOpen(row - 1, col)) // checks if the value right above our (row, col) is open
            {
                // connects the site passed into the method with the site directly above it in the case that the site above is open
                uf.union(i, rowColTo1D(row - 1, col));
            }
        }
        if (row < width) {
            // theres no sites below the site if the site is in the last row
            if (isOpen(row + 1, col)) // checks value right below (row, col)
            {
                uf.union(i, rowColTo1D(row + 1, col));
            }
        }
        if (col > 1) {
            if (isOpen(row, col - 1)) // checks site to the left of (row, col)
            {
                uf.union(i, rowColTo1D(row, col - 1));
            }
        }
        if (col < width) {
            if (isOpen(row, col + 1)) // checks site to the right of (row, col)
            {
                uf.union(i, rowColTo1D(row, col + 1));
            }
        }
    }


    public boolean isOpen(int row, int col) {
        checkBounds(row, col);
        // make it a single integer value so that we can utilize our 1D array grid[]
        int i = rowColTo1D(row, col);
        // a value in grid is open if and only if its value at that index is 0. Our initial array is all closed because I ensured that there would be no values i such that grid[i] = 0
        return (grid[i] == 0);
    }

    // we gotta check if the given site connects with the virtual top spot that is connected to all sites in the top row
    public boolean isFull(int row, int col) {
        checkBounds(row, col);
        int i = rowColTo1D(row, col);
        // initially only sites in the top row are connected to the numSites. so if i is connected to numSites, then i is connected to the top row, then the site is full.
        return uf.connected(i, numSites);
    }

    public int numberOfOpenSites() {
        return numOpenSites; // we will use this in the PercolationStats
    }

    // we check if the virtual bottom spot connected to all the sites in the bottom row is connected to the
    public boolean percolates() {
        // the only sites initially connected to numSites are open sites in the top row. the only sites initially connected to numSites + 1 are open sites in the bottom row. Then if numSites and numSites + 1 are connected, the system percolates.
        return uf.connected(numSites, numSites + 1);
    }

    public static void main(String[] args) {
        Percolation perc = new Percolation(200);
        for (int i = 1; i <= 165; i++) {
            perc.open(i, 4);
        }
        System.out.println(perc.percolates());
        System.out.println(perc.numberOfOpenSites());
        System.out.println(perc.isFull(150, 4));
    }
}
