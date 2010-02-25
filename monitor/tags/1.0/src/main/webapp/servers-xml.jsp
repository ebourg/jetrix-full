<?xml version="1.0"?>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page contentType="text/xml" pageEncoding="UTF-8" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");

    List<ServerInfo> servers = dao.getServers();
%>
<tetrinet-servers>
<%  for (ServerInfo server : servers) {
        // skip offline servers
        if (!server.isOnline()) {
            continue;
        }
%>
  <server
      id="<%= server.getId() %>"
      name="<%= server.getHostname() %>"
<%      if (server.getVersion() != null) { %>
      version="<%= StringEscapeUtils.escapeXml(server.getVersion()) %>"
<%      } %>
<%      if (server.getDescription() != null) { %>
      description="<%= StringEscapeUtils.escapeXml(server.getDescription()) %>"
<%      } %>
      spec="<%= server.isSpectate() ? "true" : "false" %>"
<%      if (server.getCountry() != null) {
            Locale locale = new Locale("en", server.getCountry());
%>
      country="<%= server.getCountry() %>"
      countryName="<%= locale.getDisplayCountry(Locale.ENGLISH) %>"
<%      } %>
      ping="<%= server.getStats().getPing() %>"
      players="<%= server.getStats().getPlayerCount() %>"
      activePlayers="<%= server.getStats().getActivePlayerCount() %>"
      channels="<%= server.getStats().getChannelCount() %>"
      activeChannels="<%= server.getStats().getActiveChannelCount() %>"
          />
<%  } %>
</tetrinet-servers>
