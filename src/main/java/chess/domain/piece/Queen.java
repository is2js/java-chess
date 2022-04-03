package chess.domain.piece;

import static chess.domain.piece.PieceProperty.QUEEN;

import chess.domain.Camp;
import chess.domain.board.Position;
import chess.domain.board.Positions;
import java.util.function.Consumer;

public final class Queen extends NotNullPiece {

    private static final String NOT_MOVABLE_POSITION = "이동할 수 없는 위치입니다.";
    private static final int NOT_MOVED_DISTANCE = 0;

    public Queen(Camp camp) {
        super(camp, QUEEN);
    }

    @Override
    public void move(Position beforePosition, Position afterPosition, Consumer<Piece> movePiece) {
        if (!canMove(beforePosition, afterPosition)) {
            throw new IllegalArgumentException(NOT_MOVABLE_POSITION);
        }
        movePiece.accept(this);
    }

    @Override
    public void move(final Positions positions, final Consumer<Piece> movePiece) {
        if (!canMove(positions)) {
            throw new IllegalArgumentException(NOT_MOVABLE_POSITION);
        }
        movePiece.accept(this);
    }

    @Override
    public boolean canMove(Position beforePosition, Position afterPosition) {
        int columnDistance = beforePosition.columnDistance(afterPosition);
        int rowDistance = beforePosition.rowDistance(afterPosition);
        if (columnDistance == NOT_MOVED_DISTANCE) {
            return true;
        }
        if (rowDistance == NOT_MOVED_DISTANCE) {
            return true;
        }
        return columnDistance == rowDistance;
    }

    @Override
    public boolean canMove(final Positions positions) {
        int columnDistance = positions.before().columnDistance(positions.after());
        int rowDistance = positions.before().rowDistance(positions.after());
        if (columnDistance == NOT_MOVED_DISTANCE) {
            return true;
        }
        if (rowDistance == NOT_MOVED_DISTANCE) {
            return true;
        }
        return columnDistance == rowDistance;
    }
}
