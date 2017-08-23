package net.samagames.hydroangeas.common.packets;

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
public abstract class AbstractPacket
{
    private final Runnable callback;

    public AbstractPacket(Runnable callback)
    {
        this.callback = callback;
    }

    public AbstractPacket()
    {
        this.callback = null;
    }

    public void callback()
    {
        try
        {
            if (this.callback != null)
                this.callback.run();
        } catch (Exception ignored)
        {
        }
    }
}