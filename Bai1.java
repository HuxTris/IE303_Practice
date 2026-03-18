import java.util.Random;
import java.util.Scanner;
import java.util.Locale;

public class Bai1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Nhap ban kinh r
        double r = sc.nextDouble();

        // So luong diem random de xap xi
        int soMau = 1_000_000;

        Random random = new Random();
        int trongHinhTron = 0;

        for (int i = 0; i < soMau; i++) {
            double x = -r + 2 * r * random.nextDouble();
            double y = -r + 2 * r * random.nextDouble();

            if (x * x + y * y <= r * r) {
                trongHinhTron++;
            }
        }

        double dienTichHinhVuong = (2 * r) * (2 * r);
        double dienTichXapXi = ((double) trongHinhTron / soMau) * dienTichHinhVuong;

        System.out.printf(Locale.US, "Dien tich xap xi = %.6f%n", dienTichXapXi);
        sc.close();
    }
}
