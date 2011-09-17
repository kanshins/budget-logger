<%@page import="org.apache.log4j.Logger"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>

<c:set var="pageTitle" value="Произошла ошибка" />
<%@include file="header.jsp"%>

<span class="errorSpan"><c:out value="${errorMessage}" /></span>

<c:if test="${null != exception}">
	<span class="errorSpan">${exception}</span>
</c:if>

<br />
<%
	Logger logger = Logger.getLogger("server");
	Throwable exception = (Throwable) request
			.getAttribute("javax.servlet.error.exception");
	if (exception != null) {
		logger.error(exception.getMessage(), exception);
	}
%>

<%@include file="footer.jsp"%>