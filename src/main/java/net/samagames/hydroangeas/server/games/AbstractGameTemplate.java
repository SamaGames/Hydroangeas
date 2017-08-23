package net.samagames.hydroangeas.server.games;

import com.google.gson.JsonElement;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
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

    @Override
    String toString();

    JsonElement getStartupOptions();

    void addTimeToStart(long time);

    long getTimeToStart();

    void resetStats();
}
