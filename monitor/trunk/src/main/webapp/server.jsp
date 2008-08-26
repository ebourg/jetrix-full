<%@ page import="java.util.Locale" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.agent.ChannelInfo" %>
<%@ page import="net.jetrix.agent.PlayerInfo" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="net.jetrix.monitor.StyleUtils" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");

    ServerInfo server = dao.getServer(Long.parseLong(request.getParameter("id")));

    String countryName = "Unknown";
    String countryFlag = "United Nations";

    if (server.getCountry() != null) {
        Locale locale = new Locale("en", server.getCountry());
        countryName = locale.getDisplayCountry(Locale.ENGLISH);
        countryFlag = countryName;
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>TetriNET Server - <%= server.getHostname() %></title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
  <script type="text/javascript" src="scripts/sortable.js"></script>
</head>
<body>

<h1>TetriNET Server - <%= server.getHostname() %></h1>

<%  if (server.getMaxPlayerCount() > 0) { %>
<div style="float: right">
  <img src="images/graphs/server-<%= server.getId() %>.png" alt="Activity graph">
</div>
<%  } %>

<table class="thin" border="1" cellspacing="0" style="min-width: 40%">
  <tr>
    <th width="25%">Website</th>
    <td>
<%  if (server.getWebsite() != null) { %>
      <a href="<%= server.getWebsite() %>"><%= server.getWebsite() %></a>
<%  } %>
    </td>
  </tr>
  <tr>
    <th>Description</th>
    <td><%= server.getDescription() != null ? server.getDescription() : "" %></td>
  </tr>
  <tr>
    <th>Version</th>
    <td><%= server.getVersion() != null ? server.getVersion() : "" %></td>
  </tr>
  <tr>
    <th>Country</th>
    <td>
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td><img src="images/flags/24/<%= countryFlag %>.png" alt="<%= countryName%>" title="<%= countryName%>"></td>
          <td><%= countryName%></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <th>Players</th>
    <td>
      <%= server.getStats().getPlayerCount() %>
<%  if (server.getStats().getActivePlayerCount() > 0) { %>
      (<%= server.getStats().getActivePlayerCount() %> active)
<%  } %>
    </td>
  </tr>
<%  if (server.getMaxPlayerCount() != 0 && server.getMaxActivePlayerCount() != 0) { %>    
  <tr>
    <th>Peak</th>
    <td>
      <%= server.getMaxPlayerCount() %> players reached on <%= server.getMaxPlayerDate() %><br>
      <%= server.getMaxActivePlayerCount() %> active players reached on <%= server.getMaxActivePlayerDate() %>
    </td>
  </tr>
<%  } %>
  <tr>
    <th>Channels</th>
    <td><%= server.getStats().getChannelCount() %></td>
  </tr>
  <tr>
    <th>TSpec Access</th>
    <td style="color: <%= server.isSpectate() ? "green" : "red" %>"><%= server.isSpectate() ? "Yes" : "No" %></td>
  </tr>
  <tr>
    <th>Ping</th>
    <td><%= server.getStats().getPing() %> ms</td>
  </tr>
  <tr>
    <th>Date Added</th>
    <td><%= server.getDateAdded() %></td>
  </tr>
  <tr>
    <th>Last Online</th>
    <td><%= server.getLastOnline() != null ? server.getLastOnline() : "Never" %></td>
  </tr>
</table>

<p><a href="<%= request.getContextPath() %>/">Back to the server list</a></p>

<div style="clear: both;"></div>

<%  if (!server.getPlayers().isEmpty()) { %>

<h2>Players</h2>

<table class="thin sortable" id="players" border="1" style="min-width: 40%">
  <thead>
    <tr>
      <th>Nick</th>
      <th>Team</th>
      <th>Channel</th>
      <th>Slot</th>
      <th>Status</th>
      <th>Client</th>
    </tr>
  </thead>
  <tbody>
<%  for (PlayerInfo player : server.getPlayers()) { %>
    <tr>
      <td<%= player.getAuthenticationLevel() > 1 ? " style=\"font-weight: bold\"" : "" %>><a style="text-decoration: none; color: black" href="player.jsp?id=<%= player.getNick() %>"><%= StyleUtils.toHTML(player.getNick()) %></a></td>
      <td><%= StyleUtils.toHTML(player.getTeam()) %></td>
      <td><%= player.getChannel() %></td>
      <td><%= player.getSlot() %></td>
      <td style="color: <%= player.isPlaying() ? "green" : "inherit" %>"><%= player.isPlaying() ? "Playing" : "Idle" %></td>
      <td><%= player.getVersion() %></td>
    </tr>
<%  } %>
  </tbody>
</table>

<%  } %>


<%  if (!server.getChannels().isEmpty()) { %>

<h2>Channels</h2>

<table class="thin sortable" id="channels" border="1" style="min-width: 40%">
  <thead>
    <tr>
      <th>Name</th>
      <th>Description</th>
      <th>Players</th>
      <th>Status</th>
    </tr>
  </thead>
  <tbody>
<%  for (ChannelInfo channel : server.getChannels()) { %>
    <tr>
      <td><%= channel.getName() %></td>
      <td><%= StyleUtils.toHTML(channel.getDescription()) %></td>
      <td><%= channel.getPlayernum() %> / <%= channel.getPlayermax() %></td>
      <td style="color: green; font-weight: bold"><%= channel.isPlaying() ? "INGAME" : "" %></td>
    </tr>
<%  } %>
  </tbody>
</table>

<%  } %>

</body>
</html>
