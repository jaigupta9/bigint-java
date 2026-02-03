package src;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Number 1: ");
        BigInt a = new BigInt(sc.nextLine());

        System.out.print("Enter Number 2: ");
        BigInt b = new BigInt(sc.nextLine());

        System.out.println("""
                1. Add
                2. Subtract
                3. Multiply
                4. Divide
                5. Power
                6. GCD
                7. LCM
                8. Modulus
                9. Absolute Compare
                10. Compare
                """);

        int choice = sc.nextInt();

        switch (choice) {
            case 1 -> System.out.println(BigInt.add(a, b));
            case 2 -> System.out.println(BigInt.subtract(a, b));
            case 3 -> System.out.println(BigInt.multiply(a, b));
            case 4 -> System.out.println(BigInt.divide(a, b));
            case 5 -> System.out.println(BigInt.power(a, b));
            case 6 -> System.out.println(BigInt.gcd(a, b));
            case 7 -> System.out.println(BigInt.lcm(a, b));
            case 8 -> System.out.println(BigInt.mod(a, b));
            case 9 -> System.out.println(BigInt.absCompare(a, b));
            case 10 -> System.out.println(BigInt.compare(a, b));
            default -> System.out.println("Invalid choice");
        }
    }
}
