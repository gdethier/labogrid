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
 * Contains classes implementing Sean Murphy's D3Q19 lattice.
 * This implementation is based on a solution proposed
 * by <a href="http://www2.epcc.ed.ac.uk/msc/dissertations/dissertations-0405/0762240-9j-dissertation1.1.pdf">Sean Murphy (2005)</a>
 * and greatly improves stream operation's efficiency by using a constant time
 * array shifting technique.
 */
package laboGrid.lb.lattice.d3.q19.murphy;
