/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Send a private message to a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TellCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "tell", "msg", "cmsg", "send" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/tell <playername|playernum> <message>";
    }

    public String getDescription()
    {
        return "Send a private message to a player.";
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 2)
        {
            String targetName = m.getParameter(0);
            Client target = null;

            // checking if the second parameter is a slot number
            try
            {
                int slot = Integer.parseInt(targetName);
                if (slot >= 1 && slot <= 6)
                {
                    Channel channel = client.getChannel();
                    target = channel.getPlayer(slot);
                }
            }
            catch (NumberFormatException e) {}

            if (target == null)
            {
                // target is still null, the second parameter is a playername
                ClientRepository repository = ClientRepository.getInstance();
                target = repository.getClient(targetName);
            }

            if (target == null)
            {
                // no player found
                PlineMessage reponse = new PlineMessage();
                String message = Color.red + "Player " + targetName + " cannot be found on the server.";
                reponse.setText(message);
                client.sendMessage(reponse);
            }
            else
            {
                // player found
                PlineMessage reponse = new PlineMessage();
                String privateMessage = m.getText().substring(targetName.length() + 1);
                String message = Color.aqua + "{" + client.getPlayer().getName() + "} " + Color.darkBlue + privateMessage;
                reponse.setText(message);
                target.sendMessage(reponse);
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            String message = Color.red + cmd + Color.blue + " <playername|playernumber> <message>";
            response.setText(message);
            client.sendMessage(response);
        }
    }

}
