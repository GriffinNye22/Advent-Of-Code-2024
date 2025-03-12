import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

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

class Node implements Comparable<Node> {
	private int r, c;                //Position
	private int cost;                //Distance
	private DIR dir;                 //Direction
	
	public Node(int _r, int _c, int _cost, DIR _d) {
		r = _r;
		c = _c;
		cost = _cost;
		dir = _d;
	}//end constructor
	
	public int getR() { 
		return r;
	}//end getR
	
	public int getC() {
		return c;
	}//end getC
	
	public int getCost() {
		return cost;
	}//end getCost
	
	public void setCost(int c) {
		cost = c;
	}//end setCost
	
	public DIR getDir() {
		return dir;
	}//end getDir
	
	public void setDir(DIR d) {
		dir = d;
	}//end setDir
	
	//Used for updating Node to shortest cost and direction
	public void update(Node other) {
		if(other == null) return;
		
		if(other.getCost() < this.cost ) {
			this.cost = other.getCost();
			this.dir = other.getDir();
		}//end if
	}//end update
	
	@Override
	public int compareTo(Node other) { //For use with Priority Queues
		return Integer.compare(this.cost, other.getCost() );
	}//end compareTo
	
	@Override
	public boolean equals(Object obj) { //For use with Sets, Maps, & easier comparisons
		if(obj != null && this == obj) return true;
		if(this.getClass() != obj.getClass() ) return false;
		Node other = (Node) obj;
		return this.r == other.getR() && this.c == other.getC();
	}//end equals
	
	@Override
	public int hashCode() { //For use with Sets & Maps
		return 109 * this.r + this.c;
	}//end hashCode
	
	public String toString() {
		return "Pos: (" + this.r + ", " + this.c + ") Cost: " + this.cost + " Dir: " + dir;
	}//end toString
	
}//end Node

public class Solution {
	//Char symbols for start and end nodes
	private static final char STARTSYM = 'S', ENDSYM = 'E';
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<StringBuilder> maze = new ArrayList<>();
		Node start, end;
		int minCost;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read maze from file
		readFile(fileIn, maze);
		
		//Find starting node in maze
		start = findStart(maze);
		
		//Find ending node in maze
		end = findEnd(maze);
		
		//Find the shortest cost path to the ending node
		minCost = findShortestPath(maze, start, end);
		
		System.out.println(minCost);

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
	
	public static void readFile(Scanner fin, ArrayList<StringBuilder> maze) {
		
		while(fin.hasNextLine() ) {
			maze.add( new StringBuilder( fin.nextLine() ) );
		}//end while
		
	}//end readFile
	
	public static Node findStart(ArrayList<StringBuilder> maze) {
		
		for(int i = 0; i < maze.size(); i++) {
			for(int j = 0; j < maze.get(0).length(); j++) {
				
				if ( maze.get(i).charAt(j) == STARTSYM ) {
					return new Node(i, j, 0, DIR.RIGHT);
				}//end if
				
			}//end for
		}//end for
		
		return null;
	}//end findStart
	
	public static Node findEnd(ArrayList<StringBuilder> maze) {
		
		for(int i = 0; i < maze.size(); i++) {
			for(int j = 0; j < maze.get(0).length(); j++) {
				
				if ( maze.get(i).charAt(j) == ENDSYM ) {
					return new Node(i, j, Integer.MAX_VALUE, null);
				}//end if
				
			}//end for
		}//end for
		
		return null;
	}//end findEnd
	
