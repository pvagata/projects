public class Utils {
	/**
	 * @param row: row to make a move on
	 * @param col: column to make a move on
	 * @param rowUpdate: how to move rows (0, -1, 1)
	 * @param colUpdate: how to move columns (0, -1, 1)
	 * @param player: current player color
	 * @param flip: flips captured pieces while investigating
	 * @return whether the move specified was legal
	 * the combination of rowUpdate and colUpdate is the direction to investigate (i.e. diagonally, north, south, etc)
	 * if flip is set, the board will be updated to the correct state when a valid move was found.
	 */
	public static boolean tunnel(short[] board, int row, int col, int dim, int rowUpdate, int colUpdate, short player, boolean flip) {
	
		boolean validMove = false;
		boolean sawOpponent = false;
		boolean sawFlank = false;
		if (rowUpdate == 0 && colUpdate == 0) {
			return false;
		}
		int originalRow = row;
		int originalCol = col;
		
		row += rowUpdate;
		col += colUpdate;
		int curIdx;
			while(row < dim && col < dim && row >= 0 && col >= 0) {
				curIdx = getIdx(row, col, dim);
				if(board[curIdx] == Piece.NULL) {
					assert(sawFlank == false);
					break;
				}
				if (board[curIdx] == player) {
					sawFlank = true;
					break;
				}
				sawOpponent |= board[curIdx] == Piece.otherPiece(player);
				if (flip) {
					board[curIdx] = player;
				}
				row += rowUpdate;
				col += colUpdate;
			}
			validMove = sawFlank && sawOpponent;
			if (!validMove && flip) {
		 		// rolling back the flips we just made if flip is set
				row -= rowUpdate;
				col -= colUpdate;
				while (row != originalRow || col != originalCol) {
		 			curIdx = getIdx(row, col, dim);
					board[curIdx] = Piece.otherPiece(player);
		 			row -= rowUpdate;
		 			col -= colUpdate;
				}
			}
		return validMove || (sawFlank && sawOpponent);
	}

	protected static int getIdx(int row, int col, int dim) {
		return (row * dim) + col;
	}
	
	public static boolean attemptMove(short [] board, int row, int col, int dim, int boardIdx, short player){
		if (row > dim || col > dim || row < 0 || col < 0) {
			return false;
		}

		if (board[boardIdx] != Piece.NULL) return false;
		board[boardIdx] = player;

		boolean validMove = false;
		int [] updateDirections = new int [] {1, 0, -1};
		for (int updateRow : updateDirections) {
			for (int updateCol: updateDirections) {
				validMove |= Utils.tunnel(board, row, col, dim, updateRow, updateCol, player, true);
			}
		}
		
		return validMove;
	}

}