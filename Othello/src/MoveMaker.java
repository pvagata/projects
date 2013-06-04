
public interface MoveMaker {

	/**
	 * @param args
	 */
	public void makeMove(int move);
	public short getColor();
	public void updateBestMove(int row, int col);
	public double getElapsedTime();
}
