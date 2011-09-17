<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>

<%@page import="org.budget.logger.model.Type"%>

<c:set var="pageTitle" value="Финансовый учет - Добавление чека" />
<%@include file="header.jsp"%>

<form action="<c:url value="/logger/bill/add"/>" method="post">
<table class="bill">
	<tr>
		<td colspan="2">
			<h2>Добавить чек</h2>
			<br />
		</td>
	</tr>
	<c:if test="${!empty messageCode}">
		<tr>
			<td colspan="2">
				<span class="messageSpan"><fmt:message key="${messageCode}"/></span>
			</td>
		</tr>
	</c:if>
	<c:if test="${!empty errorCode}">
		<tr>
			<td colspan="2">
				<span class="errorSpan"><fmt:message key="${errorCode}"/></span>
			</td>
		</tr>
	</c:if>
	<tr>
		<td class="label">Дата:</td>
		<td class="value">
			<bl:datefield name="date" value="${today}" tooltip="Дата"/>
		<td>
	</tr>
	<tr>
		<td class="label">Категория:</td>
		<td class="value">
			<bl:categories categories="${categories}" name="category"/>
		</td>
	</tr>
</table>

<input type="hidden" id="row_count_input_id" name="rowCount" value="0"/>
<table class="bill">
	<tr>
		<th class="label">Сумма</th>
		<th class="value">Описание</th>
	</tr>
	<tr>
		<td id="rows_td_id" colspan="2">
			<c:forEach var="row" items="${rows}">
				<script type="text/javascript">
					addRow('<c:out value="${row.key}"/>','<c:out value="${row.value}"/>');
				</script>
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td align="left">
			<input type="button" value="+" onClick="addRow();"/>
		</td>
		<td align="right">
			<input type="submit" value="Сохранить"/>
		</td>
	</tr>
</table>
</form>

<c:if test="${null == rows}">
	<script type="text/javascript">
		addRowDefault();
	</script>
</c:if>
<%@include file="footer.jsp"%>