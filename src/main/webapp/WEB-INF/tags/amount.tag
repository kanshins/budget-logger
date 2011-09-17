<%@tag import="org.budget.logger.model.Type"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="value" required="true"%>
<%@ attribute name="typeId" required="true"%>

<c:set var="incomeTypeId"><%=Type.INCOME.getId()%></c:set>
<c:set var="outcomeTypeId"><%=Type.OUTCOME.getId()%></c:set>
<c:set var="storingTypeId"><%=Type.STORING.getId()%></c:set>

<c:choose>
        <c:when test="${typeId == incomeTypeId}">
        	<span style="color: #0000AA;">
				<fmt:formatNumber value="${value}" maxFractionDigits="2" minFractionDigits="2"/>
			</span>
        </c:when>
        <c:when test="${typeId == outcomeTypeId}">
        	<span style="color: #AA0000;">
				-<fmt:formatNumber value="${value}" maxFractionDigits="2" minFractionDigits="2"/>
			</span>
        </c:when>
        <c:when test="${typeId == storingTypeId}">
        	<span style="color: #00AA00;">
				<fmt:formatNumber value="${value}" maxFractionDigits="2" minFractionDigits="2"/>
			</span>
        </c:when>
</c:choose>    