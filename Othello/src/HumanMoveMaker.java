import java.util.Scanner;


public class HumanMoveMaker implements MoveMaker {
	short color = Piece.NULL;
	Board board;
	Scanner sc;
	public HumanMoveMaker(Scanner scanner, Board b, short playerColor) {
		board = b;
		color = playerColor;
		sc = scanner;
	}
	
	@Override
	public void makeMove() {
		boolean validMove = false;
		System.out.println("Talking to you, " + Piece.toString(color));
		while (!validMove) {
			System.out.print("row? ");
			int row = sc.nextInt();
			System.out.print("col? ");
			int col = sc.nextInt();
			validMove = board.makeMove(row, col, color);
			if (!validMove) {
				System.out.println("(" + row + ", " + col + ") is an invalid move." +
						" A robot is balking at your clowniness.");
			}
		}
	}
	
	@Override
	public short getColor() {
		return color;
	}

	@Override
	public void updateBestMove(int row, int col) {
		// pass
	}

	@Override
	public double getElapsedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
