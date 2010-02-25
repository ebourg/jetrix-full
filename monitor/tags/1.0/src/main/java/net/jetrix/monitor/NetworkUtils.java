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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class NetworkUtils
{
    public static boolean isPortOpen(String address, int port)
    {
        try
        {
            return isPortOpen(InetAddress.getByName(address), port);
        }
        catch (UnknownHostException e)
        {
            return false;
        }
    }

    /**
     * Check if the specified port is open.
     *
     * @param address
     * @param port
     */
    public static boolean isPortOpen(InetAddress address, int port)
    {
        Socket socket = new Socket();

        try
        {
            socket.connect(new InetSocketAddress(address, port), 5000);
            socket.close();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
