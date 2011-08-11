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

public class D3Q19MRTSmagoCollider extends D3Q19MRTCollider {
	
	// Parameter
	protected D3Q19Smagorinsky smago;
	
	// Variable
	protected double omegax;
	protected double[] d;

	public void setParameters(String[] params) throws LBException {
		
		if(params.length != 5)
			throw new LBException("Wrong number of parameters: "+params.length);

		double omega = Double.parseDouble(params[0]);
		double xAccel = Double.parseDouble(params[1]);
		double yAccel = Double.parseDouble(params[2]);
		double zAccel = Double.parseDouble(params[3]);
		double csmago = Double.parseDouble(params[4]);

		setParameters(omega, csmago, xAccel, yAccel, zAccel);
	}
	
	public void setParameters(double omega, double csmago, double xAccel, double yAccel, double zAccel) {
		super.setParameters(omega, xAccel, yAccel, zAccel);
		smago = new D3Q19Smagorinsky(csmago, omega);
	}
	
	protected void collide(boolean fluid) {
		
		if(fluid) {

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

			// Update imsm and ims
			LinearAlgebra.mult(tmp1, im, mEq);
			LinearAlgebra.sub(d, site, tmp1);
			omegax = smago.getMRTCoeff(dloc, d);
			s[9][9] = omegax;
			s[11][11] = omegax;
			s[13][13] = omegax;
			s[14][14] = omegax;
			s[15][15] = omegax;

			LinearAlgebra.mult(ims, im, s);
			LinearAlgebra.mult(imsm, ims, M);
			
			LinearAlgebra.mult(tmp1, imsm, site);
			LinearAlgebra.mult(tmp2, ims, mEq);
			LinearAlgebra.inPlaceSub(site, tmp1);
			LinearAlgebra.inPlaceAdd(site, tmp2);

		} else { // Bounce back

			bounceBack.apply(site);

		}
		
	}
	
}
