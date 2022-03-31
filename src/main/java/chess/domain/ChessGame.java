package chess.domain;

import chess.domain.board.Board;
import chess.domain.board.Positions;
import chess.domain.gamestate.Ready;
import chess.domain.gamestate.State;
import chess.view.InputView;
import chess.view.OutputView;

public class ChessGame {

    private State state;
    public boolean isEnd = false;

    public ChessGame() {
        state = new Ready();
    }

    public void run() {
        while (!isEnd()) {
            final String command = InputView.inputCommand();
            executeCommand(command);
        }
    }

    private void executeCommand(final String command) {
        final GameCommand gameCommand = GameCommand.from(command);
        if (gameCommand == GameCommand.START) {
            executeStartCommand();
        }
        if (gameCommand == GameCommand.MOVE) {
            executeMoveCommand(command);
        }
        if (gameCommand == GameCommand.END) {
            executeEndCommand();
        }
        if (gameCommand == GameCommand.STATUS) {
            executeStatusCommand();
        }
    }

    private void executeStartCommand() {
        start();
        OutputView.printBoard(board().getBoard());
    }

    private void start() {
        state = state.start();
    }

    private void executeMoveCommand(final String command) {
        final Positions movePositions = Positions.from(command);
        move(movePositions);
        OutputView.printBoard(board().getBoard());
        if (isNotRunning()) {
            OutputView.printFinishMessage();
            OutputView.printStatus(statusOfWhite(), statusOfBlack());
            OutputView.printResultMessage(getResultMessage());
        }
    }

    public Board board() {
        return state.getBoard();
    }

    private void move(final Positions movePositions) {
        state = state.move(movePositions);
    }

    public boolean isNotRunning() {
        return !state.isRunning();
    }

    private void executeEndCommand() {
        OutputView.printFinishMessage();
        if (isNotRunning()) {
            turnOff();
            return;
        }
        end();
        OutputView.printStatus(statusOfWhite(), statusOfBlack());
        OutputView.printResultMessage(getResultMessage());
    }

    public void turnOff() {
        isEnd = true;
    }

    public void end() {
        state = state.end();
    }

    private void executeStatusCommand() {
        OutputView.printStatus(statusOfWhite(), statusOfBlack());
    }

    public double statusOfWhite() {
        return state.statusOfWhite();
    }

    public double statusOfBlack() {
        return state.statusOfBlack();
    }

    public boolean isEnd() {
        return isEnd;
    }

    public String getResultMessage() {
        return GameResult.from(state.getResult()).getMessage();
    }
}
