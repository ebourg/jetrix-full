<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="net.jetrix.monitor.dao.ServerInfoDao" %>
<%@ page import="net.jetrix.monitor.ServerInfo" %>
<%
    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    ServerInfoDao dao = (ServerInfoDao) context.getBean("serverInfoDao");

    ServerInfo server = dao.getServer(Long.parseLong(request.getParameter("id")));

    String channel = request.getParameter("channel");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>
    <fmt:message key="title.channel">
      <fmt:param><%= channel %></fmt:param>
      <fmt:param><%= server.getHostname() %></fmt:param>
    </fmt:message>
  </title>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
  <link rel="Shorcut Icon" href="favicon.ico">
</head>
<body>

<h1>
  <fmt:message key="title.channel">
    <fmt:param><%= channel %></fmt:param>
    <fmt:param><%= server.getHostname() %></fmt:param>
  </fmt:message>
</h1>

<div align="center">
<applet code="net.jetrix.spectator.SpectatorApplet" archive="webspec-0.1-SNAPSHOT.jar,jetrix-0.3-SNAPSHOT.jar" width="640" height="480">
  <param name="hostname" value="<%= server.getHostname() %>">
  <param name="password" value="<%= server.getSpectatorPassword() %>">
  <param name="menu"     value="false">
  <param name="channel"  value="#<%= channel %>">
</applet>
</div>

<a href="server.jsp?id=<%= server.getId() %>"><fmt:message key="message.back-to-server-info"/></a>

</body>
</html>
