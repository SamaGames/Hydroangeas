package net.samagames.hydroangeas.client.schedulers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.protocol.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.HelloFromClientPacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerUpdatePacket;
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
        instance.getScheduler().scheduleAtFixedRate(() -> check(), 2, 10, TimeUnit.SECONDS);
    }

    public void check()
    {
        instance.getConnectionManager().sendPacket(new HeartbeatPacket(instance.getClientUUID()));

        if(System.currentTimeMillis() - lastHeartbeatFromServer > TIMEOUT)
        {
            if(this.connected)
            {
                ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientUUID().toString() + "] Impossible de contacter le serveur Hydroangeas !");
            }
            this.instance.log(Level.SEVERE, "Can't tell the Hydroangeas Server! Maybe it's down?");
            this.connected = false;
        }else if(!connected)
        {
            ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientUUID().toString() + "] Retour à la normale !");

            this.instance.log(Level.INFO, "Hydroangeas Server has responded! Resync data...");
            sendData(true);

            connected = true;
        }
    }

    public void sendData(boolean all)
    {
        instance.getConnectionManager().sendPacket(new HelloFromClientPacket(instance));
        if(all)
        {
            for(MinecraftServerC server : instance.getServerManager().getServers())
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.INFO));
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
