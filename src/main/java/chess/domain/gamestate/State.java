package chess.domain.gamestate;

import chess.domain.Camp;
import chess.domain.StatusScore;
import chess.domain.board.Position;
import chess.domain.board.Positions;
import chess.domain.piece.Piece;
import java.util.Map;

public interface State {

    State start();

    State move(Positions positions);

    State move(Position before, Position after);

    Camp switchCamp();

    State end();

    StatusScore calculateStatus();

    Map<Position, Piece> getBoard();

    boolean isRunning();

    boolean isFinished();

    State status();

    boolean isStatus();

    State returnState();
}
