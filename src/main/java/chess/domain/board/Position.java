package chess.domain.board;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Position implements Comparable<Position> {

    private static final HashMap<String, Position> CACHE = new HashMap<>(64);
    private static final int NO_SIZE = 0;
    private static final int SIZE_START_INDEX = 0;
    private static final String NOT_VALID_POSITION_INPUT = "정상적인 이동경로가 아닙니다. 다시 입력해주세요!";

    private final Column column;
    private final Row row;

    private Position(Column column, Row row) {
        this.column = column;
        this.row = row;
    }

    public static Position from(String position) {
        final Column column = Column.from(position.charAt(0));
        final Row row = Row.from(position.charAt(1));
        return Position.of(column, row);
    }

    public static Position of(Column column, Row row) {
        return CACHE.computeIfAbsent(column.name() + row.name(), ignored -> new Position(column, row));
    }

    public int columnDirectedDistance(Position otherPosition) {
        return column.directedDistance(otherPosition.column);
    }

    public int columnDistance(Position otherPosition) {
        return column.distance(otherPosition.column);
    }

    public int rowDirectedDistance(Position otherPosition) {
        return row.directedDistance(otherPosition.row);
    }

    public int rowDistance(Position otherPosition) {
        return row.distance(otherPosition.row);
    }

    public Position flipHorizontally() {
        return new Position(column.flip(), row);
    }

    public Position flipVertically() {
        return new Position(column, row.flip());
    }

    public Position flipDiagonally() {
        return new Position(column.flip(), row.flip());
    }

    public List<Position> pathTo(Position otherPosition) {
        List<Row> rowPath = row.pathTo(otherPosition.row);
        List<Column> columnPath = column.pathTo(otherPosition.column);
        if (rowPath.size() == NO_SIZE) {
            return getVerticalPositions(columnPath);
        }
        if (columnPath.size() == NO_SIZE) {
            return getHorizontalPositions(rowPath);
        }
        return getDiagonalPositions(rowPath, columnPath);
    }

    private List<Position> getVerticalPositions(final List<Column> columnPath) {
        return columnPath.stream()
            .map(column -> new Position(column, row))
            .collect(Collectors.toList());
    }

    private List<Position> getHorizontalPositions(final List<Row> rowPath) {
        return rowPath.stream()
            .map(row -> new Position(column, row))
            .collect(Collectors.toList());
    }

    private List<Position> getDiagonalPositions(final List<Row> rowPath,
                                                final List<Column> columnPath) {
        //대각선 움직임이 클릭/입력은 잘못할 수도 있다. ->  예외처리 추가
        if (rowPath.size() != columnPath.size()) {
            throw new IllegalArgumentException(NOT_VALID_POSITION_INPUT);
        }
        return IntStream.range(SIZE_START_INDEX, rowPath.size())
            .mapToObj(index -> new Position(columnPath.get(index), rowPath.get(index)))
            .collect(Collectors.toList());
    }

    public boolean isSameRow(final Row row) {
        return this.row == row;
    }

    public double computeUnitDirectionVector(final Position after) {
        final int yVector = this.rowDirectedDistance(after);
        final int xVector = this.columnDistance(after);
        return Math.atan2(yVector, xVector);
    }

    public Column getColumn() {
        return column;
    }

    public Row getRow() {
        return row;
    }

    @Override
    public int compareTo(Position o) {
        return Comparator.comparing(Position::getRow, Comparator.reverseOrder())
            .thenComparing(Position::getColumn)
            .compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position that = (Position) o;
        return column == that.column && row == that.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    @Override
    public String toString() {
        return "" + column + row;
    }

    public Position calculatePossibleAfterPosition(final UnitDirectVector direction) {
        final Column nextColumn = column.nextWith(direction);
        final Row nextRow = row.nextWith(direction);
        return Position.of(nextColumn, nextRow);

    }

    public boolean isValidNextPosition(final UnitDirectVector direction) {
        if (!column.isNextValid(direction)) {
            return false;
        }
        return row.isNextValid(direction);
    }

    public boolean isNextValid(final UnitDirectVector direction) {
        return isValidNextPosition(direction);
    }

    public Position calculatePossibleAfterPositions(final UnitDirectVector direction) {
        return calculatePossibleAfterPosition(direction);
    }
}

