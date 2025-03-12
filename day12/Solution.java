import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

class Dim {
	public int area, perim;
	
	public Dim(int _a, int _p) {
		area = _a;
		perim = _p;
	}//end constructor
	
	public void add(Dim d) {
		this.area += d.area;
		this.perim += d.perim;
	}//end add
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass() ) return false;
		Dim other = (Dim) obj;
		return other.area == this.area && other.perim == this.perim;
	}//end equals
	
}//end Dim

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<String> grid = new ArrayList<>();
		boolean[][] visited;
		int price = 0;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read grid into ArrayList
		readFile(fileIn, grid);
		
		//Create visited grid to mark visited elements
		visited = new boolean[grid.size()][grid.get(0).length()];
		
		//Initialize visited grid
		for(int i = 0; i < grid.size(); i++) {
			Arrays.fill(visited[i], false);
		}//end for
		
		//Calculate total price for each garden
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < grid.get(0).length(); j++) {
				
				//Calculate garden dimensions
				Dim gardenDim = calcGardenDim(grid, visited, i, j, grid.get(i).charAt(j) );
				
				// if (gardenDim.area != 0 && gardenDim.perim != 0) {
					// System.out.println("Region " + grid.get(i).charAt(j) + " has area " + gardenDim.area + " and perimeter " + gardenDim.perim + " for total of " + (gardenDim.perim * gardenDim.area) );
				// }//end if
				
				//Add price of garden
				price += gardenDim.area * gardenDim.perim;
			}//end for
		}//end for
		
		System.out.println(price);
	}//end main
	
	public static Scanner openFile(String fname) throws FileNotFoundException{
		Scanner scanner = null;
		
		try {
			File file = new File(fname);
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("ERROR: " + fname + " not found.");
		}//end try-catch
	
		return scanner;
	}//end openFile
	
	public static void readFile(Scanner fin, ArrayList<String> grid) {
		
		while(fin.hasNextLine() ) {
			grid.add( fin.nextLine() );
		}//end while
		
	}//end readFile
	
	public static boolean isInBounds(int r, int c, ArrayList<String> grid) {
		if (grid.size() == 0) return false;
		
		int width = grid.get(0).length();
		int height = grid.size();
		
		return (r >= 0 && r < height && c >= 0 && c < height);
	}//end isInBounds
	
	public static Dim calcGardenDim(ArrayList<String> grid, boolean[][] visit, int r, int c, char ch) {
		Dim thisDim = new Dim(0,0);
		
		//If part of same garden and already visited, return empty dimensions
		if (visit[r][c] && grid.get(r).charAt(c) == ch) {
			return thisDim;
		} else if (grid.get(r).charAt(c) == ch) { //If part of same garden and not visited, count for area and mark as visited
			thisDim.area++;
			visit[r][c] = true;
		} else { //If not part of same garden, count for perimeter and return
			thisDim.perim++;
			return thisDim;
		}//end if
		
		//Check right	
		if (isInBounds(r, c + 1, grid) ) {
			thisDim.add( calcGardenDim(grid, visit, r, c + 1, ch) );
		} else { //Increment perimeter if out of bounds
			thisDim.perim++;
		}//end if
		
		//Check down
		if (isInBounds(r + 1, c, grid) ) {
			thisDim.add( calcGardenDim(grid, visit, r + 1, c, ch) );
		} else { //Increment perimeter if out of bounds
			thisDim.perim++;
		}//end if
		
		//Check left
		if (isInBounds(r, c - 1, grid) ) {
			thisDim.add( calcGardenDim(grid, visit, r, c - 1, ch) );
		} else { //Increment perimeter if out of bounds
			thisDim.perim++;
		}//end if
		
		//Check up
		if (isInBounds(r - 1, c , grid) ) {
			thisDim.add( calcGardenDim(grid, visit, r - 1, c, ch) );
		} else { //Increment perimeter if out of bounds
			thisDim.perim++;
		}//end if
		
		return thisDim;
	}//end calcGardenPrice
	
}//end Solution