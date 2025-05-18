<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="isNewRegistration"
	value="${empty registration.id || registration.id == 0}" />
<c:set var="pageTitleAction"
	value="${isNewRegistration ? 'Створення' : 'Редагування'}" />
<c:set var="headerActionText"
	value="${isNewRegistration ? 'Створити нову' : 'Редагувати'}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${pageTitleAction} реєстрації</title>
<style>
body {
	max-width: 700px;
	margin: auto;
	padding: 20px;
}

label {
	display: block;
	margin-top: 10px;
	font-weight: bold;
}

input[type=text], select, textarea, input[type=submit] {
	font-family: inherit;
	font-size: 1em;
	border-radius: 4px;
}

input[type=text], select, textarea {
	width: 100%;
	padding: 8px;
	margin-top: 5px;
	box-sizing: border-box;
	border: 1px solid #ccc;
}

input[type=submit], .form-actions a {
	cursor: pointer;
	color: white;
	border: none;
	padding: 10px 15px;
	text-decoration: none;
	display: inline-block;
	margin-right: 10px;
}

input[type=submit] {
	background-color: #007bff;
	width: auto;
}

input[type=submit]:hover {
	background-color: #0056b3;
}

.form-actions a {
	background-color: #6c757d;
	border-radius: 4px;
}

.form-actions a:hover {
	background-color: #5a6268;
}

.error {
	color: red;
	margin-top: 5px;
	font-size: 0.9em;
}

.form-actions {
	margin-top: 20px;
}

.details-box {
	margin-top: 10px;
	padding: 10px;
	border: 1px solid #e0e0e0;
	background-color: #f9f9f9;
	border-radius: 4px;
}

.details-box h4 {
	margin-top: 0;
	color: #333;
	border-bottom: 1px solid #ddd;
	padding-bottom: 5px;
}

.details-box p {
	margin: 3px 0;
	font-size: 0.95em;
}

.details-box p strong {
	min-width: 130px;
	display: inline-block;
}

