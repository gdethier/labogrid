/*
 * #%L
 * LaBoGrid
 * %%
 * Copyright (C) 2011 LaBoGrid Team
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package laboGrid.math;

import java.util.LinkedList;
import java.util.ListIterator;

public class IntegerVector {

	public static int[] parseIntegerVector(String str) throws VectorWrongFormatException {

		LinkedList<String> components = new LinkedList<String>();
		str = str.replace(" ","");
		
		if(str.charAt(0) != '(' || str.charAt(str.length()-1) != ')') {
			throw new VectorWrongFormatException("The format should be (x,y,...,z) : "+str);
		}
		
		int i = 1;
		String comp = "";
		while(i < str.length()-1) {
			comp += str.charAt(i);
			++i;
			
			if(i < str.length() && str.charAt(i) == ',') {
				components.add(comp);
				comp = "";
				++i;
			}
		}

		components.add(comp);
		
		int[] v = new int[components.size()];
		ListIterator<String> it = components.listIterator();
		
		i = 0;
		while(it.hasNext()) {
			String s = it.next();
			v[i] = Integer.parseInt(s);
			++i;
		}
		
		return v;
	}


//	PUBLIC INT GETDIMENSION() {
//		RETURN VECTOR.LENGTH;
//	}

//	public boolean isSmaller(int[] minPoint) {
//		boolean toReturn = true;
//		
//		for(int i = 0; i < vector.length && toReturn; ++i) {
//			toReturn |= (vector[i] < minPoint.vector[i]); 
//		}
//		return toReturn;
//	}

//	public boolean isGreater(int[] maxPoint) {
//		boolean toReturn = true;
//		for(int i = 0; i < vector.length && toReturn; ++i) {
//			toReturn |= (vector[i] > maxPoint.vector[i]); 
//		}
//		return toReturn;
//	}

//	public void modulo(int[] mod) {
//		for(int i = 0; i < vector.length; ++i) {
//			if(vector[i] < 0) {
//				int y = -vector[i]/mod.vector[i];
//				vector[i] = vector[i] + (y+1)*mod.vector[i];
//			}
//			vector[i] = vector[i] % mod.vector[i];
//		}
//	}

//	public float squareDistanceTo(int[] other) {
//		float distance = 0;
//		for(int i = 0; i < vector.length; ++i) {
//			distance += Math.pow(other.vector[i] - vector[i], 2);
//		}
//		return distance;
//	}

//	public float sqrNorm() {
//		float norm = 0;
//		for(int i = 0; i < vector.length; ++i) {
//			norm += Math.pow(vector[i], 2);
//		}
//		return norm;
//	}

	public static int[] add(int[] v1, int[] v2) {
		assert v1.length == v2.length;
		int[] toReturn = new int[v1.length];
		
		for(int i = 0; i < v1.length; ++i) {
			toReturn[i] = v1[i] +v2[i];
		}
		
		return toReturn;
	}

	public static double squareDistance(int[] v1, int[] v2) {
		assert v1.length == v2.length;
		float distance = 0;
		for(int i = 0; i < v1.length; ++i) {
			distance += Math.pow(v2[i] - v1[i], 2);
		}
		return distance;
	}

	public static int[] sub(int[] v1, int[] v2) {
		int[] toReturn = new int[v1.length];
		
		for(int i = 0; i < v1.length; ++i) {
			toReturn[i] = v1[i] - v2[i];
		}
		
		return toReturn;
	}

	public static boolean isZero(int[] v) {
		for(int i = 0; i < v.length; ++i) {
			if(v[i] != 0)
				return false;
		}
		return true;
	}

	public static int[] negate(int[] v) {
		int[] negated = new int[v.length];
		for(int i = 0; i < v.length; ++i)
			negated[i] = -v[i];
		return negated;
	}

	public static String toString(int[] v) {
		String str = "(";
		for(int i = 0; i < v.length-1; ++i) {
			str += v[i];
			str += ",";
		}
		str += v[v.length-1];
		str += ")";
		return str;
	}


	public static int mult(int[] v) {

		int mult = 1;
		
		for(int i = 0; i < v.length; ++i) {
			mult *= v[i];
		}
		
		return mult;
		
	}

}
