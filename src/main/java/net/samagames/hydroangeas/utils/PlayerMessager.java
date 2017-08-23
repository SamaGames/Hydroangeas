package net.samagames.hydroangeas.utils;

import net.samagames.hydroangeas.Hydroangeas;

import java.util.UUID;

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
public class PlayerMessager
{

    public static void sendMessage(UUID player, String message)
    {
        Hydroangeas.getInstance().getRedisSubscriber().send("apiexec.send", player + " {\"text\":\"" + message + "\"}");
    }
}
