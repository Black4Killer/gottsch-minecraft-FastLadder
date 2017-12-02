/**
 * 
 */
package com.someguyssoftware.fastladder.block.material;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;

/**
 * Basically a tag class for comparison purposes.  Behaves the same as MaterialLogic (circuits).
 * @author Mark Gottschling on Jan 3, 2016
 *
 */
public class FasterMaterial extends MaterialLogic {

	public FasterMaterial(MapColor mapColor) {
		super(mapColor);
		this.setNoPushMobility();
	}

}
