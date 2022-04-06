package chess.web.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import chess.web.dao.board.BoardDao;
import chess.web.dao.camp.CampDao;
import chess.web.dao.member.MemberDaoImpl;
import chess.web.service.ChessService;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class WebController {

    private final ChessService chessService;

    public WebController() {
        chessService = new ChessService(
            new BoardDao(),
            new CampDao(),
            new MemberDaoImpl()
        );
    }

    public void run() {
        get("/", (req, res) -> {
            chessService.init();
            return render(new HashMap<>(), "index.html");
        });

        post("/:command", (req, res) -> {
            if (chessService.isNotExistGame()) {
                res.redirect("/");
            }
            if (chessService.isEndInGameOff()) {
                render(executeAndGetModel(req), "index.html");
            }
            return render(executeAndGetModel(req), "game.html");
        });
    }

    private Map<String, Object> executeAndGetModel(final Request req) {
        try {
            return chessService.executeCommand(req);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new HashMap<>(Map.of("errorMessage", e.getMessage()));
        }
    }

    private static String render(Map<String, Object> model, String templatePath) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}
