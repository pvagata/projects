import java.util.Iterator;


public class Board {
	public class Move {
		int row = 0;
		private int col = 0;
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return col;
		}
		private void setValues(int r, int c) {
			row = r;
			col = c;
		}
	}
	
	protected short [] board;
	public final int dim;
	boolean gameOver = false;
	boolean whiteHasMoves = true;
	boolean blackHasMoves = true;
	private Move lastMove = new Move();
	public Board(int dimension) {
		dim = dimension;
		board = new short[dimension * dimension];
		for (int i = 0; i < board.length; i++) {
			board[i] = Piece.NULL;
		}
		board[Utils.getIdx((dim/2) - 1, (dim/2) - 1, dim)] = Piece.BLACK;
		board[Utils.getIdx((dim/2) - 1, (dim/2), dim)] = Piece.WHITE;
		board[Utils.getIdx((dim/2), (dim/2) - 1, dim)] = Piece.WHITE;
		board[Utils.getIdx((dim/2), (dim/2), dim)] = Piece.BLACK;
	}
	
	public Move getLastMove(){
		return lastMove;
		
	}
	
	public boolean isCorner(int idx) {
		return false;
	}
	
	public boolean isEdge(int idx) {
		return false;
	}
	

	
	private int getIdx(int row, int col) {
		return Utils.getIdx(row, col, dim);
	}
	
	public short getWinner() {
		int whiteScore = 0;
		int blackScore = 0;
		for (int pos : board) {
			if (pos == Piece.WHITE) {
				whiteScore++;
			} else if(pos == Piece.BLACK) {
				blackScore++;
			}
		}
		if (whiteScore > blackScore) {
			return Piece.WHITE; 
		} else if(whiteScore < blackScore) {
			return Piece.BLACK;
		} 
		return Piece.NULL;
	}
	
	
	
	private boolean attemptMove(int row, int col, short player) {
		return attemptMove(row, col, player, true);
	}
	

	public boolean attemptMove(int row, int col, short player, boolean flip){
		if (row > dim || col > dim || row < 0 || col < 0) {
			return false;
		}
		int idx = getIdx(row,col);
		
		if (board[idx] != Piece.NULL) return false;

		boolean validMove = false;
		int [] updateDirections = new int [] {1, 0, -1};
		for (int updateRow : updateDirections) {
			for (int updateCol: updateDirections) {
				validMove |= Utils.tunnel(board, row, col, dim, updateRow, updateCol, player, flip);
			}
		}
		if (validMove) {

			if (flip) {
				lastMove.setValues(row, col);
			}
		}
		
		return validMove;
	}
	
	private boolean isGameOver() {
		return !hasRemainingMoves(Piece.WHITE) && !hasRemainingMoves(Piece.BLACK);
	}
	
	public boolean hasRemainingMoves(short player) {
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				if(attemptMove(row, col, player, false)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param row
	 * @param col
	 * @param player
	 * @return whether your move was good or not.
	 */
	public boolean makeMove(int row, int col, short player) {
		if (!attemptMove(row, col, player)) {
			return false;
		}
		else {
			board[getIdx(row, col)] = player;
			return true;
		}
	}

	public void printBoard() {
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		String s = "  ";
		for (int i = 0; i < dim; i++){
			s += " " + i;
		}
		s += "\n  ";
		for (int i = 0; i < dim; i++){
			s += " _";
		}
		int curIdx = 0;
		for (int piece : board) {
			if (curIdx % dim == 0) {
				s += "\n" + curIdx/dim + " |"; 
			}
			if (piece == Piece.NULL) {
				s += "_";
			} else if (piece == Piece.BLACK) {
				s += "@";
			} else {
				s += "O";
			} 
			s += "|";
			curIdx++;
		}
		return s;
	}

	
	
}
