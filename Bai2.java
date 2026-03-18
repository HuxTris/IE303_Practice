import java.util.Random;
import java.util.Scanner;
import java.util.Locale;

public class Bai2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Co the nhap so mau, neu khong nhap thi mac dinh 1_000_000
        int soMau = 1_000_000;
        if (sc.hasNextInt()) {
            soMau = sc.nextInt();
        }

        Random random = new Random();
        int trongDuongTron = 0;

        for (int i = 0; i < soMau; i++) {
            double x = -1 + 2 * random.nextDouble();
            double y = -1 + 2 * random.nextDouble();

            if (x * x + y * y <= 1) {
                trongDuongTron++;
            }
        }

        double piXapXi = 4.0 * trongDuongTron / soMau;
        System.out.printf(Locale.US, "Pi xap xi = %.8f%n", piXapXi);

        sc.close();
    }
}
