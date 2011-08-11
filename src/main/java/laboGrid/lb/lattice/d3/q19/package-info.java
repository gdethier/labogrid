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
 * Contains classes implementing D3Q19 lattices.
 * LaBoGrid includes 3 implementations of D3Q19 lattices:
 * {@link laboGrid.lb.lattice.d3.q19.simple simple},
 * {@link laboGrid.lb.lattice.d3.q19.simple murphy} and
 * {@link laboGrid.lb.lattice.d3.q19.simple off}.
 * <p>
 * Simple implementation uses a widely used data layout based on
 * arrays of arrays. Murphy implementation is based on a solution proposed
 * by <a href="http://www2.epcc.ed.ac.uk/msc/dissertations/dissertations-0405/0762240-9j-dissertation1.1.pdf">Sean Murphy (2005)</a>
 * and greatly improves stream operation's efficiency.
 * Finally, off implementation is based on Murphy implementation but
 * solves some drawbacks and leads to a more efficient stream implementation. 
 */
package laboGrid.lb.lattice.d3.q19;
