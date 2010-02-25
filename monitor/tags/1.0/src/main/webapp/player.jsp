<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
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
  <title>
    <fmt:message key="title.player">
      <fmt:param><%= StyleUtils.strip(player.getName()) %></fmt:param>
    </fmt:message>
  </title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
</head>
<body>



<h1>
  <fmt:message key="title.player">
    <fmt:param><%= StyleUtils.strip(player.getName()) %></fmt:param>
  </fmt:message>
</h1>

<table class="thin" border="1" cellspacing="0" style="min-width: 40%">
  <tr>
    <th width="25%"><fmt:message key="word.first-seen"/></th>
    <td><%= player.getFirstSeen() %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.last-seen"/></th>
    <td><%= player.getLastSeen() %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.last-played"/></th>
    <td><%= player.getLastPlayed() != null ? player.getLastPlayed() : "-" %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.server"/></th>
    <td><a href="server.jsp?id=<%= player.getLastServer().getId() %>"><%= player.getLastServer().getHostname() %></a></td>
  </tr>
  <tr>
    <th><fmt:message key="word.channel"/></th>
    <td><%= player.getChannel() != null ? player.getChannel() : "" %></td>
  </tr>
  <tr>
    <th><fmt:message key="word.team"/></th>
    <td><%= player.getTeam() != null ? player.getTeam() : "" %></td>
  </tr>
</table>

</body>
</html>
