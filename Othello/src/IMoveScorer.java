
public interface IMoveScorer {
	public int getScore();
	void scoreMove(short [] bd);
	void reset();
	public int getNumberLeaves();
	public int scoreBoard(short[] bd);
}
