<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.agent.ChannelInfo" %>
<%@ page import="net.jetrix.agent.PlayerInfo" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="net.jetrix.monitor.StyleUtils" %>
<%@ page import="java.net.URLEncoder" %>
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>
    <fmt:message key="title.server">
      <fmt:param><%= server.getHostname() %></fmt:param>
    </fmt:message>  
  </title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
  <script type="text/javascript" src="scripts/sortable.js"></script>
</head>
<body>

<h1>
  <fmt:message key="title.server">
    <fmt:param><%= server.getHostname() %></fmt:param>
  </fmt:message>    
</h1>

<%  if (server.getMaxPlayerCount() > 0) { %>
<div style="float: right">
  <img src="images/graphs/server-<%= server.getId() %>.png" alt="<fmt:message key="word.activity-graph"/>">
</div>
<%  } %>

<table class="thin" border="1" cellspacing="0" style="min-width: 40%">
  <tr>
    <th width="25%"><fmt:message key="word.website"/></th>
    <td>
<%  if (server.getWebsite() != null) { %>
      <a href="<%= server.getWebsite() %>"><%= server.getWebsite() %></a>
<%  } %>
    </td>
  </tr>
  <tr>
    <th><fmt:message key="word.description"/></th>
    <td><%= server.getDescription() != null ? server.getDescription() : "" %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.version"/></th>
    <td><%= server.getVersion() != null ? server.getVersion() : "" %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.country"/></th>
    <td>
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td><img src="images/flags/24/<%= countryFlag %>.png" alt="<%= localizedCountryName%>"></td>
          <td><%= localizedCountryName%></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <th><fmt:message key="word.players"/></th>
    <td>
      <%= server.getStats().getPlayerCount() %>
<%  if (server.getStats().getActivePlayerCount() > 0) { %>
      (<%= server.getStats().getActivePlayerCount() %> <fmt:message key="word.active"/>)
<%  } %>
    </td>
  </tr>
<%  if (server.getMaxPlayerCount() != 0 && server.getMaxActivePlayerCount() != 0) { %>    
  <tr>
    <th><fmt:message key="word.peak"/></th>
    <td>
      <fmt:message key="message.peak.players">
        <fmt:param><%= server.getMaxPlayerCount() %></fmt:param>
        <fmt:param><%= server.getMaxPlayerDate() %></fmt:param>
      </fmt:message>
      <br>
      <fmt:message key="message.peak.active-players">
        <fmt:param><%= server.getMaxActivePlayerCount() %></fmt:param>
        <fmt:param><%= server.getMaxActivePlayerDate() %></fmt:param>
      </fmt:message>
    </td>
  </tr>
<%  } %>
  <tr>
    <th><fmt:message key="word.channels"/></th>
    <td><%= server.getStats().getChannelCount() %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.tspec-access"/></th>
    <td style="color: <%= server.isSpectate() ? "green" : "red" %>">
      <% if (server.isSpectate()) { %>
      <fmt:message key="word.yes"/>
      <% } else { %>
      <fmt:message key="word.no"/>
      <% } %>
    </td>
  </tr>
  <tr>
    <th><fmt:message key="word.ping"/></th>
    <td><%= server.getStats().getPing() %> ms</td>
  </tr>
  <tr>
    <th><fmt:message key="word.date-added"/></th>
    <td><%= server.getDateAdded() %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.last-online"/></th>
    <td>
      <% if (server.getLastOnline() != null) { %>
      <%= server.getLastOnline() %>
      <% } else { %>
      <fmt:message key="word.never"/>
      <% } %>
    </td>
  </tr>
</table>

<p><a href="<%= request.getContextPath() %>/"><fmt:message key="message.back-to-servers"/></a></p>

<div style="clear: both;"></div>

<%  if (!server.getPlayers().isEmpty()) { %>

<h2><fmt:message key="word.players"/></h2>

<table class="thin sortable" id="players" border="1" style="min-width: 40%">
  <thead>
    <tr>
      <th><fmt:message key="word.nick"/></th>
      <th><fmt:message key="word.team"/></th>
      <th><fmt:message key="word.channel"/></th>
      <th><fmt:message key="word.slot"/></th>
      <th><fmt:message key="word.status"/></th>
      <th><fmt:message key="word.tetrinet-client"/></th>
    </tr>
  </thead>
  <tbody>
<%  for (PlayerInfo player : server.getPlayers()) { %>
    <tr>
      <td<%= player.getAuthenticationLevel() > 1 ? " style=\"font-weight: bold\"" : "" %>><a style="text-decoration: none; color: black" href="player.jsp?id=<%= player.getNick() %>"><%= StyleUtils.toHTML(player.getNick()) %></a></td>
      <td><%= StyleUtils.toHTML(player.getTeam()) %></td>
      <td><%= player.getChannel() %></td>
      <td><%= player.getSlot() %></td>
      <td style="color: <%= player.isPlaying() ? "green" : "inherit" %>">
        <% if (player.isPlaying()) { %>
        <fmt:message key="word.playing"/>
        <% } else { %>
        <fmt:message key="word.idle"/>
        <% } %>
      </td>
      <td><%= player.getVersion() %></td>
    </tr>
<%  } %>
  </tbody>
</table>

<%  } %>


<%  if (!server.getChannels().isEmpty()) { %>

<h2><fmt:message key="word.channels"/></h2>

<table class="thin sortable" id="channels" border="1" style="min-width: 40%">
  <thead>
    <tr>
      <th><fmt:message key="word.name"/></th>
      <th><fmt:message key="word.description"/></th>
      <th><fmt:message key="word.players"/></th>
      <th><fmt:message key="word.status"/></th>
<%  if (viewable) { %>
      <th><fmt:message key="word.action"/></th>
<%  } %>
    </tr>
  </thead>
  <tbody>
<%  for (ChannelInfo channel : server.getChannels()) { %>
    <tr>
      <td><%= channel.getName() %></td>
      <td><%= StyleUtils.toHTML(channel.getDescription()) %></td>
      <td><%= channel.getPlayernum() %> / <%= channel.getPlayermax() %></td>
      <td style="color: green; font-weight: bold">
        <% if (channel.isPlaying()) { %>
        <fmt:message key="word.ingame"/>
        <% } %>
      </td>
<%  if (viewable) { %>
      <td align="center">
<%      if (channel.getPlayernum() > 0) { %>
        <a href="spec.jsp?id=<%= server.getId() %>&channel=<%= URLEncoder.encode(channel.getName()) %>">View</a>
<%      } %>
      </td>
<%  } %>
    </tr>
<%  } %>
  </tbody>
</table>

<%  } %>

</body>
</html>
