package e2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class SimpleChessPieceFunctions implements ChessPieceFunctions {

    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getMovementFunction() {
        return (position, sizeOfTheBoard) -> new ArrayList<>();
    }

    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getHitFunction() {
        return (position, sizeOfTheBoard) -> new ArrayList<>();
    }
}
