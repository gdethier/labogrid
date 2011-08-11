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
package laboGrid.lb.collision.d3.q19;

import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;

public class D3Q19Smagorinsky {
	
	private double csmago;
	private double omega;
	
	private double[] d = new double[19];
	private int[][] vels = new int[19][3];
	
	private double pi, q;
	private double tau, taux, t;


	public D3Q19Smagorinsky(double csmago, double omega) {
		this.csmago = csmago;
		this.omega = omega;
		
		D3Q19LatticeDescriptor desc = D3Q19LatticeDescriptor.getSingleton();
		for(int q = 0; q < 19; ++q) {
			int[] v = desc.getVector(q);
			System.arraycopy(v, 0, vels[q], 0, 3);
		}
	}
	
	public double getSRTCoeff(double dloc, double[] site, double[] distEq) {
		q = 0;
		for(int a = 0; a < 2; ++a) {
			for(int b = 0; b < 2; ++b) {
				pi = 0;
				for(int i = 0; i < 19; ++i) {
					pi = pi + d[i]*vels[i][a]*vels[i][b];
				}
				q = q + pi*pi;
			}
		}

		tau = 1. / omega;
		t = (tau*tau) + 18*Math.sqrt(2)*(csmago*csmago)*Math.sqrt(q) / dloc;
		taux = (tau + Math.sqrt(t)) / 2.;
		return 1. / taux;
	}

	public double getMRTCoeff(double dloc, double[] d) {
		q = 0;
		for(int a = 0; a < 2; ++a) {
			for(int b = 0; b < 2; ++b) {
				pi = 0;
				for(int i = 0; i < 19; ++i) {
					pi = pi + d[i]*vels[i][a]*vels[i][b];
				}
				q = q + pi*pi;
			}
		}

		tau = 1. / omega;
		t = (tau*tau) + 18*Math.sqrt(2)*(csmago*csmago)*Math.sqrt(q);
		taux = ((Math.sqrt(t) - tau) / 2.) + tau;
		return 1./taux;
	}
	
}
