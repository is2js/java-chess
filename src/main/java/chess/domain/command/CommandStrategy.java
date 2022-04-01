package chess.domain.command;

import chess.ChessGame;

public interface CommandStrategy {
    void execute(final String command, final ChessGame chessGame, final Runnable runnable);
}
