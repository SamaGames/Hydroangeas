package net.samagames.hydroangeas.common.protocol.coupaings;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

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
public class CoupaingServerPacket extends AbstractPacket
{

    private String game;

    private String map;

    private int minSlot;
    private int maxSlot;

    private JsonElement options;
    private int weight;

    public CoupaingServerPacket()
    {

    }

    public String getGame()
    {
        return game;
    }

    public String getMap()
    {
        return map;
    }

    public int getMinSlot()
    {
        return minSlot;
    }

    public int getMaxSlot()
    {
        return maxSlot;
    }

    public JsonElement getOptions()
    {
        return options;
    }

    public int getWeight()
    {
        return weight;
    }
}
