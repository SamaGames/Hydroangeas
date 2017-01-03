package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

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

    private ArrayBlockingQueue<Long> stats;

    public SimpleGameTemplate(String id, JsonElement data)
    {
        //TODO package of template

        stats = new ArrayBlockingQueue<>(5, true);

        JsonObject formated = data.getAsJsonObject();
        this.id = id;
        this.gameName = multiple(formated, "game-name", "gameName").getAsString();
        this.mapName = multiple(formated, "map-name", "mapName").getAsString();
        this.minSlot = multiple(formated, "min-slots", "minSlot").getAsInt();
        this.maxSlot = multiple(formated, "max-slots", "maxSlot").getAsInt();
        this.options = formated.get("options");
        this.startupOptions = new JsonObject();
        JsonElement startupElement = formated.get("startupOptions");
        if (startupElement != null)
        {
            for (Map.Entry<String, JsonElement> entry : startupElement.getAsJsonObject().entrySet())
            {
                startupOptions.addProperty(entry.getKey(), entry.getValue().getAsString());
            }
        }
        for (Map.Entry<String, JsonElement> entry : DEFAULT_STARTUP_OPTIONS.entrySet())
        {
            if(startupOptions.get(entry.getKey()) == null)
            {
                startupOptions.addProperty(entry.getKey(), entry.getValue().getAsString());
            }
        }
        this.isCoupaing = formated.get("isCoupaing").getAsBoolean();

        //Temproray until autmatic compute
        this.weight = 150;
        if (formated.has("weight"))
        this.weight = formated.get("weight").getAsInt();
    }

    private int computeWeight()
    {



        return 0;
    }

    private JsonElement multiple(JsonObject object, String... multiple)
    {
        for (String id :
                multiple) {
            if (object.has(id))
                return object.get(id);
        }
        return null;
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

    @Override
    public void addTimeToStart(long time) {
        if(stats.remainingCapacity() <= 0)
        {
            stats.poll();
        }
        stats.offer(time);
    }

    @Override
    public long getTimeToStart()
    {
        long startTime = 0;
        int nb = 0;
        for(Long time : stats)
        {
            startTime += time;
            nb++;
        }
        return (nb > 0) ? startTime / nb : -1;
    }

    @Override
    public void resetStats() {
        stats.clear();
    }
}
