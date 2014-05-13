package FastForce;

import java.util.ArrayList;
import java.util.List;

public class Convert {

	public static int[] RelationToAdjancy(int[][] input) {
		List<Integer> result = new ArrayList<Integer>();
		int len = input.length;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < i; j++) {
				if (input[i][j] == 1) {
					result.add(i);
					result.add(j);
				}
			}
		}
		int resultlen = result.size();
		int[] adjacency = new int[resultlen];
		int loopnumber = 0;
		for (Object number : result) {
			adjacency[loopnumber++] = (Integer) number;
		}
		return adjacency;
	}

}
