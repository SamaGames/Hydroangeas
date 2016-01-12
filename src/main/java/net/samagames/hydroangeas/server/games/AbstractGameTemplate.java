package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;

/**
 * Created by Silva on 09/09/2015.
 */
public interface AbstractGameTemplate
{

    String getId();

    String getGameName();

    String getMapName();

    int getMinSlot();

    int getMaxSlot();

    JsonElement getOptions();

    int getWeight();

    boolean isCoupaing();

    String toString();

    JsonElement getStartupOptions();

    void addTimeToStart(long time);

    long getTimeToStart();

    void resetStats();
}
