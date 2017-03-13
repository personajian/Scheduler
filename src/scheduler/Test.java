package scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class Test {
	public final static String filePath = "C:/Users/Guanglei Pan/Desktop/task/";

	public static String add(String date, int gap) {
		String[] time = date.split("-");
		int year = Integer.parseInt(time[0]);
		int month = Integer.parseInt(time[1]);
		int day = Integer.parseInt(time[2]);
		year = year + gap;
		StringBuilder result = new StringBuilder();
		result.append(year);
		result.append("-");
		if (month < 10) {
			result.append("0");
		}
		result.append(month);
		result.append("-");
		if (day < 10) {
			result.append("0");
		}
		result.append(day);
		return result.toString();
	}

	private static void setSecondHead(HSSFRow head, HSSFCellStyle cellStyle) {

		HSSFCell cell = null;

		cell = head.createCell(0);
		cell.setCellValue("��������");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(1);
		cell.setCellValue("��ͬ��");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(2);
		cell.setCellValue("�ͺ�");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(3);
		cell.setCellValue("���");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(4);
		cell.setCellValue("��Ʒ���");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(5);
		cell.setCellValue("ʣ������");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(6);
		cell.setCellValue("����Ա");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(7);
		cell.setCellValue("�ͻ�");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(8);
		cell.setCellValue("��������");
		cell.setCellStyle(cellStyle);
	}

	private static void setFirstHead(HSSFRow head, HSSFCellStyle cellStyle) {

		HSSFCell cell = null;

		cell = head.createCell(0);
		cell.setCellValue("��������");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(1);
		cell.setCellValue("��ͬ��");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(2);
		cell.setCellValue("�ͺ�");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(3);
		cell.setCellValue("���");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(4);
		cell.setCellValue("��Ʒ���");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(5);
		cell.setCellValue("ʣ������");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(6);
		cell.setCellValue("����Ա");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(7);
		cell.setCellValue("�ͻ�");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(8);
		cell.setCellValue("��������");
		cell.setCellStyle(cellStyle);
	}

	public static String minus(String date) {
		String[] time = date.split("-");
		int year = Integer.parseInt(time[0]);
		int month = Integer.parseInt(time[1]);
		int day = Integer.parseInt(time[2]);

		boolean isLeapYear = (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
		int[] map = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (isLeapYear) {
			map[2] += 1;
		}
		if (day == 1) {
			if (month == 1) {
				day = map[12];
				month = 12;
				year--;
			} else {
				month--;
				day = map[month];
			}
		} else {
			day--;
		}
		StringBuilder result = new StringBuilder();
		result.append(year);
		result.append("-");
		if (month < 10) {
			result.append("0");
		}
		result.append(month);
		result.append("-");
		if (day < 10) {
			result.append("0");
		}
		result.append(day);
		return result.toString();
	}

	private static boolean isWeekend(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date bdate = format.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bdate);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 6 || week == 0) {// 0�������գ�6��������
			return true;
		}
		return false;
	}

	public static Map<String, Integer> getPriority() {
		HashMap<String, Integer> priority = new HashMap<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath + "priority.txt"));// ����һ��BufferedReader������ȡ�ļ�
			String s = null;
			while ((s = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
				String[] p = s.split("/");
				priority.put(p[0], Integer.parseInt(p[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return priority;
	}

	public static void writePriority(Map<String, Integer> priority) {
		try {
			// ����һ��FileWriter����
			FileWriter fw = new FileWriter(filePath + "priority.txt");
			for (String key : priority.keySet()) {
				fw.write(key);
				fw.write("/");
				fw.write(String.valueOf(priority.get(key)));
				fw.write("\r\n");
				// fw.write("");
			}
			// ˢ�»�����
			fw.flush();
			// �ر��ļ�������
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// public static void main(String[] args) throws IOException, ParseException
	// {
	// Map<String, Integer> priority = new HashMap<>();
	// priority.put("1", 1);
	// priority.put("2", 2);
	// priority.put("3", 3);
	// writePriority(priority);
	// Map<String, Integer> p = getPriority();
	// for (String key: p.keySet()){
	// System.out.println(key + " " + p.get(key));
	// }
	// }

}
