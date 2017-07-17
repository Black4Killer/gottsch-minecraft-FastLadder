/**
 * 
 */
package com.someguyssoftware.fastladder;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.fastladder.eventhandler.PlayerEventHandler;
import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.command.ShowVersionCommand;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.config.ILoggerConfig;
import com.someguyssoftware.gottschcore.mod.AbstractMod;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.proxy.IProxy;
import com.someguyssoftware.gottschcore.version.BuildVersion;
import com.someguyssoftware.gottschcore.version.VersionChecker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
@Mod(
		modid=FastLadder.MODID,
		name=FastLadder.NAME,
		version=FastLadder.VERSION,
		dependencies="required-after:gottschcore@[1.0.0,)",
		acceptedMinecraftVersions = "[1.12]",
		// TODO add a const for update json file, like VERSION_URL
		updateJSON = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-FastLadder/master/FastLadder1.12/update.json"
	)
@Credits(values={"FastLadder! was first developed by Mark Gottschling on December 27, 2016."})
public class FastLadder extends AbstractMod {
	// constants
	public static final String MODID = "fastladder";
	public static final String NAME = "FastLadder!";
	public static final String VERSION = "1.0.4";
	
	// TODO create BuilderVersion that parses the minecraft forge updatejson file instead of custom format file
	private static final String VERSION_URL = "https://www.dropbox.com/s/9nftcgodlgsw79u/fastladder-versions.json?dl=1";
	private static final BuildVersion MINECRAFT_VERSION = new BuildVersion(1, 12, 0);
	
	// latest version
	public static BuildVersion latestVersion;

	// logger
	private static final String LOGGER_NAME = "FastLadder";
	public static Logger log = LogManager.getLogger(LOGGER_NAME);
	
	@Instance(value=FastLadder.MODID)
	public static FastLadder instance = new FastLadder();

	/*
	 * config
	 */
	private static final String FASTLADDER_CONFIG_DIR = "fastladder";
	private static FastLadderConfig config;
	
	@SidedProxy(clientSide="com.someguyssoftware.fastladder.proxy.ClientProxy", serverSide="com.someguyssoftware.fastladder.proxy.ServerProxy")
	public static IProxy proxy;
		
	/**
	 * 
	 */
	public FastLadder() {
		FastLadder.log.info("FastLadder Mod created.");
		System.out.println("System.out: FastLadder Mod created.");
	}


	@Override
	public BuildVersion getModLatestVersion() {
		return FastLadder.latestVersion;
	}
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler
	public void preInt(FMLPreInitializationEvent event) {
		FastLadder.log.info("in preInt.");
		
		super.preInt(event);
		// register additional events

		// register player events
		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
		
		// create and load the config file		
		config = new FastLadderConfig(this, event.getModConfigurationDirectory(), FASTLADDER_CONFIG_DIR, "general.cfg");
				
		// configure logging
		addRollingFileAppenderToLogger(LOGGER_NAME, LOGGER_NAME + "Appender", config);
		
        // register the packet handlers
        //network = NetworkRegistry.INSTANCE.newSimpleChannel(FastLadder.modid);

	}
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		// register client renderers
		proxy.registerRenderers(FastLadder.config);
	}
	
	/**
	 * 
	 * @param event
	 */
    @EventHandler
    public void serverStarted(FMLServerStartingEvent event) {
    	// add a show version command
    	event.registerServerCommand(new ShowVersionCommand(this));
    }
    
    /**
     * 
     * @param event
     */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// get the latest version from the website
		latestVersion = VersionChecker.getVersion(VERSION_URL, MINECRAFT_VERSION);
	}
	
	@Override
	public IConfig getConfig() {
		return FastLadder.config;
	}


	@Override
	public BuildVersion getMinecraftVersion() {
		return FastLadder.MINECRAFT_VERSION;
	}


	@Override
	public String getVerisionURL() {
		return FastLadder.VERSION_URL;
	}
	
	@Override
	public String getName() {
		return FastLadder.NAME;
	}


	@Override
	public String getId() {
		return FastLadder.MODID;
	}


	@Override
	public IMod getInstance() {
		return instance;
	}


	@Override
	public String getVersion() {
		return FastLadder.VERSION;
	}

}
