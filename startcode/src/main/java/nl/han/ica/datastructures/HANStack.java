package nl.han.ica.datastructures;

import java.util.LinkedList;

public class HANStack<T> implements IHANStack<T> {
    private LinkedList<T> list = new LinkedList<>();

    @Override
    public void push(T value) {
        list.addFirst(value);
    }

    @Override
    public T pop() {
        return list.isEmpty() ? null : list.removeFirst();
    }

    @Override
    public T peek() {
        return list.peekFirst();
    }
}
