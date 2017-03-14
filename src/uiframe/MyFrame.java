package uiframe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import scheduler.ScheduleOrder;
import scheduler.Task;

//�ÿ�ܾ�����ʾ��Frame��ܹرհ�ť�Ĺ���ʵ��  
public class MyFrame extends Frame implements WindowListener, ActionListener {
	// �����򣺲˵���
	private MenuItem fileNew = new MenuItem("New");
	private MenuItem fileOpen = new MenuItem("Open");
	private MenuItem fileExit = new MenuItem("Exit");
	private MenuItem editCut = new MenuItem("Cut");
	private MenuItem editCopy = new MenuItem("Copy");
	private MenuItem editPaste = new MenuItem("Paste");

	private Panel themePanel = new Panel();
	private Label themeLabel = new Label("����ʡ�����л������ɷ����޹�˾�����Ų�ϵͳ");
	private Button quit = new Button("�˳�����");
	private Button click = new Button("�����Ų�");

	// ����ʹ��˵��
	private Panel instructionPanel = new Panel();
	private Label instructionLable = new Label("ʹ��˵��", Label.CENTER);
	private Label instructionLable1 = new Label("1. �򿪶�����ѡ����Ҫ�Ų��������ڵ�Ŀ¼��", Label.CENTER);
	private Label instructionLable2 = new Label("2. ��������ѡ����Ҫ��������Ŀ¼��", Label.CENTER);
	private Label instructionLable3 = new Label("3. �����Ų����Ų������������Ŀ¼��", Label.CENTER);
	private Label instructionLable4 = new Label("4. �˳����򣺽�������", Label.CENTER);
	private Label instructionLable5 = new Label();

	// ���ļ��������ļ� ��ť �Լ� �Ի���
	private Panel filePanel = new Panel();
	private Button openFileButton = new Button("�򿪶���");
	private Button saveFileButton = new Button("������");

	private FileDialog fileDialogOpen = new FileDialog(this, "Open File", FileDialog.LOAD);
	private FileDialog fileDialogSave = new FileDialog(this, "Save File", FileDialog.SAVE);

	private TextField text = new TextField(10);
	private boolean secondClick = false;

	private Panel buttons = new Panel();
	private Panel textGrid = new Panel();

	public String getOpenDirectory() {
		return openDirectory;
	}

	public void setOpenDirectory(String openDirectory) {
		this.openDirectory = openDirectory;
	}

	public String getOpenFile() {
		return openFile;
	}

	public void setOpenFile(String openFile) {
		this.openFile = openFile;
	}

	public String getSaveDirectoty() {
		return saveDirectoty;
	}

	public void setSaveDirectoty(String saveDirectoty) {
		this.saveDirectoty = saveDirectoty;
	}

	public String getSaveFile() {
		return saveFile;
	}

	public void setSaveFile(String saveFile) {
		this.saveFile = saveFile;
	}

	private String openDirectory;
	private String openFile;

	private String saveDirectoty;
	private String saveFile;

