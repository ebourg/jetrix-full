<%@ page import="net.jetrix.*"%>
<%@ page import="net.jetrix.services.*"%>
<%@ page import="net.jetrix.servlets.*"%>
<%@ page import="net.jetrix.commands.*"%>
<%@ page import="net.jetrix.config.*"%>
<%@ page import="net.jetrix.filter.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>

<%
    Server server = Server.getInstance();
    ServerConfig conf = server.getConfig();
    request.setAttribute("settings", conf.getDefaultSettings());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/tabpane/js/tabpane.js"></script>
  <script type="text/javascript" src="javascript/server.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css">
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>Jetrix Administration - Server</title>
</head>
<body>

<div class="content" style="padding: 1em">

<h1>Server</h1>

<div class="tab-pane" id="tab-pane-2">

  <div class="tab-page" style="height: 400px">
    <h2 class="tab">General</h2>

    <form id="general" action="/servlet/<%= ServerAction.class.getName() %>">
    <input type="hidden" name="action" value="general">

    <table class="thin" style="width: 600px">
      <tr>
        <td width="20%">Version</td>
        <td width="80%">
          <%= ServerConfig.VERSION %>
<%  if (VersionService.isNewVersionAvailable()) { %>
          <span style="color: red; padding-left: 2em">new version available (<%= VersionService.getLatestVersion() %>), <a href="http://jetrix.sourceforge.net">download it</a> now!</span>
<%  } %>
        </td>
      </tr>
      <tr>
        <td>Name</td>
        <td><input class="thin" type="text" name="name" value="<%= conf.getName() != null ? conf.getName() : "" %>"></td>
      </tr>
      <tr>
        <td>Host</td>
        <td>
          <select name="host">
            <option value="[ALL]">All Interfaces</option>

<%
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface network = interfaces.nextElement();

                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    InetAddress address = addresses.nextElement();
%>
            <option value="<%= address.getHostAddress() %>" <%= address.equals(conf.getHost()) ? "selected" : "" %>><%= address.getHostAddress() %> <%= address.getHostAddress().equals(address.getHostName()) ? "" : " (" + address.getHostName() + ")" %></option>
<%
                }
            }
%>
          </select>

        </td>
      </tr>
      <tr>
        <td>Max Players</td>
        <td><input class="thin" type="text" name="maxPlayers" value="<%= conf.getMaxPlayers() %>"></td>
      </tr>
      <tr>
        <td>Max Connections</td>
        <td><input class="thin" type="text" name="maxConnections" value="<%= conf.getMaxConnections() %>"></td>
      </tr>
      <tr>
        <td>Operator Password</td>
        <td><input class="thin" type="text" name="opPassword" value="<%= conf.getOpPassword() != null ? conf.getOpPassword() : "" %>"></td>
      </tr>
      <tr>
        <td>Administrator Password</td>
        <td><input class="thin" type="text" name="adminPassword" value="<%= conf.getAdminPassword() != null ? conf.getAdminPassword() : "" %>"></td>
      </tr>
      <tr>
        <td>Default Language</td>
        <td>
<%  Iterator locales = Language.getLocales().iterator(); %>
          <select class="thin" name="locale">
<%  while (locales.hasNext()) {
        Locale locale = (Locale) locales.next(); %>
            <option value="<%= locale.getLanguage() %>" <%= conf.getLocale().equals(locale) ? "selected" : "" %>>
              <%= locale.getLanguage().toUpperCase() %> - <%= locale.getDisplayLanguage() %>
            </option>
<%  } %>
          </select>
        </td>
      </tr>
        <tr>
          <td>Status</td>
          <td>
            <table>
              <tr>
                <td><input type="radio" value="<%= ServerConfig.STATUS_OPENED %>" name="status" id="status1" <%= conf.getStatus() == ServerConfig.STATUS_OPENED ? "checked" : "" %>></td>
                <td><label for="status1">Opened</label></td>
                <td><input type="radio" value="<%= ServerConfig.STATUS_LOCKED %>" name="status" id="status2" <%= conf.getStatus() == ServerConfig.STATUS_LOCKED ? "checked" : "" %>></td>
                <td><label for="status2">Locked</label></td>
              </tr>
            </table>
          </td>
        </tr>
      <tr>
        <td valign="top">Message of the day</td>
        <td><textarea class="thin" name="motd" cols="20" rows="5" style="width: 100%"><%= conf.getMessageOfTheDay() %></textarea></td>
      </tr>
    </table>

    <br>

    <input type="submit" value="Save Changes">
    <input type="button" value="Shutdown" onclick="shutdown()">

    </form>

    <br>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Clients</h2>

    <table class="thin" style="width: 500px">
      <tr>
        <th>Name</th>
        <th>Type</th>
        <th>Version</th>
        <th>Protocol</th>
        <th>IP</th>
        <th>Access Level</th>
        <th>Channel</th>
      </tr>
