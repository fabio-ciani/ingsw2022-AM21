package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void equals_PairsWithEqualValues_PairsAreEquals() {
        Pair<Integer, String> pair1 = new Pair<>(1, "hello");
        Pair<Integer, String> pair2 = new Pair<>(1, "hello");
        assertEquals(pair1, pair2);
    }

    @Test
    void hashCode_PairsFromSameComponents_SameHashValue() {
        Integer value0 = 0;
        String value1 = "Hello World!!";
        Pair<Integer, String> pair1 = new Pair<>(value0, value1);
        Pair<Integer, String> pair2 = new Pair<>(value0, value1);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    void toString_Pair_StringContainsValues() {
        Pair<Integer, String> pair = new Pair<>(1, "hello");
        String string = pair.toString();
        assertTrue(string.contains(Integer.toString(1)));
        assertTrue(string.contains("hello"));
    }

    @Test
    void values_SomeValuesInConstructor_SameValuesReturned() {
        Pair<String, String> pair = new Pair<>("hello", "world");
        assertEquals("hello", pair.value0());
        assertEquals("world", pair.value1());
    }
}