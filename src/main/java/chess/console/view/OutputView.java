package chess.console.view;

import chess.domain.board.Position;
import chess.domain.chessgame.StatusScore;
import chess.domain.piece.Piece;
import java.util.List;
import java.util.Map;

public class OutputView {

    private static final String GAME_START_MESSAGE = "> 체스 게임을 시작합니다.";
    private static final String GAME_COMMAND_FORMAT = "> %s : %s%n";
    private static final String START_COMMAND = "start";
    private static final String END_COMMAND = "end";
    private static final String GAME_START_COMMAND_NAME = "게임 시작";
    private static final String GAME_MOVE_COMMAND_NAME = "게임 이동";
    private static final String GAME_END_COMMAND_NAME = "게임 종료";
    private static final String GAME_MOVE_COMMAND = "move source위치 target위치 - 예. move b2 b3";
    private static final String RESULT_MESSAGE_SUFFIX = "입니다.";
    private static final int BOARD_ROW_AND_COLUMN_UNIT = 8;
    private static final int UNIT_DIVISION_REMAINDER = 0;
    private static final String CURRENT_GAME_END_MESSAGE = "해당 게임이 종료되었습니다.";
    private static final String RESULT_SCORE_FORMAT = "백 진영 점수 : %.1f%n흑 진영 점수 : %.1f%n";
    private static final String CHESS_GAME_END_MESSAGE = "체스 게임을 완전히 종료합니다.";
    private static final String CHECK_KING_MESSAGE = "현재 킹이 check된 상황입니다.";
    private static final String CHECKMATE_KING_MESSAGE = "현재 킹이 어느 한 곳에서 checkmated된 상황입니다.";

    public void printStartMessage() {
        System.out.println(GAME_START_MESSAGE);
        System.out.printf(GAME_COMMAND_FORMAT, GAME_START_COMMAND_NAME, START_COMMAND);
        System.out.printf(GAME_COMMAND_FORMAT, GAME_END_COMMAND_NAME, END_COMMAND);
        System.out.printf(GAME_COMMAND_FORMAT, GAME_MOVE_COMMAND_NAME, GAME_MOVE_COMMAND);
    }

    public void printBoard(Map<Position, Piece> board) {
        int count = 0;
        for (Position position : board.keySet()) {
            final Piece piece = board.get(position);
            String content = makeBoardContentString(piece);
            System.out.print(content);
            count++;
            if (count % BOARD_ROW_AND_COLUMN_UNIT == UNIT_DIVISION_REMAINDER) {
                System.out.println();
            }
        }
    }

    private String makeBoardContentString(Piece piece) {
        if (piece.isBlack()) {
            return piece.getCharacter().toUpperCase();
        }
        return piece.getCharacter();
    }

    public void printStatus(final StatusScore statusScore) {
        System.out.printf(RESULT_SCORE_FORMAT, statusScore.getWhite(), statusScore.getBlack());
    }

    public void printFinalStatus(final StatusScore statusScore) {
        System.out.println(CURRENT_GAME_END_MESSAGE);
        System.out.printf(RESULT_SCORE_FORMAT, statusScore.getWhite(), statusScore.getBlack());
        System.out.println(statusScore.getGameResultMessage() + RESULT_MESSAGE_SUFFIX);
    }

    public void printEndMessage() {
        System.out.println(CHESS_GAME_END_MESSAGE);
    }

    public void printKingCheckedMessage() {
        System.out.println(CHECK_KING_MESSAGE);
    }

    public void printKingCheckmatedMessage(
        final List<Position> kingCheckmatedPositions) {
        System.out.println(CHECKMATE_KING_MESSAGE);
        for (Position kingCheckmatedPosition : kingCheckmatedPositions) {
            System.out.println(kingCheckmatedPosition + "에 갈 수 없습니다.");
        }
    }

    public void printALLKingCheckmatedMessage() {
        System.out.println("킹의 모든 포지션이 check된 상황인 checkmate입니다. 게임을 종료합니다.");
    }
}
