/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

package net.jetrix.config;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.digester.*;
import org.xml.sax.*;

import net.jetrix.*;
import net.jetrix.winlist.*;
import net.jetrix.filter.*;
import net.jetrix.commands.*;

/**
 * Server configuration. This objet reads and retains server parameters,
 * channel definitions and the ban list.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerConfig
{
    public static final String ENCODING = "ISO-8859-1";

    private String name;
    private InetAddress host;
    private int timeout;
    private int maxChannels;
    private int maxPlayers;
    private int maxConnections;
    private String oppass;
    private String accesslogPath;
    private String errorlogPath;
    private String motd;
    private Locale locale;

    // private List bans;
    private List<ChannelConfig> channels;
    private List<FilterConfig> globalFilters;
    private List<Listener> listeners;
    private List<Service> services;
    private boolean running;
    private int status;

    public static final int STATUS_OPENED = 0;
    public static final int STATUS_LOCKED = 1;

    public static final String VERSION = "@version@";

    private static Logger log = Logger.getLogger("net.jetrix");

    /**
     * Constructor declaration
     */
    public ServerConfig()
    {
        channels = new ArrayList<ChannelConfig>();
        globalFilters = new ArrayList<FilterConfig>();
        listeners = new ArrayList<Listener>();
        services = new ArrayList<Service>();
    }

    /**
     * Load the configuration.
     */
    public void load()
    {
        try
        {
            Digester digester = new Digester();

            // register the Jetrix server configuration file DTD
            URL url = findResource("tetrinet-server.dtd");
            digester.register("-//LFJR//Jetrix TetriNET Server//EN", url.toString());

            // register the Jetrix channels configuration file DTD
            url = findResource("tetrinet-channels.dtd");
            digester.register("-//LFJR//Jetrix Channels//EN", url.toString());

            // enable the document validation
            digester.setValidating(true);

            // add the rule sets
            digester.addRuleSet(new ServerRuleSet());
            digester.addRuleSet(new ChannelsRuleSet());

            // parse the server configuration
            digester.push(this);
            Reader reader = new InputStreamReader(findResource("server.xml").openStream(), ENCODING);
            digester.parse(new InputSource(reader));
            reader.close();

            // parse the channel configuration
            digester.push(this);
            reader = new InputStreamReader(findResource("channels.xml").openStream(), ENCODING);
            digester.parse(new InputSource(reader));
            reader.close();
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Locate the specified resource by searching in the classpath and in
     * the current directory.
     *
     * @param name the name of the resource
     * @return the URL of the resource, or null if it cannot be found
     * @since 0.1.4
     */
    private URL findResource(String name) throws MalformedURLException
    {
        ClassLoader loader = ServerConfig.class.getClassLoader();
        URL url = loader.getResource(name);

        if (url == null)
        {
            File file = new File(name);
            url = file.toURL();
        }

        return url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public InetAddress getHost()
    {
        return host;
    }

    public void setHost(InetAddress host)
    {
        this.host = host;
    }

    public void setHost(String host)
    {
        // a value of "[ALL]" stands for any IP
        if (!"[ALL]".equals(host))
        {
            try
            {
                this.host = InetAddress.getByName(host);
            }
            catch (UnknownHostException e)
            {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public void setLocale(String language)
    {
        this.locale = new Locale(language);
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getMaxChannels()
    {
        return maxChannels;
    }

    public void setMaxChannels(int maxChannels)
    {
        this.maxChannels = maxChannels;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections)
    {
        this.maxConnections = maxConnections;
    }

    public String getOpPassword()
    {
        return oppass;
    }

    public void setOpPassword(String oppass)
    {
        this.oppass = oppass;
    }

    public String getAccessLogPath()
    {
        return accesslogPath;
    }

    public void setAccessLogPath(String accesslogPath)
    {
        this.accesslogPath = accesslogPath;
    }

    public String getErrorLogPath()
    {
        return errorlogPath;
    }

    public void setErrorLogPath(String errorlogPath)
    {
        this.errorlogPath = errorlogPath;
    }

    public String getMessageOfTheDay()
    {
        return motd;
    }

    public void setMessageOfTheDay(String motd)
    {
        this.motd = motd;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Settings getDefaultSettings()
    {
        return Settings.getDefaultSettings();
    }

    public void setDefaultSettings(Settings defaultSettings)
    {
        Settings.setDefaultSettings(defaultSettings);
    }

    public List<ChannelConfig> getChannels()
    {
        return channels;
    }

    public void addChannel(ChannelConfig cconf)
    {
        channels.add(cconf);
    }

    public Iterator<FilterConfig> getGlobalFilters()
    {
        return globalFilters.iterator();
    }

    public void addFilter(FilterConfig fconf)
    {
        globalFilters.add(fconf);
    }

    public void addFilterAlias(String name, String classname)
    {
        FilterManager.getInstance().addFilterAlias(name, classname);
    }

    public void addWinlist(WinlistConfig config)
    {
        WinlistManager.getInstance().addWinlist(config);
    }

    public void addCommand(Command command)
    {
        CommandManager.getInstance().addCommand(command);
    }

    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    public List<Listener> getListeners()
    {
        return listeners;
    }

    /**
     * Add a new service to the server. The service is not started
     * automatically by calling this method.
     *
     * @since 0.1.4
     *
     * @param service the service to add
     */
    public void addService(Service service)
    {
        services.add(service);
    }

    /**
     * Return the list of services currently registered on the server
     *
     * @since 0.1.4
     */
    public List<Service> getServices()
    {
        return services;
    }

    public void addBannedHost(String host)
    {
        Banlist.getInstance().ban(host);
    }

}
