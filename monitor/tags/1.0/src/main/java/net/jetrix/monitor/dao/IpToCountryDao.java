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

package net.jetrix.monitor.dao;

import java.io.IOException;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;
import static com.maxmind.geoip.LookupService.GEOIP_CHECK_CACHE;
import static com.maxmind.geoip.LookupService.GEOIP_MEMORY_CACHE;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IpToCountryDao
{
    private LookupService service;

    public IpToCountryDao() throws Exception
    {
        URL url = getClass().getResource("/GeoIP.dat");
        File file = new File(url.toURI());
        String fileName = file.getAbsolutePath();
        
        this.service = new LookupService(fileName, GEOIP_MEMORY_CACHE | GEOIP_CHECK_CACHE);
    }

    /**
     * Returns the country code matching this address, or null if the country can't be found.
     *
     * @param address
     */
    public String getCountry(InetAddress address)
    {
        Country country = service.getCountry(address);

        return country != null && !"--".equals(country.getCode()) ? country.getCode() : null;
    }

}
