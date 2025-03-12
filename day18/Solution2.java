/*** NOTE TO SELF: OPTIMIZE TO CHECK IF NEW CORRUPTED NODE IS IN THE OPTIMAL PATH
     AND IF SO, THEN NO NEED TO RUN a* ***/

// We out here learning A*!!!
// initialize everything:
	// open list : PriorityQueue<Map.Entry<Node, Integer> >
	// closed list: Map<Node, Integer> - Node to f cost
	
// Run algo:
	// pop min f
	// get neighbors and set parent
	// for each neighbor:
		// Process neighbor
		// if neighbor in open and said entry has lower f value: continue
		// add node to open list
	// push parent to closed list
		
// Process neighbor(parent, neighbor):
	// neighbor.cost = parent.cost + directCost
	// hValue = calcHValue();
	// f = neighbor.cost + hValue;
	// Return Map Entry

// calcHValue():
	// Use Manhattan Distance	

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

//Class for representing directions in a 2D Grid!
enum DIR {
	//This allows me to associate fields with the enum values!!!
	//SUPER DOPE!!!
	UP(-1,0), LEFT(0,-1), DOWN(1,0), RIGHT(0,1);
	
	public final int r, c;
	
	DIR(int _r, int _c) {
		this.r = _r;
		this.c = _c;
	}//end constructor

}//end DIR

//Class for representing the 2D grid and the set of Nodes in the maze	
class Grid {
	
	private final char EMPTY = '.', CORRUPTED = '#', VISITED = 'V', OPTIMAL = 'O';
	private Node[][] grid;
	private int height, width;
	private int[] start, end;
	private StringBuilder[] strGrid;
	
	public Grid(int h, int w, int startX, int startY, int endX, int endY) {
		height = h;
		width = w;
		start = new int[] {startY, startX};
		end = new int[] {endY, endX};
		grid = new Node[height][width];
		strGrid = new StringBuilder[height];
		
		//Initialize grid with empty Nodes
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(i == start[0] && j == start[1]) grid[i][j] = new Node(j, i, 0);
				else grid[i][j] = new Node(j, i);
			}//end for
		}//end for
		
		//Initialize strGrid
		for(int i = 0; i < height; i++) {
			strGrid[i] = new StringBuilder( String.valueOf(EMPTY).repeat(width) );
		}//end for
		
	}//end constructor
	
	public int getHeight() { return height; }//end getHeight
	public int getWidth() { return width; }//end getWidth
	public Node getStart() { return grid[start[0]][start[1]]; }//end getStart
	public Node getEnd() { return grid[end[0]][end[1]]; }//end getEnd
	public Node getNode(int x, int y) { return grid[y][x]; }//end getNode 
	
	public void corruptNode(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return;
		grid[y][x] = null;
		strGrid[y].setCharAt(x, CORRUPTED);
	}//end corruptNode
	
	public void populateNeighbors() {
		
		for(int r = 0; r < height; r++) {
			for(int c = 0; c < width; c++) {
				Node curr = this.getNode(c, r);
				
				if(curr == null) continue;
				
				for(DIR dir : DIR.values() ) {
					int newR = curr.getR() + dir.r;
					int newC = curr.getC() + dir.c;
					
					if(newR < 0 || newR >= height || newC < 0 || newC >= width) continue;
					
					Node nbr = this.getNode(newC, newR);
					
					if(nbr == null) continue;
					
					curr.addNeighbor(nbr, 1);
					
				}//end for
				
			}//end for
		}//end for
		
	}//end populateNeighbors
	
	public void markVisited(Node n) {
		if(n == null) return;
		strGrid[ n.getR() ].setCharAt(n.getC(), VISITED);
	}//end markTraveled
	
	public void markOptimal(Node n) {
		if(n == null) return;
		strGrid[ n.getR() ].setCharAt(n.getC(), OPTIMAL);
	}//end markOptimal
	
	// //Helper function for determining the cost to traverse from a Node to it's neighbor
	// private int calculateNeighborCost(Node a, Node b) {
		// return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY() );
	// }//end calculateCost
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		Arrays.stream(strGrid).forEach( line -> str.append(line).append("\n") );
		
		return str.toString();
	}//end toString
	
}//end Grid

