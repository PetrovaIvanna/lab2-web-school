<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Помилка</title>
<style>
.error-container {
	margin: 50px;
	padding: 20px;
	border: 1px solid #cc0000;
	background-color: #f8d7da;
	color: #721c24;
}

h1 {
	color: #721c24;
}
</style>
</head>
<body>
	<div class="error-container">
		<h1>Сталася помилка</h1>
		<p>На жаль, під час обробки вашого запиту виникла проблема.</p>

		<c:if test="${not empty globalErrorMessage}">
			<p>
				<strong>Повідомлення:</strong>
				<c:out value="${globalErrorMessage}" />
			</p>
		</c:if>

		<c:if test="${pageContext.exception != null}">
			<h3>Деталі помилки:</h3>
			<pre>
				<%
				pageContext.getException().printStackTrace(new java.io.PrintWriter(out));
				%>
			</pre>
		</c:if>

		<p>
			<a
				href="${pageContext.request.contextPath}/registrations?action=list">Повернутися
				до списку реєстрацій</a>
		</p>
		<p>
			<a href="${pageContext.request.contextPath}/">Повернутися на
				головну</a>
		</p>
	</div>
</body>
</html>