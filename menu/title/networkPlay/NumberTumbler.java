package menu.title.networkPlay;

import java.util.Arrays;

public class NumberTumbler {
    private Integer index = 0;
    private Integer[] digits;

    public NumberTumbler(int noDigits) {
        if (noDigits == 0) throw new RuntimeException("no");
        this.digits = new Integer[noDigits];
        for (int i = 0; i < digits.length; i++) {
            digits[i] = 0;
        }
    }

    public void incrementDigit() {
        digits[index] = (digits[index] + 1) % 10;
        System.out.println(digits[index]);
    }

    public void decrementDigit() {
        digits[index] = (digits[index] - 1) % 10;
    }

    public int getIndex() {
        return index;
    }

    public void nextDigit() {
        index = (index + 1) % digits.length;
        System.out.println(index);
    }

    public void prevDigit() {
        index = (index - 1) % digits.length;
    }

    public String getValue() {
        return Arrays.toString(digits).replaceAll("[\\[\\],\\s]", "");
    }

    public Integer getIntegerValue() {
        return Integer.valueOf(getValue());
    }

    public String getAsIPAddress() {
        if (digits.length != 12) throw new RuntimeException();
        String str = getValue();
        String[] substrings = str.split(".{3}");
        return String.join(".", substrings);
    }
}
