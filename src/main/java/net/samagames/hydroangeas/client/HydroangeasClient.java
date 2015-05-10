package net.samagames.hydroangeas.client;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.packets.HelloServerPacketReceiver;
import net.samagames.hydroangeas.utils.JsonUtils;

import java.util.UUID;
import java.util.logging.Level;

public class HydroangeasClient extends Hydroangeas
{
    private UUID clientUUID;
    private String dedicatedGame;
    private LifeThread lifeThread;

    public HydroangeasClient(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas client...");

        this.clientUUID = UUID.randomUUID();
        this.dedicatedGame = JsonUtils.getStringOrNull(this.configuration.getJsonConfiguration().get("dedicated-game"));

        this.redisSubscriber.registerReceiver("hello@hydroangeas-client", new HelloServerPacketReceiver());

        this.lifeThread = new LifeThread(this);
        this.lifeThread.start();
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
        this.lifeThread.stop();
    }

    public UUID getClientUUID()
    {
        return this.clientUUID;
    }

    public String getDedicatedGame()
    {
        return this.dedicatedGame;
    }

    public LifeThread getLifeThread()
    {
        return this.lifeThread;
    }
}
