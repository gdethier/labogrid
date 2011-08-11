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

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;
import laboGrid.math.LinearAlgebra;

public class D3Q19MRTCollider extends D3Q19Collider {
	
	/**
	 * MRT matrices (warning : they correspond to the D'Humière velocities notation)
	 */
	protected final double[][] M = {
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{-30,-11,-11,-11,-11,-11,-11,8,8,8,8,8,8,8,8,8,8,8,8},
			{12,-4,-4,-4,-4,-4,-4,1,1,1,1,1,1,1,1,1,1,1,1},
			{0,1,-1,0,0,0,0,1,-1,1,-1,1,-1,1,-1,0,0,0,0},
			{0,-4,4,0,0,0,0,1,-1,1,-1,1,-1,1,-1,0,0,0,0},
			{0,0,0,1,-1,0,0,1,1,-1,-1,0,0,0,0,1,-1,1,-1},
			{0,0,0,-4,4,0,0,1,1,-1,-1,0,0,0,0,1,-1,1,-1},
			{0,0,0,0,0,1,-1,0,0,0,0,1,1,-1,-1,1,1,-1,-1},
			{0,0,0,0,0,-4,4,0,0,0,0,1,1,-1,-1,1,1,-1,-1},
			{0,2,2,-1,-1,-1,-1,1,1,1,1,1,1,1,1,-2,-2,-2,-2},
			{0,-4,-4,2,2,2,2,1,1,1,1,1,1,1,1,-2,-2,-2,-2},
			{0,0,0,1,1,-1,-1,1,1,1,1,-1,-1,-1,-1,0,0,0,0},
			{0,0,0,-2,-2,2,2,1,1,1,1,-1,-1,-1,-1,0,0,0,0},
			{0,0,0,0,0,0,0,1,-1,-1,1,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,-1,-1,1},
			{0,0,0,0,0,0,0,0,0,0,0,1,-1,-1,1,0,0,0,0},
			{0,0,0,0,0,0,0,1,-1,1,-1,-1,1,-1,1,0,0,0,0},
			{0,0,0,0,0,0,0,-1,-1,1,1,0,0,0,0,1,-1,1,-1},
			{0,0,0,0,0,0,0,0,0,0,0,1,1,-1,-1,-1,-1,1,1}};

	protected double[] omegaV = {0., 1.19, 1.4, 0., 1.2, 0., 1.2, 0., 1.2, Double.NaN, 1.4, Double.NaN, 1.4, Double.NaN, Double.NaN, Double.NaN, 1.98, 1.98, 1.98};
	protected double[][] im;
	protected double[][] d;
	protected double[][] s;
	protected double[][] ims;
	protected double[][] imsm;
	
	protected double omega;
	protected double xAccel;
	protected double yAccel;
	protected double zAccel;
	
	protected D3Q19FullWayBounceBack bounceBack;
	
	// Variables
	protected double[] mEq = new double[19]; // density at equilibrum
	protected double[] tmp1 = new double[19], tmp2 = new double[19];
	protected double dloc, jux, juy, juz, uSqrd;
	
	
	public D3Q19MRTCollider() {
		super();
		bounceBack = new D3Q19FullWayBounceBack();
	}
	

	protected void computeMRTMatrices(double omega) {
		omegaV[9] = omega;
		omegaV[11] = omega;
		omegaV[13] = omega;
		omegaV[14] = omega;
		omegaV[15] = omega;
		
		im = new double[19][19];
		for(int i = 0; i < 19; ++i)
			im[i] = new double[19];
		LinearAlgebra.transpose(im, M); // im = M^t

		d = new double[19][19];
		for(int i = 0; i < 19; ++i)
			d[i] = new double[19];
		LinearAlgebra.mult(d, M, im);
		for(int i = 0; i < 19; ++i) {
			d[i][i] = 1./d[i][i];
		}

		LinearAlgebra.inPlaceSquareMatMult(im, d); // im = im * d

		s = new double[19][19];
		for(int i = 0; i < 19; ++i)
			s[i] = new double[19];
		for(int i = 0; i < 19; ++i) {
			s[i][i] = omegaV[i];
		}
		
		ims = new double[19][19];
		for(int i = 0; i < 19; ++i)
			ims[i] = new double[19];
		LinearAlgebra.mult(ims, im, s); // ims = im * s

		imsm = new double[19][19];
		for(int i = 0; i < 19; ++i)
			imsm[i] = new double[19];
		LinearAlgebra.mult(imsm, ims, M); // imsm = ims * M
	}
	

