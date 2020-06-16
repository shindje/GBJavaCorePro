package hw1.task3;

import java.util.ArrayList;

public class Box <T extends Fruit> {
    ArrayList<T> list = new ArrayList<>();

    void add(T fruit) {
        list.add(fruit);
    }

    float getWeight() {
        if (list.size() == 0) {
            return 0.0f;
        } else {
            return list.size() * list.get(0).getFruitWeight();
        }
    }

    boolean compare(Box box) {
        return (Math.abs(this.getWeight() - box.getWeight()) < 0.0000001);
    }

    void move(Box<T> box) {
        for (T t: this.list) {
            box.add(t);
        }
        this.list.clear();
    }
}
