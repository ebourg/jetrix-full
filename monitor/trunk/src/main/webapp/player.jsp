<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.monitor.PlayerStats" %>
<%@ page import="net.jetrix.monitor.dao.PlayerStatsDao" %>
<%@ page import="net.jetrix.monitor.StyleUtils" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    PlayerStatsDao dao = (PlayerStatsDao) context.getBean("playerStatsDao");

    PlayerStats player = dao.getStats(request.getParameter("id"));

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>TetriNET Player - <%= StyleUtils.strip(player.getName()) %></title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
</head>
<body>

<h1>TetriNET Player - <%= StyleUtils.strip(player.getName()) %></h1>

<table class="thin" border="1" cellspacing="0" style="min-width: 40%">
  <tr>
    <th width="25%">First Seen</th>
    <td><%= player.getFirstSeen() %></td>
  </tr>
  <tr>
    <th>Last Seen</th>
    <td><%= player.getLastSeen() %></td>
  </tr>
  <tr>
    <th>Last Played</th>
    <td><%= player.getLastPlayed() != null ? player.getLastPlayed() : "-" %></td>
  </tr>
  <tr>
    <th>Server</th>
    <td><a href="server.jsp?id=<%= player.getLastServer().getId() %>"><%= player.getLastServer().getHostname() %></a></td>
  </tr>
  <tr>
    <th>Channel</th>
    <td><%= player.getChannel() != null ? player.getChannel() : "" %></td>
  </tr>
  <tr>
    <th>Team</th>
    <td><%= player.getTeam() != null ? player.getTeam() : "" %></td>
  </tr>
</table>

</body>
</html>
