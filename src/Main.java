import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

        switch (alg) {
            case 2:
                marathonDifficultyHelper();
                break;
            case 3:
                // TODO: Problem 3 alg goes here.
                break;
            case 4:
                // TODO: Problem 4 alg goes here.
                break;
            default:
                marathonDifficultyHelper();
                System.out.println();
                // TODO: Problem 3 alg goes here.
                System.out.println();
                // TODO: Problem 4 alg goes here.
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
}