h1 {
	text-align: center;
}
</style>
</head>
<body>
	<h1>${headerActionText}
		реєстрацію
		<c:if test="${not isNewRegistration}">
            (ID Реєстрації: <c:out value="${registration.id}" />)
        </c:if>
	</h1>

	<c:if test="${not empty formError}">
		<p class="error">
			<c:out value="${formError}" />
		</p>
	</c:if>
	<c:if test="${not empty globalErrorMessage}">
		<p class="error">
			<c:out value="${globalErrorMessage}" />
		</p>
	</c:if>

	<form method="post"
		action="${pageContext.request.contextPath}/registrations">
		<input type="hidden" name="action"
			value="<c:out value='${formAction}'/>" />
		<c:if test="${not isNewRegistration}">
			<input type="hidden" name="id"
				value="<c:out value='${registration.id}'/>" />
		</c:if>

		<div>
			<label for="studentId">Учень:</label> <select id="studentId"
				name="studentId" required
				onchange="renderStudentDetails(this.value);">
				<option value="">Оберіть учня</option>
				<c:forEach items="${listStudent}" var="studentOpt">
					<option value="${studentOpt.id}"
						${ (not empty registration && registration.studentId == studentOpt.id) ? 'selected' : ''}>
						<c:out value="${studentOpt.firstName}" />
						<c:out value="${studentOpt.lastName}" />
					</option>
				</c:forEach>
			</select>
		</div>
		<div id="selectedStudentDetails" class="details-box"
			style="display: none;">
			<h4>Інформація про обраного студента:</h4>
			<p>
				<strong>ID студента:</strong> <span id="detailStudentId"></span>
			</p>
			<p>
				<strong>ПІБ:</strong> <span id="detailStudentName"></span>
			</p>
			<p>
				<strong>Дата народження:</strong> <span id="detailStudentDob"></span>
			</p>
			<p>
				<strong>Школа:</strong> <span id="detailStudentSchoolName"></span>
			</p>
			<p>
				<strong>Адреса школи:</strong> <span id="detailStudentSchoolAddress"></span>
			</p>
		</div>

		<div>
			<label for="subjectId">Предмет:</label> <select id="subjectId"
				name="subjectId" required
				onchange="renderSubjectDetails(this.value);">
				<option value="">Оберіть предмет</option>
				<c:forEach items="${listSubject}" var="subjectOpt">
					<option value="${subjectOpt.id}"
						${ (not empty registration && registration.subjectId == subjectOpt.id) ? 'selected' : ''}>
						<c:out value="${subjectOpt.name}" />
					</option>
				</c:forEach>
			</select>
		</div>
		<div id="selectedSubjectDetails" class="details-box"
			style="display: none;">
			<h4>Інформація про обраний предмет:</h4>
			<p>
				<strong>ID предмета:</strong> <span id="detailSubjectId"></span>
			</p>
			<p>
				<strong>Назва:</strong> <span id="detailSubjectName"></span>
			</p>
			<p>
				<strong>Опис:</strong> <span id="detailSubjectDescription"></span>
			</p>
		</div>

		<div>
			<label for="gradesString">Оцінки (наприклад: 10, 8, 12):</label> <input
				type="text" id="gradesString" name="gradesString"
				value="<c:out value='${gradesStringValue}' escapeXml='true'/>" />
		</div>

		<div class="form-actions">
			<input type="submit" value="<c:out value='${actionLabel}'/>" /> <a
				href="${pageContext.request.contextPath}/registrations?action=list">Скасувати</a>
		</div>
	</form>

	<script type="text/javascript">
    var studentsData = [
        <c:forEach items="${listStudent}" var="s" varStatus="loop">
            {
                id: ${s.id},
                firstName: "<c:out value='${s.firstName}' escapeXml='true'/>",
                lastName: "<c:out value='${s.lastName}' escapeXml='true'/>",
                dateOfBirth: "<c:out value='${s.dateOfBirth}' escapeXml='true'/>",
                schoolName: "<c:out value='${s.school != null ? s.school.name : ""}' escapeXml='true'/>",
                schoolAddress: "<c:out value='${s.school != null ? s.school.address : ""}' escapeXml='true'/>"
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    var subjectsData = [
        <c:forEach items="${listSubject}" var="sub" varStatus="loop">
            {
                id: ${sub.id},
                name: "<c:out value='${sub.name}' escapeXml='true'/>",
                description: "<c:out value='${sub.description}' escapeXml='true'/>"
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    function updateDetailsDisplay(itemId, dataArray, detailsDivId, fieldConfig, defaultText = 'Не вказано') {
        var detailsDiv = document.getElementById(detailsDivId);
        if (!itemId || itemId === "") {
            detailsDiv.style.display = 'none';
            return;
        }
        var item = dataArray.find(function(d) { return d.id == itemId; });

        if (item) {
            fieldConfig.forEach(function(field) {
                var element = document.getElementById(field.elementId);
                if (element) {
                    var textContent;
                    if (typeof field.valueExtractor === 'function') {
                        textContent = field.valueExtractor(item);
                    } else {
                        textContent = item[field.valueExtractor];
                    }
                    element.textContent = textContent || (field.defaultText !== undefined ? field.defaultText : defaultText);
                }
            });
            detailsDiv.style.display = 'block';
        } else {
            detailsDiv.style.display = 'none';
        }
    }

    var studentFieldConfiguration = [
        { elementId: 'detailStudentId', valueExtractor: 'id' },
        { elementId: 'detailStudentName', valueExtractor: function(item) { return item.firstName + ' ' + item.lastName; } },
        { elementId: 'detailStudentDob', valueExtractor: 'dateOfBirth' },
        { elementId: 'detailStudentSchoolName', valueExtractor: 'schoolName' },
        { elementId: 'detailStudentSchoolAddress', valueExtractor: 'schoolAddress' }
    ];

    var subjectFieldConfiguration = [
        { elementId: 'detailSubjectId', valueExtractor: 'id' },
        { elementId: 'detailSubjectName', valueExtractor: 'name' },
        { elementId: 'detailSubjectDescription', valueExtractor: 'description', defaultText: 'Немає опису' }
    ];

    function renderStudentDetails(studentId) {
        updateDetailsDisplay(studentId, studentsData, 'selectedStudentDetails', studentFieldConfiguration);
    }

    function renderSubjectDetails(subjectId) {
        updateDetailsDisplay(subjectId, subjectsData, 'selectedSubjectDetails', subjectFieldConfiguration);
    }

    document.addEventListener('DOMContentLoaded', function() {
        var initialStudentId = document.getElementById('studentId').value;
        if (initialStudentId) {
            renderStudentDetails(initialStudentId);
        }
        var initialSubjectId = document.getElementById('subjectId').value;
        if (initialSubjectId) {
            renderSubjectDetails(initialSubjectId);
        }
    });
</script>
</body>
</html>