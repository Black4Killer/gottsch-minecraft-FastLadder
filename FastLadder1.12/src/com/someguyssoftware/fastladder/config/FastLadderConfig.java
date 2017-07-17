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
	private String fastLadderBlockId;
	private String fasterLadderBlockId;
	private String fastestLadderBlockId;
	
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

		// TODO add mod specific settings here
        // enable fastladder settings
        config.setCategoryComment("03-fastladder", "General Fast Ladder! mod properties.");        
              
        // ids
        config.setCategoryComment("99-ids", "ID properties.");
        fastLadderBlockId = config.getString("fastLadderBlockId", "99-ids", "fast_ladder", "");
        fasterLadderBlockId = config.getString("fasterLadderBlockId", "99-ids", "faster_ladder", "");
        fastestLadderBlockId = config.getString("fastestLadderBlockId", "99-ids", "fastest_ladder", "");
        
        // the the default values
       if(config.hasChanged()) {
    	   config.save();
       }
       
		return config;		
	}

	public String getFastLadderBlockId() {
		return fastLadderBlockId;
	}


	public String getFasterLadderBlockId() {
		return fasterLadderBlockId;
	}


	public String getFastestLadderBlockId() {
		return fastestLadderBlockId;
	}

}
