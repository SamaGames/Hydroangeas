package net.samagames.hydroangeas.common.protocol.queues;

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
public abstract class QueuePacket extends AbstractPacket
{

    private String game;
    private String map;

    private String templateID;

    private TypeQueue typeQueue;

    public QueuePacket()
    {
    }

    public QueuePacket(TypeQueue typeQueue, String game, String map)
    {
        this(typeQueue, game + "_" + map);
        this.game = game;
        this.map = map;
    }

    public QueuePacket(TypeQueue typeQueue, String templateID)
    {
        this.typeQueue = typeQueue;
        this.templateID = templateID;
    }

    public TypeQueue getTypeQueue()
    {
        return typeQueue;
    }

    public String getTemplateID()
    {
        return templateID;
    }

    public String getMap()
    {
        return map;
    }

    public String getGame()
    {
        return game;
    }


    public enum TypeQueue
    {
        NAMEDID, NAMED, RANDOM, FAST
    }
}