<%  for (Client client : ClientRepository.getInstance().getClients()) {
        User user = client.getUser(); %>
      <tr>
        <td><a href="user.jsp?name=<%= user.getName() %>"><%= user.getName() %></a></td>
        <td align="center"><%= client.getType() %></td>
        <td align="center"><%= client.getVersion() %></td>
        <td align="center"><%= client.getProtocol().getName() %></td>
        <td><%= client.getInetAddress().getHostName() %></td>
        <td align="center"><%= user.getAccessLevel() %></td>
        <td><a href="channel.jsp?name=<%= client.getChannel().getConfig().getName() %>"><%= client.getChannel().getConfig().getName() %></a></td>
      </tr>
<%  } %>
    </table>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Settings</h2>

    <jsp:include page="/servlet/org.apache.jsp.settings_jsp"/>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Filters</h2>

<%  Iterator filters = conf.getGlobalFilters(); %>

    <h2>Filters</h2>

    <table class="thin" style="width: 500px">
      <tr>
        <th>Name</th>
        <th>Version</th>
        <th>Description</th>
        <th></th>
      </tr>
<%  while (filters.hasNext()) {
        net.jetrix.config.FilterConfig filterConfig = (net.jetrix.config.FilterConfig) filters.next();
        MessageFilter filter = FilterManager.getInstance().getFilterByName(filterConfig.getName());
%>
      <tr>
        <td><%= filter.getName() %></td>
        <td><%= filter.getVersion() %></td>
        <td><%= filter.getDescription() %></td>
        <td><input type="image" src="images/delete16.png" value="remove" alt="Remove" title="Remove"></td>
      </tr>
<%  } %>
    </table>

    <!-- list, add, remove -->

  </div>
  <div class="tab-page" style="height: 400px; overflow: auto">
    <h2 class="tab">Commands</h2>

<%
    CommandManager commandManager = CommandManager.getInstance();
    Iterator<Command> commands = commandManager.getCommands(AccessLevel.ADMINISTRATOR);
%>

    <table class="thin">
      <tr>
        <th>Aliases</th>
        <th>Usage</th>
        <th>Description</th>
        <th>Access Level</th>
        <th></th>
      </tr>
<%  while (commands.hasNext()) {
        Command command = commands.next();
        String usage = command.getUsage(conf.getLocale());
        usage = usage.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        StringBuffer aliases = new StringBuffer();
        for (int i = 0; i < command.getAliases().length; i++) {
            if (i > 0) { aliases.append(", "); }
            aliases.append(command.getAliases()[i]);
        } %>
      <tr>
        <td><%= aliases %></td>
        <td><%= usage %></td>
        <td><%= command.getDescription(conf.getLocale()) %></td>
        <td><%= command.getAccessLevel() %></td>
        <td><a href="/servlet/<%= ServerAction.class.getName() %>?action=command.remove&command=<%= command.getClass().getName() %>"><img src="images/delete16.png" alt="Remove" title="Remove" border="0" ></a></td>
      </tr>
<%  } %>
    </table>

    <%-- todo UI to add a new command --%>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Ban List</h2>

    <script type="text/javascript">
    function unban(value) {
        var form = document.forms["banlist.remove"];
        form["pattern"].value = value;
        form.submit();
    }
    </script>

    <form id="banlist.remove" action="/servlet/<%= ServerAction.class.getName() %>">
      <input type="hidden" name="action" value="banlist.remove">
      <input type="hidden" name="pattern" value="">
    </form>

    <form id="banlist" action="/servlet/<%= ServerAction.class.getName() %>">
    <input type="hidden" name="action" value="banlist.add">

<%  Iterator banlist = Banlist.getInstance().getBanlist(); %>

    <table class="thin" style="width: 400px">
      <tr>
        <th>Pattern</th>
        <th>Expires</th>
        <th>Type</th>
        <th></th>
      </tr>
<%  while (banlist.hasNext()) {
        Banlist.Entry entry = (Banlist.Entry) banlist.next(); %>
      <tr>
        <td><%= entry.pattern %></td>
        <td><%= entry.expiration != null ? entry.expiration.toString() : "" %></td>
        <td width="50" align="center">Host</td>
        <td width="50" align="center">
          <a href="javascript: unban('<%= entry.pattern %>')"><img src="images/delete16.png" alt="Remove" title="Remove"></a>
        </td>
      </tr>
<%  } %>
      <tr>
        <td><input type="text" name="pattern" style="thin"></td>
        <td><input type="text" name="expires" style="thin"></td>
        <td width="50" align="center">
          <select name="type">
            <option value="0">Host</option>
            <option value="1">Nickname</option>
            <option value="2">Team</option>
          </select>
        </td>
        <td width="50"><input type="submit" value="Add"></td>
      </tr>
    </table>

    </form>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Listeners</h2>

