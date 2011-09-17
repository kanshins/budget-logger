<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="org.budget.logger.model.Type"%>

<c:set var="pageTitle" value="Финансовый учет - Отчет по категориям" />
<%@include file="/WEB-INF/jsp/header.jsp"%>

<c:set var="report" value="category"/>
<%@include file="header.jsp"%>
<form action="<c:url value="/logger/reports/category/period"/>" method="get">
<table style="width: 100%">
	<tr>
		<td align="center" nowrap="nowrap">
			Период:
			<select name="period">
				<c:forEach begin="1" end="12" step="1" varStatus="status">
					<option value="${status.index}" <c:if test="${status.index == period}">selected="selected"</c:if>>
						${status.index} <fmt:message key="jsp.reports.month.${status.index}"/>
					</option>
				</c:forEach>
			</select>
			<input type="submit" value="Показать"/>
		</td>
	</tr>
	<tr>
		<td align="center">
			<img src="<c:url value="/logger/reports/category/chartoutcome?period=${period}"/>"/>
		</td>
	</tr>
	<c:forEach var="cat" items="${categories}">
		<tr>
			<td align="center">
				<img src="<c:url value="/logger/reports/category/chartcat?catid=${cat.id}&period=${period}"/>"/>
			</td>
		</tr>
	</c:forEach>
</table>
</form>

<%@include file="/WEB-INF/jsp/footer.jsp"%>