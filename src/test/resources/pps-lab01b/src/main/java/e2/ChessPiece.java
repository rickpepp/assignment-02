package e2;

import java.util.Collection;

public interface ChessPiece {
    /**
     * Get the position of the chess piece
     * @return actual position of the chess piece
     */
    public Pair<Integer, Integer> getPosition();

    /**
     * Obtain possible movements legit for the chess piece
     * @param sizeOfTheBoard int of the dimension of the board (square board)
     * @return Collection of possible movements
     */
    public Collection<Pair<Integer, Integer>> getPossibleMovements(int sizeOfTheBoard);

    /**
     * Obtain possible hits legit for the chess piece
     * @param sizeOfTheBoard int of the dimension of the board (square board)
     * @return Collection of possible hits
     */
    public Collection<Pair<Integer, Integer>> getPossibleHits(int sizeOfTheBoard);

    /**
     * Set new position for the chess piece
     * @param newPosition new position of the chess piece
     */
    public void setNewPosition(Pair<Integer, Integer> newPosition);
}
