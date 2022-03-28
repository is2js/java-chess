package chess.domain.board;

import static chess.domain.Camp.BLACK;
import static chess.domain.Camp.WHITE;

import chess.domain.Camp;
import chess.domain.piece.Bishop;
import chess.domain.piece.King;
import chess.domain.piece.Knight;
import chess.domain.piece.NullPiece;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import chess.domain.piece.Queen;
import chess.domain.piece.Rook;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Board {
    private static final Position ROOK_INITIAL_POSITION = new Position(Column.A, Row.ONE);
    private static final Position KNIGHT_INITIAL_POSITION = new Position(Column.B, Row.ONE);
    private static final Position BISHOP_INITIAL_POSITION = new Position(Column.C, Row.ONE);
    private static final Position QUEEN_INITIAL_POSITION = new Position(Column.D, Row.ONE);
    private static final Position KING_INITIAL_POSITION = new Position(Column.E, Row.ONE);
    private static final Row PAWN_INITIAL_ROW = Row.TWO;
    private static final int BLANK_INITIAL_START_ROW_INDEX = 2;
    private static final int BLANK_INITIAL_END_ROW_INDEX = 5;

    private final Map<Position, Piece> value;
    private boolean whiteTurn;

    public Board() {
        this.value = new TreeMap<>();
        this.whiteTurn = true;
        initializeFourPiecesOf(ROOK_INITIAL_POSITION, Rook::new);
        initializeFourPiecesOf(KNIGHT_INITIAL_POSITION, Knight::new);
        initializeFourPiecesOf(BISHOP_INITIAL_POSITION, Bishop::new);

        initializeTwoPiecesOf(QUEEN_INITIAL_POSITION, Queen::new);
        initializeTwoPiecesOf(KING_INITIAL_POSITION, King::new);

        initializePawn();
        initializeBlanks();
    }

    private void initializeFourPiecesOf(Position pieceInitialPosition,
                                        Function<Camp, Piece> pieceConstructor) {
        value.put(pieceInitialPosition, pieceConstructor.apply(WHITE));
        value.put(pieceInitialPosition.flipHorizontally(), pieceConstructor.apply(WHITE));
        value.put(pieceInitialPosition.flipVertically(), pieceConstructor.apply(BLACK));
        value.put(pieceInitialPosition.flipDiagonally(), pieceConstructor.apply(BLACK));
    }

    private void initializeTwoPiecesOf(Position pieceInitialPosition, Function<Camp, Piece> pieceConstructor) {
        value.put(pieceInitialPosition, pieceConstructor.apply(WHITE));
        value.put(pieceInitialPosition.flipVertically(), pieceConstructor.apply(BLACK));
    }

    private void initializePawn() {
        for (Column column : Column.values()) {
            initializeTwoPiecesOf(new Position(column, PAWN_INITIAL_ROW), Pawn::new);
        }
    }

    private void initializeBlanks() {
        for (Column column : Column.values()) {
            initializeBlankColumn(column);
        }
    }

    private void initializeBlankColumn(Column column) {
        for (int i = BLANK_INITIAL_START_ROW_INDEX; i <= BLANK_INITIAL_END_ROW_INDEX; i++) {
            value.put(new Position(column, Row.values()[i]), new NullPiece(null));
        }
    }

    public void move(Position beforePosition, Position afterPosition) {
        Piece piece = this.value.get(beforePosition);

        if (isBlank(beforePosition)) {
            throw new IllegalArgumentException("이동할 수 있는 기물이 없습니다.");
        }
        if (piece.isBlack() == whiteTurn) {
            throw new IllegalArgumentException("상대 진영의 차례입니다.");
        }
        if (!piece.isKnight() && !PathCheck.check(beforePosition, afterPosition, this::isBlank)) {
            throw new IllegalArgumentException("경로에 기물이 있어 움직일 수 없습니다.");
        }

        this.whiteTurn = !whiteTurn;
        if (isBlank(afterPosition)) {
            piece.move(beforePosition, afterPosition, moveFunction(beforePosition, afterPosition));
            return;
        }
        if (isCapturing(piece, afterPosition)) {
            piece.capture(beforePosition, afterPosition, moveFunction(beforePosition, afterPosition));
            return;
        }

        throw new IllegalArgumentException("같은 팀 기물이 있는 위치로는 이동할 수 없습니다.");
    }

    private Consumer<Piece> moveFunction(Position beforePosition, Position afterPosition) {
        return (piece) -> {
            this.value.put(afterPosition, piece);
            this.value.put(beforePosition, new NullPiece(null));
        };
    }

    private boolean isBlank(Position afterPosition) {
        return value.get(afterPosition).isNull();
    }

    private boolean isCapturing(Piece piece, Position afterPosition) {
        return !piece.isSameCampWith(value.get(afterPosition));
    }

    public boolean hasKingCaptured() {
        return 2 != collectKing().size();
    }

    private List<Piece> collectKing() {
        return this.value.values()
            .stream()
            .filter(Objects::nonNull)
            .filter(Piece::isKing)
            .collect(Collectors.toList());
    }

    public double calculateScoreOfBlack() {
        return Arrays.stream(Column.values())
            .map(this::collectBlackPiecesIn)
            .mapToDouble(pieces -> calculatePawnScoreIn(pieces) + calculateScoreWithoutPawnIn(pieces))
            .sum();
    }

    private List<Piece> collectBlackPiecesIn(Column column) {
        return Arrays.stream(Row.values())
            .map(row -> this.value.get(new Position(column, row)))
            .filter(Objects::nonNull)
            .filter(Piece::isBlack)
            .collect(Collectors.toList());
    }

    public double calculateScoreOfWhite() {
        return Arrays.stream(Column.values())
            .map(this::collectWhitePiecesIn)
            .mapToDouble(pieces -> calculatePawnScoreIn(pieces) + calculateScoreWithoutPawnIn(pieces))
            .sum();
    }

    private List<Piece> collectWhitePiecesIn(Column column) {
        return Arrays.stream(Row.values())
            .map(row -> this.value.get(new Position(column, row)))
            .filter(Objects::nonNull)
            .filter(piece -> !piece.isBlack())
            .collect(Collectors.toList());
    }

    private double calculateScoreWithoutPawnIn(List<Piece> pieces) {
        List<Piece> piecesWithoutPawn = collectPiecesWithoutPawnIn(pieces);
        return piecesWithoutPawn.stream()
            .mapToDouble(Piece::getScore)
            .sum();
    }

    private List<Piece> collectPiecesWithoutPawnIn(List<Piece> pieces) {
        return pieces.stream()
            .filter(piece -> !piece.isPawn())
            .collect(Collectors.toList());
    }

    private double calculatePawnScoreIn(List<Piece> pieces) {
        List<Piece> pawns = collectPawnsIn(pieces);
        if (pawns.size() == 0) {
            return 0;
        }
        if (pawns.size() > 1) {
            return 0.5 * pawns.size();
        }
        return pawns.get(0).getScore() * pawns.size();
    }

    private List<Piece> collectPawnsIn(List<Piece> pieces) {
        return pieces.stream()
            .filter(Piece::isPawn)
            .collect(Collectors.toList());
    }

    public Map<Position, Piece> getValue() {
        return Collections.unmodifiableMap(value);
    }

    public boolean hasBlackKingCaptured() {
        return collectKing().stream()
            .noneMatch(Piece::isBlack);
    }

    public boolean hasWhiteKingCaptured() {
        return collectKing().stream()
            .allMatch(Piece::isBlack);
    }
}
