package com.akanpian.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.akanpian.spider.SAv;
import com.akanpian.utils.DBUtils;

public class MainUI implements HyperlinkListener {

	private JFrame frame;
	private JTextField txtSearch;
	private JList list_1;
	private JComboBox<DefaultComboBoxModel> lstType;
	private JPanel panBar;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				init();
			}
		});
		frame.setBounds(100, 100, 887, 591);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		panBar = new JPanel();
		frame.getContentPane().add(panBar, BorderLayout.NORTH);
		panBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		txtSearch = new JTextField();
		panBar.add(txtSearch);
		txtSearch.setColumns(50);

		lstType = new JComboBox<DefaultComboBoxModel>();
		panBar.add(lstType);
		lstType.setModel(new DefaultComboBoxModel(new String[] { "JZ_无码中文", "JZ_无码漫画" }));

		JButton btnSearch = new JButton("Search");
		panBar.add(btnSearch);

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		editorPane = new JEditorPane("text/html", "<html></html>");
		editorPane.setOpaque(true);
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);
		scrollPane.setViewportView(editorPane);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search(txtSearch.getText(), lstType.getSelectedItem().toString());
			}
		});

	}

	protected void init() {

	}

	public SAv create(String txt) {
		String st = "%" + txt + "%";
		SAv s = new SAv();
		s.aname = st;
		s.fanhao = st;
		s.keywords = st;
		s.name = st;
		s.nvyouname = st;
		return s;
	}

	protected void search(String text, String string) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		List<SAv> avs = null;
		try {
			avs = DBUtils.getSAv(create(text));
		} catch (Exception e) {
			avs = null;
		}
		if (avs != null && avs.size() > 0) {
			for (SAv s : avs) {
				sb.append("<a href='http://www.abc.com'>");
				sb.append(
						"<img src='file:///H:/backup/other/spider/7mm/0/[APAA-338]千葉のチョイ田舎に在住のJK・美香は、セフレが7人いて、正にパンツを履くひまがありません…。 三宅美香_s.jpg' path=''/>");
				sb.append("</a>");
			}
		}
		sb.append("</html>");
		editorPane.setText(sb.toString());
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		System.out.println(e.getSourceElement());
	}
}
