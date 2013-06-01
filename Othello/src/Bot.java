
public class Bot implements MoveMaker {
	Board gameBd;
	short player;
	short opponent;
	int depth;
	int bestRowMove = -1;
	int bestColMove = -1;
	IMoveScorer scorer;
	
	public Bot(Board board, short player, int depth, IMoveScorer scorerChoice) {
		gameBd = board;
		this.player = player;
		this.depth = depth;
		opponent = Piece.otherPiece(player);	
		scorer = scorerChoice;
		
	}
	/*
	public Bot(Board board, short player, int depth) {
		MoveScore
		if(board.dim == 8) {
			scorerChoice = new EightMatrixMoveScorer();
		} else {
			scorerChoice = new SimpleMoveScorer();
		}
	}*/

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
	
	@Override
	public void makeMove() {
		int optScore = findOptimalMove(gameBd.board, gameBd.dim, this.player, scorer, this.depth, true);
		System.out.println(String.format("%s making move: (%d,%d) with score %d (%s currently winning)", 
				Piece.toString(player), bestRowMove, bestColMove, optScore, 
				Piece.toString(optScore > 0 ? Piece.WHITE: Piece.BLACK)));
		gameBd.makeMove(bestRowMove, bestColMove, this.player);
		this.clear();
	}
	
	@Override
	public short getColor() {
		return this.player;
	}
}


