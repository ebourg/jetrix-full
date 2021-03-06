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

package net.jetrix.commands;

import java.util.*;

import net.jetrix.messages.*;
import net.jetrix.*;

/**
 * Show the spectators in the channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpecListCommand implements Command
{
    public String[] getAliases()
    {
        return new String[] { "speclist", "slist" };
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/speclist";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.speclist.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        StringBuffer message = new StringBuffer();
        Iterator specators = client.getChannel().getSpectators();
        while (specators.hasNext())
        {
            Client spectator = (Client) specators.next();
            message.append(spectator.getUser().getName());
            if (specators.hasNext())
            {
                message.append(", ");
            }
        }

        PlineMessage response = new PlineMessage();
        response.setKey("command.speclist.format", message.toString());
        client.sendMessage(response);
    }
}
