
public interface MoveMaker {

	/**
	 * @param args
	 */
	public void makeMove();
	public short getColor();
	public void updateBestMove(int row, int col);
	public double getElapsedTime();
}
