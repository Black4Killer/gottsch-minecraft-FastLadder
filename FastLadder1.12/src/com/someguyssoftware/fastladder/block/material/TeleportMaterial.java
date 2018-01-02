/**
 * 
 */
package com.someguyssoftware.fastladder.block.material;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;

/**
 * Basically a tag class for comparison purposes.  Behaves the same as MaterialLogic (circuits).
 * @author Mark Gottschling on Dec 26, 2017
 *
 */
public class TeleportMaterial extends MaterialLogic {

	public TeleportMaterial(MapColor mapColor) {
		super(mapColor);
		this.setNoPushMobility();
	}

}
