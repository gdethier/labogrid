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

import static org.junit.Assert.*;

/**
 * This class encompasses typical linear algebra operations on matrices and
 * vectors.
 * 
 * @author Gerard Dethier
 */
public class LinearAlgebra {
	/**
	 * Substraction of two vectors. The dimensions of given
	 * vectors must agree.
	 * 
	 * @param rop Destination vector
	 * @param op1 First operand
	 * @param op2 Second operand
	 */
	public static void sub(double[] rop, double[] op1, double[] op2) {
		for(int i = 0; i < rop.length; ++i)
			rop[i] = op1[i] - op2[i];
	}
	
	@org.junit.Test
	public void testSub() {
		double[] tmp = {1, 2, 3, 4, 5};
		double[] tmp2 = {1, 2, 3, 4, 5};
		double[] tmp3 = {1, 2, 3, 4, 5};

		sub(tmp, tmp2, tmp3);
		
		boolean isOk = true;
		for(int i = 0; i < tmp.length && isOk; ++i) {
			isOk = isOk && (tmp[i] == (tmp2[i] - tmp3[i]));
		}
		
		assertTrue(isOk);
	}

	/**
	 * In place substraction of two vectors. The dimensions of given
	 * vectors must agree.
	 * 
	 * @param rop Destination vector and first operand
	 * @param op Second operand
	 */
	public static void inPlaceSub(double[] rop, double[] op) {
		for(int i = 0; i < rop.length; ++i)
			rop[i] -= op[i];
	}
	
	@org.junit.Test
	public void testInPlaceSub() {
		double[] tmp2 = {1, 2, 3, 4, 5};
		double[] tmp3 = {1, 2, 3, 4, 5};

		inPlaceSub(tmp2, tmp3);
		
		boolean isOk = true;
		for(int i = 0; i < tmp2.length && isOk; ++i) {
			isOk = isOk && (tmp2[i] == 0);
		}
		
		assertTrue(isOk);
	}
	
	/**
	 * In place addition of two vectors. The dimensions of given
	 * vectors must agree.
	 * 
	 * @param rop Destination vector and first operand
	 * @param op Second operand
	 */
	public static void inPlaceAdd(double[] rop, double[] op) {
		for(int i = 0; i < rop.length; ++i)
			rop[i] += op[i];
	}
	
	@org.junit.Test
	public void testInPlaceAdd() {
		double[] tmp = {1, 2, 3, 4, 5};
		double[] tmp2 = {1, 2, 3, 4, 5};

		inPlaceAdd(tmp, tmp2);
		
		boolean isOk = true;
		for(int i = 0; i < tmp.length && isOk; ++i) {
			isOk = isOk && (tmp[i] == 2*tmp2[i]);
		}
		
		assertTrue(isOk);
	}

	/**
	 * Multiplication of a matrix (first operand) and a vector (second operand).
	 * The dimensions of given matrix and vectors must agree.
	 * 
	 * @param rop Destination vector
	 * @param op1 First operand
	 * @param op2 Second operand
	 */
	public static void mult(double[] rop, double[][] op1, double[] op2) {
		for(int i = 0; i < rop.length; ++i) {
			rop[i] = 0;
			for(int j = 0; j < op2.length; ++j) {
				rop[i] += op1[i][j] * op2[j];
			}
		}
	}
	
	@org.junit.Test
	public void testVectMult() {
		double[] tmp = {0, 0};
		double[][] tmp2 = {{1, 2, 3}, {1, 2, 3}};
		double[] tmp3 = {1, 2, 3};

		mult(tmp, tmp2, tmp3);
		
		assertTrue(tmp[0] == 14 && tmp[1] == 14);
	}
	
	/**
	 * Multiplication of two matrices. The dimensions of given
	 * matrices must agree.
	 * 
	 * @param rop Destination matrix
	 * @param op1 First operand
	 * @param op2 Second operand
	 */
	public static void mult(double[][] rop, double[][] op1, double[][] op2) {
		for(int i = 0; i < rop.length; ++i) {
			for(int j = 0; j < rop[i].length; ++j) {
				rop[i][j] = 0;
				for(int k = 0; k < op1[i].length; ++k) {
					rop[i][j] += op1[i][k] * op2[k][j];
				}
			}
		}
	}
	
	@org.junit.Test
	public void testMatMult() {
		double[][] tmp = {{0, 0}, {0, 0}};
		double[][] tmp2 = {{1, 2, 3}, {1, 2, 3}};
		double[][] tmp3 = {{1, 2}, {3, 4}, {5, 6}};

		mult(tmp, tmp2, tmp3);
		
		boolean isOk = true;
		isOk = isOk && (tmp[0][0] == 22 && tmp[1][0] == 22);
		assertTrue(isOk);
		
		isOk = isOk && (tmp[0][1] == 28 && tmp[1][1] == 28);
		
		assertTrue(isOk);
	}
	
	/**
	 * In place multiplication of two square matrices (result of the multiplication
	 * is stored into first given array). The dimensions of given
	 * matrices must agree (i.e. both matrices must be square and have same
	 * sizes).
	 * 
	 * @param rop Destination matrix and first operand
	 * @param op Second operand
	 */
	public static void inPlaceSquareMatMult(double[][] rop, double[][] op) {
		if(rop.length == 0)
			return;

		double[] tmp = new double[rop[0].length];
		for(int i = 0; i < rop.length; ++i) {
			// Row i is copied from rop to tmp so it can be overwritten
			System.arraycopy(rop[i], 0, tmp, 0, rop[i].length);
			
			// Each element of row i is computed
			for(int j = 0; j < rop[i].length; ++j) {
				rop[i][j] = 0;
				for(int k = 0; k < tmp.length; ++k) {
					rop[i][j] += tmp[k] * op[k][j];
				}
			}
		}
	}
	
	@org.junit.Test
	public void testInPlaceMatMult() {
		double[][] tmp = {{0, 0}, {0, 0}};
		double[][] tmp2 = {{1, 2}, {3, 4}};
		double[][] tmp3 = {{1, 2}, {3, 4}};

		mult(tmp, tmp2, tmp3);
		inPlaceSquareMatMult(tmp2, tmp3);
		
		for(int i = 0; i < 2; ++i)
			for(int j = 0; j < 2; ++j)
				assertTrue(tmp[i][j] == tmp2[i][j]);
	}

	/**
	 * Transposition of a matrix. The dimensions of given
	 * matrices must agree.
	 * 
	 * @param rop Destination matrix
	 * @param op The matrix to transpose
	 */
	public static void transpose(double[][] rop, double[][] op) {
		for(int i = 0; i < op.length; ++i) {
			for(int j = 0; j < op[i].length; ++j) {
				rop[i][j] = op[j][i];
			}
		}
	}
	
	@org.junit.Test
	public void testTranspose() {
		double[][] x = {{1, 2}, {3, 4}};
		double[][] y = {{1, 2}, {3, 4}};
		
		transpose(x, y);
		
		for(int i = 0; i < 2; ++i)
			for(int j = 0; j < 2; ++j)
				assertTrue(x[i][j] == y[j][i]);
	}
}
