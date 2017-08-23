package net.samagames.hydroangeas.common.log;

import net.samagames.hydroangeas.Hydroangeas;

import java.io.IOException;
import java.util.logging.*;

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
public class HydroLogger extends Logger
{

    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher(this);

    @SuppressWarnings(
            {
                    "CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"
            })
    public HydroLogger(Hydroangeas hydroangeas)
    {
        super("Hydroangeas", null);
        setLevel(Level.ALL);

        try
        {
            FileHandler fileHandler = new FileHandler("Hydroangeas.log", 1 << 24, 8, true);
            fileHandler.setFormatter(formatter);
            addHandler(fileHandler);

            ColouredWriter consoleHandler = new ColouredWriter(hydroangeas.getConsoleReader());
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(formatter);
            addHandler(consoleHandler);
        } catch (IOException ex)
        {
            System.err.println("Could not register logger!");
            ex.printStackTrace();
        }
        dispatcher.start();
    }

    @Override
    public void log(LogRecord record)
    {
        dispatcher.queue(record);
    }

    void doLog(LogRecord record)
    {
        super.log(record);
    }
}