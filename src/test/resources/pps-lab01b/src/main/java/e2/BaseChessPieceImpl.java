package e2;

import java.util.Collection;
import java.util.Optional;

public class BaseChessPieceImpl implements ChessPiece {

    private Optional<Pair<Integer, Integer>> position;
    private final ChessPieceFunctions movementFunction;

    public BaseChessPieceImpl(ChessPieceFunctions movementFunction) {
        this.position = Optional.empty();
        this.movementFunction = movementFunction;
    }

    public void setNewPosition(Pair<Integer, Integer> newPosition) {
        this.position = Optional.of(newPosition);
    }

    public Pair<Integer, Integer> getPosition() {
        checkPositionIsSet();
        return this.position.get();
    }

    public Collection<Pair<Integer, Integer>> getPossibleMovements(int sizeOfTheBoard) {
        checkPositionIsSet();
        checkValidActualPositionWithSizeOfTheBoard(sizeOfTheBoard);
        return movementFunction.getMovementFunction().apply(this.position.get(), sizeOfTheBoard);
    }

    public Collection<Pair<Integer, Integer>> getPossibleHits(int sizeOfTheBoard) {
        checkPositionIsSet();
        checkValidActualPositionWithSizeOfTheBoard(sizeOfTheBoard);
        return movementFunction.getHitFunction().apply(this.position.get(), sizeOfTheBoard);
    }

    private void checkValidActualPositionWithSizeOfTheBoard(int sizeOfTheBoard) {
        if (isPositionInTheBoard(sizeOfTheBoard, this.position.get()))
            throw new IllegalStateException("Actual position is invalid with this size of the board");
    }

    private boolean isPositionInTheBoard(int sizeOfTheBoard, Pair<Integer, Integer> position) {
        return position.getX() < 0
                || position.getY() < 0
                || position.getX() >= sizeOfTheBoard
                || position.getY() >= sizeOfTheBoard;
    }

    private void checkPositionIsSet() {
        if (this.position.isEmpty())
            throw new IllegalStateException("Position is not set");
    }

}
