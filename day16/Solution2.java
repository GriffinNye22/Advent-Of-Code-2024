import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;

import java.util.TreeMap;

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
	private int r, c;                  //Position
	private Map<DIR, Integer> dirCost; //Direction to Cost Map
	private Map<Node, Integer> next;   //Map of neighbors to direct cost
    
	public Node(int _r, int _c, int _cost, DIR _d) {
		r = _r;
		c = _c;
		dirCost = new HashMap<DIR, Integer>();
    dirCost.put(_d, _cost);
    next = new HashMap<>();
	}//end constructor
	
	public int getR() { 
		return r;
	}//end getR
	
	public int getC() {
		return c;
	}//end getC
  
  public Set<Integer> getCost() {
    return new HashSet<>( dirCost.values() );
  }//end getCost
	
	public int getCost(DIR dir) {
		return dirCost.getOrDefault(dir, -1);
	}//end getCost
	
	public void setCost(DIR dir, int cost) {
		dirCost.put(dir, cost);
	}//end setCost
  
  public int getMinCost() {
    return dirCost.values().stream().min(Integer::compare).orElse(Integer.MAX_VALUE);
  }//end getMinCost
  
  public DIR getMinDir() {
    return dirCost.entrySet().stream().min(Map.Entry.comparingByValue() )
                  .map(Map.Entry::getKey).orElse(null);
  }//end getMinDir()
	
	public Set<DIR> getDirs() {
		return dirCost.keySet();
	}//end getDir
  
  //GetCurrentDirection()
  //Returns the direction this node is relative to other
  public DIR getCurrDir(Node other) {
    int rChange = this.r - other.getR() == 0 ? 0 : ( this.r - other.getR() ) / Math.abs(this.r - other.getR() );
    int cChange = this.c - other.getC() == 0 ? 0 : ( this.c - other.getC() ) / Math.abs(this.c - other.getC() );
    
    for(DIR dir : DIR.values() ) {
      if(dir.r == rChange && dir.c == cChange) {
        return dir;
      }//end if       
    }//end for
    
    return null;
  }//end getRelDir
	
  public Set<Map.Entry<Node, Integer>> getNeighbors() {
    return next.entrySet();
  }//end getNeighbors
   
  public void addNeighbor(Node other, int cost) {
    if (other == null) return;
    next.put(other, cost);
  }//end addNeighbor
  
	//Used for updating Node to shortest cost and direction
	public void update(Node other) {
		if(other == null) return;
    
    Set<DIR> otherDirs = other.getDirs();
    
    for(DIR nbrDir : otherDirs) {
      
      if(other.getCost(nbrDir) < this.dirCost.getOrDefault(nbrDir, Integer.MAX_VALUE) ) {
        this.dirCost.put(nbrDir, other.getCost(nbrDir) );
      }//end if
      
    }//end for
	
	}//end update
	
	@Override
	public int compareTo(Node other) { //For use with Priority Queues
		return Integer.compare( 
             this.dirCost.values().stream().min(Integer::compare).orElse(Integer.MAX_VALUE),
             other.dirCost.values().stream().min(Integer::compare).orElse(Integer.MAX_VALUE) );
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
	
  @Override
	public String toString() {
    StringBuilder str = new StringBuilder();
    
    str.append("Pos: (").append(this.r).append(", ").append(this.c).append(") ");
    str.append("Costs: ");
    
    for(Map.Entry<DIR,Integer> cost : dirCost.entrySet() ) {
      String key = cost.getKey() != null ? cost.getKey().name() : "null";
      str.append(key).append(": ").append(cost.getValue() ).append(" ");
    }//end for
    
		return str.toString();
	}//end toString
	
}//end Node

public class Solution2 {
	//Char symbols for start and end nodes
	private static final char STARTSYM = 'S', ENDSYM = 'E';
  private static final Set<Character> NODESYM = Set.of('.','E','>','<','^','v');
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<StringBuilder> maze = new ArrayList<>();
		Node start, end;
		int minCost;
    int numNodesInOptPath;
		
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
    
    //Count number of Nodes that are found in any of the shortest paths
    numNodesInOptPath = countShortestPathNodes(maze, start, end, minCost, 0);
		
		System.out.println("Min Cost: " + minCost);
    System.out.println("Num Nodes: " + numNodesInOptPath);
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
		