class Node implements Comparable<Node> {
	private int x, y;                      //Coordinates	
	private int cost;                      //Cost to arrive at this node
	private Node parent;									  //Parent Node used to reach this Node
	private Map<DIR, Node> neighbors;      //Map of neighboring nnodes in 4 cardinal directions
	private Map<DIR, Integer> directCost;  //Map of direct cost to travel from this node to its neighbor
	
	public Node(int _x, int _y) {
		x = _x;
		y = _y;
		cost = Integer.MAX_VALUE;
		parent = null;
		neighbors = new HashMap<>();
		directCost = new HashMap<>();
	}//end Node
	
	public Node(int _x, int _y, int _cost) {
		x = _x;
		y = _y;
		cost = _cost;
		parent = null;
		neighbors = new HashMap<>();
		directCost = new HashMap<>();
	}//end Node

	public int getX() { return x; }//end getX
	public int getY() { return y; }//end getY
	public int getR() { return y; }//end getR
	public int getC() { return x; }//end getC
	public int getCost() { return cost; }//end getCost
	public void setCost(int c) { cost = c; }//end setCost
	public Node getParent() { return parent; }//end getParent
	public void setParent(Node p) { parent = p; }//end setParent
	
	Node getNeighbor(DIR dir) {
		return neighbors.getOrDefault(dir, null);
	}//end getNeighbor
	
	public int getDirectCost(DIR dir) {
		return directCost.getOrDefault(dir, -1);
	}//end getDirectCost
	
	public int getDirectCost(Node other) {
		if(other == null) return -1;
		
		DIR dir = null;
		
		for(Map.Entry<DIR, Node> nbr : neighbors.entrySet() ) {
			if( nbr.getValue().equals(other) ) dir = nbr.getKey();
		}//end for
		
		return getDirectCost(dir);
	}//end getDirectCost
	
	//Returns the direction other node is relative to this node
	//Other does NOT need to be an immediate neighbor
	public DIR getDirection(Node other) {
		if(other == null) return null;
		
		int distR = other.getY() - this.y, distC = other.getX() - this.x;
		int rChange = distR == 0 ? 0 : distR / Math.abs( distR );
		int cChange = distC == 0 ? 0 : distC / Math.abs( distC );
	
		for(DIR dir : DIR.values() ) {
			if( dir.r == rChange && dir.c == cChange) {
				return dir;
			}//end if
		}//end for
		
		return null;
	}//end getDirection
	
	public ArrayList<Node> getNeighbors() {
		ArrayList<Node> nbrs = new ArrayList<>();
		
		neighbors.forEach( (k,v) -> nbrs.add(v) );
		
		return nbrs;
	}//end getNeighbors
	
	//Used to add a neighbor with direct cost to the neighbors and directCost maps
	//Can also be used to update existing neighbor entries with no conditions
	public void addNeighbor(Node nbr, int dirCost) {
		if(nbr == null) return;
		DIR dir = getDirection(nbr);
		neighbors.put(dir, nbr);
		directCost.put(dir, dirCost);
	}//end addNeighbor
	
	//Updates Neighbor entry in the neighbors and directCost maps on the conidition that the direct
	//cost to traverse to that neighbor is cheaper.
	//Can be used to update cost for an existing neighbor, or replace the neighbor with a new neighbor
	//entirely if that new neighbor is cheaper to traverse to.
	//Returns: boolean - True, if succesfully updated; False if dirCost is not optimal.
	public boolean updateNeighbor(Node nbr, int dirCost) {
		if(nbr == null) return false;
	
		DIR dir = getDirection(nbr);

		int oldCost = directCost.get(dir);
	
		//Original cost is cheaper, so it will remain
		if(oldCost < dirCost) return false;

		//Update lists as long as dirCost is optimal
		neighbors.put(dir, nbr);
		directCost.put(dir, dirCost);	
		
		return true;
	}//end updateNeighbor
	
