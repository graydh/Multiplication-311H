package analysis;


import java.math.BigInteger;

public class Solver {
	BigInteger A  = null;
	BigInteger B = null;
	
	boolean DEBUG = false;
	
	// CONSTRUCTOR
	public Solver() {
		A = new BigInteger("0");
		B = new BigInteger("0");
	}
	
	// CONSTRUCTOR
	public Solver(String inputA, String inputB){
		this.setInputs(new BigInteger(inputA), new BigInteger(inputB));
	}
	
	/**
	 * @param inputA - string of first number to multiply
	 * @param inputB - string of second number to multiply
	 */
	public void setInputs(BigInteger inputA, BigInteger inputB) {
		//Ensure A is the larger BigInteger
		if(inputA.compareTo(inputB) < 0) {
			BigInteger temp = A;
			A = B;
			B = temp;	
		}
	}
	
	/**
	 * @return - String decimal representation of the two inputs multiplied together
	 * Multiplies using standard BigInteger Library Multiplication (Optimal)
	 */
	public String multiply() {
		BigInteger result = A.multiply(B);
		return result.toString();
	}
	
	public String gradeschool() {
		return binary_Representation(gradeschool_helper(A,B));
	}
	
	public BigInteger gradeschool_helper(BigInteger X,BigInteger Y) {
		//Iterate through bits in B, multiplying each bit by A*10^i and adding to result
		int bits = Y.bitLength();
		BigInteger curr = new BigInteger(Y.toString());
		BigInteger result = new BigInteger("0");
		
		for (int i = 0; i < bits; i++) {
			int lowest_bit = curr.getLowestSetBit();
			if (lowest_bit == 0) {
				result = result.add(X.shiftLeft(i));
			}
			curr = curr.shiftRight(1);
		}
		return result;
		
	}
	
	public String karatsuba() {
		return binary_Representation(karatsuba_helper(A,B));
	}
	private BigInteger karatsuba_helper(BigInteger X, BigInteger Y) {
		//divide each A, B into A1, A0, B0, B1 (0 is the lower order bits)
		int len_a = X.bitLength();
		int len_b = Y.bitLength();
		
		//If either is 0, return 0
		if(len_a ==0 | len_b == 0) {
			return new BigInteger("0");
		}
		
		int n = Math.max(len_a, len_b);
		int m = ( n + 1 )/ 2; //rounded up n/2
		
		//If either is 1 return the others value
		if (len_a == 1) {
			return Y;
		}
		if(len_b == 1) {
			return X;
		}
		
		BigInteger A1 = X.shiftRight(m);
		BigInteger B1 = Y.shiftRight(m);
		BigInteger A0 = X.subtract(A1.shiftLeft(m));
		BigInteger B0 = Y.subtract(B1.shiftLeft(m));
		// where A0 and B0 are same size, and that size is half length of A, rounded up
		
		//compute part_A = (A1+A0)*(B1+B0) recursively
		BigInteger part_A = karatsuba_helper(A1.add(A0), B1.add(B0));
		
		//compute part_B = A1*B1 recursively
		BigInteger part_B = karatsuba_helper(A1, B1);
		
		//compute part_C = A0*B0 recursively
		BigInteger part_C = karatsuba_helper(A0, B0);
		
		//return B*2^(size of A) + 2^(size of A0)*(part_A - part_B - part_C) +  part_C
		return part_B.shiftLeft(2*m).add(part_A.subtract(part_B).subtract(part_C).shiftLeft(m)).add(part_C);
	}
	
	
	public String toom3() {
		return binary_Representation(toom3_helper(A,B));
	}
	
