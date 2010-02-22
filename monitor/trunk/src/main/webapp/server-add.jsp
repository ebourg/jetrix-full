<%@ page import="java.io.IOException" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.InetSocketAddress" %>
<%@ page import="java.net.Socket" %>
<%@ page import="java.util.Date" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.IpToCountryDao" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="java.util.logging.Logger" %>
<%
    String hostname = request.getParameter("hostname");
    Logger log = Logger.getLogger("net.jetrix.monitor");
    
    log.info("New server requested from [" + request.getRemoteHost() + "] : " + hostname);
    
    try
    {
        // lookup the IP
        InetAddress address = InetAddress.getByName(hostname);
        
        if (address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            // multicast and non routable addresses are ignored
            log.info("Server " + address + " not added: address not reachable");
            return;
        }
        
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");
        IpToCountryDao countryDao = (IpToCountryDao) context.getBean("ipToCountryDao");
        
        ServerInfo server = new ServerInfo();
        server.setHostname(hostname);
        server.setIP(address.getHostAddress());
        server.setDateAdded(new Date());
        server.setCountry(countryDao.getCountry(address));
        
        // lookup the hostname if the IP was specified
        if (address.getHostAddress().equals(hostname))
        {
            server.setHostname(address.getCanonicalHostName());
        }
        
        // check if the tetrinet port is opened
        long t0 = System.currentTimeMillis();
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(address, 31457), 15000);
        long t1 = System.currentTimeMillis();
        socket.close();
        
        server.getStats().setPing((int) (t1 - t0));
        
        if (!dao.exists(server))
        {
            dao.save(server);
            out.println("Server added (" + address + ")");
            log.info("Server added (" + address + ")");
        }
        
        // todo register the alias if the server is already registered
    }
    catch (IOException e)
    {
        log.info("Server " + hostname + " not added: " + e.getMessage());
        out.println(e.getMessage());
        // server ignored
    }
    finally
    {
        response.sendRedirect("index.jsp");
    }
%>