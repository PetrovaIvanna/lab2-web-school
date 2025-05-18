<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Список реєстрацій</title>
<style>
table {
	width: 100%;
	border-collapse: collapse;
}

th, td {
	border: 1px solid black;
	padding: 8px;
	text-align: left;
}

th {
	background-color: #f2f2f2;
}

.actions a {
	margin-right: 10px;
}

.message {
	padding: 10px;
	margin-bottom: 15px;
}

.success {
	background-color: #d4edda;
	color: #155724;
	border: 1px solid #c3e6cb;
}

.error {
	background-color: #f8d7da;
	color: #721c24;
	border: 1px solid #f5c6cb;
}

h1 {
	text-align: center;
}

.center-aligned-paragraph,
.filter-form-container {
    text-align: center;
    margin-bottom: 20px;
}
.center-aligned-paragraph a {
    font-size: 1em;
    text-decoration: none;
    padding: 8px 15px;
    background-color: #28a745;
    color: white;
    border-radius: 4px;
    border: none;
    cursor: pointer;
}
.center-aligned-paragraph a:hover {
    background-color: #218838;
}

.filter-form-container label {
    margin-right: 5px;
    font-weight: bold;
}

.filter-form-container select {
    font-family: inherit;
    font-size: 1em;
    padding: 8px;
    margin-right: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
    vertical-align: middle;
}

.filter-form-container input[type=submit],
.filter-form-container a.clear-filter {
    font-family: inherit;
    font-size: 1em;
    text-decoration: none;
    padding: 8px 15px;
    background-color: #007bff;
    color: white;
    border-radius: 4px;
    border: none;
    cursor: pointer;
    margin-left: 5px;
    vertical-align: middle;
}
.filter-form-container input[type=submit]:hover,
.filter-form-container a.clear-filter:hover {
    background-color: #0056b3;
}

</style>
</head>
<body>
	<h1>Список записів учнів на предмети</h1>

	<c:if test="${not empty param.successMessage || not empty param.errorMessage || not empty globalErrorMessage || not empty filterError}">
        <div class="message
            <c:choose>
                <c:when test="${not empty param.successMessage}">success</c:when>
                <c:otherwise>error</c:otherwise>
            </c:choose>
        ">
            <c:choose>
                <c:when test="${param.successMessage eq 'create'}">Реєстрацію успішно створено!</c:when>
                <c:when test="${param.successMessage eq 'update'}">Реєстрацію успішно оновлено!</c:when>
                <c:when test="${param.successMessage eq 'delete'}">Реєстрацію успішно видалено!</c:when>
                <c:when test="${not empty param.errorMessage}">
                    <c:choose>
                         <c:when test="${param.errorMessage eq 'notFoundOnUpdate'}">Помилка: Запис для оновлення не знайдено.</c:when>
                         <c:when test="${param.errorMessage eq 'deleteFailed'}">Помилка: Не вдалося видалити запис.</c:when>
                         <c:otherwise>Помилка: ${param.errorMessage}</c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${not empty globalErrorMessage}">Помилка: ${globalErrorMessage}</c:when>
                <c:when test="${not empty filterError}">Помилка фільтрації: ${filterError}</c:when>
            </c:choose>
        </div>
    </c:if>

	<p class="center-aligned-paragraph">
		<a href="${pageContext.request.contextPath}/registrations?action=new">Створити нову реєстрацію</a>
	</p>

    <div class="filter-form-container">
        <form method="get" action="${pageContext.request.contextPath}/registrations">
            <input type="hidden" name="action" value="list">
            
            <label for="filterStudentId">Студент:</label>
            <select name="filterStudentId" id="filterStudentId">
                <option value="0">Всі студенти</option>
                <c:forEach items="${allStudentsForFilter}" var="student">
                    <option value="${student.id}" ${selectedStudentId == student.id ? 'selected' : ''}>
                        <c:out value="${student.firstName} ${student.lastName}"/>
                    </option>
                </c:forEach>
            </select>

            <label for="filterSubjectId">Предмет:</label>
            <select name="filterSubjectId" id="filterSubjectId">
                <option value="0">Всі предмети</option>
                <c:forEach items="${allSubjectsForFilter}" var="subject">
                    <option value="${subject.id}" ${selectedSubjectId == subject.id ? 'selected' : ''}>
                        <c:out value="${subject.name}"/>
                    </option>
                </c:forEach>
            </select>

            <input type="submit" value="Фільтрувати">
            <a href="${pageContext.request.contextPath}/registrations?action=list" class="clear-filter">Очистити фільтри</a>
        </form>
    </div>

	<c:if test="${empty listRegistration}">
        <p class="center-aligned-paragraph">Записів не знайдено (або немає жодної реєстрації).</p>
    </c:if>
    
	<c:if test="${not empty listRegistration}">
		<table>
			<tr>
				<th>ID Реєстрації</th>
				<th>Учень (ID)</th>
				<th>Предмет (ID)</th>
				<th>Оцінки</th>
				<th>Школа Учня</th>
				<th>Дії</th>
			</tr>
			<c:forEach items="${listRegistration}" var="reg">
				<tr>
					<td><c:out value="${reg.id}" /></td>
					<td><c:out value="${reg.student.firstName}" /> <c:out
							value="${reg.student.lastName}" /> (<c:out
							value="${reg.student.id}" />)</td>
					<td><c:out value="${reg.subject.name}" /> (<c:out
							value="${reg.subject.id}" />)</td>
					<td><c:if test="${not empty reg.studentGrades}">
							<c:forEach items="${reg.studentGrades}" var="studentGrade"
								varStatus="loop">
								<c:out value="${studentGrade.gradeValue}" />
								<c:if test="${not loop.last}">, </c:if>
							</c:forEach>
						</c:if> <c:if test="${empty reg.studentGrades}">
                            Немає оцінок
                        </c:if></td>
					<td><c:out value="${reg.student.school.name}" /></td>
					<td class="actions"><a
						href="${pageContext.request.contextPath}/registrations?action=edit&id=${reg.id}">Редагувати</a>
						<a
						href="${pageContext.request.contextPath}/registrations?action=delete&id=${reg.id}"
						onclick="return confirm('Ви впевнені, що хочете видалити цей запис?');">Видалити</a>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</body>
</html>