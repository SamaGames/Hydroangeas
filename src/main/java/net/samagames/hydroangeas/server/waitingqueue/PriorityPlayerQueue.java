package net.samagames.hydroangeas.server.waitingqueue;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

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
public class PriorityPlayerQueue extends PriorityBlockingQueue<QGroup>
{
    private final ReentrantLock lock = new ReentrantLock();
    private Method remoteAtMethod;

    public PriorityPlayerQueue(int initialCapacity, Comparator<? super QGroup> comparator)
    {
        super(initialCapacity, comparator);
        try
        {
            this.remoteAtMethod = PriorityBlockingQueue.class.getDeclaredMethod("removeAt", int.class);
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    public int drainPlayerTo(Collection<? super QGroup> c, int maxElements)
    {
        if (c == null)
            throw new NullPointerException();
        if (maxElements <= 0)
            return 0;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int check = maxElements;
            int j = 0;

            for (int i = 0; i < this.size(); i++)
            {
                QGroup group = (QGroup) this.toArray()[j];

                if (check - group.getSize() >= 0)
                {
                    c.add(group); // In this order, in case add() throws.

                    try
                    {
                        this.remoteAtMethod.setAccessible(true);
                        this.remoteAtMethod.invoke(this, j);
                    } catch (ReflectiveOperationException e)
                    {
                        e.printStackTrace();
                    }

                    check -= group.getSize();
                } else
                {
                    j++;
                }

                if (check <= 0)
                    break;

            }
            return maxElements - check;
        } finally
        {
            lock.unlock();
        }
    }
}
