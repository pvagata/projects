import java.util.ArrayList;
import java.util.List;


public class BoardPool {
	List<short[]> whiteBoards;
	List<short[]> blackBoards;

	int numBoards;
	public BoardPool(int dim, int depth) {
		this(dim, new int[] {depth});
	}
	public BoardPool(int dim, int [] depths) {
		int sum = 0;
		for (int d = 0; d < depths.length; d++) {
			sum += depths[d];
		}
		numBoards = sum;
		whiteBoards = new ArrayList<short[]>(numBoards);
		blackBoards = new ArrayList<short[]>(numBoards);

		for(int i = 0; i < numBoards; i++) {
			for(int j = 0; j < 2; j++) {
				short[] b = new short[dim];
				for (int bi = 0 ; bi < dim; bi++) {
					b[bi] = Piece.NULL;
				}
				if (j%2 == 0) {
					whiteBoards.add(b);
				} else {
					blackBoards.add(b);
				}
			}
		}
	}
	
	public short[] getBoard(int depth, short player) {
		depth = depth - 1;
		if (player == Piece.WHITE) {
			return whiteBoards.get(depth);
		} else {
			return blackBoards.get(depth);
		}
		
	}
}
