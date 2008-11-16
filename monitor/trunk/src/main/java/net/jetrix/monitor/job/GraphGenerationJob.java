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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletContext;

import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import net.jetrix.monitor.ServerInfo;
import net.jetrix.monitor.ServerStats;
import net.jetrix.monitor.dao.ServerInfoDao;
import net.jetrix.monitor.dao.ServerStatsDao;

/**
 * Job updating the graphs.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GraphGenerationJob extends TransactionalQuartzJob
{
    public static final String DEFAULT_DATAPATH = System.getProperty("user.home") + "/.jetrix";

    /** The directory where the RRD files are stored. */
    private String dataPath = DEFAULT_DATAPATH;
    
    /** The directory where the images are generated. */
    private String outpoutPath = DEFAULT_DATAPATH;

    public void setDataPath(String dataPath)
    {
        this.dataPath = dataPath;
    }

    public void setOutpoutPath(String outpoutPath)
    {
        this.outpoutPath = outpoutPath;
    }

    protected void executeTransactional(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        log.info("Generating graphs...");

        ApplicationContext context = (ApplicationContext) jobExecutionContext.getMergedJobDataMap().get("applicationContext");

        ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        setOutpoutPath(servletContext.getRealPath("/images/graphs"));

        ServerInfoDao serverInfoDao = (ServerInfoDao) context.getBean("serverInfoDao");
        ServerStatsDao serverStatsDao = (ServerStatsDao) context.getBean("serverStatsDao");

        List<ServerInfo> servers = serverInfoDao.getServers();

        for (ServerInfo server : servers)
        {
            try
            {
                List<ServerStats> stats = serverStatsDao.getStats(server.getId());
                generateGraph(server, stats);

                /**
                 * Remove the stats from the SQL database
                 */
                if (!stats.isEmpty())
                {
                    ServerStats lastStats = stats.get(stats.size() - 1);
                    serverStatsDao.delete(server.getId(), lastStats.getDate());
                }
            }
            catch (Exception e)
            {
                log.log(Level.WARNING, "Unable to draw the activity graph for " + server.getHostname() + " (" + server.getId() + ")", e);
            }
        }
    }

    private File getRRDFile(ServerInfo server)
    {
        return new File(dataPath, "server-" + server.getId() + ".rrd");
    }

    protected void generateGraph(ServerInfo server, List<ServerStats> stats) throws RrdException, IOException
    {
        boolean update= updateDatabase(server, stats);
        if (update)
        {
            generateGraph(server, 1);
        }
    }

    private boolean updateDatabase(ServerInfo server, List<ServerStats> stats) throws IOException, RrdException
    {
        if (stats.isEmpty())
        {
            return false;
        }

        File file = getRRDFile(server);
        if (!file.exists())
        {
            createDatabase(server, stats.get(0).getDate());
        }

        RrdDb rrdDb = new RrdDb(file.getAbsolutePath());

        try
        {
            Sample sample = rrdDb.createSample();
            for (ServerStats stat : stats)
            {
                long timestamp = Util.getTimestamp(stat.getDate());
                if (timestamp > rrdDb.getLastUpdateTime())
                {
                    sample.setTime(timestamp);
                    sample.setValue("players", stat.getPlayerCount());
                    sample.setValue("activePlayers", stat.getActivePlayerCount());
                    sample.update();
                }
            }
        }
        finally
        {
            rrdDb.close();
        }

        return true;
    }

    private void createDatabase(ServerInfo server, Date startDate) throws IOException, RrdException
    {
        File file = getRRDFile(server);
        file.getParentFile().mkdirs();

        RrdDef def = new RrdDef(file.getAbsolutePath());
        def.setStartTime(Util.getTimestamp(startDate) - 1);
        def.setStep(300);

        def.addDatasource("players", "GAUGE", 600, 0, Double.NaN);
        def.addDatasource("activePlayers", "GAUGE", 600, 0, Double.NaN);
        def.addArchive("AVERAGE", 0.5, 1, 500000);

        RrdDb rrdDb = new RrdDb(def);
        rrdDb.close();
    }

    private void generateGraph(ServerInfo server, int days) throws IOException, RrdException
    {
        RrdGraphDef graph = new RrdGraphDef();

        // scale
        graph.setStartTime(System.currentTimeMillis() / 1000 - days * 24 * 3600);
        graph.setEndTime(System.currentTimeMillis() / 1000);
        graph.setMinValue(0);
        if (server.getMaxPlayerCount() < 10)
        {
            graph.setValueAxis(1, 1);
            graph.setMaxValue(10);
        }

        // data
        File file = getRRDFile(server);
        graph.datasource("players", file.getAbsolutePath(), "players", "AVERAGE");
        graph.datasource("activePlayers", file.getAbsolutePath(), "activePlayers", "AVERAGE");
        graph.area("players", new Color(141, 255, 141), "Players");
        graph.area("activePlayers", Color.GREEN, "Active Players");
        graph.hrule(server.getMaxPlayerCount(), Color.RED, "Max players");

        // style
        //graph.setTitle(server.getHostname().toUpperCase());
        graph.setVerticalLabel("Players");
        graph.setUnitsLength(7);
        graph.setAntiAliasing(true);
        graph.setShowSignature(false);
        graph.setSignature(server.getHostname());
        graph.setNoLegend(true);
        graph.setColor(RrdGraph.COLOR_SHADEA, Color.WHITE);
        graph.setColor(RrdGraph.COLOR_SHADEB, Color.WHITE);
        graph.setColor(RrdGraph.COLOR_BACK, Color.WHITE);
        graph.setColor(RrdGraph.COLOR_ARROW, Color.WHITE);

        // output
        File gfile = new File(outpoutPath, "server-" + server.getId() + ".png");
        gfile.getParentFile().mkdirs();
        graph.setWidth(400);
        graph.setHeight(300);
        graph.setFilename(gfile.getAbsolutePath());
        graph.setImageFormat("png");
        graph.setPoolUsed(false);

        new RrdGraph(graph);

        log.fine("Done generating " + gfile);
    }
}
