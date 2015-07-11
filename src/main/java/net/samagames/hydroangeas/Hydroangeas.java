package net.samagames.hydroangeas;

import jline.UnsupportedTerminal;
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
import java.net.ServerSocket;
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

    protected Logger logger;

    public Hydroangeas(OptionSet options) throws IOException {
        instance = this;

        uuid = UUID.randomUUID();

        AnsiConsole.systemInstall();
        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);

        logger = new HydroLogger(this);

        if ( consoleReader.getTerminal() instanceof UnsupportedTerminal)
        {
            log(Level.INFO, "Unable to initialize fancy terminal. To fix this on Windows, install the correct Microsoft Visual C++ 2008 Runtime");
            log(Level.INFO, "NOTE: This error is non crucial, and BungeeCord will still function correctly! Do not bug the author about it unless you are still unable to get it working");
        }

        logger.info("Hydroangeas version 1.0.0 by BlueSlime");
        logger.info("----------------------------------------");

        this.scheduler = Executors.newScheduledThreadPool(16);

        this.options = options;
        this.configuration = new Configuration(this, options);
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

    public static Hydroangeas getInstance()
    {
        return instance;
    }

    public static int findRandomOpenPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public abstract void enable();

    public abstract void disable();

    public void shutdown()
    {
        isRunning = false;

        disable();

        scheduler.shutdownNow();

        this.redisSubscriber.disable();
    }

    public void log(Level level, String message)
    {
        /*DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String finalMessage = "[" + dateFormat.format(new Date()) + "]" + " [" + level.getName().toUpperCase() + "] " + message;

        if(level == Level.SEVERE)
            System.err.println(finalMessage);
        else
            System.out.println(finalMessage);*/
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
        if(this instanceof HydroangeasClient)
            return (HydroangeasClient) this;
        else
            return null;
    }

    public HydroangeasServer getAsServer()
    {
        if(this instanceof HydroangeasServer)
            return (HydroangeasServer) this;
        else
            return null;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public UUID getUUID() {
        return uuid;
    }
}