	public void setParameters(String[] params) throws LBException {
		omega = Float.parseFloat(params[0]);
		xAccel = Float.parseFloat(params[1]);
		yAccel = Float.parseFloat(params[2]);
		zAccel = Float.parseFloat(params[3]);
		
		computeMRTMatrices(omega);
	}
	
	public void setParameters(double omega, double xAccel, double yAccel, double zAccel) {
		this.omega = omega;
		this.xAccel = xAccel;
		this.yAccel = yAccel;
		this.zAccel = zAccel;
		
		computeMRTMatrices(omega);		
	}
	
	@Override
	protected void collide(boolean isFluid) {
		
		if(isFluid) {

			dloc = getLocalDensity(site);

			jux = site[D3Q19LatticeDescriptor.EAST] + 
			site[D3Q19LatticeDescriptor.NORTHEAST] + 
			site[D3Q19LatticeDescriptor.SOUTHEAST] + 
			site[D3Q19LatticeDescriptor.UPEAST] + 
			site[D3Q19LatticeDescriptor.DOWNEAST] -
			site[D3Q19LatticeDescriptor.WEST] -
			site[D3Q19LatticeDescriptor.NORTHWEST] -
			site[D3Q19LatticeDescriptor.SOUTHWEST] -
			site[D3Q19LatticeDescriptor.UPWEST] -
			site[D3Q19LatticeDescriptor.DOWNWEST]+xAccel;

			juy = site[D3Q19LatticeDescriptor.NORTHEAST] +
			site[D3Q19LatticeDescriptor.NORTH] +
			site[D3Q19LatticeDescriptor.NORTHWEST] +
			site[D3Q19LatticeDescriptor.UPNORTH] +
			site[D3Q19LatticeDescriptor.DOWNNORTH] -
			site[D3Q19LatticeDescriptor.SOUTHWEST] -
			site[D3Q19LatticeDescriptor.SOUTH] -
			site[D3Q19LatticeDescriptor.SOUTHEAST] -
			site[D3Q19LatticeDescriptor.UPSOUTH] -
			site[D3Q19LatticeDescriptor.DOWNSOUTH]+yAccel;

			juz = site[D3Q19LatticeDescriptor.UP] +
			site[D3Q19LatticeDescriptor.UPEAST] +
			site[D3Q19LatticeDescriptor.UPNORTH] +
			site[D3Q19LatticeDescriptor.UPWEST] +
			site[D3Q19LatticeDescriptor.UPSOUTH] -
			site[D3Q19LatticeDescriptor.DOWNEAST] -
			site[D3Q19LatticeDescriptor.DOWNNORTH] -
			site[D3Q19LatticeDescriptor.DOWNWEST] -
			site[D3Q19LatticeDescriptor.DOWNSOUTH] -
			site[D3Q19LatticeDescriptor.DOWN]+zAccel;

			uSqrd = jux*jux+juy*juy+juz*juz;  
			mEq[0] = dloc;
			mEq[1] = -11.0*dloc + 19.0*uSqrd;
			mEq[2] = 3*dloc-5.5*uSqrd;
			mEq[3] = jux;
			mEq[4] = -2.0/3.0*jux;
			mEq[5] = juy;
			mEq[6] = -2.0/3.0*juy;
			mEq[7] = juz;
			mEq[8] = -2.0/3.0*juz;
			mEq[9] = 2.0*jux*jux-juy*juy-juz*juz;
			mEq[10] = 0.0;
			mEq[11] = juy*juy-juz*juz;
			mEq[12] = 0.0;
			mEq[13] = jux*juy;
			mEq[14] = juy*juz;
			mEq[15] = jux*juz;
			mEq[16] = 0.0;
			mEq[17] = 0.0;
			mEq[18] = 0.0;

			LinearAlgebra.mult(tmp1, imsm, site);
			LinearAlgebra.mult(tmp2, ims, mEq);
			LinearAlgebra.inPlaceSub(site, tmp1);
			LinearAlgebra.inPlaceAdd(site, tmp2);

		} else { // Bounce back

			bounceBack.apply(site);

		}
	}

}
