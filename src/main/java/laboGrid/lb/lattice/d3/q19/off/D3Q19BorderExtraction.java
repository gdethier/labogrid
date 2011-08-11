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
package laboGrid.lb.lattice.d3.q19.off;

public interface D3Q19BorderExtraction {

	void extractXZPlane(int link, int y, double[] plane);
	void extractYZPlane(int link, int x, double[] plane);
	void extractXYPlane(int link, int z, double[] plane);
	void extractXLine(int link, int y, int z, double[] line);
	void extractYLine(int link, int x, int z, double[] line);
	void extractZLine(int link, int x, int y, double[] line);

}
