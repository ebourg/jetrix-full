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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import net.jetrix.agent.QueryAgent;
import net.jetrix.agent.QueryInfo;
import net.jetrix.agent.PlayerInfo;
import net.jetrix.monitor.NetworkUtils;
import net.jetrix.monitor.ServerInfo;
import net.jetrix.monitor.ServerStats;
import net.jetrix.monitor.PlayerStats;
import net.jetrix.monitor.dao.ServerInfoDao;
import net.jetrix.monitor.dao.ServerStatsDao;
import net.jetrix.monitor.dao.PlayerStatsDao;

/**
 * Job polling the tetrinet servers.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerSurveyJob extends TransactionalQuartzJob
{
    protected void executeTransactional(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        ApplicationContext context = (ApplicationContext) jobExecutionContext.getMergedJobDataMap().get("applicationContext");
        ServerInfoDao serverInfoDao = (ServerInfoDao) context.getBean("serverInfoDao");
        ServerStatsDao serverStatsDao = (ServerStatsDao) context.getBean("serverStatsDao");
        PlayerStatsDao playerStatsDao = (PlayerStatsDao) context.getBean("playerStatsDao");

        List<ServerInfo> servers = serverInfoDao.getServers();

        log.info("Checking servers... ");

        List<Callable<ServerInfo>> workers = new ArrayList<Callable<ServerInfo>>();

        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (ServerInfo server : servers)
        {
            workers.add(new Worker(server));
        }

        try
        {
            List<Future<ServerInfo>> results = executor.invokeAll(workers, 45, TimeUnit.SECONDS);
            executor.shutdown();

            for (Future<ServerInfo> result : results)
            {
                try
                {
                    ServerInfo server = result.get();
                    if (!server.isOnline())
                    {
                        server.getPlayers().clear();
                    }
                    serverInfoDao.save(server);

                    // update the server stats for the activity graph
                    ServerStats stats = server.getStats();
                    stats.setServerId(server.getId());
                    stats.setDate(server.getLastChecked());
                    if (stats.getPlayerCount() > 0)
                    {
                        serverStatsDao.save(stats);
                    }

                    // update the player stats
                    for (PlayerInfo player : server.getPlayers())
                    {
                        PlayerStats playerStats = playerStatsDao.getStats(player.getNick());
                        if (playerStats == null)
                        {
                            playerStats = new PlayerStats();
                            playerStats.setName(player.getNick());
                            playerStats.setFirstSeen(server.getLastOnline());
                        }

                        playerStats.setTeam(player.getTeam());
                        playerStats.setLastSeen(server.getLastOnline());
                        playerStats.setLastServer(server);
                        if (player.isPlaying())
                        {
                            playerStats.setLastPlayed(server.getLastOnline());
                        }

                        playerStatsDao.save(playerStats);
                    }
                }
                catch (ExecutionException e)
                {
                    log.log(Level.WARNING, "Error when checking the server", e);
                }
                catch (CancellationException e)
                {
                    log.log(Level.WARNING, "Server survey task cancelled", e);
                }
            }
        }
        catch (Exception e)
        {
            throw new JobExecutionException(e);
        }
    }

    private class Worker implements Callable<ServerInfo>
    {
        private ServerInfo server;

        public Worker(ServerInfo server)
        {
            this.server = server;
        }

        public ServerInfo call() throws Exception
        {
            QueryAgent agent = new QueryAgent();
            server.setLastChecked(new Date());
            server.setStats(new ServerStats());

            try
            {
                agent.connect(server.getHostname());

                QueryInfo info = agent.getInfo();

                server.getStats().update(info);
                server.setVersion(info.getVersion());
                server.setLastOnline(server.getLastChecked());
                
                if (server.getChannels() == null)
                {
                    server.setChannels(info.getChannels());
                }
                else
                {
                    server.getChannels().clear();
                    server.getChannels().addAll(info.getChannels());
                }
                
                server.setPlayers(info.getPlayers());

                if (server.getStats().getActivePlayerCount() > server.getMaxActivePlayerCount())
                {
                    server.setMaxActivePlayerCount(server.getStats().getActivePlayerCount());
                    server.setMaxActivePlayerDate(server.getLastChecked());
                }
                if (server.getStats().getPlayerCount() > server.getMaxPlayerCount())
                {
                    server.setMaxPlayerCount(server.getStats().getPlayerCount());
                    server.setMaxPlayerDate(server.getLastChecked());
                }

                server.setSpectate(NetworkUtils.isPortOpen(server.getHostname(), 31458));
            }
            catch (Exception e)
            {
                log.info("Unable to check the server " + server.getHostname() + " : " + e.getMessage());
            }
            finally
            {
                agent.disconnect();
            }

            return server;
        }
    }
}
