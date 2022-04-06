package chess.web.service;

import chess.console.ChessGame;
import chess.web.commandweb.WebGameCommand;
import chess.web.dao.board.BoardDao;
import chess.web.dao.camp.CampDao;
import chess.web.dao.member.MemberDaoImpl;
import chess.web.dto.BoardDto;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import spark.Request;

public class ChessService {
    private final BoardDao boardDao;
    private final CampDao campDao;
    private final MemberDaoImpl memberDao;
    private ChessGame chessGame;

    public ChessService(final BoardDao boardDao, final CampDao campDao, final MemberDaoImpl memberDao) {
        this.boardDao = boardDao;
        this.campDao = campDao;
        this.memberDao = memberDao;
    }

    public void init() {
        chessGame = new ChessGame();
    }

    public Map<String, Object> executeCommand(final Request req) {
        final String command = req.queryParams("command");
        System.err.println("front에서 받은 command = " + command);
        final WebGameCommand webgameCommand = WebGameCommand.from(command);
        return webgameCommand.execute(command, chessGame, getModelToState());
    }

    private Supplier getModelToState() {
        final HashMap<String, Object> model = new HashMap<>();
        return () -> {
            if (chessGame.isRunning()) {
//                OutputView.printBoard(getBoardDto());
                System.err.println("게임이 진행중입니다. 현재 데이터를 받아와요");
                model.put("meesage", "게임이 진행중 입니다.");
                model.put("board", BoardDto.from(chessGame.getBoard()).getBoard());
                model.put("camp", chessGame.getCamp());

                // status요청이 아니라 항상 status를 같이 반환하도록 수정
//                OutputView.printStatus(calculateStatus());
                model.put("status", chessGame.calculateStatus());
                return model;
            }
//            if (isStatusInRunning()) {
//                OutputView.printStatus(calculateStatus());
//            }
            if (isEndInRunning()) {
//                OutputView.printFinalStatus(calculateStatus());
                model.put("message", "현재 게임이 종료되었습니다.");
                model.put("board", BoardDto.from(chessGame.getBoard()).getBoard());
                model.put("camp", chessGame.getCamp());
                model.put("status", chessGame.calculateStatus());
                return model;
            }
//            if (isEndInGameOff()) {
//
//            }
            return null;
        };
    }

    private boolean isStatusInRunning() {
        if (isEndInGameOff()) {
            return false;
        }
        return chessGame.isStatusInRunning();
    }

    private boolean isEndInRunning() {
        if (isEndInGameOff()) {
            return false;
        }
        return chessGame.isEndInRunning();
    }

    public boolean isEndInGameOff() {
        return chessGame.isEndInGameOff();
    }

    public boolean isNotExistGame() {
        return chessGame == null;
    }
}
