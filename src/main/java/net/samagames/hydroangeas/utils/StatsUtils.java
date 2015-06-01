package net.samagames.hydroangeas.utils;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import redis.clients.jedis.Jedis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StatsUtils
{
    public static void newServer(MinecraftServerInfos serverInfos)
    {
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(now);

        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int todayServerCount = 1;

        if(jedis.hexists("hydroangeas:stats:servers", formatter.format(now)))
            todayServerCount = Integer.valueOf(jedis.hget("hydroangeas:stats:servers", formatter.format(now))) + 1;

        int todayGameCount = 1;

        if(jedis.hexists("hydroangeas:stats:games:" + serverInfos.getGame(), formatter.format(now)))
            todayGameCount = Integer.valueOf(jedis.hget("hydroangeas:stats:games:" + serverInfos.getGame(), formatter.format(now))) + 1;

        jedis.hset("hydroangeas:stats:servers", formatter.format(now), String.valueOf(todayServerCount));
        jedis.hset("hydroangeas:stats:games:" + serverInfos.getGame(), formatter.format(now), String.valueOf(todayGameCount));
        jedis.close();
    }

    public static int getTodayServersStats() throws ParseException
    {
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());

        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int count = 0;

        for(String date : jedis.hgetAll("hydroangeas:stats:servers").keySet())
        {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            dateCalendar.setTime(formatter.parse(date));

            if(calendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH))
                count++;
        }

        jedis.close();

        return count;
    }

    public static HashMap<Integer, Integer> getWeeklyServersStats() throws ParseException
    {
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());

        HashMap<Integer, Integer> stats = new HashMap<>();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int firstDayOfWeekInMonth = calendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_WEEK);


        for(String date : jedis.hgetAll("hydroangeas:stats:servers").keySet())
        {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            dateCalendar.setTime(formatter.parse(date));

            if(dateCalendar.get(Calendar.DAY_OF_MONTH) >= firstDayOfWeekInMonth && dateCalendar.get(Calendar.DAY_OF_MONTH) <= (firstDayOfWeekInMonth + 7))
            {
                if(dateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))
                {
                    if(dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                    {
                        int toSet = 1;

                        if(stats.containsKey(dateCalendar.get(Calendar.DAY_OF_WEEK)))
                            toSet = stats.get(dateCalendar.get(Calendar.DAY_OF_WEEK)) + 1;

                        stats.put(dateCalendar.get(Calendar.DAY_OF_WEEK), toSet);
                    }
                }
            }
        }

        for(int i = 1; i <= 7; i++)
            if(!stats.containsKey(i))
                stats.put(i, 0);

        return stats;
    }

    public static HashMap<String, Integer> getWeeklyGamesStats() throws ParseException
    {
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());

        HashMap<String, Integer> stats = new HashMap<>();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int firstDayOfWeekInMonth = calendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_WEEK);


        for(String key : jedis.keys("hydroangeas:stats:games:*"))
        {
            String gameName = key.split(":")[3];

            for(String date : jedis.hgetAll(key).keySet())
            {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                dateCalendar.setTime(formatter.parse(date));

                if (dateCalendar.get(Calendar.DAY_OF_MONTH) >= firstDayOfWeekInMonth && dateCalendar.get(Calendar.DAY_OF_MONTH) <= (firstDayOfWeekInMonth + 7))
                {
                    if (dateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))
                    {
                        if (dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                        {
                            int toSet = 1;

                            if (stats.containsKey(gameName))
                                toSet = stats.get(gameName) + 1;

                            stats.put(gameName, toSet);
                        }
                    }
                }
            }
        }

        return stats;
    }
}