		//Continue until all nodes have been processed
		while( !pq.isEmpty() ) {
      System.out.println( String.format("Visited: (%d,%d)", pq.peek().getR(), pq.peek().getC()) );
      System.out.println( pq );
			Node next = pq.poll();
      
      //Skip processing end node neighbors
      if(next == end) continue; 
			
			//Mark as visited
			markVisited(maze, next);
			
			maze.forEach(i -> System.out.println(i+"\n") );
			
			//Get all neighbors
			ArrayList<Node> neighbors = getNeighbors(maze, next);
			
			//Process each neighbor
			for(Node neighbor : neighbors) {
        
        //Calculate cost solely for traversing from next to neighbor
        DIR currDir = neighbor.getCurrDir(next);
        int n2nCost = calcCost(next , currDir) - next.getCost(currDir);
        
        //Add neighbor to list of Node's neighbors
        next.addNeighbor(neighbor, n2nCost);
				
				// //Skip if already visited
				// if(  ) ; 
				
				//Construct key from coordinates
				String key = new StringBuilder().append(neighbor.getR()).append(",")
																				.append(neighbor.getC()).toString();
				
				if( isVisited(maze, neighbor) || nodes.containsKey(key) ) { //Update cost and dir if applicable
					nodes.get(key).update(neighbor);
				} else { //Insert into nodes map and priority queue
					nodes.put(key, neighbor);
					pq.add(neighbor);
				}//end if
				
			}//end for
			
		}//end while
    
    TreeMap<String, Node> sortedNodes = new TreeMap<>( (a,b) -> nodes.get(a).compareTo(nodes.get(b)) );
    sortedNodes.putAll(nodes);
    
    for (Map.Entry<String, Node> node : sortedNodes.entrySet() ) {
      Node curr = node.getValue();
      System.out.println("HOST NODE: " + curr);
      Set<Map.Entry<Node,Integer>> nbrs = curr.getNeighbors();
      System.out.println("NEIGHBORS: ");
      for(Map.Entry<Node,Integer> nbr : nbrs) {
        System.out.println("NODE: " + nbr.getKey() + " Cost: " + nbr.getValue() + " ");
      }//end for
      System.out.println();
    }//end for
    
		
		return end.getMinCost();
	}//end findShortestPath
	
	public static void markVisited(ArrayList<StringBuilder> maze, Node curr) {
		char ch;
		
		//Get appropriate arrow based on node's direction
		switch( curr.getMinDir() ) {
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
			
			//If any neighbors exist, construct new Node and add to neighbors list
      //Regardless of whether visited or not
			if( NODESYM.contains(nCh) ) {
				int nr = r + nd.r, nc = c + nd.c;
				int nbrCost = calcCost(curr, nd);
				neighbors.add( new Node(nr, nc, nbrCost, nd) );
			}//end if
		}//end for
		
		return neighbors;
	}//end getNeighbors
	
	public static int calcCost(Node curr, DIR nbrDir) {
    //Direct distance between DIR enum values 
    int direct = Math.abs( curr.getMinDir().ordinal() - nbrDir.ordinal() );
    //Wraparound distance between DIR enum values (rotate CW or CCW)
    int wrap = DIR.values().length - direct;
    //Total 90 deg. rotations to get to neighbor
    int diff = Math.min(direct, wrap);
		
		//1000 for each rotation, 1 for each step
		return curr.getCost( curr.getMinDir() ) + 1000 * diff + 1;
	}//end calcCost
  
  public static int countShortestPathNodes(ArrayList<StringBuilder> maze, Node start, 
                                            Node end, int minCost, int totalCost) {
    Set<Node> optimalNodes = new HashSet<>();
 
    isOptimalPath(maze, start, end, minCost, totalCost, optimalNodes);
    
    optimalNodes.forEach(i -> maze.get( i.getR() ).setCharAt( i.getC(), 'O') );
    System.out.println("OPTIMAL NODES");
    maze.forEach(i -> System.out.println(i) );
    
    return optimalNodes.size();
  }//end countShortestPathNodes
  
  public static boolean isOptimalPath(ArrayList<StringBuilder> maze, Node start, 
                                      Node end, int minCost, int totalCost, Set<Node> optNodes) {
    
    Set< Map.Entry<Node,Integer>> neighbors;
    boolean isOptimal = false;
    
    if (start.equals(end) && totalCost == minCost) {
      optNodes.add(start);
      return true;
    } else if ( start.equals(end) ) {
      return false;
    }//end if
    
    neighbors = start.getNeighbors();
    // System.out.println("NODE (" + start.getR() + "," + start.getC() + "): ");
    // neighbors.forEach(i -> System.out.println(i) );
    
    for(Map.Entry<Node, Integer> neighbor: neighbors) {
      
      //Recursively have neighbor check if it's part of optimal path
      boolean isNbrOptimal = isOptimalPath(maze, neighbor.getKey(), end, minCost, 
                                           totalCost + neighbor.getValue(), optNodes);
                          
      //If neighbor is part of the optimal path, then indicate this node is too
      if(isNbrOptimal) {
        optNodes.add(start);
        isOptimal = true;
      }//end if
  
    }//end for
    
    return isOptimal;
  }//end isOptimalPath
	
}//end Solution