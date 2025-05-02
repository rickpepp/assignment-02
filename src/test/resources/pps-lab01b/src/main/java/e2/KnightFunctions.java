package e2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class KnightFunctions implements ChessPieceFunctions{
    @Override
    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getMovementFunction() {
        return (position, sizeOfTheBoard) -> {
            Collection<Pair<Integer, Integer>> result = new ArrayList<>();
            for (int i = 0; i < sizeOfTheBoard; i++) {
                for (int j = 0; j < sizeOfTheBoard; j++) {
                    if (isKnightMovementValid(position, i, j)) {
                        result.add(new Pair<>(i, j));
                    }
                }

            }
            return result;
        };
    }

    @Override
    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getHitFunction() {
        return getMovementFunction();
    }

    private static boolean isKnightMovementValid(Pair<Integer, Integer> position, int x, int y) {
        return Math.abs(x - position.getX()) + Math.abs(y - position.getY()) == 3
                && Math.abs(y - position.getY()) != 0
                && Math.abs(x - position.getX()) != 0;
    }
}
