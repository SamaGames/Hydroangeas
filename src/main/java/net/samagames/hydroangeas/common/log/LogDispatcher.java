package net.samagames.hydroangeas.common.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

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
public class LogDispatcher extends Thread
{

    private final HydroLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public LogDispatcher(HydroLogger logger)
    {
        super("Hydroangeas Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run()
    {
        while (!isInterrupted())
        {
            LogRecord record;
            try
            {
                record = queue.take();
            } catch (InterruptedException ex)
            {
                continue;
            }

            logger.doLog(record);
        }
        queue.forEach(logger::doLog);
    }

    public void queue(LogRecord record)
    {
        if (!isInterrupted())
        {
            queue.add(record);
        }
    }
}
