package net.samagames.hydroangeas.server.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.HeartbeatPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class KeepUpdatedThread
{
    private final static long TIMEOUT = 20 * 1000L;
    private final HydroangeasServer instance;

    public KeepUpdatedThread(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void start()
    {
        instance.getScheduler().scheduleAtFixedRate(this::check, 2, 10, TimeUnit.SECONDS);
    }

    public void check()
    {
        this.instance.getClientManager().getClients().stream().forEachOrdered(client -> {
            try
            {
                instance.getConnectionManager().sendPacket(client, new HeartbeatPacket(instance.getServerUUID()));
                if (System.currentTimeMillis() - client.getTimestamp() > TIMEOUT)
                {
                    Hydroangeas.getInstance().log(Level.WARNING, "Lost connection with client " + client.getUUID() + "!");
                    ModMessage.sendMessage(InstanceType.SERVER, "Connexion perdue avec le client " + client.getUUID() + " !");

                    instance.getClientManager().onClientNoReachable(client.getUUID());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
