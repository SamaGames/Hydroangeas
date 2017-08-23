package net.samagames.hydroangeas.server.data;

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
public enum Status
{

    STARTING("starting", false),
    WAITING_FOR_PLAYERS("waitingForPlayers", true),
    READY_TO_START("readyToStart", true),
    IN_GAME("inGame", false),
    FINISHED("finished", false),
    REBOOTING("rebooting", false),
    NOT_RESPONDING("idle", false);

    private final String id;
    private final boolean allowJoin;

    Status(String id, boolean allowJoin)
    {
        this.id = id;
        this.allowJoin = allowJoin;
    }

    public static Status fromString(String str)
    {
        for (Status status : Status.values())
            if (status.getId().equals(str))
                return status;

        return null;
    }

    public boolean isAllowJoin()
    {
        return allowJoin;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return getId();
    }
}
