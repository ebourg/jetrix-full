<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>

<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");

    List<ServerInfo> servers = dao.getServers();

    int totalPlayerCount = 0;
    int serverCount = 0;
    Date lastChecked = null;
    for (ServerInfo server : servers) {
        if (server.isOnline()) {
            serverCount++;
            totalPlayerCount += server.getStats().getPlayerCount();
        }

        if (lastChecked == null || (server.getLastChecked() != null && server.getLastChecked().after(lastChecked))) {
            lastChecked = server.getLastChecked();
        }
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title><fmt:message key="title.servers"/></title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
  <script type="text/javascript" src="scripts/sortable.js"></script>
</head>
<body>

<h1><fmt:message key="title.servers"/></h1>

<p>
<fmt:message key="message.intro">
  <fmt:param>5</fmt:param>
  <fmt:param>smanux@lfjr.net</fmt:param>
</fmt:message>   
</p>

<p>
<fmt:message key="message.global-status">
  <fmt:param><%= totalPlayerCount %></fmt:param>
  <fmt:param><%= serverCount %></fmt:param>
</fmt:message>
<fmt:message key="message.last-checked">
  <fmt:param><%= lastChecked %></fmt:param>
</fmt:message>
</p>

<table class="thin sortable" id="serverlist" border="1" align="center">
  <thead>
    <tr>
      <th><fmt:message key="word.server"/></th>
      <th><fmt:message key="word.country"/></th>
      <th><fmt:message key="word.players"/></th>
      <th><fmt:message key="word.channels"/></th>
      <th><fmt:message key="word.version"/></th>
      <th><fmt:message key="word.ping"/> <small>(ms)</small></th>
      <th>TSpec</th>
    </tr>
  </thead>
  <tbody>
<%  for (ServerInfo server : servers) {
        // skip offline servers
        if (!server.isOnline()) {
            continue;
        }
%>
    <tr>
      <td><a href="server.jsp?id=<%= server.getId() %>"><%= server.getHostname() %></a></td>
      <td align="center">
<%      if (server.getCountry() != null) { %>
          <%
              Locale locale = new Locale("en", server.getCountry());
              String countryName = locale.getDisplayCountry(Locale.ENGLISH);
              String localizedCountryName = locale.getDisplayCountry(request.getLocale());
          %>
          <img src="images/flags/24/<%= countryName %>.png" alt="<%= localizedCountryName%>" title="<%= localizedCountryName%>">
<%      } else { %>
          <img src="images/flags/24/United Nations.png" alt="<fmt:message key="word.unknown"/>" title="<fmt:message key="word.unknown"/>">
<%      } %>
      </td>
      <td align="right" style="font-weight: bold; <%= server.getStats().getActivePlayerCount() > 0 ? "color: green" : "" %>">
        <%= server.getStats().getPlayerCount() %>
      </td>
      <td align="right"><%= server.getStats().getChannelCount() %></td>
      <td><%= server.getVersion() != null ? server.getVersion() : "" %></td>
      <td align="right"><%= server.getStats().getPing() %></td>
      <td align="center" style="color: <%= server.isSpectate() ? "green" : "red" %>">
        <% if (server.isSpectate()) { %>
        <fmt:message key="word.yes"/>
        <% } else { %>
        <fmt:message key="word.no"/>
        <% } %>
      </td>
    </tr>
<%  } %>
  </tbody>
</table>

<fieldset style="margin: 1em auto; width: 200px; -moz-border-radius: 6px">
  <legend><fmt:message key="message.add-server"/></legend>
  <form action="server-add.jsp">
    <input type="text" name="hostname">
    <input type="submit" value="<fmt:message key="word.add"/>">
    <br>
    (i.e. <tt>tetrinet.fr</tt>, <tt>194.117.194.68</tt>)
  </form>
</fieldset>

<hr>

<div style="font-size: smaller; float: left">
 Powered by <a href="http://jetrix.sourceforge.net">Jetrix Monitor</a><br>
</div>
<div style="font-size: smaller; text-align: right">
 Flags icons are made by <a href="http://www.icondrawer.com">www.icondrawer.com</a>
</div>

</body>
</html>
