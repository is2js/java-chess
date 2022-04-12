package chess.domain.command;

import chess.domain.chessgame.ChessGame;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public enum GameCommand {

    START("start", Start::new),
    MOVE("move [a-h][1-9] [a-h][1-9]", Move::new),
    END("end", End::new),
    STATUS("status", Status::new),
    ;

    private static final String WRONG_COMMAND_MESSAGE = "잘못된 명령어를 입력하였습니다.";

    private final String command;
    private final Supplier<CommandGenerator> commandGenerator;

    GameCommand(String command,
                Supplier<CommandGenerator> commandGenerator) {
        this.command = command;
        this.commandGenerator = commandGenerator;
    }

    public static GameCommand from(final String command) {
        return Arrays.stream(GameCommand.values())
            .filter(it -> Pattern.matches(it.command, command))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(WRONG_COMMAND_MESSAGE));
    }

    public void execute(final String command,
                        final ChessGame chessGame,
                        final Runnable returnModelToState) {
        final CommandGenerator gameCommand = commandGenerator.get();
        gameCommand.execute(command, chessGame, returnModelToState);
    }

    public Map<String, Object> execute(final String command,
                                       final ChessGame chessGame,
                                       final Supplier<Map<String, Object>> returnModelToState) {
        final CommandGenerator webGameCommand = commandGenerator.get();
        return webGameCommand.execute(command, chessGame, returnModelToState);
    }
}
