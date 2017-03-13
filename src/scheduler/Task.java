package scheduler;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * ��Ҫ�������ֲ�ͬ��ͬ�ŵ�����
 * 
 * @author Guanglei Pan
 * @version 2.0
 * 
 */
public class Task implements Comparable<Task> {
	private String startTime; // ��������
	private String orderNo; // ��ͬ��
	private String salesMan; // ����Ա
	private String client; // �ͻ�
	private String endDate;// ��������
	private int priority;// �������ȼ�
	private boolean isExecuting;//��ʾ�����Ƿ�����ִ��

	private ArrayList<Product> require;// �ú�ͬ���£����еĲ�ͬ�ͺš�����Product

	public ArrayList<Product> getRequire() {
		return require;
	}

	public void setRequire(ArrayList<Product> require) {
		this.require = require;
	}
	public boolean isExecuting() {
		return isExecuting;
	}

	public void setExecuting(boolean isExecuting) {
		this.isExecuting = isExecuting;
	}
	
	public Task() {
		priority = Integer.MAX_VALUE;
	}

	public Task(String startTime, String orderNo, String salesMan, String client, String endDate,
			Boolean isExecuting,  ArrayList<Product> require) {
		this.startTime = startTime;
		this.orderNo = orderNo;
		this.salesMan = salesMan;
		this.client = client;
		this.endDate = endDate;
		this.isExecuting = isExecuting;
		this.require = require;
		priority = Integer.MAX_VALUE;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String time) {
		this.startTime = time;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSalesMan() {
		return salesMan;
	}

	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	//�����ȼ���������
	public int compareTo(Task other) {
		int p1 = this.getPriority();
		int p2 = other.getPriority();
		if (p1 == p2) {
			return this.getEndDate().compareTo(other.getEndDate());
		} else {
			return p1 - p2;
		}
	}
}
