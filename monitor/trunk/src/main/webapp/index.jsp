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
  <title>TetriNET Servers Listing</title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
  <script type="text/javascript" src="scripts/sortable.js"></script>
</head>
<body>

<h1>TetriNET Servers Listing</h1>

<p>This is a list of the public TetriNET servers. The list is refreshed every 5 minutes. Feel free to add your favorite
server if it's not in the list.</p>

<p>There are currently <b><%= totalPlayerCount %></b> players online on <b><%= serverCount %></b> servers. The
servers were last checked on <%= lastChecked %>.</p>

<table class="thin sortable" id="serverlist" border="1" align="center">
  <thead>
    <tr>
      <th>Server</th>
      <th>Country</th>
      <th>Players</th>
      <th>Channels</th>
      <th>Version</th>
      <th>Ping <small>(ms)</small></th>
      <th>Spec</th>
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
          %>
          <img src="images/flags/24/<%= countryName %>.png" alt="<%= countryName%>" title="<%= countryName%>">
<%      } else { %>
          <img src="images/flags/24/United Nations.png" alt="Unknown" title="Unknown">
<%      } %>
      </td>
      <td align="right" style="font-weight: bold; <%= server.getStats().getActivePlayerCount() > 0 ? "color: green" : "" %>">
        <%= server.getStats().getPlayerCount() %>
      </td>
      <td align="right"><%= server.getStats().getChannelCount() %></td>
      <td><%= server.getVersion() != null ? server.getVersion() : "" %></td>
      <td align="right"><%= server.getStats().getPing() %></td>
      <td align="center" style="color: <%= server.isSpectate() ? "green" : "red" %>"><%= server.isSpectate() ? "Yes" : "No" %></td>
    </tr>
<%  } %>
  </tbody>
</table>

<fieldset style="margin: 1em auto; width: 200px; -moz-border-radius: 6px">
  <legend>Add a new server</legend>
  <form action="server-add.jsp">
    <input type="text" name="hostname">
    <input type="submit" value="Add">
    <br>
    (<tt>tetrinet.fr</tt> or <tt>194.117.194.68</tt>)
  </form>
</fieldset>

<hr>

<p>This list is also available in <a href="servers-xml.jsp">XML</a> and can be freely integrated with an application
or a website. Flags icons are made by www.icondrawer.com.</p>

<p>Feedback and suggestions can be mailed to <a href="mailto:smanux@lfjr.net">smanux@lfjr.net</a></p>

</body>
</html>
