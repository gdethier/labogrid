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
/**
 * Contains classes implementing LB collision operators
 * compatible with D3Q19 lattices.
 * LaBoGrid includes an
 * {@link laboGrid.lb.collision.d3.q19.D3Q19SRTCollider implementation}
 * of the BGK operator and the
 * {@link laboGrid.lb.collision.d3.q19.D3Q19MRTCollider implementation}
 * of a multiple relaxation times operator. In both implementations,
 * the {@link laboGrid.lb.collision.d3.q19.D3Q19FullWayBounceBack full-way bounce back}
 * is used for fluid-solid interface sites.
 * <p>
 * These implementations have also been adapted to include the Smagorinsky model
 * for large eddy simulations (D3Q19*SmagoCollider classes).
 * <p>
 * Finally, in order to ensure a better data locality during collision with special
 * representations of D3Q19 lattices, so-called "blocked" implementations
 * of above collision operators are proposed (D3Q19*BlockCollider classes).
 * 
 * @see laboGrid.lb.lattice.d3.q19
 */
package laboGrid.lb.collision.d3.q19;
