package scheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 模具资源数量以及模具匹配方法
 * 
 * @author Fuchao Chen
 * @version 2.0
 */
public class Moulds {
	private String type;
	private String configure;
	public static HashMap<String, Object> map = new HashMap<String, Object>();
	public static HashMap<String, Object> numbersMap = new HashMap<>();

	public Moulds() {
		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Moulds(String type, String configure) {
		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.type = type;
		this.configure = configure;
	}

	public static void read() throws IOException {
		String path = ScheduleOrder.srcFilePath + "models.xls";// 模具文件所在位置
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(path));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		HSSFRow row = null;
		for (int i = 1; i <= rows; i++) {
			row = sheet.getRow(i);
			String type = getCellValue(row.getCell(0));
			if (!map.containsKey(type))
				map.put(type, new LinkedHashMap<String, Object>());
			HashMap<String, Object> hashMap = (LinkedHashMap<String, Object>) map.get(type);
			String s1 = getCellValue(row.getCell(2));
			if (!hashMap.containsKey(s1))
				hashMap.put(s1, new LinkedHashMap<String, Object>());
			LinkedHashMap<String, Object> sonOfhashMap = (LinkedHashMap<String, Object>) hashMap.get(s1);
			sonOfhashMap.put(getCellValue(row.getCell(3)), getCellValue(row.getCell(1)));
			boolean flag = (getCellValue(row.getCell(5)).equals("T")) ? true : false;
			if (!hashMap.containsKey("flag"))
				hashMap.put("flag", flag);
			if (!numbersMap.containsKey(type))
				numbersMap.put(type, new LinkedHashMap<String, Object>());
			HashMap<String, Object> m = (LinkedHashMap<String, Object>) numbersMap.get(type);
			m.put(getCellValue(row.getCell(1)), Integer.parseInt(getCellValue(row.getCell(4))));
		}

		wb.close();
	}

	/**
	 * 显示map中的值： key表示类型： value： flag true表示可以共用，false表示不能共用 电流比 {类型=数量} 例如：
	 * LZZBJ9-10A1 5-800/5 {-;--;/=40,
	 * -;--;/;//;-/-;-/;/-;-/-/-;--/--;--/-;-/--;/--;--/;-/-/;/-/-;//-;---;---/;/---;---/-;-/---=20}
	 * 1000-2500/5 {-;--;/=8, //;-/-;-/;/-;---;--/;/--=4} flag false
	 */
	public static void print() {
		for (String key : map.keySet()) {
			// System.out.println(key);
			HashMap<String, Object> sonOfMap = (HashMap<String, Object>) map.get(key);
			for (String sonKey : sonOfMap.keySet()) {
				System.out.println(sonKey + " " + sonOfMap.get(sonKey));
			}
			// System.out.println("flag" + " " + sonOfMap.get("flag"));
			// System.out.println();
		}
	}

