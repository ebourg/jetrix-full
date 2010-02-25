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

package net.jetrix.monitor.job;

import java.util.List;

import net.jetrix.monitor.ServerInfo;
import net.jetrix.monitor.ServerStats;
import net.jetrix.monitor.SpringTestCase;
import net.jetrix.monitor.dao.ServerInfoDao;
import net.jetrix.monitor.dao.ServerStatsDao;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GraphGenerationJobTest extends SpringTestCase
{
    public void testGenerateGraph() throws Exception
    {
        ServerInfoDao serverInfoDao = (ServerInfoDao) context.getBean("serverInfoDao");
        ServerStatsDao serverStatsDao = (ServerStatsDao) context.getBean("serverStatsDao");

        ServerInfo server = serverInfoDao.getServer("jetrix.tetrinet.fr");
        assertNotNull("server not found", server);

        List<ServerStats> stats = serverStatsDao.getStats(server.getId());

        GraphGenerationJob job = new GraphGenerationJob();

        job.generateGraph(server, stats);
    }
}
