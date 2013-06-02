
public class MinimaxSearchAlgorithm extends SearchAlgorithm {
	@Override
	public int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, 
			int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker) {
		return findMinimaxOptimalMove(board, dim, curPlayer, scorer, depth, curDepth, hadPrevValidMove, moveMaker);
	}
	
	public int findMinimaxOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, 
			int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker) {
		// Only score at leaves 
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
		
		short [] curBoard = new short[board.length];

		// begin minimax-ing
		// Start by assuming the worst outcome (if white, assume lost to black)
		int bestOpponentScore = curPlayer == Piece.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		boolean hasValidMove = false;
		
		for (int row = 0; row < dim; row++) {
			for (int col=0; col < dim; col++) {
				// Pave the board
				for (int i = 0; i < board.length; i++) {
					curBoard[i] = board[i]; 
				}
				
				int boardIdx = (row * dim) + col;	// get board index for actual board representation
				
				if (curBoard[boardIdx] == Piece.NULL) {		// if current position has no pieces: potentially a valid move
					if(Utils.attemptMove(curBoard, row, col, dim, boardIdx, curPlayer)) {	// move attempted was valid - board mutated (post-move)
						hasValidMove = true;	//	remember that there was a legal move (for passing)
						
						// determine how well the opponent can respond
						int opponentScore = findMinimaxOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth - 1, true, moveMaker);
						
						// if black, pick the minimum score, else if white, pick the maximum score 
						if ((curPlayer == Piece.BLACK && opponentScore < bestOpponentScore) || 
								(curPlayer == Piece.WHITE && opponentScore > bestOpponentScore)) {
							// current Opponent's response is better than the previous best
							bestOpponentScore = opponentScore;
							// if this is the root of the investigation, update the best move known
							if(depth == curDepth) {
								moveMaker.updateBestMove(row,col);
							}
						} 						
					}
				}
			}
		}
		if (hasValidMove) {
			return bestOpponentScore;
		} else {
			if (hadPrevValidMove) {	// check to make sure previous player made a move
				// current player passes
				return findMinimaxOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth, false, moveMaker);
			} else {
				// game is over because previous player and current player passed 
				return scorer.scoreBoard(curBoard);
			}
		}
	}
}
