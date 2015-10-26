﻿package calc;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
public class Calculator {
	private static final int MAX_PEOPLE = 100;
	private double costOneDay;            // Сс
	private int days;                     // H
	private int numberOfPeople = 0;       // n
	private double[] personalAviaCost = new double[MAX_PEOPLE];    // Can
	private double[] personalTransferCost = new double[MAX_PEOPLE];// Синд
	private JTextField jCostOneDay = new JTextField();
	private JTextField jDays = new JTextField();
	private JTextField jNumberOfPeople = new JTextField();
	private JTextField[] jPersonalAviaCost = new JTextField[MAX_PEOPLE];
	private JTextField[] jPersonalTransferCost = new JTextField[MAX_PEOPLE];
	// Для отображения ошибок
	private final JTextPane lError = new JTextPane(); 
	// Таблица для ввода стоимостей трансфера и авиаперелёта для
	// отдельных пассажиров.
	private final JPanel pTable = new JPanel(new GridLayout(0, 2, 4, 4));
	private final JFrame frame = new JFrame("Рассчёт стоимости тура");
	// Визуальные свойства текста
	SimpleAttributeSet attribs = new SimpleAttributeSet();
	private final DocumentListener onFillListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			process();
		}
		public void removeUpdate(DocumentEvent e) {
			process();
		}
		public void insertUpdate(DocumentEvent e) {
			process();
		}
		public void process() {
		// Сперва проверяем всю форму.
			if (jNumberOfPeople.getText().equals("") || !validate()) {
				return;
			}
			int prevNumberOfPeople = numberOfPeople;
			numberOfPeople = Integer.parseInt(jNumberOfPeople.getText());
			buildTouristsTable(prevNumberOfPeople);
		}
	};
	public static void main(String[] args) {
		new Calculator();
	}
	public Calculator() {
		// Создаём окно приложения
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Создаём внутренности окна
		JPanel pMain = new JPanel();
		
		JPanel pFirst = new JPanel();
		pFirst.setPreferredSize(new Dimension(300,110));
		pFirst.setLayout(new BoxLayout(pFirst, BoxLayout.Y_AXIS));
		JPanel pTableColsHeadings = new JPanel(new GridLayout(0, 2));
		JPanel pButtons = new JPanel(new GridLayout(0, 2));
		JPanel pTableHeader = new JPanel();
		JPanel pError = new JPanel();

		//panel.setLayout(new GridLayout(0, 1));
		pMain.setLayout(new BoxLayout(pMain, BoxLayout.Y_AXIS));
		pMain.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Содержимое первой панели
		JLabel lCostOneDay = new JLabel("Стоимость одного дня проживания");
		JLabel lDays = new JLabel("Количество дней проживания");
		JLabel lNumberOfPeople = new JLabel("Количество человек");

		// Содержимое второй панели
		JLabel lTourists = new JLabel("Индивидуальная стоимость");
		// Содержимое третьей панели
		JLabel lAvia = new JLabel("Перелёта");
		JLabel lTransfer = new JLabel("Трансфера");
// Содержимое панели с ошибками
		// Установаить внешний вид теста в сообщени об ошибке
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "Sans-Serif");
		StyleConstants.setBold(attribs, true);
		lError.setPreferredSize(new Dimension(200, 80));
		lError.setParagraphAttributes(attribs,true);
		// Установить цвет фона
		lError.setBackground(frame.getBackground());

		// Содержимое последней панели с кнопкой "Рассчитать"
		JButton bCalculate = new JButton("Рассчитать");

		// Обработчики событий
		jNumberOfPeople.getDocument().addDocumentListener(onFillListener);
		jCostOneDay.getDocument().addDocumentListener(onFillListener);
		jDays.getDocument().addDocumentListener(onFillListener);
		bCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!validate() || !validateFilled()) {
					return;
				}
				costOneDay = Double.parseDouble(jCostOneDay.getText());
				days = Integer.parseInt(jDays.getText());
				numberOfPeople = Integer.parseInt(jNumberOfPeople.getText());
				for (int i=0; i<numberOfPeople; i++) {
					personalAviaCost[i] = Double.parseDouble(jPersonalAviaCost[i].getText());
					personalTransferCost[i] = Double.parseDouble(jPersonalTransferCost[i].getText());
				}
				showAnswer(""+calculate());
			}
		});

		pTableColsHeadings.add(lAvia);
		lAvia.setHorizontalAlignment(SwingConstants.CENTER);
		pTableColsHeadings.add(lTransfer);
		lTransfer.setHorizontalAlignment(SwingConstants.CENTER);

		frame.setContentPane(pMain);
		frame.setResizable(false);
		// Собираем панели из элементов
		// Первая
		pFirst.add(lCostOneDay);
		pFirst.add(jCostOneDay);
		pFirst.add(lDays);
		pFirst.add(jDays);
		pFirst.add(lNumberOfPeople);
		pFirst.add(jNumberOfPeople);
		// Заголовок всей таблицы
		pTableHeader.add(lTourists);
		// Заголовки колонок таблицы
		pTableColsHeadings.add(lAvia);
		pTableColsHeadings.add(lTransfer);
		// Ошибки
		pError.add(lError);
		// Кнопки
		pButtons.add(bCalculate);
		// Собираем все панели на главной панели
		pMain.add(pFirst);
		pMain.add(pTableHeader);
		pMain.add(pTableColsHeadings);
		pMain.add(pTable);
		pMain.add(pError);
		pMain.add(pButtons);

		frame.pack();
		frame.setVisible(true);
	}
	// Возвращает true, если все поля формы валидные. Иначе возвращает false.
	// Форма считается валидной, если в каждом из полей текст ещё не введён
	// или введён текст, корректно преобразующийся к нужному числовому типу
	// данных.
