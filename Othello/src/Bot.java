
public class Bot implements MoveMaker {
	Board gameBd;
	short player;
	short opponent;
	int depth;
	int bestRowMove = -1;
	int bestColMove = -1;
	IMoveScorer scorer;
	boolean minimax;
	SearchAlgorithm searchAlgorithm;
	
	public Bot(Board board, short player, int depth, IMoveScorer scorerChoice, boolean useMinimax) {
		gameBd = board;
		this.player = player;
		this.depth = depth;
		opponent = Piece.otherPiece(player);	
		scorer = scorerChoice;
		minimax = useMinimax;
		searchAlgorithm = minimax ? new MinimaxSearchAlgorithm() : new AlphaBetaSearchAlgorithm(board.board.length, depth);
	}
	
	@Override
	public void updateBestMove(int row, int col) {
		this.bestRowMove = row;
		this.bestColMove = col;
	}
	
	
	private void clear() {
		scorer.reset();
		this.bestColMove = -1;
		this.bestRowMove = -1;
	}
	double totalSeconds = 0.0;
	
	@Override
	public void makeMove() {
		long startTime = System.currentTimeMillis();
		int optScore = searchAlgorithm.findOptimalMove(gameBd.board, gameBd.dim, this.player, scorer, this.depth, this.depth, true, this);
				
		System.out.println(String.format("%s making move: (%d,%d) with score %d (%s currently winning)", 
				Piece.toString(player), bestRowMove, bestColMove, optScore, 
				Piece.toString(optScore > 0 ? Piece.WHITE: Piece.BLACK)));
		long endTime = System.currentTimeMillis();
		gameBd.makeMove(bestRowMove, bestColMove, this.player);
		System.out.println("evaluated " + scorer.getNumberLeaves() + " boards");
		double elapsedSeconds = ((endTime - startTime)/1000.0);
		System.out.println("elapsed time (seconds): " + elapsedSeconds);
		totalSeconds += elapsedSeconds;
		System.out.println("Boards/second " + (scorer.getNumberLeaves() / elapsedSeconds));
		System.out.println();
		//if (!minimax)
		//	System.out.println("pruned events " + numberPruned);

		this.clear();
	}
	
	@Override
	public short getColor() {
		return this.player;
	}
	
	@Override
	public String toString() {
		return "color: " + Piece.toString(this.player) +
				", depth: " + this.depth + 
				", Scorer: " + scorer.getClass().getName() + 
				", search algorithm: " + (minimax ? "Minimax" : "alpha-beta pruning");
	}

	@Override
	public double getElapsedTime() {
		// TODO Auto-generated method stub
		return totalSeconds;
	}
}