	//Comparison is achieved through the following precedence:
	//1) Cost to traverse to the Node
	//2) Y value 
	//3) X value
	//If all 3 values are equal, then the Nodes are treated as equal regardless of variations in 
	//neighbors maps or dirCost maps
	@Override
	public int compareTo(Node other) {  //For use with Priority Queues 
		int otherVal = other.getCost();
	
		//First compare by costs
		if(this.cost != otherVal) return Integer.compare(this.cost, otherVal);

		otherVal = other.getY();
		
		//If same costs, then compare by Y values
		if(this.y != otherVal) return Integer.compare(this.y, otherVal);
		
		otherVal = other.getX();
		
		//If same Y values, then compare by X values
		if(this.x != otherVal) return Integer.compare(this.x, otherVal);
		
		return 0; //Otherwise theyre the same Node
	}//end compareTo
	
	@Override
	public boolean equals(Object obj) { //For use with Sets, Maps, & easier comparison
		if(this == obj) return true;
		if(this == null || !(obj instanceof Node) ) return false;
		Node other = (Node) obj;
		return this.x == other.getX() && this.y == other.getY();
	}//end equals

	@Override
	public int hashCode() { //For use with Sets and Maps
		return 109 * this.x + this.y;
	}//end hashCode
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("Node (x,y) = (").append(this.x).append(",");
		str.append(this.y).append(") Ovr Cost: ").append(this.cost);
		str.append(" Direct Costs: ");
		
		for(DIR dir : DIR.values() ) {
			if( !neighbors.containsKey(dir) ) continue;
			Node nbr = neighbors.get(dir);
			int cst = directCost.get(dir);
			str.append(dir).append(" = (").append( nbr.getX() ).append(",");
			str.append( nbr.getY() ).append(") @ cost ").append(cst).append(" ");
		}//end for
		
		return str.toString();
	}//end toString
	
}//end Node

public class Solution2 {

	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		final int MEMSPACEW = 71, MEMSPACEH = 71;
		final int STARTX = 0, STARTY = 0;
		final int ENDX = MEMSPACEW - 1, ENDY = MEMSPACEH - 1;
		int numFallen = 1024;
		Scanner fileIn = null;
		ArrayList<int[]> corruptedNodes = new ArrayList<>();
		int minSteps = 0;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read corrupted nodes list from file
		readFile(fileIn, corruptedNodes);
		
		for(; numFallen < corruptedNodes.size() && minSteps != -1; numFallen++) {
		
			//Create new Memory Space with selected settings
			Grid memorySpace = new Grid(MEMSPACEH, MEMSPACEW, STARTX, STARTY, ENDX, ENDY);
			
			//Corrupt all fallen bytes in the memory space up to index numFallen 
			corruptedNodes.stream().limit(numFallen+1).forEach( i -> memorySpace.corruptNode(i[0], i[1]) );
			
			//Populate all Node's neighbor lists
			memorySpace.populateNeighbors();
			
			//Calculate the minimum number of steps to traverse from start to end
			minSteps = findShortestPath(memorySpace);
			
			//System.out.println( memorySpace );
			//System.out.println( "Min Steps: " + minSteps );
		
		}//end for
		
		int byt[] = corruptedNodes.get(numFallen - 1); 
		System.out.println("First blocking byte = " + byt[0] + "," + byt[1]); 
		
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
	
	public static void readFile(Scanner fin, ArrayList<int[]> nodes) {
		
      while(fin.hasNextLine() ) {
        nodes.add( Arrays.stream( fin.nextLine().split(",") ).map(String::trim)
												 .mapToInt(Integer::parseInt).toArray() );
      }//end while
		
	}//end readFile
	