private boolean validate() {
		if (!jNumberOfPeople.getText().equals("")) {
			try {
				Integer.parseInt(jNumberOfPeople.getText());
			} catch (Exception e) {
				//  Если одна из операций завершилась с ошибкой — форма не проходит валидацию.
				showError("В поле \"Количество человек\" должно быть введено целое число");
				return false;
			}
			if (Integer.parseInt(jNumberOfPeople.getText()) > MAX_PEOPLE) {
				showError("Количество человек не может превышать "+MAX_PEOPLE);
				return false;
			}
		}
		if (!jCostOneDay.getText().equals("")) {
			try {
				double tCostOneday = Double.parseDouble(jCostOneDay.getText());
				if (!isCorrectCurrencyValue(tCostOneday)) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				showError("В поле \"Стоимость одного дня проживания\" должно быть введено целое число или дробь с точностью до одной сотой");
				return false;
			}
		}
		if (!jDays.getText().equals("")) {
			try {
				Integer.parseInt(jDays.getText());
			} catch (Exception e) {
				showError("В поле \"Количество дней проживания\" должно быть введено целое число");
				return false;
			}
		}
		try {
			for (int i=0; i<numberOfPeople; i++) {
				if (!jPersonalAviaCost[i].getText().equals("")) {
					double tPersonalAviaCost = Double.parseDouble(jPersonalAviaCost[i].getText());
					if (!isCorrectCurrencyValue(tPersonalAviaCost)) {
						throw new RuntimeException();
					}
				}
				if (!jPersonalTransferCost[i].getText().equals("")) {
					double tPersonalTransferCost = Double.parseDouble(jPersonalTransferCost[i].getText());
					if (!isCorrectCurrencyValue(tPersonalTransferCost)) {
						throw new RuntimeException();
					}
				}
			}
		} catch (Exception e) {
			showError("Стоимость авиаперелёта и трансфера должна быть целым чисом или десятичной дробью с точнотью до одной сотой");
			return false;
		}
		// Если все проверки пройдены — не показывать ошибку и вернуть true.
		hideError();
		return true;
	}
	// Проверяет, является ли число double корректным значением для
	// обозначения количества валюты (имеет точность не более одной сотой).
	private boolean isCorrectCurrencyValue(double value) {
		String s = new Double(value).toString();
		if (
			(s.lastIndexOf(".") == -1
			|| s.lastIndexOf(".") == s.length()-3
			|| s.lastIndexOf(".") == s.length()-2)
			&& s.indexOf(".") == s.lastIndexOf(".")
		) {
		// Если третий с конца или второй с конца символ — точка, либо в
		// запси числа вообще нет точки, и в записи числа есть только одна
		// точка, то это число — корректное значение для количества валюты
			return true;
		}
		return false;
	}
	// Показать сообщение об ошибке
	private void showError(String text) {
		StyleConstants.setForeground(attribs, Color.RED);
		StyleConstants.setFontSize(attribs, 12);				
		lError.setParagraphAttributes(attribs,true);
		lError.setText(text);
	}
	private void showAnswer(String text) {
		StyleConstants.setForeground(attribs, Color.BLUE);
		StyleConstants.setFontSize(attribs, 20);				
		lError.setParagraphAttributes(attribs,true);
		lError.setText(text);
	}
	// Убрать сообщение об ошибке
	private void hideError() {
		lError.setText("");
	}