	public static int findShortestPath(ArrayList<StringBuilder> maze, Node start, Node end) {
		Map<String, Node> nodes = new HashMap<>();
		PriorityQueue<Node> pq = new PriorityQueue<>();
		
		//Construct map keys for start and end nodes
		String sKey = new StringBuilder().append(start.getR()).append(",")
																		 .append(start.getC()).toString();
		String eKey = new StringBuilder().append(end.getR()).append(",")
																		 .append(end.getC()).toString();
		
		//Add start and end nodes to nodes map
		nodes.put(sKey, start);
		nodes.put(eKey, end);
		
		//Add start and end nodes to priority queue
		pq.add(start);
		pq.add(end);
		
		//Continue until end node is next cheapest node to be visited
		while( pq.peek() != end ) {
			Node next = pq.poll();
			
			//Mark as visited
			markVisited(maze, next);
			
			// DEBUG / ANIMATION
			// System.out.println( String.format("Visited: (%d,%d)", next.getR(), next.getC()) );
			// maze.forEach(i -> System.out.println(i+"\n") );
			
			//Get all neighbors
			ArrayList<Node> neighbors = getNeighbors(maze, next);
			
			//Process each neighbor
			for(Node neighbor : neighbors) {
				
				//Skip if already visited
				if( isVisited(maze, neighbor) ) continue; 
				
				//Construct key from coordinates
				String key = new StringBuilder().append(neighbor.getR()).append(",")
																				.append(neighbor.getC()).toString();
				
				if( nodes.containsKey(key) ) { //Update cost and dir if applicable
					nodes.get(key).update(neighbor);
				} else { //Insert into nodes map and priority queue
					nodes.put(key, neighbor);
					pq.add(neighbor);
				}//end if
				
			}//end for
			
		}//end while
		
		return pq.peek().getCost(); //Return cost to get to End Node
	}//end findShortestPath
	
	public static void markVisited(ArrayList<StringBuilder> maze, Node curr) {
		char ch;
		
		//Get appropriate arrow based on node's direction
		switch(curr.getDir() ) {
		case UP:
			ch = '^';
			break;
		case LEFT:
			ch = '<';
			break;
		case RIGHT:
			ch = '>';
			break;
		case DOWN:
			ch = 'v';
			break;
		default:
			ch = '.';
			break;
		}//end switch
		
		//Change node in maze to represent directional arrow
		maze.get( curr.getR() ).setCharAt( curr.getC(), ch );
	}//end markVisited
	
	public static boolean isVisited(ArrayList<StringBuilder> maze, Node curr) {
		char ch = maze.get( curr.getR() ).charAt( curr.getC() );
		return ch == '>' || ch == '<' || ch == '^' || ch == 'v';
	}//end isVisited
	
	public static ArrayList<Node> getNeighbors(ArrayList<StringBuilder> maze, Node curr) {
		ArrayList<Node> neighbors = new ArrayList<>(); //List of curr's neighbors
		int r = curr.getR(), c = curr.getC();          //Curr's position coords
		DIR[] cardinal = DIR.values();
		
		//Check for neighbors in all 4 cardinal directions
		for(int i = 0; i < cardinal.length; i++) {
			DIR nd = cardinal[i];                           //Current direction
			char nCh = maze.get(r + nd.r).charAt(c + nd.c); //Neighbor elt
			
			//If neighbor is unvisited, construct new Node and add to neighbors list
			if( nCh == '.' || nCh == 'E') {
				int nr = r + nd.r, nc = c + nd.c;
				int nbrCost = calcCost(curr, nd);
				neighbors.add( new Node(nr, nc, nbrCost, nd) );
			}//end if
		}//end for
		
		return neighbors;
	}//end getNeighbors
	
	public static int calcCost(Node curr, DIR nbrDir) {
		//Current host direction
		DIR hostDir = curr.getDir();
		//Direct distance between DIR enum values 
		int direct = Math.abs( hostDir.ordinal() - nbrDir.ordinal() );
		//Wraparound distance between DIR enum values (rotate CW or CCW)
		int wrap = DIR.values().length - direct;
		//Total 90 deg. rotations to get to neighbor
		int diff = Math.min(direct, wrap);
		
		//1000 for each rotation, 1 for each step
		return curr.getCost() + 1000 * diff + 1;
	}//end calcCost
	
	
}//end Solution