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
 * Provides operators that can be inserted in a processing chain. All operators
 * are implemented by subclasses of
 * {@link laboGrid.procChain.operators.LBOperator LBOperator}.
 * <p>
 * LaBoGrid provides predefined operators (see classes of this package).
 * Some being specific to 3D lattices
 * (see package {@link laboGrid.procChain.operators.d3})
 * and D3Q19 lattices (see package {@link laboGrid.procChain.operators.d3.q19}).
 */
package laboGrid.procChain.operators;
