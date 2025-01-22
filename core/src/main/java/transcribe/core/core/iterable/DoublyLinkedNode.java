package transcribe.core.core.iterable;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DoublyLinkedNode<T> {

    private T value;
    private DoublyLinkedNode<T> next;
    private DoublyLinkedNode<T> prev;

    public List<DoublyLinkedNode<T>> getAllPrev() {
        var result = new ArrayList<DoublyLinkedNode<T>>();
        var prev = this.getPrev();

        while (prev != null) {
            result.add(prev);
            prev = prev.getPrev();
        }

        return result;
    }

    public List<DoublyLinkedNode<T>> getAllNext() {
        var result = new ArrayList<DoublyLinkedNode<T>>();
        var next = this.next;

        while (next != null) {
            result.add(next);
            next = next.next;
        }

        return result;
    }

    public DoublyLinkedNode(T value) {
        this.value = value;
    }

}
