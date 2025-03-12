import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;

public class Solution2 {
	
	private enum CMD {ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV}; 
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Integer> registers = new ArrayList<>();
		ArrayList<Integer> opCodes = new ArrayList<>();
		ArrayList<Integer> operands = new ArrayList<>();
		int regAValue;
		
		try {
			fileIn = openFile(FILENAME);
			readFile(fileIn, registers, opCodes, operands);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
    
    registers.set(0, 0);

		regAValue = uncorruptRegA(registers, opCodes, operands);
		
		System.out.println("Reg A: " + regAValue);

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
	
	public static void readFile(Scanner fin, ArrayList<Integer> reg, 
															ArrayList<Integer> opC, ArrayList<Integer> ops)
															throws NoSuchElementException, ArrayIndexOutOfBoundsException,
															NumberFormatException{
		
		try {
			//Read in register values
			for(int i = 0; i < 3; i++) {
				reg.add( Integer.valueOf( fin.nextLine().split(":")[1].trim() ) );
			}//end for
			
			//Skip blank line
			fin.nextLine();
			
			//Read in program instructions and operands
			String[] program = fin.nextLine().split(":")[1].trim().split(",");
			
			//Split opcodes and operands into respective lists
			for(int i = 0; i < program.length; i+=2) {
				opC.add( Integer.valueOf( program[i] ) );
				ops.add( Integer.valueOf( program[i+1] ) );
			}//end for
			
		} catch (NoSuchElementException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new NoSuchElementException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		} catch (ArrayIndexOutOfBoundsException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new ArrayIndexOutOfBoundsException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		} catch (NumberFormatException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new NumberFormatException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		}//end try-catch
		
	}//end readFile
	
	public static int uncorruptRegA(ArrayList<Integer> reg, ArrayList<Integer> opC, 
	                                    ArrayList<Integer> ops) {
																				
		int regA = reg.get(0); int regB = reg.get(1); int regC = reg.get(2);
		int iPtr = opC.size() - 1;
		StringBuilder output = new StringBuilder();
    int jumpPoint = iPtr;
		
		//Continue until end of program reached
		while(output.length() / 2 < opC.size() + ops.size() ) {
			
			//Get next command	
			int opCode = opC.get(iPtr);
			
			//Get next operand
			int arg = ops.get(iPtr);
			
			//Determine command type
			CMD cmd = convertToCMD(opCode);
			
			//Process command
			switch(cmd) {
				case ADV: //Divide regA by 2^Combo arg => regA
					arg = getComboOperandValue(arg, regA, regB, regC);
					regA *= Math.pow(2, arg);
					break;
				case BXL: //XOR regB and literal arg => regB
					regB = XOR(regB, arg);
					break;
				case BST: //Combo arg Mod 8 => regB
					// arg = getComboOperandValue(arg, regA, regB, regC);
					// regB = arg % 8; skip???
					break;
				case JNZ: //Jump instruction pointer to arg
					if (regA != 0) jumpPoint = iPtr; //COME BACK, HOW TO DEAL WITH non zero jumps?
					// iPtr = arg; //divided by 2???
					// break;
				case BXC: //XOR regB and regC => regC
					regB = XOR(regB, regC);
					break;
				case OUT: //Output Combo arg Mod 8
					arg = getComboOperandValue(arg, regA, regB, regC);
					output.insert(0, String.valueOf(arg % 8) + ",");
					break;
				case BDV: //Divide regA by 2^Combo arg => regB
					// arg = getComboOperandValue(arg, regA, regB, regC);
					// regB = regA * (int) Math.pow(2, arg); skip?
					break;
				case CDV: //Divide regA by 2^Combo arg => regC
					// arg = getComboOperandValue(arg, regA, regB, regC);
					// regC = regA * (int) Math.pow(2, arg); skip?
					break;
			}//end switch
			
			//Update instruction pointer
			// if(cmd != CMD.JNZ || (cmd == CMD.JNZ && regA == 0) ) {
				// iPtr ++;
			// }//end if
      
      iPtr--;
      if(iPtr < 0) iPtr = jumpPoint;
			
		}//end while
		
		//Debug
		System.out.println("Register A: " + regA);
		System.out.println("Register B: " + regB);
		System.out.println("Register C: " + regC);
    System.out.println("Program Output: " + output.deleteCharAt( output.length() - 1 ).toString() );
		
		//Remove extra comma and convert to string		
		return regA;										
	}//end executeProgram
	
	public static CMD convertToCMD(int opcode) {
		return CMD.values()[opcode];
	}//end converToCMD
	
	public static int getComboOperandValue(int arg, int regA, int regB, int regC) {
		
		switch(arg) {
			case 0:
			case 1:
			case 2:
			case 3:
				return arg;
			case 4:
				return regA;
			case 5:
				return regB;
			case 6:
				return regC;
			default:
				return Integer.MAX_VALUE;
		}//end switch
	}//end getComboOperandValue
	
	public static int XOR(int op1, int op2) {
		return op1 ^ op2;
	}//end XOR
	
	
	
}//end Solution