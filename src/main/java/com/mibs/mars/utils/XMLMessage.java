package com.mibs.mars.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML сообщение
 *
 * <p>
 * В конструктор класса передаются строка соединения к базе Doctor и карта
 * параметров пациента, посещения и исследования</p>
 *
 * <p>
 * Ключи карты параметров:<br>
 * <b>serviceAddress</b> - Адрес WEB-сервиса; <b>email</b> - Адрес электронной
 * почты пациента; <b>first</b> - Имя пациента; <b>parent</b> - Отчество
 * пациента; <b>family</b> - Фамилия пациента; <b>uid</b> - Уникальный номер
 * исследования; <b>studyName</b> - Наименование исследования; <b>path</b> -
 * Полный сетевой путь к папке с исследованием (например:
 * "\\\\172.16.28.125\\D$\\Users\\Public\\Doctor\\studies\\");
 * <b>isPaid</b> - Признак оплаты услуг через кассу ("true" или "false");
 * <b>code</b> - Код услуги (на данный момент: "1" - открытие кабинета и
 * размещение первого исследования, "2" - размещение дополнительного
 * исследования, "3" - продление кабинета на год, "4" - продление кабинета на 3
 * года); <b>count</b> - Сумма оплаты; <b>sumUnit</b> - Наименование валюты
 * (RUB); <b>date</b> - Дата и время оплаты в формате
 * SimpleDateFormat("dd.MM.yyyy HH:mm:ss");</p>
 *
 * @author chikalov
 */
public class XMLMessage {

	/**
	 * Заголовок системного сообщения
	 */
	private final String SYSTEM_MESSAGE_CAPTION = "Системное сообщение";
	/**
	 * Сообщение об отмене команды
	 */
	private final String COMMAND_CANCEL_MESSAGE
			= "Выполнение команды отменено:\n";
	/**
	 * Папка для хранения временных файлов
	 */
	private final String TEMPORARY_FILE_PATH_NAME
			= System.getProperty("java.io.tmpdir");
	/**
	 * Карта кодов услуг
	 */
	private final HashMap<String, Integer> serviceCodes = new HashMap<>();
	/**
	 * Наименование метода Test WEB-сервиса
	 */
	private final String TEST_METHOD_NAME = "Test";
	/**
	 * Наименование метода UploadStudy WEB-сервиса
	 */
	private final String UPLOAD_METHOD_NAME = "UploadStudy";
	/**
	 * Наименование метода UpdatePaymentInfo WEB-сервиса
	 */
	private final String UPDATE_METHOD_NAME = "UpdatePaymentInfo";
	/**
	 * Карта сведений о пациенте
	 */
	private final HashMap<String, String> patientInfo;
	/**
	 * Строка подключения к базе данных Doctor
	 */
	private final String connectionString;
	/**
	 * Соединение с SQL сервером
	 */
	private Connection connection = null;
	/**
	 * Объект для подготовки и запуска SQL запроса
	 */
	private Statement statement = null;
	/**
	 * Полученный в результате выполнения SQL запроса набор данных
	 */
	private ResultSet resultSet = null;

	/**
	 * XML сообщение
	 *
	 * @param connectionString
	 * @param patientInfo Карта сведений о пациенте
	 */
	public XMLMessage(String connectionString,
			HashMap<String, String> patientInfo) {
		this.connectionString = connectionString;
		this.patientInfo = patientInfo;
		// Получаем карту кодов услуг
		createJDBC();
		executeQuery("SelectServiceCode");
		try {
			while (resultSet.next()) {
				serviceCodes.put(resultSet.getString("ItemCode"),
						resultSet.getInt("ItemID"));
			}
		} catch (SQLException ex) {
			showExceptionMessage(ex);
		} finally {
			closeJDBC();
		}
	}

