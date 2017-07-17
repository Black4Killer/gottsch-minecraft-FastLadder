/**
 * 
 */
package com.someguyssoftware.fastladder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.fastladder.eventhandler.PlayerEventHandler;
import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.config.IConfig;
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
		updateJSON = "https://gist.github.com/gottsch/3b047726b8124dfa9b5d90d021869843"
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
	public static Logger log = LogManager.getLogger("FastLadder");
	
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
//		configLogging(config);
		
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
	
	/**
	 * Add rolling file appender to the current logging system.
	 */
//	public void configLogging(ILoggerConfig config) {
//		log.info("configuring logger...");
//		Appender appender = createAppender("FastLadder", config);
//		addAppenderToLogger("FastLadder", appender, "FastLadder", config);
//	}
	
//	/**
//	 * Add rolling file appender to the current logging system.
//	 */
//	public static void configLogging(ILoggerConfig flConfig) {
//		// get config properties
//		String loggerLevel = flConfig.getLoggerLevel();
//		String loggerFolder = flConfig.getLoggerFolder();
//		
//		if (!loggerFolder.endsWith("/")) {
//			loggerFolder += "/";
//		}
//
//		final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
//        final Configuration config = loggerContext.getConfiguration();
//        
//        // create a size-based trigger policy for 500K
//        //SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy("500K");
//        // create a sized-based trigger policy, using config setting for size.  Default 500K
//        SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(flConfig.getLoggerSize());
//        // create the pattern for log statements
//        PatternLayout layout = PatternLayout.createLayout("%d [%t] %p %c | %F:%L | %m%n", null, null, null, "true");
//        
//        // create a rolling file appender for SGS_Treasure logger (which is used by the Treasure mod)
//        Appender appender = RollingFileAppender.createAppender(
//        	loggerFolder + "sgs_treasure.log",
//        	loggerFolder + "sgs_treasure-%d{yyyy-MM-dd-HH_mm_ss}.log",
//        	"true",
//        	"SGS_TREASURE",
//        	"true",
//            "true",
//            policy,
//            null,
//            layout,
//            null,
//            "true",
//            "false",
//            null,
//            config);
//
//        // start the appender
//        appender.start();
//        
//        // add appenders to config
////        ((BaseConfiguration) config).addAppender(appender);
//        config.addAppender(appender);
//        
//        // create appender references
//        AppenderRef treasureAppenderReference = AppenderRef.createAppenderRef("SGS_TREASURE", null, null);
//        
//        // create logger config
//        AppenderRef[] refs = new AppenderRef[] {treasureAppenderReference};
//        
//        // set the logger name "SGS Treasure!" to use the rolling file appender
//        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", loggerLevel, "SGS Treasure!", "true", refs, null, config, null );
//
//        
//        // add appenders to logger config
//        loggerConfig.addAppender(appender, null, null);
//
//        // add loggers to base configuratoin
//        ((BaseConfiguration) config).addLogger("SGS Treasure!", loggerConfig);
//
//        // update existing loggers
//        loggerContext.updateLoggers();	
//	}

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
