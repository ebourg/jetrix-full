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

package net.jetrix.monitor.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import net.jetrix.monitor.ServerInfo;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerInfoDao extends HibernateDaoSupport
{
    public ServerInfo getServer(long id)
    {
        return (ServerInfo) getSession().get(ServerInfo.class, id);
    }

    public ServerInfo getServer(String hostname)
    {
        Query query = getSession().createQuery("FROM ServerInfo WHERE hostname = :hostname");
        query.setParameter("hostname", hostname);

        return (ServerInfo) query.uniqueResult();
    }

    public List<ServerInfo> getServers()
    {
        return getSession().createQuery("FROM ServerInfo ORDER BY stats.playerCount DESC, hostname").list();
    }

    public void save(ServerInfo server)
    {
        getSession().saveOrUpdate(server);
    }

    public void remove(long id)
    {
        Object server = getSession().load(ServerInfo.class, id);
        getSession().delete(server);
    }

    /**
     * Check if the hostname or IP address of the specified server already exist in the database.
     *
     * @param server
     */
    public boolean exists(ServerInfo server)
    {
        Query query = getSession().createQuery("FROM ServerInfo WHERE IP = :ip OR hostname = :name OR :ip IN elements(aliases)");
        query.setParameter("ip", server.getIP());
        query.setParameter("name", server.getHostname());
        List result = query.list();

        return !result.isEmpty();
    }
}
