package e2;

import java.util.*;

public class LogicsImpl implements Logics {

	private static final int MIN_SIZE_VALUE = 2;

	private ChessPiece knight;
	private ChessPiece pawn;
	private final Random random = new Random();
	private final int size;

	public LogicsImpl(int size, Pair<Integer,Integer> knightPosition, Pair<Integer,Integer> pawnPosition) {
		this(size);
		checkValidPosition(pawnPosition);
		checkValidPosition(knightPosition);
		this.pawn.setNewPosition(pawnPosition);
		this.knight.setNewPosition(knightPosition);
	}

	public LogicsImpl(int size){
		checkValidSize(size);
		this.size = size;
		createPawnAndKnight();
		generateChessPiecesRandomPositions();
	}
    
	@Override
	public boolean hit(int row, int col) {
		Pair<Integer, Integer> positionToEvaluate = new Pair<>(row, col);
		checkValidPosition(positionToEvaluate);
		if (this.knight.getPossibleMovements(this.size).contains(positionToEvaluate))
			this.knight.setNewPosition(positionToEvaluate);
		return this.knight.getPosition().equals(this.pawn.getPosition());
	}

	@Override
	public boolean hasKnight(int row, int col) {
		return this.knight.getPosition().equals(new Pair<>(row,col));
	}

	@Override
	public boolean hasPawn(int row, int col) {
		return this.pawn.getPosition().equals(new Pair<>(row,col));
	}

	private void generateChessPiecesRandomPositions() {
		this.pawn.setNewPosition(this.randomEmptyPosition());
		Pair<Integer, Integer> pos;
		do {
			pos = this.randomEmptyPosition();
		} while(pos.equals(this.pawn.getPosition()));
		this.knight.setNewPosition(pos);
	}

	private void createPawnAndKnight() {
		ChessPieceFactory factory = new ChessPieceFactory();
		this.pawn = factory.createPawn();
		this.knight = factory.createKnight();
	}

	private void checkValidSize(int size) {
		if (size < MIN_SIZE_VALUE)
			throw new IllegalArgumentException();
	}

	private Pair<Integer,Integer> randomEmptyPosition(){
		return new Pair<>(this.random.nextInt(size),this.random.nextInt(size));
	}

	private void checkValidPosition(Pair<Integer,Integer> position) {
		if (position.getX() < 0 || position.getY() < 0 || position.getX() >= this.size || position.getY() >= this.size)
			throw new IndexOutOfBoundsException();
	}
}