	private static String getCellValue(HSSFCell cell) {
		String s = "";
		if (cell == null)
			return " ";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			s = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC://
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

	/**
	 * 根据电流电压比，获取最大电流比 例如：5-800/5 返回800
	 * 
	 * @param s
	 * @return
	 */
	private int getMax(String s) {
		String[] temp = s.split("[,/-]");
		// a:配置中的电流比空间中的最大值
		double a = -1;
		for (String ss : temp) {
			if (!ss.contains("√"))
				if (a < Double.parseDouble(ss))
					a = Double.parseDouble(ss);
		}
		return (int) a;
	}

	/**
	 * 通过configure中的最大电流比，选择适用电流比区间(返回值)
	 * 
	 * @param a
	 * @param set
	 * @return
	 */
	private String getKey(int a, Set<String> set) {
		String key = "";
		for (String s : set) {
			if (!s.equals("flag")) {
				if (s.contains("-")) {
					int min = Integer.parseInt(s.substring(0, s.indexOf("-")));
					int max = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf("/")));
					if (a >= min && a <= max) {
						key = s;
					}
				} else {
					int m = Integer.parseInt(s.substring(0, s.indexOf("/")));
					if (a == m) {
						key = s;
					}
				}
			}
		}
		return key;
	}

	/**
	 * 根据容量类别适配
	 * 
	 * @param s
	 * @param strings
	 * @return
	 */
	private boolean Match(String s, String[] strings) {
		boolean flag = false;
		for (int i = 0; i < strings.length; i++) {
			if (!strings[i].equals("无")) {
				StringBuilder sb = new StringBuilder();
				sb.append("(\\d+)");
				for (int j = 0; j < strings[i].length(); j++) {
					sb.append(strings[i].charAt(j));
					sb.append("(\\d+)");
				}
				Matcher m = Pattern.compile(sb.toString()).matcher(s);
				flag = m.matches();
			} else {
				flag = true;
			}
			if (flag)
				break;
		}

		return flag;

	}

	/**
	 * 根据type、configure,返回能够满足的模具的类别
	 * 
	 * @return
	 */
	public LinkedList<String> match() {
		LinkedList<String> list = new LinkedList<>();
		// hashMap:存放type类型传感器的所有信息
		if (!map.containsKey(type))
			return null;
		HashMap<String, Object> hashMap = (HashMap) map.get(type);
		String[] s = configure.split(" ");
		// s1：电流比
		String s1 = s[0];
		// s2：额定输出
		String s2 = s[2];
		// a:配置中的电流比空间中的最大值
		int a = getMax(s1);
		boolean flag = (boolean) hashMap.get("flag");
		String key = getKey(a, hashMap.keySet());
		LinkedHashMap<String, Object> sonMap = (LinkedHashMap<String, Object>) hashMap.get(key);
		for (String ss : sonMap.keySet()) {
			boolean b = Match(s2, ss.split(";"));
			if (b) {
				list.add((String) sonMap.get(ss));
				break;
			}
		}
		if (flag) {
			LinkedList<String> keys = new LinkedList<>();
			for (String ss : hashMap.keySet()) {
				if (!ss.equals("flag"))
					keys.add(ss);
			}
			int i = keys.indexOf(key);
			i++;
			for (; i < keys.size(); i++) {
				sonMap = (LinkedHashMap<String, Object>) hashMap.get(keys.get(i));
				for (String ss : sonMap.keySet()) {
					list.add((String) sonMap.get(ss));
				}
			}
		}
		return list;
	}

	/**
	 * 申请资源
	 * 
	 * @param list
	 *            类别list
	 * @param number
	 *            申请模具数量
	 * @return 记录申请了某一类型下的哪些模具类别的多少资源
	 */
	public Map<String, Object> apply(List<String> list, int number) {
		HashMap<String, Object> usedMap = new HashMap<>();
		HashMap<String, Object> numbers = (LinkedHashMap<String, Object>) numbersMap.get(type);
		// 记录申请了某一类型下的哪些模具类别的多少资源
		for (String s : list) {
			int amount = (int) numbers.get(s);
			if (amount >= number) {
				usedMap.put(s, number);
				numbers.put(s, amount - number);
				number = 0;
				break;
			} else {
				usedMap.put(s, amount);
				numbers.put(s, 0);
				number = number - amount;
			}
		}
		// 若等于0，则表示模具能满足需求；若大于0，表示还有多少台不能完成
		// usedMap.put("left", number);
		return usedMap;
	}

	/**
	 * 预申请资源
	 * 
	 * @param num
	 *            资源数目
	 * @return 申请到的资源
	 */
	public ArrayList<Capacity> apply(int num) {
		ArrayList<Capacity> res = new ArrayList<Capacity>();
		LinkedList<String> list = match();
		Map<String, Object> result = apply(list, num);
		// Capacity(String model, int modelNumber, int type)
		String model = getType();
		for (String type : result.keySet()) {
			int modelNumber = (int) result.get(type);
			Capacity temp = new Capacity(model, modelNumber, type);
			res.add(temp);
		}
		return res;
	}

	/**
	 * 还原资源
	 * 
	 * @param capacity
	 *            资源描述
	 */
	public void restore(Capacity capacity) {
		Map<String, Object> result = new HashMap<String, Object>();
		setType(capacity.getModel());
		result.put(capacity.getType(), capacity.getModelNumber());
		restore(result);
	}

	public void restore(Map<String, Object> usedMap) {
		HashMap<String, Object> numbers = (LinkedHashMap<String, Object>) numbersMap.get(type);
		for (String key : usedMap.keySet()) {
			if (!key.equals("left")) {
				int used = (int) usedMap.get(key);
				used += (int) numbers.get(key);
				numbers.put(key, used);
			}
		}
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setConfigure(String configure) {
		this.configure = configure;
	}

	public static void main(String[] args) {
		try {

			read();
			System.out.println("申请前：");
			System.out.println(numbersMap);

			Moulds m = new Moulds("LZZBJ9-10E2", "400/5 0.5/10P10 20-20");
			LinkedList<String> list = m.match();
			System.out.println("满足要求的模具类别：");
			System.out.println(m.getType() + ":" + list);

			Map<String, Object> result = m.apply(list, 27);
			System.out.println("申请模具数量：");
			System.out.println(result);
			System.out.println("申请后：");
			System.out.println(numbersMap);

			m.restore(result);
			System.out.println("返还后：");
			System.out.println(numbersMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
