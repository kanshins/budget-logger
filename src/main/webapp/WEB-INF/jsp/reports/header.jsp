<%@page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<table width="100%">
	<tr>
		<td align="center">
			<table>
				<tr>
					<td width="150px">
						<c:choose>
							<c:when test="${report == 'main'}">
								<b>основной</b>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/logger/reports/main"/>">основной</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td width="150px">
						<c:choose>
							<c:when test="${report == 'category'}">
								<b>по категориям</b>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/logger/reports/category"/>">по категориям</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td width="150px">
						<c:choose>
							<c:when test="${report == 'detailed'}">
								<b>детальный</b>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/logger/reports/detailed"/>">детальный</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
