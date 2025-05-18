package com.lab2school.controller;

import com.lab2school.model.entity.Registration;
import com.lab2school.model.entity.Student;
import com.lab2school.model.entity.Subject;
import com.lab2school.model.service.RegistrationService;
import com.lab2school.model.service.StudentService;
import com.lab2school.model.service.SubjectService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/registrations")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RegistrationService registrationService;
	private StudentService studentService;
	private SubjectService subjectService;

	@Override
	public void init() throws ServletException {
		super.init();
		registrationService = new RegistrationService();
		studentService = new StudentService();
		subjectService = new SubjectService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		if (action == null) {
			action = "list";
		}

		try {
			switch (action) {
			case "new":
				showNewForm(request, response);
				break;
			case "edit":
				showEditForm(request, response);
				break;
			case "delete":
				deleteRegistration(request, response);
				break;
			case "list":
			default:
				listRegistrations(request, response);
				break;
			}
		} catch (Exception e) {
			System.err.println("Помилка в doGet RegistrationServlet: " + e.getMessage());
			e.printStackTrace();
			request.setAttribute("globalErrorMessage", "Виникла системна помилка: " + e.getMessage());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/errorPage.jsp");
			dispatcher.forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		if (action == null) {
			redirectToError(request, response, "Невідома дія POST запиту.");
			return;
		}

		try {
			switch (action) {
			case "create":
				createRegistration(request, response);
				break;
			case "update":
				updateRegistration(request, response);
				break;
			default:
				redirectToError(request, response, "Невідома дія POST запиту: " + action);
				break;
			}
		} catch (NumberFormatException e) {
			System.err.println("Помилка формату числа в doPost: " + e.getMessage());
			e.printStackTrace();
			String gradesString = request.getParameter("gradesString");
			prepareFormWithError(request, response, null, "Помилка: Некоректний формат числових даних.", gradesString);
		} catch (Exception e) {
			System.err.println("Помилка в doPost RegistrationServlet: " + e.getMessage());
			e.printStackTrace();
			String gradesString = request.getParameter("gradesString");
			prepareFormWithError(request, response, null,
					"Виникла системна помилка при обробці форми: " + e.getMessage(), gradesString);
		}
	}

	private void listRegistrations(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Registration> listRegistration;
		String filterStudentIdStr = request.getParameter("filterStudentId");
		String filterSubjectIdStr = request.getParameter("filterSubjectId");
		Integer selectedStudentId = null;
		Integer selectedSubjectId = null;
		String filterError = null;

		try {
			if (filterStudentIdStr != null && !filterStudentIdStr.isEmpty() && !"0".equals(filterStudentIdStr)) {
				selectedStudentId = Integer.parseInt(filterStudentIdStr);
				listRegistration = registrationService.getRegistrationsByStudentId(selectedStudentId);
			} else if (filterSubjectIdStr != null && !filterSubjectIdStr.isEmpty() && !"0".equals(filterSubjectIdStr)) {
				selectedSubjectId = Integer.parseInt(filterSubjectIdStr);
				listRegistration = registrationService.getRegistrationsBySubjectId(selectedSubjectId);
			} else {
				listRegistration = registrationService.getAllRegistrations();
			}
		} catch (NumberFormatException e) {
			System.err.println("Помилка парсингу ID фільтра: " + e.getMessage());
			listRegistration = registrationService.getAllRegistrations();
			filterError = "Некоректний ID для фільтрації.";
		}

		request.setAttribute("filterError", filterError);
		List<Student> allStudentsForFilter = studentService.getAllStudents();
		List<Subject> allSubjectsForFilter = subjectService.getAllSubjects();
		request.setAttribute("listRegistration", listRegistration);
		request.setAttribute("allStudentsForFilter", allStudentsForFilter);
		request.setAttribute("allSubjectsForFilter", allSubjectsForFilter);
		if (selectedStudentId != null)
			request.setAttribute("selectedStudentId", selectedStudentId);
		if (selectedSubjectId != null)
			request.setAttribute("selectedSubjectId", selectedSubjectId);
		if ("0".equals(filterStudentIdStr))
			request.setAttribute("selectedStudentId", 0);
		if ("0".equals(filterSubjectIdStr))
			request.setAttribute("selectedSubjectId", 0);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/listRegistrations.jsp");
		dispatcher.forward(request, response);
	}

	private void showNewForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		loadFormDependencies(request);
		request.setAttribute("actionLabel", "Створити");
		request.setAttribute("formAction", "create");
		request.setAttribute("gradesStringValue", "");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/registrationForm.jsp");
		dispatcher.forward(request, response);
	}

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			Registration existingRegistration = registrationService.getRegistrationById(id);

			if (existingRegistration == null) {
				request.setAttribute("globalErrorMessage", "Реєстрацію з ID " + id + " не знайдено.");
				listRegistrations(request, response);
				return;
			}
			request.setAttribute("registration", existingRegistration);
			loadFormDependencies(request);
			request.setAttribute("actionLabel", "Оновити");
			request.setAttribute("formAction", "update");

			if (existingRegistration.getStudentGrades() != null && !existingRegistration.getStudentGrades().isEmpty()) {
				String gradesStr = existingRegistration.getStudentGrades().stream()
						.map(sg -> String.valueOf(sg.getGradeValue())).collect(Collectors.joining(", "));
				request.setAttribute("gradesStringValue", gradesStr);
			} else {
				request.setAttribute("gradesStringValue", "");
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/registrationForm.jsp");
			dispatcher.forward(request, response);
		} catch (NumberFormatException e) {
			redirectToError(request, response, "Некоректний ID для редагування реєстрації.");
		}
	}

	private void createRegistration(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Registration newRegistration = new Registration();
		String gradesString = request.getParameter("gradesString");

		try {
			int studentId = Integer.parseInt(request.getParameter("studentId"));
			int subjectId = Integer.parseInt(request.getParameter("subjectId"));

			newRegistration.setStudentId(studentId);
			newRegistration.setSubjectId(subjectId);

			boolean success = registrationService.addRegistrationWithGrades(newRegistration, gradesString);

			if (success) {
				response.sendRedirect(request.getContextPath() + "/registrations?action=list&successMessage=create");
			} else {
				newRegistration.setId(0);
				prepareFormWithError(request, response, newRegistration,
						"Не вдалося створити реєстрацію. Перевірте дані або такий запис вже існує.", gradesString);
			}
		} catch (NumberFormatException e) {
			prepareFormWithError(request, response, newRegistration, "Некоректні дані студента або предмету.",
					gradesString);
		}
	}

	private void updateRegistration(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Registration registrationToUpdate = null;
		String gradesString = request.getParameter("gradesString");

		try {
			int id = Integer.parseInt(request.getParameter("id"));
			int studentId = Integer.parseInt(request.getParameter("studentId"));
			int subjectId = Integer.parseInt(request.getParameter("subjectId"));

			registrationToUpdate = registrationService.getRegistrationById(id);
			if (registrationToUpdate == null) {
				response.sendRedirect(
						request.getContextPath() + "/registrations?action=list&errorMessage=notFoundOnUpdate");
				return;
			}

			registrationToUpdate.setStudentId(studentId);
			registrationToUpdate.setSubjectId(subjectId);

			boolean success = registrationService.updateRegistrationWithGrades(registrationToUpdate, gradesString);

			if (success) {
				response.sendRedirect(request.getContextPath() + "/registrations?action=list&successMessage=update");
			} else {
				prepareFormWithError(request, response, registrationToUpdate,
						"Не вдалося оновити реєстрацію. Перевірте дані.", gradesString);
			}
		} catch (NumberFormatException e) {
			if (registrationToUpdate == null && request.getParameter("id") != null) {
				try {
					int id = Integer.parseInt(request.getParameter("id"));
					registrationToUpdate = new Registration();
					registrationToUpdate.setId(id);
				} catch (NumberFormatException nfe) {
					// ігноруємо, якщо ID теж невалідний
				}
			}
			prepareFormWithError(request, response, registrationToUpdate,
					"Некоректні дані студента, предмету або ID реєстрації.", gradesString);
		}
	}

	private void deleteRegistration(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			boolean success = registrationService.deleteRegistration(id);
			if (success) {
				response.sendRedirect(request.getContextPath() + "/registrations?action=list&successMessage=delete");
			} else {
				request.setAttribute("globalErrorMessage", "Не вдалося видалити реєстрацію ID " + id
						+ ". Можливо, є пов'язані дані або запис не знайдено.");
				listRegistrations(request, response);
			}
		} catch (NumberFormatException e) {
			redirectToError(request, response, "Некоректний ID для видалення реєстрації.");
		}
	}

	private void loadFormDependencies(HttpServletRequest request) {
		List<Student> listStudent = studentService.getAllStudents();
		List<Subject> listSubject = subjectService.getAllSubjects();
		request.setAttribute("listStudent", listStudent);
		request.setAttribute("listSubject", listSubject);
	}

	private void redirectToError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("globalErrorMessage", message);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/errorPage.jsp");
		dispatcher.forward(request, response);
	}

	private void prepareFormWithError(HttpServletRequest request, HttpServletResponse response,
			Registration registrationData, String errorMessage, String gradesStringValue)
			throws ServletException, IOException {
		request.setAttribute("formError", errorMessage);
		if (registrationData != null) {
			request.setAttribute("registration", registrationData);
		}
		request.setAttribute("gradesStringValue", gradesStringValue);

		loadFormDependencies(request);
		if (registrationData != null && registrationData.getId() != 0) {
			request.setAttribute("actionLabel", "Оновити");
			request.setAttribute("formAction", "update");
		} else {
			request.setAttribute("actionLabel", "Створити");
			request.setAttribute("formAction", "create");
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/registrationForm.jsp");
		dispatcher.forward(request, response);
	}
}