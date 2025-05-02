package e2;

public class ChessPieceFactory {
    public ChessPiece createKnight() {
        return new BaseChessPieceImpl(new KnightFunctions());
    }

    public ChessPiece createPawn() {
        return new BaseChessPieceImpl(new SimpleChessPieceFunctions());
    }
}
