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

package  net.jetrix.config;

import java.net.URL;

import junit.framework.*;
import net.jetrix.ChannelManager;

/**
 * JUnit TestCase for the class net.jetrix.config.ServerConfig
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerConfigTest extends TestCase
{
    private URL serverConfigURL = getClass().getResource("/conf/server.xml");

    public void testGetInstance()
    {
        try
        {
            ServerConfig config = new ServerConfig();
            config.load(serverConfigURL);
        }
        catch (Throwable e)
        {
            fail(e.getMessage());
        }
    }

    public void testSave() throws Exception
    {
        // load...
        ServerConfig config = new ServerConfig();
        config.load(serverConfigURL);

        // create the channels
        for (ChannelConfig cc : config.getChannels())
        {
            cc.setPersistent(true);
            ChannelManager.getInstance().createChannel(cc, false);
        }

        // save...
        config.save();

        // and load again !
        ServerConfig config2 = new ServerConfig();
        config2.load(serverConfigURL);
    }

}
