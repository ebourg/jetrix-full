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

import net.jetrix.monitor.ServerStats;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerStatsDao extends HibernateDaoSupport
{    
    public List<ServerStats> getStats(long serverId)
    {
        Query query = getSession().createQuery("FROM ServerStats WHERE serverId=:id ORDER BY date");
        query.setParameter("id", serverId);

        return (List<ServerStats>) query.list();
    }

    public void save(ServerStats stats)
    {
        getSession().saveOrUpdate(stats);
    }
}
