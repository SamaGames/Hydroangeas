package net.samagames.hydroangeas.client.resources;

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
public class ServerDependency
{
    private String name;
    private String version;
    private String type;
    private String ext;

    public String getName()
    {
        return this.name;
    }

    public String getVersion()
    {
        return this.version;
    }


    public String getType()
    {
        if (type == null)
            return "plugin";
        return type;
    }

    public String getExt()
    {
        if (ext == null)
            return "jar";
        return ext;
    }

    public boolean isExtractable()
    {
        return ext != null && !ext.equals("jar");
    }
}
