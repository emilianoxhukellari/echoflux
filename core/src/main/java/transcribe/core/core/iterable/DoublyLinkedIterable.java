package transcribe.core.core.iterable;

import lombok.NonNull;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DoublyLinkedIterable<T> implements Iterable<DoublyLinkedNode<T>> {

    private DoublyLinkedNode<T> head;
    private DoublyLinkedNode<T> tail;

    public Stream<DoublyLinkedNode<T>> nodeStream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public static <T> DoublyLinkedIterable<T> of(Iterable<T> values) {
        var iterable = new DoublyLinkedIterable<T>();

        for (var value : values) {
            iterable.add(value);
        }

        return iterable;
    }

    public void add(T value) {
        var newNode = new DoublyLinkedNode<>(value);

        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }

        tail = newNode;
    }

    public T getFirst() {
        return head != null
                ? head.getValue()
                : null;
    }

    public T getLast() {
        return tail != null
                ? tail.getValue()
                : null;
    }

    @Override
    @NonNull
    public Iterator<DoublyLinkedNode<T>> iterator() {
        return new Iterator<>() {

            private DoublyLinkedNode<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public DoublyLinkedNode<T> next() {
                var node = current;
                current = current.getNext();

                return node;
            }

        };
    }

}
