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

import java.util.logging.Logger;
import java.util.logging.Level;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpringTestCase extends TestCase
{
    protected ApplicationContext context;

    public SpringTestCase()
    {
        Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
        Logger.getLogger("org.springframework").setLevel(Level.WARNING);
        context = new ClassPathXmlApplicationContext("/testContext.xml");
    }
}
