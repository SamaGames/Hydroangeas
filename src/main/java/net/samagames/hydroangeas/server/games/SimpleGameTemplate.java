package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.util.Map;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 07/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class SimpleGameTemplate implements AbstractGameTemplate
{
    private static JsonObject DEFAULT_STARTUP_OPTIONS;
    static {
        DEFAULT_STARTUP_OPTIONS = new JsonObject();
        DEFAULT_STARTUP_OPTIONS.addProperty("minRAM", "512M");
        DEFAULT_STARTUP_OPTIONS.addProperty("maxRAM", "1024M");
        DEFAULT_STARTUP_OPTIONS.addProperty("edenRAM", "256M");
    }
    private String id;

    private String gameName;
    private String mapName;
    private int minSlot;
    private int maxSlot;
    private JsonElement options;
    private JsonObject startupOptions;
    private boolean isCoupaing;

    private int weight;

    public SimpleGameTemplate(String id, JsonElement data)
    {
        //TODO package of template

        JsonObject formated = data.getAsJsonObject();
        this.id = id;
        this.gameName = formated.get("game-name").getAsString();
        this.mapName = formated.get("map-name").getAsString();
        this.minSlot = formated.get("min-slots").getAsInt();
        this.maxSlot = formated.get("max-slots").getAsInt();
        this.options = formated.get("options");
        this.startupOptions = DEFAULT_STARTUP_OPTIONS;
        JsonElement startupElement = formated.get("startupOptions");
        if (startupElement != null)
        {
            for (Map.Entry<String, JsonElement> entry : startupElement.getAsJsonObject().entrySet())
            {
                startupOptions.add(entry.getKey(), entry.getValue());
            }
        }
        this.isCoupaing = formated.get("isCoupaing").getAsBoolean();

        JsonElement weightObj = formated.get("weight");
        if (weightObj == null)
        {
            this.weight = MiscUtils.calculServerWeight(gameName, maxSlot, isCoupaing);
        } else
            weight = weightObj.getAsInt();
    }

    public SimpleGameTemplate(String id, String gameName, String mapName, int minSlot, int maxSlot, int weight, JsonElement options)
    {
        this(id, gameName, mapName, minSlot, maxSlot, options, weight, false);
    }

    public SimpleGameTemplate(String id, String gameName, String mapName, int minSlot, int maxSlot, JsonElement options, int weight, boolean isCoupaing)
    {
        this.id = id;
        this.gameName = gameName;
        this.mapName = mapName;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;
        this.isCoupaing = isCoupaing;
    }

    public String getId()
    {
        return id;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getMapName()
    {
        return mapName;
    }

    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }

    public int getMinSlot()
    {
        return minSlot;
    }

    public void setMinSlot(int minSlot)
    {
        this.minSlot = minSlot;
    }

    public int getMaxSlot()
    {
        return maxSlot;
    }

    public void setMaxSlot(int maxSlot)
    {
        this.maxSlot = maxSlot;
    }

    public JsonElement getOptions()
    {
        return options;
    }

    public void setOptions(JsonElement options)
    {
        this.options = options;
    }

    public int getWeight()
    {
        return weight;
    }

    public boolean isCoupaing()
    {
        return isCoupaing;
    }

    public void setIsCoupaing(boolean isCoupaing)
    {
        this.isCoupaing = isCoupaing;
    }

    public String toString()
    {
        return "Template id: " + id + ((isCoupaing) ? " Coupaing Server " : " ");
    }

    @Override
    public JsonObject getStartupOptions()
    {
        return startupOptions;
    }
}
