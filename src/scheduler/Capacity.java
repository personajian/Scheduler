package scheduler;

public class Capacity {
	/**
	 * �ͺ�
	 */
	private String model;
	/**
	 * ����
	 */
	private int modelNumber;
	/**
	 * ����
	 */
	private String type;
	public Capacity() {
		
	}
	public Capacity(String model, int modelNumber, String type) {
		this.model = model;
		this.modelNumber = modelNumber;
		this.type = type;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(int modelNumber) {
		this.modelNumber = modelNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
