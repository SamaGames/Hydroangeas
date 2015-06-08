package net.samagames.hydroangeas.server.scheduler;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.logging.Level;

public class ClientScheduledRunnable implements Runnable
{
    private final HydroangeasServer instance;
    private final String clientName;

    private ClientInfos lastInfos;

    public ClientScheduledRunnable(HydroangeasServer instance, ClientInfos initialClientInfos)
    {
        this.instance = instance;
        this.clientName = initialClientInfos.getClientName();
        this.lastInfos = initialClientInfos;
    }

    @Override
    public void run()
    {
        ClientInfos now = Hydroangeas.getInstance().getAsServer().getClientManager().getClientInfosByUUID(this.clientName);

        if(!now.getTimestamp().after(this.lastInfos.getTimestamp()))
        {
            Hydroangeas.getInstance().log(Level.WARNING, "Lost connection with client " + this.clientName + "!");
            ModMessage.sendMessage(InstanceType.SERVER, "Connexion perdue avec le client " + this.clientName + " !");

            this.instance.getClientManager().onClientNoReachable(this.clientName);
            this.instance.getClientManager().getKeepUpdatedThread().stopClient(this.clientName);

            return;
        }

        this.lastInfos = now;
    }
}
