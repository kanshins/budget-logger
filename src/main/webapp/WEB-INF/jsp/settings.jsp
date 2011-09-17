<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="include.jsp"%>

<c:set var="pageTitle" value="Финансовый учет - Настройки" />
<%@include file="header.jsp"%>

<table class="settings">
	<tr>
		<td>
			<h2>Настройки</h2>
		</td>
	</tr>
	<tr>
		<td>
			<%@include file="message.jsp" %>
		</td>
	</tr>
	<tr>
		<td class="border">
			<h3>Категории</h3>
			<form action="<c:url value="/logger/settings/category/save"/>" method="post">
				<input id="categoryId_id" type="hidden" name="categoryId" value=""/>		
				<input id="categoryDef_id" type="hidden" name="isDefault" value="false"/>		
				<input id="categoryReport_id" type="hidden" name="report" value="false"/>		
				<table class="search_results">
					<tr>
						<th>По умолчанию</th>
						<th>Отчет</th>
						<th>Категория</th>
						<th>Действие</th>
					</tr>
					<tr>
						<td width="140px;" align="center">
							<input type="checkbox" id="category_def_id" onClick="document.getElementById('categoryDef_id').value=this.checked;"/>
						</td>
						<td width="140px;" align="center">
							<input type="checkbox" id="category_report_id" onClick="document.getElementById('categoryReport_id').value=this.checked;"/>
						</td>
						<td style="padding-right: 5px;">
							<input style="width: 100%;" type="text" name="category" value="" id="category_name_id"/>
						</td>
						<td width="60px;">
							<input type="submit" name="submit" value="Сохранить"/>
						</td>
					</tr>
				</table>
			</form>
			<table class="search_results">
				<c:forEach var="cat" items="${categories}" varStatus="status">
					<tr <c:if test="${status.index%2 == 0}">bgcolor="#F0F0F0"</c:if>>
						<td width="140px;" align="center">
							<input type="checkbox" <c:if test="${cat.def}">checked="checked"</c:if> disabled="disabled"/>
						</td>
						<td width="140px;" align="center">
							<input type="checkbox" <c:if test="${cat.report}">checked="checked"</c:if> disabled="disabled"/>
						</td>
						<td style="padding-right: 5px;">
							<c:out value="${cat.name}"/>
						</td>
						<td width="60px;">
							<a href="#" onclick="setCategory('<c:out value="${cat.id}"/>','<c:out value="${cat.name}"/>', <c:out value="${cat.def}"/>, <c:out value="${cat.report}"/>);">
								<img border="0" alt="Редактировать" title="Редактировать" src="<c:url value="/img/edit_ico.gif"/>">
							</a>
							&nbsp;
							<a href="<c:url value="/logger/settings/category/${cat.id}/delete"/>" onClick="return confirm('Точно удалить?');">
								<img border="0" alt="Удалить" title="Удалить" src="<c:url value="/img/delete_ico.png"/>">
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<tr>
		<td class="border">
			<h3>Экспорт данных</h3>
			<input type="button" name="export" value="Сохранить файл" onClick="document.location.href='<c:url value="/logger/export"/>';"/>
		</td>
	</tr>
	<tr>
		<td class="border">
			<h3>Импорт данных</h3>
			<form action="<c:url value="/logger/import"/>" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td>
							<input type="file" name="fileData" />
						</td>
						<td>
							<input type="submit" name="import" value="Загрузить"/>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
	<tr>
		<td class="border">
			<h3>Изменение валюты</h3>
			<form action="<c:url value="/logger/settings/multiple"/>" method="post">
				<table>
					<tr>
						<td>Умножить на</td>
						<td>
							<input type="text" name="amount" value=""/><input type="submit" name="submit" value="Умножить"/>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>

<%@include file="footer.jsp"%>