<%  List listeners = conf.getListeners(); %>

    <table class="thin" style="width: 400px">
      <tr>
        <th>Name</th>
        <th>Port</th>
        <th>Status</th>
        <th>Auto Start</th>
        <th></th>
      </tr>
<%  for (int i = 0; i < listeners.size(); i++) {
        Listener listener = (Listener) listeners.get(i);  %>
      <tr>
        <td><%= listener.getName() %></td>
        <td width="70" align="center"><%= listener.getPort() %></td>
        <td width="70" align="center"><%= listener.isRunning() ? "Started" : "Stopped" %></td>
        <td width="70" align="center"><%= listener.isAutoStart() ? "yes" : "no" %></td>
        <td width="50">
          <form id="listener" action="/servlet/<%= ServerAction.class.getName() %>">
            <input type="hidden" name="index"  value="<%= i %>">
<%      if (listener.isRunning()) { %>
            <input type="hidden" name="action" value="listener.stop">
            <input type="submit" value="Stop">
<%      } else { %>
            <input type="hidden" name="action" value="listener.start">
            <input type="submit" value="Start">
<%      } %>
          </form>
        </td>
      </tr>
<%  } %>
    </table>

    <br>

    <input type="button" value="Add">

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Services</h2>

<%  List services = conf.getServices(); %>

    <table class="thin" style="width: 400px">
      <tr>
        <th>Name</th>
        <th>Status</th>
        <th>Auto Start</th>
        <th></th>
      </tr>
<%  for (int i = 0; i < services.size(); i++) {
        Service service = (Service) services.get(i);  %>
      <tr>
        <td><%= service.getName() %></td>
        <td width="70" align="center"><%= service.isRunning() ? "Started" : "Stopped" %></td>
        <td width="70" align="center"><%= service.isAutoStart() ? "yes" : "no" %></td>
        <td width="50">
          <form id="listener" action="/servlet/<%= ServerAction.class.getName() %>">
            <input type="hidden" name="index"  value="<%= i %>">
<%      if (service.isRunning()) { %>
            <input type="hidden" name="action" value="service.stop">
            <input type="submit" value="Stop">
<%      } else { %>
            <input type="hidden" name="action" value="service.start">
            <input type="submit" value="Start">
<%      } %>
          </form>
        </td>
      </tr>
<%  } %>
    </table>

    <br>

    <input type="button" value="Add">

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Statistics</h2>

    <table class="thin" style="width: 400px">
      <tr>
        <td width="30%">Uptime</td>
        <td><%= conf.getStatistics().getUpTime() %> day<%= conf.getStatistics().getUpTime() > 1 ? "s" : "" %></td>
      </tr>
      <tr>
        <td>Number of Connections</td>
        <td><%= conf.getStatistics().getConnectionCount() %></td>
      </tr>
      <tr>
        <td>Games Played</td>
        <td><%= conf.getStatistics().getGameCount() %></td>
      </tr>
    </table>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">System</h2>

<%
    Runtime runtime = Runtime.getRuntime();
    DecimalFormat df = new DecimalFormat("0.##"); 
%>
    
    <h2>Environment</h2>

    <table class="thin" style="width: 600px">
      <tr>
        <td width="30%">Java Virtual Machine</td>
        <td>
          <%= System.getProperty("java.vm.name") %>
          (build <%= System.getProperty("java.vm.version") %>,
          <%= System.getProperty("java.vm.info") %>)
        </td>
      </tr>
      <tr>
        <td>Java Runtime Environment</td>
        <td>
          <%= System.getProperty("java.runtime.name") %>
          (build <%= System.getProperty("java.vm.version") %>)
        </td>
      </tr>
      <tr>
        <td>Operating System</td>
        <td>
          <%= System.getProperty("os.name") %>
          <%= System.getProperty("os.version") %>
          <%= System.getProperty("os.arch") %>
        </td>
      </tr>
      <tr>
        <td>Processor(s)</td>
        <td><%= runtime.availableProcessors() %></td>
      </tr>
    </table>
    
    
    <h2>Memory</h2>
    
    <table class="thin" style="width: 600px">
      <tr>
        <td width="30%">Total Memory</td>
        <td><%= df.format(runtime.totalMemory()/1024d/1024d) %> Mb</td>
      </tr>
      <tr>
        <td>Max Memory</td>
        <td><%= df.format(runtime.maxMemory()/1024d/1024d) %> Mb</td>
      </tr>
      <tr>
        <td>Free Memory</td>
        <td><%= df.format(runtime.freeMemory()/1024d/1024d) %> Mb</td>
      </tr>
    </table>

    <br>

    <form id="gc" action="/servlet/<%= ServerAction.class.getName() %>">
      <input type="hidden" name="action" value="gc">
      <input type="submit" value="Run the Garbage Collector">
    </form>

  </div>
</div>


</body>
</html>
