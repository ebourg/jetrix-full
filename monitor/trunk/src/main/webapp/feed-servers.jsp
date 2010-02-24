<%@ page contentType="application/atom+xml" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sun.syndication.feed.synd.*" %>
<%@ page import="com.sun.syndication.io.SyndFeedOutput" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");
    
    ResourceBundle bundle = ResourceBundle.getBundle("messages", request.getLocale());
    
    String basePath = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf('/'));
    
    List<ServerInfo> servers = dao.getLatestServers(10);
    
    SyndFeed feed = new SyndFeedImpl();
    feed.setFeedType("atom_1.0");
    feed.setTitle(bundle.getString("feed.title"));
    feed.setLink(basePath);
    feed.setDescription(bundle.getString("feed.description"));
    
    List<SyndEntry> entries = new ArrayList<SyndEntry>();
    for (ServerInfo server : servers) {
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(server.getHostname());
        entry.setLink(basePath + "/server/" + server.getHostname());
        entry.setPublishedDate(server.getDateAdded());
        
        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(server.getVersion());
        
        entry.setDescription(description);
        entries.add(entry);
    }
    
    feed.setEntries(entries);
    
    SyndFeedOutput output = new SyndFeedOutput();
    output.output(feed,response.getWriter());
    response.getWriter().flush();
%>