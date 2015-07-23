package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.server.waitingqueue.QPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 19/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueUpdateFromHub extends AbstractPacket
{

    private String game;

    private String map;

    private ActionQueue action;
    private TypeQueue typeQueue;
    private QPlayer groupLeader;
    private List<QPlayer> players = new ArrayList<>();

    public QueueUpdateFromHub()
    {
    }

    public QueueUpdateFromHub(ActionQueue action, String game, String map, TypeQueue typeQueue, List<QPlayer> players)
    {
        this(action, game, map, typeQueue, players.get(0), players);
    }

    public QueueUpdateFromHub(ActionQueue action, String game, String map, TypeQueue typeQueue, QPlayer leader, List<QPlayer> players)
    {
        this.action = action;
        this.game = game;
        this.typeQueue = typeQueue;
        this.players = players;
        this.groupLeader = leader;
        this.map = map;
    }

    public ActionQueue getAction() {
        return action;
    }

    public QPlayer getGroupLeader()
    {
        return groupLeader;
    }

    public List<QPlayer> getPlayers() {
        return players;
    }

    public String getGame() {
        return game;
    }

    public String getMap() {
        return map;
    }

    public TypeQueue getTypeQueue() {
        return typeQueue;
    }

    public enum ActionQueue{ADD, REMOVE}
    public enum TypeQueue{NAMED, RANDOM, FAST}
}
