package models;

import java.util.Objects;

public class CustomNode {

    private Task value;
    private CustomNode next;
    private CustomNode previous;


    public CustomNode(Task value, CustomNode next, CustomNode previous) {
        this.value = value;
        this.next = next;
        this.previous = previous;
    }


    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public CustomNode getNext() {
        return next;
    }

    public void setNext(CustomNode next) {
        this.next = next;
    }

    public CustomNode getPrevious() {
        return previous;
    }

    public void setPrevious(CustomNode previous) {
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomNode that = (CustomNode) o;
        return value.equals(that.value) && Objects.equals(next, that.next) && Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, next, previous);
    }
}
