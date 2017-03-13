package scheduler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Font;

public class ScheduleOrder {
	private String srcPath;
	private String desPath;
	private String[][] contents;
	private int number;
	private ArrayList<Task> taskset;
	private HashSet<String> typeSet;

	private Set<String> holiday;
	private Set<String> extrawork;

	public static String srcFilePath = "C:/Users/Guanglei Pan/Desktop/task/";
	public static String desFilePath = "C:/Users/Guanglei Pan/Desktop/task/";

	public ScheduleOrder() {
		srcPath = srcFilePath + "orders.xls";
		desPath = desFilePath + new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()) + "_totaltask.xls";
		taskset = new ArrayList<Task>();
		typeSet = new HashSet<String>();
		holiday = new HashSet<String>();
		extrawork = new HashSet<String>();
		number = 0;
	}

	public ScheduleOrder(String src, String des) {
		srcPath = src;
		desPath = des;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public void setDesPath(String desPath) {
		this.desPath = desPath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public String getDesPath() {
		return desPath;
	}

	public String[][] getContents() {
		return contents;
	}

	// 对contents进行排序，按截止日期从小到大排序
	public void sort() {
		Arrays.sort(contents, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				String date1 = o1[8];
				String date2 = o2[8];
				Date d1 = null, d2 = null;
				try {
					d1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
					d2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (d1.before(d2))
					return -1;
				if (d1.after(d2))
					return 1;
				return 0;
			}
		});
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 * @return
	 */

	public String getCellValue(HSSFCell cell) {
		String s = "";
		if (cell == null)
			return " ";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			s = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:// 需要判断是数值还是日期
			if (HSSFDateUtil.isCellDateFormatted(cell))
				s = new SimpleDateFormat("yyyy-MM-dd").format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
			else
				s = String.valueOf((int) cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			s = " ";
			break;
		}
		return s;
	}

	public void read() throws FileNotFoundException, IOException {
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(srcPath));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		String[][] content = new String[rows][9];
		int j = 0;
		HSSFRow row = null;
		for (int i = 1; i <= rows; i++) {
			row = sheet.getRow(i);
			if (!(getCellValue(row.getCell(16)).equals("0"))) {// 看是否还有没做完的
				content[j][0] = getCellValue(row.getCell(4));// 凭证日期
				content[j][1] = getCellValue(row.getCell(8));// 合同号
				String type = getCellValue(row.getCell(10));// 物料名称
				if (!typeSet.contains(type))
					typeSet.add(type);
				content[j][2] = type;// 物料名称
				content[j][3] = getCellValue(row.getCell(11)) + " " + getCellValue(row.getCell(12)) + " "
						+ getCellValue(row.getCell(13));// 规格
				content[j][4] = getCellValue(row.getCell(2));// 条码范围
				content[j][5] = getCellValue(row.getCell(16));// 未清数
				content[j][6] = getCellValue(row.getCell(19));// 业务员
				content[j][7] = getCellValue(row.getCell(21));// 客户
				content[j][8] = getCellValue(row.getCell(33));// 需求日期
				j++;
			}
		}
		number = j;
		contents = new String[j][9];
		for (int i = 0; i < j; i++) {
			for (int k = 0; k < 9; k++) {
				contents[i][k] = content[i][k];
			}
		}
		wb.close();

		// 下面是读取节假日信息
		String path = srcFilePath + "holidays.xls";// 模具文件所在位置
		fs = new POIFSFileSystem(new FileInputStream(path));
		wb = new HSSFWorkbook(fs);
		sheet = wb.getSheetAt(0);
		rows = sheet.getLastRowNum();
		row = null;
		// 法定节假日
		for (int i = 1; i <= rows; i++) {
			row = sheet.getRow(i);
			if (!getCellValue(row.getCell(1)).trim().equals("")) {
				String begin = getCellValue(row.getCell(1));// 某个节假日起始日期
				String end = getCellValue(row.getCell(2));// 某个节假日终止日期
				for (; begin.compareTo(end) <= 0; begin = add(begin)) {
					holiday.add(begin);
				}
			}

		}
		// 加班日期
		sheet = wb.getSheetAt(1);
		rows = sheet.getLastRowNum();
		row = null;
		for (int i = 1; i <= rows; i++) {
			row = sheet.getRow(i);
			String date = getCellValue(row.getCell(0));
			extrawork.add(date);

		}
	}

	private void setHead(HSSFRow head, HSSFCellStyle cellStyle) {
		HSSFCell cell = null;

		cell = head.createCell(0);
		cell.setCellValue("订单日期");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(1);
		cell.setCellValue("合同号");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(2);
		cell.setCellValue("型号");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(3);
		cell.setCellValue("规格");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(4);
		cell.setCellValue("产品编号");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(5);
		cell.setCellValue("剩余数量");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(6);
		cell.setCellValue("销售员");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(7);
		cell.setCellValue("客户");
		cell.setCellStyle(cellStyle);

		cell = head.createCell(8);
		cell.setCellValue("交付日期");
		cell.setCellStyle(cellStyle);
	}

	private void setRow(HSSFRow row, String[] s, HSSFCellStyle cellStyle) {
		HSSFCell cell = null;
		for (int i = 0; i < s.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(s[i]);
			cell.setCellStyle(cellStyle);
		}
	}

	private void setSheetColAuto(HSSFSheet sheet) {
		for (int i = 0; i < 9; i++)
			sheet.autoSizeColumn(i);
	}

	public void writeTotalTask() throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("总台账");

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		HSSFRow head = sheet.createRow(0);
		setHead(head, cellStyle);

		int i = 1;
		HSSFRow row = null;
		while ((i - 1) < contents.length) {
			row = sheet.createRow(i);
			setRow(row, contents[i - 1], cellStyle);
			i++;
		}
		setSheetColAuto(sheet);

		sheet.setColumnWidth(2, (int) ((5 + 0.72) * 256));
		sheet.setColumnWidth(6, (int) ((10 + 0.72) * 256));
		sheet.setColumnWidth(7, (int) ((37 + 0.72) * 256));

		OutputStream out = new FileOutputStream(desPath);
		wb.write(out);
		out.close();
		wb.close();
	}

	public void writeSingleTask() throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HashMap<String, HSSFSheet> typeMap = new HashMap<>();
		HashMap<String, Integer> rowMap = new HashMap<>();
		for (String type : typeSet) {
			typeMap.put(type, wb.createSheet(type));
			rowMap.put(type, 1);
		}
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);

		for (String type : typeSet) {
			HSSFRow head = typeMap.get(type).createRow(0);
			setHead(head, cellStyle);
		}
		HSSFRow row = null;
		for (int i = 0; i < contents.length; i++) {
			String s = contents[i][2];
			if (typeSet.contains(s)) {
				int k = rowMap.get(s);
				row = typeMap.get(s).createRow(k);
				k++;
				rowMap.put(s, k);
				setRow(row, contents[i], cellStyle);
			}
		}
		for (String type : typeSet) {
			setSheetColAuto(typeMap.get(type));
		}
		OutputStream out = new FileOutputStream(
				desFilePath + new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()) + "_singletask.xls");
		wb.write(out);
		out.close();
		wb.close();
	}

	@SuppressWarnings("deprecation")
	public HSSFSheet getPlanHeaderSheet(HSSFWorkbook wb, HSSFSheet sheet, int rowNumber) {
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);

		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16);
		font.setFontName("新宋体");
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// 2.生成样式对象
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFont(font); // 调用字体样式对象

		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 19));
		HSSFRow row = sheet.createRow(rowNumber);
		HSSFCell cell = row.createCell(0);
		cell.setCellStyle(style);

		cell.setCellValue("日计划完成");

		// 1.生成字体对象
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 10);
		font1.setFontName("新宋体");
		font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// 2.生成样式对象
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style1.setFont(font1); // 调用字体样式对象

		rowNumber++;
		HSSFRow row1 = sheet.createRow(rowNumber);
		HSSFRow row2 = sheet.createRow(rowNumber + 1);
		HSSFCell cell1 = null;
		HSSFCell cell2 = null;

		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + 1, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + 1, 2, 2));
		cell = row1.createCell(0);
		cell.setCellStyle(style1);
		cell.setCellValue("型号");

		cell = row1.createCell(1);
		cell.setCellStyle(style1);
		cell.setCellValue("规格");

		cell = row1.createCell(2);
		cell.setCellStyle(style1);
		cell.setCellValue("编号");

		cell1 = row1.createCell(3);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 3, 6));
		cell1.setCellValue("线圈");
		cell1.setCellStyle(style1);

		cell2 = row2.createCell(3);
		cell2.setCellStyle(style1);
		cell2.setCellValue("时间");

		cell2 = row2.createCell(4);
		cell2.setCellStyle(style1);
		cell2.setCellValue("计划数");

		cell2 = row2.createCell(5);
		cell2.setCellStyle(style1);
		cell2.setCellValue("实绩");

		cell2 = row2.createCell(6);
		cell2.setCellStyle(style1);
		cell2.setCellValue("结存");

		cell1 = row1.createCell(7);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 7, 10));
		cell1.setCellValue("浇注");
		cell1.setCellStyle(style1);

		cell2 = row2.createCell(7);
		cell2.setCellStyle(style1);
		cell2.setCellValue("时间");

		cell2 = row2.createCell(8);
		cell2.setCellStyle(style1);
		cell2.setCellValue("计划数");

		cell2 = row2.createCell(9);
		cell2.setCellStyle(style1);
		cell2.setCellValue("实绩");

		cell2 = row2.createCell(10);
		cell2.setCellStyle(style1);
		cell2.setCellValue("结存");

		cell1 = row1.createCell(11);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 11, 14));
		cell1.setCellValue("检验");
		cell1.setCellStyle(style1);

		cell2 = row2.createCell(11);
		cell2.setCellStyle(style1);
		cell2.setCellValue("时间");

		cell2 = row2.createCell(12);
		cell2.setCellStyle(style1);
		cell2.setCellValue("计划数");

		cell2 = row2.createCell(13);
		cell2.setCellStyle(style1);
		cell2.setCellValue("实绩");

		cell2 = row2.createCell(14);
		cell2.setCellStyle(style1);
		cell2.setCellValue("结存");

		cell1 = row1.createCell(15);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 15, 18));
		cell1.setCellValue("成品入库");
		cell1.setCellStyle(style1);

		cell2 = row2.createCell(15);
		cell2.setCellStyle(style1);
		cell2.setCellValue("时间");

		cell2 = row2.createCell(16);
		cell2.setCellStyle(style1);
		cell2.setCellValue("计划数");

		cell2 = row2.createCell(17);
		cell2.setCellStyle(style1);
		cell2.setCellValue("实绩");

		cell2 = row2.createCell(18);
		cell2.setCellStyle(style1);
		cell2.setCellValue("结存");

		return sheet;
	}

	@SuppressWarnings("deprecation")
	public HSSFSheet getOrderHeaderSheet(HSSFWorkbook wb, String id) {
		HSSFSheet sheet = wb.createSheet("生产计划单" + id);

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);

		// 1.生成字体对象
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16);
		font.setFontName("新宋体");
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// 2.生成样式对象
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFont(font); // 调用字体样式对象

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell1 = row.createCell(0);
		cell1.setCellStyle(style);
		cell1.setCellValue("客户订单信息");

		// 1.生成字体对象
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 10);
		font1.setFontName("新宋体");
		font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// 2.生成样式对象
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style1.setFont(font1); // 调用字体样式对象

		HSSFRow row1 = sheet.createRow(1);
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 4, 4));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 5, 5));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 6, 6));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 7, 7));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));

		HSSFCell cell = row1.createCell(0);
		cell.setCellStyle(style1);
		cell.setCellValue("订单日期");

		cell = row1.createCell(1);
		cell.setCellStyle(style1);
		cell.setCellValue("合同号");

		cell = row1.createCell(2);
		cell.setCellStyle(style1);
		cell.setCellValue("型号");

		cell = row1.createCell(3);
		cell.setCellStyle(style1);
		cell.setCellValue("规格");

		cell = row1.createCell(4);
		cell.setCellStyle(style1);
		cell.setCellValue("编号");

		cell = row1.createCell(5);
		cell.setCellStyle(style1);
		cell.setCellValue("数量");

		cell = row1.createCell(6);
		cell.setCellStyle(style1);
		cell.setCellValue("销售员");

		cell = row1.createCell(7);
		cell.setCellStyle(style1);
		cell.setCellValue("客户");

		cell = row1.createCell(8);
		cell.setCellStyle(style1);
		cell.setCellValue("交货日期");

		return sheet;
	}

	public ArrayList<Task> getTask() {
		String orderNo = contents[0][1];
		Product prod = new Product(contents[0][2], contents[0][3], contents[0][4], Integer.parseInt(contents[0][5]),
				Integer.parseInt(contents[0][5]));
		ArrayList<Product> list = new ArrayList<Product>();
		list.add(prod);
		int i = 1;
		int cnt = 0;
		for (i = 1; i < number; i++) {
			if (orderNo.equals(contents[i][1])) {
				Product temp = new Product(contents[i][2], contents[i][3], contents[i][4],
						Integer.parseInt(contents[i][5]), Integer.parseInt(contents[i][5]));
				list.add(temp);
			} else {
				// public Task(String startTime, String orderNo,String salesMan,
				// String client,String endDate, ArrayList<Product> require)
				Task task = new Task(contents[cnt][0], orderNo, contents[cnt][6], contents[cnt][7], contents[cnt][8],
						false, list);
				cnt = i;
				taskset.add(task);
				list = new ArrayList<Product>();
				orderNo = contents[i][1];
				Product temp = new Product(contents[i][2], contents[i][3], contents[i][4],
						Integer.parseInt(contents[i][5]), Integer.parseInt(contents[i][5]));
				list.add(temp);
			}
		}
		i--;
		Task task = new Task(contents[i][0], orderNo, contents[i][6], contents[i][7], contents[i][8], false, list);
		taskset.add(task);
		return taskset;
	}

	/**
	 * @param date
	 * @return 当前日期前一天的日期
	 */

	public String minus(String date) {
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

	/**
	 * 
	 * @param date
	 * @return 当前日期加一天后的日期
	 */
	public String add(String date) {
		String[] time = date.split("-");
		int year = Integer.parseInt(time[0]);
		int month = Integer.parseInt(time[1]);
		int day = Integer.parseInt(time[2]);

		boolean isLeapYear = (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
		int[] map = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (isLeapYear) {
			map[2] += 1;
		}
		if (day == map[month]) {
			day = 1;
			if (month == 12) {
				month = 1;
				year += 1;
			} else {
				month += 1;
			}
		} else {
			day++;
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

	public HashMap<String, Moulds> getResource(String currentDate, String endDate) throws ParseException {
		HashMap<String, Moulds> resource = new HashMap<String, Moulds>();
		for (String date = currentDate; date.compareTo(endDate) <= 0; date = add(date)) {
			if (isWorkDay(date)) {
				System.out.println(date);
				resource.put(date, new Moulds());
			}
		}
		return resource;
	}

	/**
	 * 判断日期是否是双休日
	 * 
	 * @param date
	 * @return true 双休日<br>
	 *         false 不是双休日
	 * @throws ParseException
	 */
	private boolean isWeekend(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date bdate = format.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bdate);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 6 || week == 0) {// 0代表周日，6代表周六
			return true;
		}
		return false;
	}

	/**
	 * 判断日期是否是工作日
	 * 
	 * @param date
	 *            日期
	 * @return true 工作日<br>
	 *         false 不是工作日
	 * @throws ParseException
	 */
	public boolean isWorkDay(String date) throws ParseException {
		if (holiday.contains(date)) {// 国家法定节假日必然休息
			return false;
		}
		if (extrawork.contains(date)) {// 加班的日期
			return true;
		}
		if (isWeekend(date)) {// 双休日休息
			return false;
		}
		return true;

	}

	/**
	 * 读取txt文件中的优先级
	 * 
	 * @return 优先级
	 */
	private Map<String, Integer> getPriority() {
		HashMap<String, Integer> priority = new HashMap<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(srcFilePath + "priority.txt"));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				String[] p = s.split("/");
				priority.put(p[0], Integer.parseInt(p[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return priority;
	}

	private void setPriority(ArrayList<Task> taskset, Map<String, Integer> priority) {
		for (int i = 0; i < taskset.size(); i++) {
			Task temp = taskset.get(i);
			if (priority.containsKey(temp.getOrderNo())) {
				temp.setPriority(priority.get(temp.getOrderNo()));
			} else {// 如果不含有 则优先级为Integer.MAX_VALUE，数字最大优先级越低
				temp.setPriority(Integer.MAX_VALUE);
			}
		}
	}

	/**
	 * 将优先级写入txt文件
	 */
	private void writePriority(Map<String, Integer> priority) {
		try {
			// 创建一个FileWriter对象
			FileWriter fw = new FileWriter(srcFilePath + "priority.txt");
			for (String key : priority.keySet()) {
				fw.write(key);
				fw.write("/");
				fw.write(String.valueOf(priority.get(key)));
				fw.write("\r\n");
			}
			// 刷新缓冲区
			fw.flush();
			// 关闭文件流对象
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void process(ArrayList<Task> taskset, String currentDate) throws IOException, ParseException {
		int size = taskset.size();
		String endDate = taskset.get(size - 1).getEndDate();
		// 申请资源
		HashMap<String, Moulds> resource = new HashMap<String, Moulds>();
		resource = getResource(currentDate, endDate);

		HSSFWorkbook wb = new HSSFWorkbook();

		// 优先级主要是为了让已经安排的任务提前被安排
		Map<String, Integer> priority = getPriority();
		if (priority.size() != 0) {
			// 如果优先级不为空，则给任务设置优先级
			setPriority(taskset, priority);
			// 按优先级排序
			Collections.sort(taskset);
		}
		/**
		 * 写入excel
		 */
		Iterator<Task> it = taskset.iterator();

		Map<String, Integer> newPriority = new HashMap<String, Integer>();
		int cnt = 0;
		while (it.hasNext()) {
			Task temp = it.next();
			ArrayList<Product> require = temp.getRequire();
			/*
			 * begin为开始执行的时间，取订单开始时间和当前日期最大的那个。
			 * 
			 */
			String startTime = add(temp.getStartTime());
			String begin = startTime.compareTo(currentDate) > 0 ? startTime : currentDate;
			String end = temp.getEndDate();

			LinkedHashMap<String, ArrayList<Product>> taskTab = new LinkedHashMap<String, ArrayList<Product>>();
			// 考虑到执行过程是流水线式的，如果已经在执行，则不需要开始做线圈的两天时间！！！
			if (!temp.isExecuting()) {
				begin = add(begin);// 第二天做线圈
				begin = add(begin);// 改天做线圈，还需要再加一天才能做下一阶段的事情
									// 因此下面的for循环要有begin = add(begin);
			}
			for (; begin.compareTo(end) < 0; begin = add(begin)) {
				if (!isWorkDay(begin)) {
					continue;// 判断是否是工作日，如果不是工作日则不需要工作。
				}
				Moulds m = resource.get(begin);
				ArrayList<Product> dayTask = new ArrayList<Product>();
				for (int i = 0; i < require.size(); i++) {
					Product prod = require.get(i);
					m.setType(prod.getModelNo());
					m.setConfigure(prod.getStandard());
					if (prod.getNumber() != 0) {// 当所需产品还没完成时
						ArrayList<Capacity> res = m.apply(prod.getNumber());
						if (res.size() != 0) {// 可以分配，资源减去，任务减去
							// public Product(String modelNo, String standard,
							// String serial, int number)
							int sum = 0;
							for (int k = 0; k < res.size(); k++) {
								Capacity capacity = res.get(k);
								sum += capacity.getModelNumber();
							}
							Product prodTask = new Product(prod.getModelNo(), prod.getStandard(), prod.getSerial(),
									prod.getOriginalNumber(), prod.getNumber(), sum, res);
							// System.out.println(prod.getNumber());
							prod.setNumber(prod.getNumber() - sum);
							dayTask.add(prodTask);
						}
					}

				}
				// 记录每天的完成情况
				if (dayTask != null) {
					taskTab.put(begin, dayTask);
				}
			}

			// 判断任务是否已经完成，如果完成，则写入excel，如果没有，在txt文件中标注。
			if (isCompleted(temp)) {
				// 设置客户订单信息的头部
				HSSFSheet sheet = getOrderHeaderSheet(wb, temp.getOrderNo());
				// 记录该任务
				// 输出 客户订单信息
				// 1 订单日期
				newPriority.put(temp.getOrderNo(), cnt);
				cnt++;
				System.out.println("订单日期:" + temp.getStartTime());
				// 5 交货日期
				System.out.println("交货日期:" + temp.getEndDate());
				// 2 合同号
				System.out.println("合同号:" + temp.getOrderNo());
				// 3 销售员
				System.out.println("销售员:" + temp.getSalesMan());
				// 4 客户
				System.out.println("客户:" + temp.getClient());

				// 6 型号
				// 7 规格
				// 8 编号
				// 9 数量
				ArrayList<Product> req = temp.getRequire();

				HSSFRow hssfRow = null;
				HSSFCell hssfCell = null;
				int rowNumber = 3;
				for (int i = 0; i < req.size(); i++) {
					if ((hssfRow = sheet.getRow(rowNumber)) == null)
						hssfRow = sheet.createRow(rowNumber);
					Product product = req.get(i);

					// 型号
					hssfCell = hssfRow.createCell(2);
					hssfCell.setCellValue(product.getModelNo());

					// 规格
					hssfCell = hssfRow.createCell(3);
					hssfCell.setCellValue(product.getStandard());

					// 编号
					hssfCell = hssfRow.createCell(4);
					hssfCell.setCellValue(product.getSerial());

					// 数量
					hssfCell = hssfRow.createCell(5);
					hssfCell.setCellValue(product.getOriginalNumber());

					System.out.println("------------------------------");
					System.out.println("型号:" + product.getModelNo());
					System.out.println("规格:" + product.getStandard());
					System.out.println("编号:" + product.getSerial());
					System.out.println("数量:" + product.getOriginalNumber());
					rowNumber++;
				}

				hssfRow = sheet.getRow(3);

				rowNumber--;
				if (rowNumber == 3) {
					hssfCell = hssfRow.createCell(0);
					hssfCell.setCellValue(temp.getStartTime());

					hssfCell = hssfRow.createCell(1);
					hssfCell.setCellValue(temp.getOrderNo());

					hssfCell = hssfRow.createCell(6);
					hssfCell.setCellValue(temp.getSalesMan());

					hssfCell = hssfRow.createCell(7);
					hssfCell.setCellValue(temp.getClient());

					hssfCell = hssfRow.createCell(8);
					hssfCell.setCellValue(temp.getEndDate());
				} else {
					// 订单日期
					sheet.addMergedRegion(new CellRangeAddress(3, rowNumber, 0, 0));
					hssfCell = hssfRow.createCell(0);
					hssfCell.setCellValue(temp.getStartTime());

					// 合同号
					sheet.addMergedRegion(new CellRangeAddress(3, rowNumber, 1, 1));
					hssfCell = hssfRow.createCell(1);
					hssfCell.setCellValue(temp.getOrderNo());

					// 销售员
					sheet.addMergedRegion(new CellRangeAddress(3, rowNumber, 6, 6));
					hssfCell = hssfRow.createCell(6);
					hssfCell.setCellValue(temp.getSalesMan());

					// 客户
					sheet.addMergedRegion(new CellRangeAddress(3, rowNumber, 7, 7));
					hssfCell = hssfRow.createCell(7);
					hssfCell.setCellValue(temp.getClient());

					// 交货日期
					sheet.addMergedRegion(new CellRangeAddress(3, rowNumber, 8, 8));
					hssfCell = hssfRow.createCell(8);
					hssfCell.setCellValue(temp.getEndDate());

				}
				System.out.println("\n\nTask Table");
				System.out.println("************************************************************");

				// 日计划完成 空三行
				rowNumber += 4;
				sheet = getPlanHeaderSheet(wb, sheet, rowNumber);

				rowNumber += 3;
				for (String date : taskTab.keySet()) {
					ArrayList<Product> tempTask = taskTab.get(date);
					if (tempTask.size() != 0) {
						System.out.println("日期:" + date);
						hssfRow = sheet.createRow(rowNumber);
						// 浇注时间
						hssfCell = hssfRow.createCell(8);
						hssfCell.setCellValue(date);
						for (int i = 0; i < tempTask.size(); i++) {
							hssfRow = sheet.createRow(rowNumber);
							Product p = tempTask.get(i);
							System.out.print("型号:" + p.getModelNo());
							System.out.print("  规格:" + p.getStandard());
							System.out.print("  编号:" + p.getSerial());
							System.out.println("  计划数:" + p.getSum());
							// 型号
							hssfCell = hssfRow.createCell(0);
							hssfCell.setCellValue(p.getModelNo());
							// 规格
							hssfCell = hssfRow.createCell(1);
							hssfCell.setCellValue(p.getStandard());
							// 编号
							hssfCell = hssfRow.createCell(2);
							hssfCell.setCellValue(p.getSerial());

							String dateTemp = minus(date);
							/*** 线圈 ***/
							// 时间
							hssfCell = hssfRow.createCell(3);
							hssfCell.setCellValue(dateTemp);
							// 计划数
							hssfCell = hssfRow.createCell(4);
							hssfCell.setCellValue(p.getSum());
							// 实绩
							hssfCell = hssfRow.createCell(5);
							hssfCell.setCellValue("");
							// 结存
							hssfCell = hssfRow.createCell(6);
							hssfCell.setCellValue("");

							/*** 浇注 ***/
							// 时间
							hssfCell = hssfRow.createCell(7);
							hssfCell.setCellValue(date);
							// 计划数
							hssfCell = hssfRow.createCell(8);
							hssfCell.setCellValue(p.getSum());
							// 实绩
							hssfCell = hssfRow.createCell(9);
							hssfCell.setCellValue("");
							// 结存
							hssfCell = hssfRow.createCell(10);
							hssfCell.setCellValue("");

							dateTemp = add(date);
							/*** 检验 ***/
							// 时间
							hssfCell = hssfRow.createCell(11);
							hssfCell.setCellValue(dateTemp);
							// 计划数
							hssfCell = hssfRow.createCell(12);
							hssfCell.setCellValue(p.getSum());
							// 实绩
							hssfCell = hssfRow.createCell(13);
							hssfCell.setCellValue("");
							// 结存
							hssfCell = hssfRow.createCell(14);
							hssfCell.setCellValue("");

							/*** 成品入库 ***/
							// 时间
							hssfCell = hssfRow.createCell(15);
							hssfCell.setCellValue(dateTemp);
							// 计划数
							hssfCell = hssfRow.createCell(16);
							hssfCell.setCellValue(p.getSum());
							// 实绩
							hssfCell = hssfRow.createCell(17);
							hssfCell.setCellValue("");
							// 结存
							hssfCell = hssfRow.createCell(18);
							hssfCell.setCellValue("");
							rowNumber++;
						}
						rowNumber++;
					}

				}
				System.out.println("************************************************************");
				for (int i = 0; i < 9; i++)
					sheet.autoSizeColumn(i);
			} else {
				//
				System.out.print(temp.getOrderNo());
				System.out.println("    估计不能完成");
				for (String date : taskTab.keySet()) {
					ArrayList<Product> dayTask = taskTab.get(date);
					for (int i = 0; i < dayTask.size(); i++) {
						Product prod = dayTask.get(i);
						ArrayList<Capacity> result = prod.getResult();
						Moulds mould = resource.get(date);
						// 将相应型号的磨具资源还原
						for (int k = 0; k < result.size(); k++) {
							Capacity capacity = result.get(k);
							mould.restore(capacity);
						}

					}
				}
			}

			System.out.println("--------------------");
		}
		writePriority(newPriority);// 产生新的优先级
		OutputStream out = new FileOutputStream(
				desFilePath + new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()) + "_TaskTable.xls");
		wb.write(out);
		out.close();
		wb.close();
	}

	/**
	 * 
	 * @param task
	 * @return 如果任务完成，则返回true,否则返回false
	 * 
	 */
	public boolean isCompleted(Task task) {
		ArrayList<Product> require = task.getRequire();
		for (int i = 0; i < require.size(); i++) {
			Product prod = require.get(i);
			if (prod.getNumber() != 0) {
				return false;
			}
		}
		return true;
	}

	// public static void main(String[] args) throws FileNotFoundException,
	// IOException, ParseException {
	// ScheduleOrder scheduleOrder = new ScheduleOrder();
	// scheduleOrder.read();
	// scheduleOrder.sort();
	// scheduleOrder.writeTotalTask();
	// scheduleOrder.writeSingleTask();
	// ArrayList<Task> taskset = scheduleOrder.getTask();
	// /*
	// * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
	// * String currentDate = df.format(new Date());//获取当前系统时间
	// */
	// String currentDate = "2017-01-10";//
	// scheduleOrder.process(taskset, currentDate);
	// }
}
