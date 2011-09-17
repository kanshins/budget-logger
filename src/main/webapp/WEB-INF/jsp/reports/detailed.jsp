<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="org.budget.logger.model.Type"%>

<c:set var="pageTitle" value="Финансовый учет - Отчет Детальный" />
<%@include file="/WEB-INF/jsp/header.jsp"%>

<c:set var="report" value="detailed"/>
<%@include file="header.jsp"%>
<form action="<c:url value="/logger/reports/detailed"/>" method="post">
<table style="width: 100%">
	<tr>
		<td align="center" nowrap="nowrap">
			С: <bl:datefield name="from" value="${from}" tooltip="Начало периода"/>
			По: <bl:datefield name="to" value="${to}" tooltip="Конец периода"/>
			<input type="submit" value="Показать"/>
		</td>
	</tr>
	<tr>
		<td align="center">
			<img src="<c:url value="/logger/reports/detailed/chart?from=${from}&to=${to}"/>"/>
			<br />
		</td>
	</tr>
	<c:forEach var="cat" items="${categories}">
		<tr>
			<td align="center">
				<img src="<c:url value="/logger/reports/detailed/chartcat?from=${from}&to=${to}&catid=${cat.id}"/>"/>
				<br />
			</td>
		</tr>
	</c:forEach>
</table>
</form>

<%@include file="/WEB-INF/jsp/footer.jsp"%>