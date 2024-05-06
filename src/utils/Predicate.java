package utils;

public interface Predicate<T> {
    boolean perform(T value);
}
