<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Locale" %>
<%@ page import="net.jetrix.agent.ChannelInfo" %>
<%@ page import="net.jetrix.agent.PlayerInfo" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.StyleUtils" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");
    
    // find the server from its id or hostname
    ServerInfo server = null;
    if (request.getParameter("id") != null) {
        server = dao.getServer(Long.parseLong(request.getParameter("id")));
    } else if (request.getPathInfo() != null) {
        String info = request.getPathInfo().substring(request.getPathInfo().lastIndexOf('/') + 1);
        if (StringUtils.isNumeric(info) && !info.isEmpty()) {
            server = dao.getServer(Long.parseLong(info));
        } else {
            server = dao.getServer(info);
        }
    }
    
    if (server == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "TetriNET server not found");
        return;
    }
    
    String localizedCountryName = "Unknown";
    String countryFlag = "United Nations";
    
    if (server.getCountry() != null) {
        Locale locale = new Locale("en", server.getCountry());
        localizedCountryName = locale.getDisplayCountry(request.getLocale());
        countryFlag = locale.getDisplayCountry(Locale.ENGLISH);
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
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/stylesheets/style.css">
  <link rel="Shorcut Icon" href="<%= request.getContextPath() %>/favicon.ico">
  <link rel="alternate" type="text/xml" title="<%= StringEscapeUtils.escapeHtml(server.getHostname()) %> XML data sheet" href="<%= request.getContextPath() %>/server.xml?id=<%= server.getId() %>">
  <script type="text/javascript" src="<%= request.getContextPath() %>/scripts/sortable.js"></script>
</head>
<body>

<h1>
  <fmt:message key="title.server">
    <fmt:param><%= server.getHostname() %></fmt:param>
  </fmt:message>    
</h1>

<%  if (server.getMaxPlayerCount() > 0) { %>
<div style="float: right">
  <img src="<%= request.getContextPath() %>/images/graphs/server-<%= server.getId() %>.png" alt="<fmt:message key="word.activity-graph"/>">
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
          <td><img src="<%= request.getContextPath() %>/images/flags/24/<%= countryFlag %>.png" alt="<%= localizedCountryName%>"></td>
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
  <tr>
    <th><fmt:message key="word.last-game"/></th>
    <td>
      <% if (server.getLastActive() != null) { %>
      <%= server.getLastActive() %>
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
      <td<%= player.getAuthenticationLevel() > 1 ? " style=\"font-weight: bold\"" : "" %>><a style="text-decoration: none; color: black" href="<%= request.getContextPath() %>/player.jsp?id=<%= player.getNick() %>"><%= StyleUtils.toHTML(player.getNick()) %></a></td>
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
        <a href="<%= request.getContextPath() %>/spec.jsp?id=<%= server.getId() %>&channel=<%= URLEncoder.encode(channel.getName(), "iso-8859-1") %>">View</a>
<%      } %>
      </td>
<%  } %>
    </tr>
<%  } %>
  </tbody>
</table>

<%  } %>

<hr>

<div align="right">
  <a href="<%= request.getContextPath() %>/server.xml?id=<%= server.getId() %>" class="datasheet"><fmt:message key="word.datasheet"/></a>
</div>

</body>
</html>
