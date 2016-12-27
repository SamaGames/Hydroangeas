package net.samagames.hydroangeas.common.data;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.tasks.CleanServer;

import java.util.UUID;

/**
 * /`\  ___  /`\
 * \d `"\:/"` b/
 * /`.--. ` .--.`\
 * |/ __ \ / __ \|
 * ( ((o) V (o)) )
 * |\`""`/^\`""`/|
 * \ `--'\ /'--` /
 * /`-._  `  _.-`\
 * / /.:.:.:.:.:.\ \
 * ; |.:.:.:.:.:.:.| ;
 * | |:.:.:.:.:.:.:| |
 * | |.:.:.:.:.:.:.| |
 * | |:.:.:.:.:.:.:| |
 * \/\.:.:.:.:.:.:./\/
 * _`).-.-:-.-.(`_
 * ,=^` |=  =| |=  =| `^=,
 * /     \=/\=/ \=/\=/     \
 * `  `   `  `
 * Created by Silvanosky on 26/12/2016
 */

public abstract class MinecraftServer {

    protected UUID uuid;

    protected UUID owner;
    protected boolean coupaingServer;

    protected String game;
    protected String map;

    protected int minSlot;
    protected int maxSlot;
    protected String templateID;

    protected JsonElement options;
    protected JsonElement startupOptions;

    protected Integer hubID = null;

    protected int weight;

    protected int port;

    protected Status status = Status.STARTING;
    protected int actualSlots;

    protected long timeToLive = CleanServer.LIVETIME;
    protected long startedTime;

    public MinecraftServer(UUID uuid,
                            String game,
                            String map,
                            int minSlot,
                            int maxSlot,
                            JsonElement options,
                            JsonElement startupOptions)
    {
        this.uuid = uuid;
        this.game = game;
        this.map = map;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;
        this.startupOptions = startupOptions;

        this.startedTime = System.currentTimeMillis();

    }


    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void changeUUID()
    {
        this.uuid = UUID.randomUUID();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isCoupaingServer() {
        return coupaingServer;
    }

    public void setCoupaingServer(boolean coupaingServer) {
        this.coupaingServer = coupaingServer;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getMinSlot() {
        return minSlot;
    }

    public void setMinSlot(int minSlot) {
        this.minSlot = minSlot;
    }

    public int getMaxSlot() {
        return maxSlot;
    }

    public void setMaxSlot(int maxSlot) {
        this.maxSlot = maxSlot;
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    public String getServerName()
    {
        return this.game + "_" + ((hubID == null) ? this.uuid.toString().split("-")[0] : hubID);
    }

    public JsonElement getOptions() {
        return options;
    }

    public void setOptions(JsonElement options) {
        this.options = options;
    }

    public JsonElement getStartupOptions() {
        return startupOptions;
    }

    public void setStartupOptions(JsonElement startupOptions) {
        this.startupOptions = startupOptions;
    }

    public boolean isHub()
    {
        return hubID != null;
    }

    public Integer getHubID() {
        return hubID;
    }

    public void setHubID(Integer hubID) {
        this.hubID = hubID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getActualSlots() {
        return actualSlots;
    }

    public void setActualSlots(int actualSlots) {
        this.actualSlots = actualSlots;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }
}
