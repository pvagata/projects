
public final class Piece {
	public static final short WHITE = 0;
	public static final short BLACK = 1;
	public static final short NULL = -1;
	public static short otherPiece(short curPiece) {
		return (curPiece == WHITE)  ? BLACK : WHITE;
	}
	public static String toString(short curPiece) {
		return curPiece == WHITE ? 
				"White" : 
			((curPiece == BLACK) ? "Black" : "Empty");
	}
}
