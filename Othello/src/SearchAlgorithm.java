
public abstract class SearchAlgorithm {
	
	public abstract int getDepth(int move);
	public abstract int findOptimalMove(short[] board, int dim, short curPlayer, IMoveScorer scorer, int depth, int curDepth, boolean hadPrevValidMove, MoveMaker moveMaker);
}
