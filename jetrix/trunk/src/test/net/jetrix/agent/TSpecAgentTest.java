/**
 * Jetrix TetriNET Server
 * Copyright (C) 2005  Emmanuel Bourg
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

package net.jetrix.agent;

import junit.framework.TestCase;
import net.jetrix.messages.channel.PlineMessage;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TSpecAgentTest extends TestCase
{
    public void testConnect() throws Exception
    {
        TSpecAgent agent = new TSpecAgent("JetrixSpec", "rien");
        agent.connect("tetrinet.fr");

        agent.send(new PlineMessage("// Hi!"));
        agent.send(new PlineMessage("Hello there!"));

        Thread.sleep(1000);

        agent.disconnect();
    }
}
