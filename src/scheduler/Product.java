package scheduler;
import java.util.ArrayList;

/**
 * 每个不同型号和规格的产品
 * @author Guanglei Pan
 * @version 2.0
 */
public class Product {
	private String modelNo;
	private String standard;
	private String serial;
	private int originalNumber;

	private int number;
	private int sum;
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	ArrayList<Capacity> result;
	/**
	 * @param modelNo 型号
	 * @param standard 规格
	 * @param serial 编号（序列号）
	 * @param number 数量
	 */
	public Product(String modelNo, String standard, String serial, int originalNumber, int number) {
		this.modelNo = modelNo;
		this.standard = standard;
		this.serial = serial;
		this.originalNumber = originalNumber;
		this.number = number;
	}
	public Product(String modelNo, String standard, String serial, int originalNumber, int number, int sum,
			ArrayList<Capacity> result) {
		this.modelNo = modelNo;
		this.standard = standard;
		this.serial = serial;
		this.originalNumber = originalNumber;
		this.number = number;
		this.sum = sum;
		this.result = result;
	}
	public int getOriginalNumber() {
		return originalNumber;
	}
	public void setOriginalNumber(int originalNumber) {
		this.originalNumber = originalNumber;
	}
	public ArrayList<Capacity> getResult() {
		return result;
	}
	public void setResult(ArrayList<Capacity> result) {
		this.result = result;
	}
	public Product() {
		
	}

	public String getModelNo(){
		return modelNo;
	}
	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}
	
	public String getStandard()	{
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number)	{
		this.number = number;
	}
}
