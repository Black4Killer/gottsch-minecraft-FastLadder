/**
 * 
 */
package com.someguyssoftware.fastladder.config;

import java.io.File;

import com.someguyssoftware.gottschcore.config.AbstractConfig;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.common.config.Configuration;

/**
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
public class FastLadderConfig extends AbstractConfig {
	// ids
	public static String fastLadderBlockID;
	public static String fasterLadderBlockID;
	public static String fastestLadderBlockID;
//	public static String teleportLadderContraptionBlockID;
	public static String teleportLadderBlockID;
	
	/**
	 * 
	 * @param mod
	 * @param configDir
	 * @param modDir
	 * @param filename
	 */
	public FastLadderConfig(IMod mod, File configDir, String modDir, String filename) {
		super(mod, configDir, modDir, filename);
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.config.IConfig#load(java.io.File)
	 */
	@Override
	public Configuration load(File file) {
		// load the config file
		Configuration config = super.load(file);

		// add mod specific settings here
        config.setCategoryComment("03-fastladder", "General Fast Ladder! mod properties.");        
              
        // ids
        config.setCategoryComment("99-ids", "ID properties.");
        fastLadderBlockID = config.getString("fastLadderBlockId", "99-ids", "fast_ladder", "");
        fasterLadderBlockID = config.getString("fasterLadderBlockID", "99-ids", "faster_ladder", "");
        fastestLadderBlockID = config.getString("fastestLadderBlockID", "99-ids", "fastest_ladder", "");
//        teleportLadderContraptionBlockID = config.getString("teleportLadderContraptionBlockID", "99-ids", "teleport_ladder_contraption", "");
        teleportLadderBlockID = config.getString("teleportLadderBlockID", "99-ids", "teleport_ladder", "");
        
        // the the default values
       if(config.hasChanged()) {
    	   config.save();
       }
       
		return config;		
	}

	public String getFastLadderBlockID() {
		return FastLadderConfig.fastLadderBlockID;
	}


	public String getFasterLadderBlockID() {
		return FastLadderConfig.fasterLadderBlockID;
	}


	public String getFastestLadderBlockID() {
		return FastLadderConfig.fastestLadderBlockID;
	}

}
