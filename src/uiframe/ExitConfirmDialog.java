package uiframe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExitConfirmDialog extends Dialog implements ActionListener {
	private Button okay = new Button("OK");
	private Button cancel = new Button("Cancel");
	private Label label = new Label("Are you sure?", Label.CENTER);
	public boolean isOkay = false;

	private class WindowCloser extends WindowAdapter {
		@SuppressWarnings("deprecation")
		public void windowCloseing(WindowEvent we) {
			ExitConfirmDialog.this.isOkay = false;
			ExitConfirmDialog.this.hide();
		}
	}

	public ExitConfirmDialog(Frame parent) {
		// TODO Auto-generated constructor stub
		this(parent, "Please confirm", "Are you sure?");

	}

	public ExitConfirmDialog(Frame parent, String title, String question) {
		// TODO Auto-generated constructor stub
		super(parent, title, true);
		label.setText(question);
		setup();
		// 获取屏幕的高度和宽度
		int w = this.getWidth(); // 获取框架的宽度
		int h = this.getHeight(); // 获取框架的高度
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth(); // 获取屏幕的宽度
		double screenHeith = screenSize.getHeight(); // 获取屏幕的高度

		int x = (int) (screenWidth - w) / 2;
		int y = (int) (screenHeith - h) / 2;

		// 设置框架显示的位置
		setLocation(x, y);

		okay.addActionListener(this);
		cancel.addActionListener(this);
		addWindowListener(new WindowCloser());
		setResizable(false);
		pack();
		setVisible(true);
	}

	private void setup() {
		// TODO Auto-generated method stub
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		buttons.add(okay);
		buttons.add(cancel);
		setLayout(new BorderLayout());
		add("Center", label);
		add("South", buttons);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		isOkay = (e.getSource() == okay);
		hide();
	}

}
