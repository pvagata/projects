import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class AlphaBetaSearchSortAlgorithm extends SearchAlgorithm {
	final BoardPool boardPool;
	int sortLevel;
	public int numberPruned = 0;
	int dimension;
	int width;
	int sortedDepthLimit = 2;
	int depth;
	public class AlphaBetaSortMove {
		int row;
		int col; 
		int score;
		private AlphaBetaSortMove(int r, int c, int s) {
			row = r;
			col = c;
			score = s;
		}
		private AlphaBetaSortMove() {}
		
		@Override
		public String toString() {
			return "Score: " + score + ", Row: " + row + ", Col: " + col;
		}
	}
	public AlphaBetaFullIterable getFullIterable() {
		return new AlphaBetaFullIterable();
	}
	public class AlphaBetaFullIterable implements Iterable<AlphaBetaSortMove> {

		@Override
		public Iterator<AlphaBetaSortMove> iterator() {
			// TODO Auto-generated method stub
			return new AlphaBetaSortMoveIterator();
		}
		private class AlphaBetaSortMoveIterator implements Iterator<AlphaBetaSortMove> {
			int currentRow = 0;
			int currentCol = 0;
			AlphaBetaSortMove m = new AlphaBetaSortMove();
			@Override
			public boolean hasNext() {
				return (currentRow < width && currentCol < width);
			}

			@Override
			public AlphaBetaSortMove next() {
				m.row = currentRow;
				m.col = currentCol;
				currentCol++;
				// end of row
				if (currentCol == width) {
					currentRow++; 
					currentCol = 0;
				}
				return m;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
			}
			
		}
	}
	public AlphaBetaSearchSortAlgorithm(int width, int dim, int depth, int sortDepth, int sortLevel) {
		this.dimension = dim;
		this.width = width;
		boardPool = new BoardPool(dim, depth);
		this.sortLevel = sortLevel;
		this.depth = depth;
	}
	
	@Override
	public int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker) {
		return alphaBetaSortedMove(board, dim, curPlayer, scorer, depth, curDepth, sortedDepthLimit, hadPrevValidMove,  moveMaker, Integer.MIN_VALUE, Integer.MAX_VALUE);
		//return alphaBetaMove(new AlphaBetaFullIterable(), board, dim, curPlayer, scorer, depth, curDepth, hadPrevValidMove,  moveMaker, Integer.MIN_VALUE, Integer.MAX_VALUE);
	} 
	
	private int alphaBetaSortedMove(short[] board, int dim, short curPlayer, IMoveScorer scorer,
			int depth, int curDepth, int sortedCurDepth, boolean hadPrevValidMove, MoveMaker moveMaker,  int alpha, int beta) {
		// Only score at leaves 
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
		
		short [] curBoard = boardPool.getBoard(curDepth, curPlayer);
  
		List<AlphaBetaSortMove> sortedMoves = new ArrayList<AlphaBetaSortMove> ();
		// begin minimax-ing
		// Start by assuming the worst outcome for your opponent
		boolean hasValidMove = false;
		boolean pruned = false; 
		if (curDepth > sortLevel) {
			for (int sortedrow = 0; sortedrow < dim; sortedrow++) {

				for (int sortedcol=0; sortedcol < dim; sortedcol++) {
					// Pave the board
					for (int i = 0; i < board.length; i++) {
						curBoard[i] = board[i]; 
					}
					
					int sortedBoardIdx = (sortedrow * dim) + sortedcol;	// get board index for actual board representation
					
					if (curBoard[sortedBoardIdx] == Piece.NULL) {		// if current position has no pieces: potentially a valid move
						if(Utils.attemptMove(curBoard, sortedrow, sortedcol, dim, sortedBoardIdx, curPlayer)) {	// move attempted was valid - board mutated (post-move)
							hasValidMove = true;	//	remember that there was a legal move (for passing)
							
							// determine how well the opponent can respond
							int opponentScore = alphaBetaMove(new AlphaBetaFullIterable(), curBoard,
									dim, Piece.otherPiece(curPlayer), scorer, depth, 
									sortedCurDepth - 1, true, moveMaker, alpha, beta);
							sortedMoves.add(new AlphaBetaSortMove(sortedrow, sortedcol, opponentScore));
						}
					}
				}
			}
			Comparator<AlphaBetaSortMove> cmp;
			if (curPlayer == Piece.BLACK) {
				cmp = new Comparator<AlphaBetaSortMove> () {
					public int compare(AlphaBetaSortMove o1, AlphaBetaSortMove o2) {
						if (o1.score > o2.score) {
							return 1;
						} else if(o1.score < o2.score) {
							return -1;
						}
						return 0;
					}
				};
			} else {
				cmp = new Comparator<AlphaBetaSortMove> () {
					public int compare(AlphaBetaSortMove o1, AlphaBetaSortMove o2) {
						if (o1.score > o2.score) {
							return -1;
						} else if(o1.score < o2.score) {
							return 1;
						}
						return 0;
					}
				};
			}
			Collections.sort(sortedMoves, cmp);
			return alphaBetaMove(sortedMoves, board, dim, curPlayer, scorer, depth, curDepth, true, moveMaker, alpha, beta);

		} else {
			return alphaBetaMove(new AlphaBetaFullIterable(), board, dim, curPlayer, scorer, depth, curDepth, true, moveMaker, alpha, beta);
		}
		
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
	private int alphaBetaMove(Iterable<AlphaBetaSortMove> moves, short[] board, int dim, short curPlayer, IMoveScorer scorer,
		int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker,  int alpha, int beta) {

		// Only score at leaves 
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
	
		boolean pruned = false; 
		boolean hasValidMove = false;
		short [] curBoard = boardPool.getBoard(curDepth, curPlayer);
	
		for (AlphaBetaSortMove m : moves) {
			int row = m.row;
			int col = m.col;
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
					if (curDepth > sortLevel) {
						opponentScore = alphaBetaSortedMove(curBoard, dim, Piece.otherPiece(curPlayer), 
								scorer, depth, curDepth - 1, sortedDepthLimit, true, moveMaker, alpha, beta);
					} else {
						opponentScore = alphaBetaMove(new AlphaBetaFullIterable(), curBoard, dim, Piece.otherPiece(curPlayer),
								scorer, depth, curDepth - 1, true, moveMaker, alpha, beta);
					}
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
		if (hasValidMove) {
			return curPlayer == Piece.WHITE? alpha : beta;
		} else {
			if (hadPrevValidMove) {	// check to make sure previous player made a move
				// current player passes
				return alphaBetaMove(new AlphaBetaFullIterable(), curBoard, dim, Piece.otherPiece(curPlayer), scorer, depth, curDepth, false, moveMaker, alpha, beta);
			} else {
				// game is over because previous player and current player passed 
				return scorer.scoreBoard(curBoard);
			}
		}
	}

	@Override
	public int getDepth(int move) {
		// TODO Auto-generated method stub
		return depth;
	}
}

