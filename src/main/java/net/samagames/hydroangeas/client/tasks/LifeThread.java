package net.samagames.hydroangeas.client.tasks;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.protocol.intranet.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.intranet.HelloFromClientPacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerInfoPacket;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LifeThread
{
    private final static long TIMEOUT = 20 * 1000L;
    private final HydroangeasClient instance;
    private long lastHeartbeatFromServer;
    private boolean connected;

    public LifeThread(HydroangeasClient instance)
    {
        this.instance = instance;
        this.connected = false;
    }

    public void start()
    {
        try {
            sendData(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        instance.getScheduler().scheduleAtFixedRate(this::check, 2, 10, TimeUnit.SECONDS);
    }

    public void check()
    {
        instance.getConnectionManager().sendPacket(new HeartbeatPacket(instance.getClientUUID()));

        if (System.currentTimeMillis() - lastHeartbeatFromServer > TIMEOUT)
        {
            if (this.connected)
            {
                ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientUUID() + "] Impossible de contacter le serveur Hydroangeas !");
            }
            this.instance.log(Level.SEVERE, "Can't tell the Hydroangeas Server! Maybe it's down?");
            this.connected = false;
        } else if (!connected)
        {
            ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientUUID() + "] Retour à la normale !");

            this.instance.log(Level.INFO, "Hydroangeas Server has responded!");
            try {
                sendData(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connected = true;
        }
    }

    public void sendData(boolean all) throws InterruptedException {
        instance.getLogger().info("Resync data...");
        instance.getConnectionManager().sendPacket(new HelloFromClientPacket(instance));
        if (all)
        {
            Thread.sleep(3);
            for (MinecraftServerC server : instance.getServerManager().getServers())
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerInfoPacket(instance, server));
                Thread.sleep(1);
            }
        }
    }

    public void onServerHeartbeat(UUID uuid)
    {
        lastHeartbeatFromServer = System.currentTimeMillis();
    }

    public boolean isConnected()
    {
        return connected;
    }

}
