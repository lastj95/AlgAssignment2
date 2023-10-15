import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * Program Authors: Matthew Bertello, James Last, Adeel Sultan
 */
public class Main {

    /*
     * Lets the user pick between the three algorithms encompassing this portion of the assignment.
     */
    public static void main(String[] args) {
        int alg = 0;

        // Asks user for input corresponding to one of the three problems.
        while (alg == 0) {
            System.out.print("Welcome to Assignment 2.\nEnter 1 for all algorithms to be run, enter 2 for Problem 2, " +
                            "enter 3 for Problem 3, or enter 4 for Problem 4: ");
            Scanner userIn = new Scanner(System.in);
            String input = userIn.nextLine();
            if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
                alg = Integer.parseInt(input);
            } else {
                System.out.println("Error: Valid input not given.");
            }
        }
        System.out.println();

        switch (alg) {
            case 2:
                marathonDifficultyHelper();
                break;
            case 3:
                compoundComparisonHelper();
                break;
            case 4:
                marathonWaterHelper();
                break;
            default:
                marathonDifficultyHelper();
                System.out.println();
                compoundComparisonHelper();
                System.out.println();
                marathonWaterHelper();
                System.out.println();
                break;
        }
    }

    /*
     * Grabs file input from input2.txt in order to deliver appropriate data (the desired amount of runners finishing
     * under time, the finish time, and each runner's time) to the appropriate recursive method. Then, outputs whether
     * the desired amount of runners was achieved.
     */
    public static void marathonDifficultyHelper() {
        // Get required info from input2.txt.
        int desiredAmountOfRunners;
        int finishTime;
        ArrayList<Integer> runnerTimes = new ArrayList<>();

        try {
            File inputFile = new File("./InputFiles/input2.txt");
            Scanner fileIn = new Scanner(inputFile);
            fileIn.useDelimiter(",");

            desiredAmountOfRunners = Integer.parseInt(fileIn.next());

            // Convert time from H:MM:SS to just seconds for simplicity.
            String[] finishTimeArray = fileIn.next().split(":");
            finishTime = Integer.parseInt(finishTimeArray[2]) + Integer.parseInt(finishTimeArray[1]) * 60 +
                    Integer.parseInt(finishTimeArray[0]) * 3600;

            // Get rest of times using same conversion method.
            while (fileIn.hasNext()) {
                String[] runnerTime = fileIn.next().split(":");
                runnerTimes.add(Integer.parseInt(runnerTime[2]) + Integer.parseInt(runnerTime[1]) * 60 +
                        Integer.parseInt(runnerTime[0]) * 3600);
            }

            } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Get number of runners under time.
        int numRunnersUnderTime = marathonDifficulty(runnerTimes, finishTime, 0, runnerTimes.size() - 1);

        // Decide whether the desired amount of runners was reached.
        if (numRunnersUnderTime == desiredAmountOfRunners) {
            System.out.println("OK");
        } else {
            System.out.println("problem");
        }
    }

    /*
     * Recursively go through list of runner times to find the time which is closest to the goal without being larger.
     * Then returns the amount of times that were under said goal time.
     */
    public static int marathonDifficulty(ArrayList<Integer> runnerTimes, int goalTime, int start, int end) {
        // Base case of end - start == 0, checking one element.
        if (start >= end) {
            if (runnerTimes.get(start) < goalTime) {
                // Must always remember to add one, otherwise we're only counting runners in front of this one.
                return start + 1;
            } else {
                return 0;
            }
        } else {
            int mid = start + ((end - start) / 2);
            if (runnerTimes.get(mid) < goalTime) {
                if (runnerTimes.get(mid + 1) >= goalTime) {
                    return mid + 1;
                }
                else {
                    return marathonDifficulty(runnerTimes, goalTime, mid + 1, end);
                }
            } else {
                return marathonDifficulty(runnerTimes, goalTime, start, mid - 1);
            }
        }
    }

    /*
     * Generates a list of Mars compounds with carbon, nitrogen, and oxygen values from a file input3.txt, and then
     * calls a recursive method to determine what pair of compounds generates the lowest energy score (and thus the
     * largest amount of energy).
     */
    public static void compoundComparisonHelper() {
        ArrayList<Integer[]> marsCompounds = new ArrayList<>();

        // Get all compounds from file into array.
        try {
            File inputFile = new File("./InputFiles/input3.txt");
            Scanner fileIn = new Scanner(inputFile);

            while (fileIn.hasNextLine()) {
                String compound = fileIn.nextLine();
                Scanner valueParser = new Scanner(compound);
                valueParser.useDelimiter(",");

                // With this, compound id is index 0, carbon is index 1, nitrogen is 2, and oxygen is 3.
                int compoundID = Integer.parseInt(valueParser.next());
                int carbonValue = Integer.parseInt(valueParser.next());
                int nitrogenValue = Integer.parseInt(valueParser.next());
                int oxygenValue = Integer.parseInt(valueParser.next());

                marsCompounds.add(new Integer[]{compoundID, carbonValue, nitrogenValue, oxygenValue});
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Sort list of compounds by their carbon value.
        marsCompounds.sort(Comparator.comparing(compound -> compound[1]));

        CompoundCombination bestCompounds = compoundComparison(marsCompounds, 0, marsCompounds.size() - 1);

        // Print results.
        System.out.println("The best compounds to combine are of ID numbers " + bestCompounds.id1 + " and " +
                            bestCompounds.id2 + ", with the resulting energy score being " + bestCompounds.energyScore
                            + ".");
    }

    /*
     * Recursively obtain the pair of compounds with the lowest energy score. This problem is essentially like figuring
     * the closest pair of points in 3D space, and is thus based upon our understanding of how such an algorithm works
     * based on the limited amount of information we could gather.
     */
    public static CompoundCombination compoundComparison(ArrayList<Integer[]> marsCompounds, int start, int end) {
        // Base case, if amount of compounds to check is 3 or less, then we just look through all combinations.
        // This MUST be three in order to avoid only comparing a compound with itself.
        if (end - start <= 2) {
            CompoundCombination bestCompounds = new CompoundCombination(-1, -2, Double.MAX_VALUE);

            for (int i = start; i <= end; i++) {
                for (int j = i + 1; j <= end; j++) {
                    double energyScore = getEnergyScore(marsCompounds.get(i), marsCompounds.get(j));
                    if (energyScore < bestCompounds.energyScore) {
                        bestCompounds = new CompoundCombination(marsCompounds.get(i)[0], marsCompounds.get(j)[0],
                                energyScore);
                    }
                }
            }

            return bestCompounds;
        }
        // Recursive case.
        else {
            // Recursively look at the left half and right half of points.
            int mid = start + ((end - start) / 2);
            CompoundCombination leftBest = compoundComparison(marsCompounds, start, mid);
            CompoundCombination rightBest = compoundComparison(marsCompounds, mid + 1, end);

            // Determine best pair of compounds from each half.
            CompoundCombination bestCompounds;
            if (leftBest.energyScore < rightBest.energyScore) {
                bestCompounds = leftBest;
            } else {
                bestCompounds = rightBest;
            }

            // Get the boundaries of which points to check near the middle.
            int midCarbon = marsCompounds.get(mid)[1];
            double delta = bestCompounds.energyScore;
            double minCarbon = midCarbon - delta;
            double maxCarbon = midCarbon + delta;

            // Get list of compounds within this range of carbon and sort by nitrogen value.
            ArrayList<Integer[]> compoundsToCheck = new ArrayList<>(marsCompounds.subList(start, end + 1));

            compoundsToCheck.removeIf(compound -> compound[1] < minCarbon || compound[1] > maxCarbon);
            compoundsToCheck.sort(Comparator.comparing(compound -> compound[2]));

            // Check each compound in the list to the next 28 compounds. A lower number may be able to be used and still
            // achieve the correct answer, but we couldn't seek out a specific mathematical proof for the closest point
            // problem in three dimensions.
            for (int i = 0; i < compoundsToCheck.size(); i++) {
                for (int j = i + 1; j < compoundsToCheck.size() && j < i + 29; j++) {
                    double energyScore = getEnergyScore(marsCompounds.get(i), marsCompounds.get(j));
                    if (energyScore < bestCompounds.energyScore) {
                        bestCompounds = new CompoundCombination(marsCompounds.get(i)[0], marsCompounds.get(j)[0],
                                energyScore);
                    }
                }
            }

            // Return the pair of compounds with the lowest energy score.
            return bestCompounds;
        }
    }

    /*
     * Gets the energy score of two given compounds.
     */
    public static double getEnergyScore (Integer[] compound1, Integer[] compound2) {
        return Math.sqrt(Math.pow(compound1[1] - compound2[1], 2) + Math.pow(compound1[2] - compound2[2], 2) +
                         Math.pow(compound1[3] - compound2[3], 2));
    }

    /*
     * Class for two compounds being compared, making for easy tracking of the lowest energy score (for the purposes
     * of the recursive method) and the two compounds' IDs (for the purpose of reporting to the user at the end of the
     * helper method).
     */
    public static class CompoundCombination {
        int id1;
        int id2;
        double energyScore;

        CompoundCombination(int id1, int id2, double energyScore) {
            this.id1 = id1;
            this.id2 = id2;
            this.energyScore = energyScore;
        }
    }


    /*
     * Grabs input from input4.txt in order to obtain the amount of water stops per runner, so that said data can be
     * sent down to a recursive function to determine the optimal amount of water and where in the array this number
     * was derived from. This is then outputted to the user.
     */
    public static void marathonWaterHelper() {
        ArrayList<Integer> waterStopsPerRunner = new ArrayList<>();

        // Get all water stops into array.
        try {
            File inputFile = new File("./InputFiles/input4.txt");
            Scanner fileIn = new Scanner(inputFile);
            fileIn.useDelimiter(",");

            while (fileIn.hasNext()) {
                String waterStops = fileIn.next();
                waterStopsPerRunner.add(Integer.parseInt(waterStops));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Get results recursively.
        int[] results = marathonWater(waterStopsPerRunner, 0, waterStopsPerRunner.size() - 1);

        // Print out results.
        System.out.println("Amount of water: " + results[0] + "\nStart point in array: " + results[1]
                            + "\nEnd point in array: " + results[2]);
    }

    /*
     * Recursively determines the optimal amount of water and where in the array this number is derived from.
     */
    public static int[] marathonWater(ArrayList<Integer> waterStopsPerRunner, int start, int end) {
        if (start == end) {
            return new int[]{waterStopsPerRunner.get(start), start, end};
        } else {
            // Get the best results on the left and right hand sides.
            int mid = start + ((end - start) / 2);
            int[] leftBest = marathonWater(waterStopsPerRunner, start, mid);
            int[] rightBest = marathonWater(waterStopsPerRunner, mid + 1, end);

            // Get the best section between the two sides on their own.
            int[] bestOverall;
            if ((leftBest[0] > rightBest[0])) {
                bestOverall = leftBest;
            } else {
                bestOverall = rightBest;
            }

            // Look at section crossing between two sides, adding the greatest element bordering the "cross-section"
            // each time
            int leftBound = mid;
            int rightBound = mid + 1;
            int minStops = Math.min(waterStopsPerRunner.get(leftBound), waterStopsPerRunner.get(rightBound));
            int waterAmount = minStops * 2;
            if (waterAmount > bestOverall[0]) {
                bestOverall = new int[]{waterAmount, leftBound, rightBound};
            }

            while (leftBound > start || rightBound < end) {
                int newIndex;
                // Case where either side can still be checked.
                if (leftBound > start && rightBound < end) {
                    if (waterStopsPerRunner.get(leftBound - 1) > waterStopsPerRunner.get(rightBound + 1)) {
                        leftBound--;
                        newIndex = leftBound;
                    } else {
                        rightBound++;
                        newIndex = rightBound;
                    }
                // Case where only the left side can be checked (reached "end" on right side)
                } else if (leftBound > start) {
                    leftBound--;
                    newIndex = leftBound;
                // Case where only the right side can be checked (reached "start" on left side)
                } else {
                    rightBound++;
                    newIndex = rightBound;
                }

                // Update the minimum number of stops taken by each runner in the cross-section being checked.
                minStops = Math.min(minStops, waterStopsPerRunner.get(newIndex));
                waterAmount = minStops * (rightBound - leftBound + 1);
                if (waterAmount > bestOverall[0]) {
                    bestOverall = new int[]{waterAmount, leftBound, rightBound};
                }
            }

            // After the above loop the best overall section to base our water count on has been found.
            return bestOverall;
        }
    }

}