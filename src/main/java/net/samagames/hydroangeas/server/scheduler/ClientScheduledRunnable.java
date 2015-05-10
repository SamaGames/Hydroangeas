package net.samagames.hydroangeas.server.scheduler;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.ClientInfos;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.utils.ChatColor;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.UUID;
import java.util.logging.Level;

public class ClientScheduledRunnable implements Runnable
{
    private final HydroangeasServer instance;
    private final UUID clientUUID;

    private ClientInfos lastInfos;

    public ClientScheduledRunnable(HydroangeasServer instance, ClientInfos initialClientInfos)
    {
        this.instance = instance;
        this.clientUUID = initialClientInfos.getClientUUID();
        this.lastInfos = initialClientInfos;
    }

    @Override
    public void run()
    {
        ClientInfos now = Hydroangeas.getInstance().getAsServer().getClientManager().getClientInfosByUUID(this.clientUUID);

        if(!now.getTimestamp().after(this.lastInfos.getTimestamp()))
        {
            Hydroangeas.getInstance().log(Level.WARNING, "Lost connection with client " + this.clientUUID.toString() + "!");
            ModMessage.sendModMessage("Hydroangeas Server", ChatColor.GREEN, "Connexion perdue avec le client " + this.clientUUID.toString() + " !");

            this.instance.getClientManager().onClientNoReachable(this.clientUUID);
            this.instance.getClientManager().getKeepUpdatedThread().stopClient(this.clientUUID);

            return;
        }

        this.lastInfos = now;
    }
}
