<?xml version="1.0"?>
<%@ page import="java.util.Locale" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="net.jetrix.agent.ChannelInfo" %>
<%@ page import="net.jetrix.agent.PlayerInfo" %>
<%@ page contentType="text/xml" pageEncoding="UTF-8" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");

    ServerInfo server = dao.getServer(Long.parseLong(request.getParameter("id")));

    String countryName = "Unknown";
    String localizedCountryName = "Unknown";
    String countryFlag = "United Nations";

    if (server.getCountry() != null) {
        Locale locale = new Locale("en", server.getCountry());
        countryName = locale.getDisplayCountry(Locale.ENGLISH);
        localizedCountryName = locale.getDisplayCountry(request.getLocale());
        countryFlag = countryName;
    }

    boolean viewable = server.isSpectate() && server.getSpectatorPassword() != null;
%>
<tetrinet-server
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
      activeChannels="<%= server.getStats().getActiveChannelCount() %>">

<%  if (!server.getChannels().isEmpty()) { %>
  <channels>
<%      for (ChannelInfo channel : server.getChannels()) { %>
    <channel name="<%= StringEscapeUtils.escapeXml(channel.getName()) %>" description="<%= StringEscapeUtils.escapeXml(channel.getDescription()) %>" status="<%= channel.getStatus() %>" players="<%= channel.getPlayernum() %>" max-players="<%= channel.getPlayermax() %>"/>
<%      } %>
  </channels>
<%  } %>

<%  if (!server.getPlayers().isEmpty()) { %>
  <players>
<%      for (PlayerInfo player : server.getPlayers()) { %>
    <player name="<%= StringEscapeUtils.escapeXml(player.getNick()) %>" team="<%= StringEscapeUtils.escapeXml(player.getTeam()) %>" channel="<%= StringEscapeUtils.escapeXml(player.getChannel()) %>" status="<%= player.getStatus() %>" slot="<%= player.getSlot() %>" auth="<%= player.getAuthenticationLevel() %>" client="<%= StringEscapeUtils.escapeXml(player.getVersion()) %>"/>
<%      } %>
  </players>
<%  } %>
</tetrinet-server>
