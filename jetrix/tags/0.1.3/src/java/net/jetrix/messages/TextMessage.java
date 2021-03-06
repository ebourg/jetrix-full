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

package net.jetrix.messages;

import net.jetrix.*;

import java.util.*;

/**
 * A generic internationalized text message.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class TextMessage extends ChannelMessage
{
    private String text;
    private String key;
    private Object params[];
    private Map texts;
    private Map rawMessages;

    /**
     * Return the text of this message using the default server locale.
     */
    public String getText()
    {
        Locale defaultLocale = null;

        if (Server.getInstance() != null)
        {
            // get the server locale configured in config.xml
            defaultLocale = Server.getInstance().getConfig().getLocale();
        }
        else
        {
            // get the default system locale
            defaultLocale = Locale.getDefault();
        }

        return getText(defaultLocale);
    }

    /**
     * Return the text of this message using the specified locale.
     */
    public String getText(Locale locale)
    {
        if (key == null)
        {
            return text;
        }
        else
        {
            if (texts == null)
            {
                texts = new HashMap();
            }

            String s = (String) texts.get(locale);

            if (s == null)
            {
                s = Language.getText(key, params, locale);
                texts.put(locale, s);
            }

            return s;
        }
    }

    /**
     * Set the text of the message (locale independant).
     */
    public void setText(String text)
    {
        this.text = text;
        this.key = null;
    }

    /**
     * Return the key of the message.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Set the key of the message for internationalized text messages.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Set the key and the parameters of the message for internationalized
     * text messages.
     */
    public void setKey(String key, Object param)
    {
        setKey(key, new Object[] { param });
    }

    /**
     * Set the key and the parameters of the message for internationalized
     * text messages.
     */
    public void setKey(String key, Object param0, Object param1)
    {
        setKey(key, new Object[] { param0, param1 });
    }

    /**
     * Set the key and the parameters of the message for internationalized
     * text messages.
     */
    public void setKey(String key, Object param0, Object param1, Object param2)
    {
        setKey(key, new Object[] { param0, param1, param2 });
    }

    /**
     * Set the key and the parameters of the message for internationalized
     * text messages.
     */
    public void setKey(String key, Object param0, Object param1, Object param2, Object param3)
    {
        setKey(key, new Object[] { param0, param1, param2, param3 });
    }

    /**
     * Set the key and the parameters of the message for internationalized
     * text messages.
     */
    public void setKey(String key, Object[] params)
    {
        this.key = key;
        this.params = params;
    }

    public String getRawMessage(Protocol protocol, Locale locale)
    {
        if (key == null)
        {
            // use the default caching method for non localized messages
            return super.getRawMessage(protocol, locale);
        }
        else
        {
            // use the caching on the (protocol, locale) combo
            if (rawMessages == null)
            {
                rawMessages = new HashMap();
            }

            Map i18nMessages = (Map) rawMessages.get(protocol);
            if (i18nMessages == null)
            {
                i18nMessages = new HashMap();
                rawMessages.put(protocol, i18nMessages);
            }

            String cachedMessage = (String) i18nMessages.get(locale);
            if (cachedMessage == null)
            {
                cachedMessage = protocol.translate(this, locale);
                i18nMessages.put(locale, cachedMessage);
            }

            return cachedMessage;
        }
    }

}
