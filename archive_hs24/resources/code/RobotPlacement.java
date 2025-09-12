import java.util.Arrays;

public class RobotPlacement {
	
	private static final char ROBOT = 'R';
	private static final char TREASURE_AREA = 'T';
	private static final char BLANK = '-';
	
	public static void main(String[] args) {
		char[][] areaA = {   {'-','-','-','-','R'}, 
						     {'-','R','-','-','-'},
							 {'-','-','-','R','-'},
							 {'-','-','R','-','-'},
							 {'-','-','R','-','-'}  };
		int[] conflictingRobotsA = findConflictingRobot(areaA);
		System.out.println(Arrays.toString(conflictingRobotsA));
		
		char[][] areaB = {   {'-','-','-','-','-'}, 
							 {'-','-','-','-','-'},
							 {'-','T','T','-','-'},
							 {'-','T','T','-','-'},
							 {'-','-','-','-','-'}  };
		char[][] robotsB = placeOtherRobots(areaB);
		System.out.println(Arrays.deepToString(robotsB));
		
		char[][] areaC = {   {'-','-','-','-','-'}, 
							 {'-','-','-','-','-'},
							 {'R','T','T','-','-'},
							 {'-','T','T','R','-'},
							 {'-','-','-','-','-'}  };
		char[][] robotsC = placeOtherRobots(areaC);
		System.out.println(Arrays.deepToString(robotsC));
	}

	// Find a robot that blocks other robots' lazer light
	// O(h^2)
	public static int[] findConflictingRobot(char[][] area) {
		int h = area.length;
		int numberOfRobots = countRobots(area, h);
		
		int[][] robots = new int[numberOfRobots][];
		int index = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < h; j++) {
				if (area[i][j] != ROBOT) {
					continue;
				}
				robots[index++] = new int[] {i, j};
			}
		}
				
		return findConflictingRobot(numberOfRobots, robots);
	}
	
	private static int countRobots(char[][] area, int h) {
		int count = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < h; j++) {
				if (area[i][j] != ROBOT) {
					continue;
				}
				count++;
			}
		}
		return count;
	}
	
	private static int[] findConflictingRobot(int numberOfRobots, int[][] robots) {
		if (robots == null) {
			return null;
		}
		for (int i = 0; i < numberOfRobots; i++) {
			for (int j = 0; j < numberOfRobots; j++) {
				if (i == j) {
					continue;
				}
				if (conflictingRobots(robots[i], robots[j])) {
					return robots[i];
				}
			}
		}
		return null;
	}
	
	// O(1)
	private static boolean conflictingRobots(int[] a, int[] b) {
		if (a[0] == b[0] || a[1] == b[1]) {
			return true;
		}
		int verticalChange = a[0] - b[0];
		int horizontalChange = a[1] - b[1];
		if (Math.abs(verticalChange) == Math.abs(horizontalChange)) {
			return true;
		}	
		return false;
	}
	
	// O(h)
	private static boolean doesConflictingRobotExist(int h, int[][] robots, int[] robot) {
		for (int i = 0; i < h; i++) {
			if (robots[i] == null || robots[i] == robot) {
				continue;
			}
			if (conflictingRobots(robot, robots[i])) {
				return true;
			}
		}
		return false;
	}
	
	// O(1)
	private static boolean isInsideTreasureArea(int x, int y, char[][] area) {
		boolean isInsideTreasureArea = area[x][y] == TREASURE_AREA;
		return isInsideTreasureArea;
	}

	// Place h robots in a h*h area to protect the treasure.
	public static char[][] placeRobots(char[][] area) {
		int h = area.length;
		// Meaning of the entry robots[i] := Coordinates of the robot at row i
		// We use a h*2 array because the space complexity stays the same and it is
		// easier to work with a h*2 array since the return value is also in this format
		final int[][] robots = new int[h][];
		boolean success = placeRobotsRec(robots, 0, h, area);
		if (success) {
			return area;
		} else {
			return null;
		}
	}
	
	// Time complexity: O(h*(h!))
	// Space complexity: O(h^2) (assuming that Java GC works correctly)
	private static boolean placeRobotsRec(int[][] robots, int row, int h, char[][] area) {
		if (row == h) {
			return true; // we already found h robots since we start at 0
		}
		
		if (robots[row] != null) {
			boolean result = placeRobotsRec(robots, row+1, h, area);
			return result;
		}
		
		for (int i = 0; i < h; i++) {
			if (isInsideTreasureArea(row, i, area)) {
				continue;
			}
			
			robots[row] = new int[] { row, i };
			area[row][i] = ROBOT;

			// fail fast
			if (doesConflictingRobotExist(h, robots, robots[row])) {
				robots[row] = null; // backtrack
				area[row][i] = BLANK;
				continue;
			}
			if (placeRobotsRec(robots, row+1, h, area)) {
				return true; // found solution
			}
			robots[row] = null; // backtrack
			area[row][i] = BLANK;
		}
		return false;
	}

	// Place other robots with pre-installed robots in a h*h area to protect the treasure
	public static char[][] placeOtherRobots(char[][] area) {
		int h = area.length;
		final int[][] robots = new int[h][];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < h; j++) {
				if (area[i][j] != ROBOT) {
					continue;
				}
				robots[i] = new int[] {i, j};
 			}
		}
		
		boolean success = placeRobotsRec(robots, 0, h, area);
		if (success) {
			return area;
		} else {
			return null;
		}
	}
}