	public BigInteger toom3_helper(BigInteger X, BigInteger Y) {
		int X_len = X.bitLength();
		int Y_len = Y.bitLength();
		
		//If either of our values is smaller than 8 (fewer than 4 bits) - use built in multiply
		if(X.compareTo(new BigInteger("8")) < 0 || Y.compareTo(new BigInteger("8")) < 0) {
			return X.multiply(Y); //cheating here because none of my algorithms support negative numbers, but only when less than 8
		}
		
		int bit_length = Math.max(X_len, Y_len);
		int piece_length = (bit_length + 2) / 3; //highest order bits will always be shortest/equal
		
		// X = [X2 - X1 - X0] In bits
		BigInteger X2 = X.shiftRight(2*piece_length);
		BigInteger X1 = X.subtract(X2.shiftLeft(2*piece_length)).shiftRight(piece_length);
		BigInteger X0 = X.subtract(X1.shiftLeft(piece_length)).subtract(X2.shiftLeft(2*piece_length));
		
		
		BigInteger Y2 = Y.shiftRight(2*piece_length);
		BigInteger Y1 = Y.subtract(Y2.shiftLeft(2*piece_length)).shiftRight(piece_length);
		BigInteger Y0 = Y.subtract(Y1.shiftLeft(piece_length)).subtract(Y2.shiftLeft(2*piece_length));
		
		//Define function W(t) = X(t)*Y(t) = e*t^4 + d*t^3 + c*t^2 + b*t + a
		//These are values of W(t)
		BigInteger W_t_0 = toom3_helper(X0, Y0); //a
		BigInteger W_t_1 = toom3_helper(X0.add(X1).add(X2), Y0.add(Y1).add(Y2));
		BigInteger W_t_neg1 = toom3_helper(X2.subtract(X1).add(X0), Y2.subtract(Y1).add(Y0));
		BigInteger W_t_2 = toom3_helper(X2.shiftLeft(2).add(X1.shiftLeft(1)).add(X0), Y2.shiftLeft(2).add(Y1.shiftLeft(1)).add(Y0));
		BigInteger W_t_inf = toom3_helper(X2,Y2); //e
		
		
		BigInteger a = W_t_0;
		BigInteger c = W_t_1.subtract(W_t_0).subtract(W_t_inf).add(W_t_neg1.subtract(W_t_0).subtract(W_t_inf)).add(new BigInteger("1")).shiftRight(1);
		BigInteger e = W_t_inf;
		
		BigInteger temp3 = W_t_neg1.subtract(W_t_0).subtract(W_t_inf);
		BigInteger temp4 = W_t_2.subtract(W_t_0).subtract(W_t_inf.shiftLeft(4)); //equals 8d+4c+2b
		
		temp3 = temp3.subtract(c); //temp 3 now equals -d+-b
		temp4 = temp4.subtract(c.shiftLeft(2)); //temp4 now 8d+2b
		
		temp4 = temp4.add(temp3.shiftLeft(1)).add(new BigInteger("5")); //temp4 now 6d -- add 5 to round up on division
		BigInteger d = temp4.divide(new BigInteger("6")); //TODO - this is really not ideal, but division is tough
		BigInteger b = temp3.add(d).negate();
		
		if(DEBUG) {
			System.out.print("Bits: " + Integer.toString(X_len) + " --- X = " + binary_Representation(X) + " " + "Bits: " + Integer.toString(Y_len) + " --- Y = " + binary_Representation(Y) +"\n");
			System.out.println("Piece Length: " + Integer.toString(piece_length));
			System.out.println("X2 - " + binary_Representation(X2) + " X1 - " + binary_Representation(X1) + " X0 - " + binary_Representation(X0));
			System.out.println("Y2 - " + binary_Representation(Y2) + " Y1 - " + binary_Representation(Y1) + " Y0 - " + binary_Representation(Y0));
			System.out.println("t=0 " + binary_Representation(W_t_0) + " * t=1 " + binary_Representation(W_t_1) + " * t=-1 " + binary_Representation(W_t_neg1) + " * t=2 " + binary_Representation(W_t_2) + " * t=inf " + binary_Representation(W_t_inf) + " * ");
			System.out.println("a = " + binary_Representation(a) + " * " + "b = " + binary_Representation(b) + " * " + "c = " + binary_Representation(c) + " * "+ "d = " + binary_Representation(d) + " * " + "e = " + binary_Representation(e));
			System.out.println("");
		}
		
		//now we shift a,b,c,d,e and sum to get final value
		//a remains unchanged
		b = b.shiftLeft(piece_length);
		c = c.shiftLeft(2*piece_length);
		d = d.shiftLeft(3*piece_length);
		e = e.shiftLeft(4*piece_length);
		
		return a.add(b).add(c).add(d).add(e);
	}
	
	//HELPERS
	
	public BigInteger get_A() {
		return A;
	}
	
	public BigInteger get_B() {
		return B;
	}
	
	public String binary_Representation(BigInteger input) {
		byte[] binary_rep = input.toByteArray();
		//Bytes in Big-Endian order
		String result = input.toString() + " = ";
		for(int i = 0; i < binary_rep.length; i++) {
			byte curr = binary_rep[i];
			for(int j = 7; j>=0; --j) {
				result += this.getBit(curr,j);
			}
			//byte as string
			// String s1 = String.format("%8s", Integer.toBinaryString(curr & 0xFF)).replace(' ', '0');
		}
		
		return result;
	}
	
	public byte getBit(byte num, int position)
	{
	   return (byte) ((num >> position) & 1);
	}

}
