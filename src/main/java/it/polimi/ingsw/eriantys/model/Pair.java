package it.polimi.ingsw.eriantys.model;

import java.util.Objects;

/**
 * Represents a pair of values of any two types.
 *
 * @param value0 First value of the pair.
 * @param value1 Second value of the pair.
 * @param <T0> Type of the first value.
 * @param <T1> Type of the first value.
 */
public record Pair<T0, T1>(T0 value0, T1 value1) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return value0.equals(pair.value0) && value1.equals(pair.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value0, value1);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "value0=" + value0 +
                ", value1=" + value1 +
                '}';
    }
}
