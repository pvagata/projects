
public class Bot {
	Board gameBd;
	short player;
	short opponent;
	int depth;
	int bestRowMove = -1;
	int bestColMove = -1;
	IMoveScorer scorer;
	
	private class EightMatrixMoveScorer extends MoveScorer {
		@Override 
		public void scoreMove(short[] bd) {
			score = 0;
			/*
			 64 -8 8 8 8 8 -8 64
			 -8 -8 0 0 0 0 -8 -8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			 -8 -8 0 0 0 0 -8 -8
			 64 -8 8 8 8 8 -8 64
			 */
			int [] scoringMatrix = new int [] 
					{64, -8, 8, 8, 8, 8, -8, 64,
					-8, -8, 0, 0, 0, 0, -8, -8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					-8, -8, 0, 0, 0, 0, -8, -8,
					64, -8, 8, 8, 8, 8, -8, 64};
			for (int idx = 0; idx < bd.length; idx++) {
				if(bd[idx] == Piece.WHITE) {
					score += scoringMatrix[idx];
				} else if(bd[idx] == Piece.BLACK) {
					score -= scoringMatrix[idx];
				}
			}
		}
	}
	private class SimpleMoveScorer extends MoveScorer {
		@Override
		//only call when game is not over/making a move
		public void scoreMove(short[] bd) {
			score = 0;
			for (int idx = 0; idx < bd.length; idx++) {
				if (bd[idx] == Piece.WHITE) {
					score++;
				} else if(bd[idx] == Piece.BLACK) {
					score--;
				}
			}
		}
		
	}
	public abstract class MoveScorer implements IMoveScorer {
		protected int score = 0;
		@Override 
		public int getScore() {
			return score;
			
		}

		// only call when game is over
		public int scoreBoard(short [] bd) {
			int gamescore = 0;
			for (int idx = 0; idx < bd.length; idx++) {
	
				if (bd[idx] == Piece.WHITE) {
					gamescore++;
				} else if(bd[idx] == Piece.BLACK) {
					gamescore--;
				}
		
			}
			
			if (gamescore == 0) {
				return 0;
			} else {
				return gamescore < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			}
		}
		@Override 
		public void reset() {
			
		}
	}
	public Bot(Board board, short player, int depth) {
		gameBd = board;
		this.player = player;
		this.depth = depth;
		opponent = Piece.otherPiece(player);	
		if(board.dim == 8) {
			scorer = new EightMatrixMoveScorer();
		} else {
			scorer = new SimpleMoveScorer();
		}
	}

	private void updateBestMove(int row, int col) {
		this.bestRowMove = row;
		this.bestColMove = col;
	}
	private int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int curDepth, boolean hadPrevValidMove) {
		if (curDepth == 0) {
			scorer.scoreMove(board);
			return scorer.getScore();
		}
		
		short [] curBoard = new short[board.length];

		// begin minimax-ing
		int bestOpponentScore = curPlayer == Piece.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		boolean hasValidMove = false;
		for (int row = 0; row < dim; row++) {
			for (int col=0; col < dim; col++) {
				for (int i = 0; i < board.length; i++) {
					curBoard[i] = board[i]; 
				}
				
				int boardIdx = (row * dim) + col;
				if (curBoard[boardIdx] == Piece.NULL) {
					// potentially a valid move
					if(Utils.attemptMove(curBoard, row, col, dim, boardIdx, curPlayer)) {
						hasValidMove = true;
						// how good is this move for us?
						int opponentScore = findOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, curDepth - 1, true);
						int prevBestOpponentScore = bestOpponentScore;
						// if black, pick the minimum score
						if (curPlayer == Piece.BLACK){
							if(opponentScore <= bestOpponentScore) {
								bestOpponentScore = opponentScore;
								if(this.depth == curDepth) {
									updateBestMove(row,col);
								}

							}
						} else {
							// if white: maximize
							if(opponentScore >= bestOpponentScore) {
								bestOpponentScore = opponentScore;
								if(this.depth == curDepth) {
									updateBestMove(row,col);
								}

							}
						}

						
					}
				}
			}
		}
		if (hasValidMove) {
			return bestOpponentScore;
		} else {
			if (hadPrevValidMove) {
				return findOptimalMove(curBoard, dim, Piece.otherPiece(curPlayer), scorer, curDepth, false);
			} else {
				// game is over.
				return scorer.scoreBoard(curBoard);
			}
		}
	}

	private void clear() {
		scorer.reset();
		this.bestColMove = -1;
		this.bestRowMove = -1;
		
	}
	
	public void makeMove() {
		int optScore = findOptimalMove(gameBd.board, gameBd.dim, this.player, scorer, this.depth, true);
		System.out.println(String.format("making move: (%d,%d) with score %d", bestRowMove, bestColMove, optScore));
		gameBd.makeMove(bestRowMove, bestColMove, this.player);
		this.clear();
	}
}


