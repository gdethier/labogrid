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
package laboGrid.impl.decentral.task.events;

import laboGrid.impl.decentral.mtwa.Sub2Da;

public class NewSub2Da {
	
	private int simNum;
	private int version;
	private Sub2Da sub2Da;

	public NewSub2Da(int simNum, int version, Sub2Da sub2Da) {
		this.simNum = simNum;
		this.version = version;
		this.sub2Da = sub2Da;
	}
	
	public int getSimNum() {
		return simNum;
	}
	
	public int getVersion() {
		return version;
	}
	
	public Sub2Da getSub2Da() {
		return sub2Da;
	}

}
