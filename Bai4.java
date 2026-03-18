import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bai4 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder all = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            all.append(line).append(" ");
        }

        String[] parts = all.toString().trim().split("[^0-9-]+");
        List<Integer> nums = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) {
                nums.add(Integer.parseInt(p));
            }
        }

        if (nums.size() < 2) {
            return;
        }

        int n = nums.get(0);
        int k = nums.get(1);
        int[] a = new int[n];

        for (int i = 0; i < n && i + 2 < nums.size(); i++) {
            a[i] = nums.get(i + 2);
        }

        int NEG = -1_000_000_000;
        int[][] dp = new int[n + 1][k + 1];

        for (int s = 1; s <= k; s++) {
            dp[0][s] = NEG;
        }
        dp[0][0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int s = 0; s <= k; s++) {
                dp[i][s] = dp[i - 1][s];

                if (s >= a[i - 1] && dp[i - 1][s - a[i - 1]] != NEG) {
                    int take = dp[i - 1][s - a[i - 1]] + 1;
                    if (take > dp[i][s]) {
                        dp[i][s] = take;
                    }
                }
            }
        }

        if (dp[n][k] < 0) {
            System.out.println("Khong co day con nao");
            return;
        }

        List<Integer> answer = new ArrayList<>();
        int s = k;
        for (int i = n; i >= 1; i--) {
            if (dp[i][s] == dp[i - 1][s]) {
                continue;
            }

            answer.add(a[i - 1]);
            s -= a[i - 1];
        }

        Collections.reverse(answer);
        for (int i = 0; i < answer.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(answer.get(i));
        }
        System.out.println();
    }
}
