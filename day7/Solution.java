import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.math.BigInteger;

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList< ArrayList<Long> > expressions = new ArrayList<>();
		ArrayList<Long> solutions = new ArrayList<>();
		BigInteger result = new BigInteger("0");
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
		
		//Read expressions from file into ArrayLists
		readFile(fileIn, expressions, solutions);
		
		//Check each expression, summing possible solutions
		for(int i = 0; i < expressions.size(); i++) {
			
			//Perform the brute force and sum solution if possible
			result = result.add( isPossible(expressions.get(i), solutions.get(i), 0, null) ? 
									           BigInteger.valueOf( solutions.get(i) ) : BigInteger.ZERO );
			
		}//end for
		
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
	
	public static void readFile(Scanner fin, ArrayList< ArrayList<Long> > exp, ArrayList<Long> sol) {
		
		while(fin.hasNextLine() ) {
			String[] line = fin.nextLine().split(":");
			sol.add( Long.valueOf(line[0]) );
			exp.add( new ArrayList<>( Arrays.stream( line[1].trim().split(" ") )
																			.map(Long::valueOf)
																			.collect(Collectors.toList()) ) );
		}//end while
			
	}//end readFile

	public static boolean isPossible(ArrayList<Long> exp, long goal, int idx, Set<Long> intermed) {
		Set<Long> nextVals = new HashSet<>(); //Set of possible values produced up to element idx
		
		//If first index, add first element and call for next element
		if (idx == 0) {
			nextVals.add( exp.get(idx) );
			return isPossible(exp, goal, idx+1, nextVals);
		}//end if
		
		//Calculate possible nextVals from previous nextVals
		for(Long value : intermed) {
			long add = value + exp.get(idx);
			long mul = value * exp.get(idx);
		
			//Add new nextVals to set
			nextVals.add(add);
			nextVals.add(mul);
		}//end for
		
		//If end of expression, check if we reached goal
		if(idx == exp.size() - 1) {
			return nextVals.contains(goal);
		}//end if
		
		//Call for next element
		return isPossible(exp, goal, idx+1, nextVals);
	}//end isPossible
	
	
	
}//end Solution