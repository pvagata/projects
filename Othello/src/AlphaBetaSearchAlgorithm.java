import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class AlphaBetaSearchAlgorithm extends SearchAlgorithm {
	final BoardPool boardPool;
	public int numberPruned = 0;
	int dimension;
	
	public AlphaBetaSearchAlgorithm(int dim, int depth) {
		this.dimension = dim;
		boardPool = new BoardPool(dim, depth);
	}
	
	@Override
	public int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker) {
		//return alphaBetaSortedMove(board, dim, curPlayer, scorer, depth, curDepth, sortedDepthLimit, hadPrevValidMove,  moveMaker, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return alphaBetaMove(board, dim, curPlayer, scorer, depth, curDepth, hadPrevValidMove,  moveMaker, Integer.MIN_VALUE, Integer.MAX_VALUE);
	} 
	
	/**
	 * @param moves: iterable of (row,col) pairs to investigate as potential moves
	 * @param board
	 * @param dim
	 * @param curPlayer
	 * @param scorer
	 * @param depth: fixed depth of investigation.
	 * @param curDepth: what the current level is for the investigation
	 * @param hadPrevValidMove
	 * @param moveMaker
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private int alphaBetaMove(short[] board, int dim, short curPlayer, IMoveScorer scorer,
		int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker,  int alpha, int beta) {

		// Only score at leaves 
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
	
		boolean pruned = false; 
		boolean hasValidMove = false;
		short [] curBoard = boardPool.getBoard(curDepth, curPlayer);
	
		for (int row = 0; row < dim; row++) {
			if(pruned) {
				break;
			}
			for (int col = 0; col < dim; col++) {
				// Pave the board
				for (int i = 0; i < board.length; i++) {
					curBoard[i] = board[i]; 
				}
				
				int boardIdx = (row * dim) + col;	// get board index for actual board representation
				
				if (curBoard[boardIdx] == Piece.NULL) {		// if current position has no pieces: potentially a valid move
					if(Utils.attemptMove(curBoard, row, col, dim, boardIdx, curPlayer)) {	// move attempted was valid - board mutated (post-move)
						hasValidMove = true;	//	remember that there was a legal move (for passing)
						
						// determine how well the opponent can respond
						int opponentScore;

						opponentScore = alphaBetaMove(curBoard, dim, Piece.otherPiece(curPlayer),
								scorer, depth, curDepth - 1, true, moveMaker, alpha, beta);
					
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
				return alphaBetaMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth, false, moveMaker, alpha, beta);
			} else {
				// game is over because previous player and current player passed 
				return scorer.scoreBoard(curBoard);
			}
		}
	}
}
