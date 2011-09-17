<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="name" required="true"%>
<%@ attribute name="value" required="false"%>
<%@ attribute name="tooltip" required="false"%>
<c:choose>
	<c:when test="${null != name}">
		<img id="${name}_img_id" src="<c:url value="/img/calendar.gif"/>" 
			width="16" height="16" 
			style="cursor: pointer; border: 0 solid #ffffff;" 
			border="0" alt="${tooltip}" 
			title="${tooltip}"/>
		<input style="width: 80px;" type="text" id="${name}_id" name="${name}" value="${value}" />
		<script	type="text/javascript">
			var cal1 = Calendar.setup({inputField:"<c:out value="${name}_id"/>",button:"<c:out value="${name}_img_id"/>",ifFormat:"%d-%m-%Y",showsTime:false});
		</script>
	</c:when>
</c:choose>      