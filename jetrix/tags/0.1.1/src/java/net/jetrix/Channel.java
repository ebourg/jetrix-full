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

package net.jetrix;

import java.util.*;
import java.util.logging.*;
import net.jetrix.config.*;
import net.jetrix.filter.*;
import net.jetrix.messages.*;

/**
 * Game channel
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Channel extends Thread implements Destination
{
    private ChannelConfig channelConfig;
    private ServerConfig serverConfig;
    private Logger logger = Logger.getLogger("net.jetrix");

    private MessageQueue mq;

    private boolean running = true;

    // game states
    public static final int GAME_STATE_STOPPED = 0;
    public static final int GAME_STATE_STARTED = 1;
    public static final int GAME_STATE_PAUSED  = 2;

    private int gameState;

    /** set of clients connected to this channel */
    private Set clients;

    /** slot/player mapping */
    private List slots;

    private ArrayList filters;

    // JetriX logo
    private short jetrixLogo[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0};

    public Channel()
    {
        this(new ChannelConfig());
    }

    public Channel(ChannelConfig channelConfig)
    {
        this.channelConfig = channelConfig;
        this.serverConfig  = Server.getInstance().getConfig();
        this.gameState = GAME_STATE_STOPPED;
        this.clients = new HashSet();
        this.slots = new ArrayList(6);

        // initialize the slot mapping
        for (int i = 0; i < 6; i++)
        {
            slots.add(null);
        }

        // opening channel message queue
        mq = new MessageQueue();

        filters = new ArrayList();

        /**
         * Loading filters
         */

        // global filters
        Iterator it = serverConfig.getGlobalFilters();
        while ( it.hasNext() ) { addFilter( (FilterConfig)it.next() ); }

        // channel filters
        it = channelConfig.getFilters();
        while ( it.hasNext() ) { addFilter( (FilterConfig)it.next() ); }
    }

    /**
     * Enable a new filter for this channel.
     */
    public void addFilter(FilterConfig filterConfig)
    {
        FilterManager filterManager = FilterManager.getInstance();
        MessageFilter filter;

        try
        {
            // getting filter instance
            if (filterConfig.getClassname() != null)
            {
                filter = filterManager.getFilter(filterConfig.getClassname());
            }
            else
            {
                filter = filterManager.getFilterByName(filterConfig.getName());
            }

            // initializing filter
            filter.setChannel(this);
            filter.init(filterConfig);

            // adding filter to the list
            filters.add(filter);

            logger.info("["+channelConfig.getName()+"] loaded filter " + filter.getName() + " " + filter.getVersion());
        }
        catch (FilterException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Main loop. The channel listens for incomming messages until the server
     * or the channel closes. Every message is first passed through the
     * registered filters and then handled by the channel.
     */
    public void run()
    {
        logger.info("Channel " + channelConfig.getName() + " opened");

        while (running && serverConfig.isRunning())
        {
            LinkedList l = new LinkedList();

            try
            {
                // waiting for new messages
                l.add( mq.get() );

                // filtering message
                Iterator it = filters.iterator();
                while ( it.hasNext() )
                {
                    MessageFilter filter = (MessageFilter)it.next();
                    int size = l.size();
                    for (int i = 0; i<size; i++) { filter.process( (Message)l.removeFirst(), l ); }
                }

                // processing message(s)
                while ( !l.isEmpty() ) { process( (Message)l.removeFirst() ); }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        logger.info("Channel " + channelConfig.getName() + " closed");
    }

    private void process(CommandMessage m)
    {
        // forwards the command to the server
        Server.getInstance().sendMessage(m);
    }

    private void process(TeamMessage m)
    {
        int slot = m.getSlot();
        getPlayer(slot).setTeam(m.getName());
        sendAll(m, slot);
    }

    private void process(GmsgMessage m)
    {
        sendAll(m);
    }

    private void process(PlineMessage m)
    {
        int slot = m.getSlot();
        String text = m.getText();
        if (!text.startsWith("/")) sendAll(m, slot);
    }

    private void process(PlineActMessage m)
    {
        int slot = m.getSlot();
        if (m.getSource() == null)
            // forged by the server, send to all
            sendAll(m);
        else
            sendAll(m, slot);
    }

    private void process(PauseMessage m)
    {
        gameState = GAME_STATE_PAUSED;
        sendAll(m);
    }

    private void process(ResumeMessage m)
    {
        gameState = GAME_STATE_STARTED;
        sendAll(m);
    }

    private void process(PlayerLostMessage m)
    {
        int slot = m.getSlot();
        Client client = getClient(slot);
        sendAll(m);

        // sending closing screen
        StringBuffer screenLayout = new StringBuffer();
        for (int i = 0; i < 12*22; i++)
        {
            screenLayout.append( ( (int)(Math.random() * 4 + 1) ) * (1 - jetrixLogo[i]) );
            //screenLayout.append( ( (int)(slot%5+1) ) * (1-jetrixLogo[i]) );
        }
        FieldMessage endingScreen = new FieldMessage();
        endingScreen.setSlot(m.getSlot());
        endingScreen.setField(screenLayout.toString());
        sendAll(endingScreen);

        boolean wasPlaying = client.getUser().isPlaying();
        client.getUser().setPlaying(false);

        // check for end of game
        if (wasPlaying && countRemainingTeams() <= 1)
        {
            gameState = Channel.GAME_STATE_STOPPED;
            Message endgame = new EndGameMessage();
            sendAll(endgame);
            
            // looking for the slot of the winner
            slot = 0;
            for (int i = 0; i < slots.size(); i++)
            {
                client = (Client)slots.get(i);

                if ( client != null && client.getUser().isPlaying() )
                {
                    slot = i + 1;
                }
            }
            
            // announcing the winner
            if (slot != 0)
            {
                PlayerWonMessage playerwon = new PlayerWonMessage();
                playerwon.setSlot(slot);
                sendAll(playerwon);
                
                User winner = getPlayer(slot);
                PlineMessage announce = new PlineMessage();
                if (winner.getTeam() == null)
                {
                    announce.setKey("channel.player_won", new Object[] { winner.getName() });
                }
                else
                {
                    announce.setKey("channel.team_won", new Object[] { winner.getTeam() });
                }
                sendAll(announce);
            }
        }
    }

    private void process(SpecialMessage m)
    {
        // specials are not forwarded in pure mode
        if ( channelConfig.getSettings().getLinesPerSpecial() >0 )
        {
            int slot = m.getFromSlot();
            sendAll(m, slot);
        }
    }

    private void process(LevelMessage m)
    {
        sendAll(m);
    }

    private void process(FieldMessage m)
    {
        int slot = m.getSlot();
        sendAll(m, slot);
    }

    private void process(StartGameMessage m)
    {
        if (gameState == GAME_STATE_STOPPED)
        {
            // change the channel state
            gameState = GAME_STATE_STARTED;

            // change the game state of the players
            Iterator it = slots.iterator();
            while (it.hasNext())
            {
                Client client = (Client)it.next();
                System.out.println(client);
                if (client != null)
                {
                    client.getUser().setPlaying(true);
                }
            }

            // send the newgame message
            NewGameMessage newgame = new NewGameMessage();
            newgame.setSlot(m.getSlot());
            newgame.setSettings(channelConfig.getSettings());
            sendAll(newgame);
        }
    }

    private void process(StopGameMessage m)
    {
        if (gameState != GAME_STATE_STOPPED)
        {
            gameState = GAME_STATE_STOPPED;
            EndGameMessage end = new EndGameMessage();
            end.setSlot(m.getSlot());
            sendAll(end);
        }
    }

    private void process(EndGameMessage m)
    {
        if (gameState != GAME_STATE_STOPPED)
        {
            gameState = GAME_STATE_STOPPED;
            sendAll(m);
        }
    }

    private void process(DisconnectedMessage m)
    {
        Client client = m.getClient();
        removeClient(client);

        PlineMessage disconnected = new PlineMessage();
        disconnected.setKey("channel.disconnected", new Object[] { client.getUser().getName() });
        sendAll(disconnected);
    }

    /*private void process(LeaveMessage m)
    {
        int slot = m.getSlot();
        players.set(slot - 1, null);
        sendAll(m);
    }*/

    /**
     * Remove the specified client from the channel.
     */
    public void removeClient(Client client)
    {
        if (client != null)
        {
            clients.remove(client);
            int slot = slots.indexOf(client);

            if (slot != -1)
            {
                slots.set(slot, null);
                LeaveMessage leave = new LeaveMessage();
                leave.setSlot(slot + 1);
                sendAll(leave);
            }
            else
            {
                PlineMessage message = new PlineMessage();
                message.setText("<green>*** <b>" + client.getUser().getName() + "</b> is no longer watching");
                sendAll(message);
            }
        }

        // stop the game if the channel is now empty
        if (isEmpty() && running) { gameState = GAME_STATE_STOPPED; }
    }

    private void process(AddPlayerMessage m)
    {
        Client client = m.getClient();

        Channel previousChannel = client.getChannel();
        client.setChannel(this);

        // remove the client from the previous channel
        if (previousChannel != null)
        {
            // clear the player list
            for (int j = 1; j <= 6; j++)
            {
                if (previousChannel.getPlayer(j) != null)
                {
                    LeaveMessage clear = new LeaveMessage();
                    clear.setSlot(j);
                    client.sendMessage(clear);
                }
            }

            previousChannel.removeClient(client);

            // send a message to the previous channel announcing what channel the player joined
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.join_notice", new Object[] { client.getUser().getName(), channelConfig.getName() });
            previousChannel.sendMessage(announce);

            // clear the game status of the player
            if (client.getUser().isPlaying())
            {
                client.sendMessage(new EndGameMessage());
            }
        }

        // add the client to the channel
        clients.add(client);

        if (client.getUser().isSpectator())
        {
            // announce the new spectator to the other clients in the channel
            PlineMessage announce = new PlineMessage();
            announce.setText("<green>*** <b>" + client.getUser().getName() + "</b> is Now Watching");
            sendAll(announce, client);
        }
        else
        {
            // looking for the first free slot
            int slot = 0;
            for (slot = 0; slot < slots.size() && slots.get(slot) != null; slot++);

            if (slot >= 6)
            {
                logger.warning("[" + getConfig().getName() + "] Panic, no slot available for " + client);
                client.getUser().setSpectator();
            }
            else
            {
                slots.set(slot, client);

                // announce the new player to the other clients in the channel
                JoinMessage mjoin = new JoinMessage();
                mjoin.setSlot(slot + 1);
                mjoin.setName(client.getUser().getName());
                sendAll(mjoin, client);

                // send the slot number assigned to the new player
                PlayerNumMessage mnum = new PlayerNumMessage();
                mnum.setSlot(slot + 1);
                client.sendMessage(mnum);
            }
        }

        // send the list of players and spectators
        for (int i = 0; i < slots.size(); i++)
        {
            Client resident = (Client)slots.get(i);
            if (resident != null && resident != client)
            {
                // players...
                JoinMessage mjoin2 = new JoinMessage();
                mjoin2.setSlot(i + 1);
                mjoin2.setName(resident.getUser().getName()); // NPE
                client.sendMessage(mjoin2);

                // ...and teams
                TeamMessage mteam = new TeamMessage();
                mteam.setSlot(i + 1);
                mteam.setName(resident.getUser().getTeam());
                client.sendMessage(mteam);
            }
        }

        // send a welcome message to the incomming client
        PlineMessage mwelcome = new PlineMessage();
        mwelcome.setKey("channel.welcome", new Object[] { client.getUser().getName(), channelConfig.getName() });
        client.sendMessage(mwelcome);

        // send the status of the game to the new client
        if (gameState != GAME_STATE_STOPPED)
        {
            client.sendMessage(new IngameMessage());

            // tell the player if the game is currently paused
            if (gameState == GAME_STATE_PAUSED)
            {
                client.sendMessage(new PauseMessage());
            }
        }

    }

    public void process(Message m)
    {
        logger.finest("[" + channelConfig.getName() + "] Processing " +  m);

        if ( m instanceof CommandMessage) process((CommandMessage)m);
        else if ( m instanceof TeamMessage) process((TeamMessage)m);
        else if ( m instanceof GmsgMessage) process((GmsgMessage)m);
        else if ( m instanceof PlineMessage) process((PlineMessage)m);
        else if ( m instanceof PlineActMessage) process((PlineActMessage)m);
        else if ( m instanceof PauseMessage) process((PauseMessage)m);
        else if ( m instanceof ResumeMessage) process((ResumeMessage)m);
        else if ( m instanceof PlayerLostMessage) process((PlayerLostMessage)m);
        else if ( m instanceof SpecialMessage) process((SpecialMessage)m);
        else if ( m instanceof LevelMessage) process((LevelMessage)m);
        else if ( m instanceof FieldMessage) process((FieldMessage)m);
        else if ( m instanceof StartGameMessage) process((StartGameMessage)m);
        else if ( m instanceof StopGameMessage) process((StopGameMessage)m);
        else if ( m instanceof EndGameMessage) process((EndGameMessage)m);
        else if ( m instanceof DisconnectedMessage) process((DisconnectedMessage)m);
        //else if ( m instanceof LeaveMessage) process((LeaveMessage)m);
        else if ( m instanceof AddPlayerMessage) process((AddPlayerMessage)m);
        else
        {
            logger.finest("[" + channelConfig.getName() + "] Message not processed " +  m);
        }
    }

    /**
     * Add a message to the channel MessageQueue.
     *
     * @param m message to add
     */
    public void sendMessage(Message m)
    {
        mq.put(m);
    }

    /**
     * Send a message to all players in this channel.
     *
     * @param m       the message to send
     */
    private void sendAll(Message m)
    {
        // @todo add a fast mode to iterate on players only for game messages
        Iterator it = clients.iterator();
        while (it.hasNext())
        {
            Client client = (Client)it.next();
            client.sendMessage(m);
        }
    }

    /**
     * Send a message to all players but the one in the specified slot.
     *
     * @param m       the message to send
     * @param slot    the slot to exclude
     */
    private void sendAll(Message m, int slot)
    {
        Client client = getClient(slot);
        sendAll(m, client);
    }

    /**
     * Send a message to all players but the specified client.
     *
     * @param m       the message to send
     * @param c       the client to exclude
     */
    private void sendAll(Message m, Client c)
    {
        // @todo add a fast mode to iterate on players only for game messages
        Iterator it = clients.iterator();
        while (it.hasNext())
        {
            Client client = (Client)it.next();
            if (client != c)
            {
                client.sendMessage(m);
            }
        }
    }

    /**
     * Tell if the channel can accept more players.
     *
     * @return <tt>true</tt> if the channel is full, <tt>false</tt> if not
     */
    public boolean isFull()
    {
        return getPlayerCount() >= channelConfig.getMaxPlayers();
    }

    public boolean isEmpty()
    {
        return getPlayerCount() == 0;
    }

    /**
     * Returns the number of players currently in this chanel.
     *
     * @return player count
     */
    public int getPlayerCount()
    {
        int count = 0;

        for (int i = 0; i < slots.size(); i++)
        {
            if (slots.get(i) != null)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the channel configuration.
     */
    public ChannelConfig getConfig()
    {
        return channelConfig;
    }

    /**
     * Returns the game state.
     */
    public int getGameState()
    {
        return gameState;
    }

    /**
     * Finds the slot used in the channel by the specified client.
     */
    public int getClientSlot(Client client)
    {
        return (slots.indexOf(client) + 1);
    }

    /**
     * Returns the client in the specified slot.
     *
     * @param slot slot number between 1 and 6
     *
     * @return <tt>null</tt> if there is no client in the specified slot, or if the number is out of range
     */
    public Client getClient(int slot)
    {
        Client client = null;

        if (slot >= 1 && slot <= slots.size() )
        {
            client = (Client)slots.get(slot - 1);
        }

        return client;
    }

    /**
     * Returns the client in the specified slot.
     *
     * @param slot slot number between 1 and 6
     *
     * @return <tt>null</tt> if there is no client in the specified slot, or if the number is out of range
     */
    public User getPlayer(int slot)
    {
        Client client = getClient(slot);
        return (client != null) ? client.getUser() : null;
    }

    /**
     * Return an iterator of players in this channel.
     */
    public Iterator getPlayers()
    {
        return slots.iterator();
    }

    /**
     * Return an iterator of spectators observing this channel.
     */
    /*public Iterator getSpectators()
    {
        return spectators.iterator();
    }*/

    /**
     * Count how many teams are still fighting for victory. A teamless player
     * is considered as a separate team. The game ends when there is one team
     * left in game OR when the last player loose if only one team took part
     * in the game.
     *
     * @return number of teams still playing
     */
    private int countRemainingTeams()
    {
        Map playingTeams = new HashMap();

        int nbTeamsLeft = 0;

        for (int i = 0; i < slots.size(); i++)
        {
            Client client = (Client)slots.get(i);

            if ( client != null && client.getUser().isPlaying() )
            {
                String team = client.getUser().getTeam();

                if (team == null)
                {
                    nbTeamsLeft++;
                }
                else
                {
                    playingTeams.put(team, team);
                }
            }
        }

        return nbTeamsLeft + playingTeams.size();
    }

}
