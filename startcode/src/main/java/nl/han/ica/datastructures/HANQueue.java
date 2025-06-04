package nl.han.ica.datastructures;

import java.util.LinkedList;

public class HANQueue<T> implements IHANQueue<T> {
    private LinkedList<T> list = new LinkedList<>();

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void enqueue(T value) {
        list.addLast(value);
    }

    @Override
    public T dequeue() {
        return list.isEmpty() ? null : list.removeFirst();
    }

    @Override
    public T peek() {
        return list.peekFirst();
    }

    @Override
    public int getSize() {
        return list.size();
    }
}
