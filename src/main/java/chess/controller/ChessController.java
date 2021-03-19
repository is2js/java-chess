package chess.controller;

import chess.domain.ChessGame;
import chess.domain.Position;
import chess.domain.piece.Piece;
import chess.domain.team.BlackTeam;
import chess.domain.team.WhiteTeam;
import chess.view.InputView;
import chess.view.OutputView;
import chess.view.PieceNameConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessController {

    public void run() {
        OutputView.printChessStartMessage();
        final boolean start = InputView.inputChessStartOrEnd();
        if (!start) {
            return;
        }
        final ChessGame chessGame = new ChessGame(new BlackTeam(), new WhiteTeam());
        printChessBoard(chessGame);
        startGame(chessGame);
    }

    private void startGame(ChessGame chessGame) {
        while (chessGame.isEnd()) {
            singleTurn(chessGame);
            chessGame.changeTurn();
            printChessBoard(chessGame);
        }
    }

    private void singleTurn(ChessGame chessGame) {
        try {
            List<String> movePositions = InputView.inputMovePosition();
            chessGame.move(Position.of(movePositions.get(0)), Position.of(movePositions.get(1)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            singleTurn(chessGame);
        }
    }

    private void printChessBoard(final ChessGame chessGame) {
        final Map<Position, String> chessBoard = convertToBlackPrintName(chessGame);
        chessBoard.putAll(convertToWhitePrintName(chessGame));
        OutputView.printChessBoard(chessBoard);
    }

    private Map<Position, String> convertToBlackPrintName(final ChessGame chessGame) {
        final Map<Position, Piece> blackPosition = chessGame.getBlackTeam().getPiecePosition();
        final Map<Position, String> blackPrintFormat = new HashMap<>();
        for (Position position : blackPosition.keySet()) {
            final Piece piece = blackPosition.get(position);
            blackPrintFormat.put(position, PieceNameConverter.convert(piece).toUpperCase());
        }
        return blackPrintFormat;
    }


    private Map<Position, String> convertToWhitePrintName(final ChessGame chessGame) {
        final Map<Position, Piece> whitePosition = chessGame.getWhiteTeam().getPiecePosition();
        final Map<Position, String> whitePrintFormat = new HashMap<>();
        for (Position position : whitePosition.keySet()) {
            final Piece piece = whitePosition.get(position);
            whitePrintFormat.put(position, PieceNameConverter.convert(piece).toLowerCase());
        }
        return whitePrintFormat;
    }
}
