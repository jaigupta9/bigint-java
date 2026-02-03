package src;

public class BigInt {

    static class Node {
        int digits;
        Node prev, next;

        Node(int d) {
            digits = d;
        }
    }

    Node head, tail;
    int size;
    boolean negative;

    private static final int BASE = 10000;

    // ---------- CONSTRUCTOR ----------
    public BigInt(String s) {
        s = s.trim();
        if (s.length() == 0)
            throw new IllegalArgumentException("Empty input");

        int idx = 0;
        if (s.charAt(0) == '-') {
            negative = true;
            idx = 1;
        }

        head = tail = null;
        size = 0;

        for (int i = s.length(); i > idx; i -= 4) {
            int start = Math.max(idx, i - 4);
            int val = Integer.parseInt(s.substring(start, i));
            appendFront(val);
        }

        rebuild();
    }

    private BigInt() {
    }

    // ---------- LIST HELPERS ----------
    private void appendFront(int d) {
        Node n = new Node(d);
        if (head == null) {
            head = tail = n;
        } else {
            n.next = head;
            head.prev = n;
            head = n;
        }
        size++;
    }

    private void appendBack(int d) {
        Node n = new Node(d);
        if (tail == null) {
            head = tail = n;
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
        size++;
    }

    private void rebuild() {
        while (head != tail && head.digits == 0) {
            head = head.next;
            head.prev = null;
            size--;
        }
        if (size == 1 && head.digits == 0)
            negative = false;
    }

    // ---------- PRINT ----------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (negative)
            sb.append("-");
        Node cur = head;
        sb.append(cur.digits);
        cur = cur.next;
        while (cur != null) {
            sb.append(String.format("%04d", cur.digits));
            cur = cur.next;
        }
        return sb.toString();
    }

    // ---------- CLONE ----------
    private BigInt copy() {
        BigInt r = new BigInt();
        r.negative = negative;
        for (Node n = head; n != null; n = n.next)
            r.appendBack(n.digits);
        return r;
    }

    // ---------- COMPARE ----------
    public static int absCompare(BigInt a, BigInt b) {
        if (a.size != b.size)
            return Integer.compare(a.size, b.size);
        Node x = a.head, y = b.head;
        while (x != null) {
            if (x.digits != y.digits)
                return Integer.compare(x.digits, y.digits);
            x = x.next;
            y = y.next;
        }
        return 0;
    }

    public static int compare(BigInt a, BigInt b) {
        if (a.negative != b.negative)
            return a.negative ? -1 : 1;
        int cmp = absCompare(a, b);
        return a.negative ? -cmp : cmp;
    }

    // ---------- ADD ----------
    public static BigInt add(BigInt a, BigInt b) {
        if (a.negative != b.negative) {
            b = b.copy();
            b.negative = !b.negative;
            return subtract(a, b);
        }

        BigInt r = new BigInt();
        r.negative = a.negative;

        Node x = a.tail, y = b.tail;
        int carry = 0;

        while (x != null || y != null || carry != 0) {
            int sum = carry;
            if (x != null) {
                sum += x.digits;
                x = x.prev;
            }
            if (y != null) {
                sum += y.digits;
                y = y.prev;
            }
            r.appendFront(sum % BASE);
            carry = sum / BASE;
        }
        return r;
    }

    // ---------- SUBTRACT ----------
    public static BigInt subtract(BigInt a, BigInt b) {
        if (a.negative != b.negative) {
            b = b.copy();
            b.negative = !b.negative;
            return add(a, b);
        }

        if (absCompare(a, b) < 0) {
            BigInt r = subtract(b, a);
            r.negative = !a.negative;
            return r;
        }

        BigInt r = new BigInt();
        r.negative = a.negative;

        Node x = a.tail, y = b.tail;
        int borrow = 0;

        while (x != null) {
            int diff = x.digits - borrow - (y != null ? y.digits : 0);
            borrow = 0;
            if (diff < 0) {
                diff += BASE;
                borrow = 1;
            }
            r.appendFront(diff);
            x = x.prev;
            if (y != null)
                y = y.prev;
        }

        r.rebuild();
        return r;
    }

    // ---------- MULTIPLY ----------
    public static BigInt multiply(BigInt a, BigInt b) {
        BigInt r = new BigInt();
        r.negative = a.negative ^ b.negative;

        int[] arr = new int[a.size + b.size];
        Node x = a.tail;
        int i = 0;

        while (x != null) {
            Node y = b.tail;
            int j = 0;
            while (y != null) {
                long prod = (long) x.digits * y.digits + arr[i + j];
                arr[i + j] = (int) (prod % BASE);
                arr[i + j + 1] += prod / BASE;
                y = y.prev;
                j++;
            }
            x = x.prev;
            i++;
        }

        for (int k = arr.length - 1; k >= 0; k--)
            r.appendBack(arr[k]);

        r.rebuild();
        return r;
    }

    // ---------- DIVIDE (binary search) ----------
    public static BigInt divide(BigInt a, BigInt b) {
        // 1. Divide by zero check
        if (b.size == 1 && b.head.digits == 0)
            throw new ArithmeticException("Divide by zero");

        // 2. Determine final sign
        boolean resultNegative = a.negative ^ b.negative;

        // 3. Work on absolute values only
        BigInt dividend = a.copy();
        BigInt divisor = b.copy();
        dividend.negative = false;
        divisor.negative = false;

        // 4. Binary search bounds
        BigInt left = new BigInt("0");
        BigInt right = dividend.copy();
        BigInt one = new BigInt("1");
        BigInt ans = new BigInt("0");

        // 5. Binary search
        while (absCompare(left, right) <= 0) {
            BigInt mid = add(left, right);
            mid.half(); // mid = (left + right) / 2

            BigInt prod = multiply(mid, divisor);
            int cmp = absCompare(prod, dividend);

            if (cmp <= 0) {
                ans = mid;
                left = add(mid, one);
            } else {
                right = subtract(mid, one);
            }
        }

        // 6. Apply sign to result
        ans.negative = resultNegative;
        return ans;
    }

    // ---------- HALF ----------
    private void half() {
        Node n = head;
        int carry = 0;
        while (n != null) {
            int cur = carry * BASE + n.digits;
            n.digits = cur / 2;
            carry = cur % 2;
            n = n.next;
        }
        rebuild();
    }

    // ---------- POWER ----------
    public static BigInt power(BigInt a, BigInt b) {
        if (b.size == 1 && b.head.digits == 0)
            return new BigInt("1");

        BigInt half = b.copy();
        half.half();

        BigInt res = power(a, half);
        res = multiply(res, res);

        if (b.tail.digits % 2 == 1)
            res = multiply(res, a);

        return res;
    }

    // ---------- GCD ----------
    public static BigInt gcd(BigInt a, BigInt b) {
        if (b.size == 1 && b.head.digits == 0)
            return a;
        return gcd(b, mod(a, b));
    }

    // ---------- LCM ----------
    public static BigInt lcm(BigInt a, BigInt b) {
        return divide(multiply(a, b), gcd(a, b));
    }

    // ---------- MOD ----------
    public static BigInt mod(BigInt a, BigInt b) {
        return subtract(a, multiply(divide(a, b), b));
    }
}
