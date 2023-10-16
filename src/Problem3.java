import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Algorithm for problem 3. In order to make sense of this problem more, we treated each compound as a point in 3D
 * space.
 */
public class Problem3 {

    public static class Point {
        public int point;
        public int x;
        public int y;
        public int z;
    }

    public static class PointPair {
        public Point point1;
        public Point point2;
        public float distance;

        public PointPair(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = getDistance(point1, point2);
        }
    }

    public static void problem3() {
        ArrayList<Point> points = readInputs("./InputFiles/input3.txt");

        PointPair bestPair = findClosestPair(points);
        System.out.println("Best energy score: " + bestPair.distance);
        System.out.println("Best compound 1: " + bestPair.point1.point);
        System.out.println("Best compound 2: " + bestPair.point2.point);
    }

    public static PointPair findClosestPair(ArrayList<Point> points) {
        // sort the points by x
        points.sort((p1, p2) -> p1.x - p2.x);

        return findClosestPairHelper(points, 0, points.size() - 1);
    }

    public static PointPair findClosestPairHelper(ArrayList<Point> points, int start, int end) {

        if (end == start) {
            return null;
        } else if (end - start < 2) {
            PointPair bestPair = new PointPair(points.get(start), points.get(end));
            return bestPair;
        }

        int mid = start + ((end - start) / 2);

        PointPair leftPair = findClosestPairHelper(points, start, mid);
        PointPair rightPair = findClosestPairHelper(points, mid + 1, end);

        PointPair bestPair = null;

        if (leftPair == null) {
            bestPair = rightPair;
        } else if (rightPair == null) {
            bestPair = leftPair;
        } else {
            if (leftPair.distance < rightPair.distance) {
                bestPair = leftPair;
            } else {
                bestPair = rightPair;
            }
        }

        float center = (points.get(mid).x + points.get(mid + 1).x) / 2;

        // Create a list of points that are within the center range
        ArrayList<Point> centerPoints = new ArrayList<Point>();

        for (int i = start; i <= end; i++) {
            float distance = Math.abs(points.get(i).x - center);
            if (distance < bestPair.distance) {
                centerPoints.add(points.get(i));
            }
        }

        return SearchStrips(centerPoints, bestPair);
    }

    static public PointPair SearchStrips(ArrayList<Point> points, PointPair bestPair) 
    {

        if(points.size() < 2) {
            return bestPair;
        }

        // Separate the center points into strips
        ArrayList<ArrayList<Point>> strips = new ArrayList<ArrayList<Point>>();

        // Get the range of y values
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for (Point point : points) {
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        float yRange = maxY - minY;
        points.sort((p1, p2) -> p1.z - p2.z);

        // Get the number of strips
        int numStrips = (int) Math.ceil(yRange / bestPair.distance);

        // Separate the points into strips
        for (int i = 0; i < numStrips; i++) {
            strips.add(new ArrayList<Point>());
        }

        for (Point point : points) {
            int stripIndex = (int) Math.floor((point.y - minY) / bestPair.distance);
            strips.get(stripIndex).add(point);
        }

        // Find the closest pair in the strips
        for (int i = 0; i < strips.size(); i++) {
            ArrayList<Point> strip = strips.get(i);
            for (int j = 0; j < strip.size(); j++) {
                Point point = strip.get(j);
                for (int k = j + 1; k < strip.size(); k++) {
                    Point nextPoint = strip.get(k);

                    // If the z distance is greater than the best distance, then we can skip the
                    // rest of the points in the strip
                    if (Math.abs(point.z - nextPoint.z) > bestPair.distance) {
                        break;
                    }

                    PointPair currentPair = new PointPair(point, nextPoint);
                    if (currentPair.distance < bestPair.distance) {
                        bestPair = currentPair;
                    }
                }

                if (i < strips.size() - 1) {

                    float lowerZ = point.z - bestPair.distance;

                    // Find the first point in the next strip that is greater than the lowerZ with a
                    // binary search
                    int lowerIndex = 0;
                    int upperIndex = strips.get(i + 1).size() - 1;
                    int midIndex = 0;

                    while (lowerIndex <= upperIndex) {
                        midIndex = lowerIndex + ((upperIndex - lowerIndex) / 2);
                        Point midPoint = strips.get(i + 1).get(midIndex);
                        if (midPoint.z < lowerZ) {
                            lowerIndex = midIndex + 1;
                        } else if (midPoint.z > lowerZ) {
                            upperIndex = midIndex - 1;
                        } else {
                            break;
                        }
                    }

                    // Search the next strip for points that are within the best distance
                    for (int k = midIndex; k < strips.get(i + 1).size(); k++) {
                        Point nextPoint = strips.get(i + 1).get(k);
                        if (Math.abs(point.z - nextPoint.z) > bestPair.distance) {
                            break;
                        }
                        PointPair currentPair = new PointPair(point, nextPoint);
                        if (currentPair.distance < bestPair.distance) {
                            bestPair = currentPair;
                        }
                    }
                }
            }
        }

        return bestPair;
    }

    static public float getDistance(Point point1, Point point2) {
        return (float) Math.sqrt(
                Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2) + Math.pow(point1.z - point2.z, 2));
    }

    static public ArrayList<Point> readInputs(String filename) {
        ArrayList<Point> points = new ArrayList<Point>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNext()) {
                Point point = new Point();
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                point.point = Integer.parseInt(parts[0]);
                point.x = Integer.parseInt(parts[1]);
                point.y = Integer.parseInt(parts[2]);
                point.z = Integer.parseInt(parts[3]);
                points.add(point);
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return points;
    }
}
