/**
 * Jetrix TetriNET Server
 * Copyright (C) 2010  Emmanuel Bourg
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

package net.jetrix.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TimestampFormatter extends Formatter
{
    private Date date = new Date();
    private DateFormat formatter;
    private boolean level;

    public TimestampFormatter()
    {
        this("yyyy-MM-dd HH:mm:ss", true);
    }

    public TimestampFormatter(String format, boolean level)
    {
        this.level = level;
        formatter = new SimpleDateFormat(format);
    }

    public synchronized String format(LogRecord record)
    {
        StringBuilder message = new StringBuilder();

        // display the timestamp
        date.setTime(record.getMillis());
        message.append("[");
        message.append(formatter.format(date));
        message.append("] ");
        
        // display the log level
        if (level)
        {
            message.append("[");
            message.append(record.getLevel().getName());
            message.append("] ");
        }
        
        // display the message
        message.append(formatMessage(record));
        message.append("\n");
        
        // display the exception
        if (record.getThrown() != null)
        {
            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter(writer);
            
            record.getThrown().printStackTrace(out);
            message.append(writer.toString());
        }
        
        return message.toString();
    }
}
