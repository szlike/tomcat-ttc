<%-- 
    Document   : mystopslist.jsp
    Loops over hashmap, producin a JSON list of Stops
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="application/json" %>
<c:choose>
    <c:when test="${stoplist != null}">
      {
        <c:forEach items="${stoplist}" var="stop" varStatus="id">
            "${stop.value.stopid}":["${stop.value.stop_title}", "${stop.value.routeid}",
	      "${stop.value.runid}", "${stop.value.run_title}"]${id.last ? "" : ","}
        </c:forEach>
      }
    </c:when>
</c:choose>