	/**
	 * Отправка сообщения
	 */
	protected void sendMessage() {
		String serviceCode = patientInfo.get("code");
		int serviceID = serviceCodes.get(serviceCode);
		int paidPeriod;
		if (serviceID == 4) {
			paidPeriod = 3;
		} else {
			paidPeriod = 1;
		}
		patientInfo.put("paidPeriod", "" + paidPeriod);
		// Проверяем наличие адреса электронной почты
		String eMail = patientInfo.get("email");
		if ((eMail == null) || eMail.isEmpty()) {
			showErrorMessage("Не указан адрес электронной почты");
			return;
		}
		if (serviceID < 3) {
			// Проверяем наличие идентификатора исследования
			String uid = patientInfo.get("uid");
			if ((uid == null) || uid.isEmpty()) {
				showErrorMessage("Не указан уникальный номер исследования");
				return;
			}
			// Проверяем наличие пути к папкам с исследованиями
			String pathName = patientInfo.get("path");
			if ((pathName == null) || pathName.isEmpty()) {
				showErrorMessage("Не указан путь к папкам с исследованиями");
				return;
			}
			// Проверяем корректность пути к папакам с исследованиями
			File path = new File(pathName);
			if (!path.exists()) {
				showErrorMessage("Путь к папкам с исследованиями не найден");
				return;
			}
			// Ищем папку с исследованием
			File[] folders = path.listFiles();
			for (File folder : folders) {
				String folderName = folder.getName();
				if (folderName.contains(uid)) {
					patientInfo.put("path", pathName + folderName);
					break;
				}
			}
			if (patientInfo.get("path").equals(pathName)) {
				showErrorMessage("Папка с исследованием не найдена");
				return;
			}
		}
		// Отправляем XML сообщение на сервер
		Document response = sendMessage(TEST_METHOD_NAME);
		if (response == null) {
			return;
		}
		// Читаем ответ
		Element element = response.getDocumentElement();
		if (checkError(element)) {
			return;
		}
		Node userExist = element.getElementsByTagName("UserExists").item(0);
		if (Boolean.parseBoolean(userExist.getTextContent())) {
			// Кабинет создан
			switch (serviceID) {
				case 1:
					// Проплачиваем кабинет и размещаем исследование
					response = sendMessage(UPLOAD_METHOD_NAME);
					break;
				case 2:
					// Размещаем дополнительное исследование
					response = sendMessage(UPLOAD_METHOD_NAME);
					break;
				default:
					// Продлеваем кабинет
					response = sendMessage(UPDATE_METHOD_NAME);
					break;
			}
		} else {
			// Кабинет не создан
			switch (serviceID) {
				case 1:
					// Создаем кабинет и размещаем исследование
					response = sendMessage(UPLOAD_METHOD_NAME);
					break;
				case 2:
					showErrorMessage("Личный кабинет пациента не найден.\n"
							+ "Необходимо указать услугу \"Открытие "
							+ "кабинета и размещение первого исследования\"");
					return;
				default:
					showErrorMessage("Личный кабинет пациента не создавался.\n"
							+ "Указана некорректная услуга.");
					return;
			}
		}
		if (response == null) {
			return;
		}
		// Читаем ответ
		element = response.getDocumentElement();
		if (!checkError(element)) {
			JOptionPane.showMessageDialog(null,
					"Выполнение команды успешно завершено",
					SYSTEM_MESSAGE_CAPTION, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Отправка XML сообщения
	 *
	 * @param commandKey Ключ команды
	 * @return XML документ с ответом службы
	 */
	protected Document sendMessage(String commandKey) {
		// Создаем XML сообщение
		Document document = createDocument();
		createMessage(document, commandKey, null, null);
		// Отправляем на сервер
		return sendToServer(document, commandKey);
	}

	/**
	 * Проверка на ошибку
	 *
	 * @param element Элемент XML документа
	 * @return Признак ошибки
	 */
	private boolean checkError(Element element) {
		boolean error = false;
		if (element.getNodeName().equals("Error")) {
			error = true;
			NodeList nodeList = element.getElementsByTagName("Message");
			showErrorMessage(nodeList.item(0).getTextContent());
		}
		return error;
	}

	/**
	 * Создание XML сообщения
	 *
	 * @param document XML документ
	 * @param commandKey Ключ команды
	 * @param parentID Идентификатор родительского объекта
	 * @param parent Родительский объект
	 */
	protected void createMessage(Document document, String commandKey,
			String parentID, Element parent) {
		ArrayList<String[]> levelArray = new ArrayList<>();
		createJDBC();
		if (parentID == null) {
			executeQuery("SelectXMLScheme '" + commandKey + "'");
		} else {
			executeQuery("SelectXMLScheme '" + commandKey + "', '"
					+ parentID + "'");
		}
		try {
			while (resultSet.next()) {
				levelArray.add(new String[]{
					resultSet.getString("ItemName"),
					resultSet.getString("ItemID")});
			}
			for (String[] item : levelArray) {
				Element element = document.createElement(item[0]);
				String text = patientInfo.get(item[0]);
				if (text != null) {
					element.appendChild(document.createTextNode(text));
				}
				if (parentID == null) {
					document.appendChild(element);
				} else {
					parent.appendChild(element);
				}
				createMessage(document, commandKey, item[1], element);
			}
		} catch (SQLException ex) {
			showExceptionMessage(ex);
		} finally {
			closeJDBC();
		}
	}

	/**
	 * Создание "пустого" XML документа
	 *
	 * @return XML документ
	 */
	private Document createDocument() {
		return createDocument(null);
	}

	/**
	 * Создание XML документа с данными из входного потока
	 *
	 * @param inputStream Входной поток данных
	 * @return XML документ
	 */
	private Document createDocument(InputStream inputStream) {
		Document document = null;
		try {
			DocumentBuilderFactory documentBuilderFactory
					= DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder
					= documentBuilderFactory.newDocumentBuilder();
			if (inputStream == null) {
				document = documentBuilder.newDocument();
			} else {
				document = documentBuilder.parse(inputStream);
			}
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			showExceptionMessage(ex);
		}
		return document;
	}

	/**
	 * Сохранение XML файла на диск
	 *
	 * @param document XML документ
	 * @param commandKey Ключ команды
	 */
	protected void saveToFile(Document document, String commandKey) {
		try {
			TransformerFactory transformerFactory
					= TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			File file = new File(TEMPORARY_FILE_PATH_NAME + commandKey + ".xml");
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
			Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
		} catch (TransformerConfigurationException ex) {
			showExceptionMessage(ex);
		} catch (TransformerException | IOException ex) {
			showExceptionMessage(ex);
		}
	}

	/**
	 * Отправка XML файла POST запросом
	 *
	 * @param document XML документ
	 * @param commandKey Ключ команды
	 */
	private Document sendToServer(Document document, String commandKey) {
		Document response = null;
		HttpURLConnection urlConnection = null;
		String serviceAddress = patientInfo.get("serviceAddress") + commandKey;
		try {
			URL url = new URL(serviceAddress);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/xml");
			OutputStream outputStream = urlConnection.getOutputStream();
			TransformerFactory transformerFactory
					= TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source, result);
			outputStream.flush();
			// Получаем ответ
			if (urlConnection.getResponseCode() == 200) {
				response = createDocument(urlConnection.getInputStream());
			} else {
				showErrorMessage("Ошибка " + urlConnection.getResponseCode()
						+ " - " + urlConnection.getResponseMessage());
			}
		} catch (MalformedURLException ex) {
			showExceptionMessage(ex);
		} catch (IOException ex) {
			showExceptionMessage(ex);
		} catch (TransformerConfigurationException ex) {
			showExceptionMessage(ex);
		} catch (TransformerException ex) {
			showExceptionMessage(ex);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return response;
	}

	/**
	 * Создание JDBC объектов
	 */
	private void createJDBC() {
		try {
			// Установка соединения
			connection = DriverManager.getConnection(connectionString);
			// Создание объекта для SQL запроса
			statement = connection.createStatement();
		} catch (SQLException ex) {
			showExceptionMessage(ex);
		}
	}

	/**
	 * Выполнение запроса
	 *
	 * @param queryString Строка запроса к SQL серверу
	 */
	private void executeQuery(String queryString) {
		try {
			// Получение набора данных
			resultSet = statement.executeQuery(queryString);
		} catch (SQLException ex) {
			showExceptionMessage(ex);
		}
	}

	/**
	 * Закрытие JDBC объектов
	 */
	private void closeJDBC() {
		if (resultSet != null) {
			try {
				if (!resultSet.isClosed()) {
					resultSet.close();
				}
			} catch (SQLException ex) {
				showExceptionMessage(ex);
			}
		}
		if (statement != null) {
			try {
				if (!statement.isClosed()) {
					statement.close();
				}
			} catch (SQLException ex) {
				showExceptionMessage(ex);
			}
		}
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException ex) {
				showExceptionMessage(ex);
			}
		}
	}

	/**
	 * Вывод сообщения об ошибке
	 *
	 * @param errorMessage Сообщение об ошибке
	 */
	private void showErrorMessage(String errorMessage) {
		JOptionPane.showMessageDialog(null, COMMAND_CANCEL_MESSAGE
				+ errorMessage, SYSTEM_MESSAGE_CAPTION,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Вывод сообщения о возникшем исключении
	 *
	 * @param ex Исключение
	 */
	private void showExceptionMessage(Exception ex) {
		JOptionPane.showMessageDialog(null, "Ошибка:\n" + ex,
				SYSTEM_MESSAGE_CAPTION, JOptionPane.ERROR_MESSAGE);
	}
}
