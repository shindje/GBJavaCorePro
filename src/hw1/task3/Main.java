package hw1.task3;

public class Main {
    public static void main(String[] args) {
        Box<Orange> orangeBox = new Box<>();
        for (int i = 0; i < 4; i++) {
            orangeBox.add(new Orange());
        }
        System.out.println("Box with 4 oranges weigths " + orangeBox.getWeight());

        Box<Apple> appleBox = new Box<>();
        for (int i = 0; i < 4; i++) {
            appleBox.add(new Apple());
        }
        System.out.println("Box with 4 apples weigths " + appleBox.getWeight());

        System.out.println("Compare boxes: " + appleBox.compare(orangeBox));

        Box<Apple> appleBox6 = new Box<>();
        for (int i = 0; i < 6; i++) {
            appleBox6.add(new Apple());
        }
        System.out.println("Compare 6 apples and 4 oranges boxes: " + appleBox6.compare(orangeBox));

        appleBox.move(appleBox6);
        System.out.println("After move box1 weigths: " + appleBox.getWeight());
        System.out.println("After move box2 weigths: " + appleBox6.getWeight());
    }
}