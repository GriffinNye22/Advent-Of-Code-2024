import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.LinkedHashMap;

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
    Map<Integer, Set<Integer> > pageMap = new HashMap<>();
    ArrayList<Integer[]> orderList = new ArrayList<>();
    Map<Integer, Integer> masterOrder = new LinkedHashMap<>();
    int result;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
    //Read in data from the file
		readFile(fileIn, pageMap, orderList);
    
    //Sum middle page numbers of all correct updates
    result = calcCorrectMiddlePageNumbers(pageMap, orderList);
 
    System.out.println(result);
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
	
	public static void readFile(Scanner fin, Map<Integer, Set<Integer> > pages, ArrayList<Integer[]> updates) {
		
		while(fin.hasNextLine() ) {
      int before, after;
      String[] text = fin.nextLine().split("\\|");
      
      if (text.length == 1 && text[0].equals("") ) break;
      
      before = Integer.valueOf( text[0] );
      after = Integer.valueOf( text[1] );
      
      if (!pages.containsKey(before) ) {
        Set<Integer> afterSet = new HashSet<>();
        afterSet.add(after);
        pages.put(before, afterSet);
      } else {
        pages.get(before).add(after);
      }//end if
     
      if(!pages.containsKey(after) ) {
        pages.put(after, new HashSet<Integer>() );
      }//end if
      
		}//end while
    
    while(fin.hasNextLine() ) {
      String[] line = fin.nextLine().split(",");
      //Converts the String array to an Integer array in one line
      updates.add( Arrays.stream(line).map(Integer::parseInt).toArray(Integer[]::new) );
    }//end while
   
	}//end readFile
  
  public static void topoSort(Map<Integer, Set<Integer> > pages, Map<Integer, Integer> master, Set<Integer> update) {
    Map<Integer, Integer> visited = new HashMap<>(); // -1 indicates unvisited, 0 indicates visiting, 1 indicates visited
    Deque<Integer> nodeOrderStack = new ArrayDeque<>();
    PriorityQueue<Integer> minNodes = new PriorityQueue<Integer>();
    int minIn = 0;
    int totVisited = 0;
    
    //Setup Visited Map
    setupTopoSort(update, visited);
    
    //Place all nodes from update with no neighbors in minNodes Queue
    fillMinNodesQueue(pages, minNodes, update, minIn);
    minIn++;
    
    //Process nodes with no neighbors
    while( minNodes.size() != 0) {
      totVisited += processNode(pages, visited, nodeOrderStack, update, minNodes.poll() );
    }//end while
    
    //Continue processing any hanging nodes 
    while( totVisited != update.size() ) {
      
      //Now that minNodes is empty, find next node(s) from update with min neighbors
      while( minNodes.size() == 0 ) {
        fillMinNodesQueue(pages, minNodes, update, minIn);
        minIn++;
      }//end while
      
      //Process new minimum neighbor nodes with Helper function
      while( minNodes.size() != 0 ) {
        totVisited += processNode(pages, visited, nodeOrderStack, update, minNodes.poll() );
      }//end while
      
    }//end while
    
    //Construct the master list from the topological sort stack
    constructMasterList(pages, master, nodeOrderStack);
    
  }//end topoSort
  
  public static int processNode(Map<Integer, Set<Integer> > pages, Map<Integer, Integer> visited, Deque<Integer> stack, Set<Integer> update, int vert) {
    int totVisited = 0;
    
    //Exit if node already visited or currently being visited (to alleviate cycles)
    if( visited.get(vert) != -1) {
      return 0;
    }//end if
    
    visited.put(vert, 0); //mark as currently visiting
      
    //Process all neighbors that are in the update list
    for(Integer neighbor : pages.get(vert) ) {
      if ( update.contains(neighbor) ) {
       totVisited += processNode(pages, visited, stack, update, neighbor);
      }//end if
    }//end for
      
    visited.put(vert, 1); //Mark current node as visited
    stack.push(vert); //Push current node onto the stack
    totVisited++; //Tally as visited
    
    return totVisited;
  }//end topoSortHelper
  
  public static void setupTopoSort(Set<Integer> update, Map<Integer, Integer> visit) {
    
    //Populate visited map
    for(Integer key : update ) {
      visit.put(key, -1);
    }//end for
    
  }//end setupVisited
  
  public static void fillMinNodesQueue(Map< Integer, Set<Integer> > pages, PriorityQueue<Integer> minNodes, Set<Integer> update, int minIn) {
    
    //Place all nodes in update list with out-degree of minIn into minNodes
    for(Integer node: update) {
      if ( pages.get(node).size() == minIn ) {
        minNodes.add( node);
      }//end if
    }//end for
    
  }//end fillMinNodesQueue
  
  public static void constructMasterList(Map<Integer, Set<Integer> > pages, Map<Integer, Integer> master, Deque<Integer> stack) {
    Set<Integer> independents = new HashSet<>(); //Stores nodes that are independent with same precedence
    int idx = 0;  //Represents precedence in the master order list
    
    //Retrieve first element and insert into master list
    int prev = stack.poll();
    master.put(prev, idx);
    
    //Insert nodes into master list until stack is empty
    while(! stack.isEmpty() ) {
      
      int curr = stack.poll(); //Get current node
      boolean isDependent = pages.get(prev).contains(curr); //Determine if node dependent on last
      
      if( isDependent ) { //If dependent on last, then increment precedence
        idx++;
      } else if (independents.size() == 0 ) { //If independent, add both nodes to independents list
        independents.add(prev);
        independents.add(curr);
      } else if (independents.size() > 0) { //If independent and multiple independents exist, check if dependent on any of these
        
        //Check if current node is dependent on any in the list
        for(Integer node: independents ) {
          if (pages.get(node).contains(curr) ) {
            isDependent = true;
            break;
          }//end if
        }//end for
        
        if(isDependent) { //If dependent on node in list, then clear list and increment precedence
          independents.clear();
          idx++;
        } else { //Otherwise add to independents list
          independents.add(curr);
        }//end if
        
      }//end if
      
      //Place current node into master list and update prev
      master.put(curr, idx);
      prev = curr;
   
    }//end while
    
  }//end constructMasterList
  
  public static int calcCorrectMiddlePageNumbers(Map<Integer, Set<Integer> > pages, ArrayList<Integer[]> updates) {
    int pageNumbers = 0;
    
    //Iterate through all updates
    for(int i = 0; i < updates.size(); i++) {
      Integer[] currUpdate = updates.get(i);
      Map<Integer, Integer> masterOrder = new HashMap<>(); //Map for sorted list of currUpdate nodes
      
      //Topologically sort nodes in the update according to the rules
      topoSort(pages, masterOrder, new HashSet<>(Arrays.asList(currUpdate) ) );
      
      //Add middle page numbers if valid update
      if (isValidUpdate(masterOrder, currUpdate) ) {
        pageNumbers += currUpdate[ currUpdate.length / 2 ];
      }//end if
    }//end for
    
    return pageNumbers;
  }//end calcCorrectMiddlePageNumbers
  
  public static boolean isValidUpdate(Map<Integer, Integer> master, Integer[] update) {
    int lastPrec = -1;
    
    for(int i = 0; i < update.length; i++) {
      int currPrec = master.get( update[i] );
      
      
      if (currPrec < lastPrec ) {
        return false;
      }//end if
      
      lastPrec = currPrec;
     
    }//end for
    
    return true;
  }//end isValidUpdate
	
}//end Solution