	public MyFrame() {
		super("���񶩵��Ų��Ż�����"); // ���ô��ڱ���
		setSize(600, 500); // ���ô��ڳߴ�
		// ��ȡ��Ļ�ĸ߶ȺͿ��
		int w = this.getWidth(); // ��ȡ��ܵĿ��
		int h = this.getHeight(); // ��ȡ��ܵĸ߶�
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth(); // ��ȡ��Ļ�Ŀ��
		double screenHeith = screenSize.getHeight(); // ��ȡ��Ļ�ĸ߶�

		int x = (int) (screenWidth - w) / 2;
		int y = (int) (screenHeith - h) / 2;

		// ���ÿ����ʾ��λ��
		setLocation(x, y);

		// this.setBounds((int)((screenWidth)/2-this.getWidth()),(int)((screenHeith/2)-this.getHeight()),
		// 200, 200); // 100,100�ֱ�Ϊ����x,y��ľ��룬�����x,y�ֱ��ܵĳ��Ϳ�
		this.setLayout(new FlowLayout()); // ���ò��ֹ�����Ϊ�����ֹ�����
		this.add(new TextField("hello welocme to java!"));

		// ��Ӳ˵���
		Menu file = new Menu("File");
		file.add(fileNew);
		fileNew.setEnabled(true);
		file.add(fileOpen);
		fileOpen.setEnabled(true);
		file.addSeparator();
		file.add(fileExit);
		fileExit.setEnabled(true);
		Menu edit = new Menu("Edit");
		edit.add(editCut);
		editCut.setEnabled(true);
		edit.add(editCopy);
		editCopy.setEnabled(true);
		edit.add(editPaste);
		editPaste.setEnabled(true);

		MenuBar bar = new MenuBar();
		bar.add(file);
		bar.add(edit);
		// setMenuBar(bar);

		fileExit.addActionListener(this);

		// ��ӱ������
		themePanel.setLayout(new FlowLayout());
		themePanel.setBackground(new Color(0XFFDEAD));
		themeLabel.setFont(new Font("Consolas", 1, 20));
		themePanel.add(themeLabel);

		// ��ӳ���ʹ��˵�����
		instructionPanel.setLayout(new GridLayout(6, 1));
		instructionPanel.setBackground(new Color(0XADD8E6));
		instructionLable.setFont(new Font("Consolas", 1, 19));
		instructionLable1.setFont(new Font("Consolas", 0, 18));
		instructionLable2.setFont(new Font("Consolas", 0, 18));
		instructionLable3.setFont(new Font("Consolas", 0, 18));
		instructionLable4.setFont(new Font("Consolas", 0, 18));
		instructionLable.setForeground(new Color(0XFF0000));
		instructionLable.setBackground(new Color(0XE1FFFF));
		instructionLable1.setBackground(new Color(0XADD8E6));
		instructionLable2.setBackground(new Color(0XE1FFFF));
		instructionLable3.setBackground(new Color(0XADD8E6));
		instructionLable4.setBackground(new Color(0XE1FFFF));

		instructionPanel.add(instructionLable);
		instructionPanel.add(instructionLable1);
		instructionPanel.add(instructionLable2);
		instructionPanel.add(instructionLable3);
		instructionPanel.add(instructionLable4);
		instructionPanel.add(instructionLable5);

		// ��Ӱ�ť���
		buttons.setLayout(new FlowLayout());
		quit.setForeground(new Color(0XFF4500));
		quit.setFont(new Font("Consolas", 1, 18));
		click.setForeground(new Color(0X4A460));
		click.setFont(new Font("Consolas", 1, 18));
		openFileButton.setForeground(new Color(0X808000));
		openFileButton.setFont(new Font("Consolas", 1, 18));
		saveFileButton.setForeground(new Color(0X808000));
		saveFileButton.setFont(new Font("Consolas", 1, 18));
		buttons.add(openFileButton);
		buttons.add(saveFileButton);
		buttons.add(click);
		buttons.add(quit);

		// �򿪡������ļ��¼�������
		// openFileButton.addActionListener(e -> {
		// fileDialogOpen.setVisible(true);
		// setOpenDirectory(fileDialogOpen.getDirectory());
		// setOpenFile(fileDialogOpen.getDirectory() +
		// fileDialogOpen.getFile());
		// System.out.println(fileDialogOpen.getDirectory() +
		// fileDialogOpen.getFile());
		// System.out.println(getOpenDirectory());
		// System.out.println(getOpenFile());
		// });
		// saveFileButton.addActionListener(e -> {
		// fileDialogSave.setVisible(true);
		// setSaveDirectoty(fileDialogSave.getDirectory());
		// setOpenFile(fileDialogSave.getDirectory() +
		// fileDialogSave.getFile());
		// System.out.println(fileDialogSave.getDirectory() +
		// fileDialogSave.getFile());
		// System.out.println(getSaveDirectoty());
		// System.out.println(getSaveFile());
		// });

		// �򿪡������ļ��¼�������
		openFileButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			// chooser.setSize(600, 500);
			chooser.setDialogTitle("ѡ����Ҫ�Ų��������ڵ�Ŀ¼");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			// chooser.setAcceptAllFileFilterUsed(false);
			//
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
				System.out.println(chooser.getCurrentDirectory().getAbsolutePath());
				System.out.println(chooser.getSelectedFile().getAbsolutePath());
				setOpenDirectory(chooser.getSelectedFile().getAbsolutePath());
			} else {
				System.out.println("No Selection ");
			}
		});
		saveFileButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			// chooser.setSize(600, 500);
			chooser.setDialogTitle("ѡ����Ҫ��������Ŀ¼");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			//
			// disable the "All files" option.
			//
			// chooser.setAcceptAllFileFilterUsed(false);
			//
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
				System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
				System.out.println(chooser.getCurrentDirectory().getAbsolutePath());
				System.out.println(chooser.getSelectedFile().getAbsolutePath());
				setSaveDirectoty(chooser.getSelectedFile().getAbsolutePath());
			} else {
				System.out.println("No Selection ");
			}
		});

		// �򿪡������ļ��¼�������
		click.addActionListener(new clickActionListener());
		setLayout(new BorderLayout());
		add("North", themePanel);
		add("Center", instructionPanel);
		add("South", buttons);

		/*
		 * // ��Ӱ�ť �Լ��¼� Button myBotton = new Button("Press Me!");
		 * myBotton.addActionListener(this); this.add(myBotton,
		 * BorderLayout.CENTER);
		 */
		// ����ť��ӵ��¼�����
		quit.addActionListener(this);

		this.addWindowListener(this); // ��Ӵ��ڼ�����
		this.setVisible(true); // ���ô��ڿɼ� true�ɼ���false ���ɼ�
	}

	public static void main(String[] args) {
		new MyFrame();// ʵ����
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		e.getWindow().dispose();
	} // �رմ��ڷ���

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fileExit) {
			ExitConfirmDialog exit = new ExitConfirmDialog(this, "Confirm Exit", "Do you really want to exit?");
			if (exit.isOkay)
				System.exit(0);
		} else if (e.getSource() == quit) {
			setVisible(false);
			System.exit(0);
		}
		System.out.println("a button has been pressed");
	}

	class clickActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ScheduleOrder.srcFilePath = getOpenDirectory() + File.separator;
			// ScheduleOrder.srcFilePath = "d:/task/";
			ScheduleOrder.desFilePath = getSaveDirectoty() + File.separator;
			ScheduleOrder scheduleOrder = new ScheduleOrder();
			try {
				scheduleOrder.read();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			scheduleOrder.sort();
			try {
				scheduleOrder.writeTotalTask();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				scheduleOrder.writeSingleTask();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ArrayList<Task> taskset = scheduleOrder.getTask();
			/*
			 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
			 * String currentDate = df.format(new Date());//��ȡ��ǰϵͳʱ��
			 */
			String currentDate = "2017-01-10";//
			try {
				scheduleOrder.process(taskset, currentDate);
			} catch (IOException | ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("a button has been pressed");
		}
	}
}
