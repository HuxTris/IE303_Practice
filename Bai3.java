import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bai3 {
    static class Point implements Comparable<Point> {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point other) {
            if (this.x != other.x) {
                return this.x - other.x;
            }
            return this.y - other.y;
        }
    }

    static long cross(Point o, Point a, Point b) {
        return 1L * (a.x - o.x) * (b.y - o.y) - 1L * (a.y - o.y) * (b.x - o.x);
    }

    static List<Point> convexHull(List<Point> points) {
        int n = points.size();
        if (n <= 1) {
            return points;
        }

        Collections.sort(points);

        List<Point> lower = new ArrayList<>();
        for (Point p : points) {
            while (lower.size() >= 2
                    && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }

        List<Point> upper = new ArrayList<>();
        for (int i = points.size() - 1; i >= 0; i--) {
            Point p = points.get(i);
            while (upper.size() >= 2
                    && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }

        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return lower;
    }

    // Doi thu tu output cho gan voi vi du: bat dau tu diem ben trai nhat va di theo chieu kim dong ho
    static List<Point> toClockwiseFromLeftmost(List<Point> hull) {
        if (hull.size() <= 1) {
            return hull;
        }

        int start = 0;
        for (int i = 1; i < hull.size(); i++) {
            Point a = hull.get(i);
            Point b = hull.get(start);
            if (a.x < b.x || (a.x == b.x && a.y > b.y)) {
                start = i;
            }
        }

        List<Point> rotated = new ArrayList<>();
        for (int i = 0; i < hull.size(); i++) {
            rotated.add(hull.get((start + i) % hull.size()));
        }

        List<Point> clockwise = new ArrayList<>();
        clockwise.add(rotated.get(0));
        for (int i = rotated.size() - 1; i >= 1; i--) {
            clockwise.add(rotated.get(i));
        }
        return clockwise;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Integer> nums = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.trim().split("[^0-9-]+");
            for (String p : parts) {
                if (!p.isEmpty()) {
                    nums.add(Integer.parseInt(p));
                }
            }
        }

        if (nums.isEmpty()) {
            return;
        }

        int n = nums.get(0);
        List<Point> points = new ArrayList<>();

        int idx = 1;
        for (int i = 0; i < n && idx + 1 < nums.size(); i++) {
            int x = nums.get(idx++);
            int y = nums.get(idx++);
            points.add(new Point(x, y));
        }

        List<Point> hull = convexHull(points);
        List<Point> result = toClockwiseFromLeftmost(hull);

        for (Point p : result) {
            System.out.println(p.x + " " + p.y);
        }
    }
}
