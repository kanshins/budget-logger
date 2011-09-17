<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>

<c:if test="${!empty errorKey}">
	<span class="errorSpan"><fmt:message key="${errorKey}"/></span>
</c:if>
<c:if test="${!empty errorMessage}">
	<span class="errorSpan">${errorMessage}</span>
</c:if>
<c:if test="${!empty messageKey}">
	<span class="messageSpan"><fmt:message key="${messageKey}"/></span>
</c:if>
