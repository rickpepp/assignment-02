package e2;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ChessPieceFunctions {
    /**
     * Generate a bi-function where give in input the position (Pair<Integer, Integer>),
     * an Integer for the board dimension and as a result a collection of Pair<Integer, Integer>
     * which represent the legal move possible
     * @return bi-function to calculate the legal movements of the chess piece
     */
    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getMovementFunction();
    /**
     * Generate a bi-function where give in input the position (Pair<Integer, Integer>),
     * an Integer for the board dimension and as a result a collection of Pair<Integer, Integer>
     * which represent the legal hit possible
     * @return bi-function to calculate the legal hit of the chess piece
     */
    public BiFunction<Pair<Integer, Integer>, Integer, Collection<Pair<Integer, Integer>>> getHitFunction();
}
