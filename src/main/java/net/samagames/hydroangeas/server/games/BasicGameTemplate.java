package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.utils.MiscUtils;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 07/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class BasicGameTemplate {

    private String id;

    private String gameName;
    private String mapName;
    private int minSlot;
    private int maxSlot;
    private JsonElement options;
    private boolean isCoupaing;

    private int weight;

    public BasicGameTemplate(String id, JsonElement data)
    {

        JsonObject formated = data.getAsJsonObject();
        this.id = id;
        this.gameName = formated.get("game-name").getAsString();
        this.mapName = formated.get("map-name").getAsString();
        this.minSlot = formated.get("min-slots").getAsInt();
        this.maxSlot = formated.get("max-slots").getAsInt();
        this.options = formated.get("options");
        this.isCoupaing = formated.get("isCoupaing").getAsBoolean();

        calculateWeight();
    }

    public BasicGameTemplate(String id, String gameName, String mapName, int minSlot, int maxSlot, JsonElement options)
    {
        this(id, gameName, mapName, minSlot, maxSlot, options, false);
    }

    public BasicGameTemplate(String id, String gameName, String mapName, int minSlot, int maxSlot, JsonElement options, boolean isCoupaing)
    {

        this.id = id;
        this.gameName = gameName;
        this.mapName = mapName;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;
        this.isCoupaing = isCoupaing;

        calculateWeight();
    }

    public String getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
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

    public JsonElement getOptions() {
        return options;
    }

    public void setOptions(JsonElement options) {
        this.options = options;
    }

    public void calculateWeight()
    {
        weight = MiscUtils.calculServerWeight(gameName, maxSlot, isCoupaing);
    }

    public int getWeight() {
        return weight;
    }

    public boolean isCoupaing() {
        return isCoupaing;
    }

    public void setIsCoupaing(boolean isCoupaing) {
        this.isCoupaing = isCoupaing;
    }

    public String toString()
    {
        return "Template id: " + id + ((isCoupaing)?" Coupaing Server ":" ");
    }
}
