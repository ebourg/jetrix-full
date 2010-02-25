/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.jetrix.monitor;

import java.util.Date;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PlayerStats
{
    private String name;
    private String team;
    private String channel;
    private Date firstSeen;
    private Date lastSeen;
    private Date lastPlayed;
    private ServerInfo lastServer;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTeam()
    {
        return team;
    }

    public void setTeam(String team)
    {
        this.team = team;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public Date getFirstSeen()
    {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen)
    {
        this.firstSeen = firstSeen;
    }

    public Date getLastSeen()
    {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public Date getLastPlayed()
    {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed)
    {
        this.lastPlayed = lastPlayed;
    }

    public ServerInfo getLastServer()
    {
        return lastServer;
    }

    public void setLastServer(ServerInfo lastServer)
    {
        this.lastServer = lastServer;
    }

    /**
     * Tells if the player has been active in the last 15 minutes.
     */
    public boolean isActive()
    {
        return lastPlayed != null && lastPlayed.getTime() > System.currentTimeMillis() - 15 * 60 * 1000;
    }
}