	public static int findShortestPath(Grid maze) {
		//Will store all processed nodes once min cost is found
		Map<Node, Integer> processed = new HashMap<>();
		//Used for storing and sorting prospective neighbors
		PriorityQueue< Map.Entry<Node, Integer> > pq = new PriorityQueue<>( 
			Comparator.comparingInt(Map.Entry::getValue) 
		);
		//Hashmap for efficient searching of prioiryt queue
		Map<Node, Integer> pqMap = new HashMap<>();
		Node end = maze.getEnd();
		
		//Get Map Entry for Start Node and place it in pq
		Map.Entry<Node, Integer> startEntry = calcNodeCosts(null, maze.getStart(), end);
		pq.add(startEntry);
		pqMap.put(startEntry.getKey(), startEntry.getValue() );
		
		//Continue searching until no more nodes to visit
		while(!pq.isEmpty() ) {
			
			//Remove next node from pq (and pq map) store as curr
			Map.Entry<Node, Integer> currEntry = pq.poll();
			Node curr = currEntry.getKey();
			pqMap.remove(curr);
			
			//Mark node as visited in Grid
			maze.markVisited(curr);
			
			//Retrieve list of curr's neighbors
			ArrayList<Node> neighbors = curr.getNeighbors();
			
			//Set all unprocessed neighbor's parent to curr
			neighbors.stream().filter(n -> !processed.containsKey(n)).forEach(
				n -> n.setParent(curr) 
			);
			
			for(Node neighbor : neighbors) {
				//Get Map Entry for neighbor Node
				Map.Entry<Node, Integer> nbrEntry = calcNodeCosts(curr, neighbor, end);
				
				//If we've reached the end, we're done!
				if( neighbor.equals(end) ) return neighbor.getCost();
				
				//If neighbor already in pq and we found a cheaper cost, update cost in pq
				if( nbrEntry.getValue() < pqMap.getOrDefault(neighbor, -1) ) {
					//Remove old entry from pq
					pq.remove( new AbstractMap.SimpleEntry<>(neighbor, pqMap.get(neighbor) ) );
					pq.add(nbrEntry);														//Place new entry in pq  
					pqMap.put(neighbor, nbrEntry.getValue() );  //Update fcost in pqMap
					continue; 																	//Move to next neighbor
				}//end if
			
				//If neighbor already processed, but we found a cheaper cost, update cost
				if( nbrEntry.getValue() < processed.getOrDefault(neighbor, -1) ) {
					processed.put(neighbor, nbrEntry.getValue() );
					continue;
				}//end if
				
				//If neighbor already in pq or processed, but already cheaper, move to next neighbor
				if( pqMap.containsKey(neighbor) || processed.containsKey(neighbor) ) 
					continue;
			
				//Add neighbor to pq
				pq.add(nbrEntry);
			}//end for
			
			//Add current node to processed list
			processed.put(curr, currEntry.getValue() );
		}//end while

	return -1;
}//end findShortestPath

public static Map.Entry<Node, Integer> calcNodeCosts(Node parent, Node curr, Node end) {
	int parentCost = parent == null ? 0 : parent.getCost();
	int directCost = parent == null ? 0 : parent.getDirectCost(curr);
	int gCost = parentCost + directCost;
	int hCost = Math.abs( curr.getX() - end.getX() ) + 
							Math.abs( curr.getY() - end.getY() );
	
	//Update current cost
	if(gCost < curr.getCost() ) curr.setCost(gCost); 
	
	return new AbstractMap.SimpleEntry<>(curr, gCost + hCost); 
}//end calcNodeCosts

//Ignore this, I never bothered keeping neighbor lists updated so this is bugged.
//I only included this rather than just returning end node cost because 	
//because I was thinking of how I could utilize this to solve the parental direction dilemna
//in the Dijkstra's problem for day 16 part 2 since it's currently kicking my ass for no reason.
public static int traceOptimalPath(Grid maze) {
	Node start = maze.getStart(), end = maze.getEnd();
	Node prev = end;
	Node curr = end.getParent();
	int cost = 0;
	
	while( !curr.equals(start) && curr != null ) {
		//Update cost by cost to traverse from parent to curr
		int dirCost = curr.getDirectCost(prev);
		cost += dirCost == -1 ? Integer.MAX_VALUE : dirCost; 			
		maze.markOptimal(curr);   //Mark part of optimal path in strGrid
		prev = curr;              //Slide pointers 
		curr = curr.getParent();
	}//end while
	
	if (!curr.equals(start)) return -1; //Indicates no successful path found
	
	int dirCost = curr.getDirectCost(prev);
	cost += dirCost == -1 ? Integer.MAX_VALUE : dirCost;
	
	maze.markOptimal(start);
	maze.markOptimal(end);
	
	return cost;
}//end traceOptimalPath

}//end Solution
