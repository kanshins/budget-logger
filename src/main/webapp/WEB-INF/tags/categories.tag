<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="name" required="true"%>
<%@ attribute name="selectAll" required="false" type="java.lang.Boolean"%>
<%@ attribute name="selected" required="false"%>
<%@ attribute name="categories" required="true" type="java.util.List"%>
<c:choose>
	<c:when test="${null != categories}">
		<select id="${name}_id" style="width: 150px;" name="${name}">
			<c:if test="${null != selectAll && selectAll}">
				<option value="" selected="selected">--</option>
			</c:if>
			<c:forEach var="cat" items="${categories}">
				<option value="${cat.name}" <c:if test="${(cat.def && null == selectAll) || (null != selected && selected == cat.name)}">selected="selected"</c:if>>${cat.name}</option>
			</c:forEach>
		</select>
	</c:when>
</c:choose>