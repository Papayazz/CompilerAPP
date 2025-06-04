package nl.han.ica.datastructures;

import java.util.LinkedList;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private LinkedList<T> list = new LinkedList<>();

    @Override
    public void addFirst(T value) {
        list.addFirst(value);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > list.size()) {
            throw new IndexOutOfBoundsException();
        }
        list.add(index, value);
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= list.size()) {
            throw new IndexOutOfBoundsException();
        }
        list.remove(pos);
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= list.size()) {
            throw new IndexOutOfBoundsException();
        }
        return list.get(pos);
    }

    @Override
    public void removeFirst() {
        if (!list.isEmpty()) {
            list.removeFirst();
        }
    }

    @Override
    public T getFirst() {
        return list.peekFirst();
    }

    @Override
    public int getSize() {
        return list.size();
    }
}
