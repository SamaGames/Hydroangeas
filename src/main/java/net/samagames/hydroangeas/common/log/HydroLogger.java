package net.samagames.hydroangeas.common.log;

import net.samagames.hydroangeas.Hydroangeas;

import java.io.IOException;
import java.util.logging.*;

public class HydroLogger extends Logger
{

    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher( this );

    @SuppressWarnings(
            {
                    "CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"
            })
    public HydroLogger(Hydroangeas hydroangeas)
    {
        super( "Hydroangeas", null );
        setLevel( Level.ALL );

        try
        {
            FileHandler fileHandler = new FileHandler( "Hydroangeas.log", 1 << 24, 8, true );
            fileHandler.setFormatter( formatter );
            addHandler( fileHandler );

            ColouredWriter consoleHandler = new ColouredWriter( hydroangeas.getConsoleReader() );
            consoleHandler.setLevel( Level.INFO );
            consoleHandler.setFormatter( formatter );
            addHandler( consoleHandler );
        } catch ( IOException ex )
        {
            System.err.println( "Could not register logger!" );
            ex.printStackTrace();
        }
        dispatcher.start();
    }

    @Override
    public void log(LogRecord record)
    {
        dispatcher.queue( record );
    }

    void doLog(LogRecord record)
    {
        super.log( record );
    }
}