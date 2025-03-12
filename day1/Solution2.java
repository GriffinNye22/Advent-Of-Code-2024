import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class Solution2 {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Integer> list1 = new ArrayList<>();
		Map<Integer, Integer> list2 = new HashMap<>();
		int similarityScore;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, list1, list2);
		
		Collections.sort(list1);
		
		similarityScore = calcSimilarityScore(list1, list2);
		
		System.out.println(similarityScore);
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
	
	public static void readFile(Scanner fin, ArrayList<Integer> l1, Map<Integer,Integer> l2) {
		
		while(fin.hasNextLine() ) {
			int int1 = fin.nextInt();
			int int2 = fin.nextInt();
			
			l1.add(int1);
			
			if (l2.containsKey(int2) ) {
				l2.replace(int2, l2.get(int2) + 1);
			} else {
				l2.put(int2, 1);
			}//end if
			
		}//end while
		
	}//end readFile
	
	public static int calcSimilarityScore(ArrayList<Integer> l1, Map<Integer, Integer> l2) {
		int sim = 0;
		
		for(int i = 0; i < l1.size(); i++) {
			int curr = l1.get(i);
			
			if (l2.containsKey( curr ) ) {
				sim += curr * l2.get(curr);
			}//end if
			
		}//end for
		
		return sim;
	}//end calcSimilarityScore
	
}//end Solution