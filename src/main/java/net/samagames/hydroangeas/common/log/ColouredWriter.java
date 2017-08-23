package net.samagames.hydroangeas.common.log;


import jline.console.ConsoleReader;
import net.samagames.hydroangeas.utils.ChatColor;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Handler;
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
public class ColouredWriter extends Handler
{

    private final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);
    private final ChatColor[] colors = ChatColor.values();
    private final ConsoleReader console;

    public ColouredWriter(ConsoleReader console)
    {
        this.console = console;

        replacements.put(ChatColor.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        replacements.put(ChatColor.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        replacements.put(ChatColor.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        replacements.put(ChatColor.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        replacements.put(ChatColor.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        replacements.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        replacements.put(ChatColor.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        replacements.put(ChatColor.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        replacements.put(ChatColor.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        replacements.put(ChatColor.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        replacements.put(ChatColor.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        replacements.put(ChatColor.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        replacements.put(ChatColor.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        replacements.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        replacements.put(ChatColor.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        replacements.put(ChatColor.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        replacements.put(ChatColor.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
        replacements.put(ChatColor.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        replacements.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
        replacements.put(ChatColor.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
        replacements.put(ChatColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
        replacements.put(ChatColor.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());
    }

    public void print(String s)
    {
        for (ChatColor color : colors)
        {
            s = s.replaceAll("(?i)" + color, replacements.get(color));
        }
        try
        {
            console.print(ConsoleReader.RESET_LINE + s + Ansi.ansi().reset());
            console.drawLine();
            console.flush();
        } catch (IOException ex)
        {
        }
    }

    @Override
    public void publish(LogRecord record)
    {
        if (isLoggable(record))
        {
            print(getFormatter().format(record));
        }
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close() throws SecurityException
    {
    }

}
