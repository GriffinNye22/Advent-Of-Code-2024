import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Integer> list1 = new ArrayList<>();
		ArrayList<Integer> list2 = new ArrayList<>();
		int totalDistance;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, list1, list2);
		
		Collections.sort(list1);
		Collections.sort(list2);
		
		totalDistance = calcDistance(list1, list2);
		
		System.out.println(totalDistance);
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
	
	public static void readFile(Scanner fin, ArrayList<Integer> l1, ArrayList<Integer> l2) {
		
		while(fin.hasNextLine() ) {
			l1.add( fin.nextInt() );
			l2.add( fin.nextInt() );
		}//end while
		
	}//end readFile
	
	public static int calcDistance(ArrayList<Integer> l1, ArrayList<Integer> l2) {
		int dist = 0;
		
		for(int i = 0; i < l1.size(); i++) {
			int diff = Math.abs( l2.get(i) - l1.get(i) );
			dist += diff;
		}//end for
		
		return dist;
	}//end calcDistance
	
}//end Solution