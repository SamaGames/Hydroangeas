package net.samagames.hydroangeas.server;

import com.google.gson.JsonArray;
import net.samagames.hydroangeas.client.packets.MinecraftServerEndPacket;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

public class AlgorithmicMachine
{
    private final HydroangeasServer instance;
    private JsonArray lastData;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void startMachinery()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");
    }

    public void onServerUpdate(MinecraftServerEndPacket serverStatus)
    {
        MinecraftServerInfos serverInfos = new MinecraftServerInfos(serverStatus.getServerInfos().getGame(), serverStatus.getServerInfos().getMap());
    }
}
