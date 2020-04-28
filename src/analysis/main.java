package analysis;

import java.math.BigInteger;
import java.util.Random;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solver sol = new Solver();
		
		int length_lower_bound = 100; //start at 4 bit numbers
		int length_upper_bound = 3000; //stop at numbers of 100 bit numbers
		int samples = 10; //for each algorithm, for each length, take 100 samples of running time
		//OUTPUT: n,gradeschool_avg,karatsuba_avg,toom3_avg,builtin_avg
		System.out.println("n,gradeschool_avg,karatsuba_avg,toom3_avg,builtin_avg");
		for(int i = length_lower_bound; i <= length_upper_bound; i=i+10) {
			//loop through lengths
			System.out.print(i);
			long curr_time = 0;
			long after_time = 0;
			
			int gradeschool_total_time = 0;
			int karatsuba_total_time = 0;
			int toom3_total_time = 0;
			int builtin_total_time = 0;
			for(int j = 0; j<samples;j=j+1) {
				BigInteger p = new BigInteger(i, 15, new Random());
				BigInteger q = new BigInteger(i, 15, new Random());
				sol.setInputs(p,q);
				curr_time = System.nanoTime();
				sol.gradeschool();
				after_time = System.nanoTime();
				gradeschool_total_time += after_time-curr_time;
				
				curr_time = System.nanoTime();
				sol.karatsuba();
				after_time = System.nanoTime();
				karatsuba_total_time += after_time-curr_time;
				
				curr_time = System.nanoTime();
				sol.toom3();
				after_time = System.nanoTime();
				toom3_total_time += after_time-curr_time;
				
				curr_time = System.nanoTime();
				sol.multiply();
				after_time = System.nanoTime();
				builtin_total_time += after_time-curr_time;
			}
			int gradeschool_average = gradeschool_total_time/samples;
			System.out.print("," + gradeschool_average );
			
			int karatsuba_average = karatsuba_total_time/samples;
			System.out.print("," + karatsuba_average );
			
			int toom3_average = toom3_total_time/samples;
			System.out.print("," + toom3_average );
			
			int builtin_average = builtin_total_time/samples;
			System.out.print("," + builtin_average );
			
			System.out.print("\n");
		}
		
		
		//SINGLE ITERATION
		
		//System.out.println("Input A: " + sol.binary_Representation(sol.get_A()));
		//System.out.println("Input B: " + sol.binary_Representation(sol.get_B()));
		
		//long curr_time = System.nanoTime();
		//System.out.println("Gradeschool Result: " + sol.gradeschool());
		//long after_time = System.nanoTime();
		//System.out.println("Gradeschool Runtime: " + (after_time-curr_time) + "\n");
		
		//curr_time = System.nanoTime();
		//System.out.println("Karatsuba Result: " + sol.karatsuba());
		//after_time = System.nanoTime();
		//System.out.println("Karatusba Runtime: " + (after_time-curr_time) + "\n");
		
		//curr_time = System.nanoTime();
		//System.out.println("Toom3 Result: " + sol.toom3());
		//after_time = System.nanoTime();
		//System.out.println("Toom3 Runtime: " + (after_time-curr_time) + "\n");
		
		//curr_time = System.nanoTime();
		//System.out.println("Built-In Result: " + sol.multiply());
		//after_time = System.nanoTime();
		//System.out.println("Built in Runtime: " + (after_time-curr_time) + "\n");
		
	}
	
	
}
