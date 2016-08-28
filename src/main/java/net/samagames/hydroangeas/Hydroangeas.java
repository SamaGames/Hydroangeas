package net.samagames.hydroangeas;

import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.CommandManager;
import net.samagames.hydroangeas.common.database.DatabaseConnector;
import net.samagames.hydroangeas.common.database.RedisSubscriber;
import net.samagames.hydroangeas.common.log.HydroLogger;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.utils.LinuxBridge;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Hydroangeas
{

    private static Hydroangeas instance;
    protected final ScheduledExecutorService scheduler;
    protected final ConsoleReader consoleReader;
    public boolean isRunning;
    protected UUID uuid;
    protected OptionSet options;
    protected Configuration configuration;
    protected DatabaseConnector databaseConnector;
    protected RedisSubscriber redisSubscriber;
    protected LinuxBridge linuxBridge;

    protected CommandManager commandManager;

    protected static Logger logger;

    public Hydroangeas(OptionSet options) throws IOException
    {
        instance = this;
        uuid = UUID.randomUUID();

        AnsiConsole.systemInstall();
        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);

        logger = new HydroLogger(this);

        logger.info("Hydroangeas version 1.0.0");
        logger.info("----------------------------------------");

        this.scheduler = Executors.newScheduledThreadPool(16);
        this.options = options;
        loadConfig();
        this.databaseConnector = new DatabaseConnector(this);

        this.redisSubscriber = new RedisSubscriber(this);
        this.linuxBridge = new LinuxBridge();
        this.enable();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            this.log(Level.INFO, "Shutdown asked!");
            this.shutdown();
            this.log(Level.INFO, "Bye!");
        }));

        isRunning = true;
    }

    public void loadConfig()
    {
        this.configuration = new Configuration(this, options);
    }

    public static Hydroangeas getInstance()
    {
        return instance;
    }

    public abstract void enable();

    public abstract void disable();

    public void shutdown()
    {
        isRunning = false;

        disable();
        scheduler.shutdown();

        this.redisSubscriber.disable();

        databaseConnector.disconnect();
    }

    public void log(Level level, String message)
    {
        logger.log(level, message);
    }

    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    public DatabaseConnector getDatabaseConnector()
    {
        return this.databaseConnector;
    }

    public RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    public LinuxBridge getLinuxBridge()
    {
        return this.linuxBridge;
    }

    public ScheduledExecutorService getScheduler()
    {
        return scheduler;
    }

    public HydroangeasClient getAsClient()
    {
        if (this instanceof HydroangeasClient)
            return (HydroangeasClient) this;
        else
            return null;
    }

    public HydroangeasServer getAsServer()
    {
        if (this instanceof HydroangeasServer)
            return (HydroangeasServer) this;
        else
            return null;
    }

    public ConsoleReader getConsoleReader()
    {
        return consoleReader;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public enum RestrictionMode
    {
        NONE("none"),
        WHITELIST("whitelist"),
        BLACKLIST("blacklist");

        private String mode;

        RestrictionMode(String mode)
        {

            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        static public RestrictionMode valueFrom(String mode)
        {
            for(RestrictionMode data : RestrictionMode.values())
            {
                if(data.getMode().equalsIgnoreCase(mode))
                    return data;
            }

            return RestrictionMode.NONE;
        }
    }
}
