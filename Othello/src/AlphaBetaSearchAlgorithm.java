
public class AlphaBetaSearchAlgorithm extends SearchAlgorithm {
	final BoardPool boardPool;
	public AlphaBetaSearchAlgorithm(int dim, int depth) {
		boardPool = new BoardPool(dim, depth);
	}
	
	@Override
	public int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker) {
		return findAlphaBetaOptimalMove(board, dim, curPlayer, scorer, depth, curDepth, hadPrevValidMove,  moveMaker, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public int numberPruned = 0;
	private int findAlphaBetaOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer,
			int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker,  int alpha, int beta) {
		// Only score at leaves 
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
		
		short [] curBoard = boardPool.getBoard(curDepth, curPlayer);
  
		
		// begin minimax-ing
		// Start by assuming the worst outcome for your opponent
		boolean hasValidMove = false;
		boolean pruned = false; 
		
		for (int row = 0; row < dim; row++) {
			if (pruned) {
				break;
			}
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
						int opponentScore = findAlphaBetaOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth - 1, true, moveMaker, alpha, beta);
						
						if (curPlayer == Piece.WHITE) {	//	maximizing player
							if (opponentScore > alpha) { // found a better move
								alpha = opponentScore;
								// if this is the root of the investigation, update the best move known
								if(depth == curDepth) {
									moveMaker.updateBestMove(row,col);
								}
								//bestOpponentScore = alpha;

								if(beta <= alpha) {
									pruned = true;
									numberPruned++; 
									break;
								}
							}
						} else {
							if(opponentScore < beta) {
								beta = opponentScore; 
								// return beta
								//bestOpponentScore = beta;
								// if this is the root of the investigation, update the best move known
								if(depth == curDepth) {
									moveMaker.updateBestMove(row,col);
								}
								if(beta <= alpha) {
									pruned = true;
									numberPruned++; 
									break;
									
								}
							}
						}						
					}
				}
			}
		}
		if (hasValidMove) {
			return curPlayer == Piece.WHITE? alpha : beta;
		} else {
			if (hadPrevValidMove) {	// check to make sure previous player made a move
				// current player passes
				return findAlphaBetaOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth, false, moveMaker, alpha, beta);
			} else {
				// game is over because previous player and current player passed 
				return scorer.scoreBoard(curBoard);
			}
		}
	}
}
