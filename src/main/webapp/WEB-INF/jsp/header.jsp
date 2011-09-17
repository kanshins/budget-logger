<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><c:out value="${pageTitle}"/></title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
		
		<script type="text/javascript" src="<c:url value="/js/bl.js"/>"></script>

		<script type="text/javascript" src="<c:url value="/" />js/calendar/calendar_stripped.js"></script>
		<script type="text/javascript" src="<c:url value="/" />js/calendar/calendar-setup_stripped.js"></script>
		<script type="text/javascript" src="<c:url value="/" />js/calendar/lang/calendar-ru.js"></script>
		<link rel="stylesheet" type="text/css" href="<c:url value="/" />js/calendar/calendar-blue.css"/>

</head>
	<body>
	<table class="TopNavMenuBorderedTable">
		<tr>
			<td class="head-menu"><a href="<c:url value="/logger/records"/>">Записи</a></td>
			<td class="head-menu"><a href="<c:url value="/logger/reports/main"/>">Отчеты</a></td>
			<td class="head-menu"><a href="<c:url value="/logger/settings"/>">Настройки</a></td>
		</tr>
	</table>
	<table class="bigBorderedTable">
		<tr>
			<td>
			<!-- content -->
	