// Суммировать значения в массиве от 0 до numberOfPeople
	private double sum(double[] array) {
		double s = 0;
		for (int i=0, l=numberOfPeople; i<l; i++) {
			s += array[i];
		}
		return s;
	}
	// Проверяет, все ли поля заполнены. Возвращает false и показывает
	// ошибку, если хотя бы  одно поле не заполнено. Возвращает true, если
	// все поля заполнены (не обязательно валидно заполнены — метод лишь
	// проверяет, что в них что-то есть).
	private boolean validateFilled() {
		if (jCostOneDay.getText().equals("")) {
			showError("Поле \"Стоимость одного дня проживания\" не заполнено");
			return false;
		}
		if (jDays.getText().equals("")) {
			showError("Поле \"Количество дней проживания\" не заполнено");
			return false;
		}
		if (jNumberOfPeople.getText().equals("")) {
			showError("Поле \"Количество человек\" не заполнено");
			return false;
		}
		for (int i=0; i<numberOfPeople; i++) {
			if (
				jPersonalAviaCost[i].getText().equals("") 
				|| jPersonalTransferCost[i].getText().equals("")
			) {
				showError("Не все поля в таблице заполнены");
				return false;
			}
		}
		return true;
	}
	// Строит таблицу с туристами, основываясь на введённом количестве
	// туристов (numberOfPeople).
	// prevNumberOfPeople — количество человек в предыдущем расчёте (см. комментарии ниже)
	private void buildTouristsTable(int prevNumberOfPeople) {
		for (int i=prevNumberOfPeople; i<numberOfPeople; i++) {
		// Создаём компоненты в таблице. Если таблица строится не в первый раз
		// после запуска программы, то мы можем использовать уже созданные
		// поля ввода, поэтому часть полей (от [0] до
		// [prevNumberOfPeople-1] можно не создавать заново). При первом
		// запуске buildTouristsTable() этот шаг просто пропустится, так как
		// prevNumberOfPeople == 0 при первом запуске.
			jPersonalAviaCost[i] = new JTextField();
			jPersonalTransferCost[i] = new JTextField();
			jPersonalAviaCost[i].getDocument().addDocumentListener(onFillListener);
			jPersonalTransferCost[i].getDocument().addDocumentListener(onFillListener);
			pTable.add(jPersonalAviaCost[i]);
			pTable.add(jPersonalTransferCost[i]);
		}
		for (int i=numberOfPeople; i<prevNumberOfPeople; i++) {
		// Теперь, наоборот, убираем лишние поля ввода из таблицы, если они остались с предыдущего шага.
			pTable.remove(jPersonalAviaCost[i]);
			pTable.remove(jPersonalTransferCost[i]);
		}
		frame.pack();
	}
	// Вычисляет стоимость сложного тура (Сст) по формуле
	private double calculate() {
		return costOneDay*days*numberOfPeople+sum(personalAviaCost)+sum(personalTransferCost);
	}
}