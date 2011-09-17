<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>

<%@page import="org.budget.logger.model.Type"%>

<c:set var="pageTitle" value="Финансовый учет - Записи" />
<%@include file="header.jsp"%>

<c:set var="outcomeTypeId"><%=Type.OUTCOME.getId()%></c:set>
<c:set var="incomeTypeId"><%=Type.INCOME.getId()%></c:set>
<c:set var="storingTypeId"><%=Type.STORING.getId()%></c:set>
<form action="<c:url value="/logger/records/show"/>" method="get">
<table style="width: 100%">
	<tr>
		<td>
			<table>
				<tr>
					<td><h2>Записи</h2></td>
					<td style="padding-left: 30px;"><a href="<c:url value="/logger/bill"/>">добавить чек</a></td>
				</tr>
			</table>
		</td>
		<td align="right">
			<table>
				<tr>
					<td nowrap="nowrap">
						Категория:<bl:categories categories="${categories}" name="cat" selectAll="true" selected="${cat}"/>
					</td>
					<td nowrap="nowrap">
						с:<bl:datefield name="from" value="${from}" tooltip="Дата начала периода"/>
					</td>
					<td nowrap="nowrap" style="padding-left: 10px;">
						по:<bl:datefield name="to" value="${to}" tooltip="Дата конца периода"/>
					</td>
					<td>
						<input type="submit" name="submit" value="Показать">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<c:if test="${!empty errorCode}">
		<tr>
			<td colspan="2">
				<span class="errorSpan"><fmt:message key="${errorCode}"/></span>
			</td>
		</tr>
	</c:if>
</table>
</form>

<form action="<c:url value="/logger/records/save"/>" method="post">
<input id="recordId_id" type="hidden" name="recordId" value=""/>
<input type="hidden" name="from" value="<c:out value="${from}"/>"/>
<input type="hidden" name="to" value="<c:out value="${to}"/>"/>
<table class="search_results">
	<tr>
		<th>Дата</th>
		<th>Тип</th>
		<th>Сумма</th>
		<th>Категория</th>
		<th>Описание</th>
		<th>Действие</th>
	</tr>
	<tr>
		<td nowrap="nowrap" class="date_header">
			<bl:datefield name="date" value="${today}" tooltip="Дата"/>
		</td>
		<td style="width: 80px;">
			<select id="type_id" name="type">
				<option value="<%=Type.OUTCOME.getId()%>">Расход</option>
				<option value="<%=Type.INCOME.getId()%>">Приход</option>
				<option value="<%=Type.STORING.getId()%>">Накопление</option>
			</select>
		</td>
		<td style="width: 80px;">
			<input id="amount_id" style="width: 80px;" type="text" name="amount" value="" /></td>
		<td style="width: 150px;">
			<bl:categories categories="${categories}" name="category"/>
		</td>
		<td style="padding-right: 5px;">
			<input id="desc_id" style="width: 100%;" type="text" name="desc" value="" />
		</td>
		<td style="width: 60px;">
			<input type="submit" name="save" value="Сохранить" />
		</td>
	</tr>
</table>
</form>
<table class="search_results" width="100%">
	<c:forEach var="record" items="${records}" varStatus="status">
		<tr <c:if test="${status.index%2 == 0}">bgcolor="#F0F0F0"</c:if>>
			<td class="date">
				<fmt:formatDate pattern="dd-MM-yyyy" value="${record.date}" />
			</td>
			<td class="amount">
				<bl:amount typeId="${record.type.id}" value="${record.amount}"/>
			</td>
			<td class="category">${record.category}</td>
			<td class="desc">${record.desc}</td>
			<td class="action">
				<a href="#" onclick="setRecord('<c:out value="${record.id}"/>','<fmt:formatDate pattern="dd-MM-yyyy" value="${record.date}" />',<c:out value="${record.type.id}"/>,<c:out value="${record.amount}"/>,'<c:out value="${record.category}"/>','<c:out value="${record.desc}"/>');">
					<img border="0" alt="Редактировать" title="Редактировать" src="<c:url value="/img/edit_ico.gif"/>">
				</a>
				&nbsp;
				<a href="<c:url value="/logger/records/delete/${record.id}?from=${from}&to=${to}"/>" onClick="return confirm('Точно удалить?');">
					<img border="0" alt="Удалить" title="Удалить" src="<c:url value="/img/delete_ico.png"/>">
				</a>
			</td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="5">
			Итого за период<br />
			Приход: <bl:amount typeId="${incomeTypeId}" value="${totalIncome}"/>
			<br />
			Расход: <bl:amount typeId="${outcomeTypeId}" value="${totalOutcome}"/>
			<br />
			Накопление: <bl:amount typeId="${storingTypeId}" value="${totalStoring}"/>
		</td>
	</tr>
</table>
<%@include file="footer.jsp"%>
