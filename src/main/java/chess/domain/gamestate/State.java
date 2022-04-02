package chess.domain.gamestate;

import chess.domain.Camp;
import chess.domain.StatusScore;
import chess.domain.board.Position;
import chess.domain.board.Positions;
import chess.domain.piece.Piece;
import java.util.Map;

public interface State {

    State run();

    State move(Positions positions);

    State move(Position before, Position after);

    Camp switchCamp();

    State status();

    StatusScore calculateStatus();

    State end();

    boolean isRunning();

    boolean isStatus();

    boolean isFinished();

    Map<Position, Piece> getBoard();
}
