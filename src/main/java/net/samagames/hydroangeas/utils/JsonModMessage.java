package net.samagames.hydroangeas.utils;

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
public class JsonModMessage
{
    protected String sender;
    protected ChatColor senderPrefix;
    protected String message;

    public JsonModMessage(String sender, ChatColor senderPrefix, String message)
    {
        this.sender = sender;
        this.senderPrefix = senderPrefix;
        this.message = message;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public ChatColor getSenderPrefix()
    {
        return senderPrefix;
    }

    public void setSenderPrefix(ChatColor senderPrefix)
    {
        this.senderPrefix = senderPrefix;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}