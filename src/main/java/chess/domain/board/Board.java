package chess.domain.board;

import chess.domain.Camp;
import chess.domain.piece.NullPiece;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceProperty;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Board {

    private static final int TOTAL_KING_COUNT = 2;
    private static final String CANT_MOVE_TO_SAME_CAMP = "같은 팀 기물이 있는 위치로는 이동할 수 없습니다.";
    private static final String CANT_MOVE_EMPTY_PIECE = "빈 기물을 움직일 수 없습니다.";

    private final Map<Position, Piece> board;

    public Board() {
        this.board = BoardFactory.generate();
    }

    public boolean checkNotKnight(final Position position) {
        final Piece piece = board.get(position);
        return piece.getPieceProperty() != PieceProperty.KNIGHT;
    }

    public boolean isNotValidCamp(final Position position, final Camp camp) {
        return !board.get(position).isSameCampWith(camp);
    }

    public boolean isBlankPosition(final Position position) {
        return board.get(position).isNullPiece();
    }

    public void moveIfValidPiece(final Positions positions) {
        if (isBlankPosition(positions.before())) {
            throw new IllegalArgumentException(CANT_MOVE_EMPTY_PIECE);
        }
        if (isAfterPieceSameCamp(positions)) {
            throw new IllegalArgumentException(CANT_MOVE_TO_SAME_CAMP);
        }

        Piece beforePiece = board.get(positions.before());
        beforePiece.move(positions, moveFunction(positions));
    }

    public void moveIfValidPiece(Position beforePosition, Position afterPosition) {
        moveIfValidPiece(new Positions(beforePosition, afterPosition));
    }

    private boolean isAfterPieceSameCamp(final Positions positions) {
        return board.get(positions.before())
            .isSameCampWith(board.get(positions.after()));
    }

    private Consumer<Piece> moveFunction(final Positions positions) {
        return (piece) -> {
            board.put(positions.after(), piece);
            board.put(positions.before(), new NullPiece(null));
        };
    }

    public boolean hasKingCaptured() {
        return TOTAL_KING_COUNT != collectKing().size();
    }

    private List<Piece> collectKing() {
        return board.values()
            .stream()
            .filter(this::isKing)
            .collect(Collectors.toList());
    }

    private boolean isKing(final Piece piece) {
        return piece.getPieceProperty() == PieceProperty.KING;
    }

    public boolean hasBlackKingCaptured() {
        return collectKing().stream()
            .noneMatch(Piece::isBlack);
    }

    public boolean hasWhiteKingCaptured() {
        return collectKing().stream()
            .allMatch(Piece::isBlack);
    }

    public boolean isPawn(final Position position) {
        final Piece piece = board.get(position);
        return piece.getPieceProperty() == PieceProperty.PAWN;
    }

    public boolean isKingChecked(final Camp camp) {
        final Position currentKingPosition = findCurrentKingPosition(camp);

        return board.entrySet()
            .stream()
            .filter(it -> it.getValue().isSameCampWith(camp.switchCamp()))
            .anyMatch(oppositePieceEntry -> canMoveOppositePieceToKing(currentKingPosition, oppositePieceEntry));
    }

    public List<Position> getKingCheckmatedPositions(final Camp camp) {
        final SortedMap<UnitDirectVector, List<Position>> movableKingPositions = findMovableKingPositionsBy(camp);

        final List<Entry<Position, Piece>> oppositeEntry = board.entrySet()
            .stream()
            .filter(it -> it.getValue().isSameCampWith(camp.switchCamp()))
            .collect(Collectors.toList());

        final List<Position> flattedMovableKingPositions = movableKingPositions.values().stream().flatMap(List::stream)
            .collect(Collectors.toList());

        return flattedMovableKingPositions.stream()
            .filter(position -> oppositeEntry.stream()
                .anyMatch(opposite -> canMoveOppositePieceToKing(position, opposite)))
            .collect(Collectors.toList());
    }

    private SortedMap<UnitDirectVector, List<Position>> findMovableKingPositionsBy(final Camp camp) {
        final Position currentKingPosition = findCurrentKingPosition(camp);

        return findMovablePositionsFrom(currentKingPosition);
    }

    public Position findCurrentKingPosition(final Camp camp) {
        return board.entrySet()
            .stream()
            .filter(it -> it.getValue().isSameCampWith(camp))
            .filter(it -> isKing(it.getValue()))
            .map(Entry::getKey)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    private boolean canMoveOppositePieceToKing(final Position kingPosition,
                                               final Entry<Position, Piece> oppositeEntry) {
        final Piece oppositePiece = oppositeEntry.getValue();
        final Position oppositePosition = oppositeEntry.getKey();
        final Positions fromOppositeTokingPositions = new Positions(oppositePosition, kingPosition);

        return oppositePiece.canMove(fromOppositeTokingPositions)
            && isNullPieceFromOppositeToKing(fromOppositeTokingPositions);
    }

    private boolean isNullPieceFromOppositeToKing(final Positions positions) {
        return positions.before().pathTo(positions.after())
            .stream()
            .allMatch(position -> isNullPiece().test(position));
    }

    public SortedMap<UnitDirectVector, List<Position>> findMovablePositionsFrom(final Position position) {
        Piece beforePiece = board.get(position);

        return beforePiece.findMovablePositionsByDirection(position, isNullPiece());
    }

    private Predicate<Position> isNullPiece() {
        return (position) -> board.get(position).isNullPiece();
    }

    public boolean isAllKingCheckmated(final Camp camp, final List<Position> positions) {
        final SortedMap<UnitDirectVector, List<Position>> movableKingPositions = findMovableKingPositionsBy(camp);

        final List<Position> flattedMovableKingPositions = movableKingPositions.values().stream().flatMap(List::stream)
            .collect(Collectors.toList());
        return positions.containsAll(flattedMovableKingPositions);
    }

    public Map<Position, Piece> getBoard() {
        return new TreeMap<>(board);
    }
}

