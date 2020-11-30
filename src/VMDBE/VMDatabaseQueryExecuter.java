package VMDBE;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;



public class VMDatabaseQueryExecuter {
	
	static String database=null;
	static boolean isFileSaved=false;
    static JTextField userTb=new JTextField();
    static JTextField serverTb=new JTextField();
    static JTextField servernameTb=new JTextField("localhost");
    static JPasswordField passTb=new JPasswordField();
    static JMenuItem saveAsFile=new JMenuItem("Save As");
    static JComboBox dbs = new JComboBox();
    static JComboBox tables = new JComboBox();
    static JLabel ql=new JLabel("Enter Query here",JLabel.LEFT);
    static JFrame jf=new JFrame("VM Database Query Executor");
    static JTextArea q = new JTextArea();
    static JScrollPane sp;
    static boolean isVmOptionsEnabled=false,isAutocompleteEnabled=true;
    static JLabel Message=new JLabel();
    static JButton tb=new JButton("On");
    static JButton EnableDisableAutoComplete=new JButton("<html><center>Disable<br>Auto Complete</center></html>");
    static String UserName="",Password="",ServerName="",LocalhostN="";
    static JPanel resultPanel=new JPanel();
    static JPanel messagePanel=new JPanel();
    static JTabbedPane tsp=new JTabbedPane();
    static String ClausesWords[]= {"ALTER","ALTER COLUMN","ALTER TABLE","BACKUP DATABASE","CREATE TABLE","CREATE DATABASE","CREATE INDEX","DELETE FROM","DROP","DROP TABLE","DROP DATABASE","DROP CONSTRAINT","DROP INDEX","DROP VIEW","INSERT INTO","INSERT INTO SELECT","SELECT * FROM","SELECT INTO","TRUNCATE TABLE","UPDATE"};
    static DefaultListModel ClausesList = new DefaultListModel();
    static JList ClausesListObject=new JList(ClausesList);
    static JScrollPane ClausesListSC = new JScrollPane(ClausesListObject);
    static DefaultListModel TCList = new DefaultListModel();
    static JList TCListObject=new JList(TCList);
    static JScrollPane TCListSC = new JScrollPane(TCListObject);
    static List<String> TCNames=new ArrayList<String>();
    static String KeyWords[]= {"ADD","ADD CONSTRAINT","ALTER","ALL","AND","ANY","AS","ASC","BETWEEN","BIGINT","BIT","CASE","CHECK","COLUMN","CONSTRAINT","CHAR","CREATE","DATABASE","DATE","DATETIME","DEFAULT","DELETE","DESC","DISTINCT","DROP","EXEC","EXISTS","FOREIGN KEY","FLOAT","FROM","FULL OUTER JOIN","GROUP BY","HAVING","IN","INDEX","INNER JOIN","INSERT","IS NULL","IS NOT NULL","INT","JOIN","LEFT","LIKE","LIMIT","MONEY","NCHAR","NTEXT","NOT","NOT NULL","OR","ORDER BY","OUTER JOIN","PRIMARY KEY","RIGHT JOIN","ROWNUM","SELECT","SELECT DISTINCT","SELECT INTO","SELECT TOP","SET","TABLE","TOP","TRUNCATE TABLE","UNION","UNION ALL","UNIQUE","UPDATE","VALUES","VIEW","VARCHAR","WHERE"};
    static DefaultListModel KeyWordsList = new DefaultListModel();
    static JList KeyWordsListObject=new JList(KeyWordsList);
    static JScrollPane KeywordsListSC = new JScrollPane(KeyWordsListObject);
    static boolean isAnyFileOpenned=false;
    static String OpennedFileNameWithPath="";
    static String OpennedFileNameOnlyName="Demo.VMDBE_SQLQ";
    private static Vector getDatabasesNames()
    { 
    	
    	if(UserName.equals("")||Password.equals("")) 
    	{
    		JOptionPane.showMessageDialog(jf,"Make Sure You have logged in","Login Required",JOptionPane.ERROR_MESSAGE);
    		System.gc();
    		return null;
    	}
    	else 
    	{
    		Vector v=new Vector();
            try {	
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433",UserName,Password);    
                DatabaseMetaData metadata = connection.getMetaData();
                ResultSet result = metadata.getCatalogs();
            while (result.next()) {
                v.add(result.getString(1));
            } 
            connection.close();
            } catch (Exception e) {Message.setText("Message: "+e.toString());}
            System.gc();
            return v;
    	}
    }
    
    private static Vector getTableNames(String databasename)
    {
    	if(UserName.equals("")||Password.equals("")) 
    	{
    		JOptionPane.showMessageDialog(jf,"Make Sure You have logged in","Login Required",JOptionPane.ERROR_MESSAGE);
    		System.gc();
    		return null;
    	}
    	else 
    	{
    	Vector v=new Vector();
        try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection = DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+databasename,UserName,Password);    
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                if (rs.getString(4).equalsIgnoreCase("TABLE")) {
                v.add(rs.getString(3));
                }
            }
        connection.close();
        System.gc();
        } catch (Exception e) {}
        return v;
        }
    }
    
    private static Vector getColumnNames(String databasename)
    {
    	if(UserName.equals("")||Password.equals("")) 
    	{
    		JOptionPane.showMessageDialog(jf,"Make Sure You have logged in","Login Required",JOptionPane.ERROR_MESSAGE);
    		System.gc();
    		return null;
    	}
    	else 
    	{
    	Vector Colum=new Vector();	
    	Vector v=getTableNames(databasename);
    	try {
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection = DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+databasename,UserName,Password);
    	Iterator value = v.iterator(); 
        while (value.hasNext()) {
        			Statement s=connection.createStatement();
                    ResultSet rs =s.executeQuery("Select * from "+value.next());
                    ResultSetMetaData rsmd = rs.getMetaData();
                    for(int i=1;i<=rsmd.getColumnCount();i++) 
                    {
                    	Colum.add(rsmd.getColumnName(i));
                    }
        	}
        connection.close();
    	} catch (Exception e) {}
    	System.gc();
        return Colum;
        }
    }
    
    private static void  buttonHover(JButton name)
    {
        name.setFont(new Font("Arial",Font.BOLD,12));
        name.setBorder(BorderFactory.createBevelBorder(0));
        name.setForeground(Color.WHITE);
        name.setBackground(Color.decode("#4F81BD"));
        name.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                name.setBackground(Color.decode("#197BF1"));
                name.repaint();
            }
            @Override
            public void mouseEntered(MouseEvent me) 
            {
                name.setBackground(Color.decode("#3883DE"));
                name.repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) 
            {
                name.setBackground(Color.decode("#4F81BD"));
                name.repaint();
            }
        });
        System.gc();
    }
    
    private static void  buttonHoverOnOff(JButton name)
    {
        name.setFont(new Font("Arial",Font.BOLD,12));
        name.setBorder(BorderFactory.createBevelBorder(0));
        name.setForeground(Color.WHITE);
        name.setBackground(Color.decode("#e34d4d"));
        name.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                name.setBackground(Color.decode("#E15858"));
                name.repaint();
            }

            @Override
            public void mousePressed(MouseEvent me){}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) 
            {
                name.setBackground(Color.decode("#E60A0A"));
                name.repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) 
            {
                name.setBackground(Color.decode("#e34d4d"));
                name.repaint();
            }
        });
        System.gc();
    }
    
    private static int getRowsCount(String Query)
    {
        int rowcount=0;try {
                
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    Connection conn= DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+dbs.getSelectedItem(),UserName,Password);
                    Statement smt=conn.createStatement();
                    ResultSet rs= smt.executeQuery(Query);
                    while (rs.next()) {                
                        rowcount++;
                    }
                }catch(Exception e){ 
                    Message.setText("Message: "+e.toString());
                }
        System.gc();
        return rowcount;
    }
    
    private static void resizeColumnWidth(JTable table) {
    final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++) {
    		TableColumn tc = columnModel.getColumn(column);
    		String TT=tc.getHeaderValue().toString();
    		int TLen=TT.length();
    		if(TLen>=6) 
    		{
    			columnModel.getColumn(column).setPreferredWidth((TLen/6)*200);
    		}
    		else {columnModel.getColumn(column).setPreferredWidth(200);}
    }
    System.gc();
}
    private static void addDataToTable(String Query)
    {
    	tsp.setSelectedIndex(0);
        try{resultPanel.remove(sp);}catch(Exception e){}
        System.gc();
        try {
                
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    Connection conn= DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+dbs.getSelectedItem(),UserName,Password);
                    PreparedStatement ps=conn.prepareStatement(Query);
                    ResultSet rs= ps.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    String cols[]=new String[columnCount];
                    for (int i = 0; i < columnCount; i++ ) {
                        cols[i]=rsmd.getColumnName(i+1).toUpperCase();
                    }
                    String data[][]=new String[getRowsCount(Query)][columnCount];
                    for (int i = 0;rs.next(); i++)
                    {
                        for (int j = 0; j < columnCount; j++) {
                            data[i][j]=rs.getString(j+1);
                        };
                    }
                JTable jt=new JTable(data, cols);
                jt.setFont(new Font("Bookman Old Style",Font.PLAIN,18));
                resizeColumnWidth(jt);
                jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                
                jt.setRowHeight(30);
                JTableHeader th=jt.getTableHeader();
                th.setFont(new Font("Courier",Font.PLAIN,25));
                sp=new JScrollPane(jt);
                sp.repaint();
                sp.revalidate();
                sp.setBounds(0,0,935,323);
                jf.repaint();
                jf.revalidate();
                resultPanel.add(sp);
                resultPanel.repaint();
                resultPanel.revalidate();
                jf.repaint();
                jf.revalidate();
                }catch(Exception e){
                	tsp.setSelectedIndex(1);
                   Message.setForeground(Color.RED);
                   Message.setText("Message: "+e.getMessage());
                }
        System.gc();
    }
    
    private static void addToQuery(JComboBox JCB) {  
        JCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED)
                {
                    if(isVmOptionsEnabled){
                    	q.insert(JCB.getSelectedItem().toString(),q.getCaretPosition());
                    	q.repaint();
                    }
                }
            }
        });
        System.gc();
    }
    private static void VMDBM() {
    	try {
    		
    	File directory = new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "DOCUMENTS"+System.getProperty("file.separator")+"VMDBE SQL Queries");
        if (! directory.exists()){
            directory.mkdir();
        }
        
        
    	jf.setLayout(null);
        jf.setSize(1000,700);
        jf.setResizable(false);
        jf.setLocation(140,10);
        try{
        	BufferedImage immm=new Resource().getResourceImage("/Res/VMDBM.png");
        	jf.setIconImage(immm);
        	}catch (Exception e) {	
		}
        
        jf.getContentPane().setBackground(Color.WHITE);
        jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jf.repaint();
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
            	if(isAnyFileOpenned) 
				{
            		if(!isFileSaved) 
            		{
					int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save changes to "+OpennedFileNameOnlyName+" ?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
					if(answer==JOptionPane.YES_OPTION) 
					{
						boolean isfileSaved=saveFile(OpennedFileNameWithPath);
						if(isfileSaved) {
							int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			                if(exitoutput==JOptionPane.YES_OPTION)
			                {
			                    System.exit(0);
			                }
			                else{}							
						}
					}
					else if(answer==JOptionPane.NO_OPTION) 
					{
						int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		                if(exitoutput==JOptionPane.YES_OPTION)
		                {
		                    System.exit(0);
		                }
		                else{}
					}
					else{}
            		}else {
            			int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		                if(exitoutput==JOptionPane.YES_OPTION)
		                {
		                    System.exit(0);
		                }
		                else{}
            		}
				}
				else 
				{
					if(q.getText().equals("")) 
					{
						int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		                if(exitoutput==JOptionPane.YES_OPTION)
		                {
		                    System.exit(0);
		                }
		                else{}
					}
					else {
						int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save the Query?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
						if(answer==JOptionPane.YES_OPTION) 
						{
							if(saveAsFile()) 
							{
								int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				                if(exitoutput==JOptionPane.YES_OPTION)
				                {
				                    System.exit(0);
				                }
				                else{}
							}
						}
						else if(answer==JOptionPane.NO_OPTION) 
						{
							int exitoutput=JOptionPane.showConfirmDialog(jf,"VM Database Query Executor", "Close VM Database Query Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			                if(exitoutput==JOptionPane.YES_OPTION)
			                {
			                    System.exit(0);
			                }
			                else{}
						}
						else {}
					}
				}
            }
        });
        jf.setVisible(true);
        jf.repaint();
        System.gc();
        JMenuBar Jmb=new JMenuBar();
        Jmb.setBounds(0, 0, 1000, 20);
        JMenu fileMenu=new JMenu("File");
        JMenuItem newFile=new JMenuItem("New");
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        JMenuItem openFile=new JMenuItem("Open");
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        JMenuItem saveFile=new JMenuItem("Save");
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        
        saveAsFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsFile);
        Jmb.add(fileMenu);
        jf.add(Jmb);
        jf.repaint();
        CheckForSaveAsED();
        newFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newFileAction();
				System.gc();
			}
		});
        
        saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFileAction();
			}
		});
        saveAsFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsFileAction();
			}
		});
        
        openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				openFileAction();
			}
		});
        
        JLabel svrl=new JLabel("Server Name",JLabel.LEFT);
        svrl.setFont(new Font("Baskerville Old Face",Font.PLAIN,15));
        svrl.setForeground(Color.decode("#3d5c5c"));
        svrl.setBounds(3,0,185,20);
        servernameTb.setBounds(40,30,90,20);
        servernameTb.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(servernameTb.getText().equals(""))
                {
					servernameTb.add(svrl);
					servernameTb.repaint();
                    jf.repaint();
                }
			}
			@Override
			public void focusGained(FocusEvent e) {
				servernameTb.remove(svrl);
				System.gc();
				servernameTb.repaint();
                jf.repaint();
			}
		});
        jf.add(servernameTb);
        
        JLabel sl=new JLabel("Instance",JLabel.LEFT);
        sl.setFont(new Font("Baskerville Old Face",Font.PLAIN,15));
        sl.setForeground(Color.decode("#3d5c5c"));
        sl.setBounds(3,0,185,20);
        serverTb.add(sl);
        serverTb.setBounds(140,30,90,20);
        serverTb.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(serverTb.getText().equals(""))
                {
                    serverTb.add(sl);
                    serverTb.repaint();
                    jf.repaint();
                }
			}
			@Override
			public void focusGained(FocusEvent e) {
				serverTb.remove(sl);
				System.gc();
                serverTb.repaint();
                jf.repaint();
			}
		});
        serverTb.addKeyListener(new KeyAdapter() {
        	@Override
   			public void keyTyped(KeyEvent ke) {
        		if(serverTb.getText().equals("")) {
        			userTb.setEditable(false);
        			passTb.setEditable(false);
        			System.gc();
        		}
        		else 
        		{
        			userTb.setEditable(true);
        			passTb.setEditable(true);
        			System.gc();
        		}
        	}
		});
        jf.add(serverTb);
        if(serverTb.getText().equals("")) {
			userTb.setEditable(false);
			passTb.setEditable(false);
		}
		else 
		{
			userTb.setEditable(true);
			passTb.setEditable(true);
		}
        
        JLabel ul=new JLabel("Username",JLabel.LEFT);
        ul.setFont(new Font("Baskerville Old Face",Font.PLAIN,15));
        ul.setForeground(Color.decode("#3d5c5c"));
        ul.setBounds(3,0,100,20);
        userTb.add(ul);
        userTb.setBounds(245,30,100,20);
        userTb.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(userTb.getText().equals(""))
                {
                    userTb.add(ul);
                    userTb.repaint();
                    jf.repaint();
                }
			}
			@Override
			public void focusGained(FocusEvent e) {
				userTb.remove(ul);
				System.gc();
                userTb.repaint();
                jf.repaint();
			}
		});
        jf.add(userTb);
        
       JLabel pl=new JLabel("Password ",JLabel.LEFT);
       pl.setBounds(3,0,100,20);
       pl.setFont(new Font("Baskerville Old Face",Font.PLAIN,15));
       pl.setForeground(Color.decode("#3d5c5c"));
       passTb.add(pl);
       passTb.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(passTb.getText().equals(""))
               {
					passTb.add(pl);
                   passTb.repaint();
                   jf.repaint();
               }
			}
			@Override
			public void focusGained(FocusEvent e) {
				passTb.remove(pl);
				System.gc();
				passTb.repaint();
               jf.repaint();
			}
		});
       passTb.setBounds(360, 30,100,20);
       passTb.addKeyListener(new KeyAdapter() {
    	   @Override
   			public void keyPressed(KeyEvent ke) {
    		   if(ke.getKeyCode()==ke.VK_ENTER) {
    		   try {
    	           Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	           Connection connection = DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName=",userTb.getText(),passTb.getText()); 
    	           JOptionPane.showMessageDialog(jf,"Successfully logged in","Login Sucessful", JOptionPane.INFORMATION_MESSAGE);
    	           UserName=userTb.getText();
    	           Password=passTb.getText();
    	           dbs.removeAllItems();
    	           System.gc();
    	           Vector v=getDatabasesNames();
    	           Iterator value = v.iterator(); 
    	           while (value.hasNext()) { 
    	               dbs.addItem(value.next());
    	           }
    	           dbs.revalidate();
    	           dbs.repaint();
    	           } catch (Exception e) {
    	           	JOptionPane.showMessageDialog(jf,"Error in logging in","Login Unsuccessful", JOptionPane.ERROR_MESSAGE);
    	           }
    		   }
   			}
       });
       jf.add(passTb);
    
       JButton LoginB=new JButton("Login");
       buttonHover(LoginB);
       LoginB.setBounds(490, 30, 80, 20);
       jf.add(LoginB);
       LoginB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            	ServerName=serverTb.getText();
            	LocalhostN=servernameTb.getText();
                try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection connection = DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName=",userTb.getText(),passTb.getText()); 
                JOptionPane.showMessageDialog(jf,"Successfully logged in","Login Sucessful", JOptionPane.INFORMATION_MESSAGE);
                UserName=userTb.getText();
                Password=passTb.getText();
                dbs.removeAllItems();
                System.gc();
                Vector v=getDatabasesNames();
                Iterator value = v.iterator(); 
                while (value.hasNext()) { 
                    dbs.addItem(value.next());
                }
                dbs.revalidate();
                dbs.repaint();
                } catch (Exception e) {
                	JOptionPane.showMessageDialog(jf,"Error in logging in","Login Unsuccessful", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
       
       
       JButton getDb=new JButton("Reset");
        buttonHover(getDb);
        getDb.setBounds(600, 30, 100, 20);
        jf.add(getDb);
        getDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isVmOptionsEnabled)
                {
                    isVmOptionsEnabled=false;
                    buttonHover(tb);
                    tb.repaint();
                    jf.repaint();
                    tb.setText("On");
                }
                if(!isAutocompleteEnabled) 
                {
                	isAutocompleteEnabled=true;
                	buttonHoverOnOff(EnableDisableAutoComplete);
                	EnableDisableAutoComplete.setText("<html><center>Disable<br>Auto Complete</center></html>");
                	EnableDisableAutoComplete.repaint();
                	jf.repaint();
                }
                try{resultPanel.remove(sp);}catch(Exception e){}
                System.gc();
                try{dbs.removeAllItems();}catch(Exception e){}
                System.gc();
                try{tables.removeAllItems();}catch(Exception e){}
                System.gc();
                q.setText("");
                userTb.setText("");
                passTb.setText("");
                UserName="";
                Password="";
                ServerName="";
                LocalhostN="";
                CheckWordForSugesstions();
                jf.repaint();
            }
        });
        
        JLabel dl=new JLabel("Database: ",JLabel.CENTER);
        dl.setBounds(715, 30,100,20);
        jf.add(dl);
        dbs.setBounds(800, 30,140,20);
        dbs.revalidate();
        dbs.repaint();
        jf.add(dbs);
        jf.repaint();
        jf.revalidate();
        dbs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                try{tables.removeAllItems();TCNames.clear();}catch (Exception e) {}
                System.gc();
                Vector v=getTableNames(dbs.getSelectedItem().toString());
                Iterator value = v.iterator();
                tables.addItem(" Tables ");
                while (value.hasNext()) {
                	String CC=value.next().toString();
                    tables.addItem(" "+CC+" ");
                    TCNames.add(CC);
                }
                Vector v2=getColumnNames(dbs.getSelectedItem().toString());
                Iterator value2 = v2.iterator();
                while (value2.hasNext()) {
                    TCNames.add(value2.next().toString());
                }
                tables.revalidate();
                tables.repaint();
            }
        });
        
        JLabel cl=new JLabel("Statements: ");
        cl.setBounds(30, 60,80,20);
        jf.add(cl);
        String Statements[]={" STATEMENTS "," ALTER "," ALTER COLUMN "," ALTER TABLE "," BACKUP DATABASE "," CREATE TABLE "," CREATE DATABASE "," CREATE INDEX "," DELETE FROM "," DROP "," DROP TABLE "," DROP DATABASE "," DROP CONSTRAINT "," DROP INDEX "," DROP VIEW "," INSERT INTO "," INSERT INTO SELECT "," SELECT * FROM "," SELECT INTO "," TRUNCATE TABLE "," UPDATE "};
        JComboBox St = new JComboBox(Statements);
        St.setBounds(103, 60,150,20);
        St.revalidate();
        St.repaint();
        addToQuery(St);
        jf.add(St);
        jf.repaint();
        jf.revalidate();
        
        
        JLabel tl=new JLabel("Table: ");
        tl.setBounds(270, 60,70,20);
        jf.add(tl);
        tables.setBounds(310,60,150,20);
        tables.revalidate();
        tables.repaint();
        addToQuery(tables);
        jf.add(tables);
        jf.repaint();
        jf.revalidate();
        
        JLabel kl=new JLabel("Keywords: ");
        kl.setBounds(470, 60,70,20);
        jf.add(kl);
        String keywds[]={" Keywords "," ADD "," ADD CONSTRAINT "," ALTER "," ALL "," AND "," ANY "," AS "," ASC "," BIGINT "," BIT "," BETWEEN "," CASE "," CHECK "," COLUMN "," CONSTRAINT "," CREATE "," DATABASE "," DATE "," DEFAULT "," DELETE "," DESC "," DECIMAL "," DISTINCT "," DROP "," EXEC "," EXISTS "," FOREIGN KEY "," FLOAT "," FROM "," FULL OUTER JOIN "," GROUP BY "," HAVING "," IN "," INDEX "," INNER JOIN "," INSERT "," IS NULL "," IS NOT NULL "," INT "," JOIN "," LEFT "," LIKE "," LIMIT "," MONEY "," NCHAR "," NOT "," NOT NULL "," NTEXT "," OR "," ORDER BY "," OUTER JOIN "," PRIMARY KEY "," RIGHT JOIN "," ROWNUM "," SELECT "," SELECT DISTINCT "," SELECT INTO "," SELECT TOP "," SET "," TABLE "," TEXT "," TOP "," TRUNCATE TABLE "," UNION "," UNION ALL "," UNIQUE "," UPDATE "," VARCHAR "," VALUES "," VIEW "," WHERE "}; 
        JComboBox keywords = new JComboBox(keywds);
        addToQuery(keywords);
        keywords.setBounds(540,60,100,20);
        keywords.revalidate();
        keywords.repaint();
        jf.add(keywords);
        jf.repaint();
        jf.revalidate();
        
        JLabel ofl=new JLabel("VM DB Helper: ");
        ofl.setBounds(650,60,100,20);
        jf.add(ofl);
        jf.repaint();
        
        tb.setBounds(740, 60, 60, 20);
        buttonHover(tb);
        tb.repaint();
        jf.add(tb);
        tb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isVmOptionsEnabled)
                {
                    isVmOptionsEnabled=false;
                    buttonHover(tb);
                    tb.repaint();
                    jf.repaint();
                    tb.setText("On");
                }
                else
                {
                    isVmOptionsEnabled=true;
                    buttonHoverOnOff(tb);
                    tb.repaint();
                    jf.repaint();
                    tb.setText("Off");
                }
            }
        });
        
        JButton exe=new JButton("Execute Query");
        exe.setBounds(820, 60, 120, 20);
        buttonHover(exe);
        exe.repaint();
        jf.add(exe);
        exe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            	executeQueryFun();
            }
        });
        
        
        ql.setFont(new Font("Arial",Font.ROMAN_BASELINE,25));
        ql.setForeground(Color.decode("#3d5c5c"));
        ql.setBounds(3,3,200,30);
        q.add(ql);
        q.setBounds(140,30,90,20);
        q.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(q.getText().equals(""))
                {
                    q.add(ql);
                    q.repaint();
                    jf.repaint();
                }
			}
			@Override
			public void focusGained(FocusEvent e) {
				q.remove(ql);System.gc();
                q.repaint();
                jf.repaint();
			}
		});
        JScrollPane query = new JScrollPane(q);
        q.setFont(new Font("Arial",Font.PLAIN,25));
        q.setLineWrap(true);
        q.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				CheckWordForSugesstions();
				isFileSaved=false;
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				CheckWordForSugesstions();
				isFileSaved=false;
				System.gc();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				CheckWordForSugesstions();
				isFileSaved=false;
			}
		});
        q.addInputMethodListener(new InputMethodListener() {
			
			@Override
			public void inputMethodTextChanged(InputMethodEvent arg0) {
				CheckWordForSugesstions();
			}
			@Override
			public void caretPositionChanged(InputMethodEvent arg0) {
				
			}
		});
        q.addKeyListener(new VmAutoCompleteTextBox(q));
        q.addKeyListener(new KeyAdapter() {
        	@Override
			public void keyReleased(KeyEvent e) {
				CheckWordForSugesstions();
				System.gc();
			}
            @Override
            public void keyPressed(KeyEvent ke) { 
            	if(ke.getKeyCode()==ke.VK_F5)
				{
            		executeQueryFun();
            		System.gc();
				}
            }
            @Override
            public void keyTyped(KeyEvent ke) {
            	isFileSaved=false;
            }
        });
        query.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
        query.setBorder(new LineBorder(Color.BLACK));
        query.setBounds(23, 100,950,60);
        jf.add(query);
        jf.getContentPane().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_F5) {
                	executeQueryFun();
                	System.gc();
                }
            }
        });
        jf.repaint();
        jf.revalidate();
        Message.setBounds(10,0, 940, 30);
        messagePanel.setLayout(null);
        messagePanel.add(Message);
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createBevelBorder(1));
        messagePanel.repaint();
        tsp.setBounds(30,305,940,350);
        resultPanel.setLayout(null);
        resultPanel.setBorder(BorderFactory.createBevelBorder(1));
        tsp.add("Result",resultPanel);
        tsp.add("Message",messagePanel);
        jf.add(tsp);
        tsp.repaint();
        jf.repaint();
        
        JLabel sugeestionLabel=new JLabel("Suggestions: ",JLabel.LEFT);
        sugeestionLabel.setBounds(25,160,100,30);
        jf.add(sugeestionLabel);
        jf.repaint();
        
        
        EnableDisableAutoComplete.setBounds(25,190,90,45);
        buttonHoverOnOff(EnableDisableAutoComplete);
        jf.add(EnableDisableAutoComplete);
        jf.repaint();
        EnableDisableAutoComplete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isAutocompleteEnabled)
                {
					isAutocompleteEnabled=false;
                    buttonHover(EnableDisableAutoComplete);
                    EnableDisableAutoComplete.setText("<html><center>Enable<br>Auto Complete</center></html>");
                    EnableDisableAutoComplete.repaint();
                    jf.repaint();
                    System.gc();
                }
                else
                {
                	isAutocompleteEnabled=true;
                    buttonHoverOnOff(EnableDisableAutoComplete);
                    EnableDisableAutoComplete.setText("<html><center>Disable<br>Auto Complete</center></html>");
                    EnableDisableAutoComplete.repaint();
                    jf.repaint();
                }
				
			}
		});
        
        
        JButton HelpB=new JButton("Controls");
        HelpB.setBounds(25,260,90,25);
        HelpB.setFont(new Font("Arial",Font.BOLD,12));
        HelpB.setBorder(BorderFactory.createBevelBorder(0));
        HelpB.setForeground(Color.WHITE);
        HelpB.setBackground(Color.decode("#FF8702"));
        HelpB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
            	HelpB.setBackground(Color.decode("#FF8706"));
            	HelpB.repaint();
            }
            @Override
            public void mouseEntered(MouseEvent me) 
            {
            	HelpB.setBackground(Color.decode("#FFA43F"));
            	HelpB.repaint();
            }
            @Override
            public void mouseExited(MouseEvent me) 
            {
            	HelpB.setBackground(Color.decode("#FF8702"));
            	HelpB.repaint();
            }
        });
        HelpB.addActionListener(new ActionListener() {
        	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel test = new JPanel();
				test.setBorder(BorderFactory.createEmptyBorder());
				test.setPreferredSize(new Dimension(400,600));
				test.setLayout(null);
				JScrollPane scrollFrame = new JScrollPane(test);
				scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollFrame.setBorder(BorderFactory.createEmptyBorder());
				scrollFrame.setPreferredSize(new Dimension(530,420));
				
				JLabel l1=new JLabel("To Execute Query: Click On");
				l1.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l1.setBounds(10,10, 210, 30);
				test.add(l1);
				test.repaint();
				JButton b1=new JButton("Execute Query");
				buttonHover(b1);
				b1.setSelected(false);
				b1.setBounds(230, 15, 120, 20);
				test.add(b1);
				test.repaint();
				
				JLabel l2=new JLabel("or press ");
				l2.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l2.setBounds(360,10, 210, 30);
				test.add(l2);
				test.repaint();
				
				JButton b2=new JButton("F5");
				b2.setBorder(BorderFactory.createBevelBorder(0));
				b2.setBackground(Color.decode("#686868"));
				b2.setForeground(Color.decode("#FFFFFF"));
				b2.setBounds(430, 15,50, 20);
				b2.setSelected(false);
				test.add(b2);
				test.repaint();
				
				JLabel l3=new JLabel("To On/Off VM DB Helper: Click On");
				l3.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l3.setBounds(10,50, 270, 30);
				test.add(l3);
				test.repaint();
				
		        
				JButton b3=new JButton("On");
				b3.setBorder(BorderFactory.createBevelBorder(0));
				b3.setBounds(280,55,50, 20);
				b3.setSelected(false);
				b3.setFont(new Font("Arial",Font.BOLD,12));
		        b3.setBorder(BorderFactory.createBevelBorder(0));
		        b3.setForeground(Color.WHITE);
		        b3.setBackground(Color.decode("#4F81BD"));
		        test.add(b3);
				test.repaint();
				
		        JLabel l4=new JLabel("To Enable/Disable Auto Complete: Click On");
				l4.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l4.setBounds(10,90, 330, 30);
				test.add(l4);
				test.repaint();
		        
				JButton b4=new JButton("<html><center>Enable<br>Auto Complete</center></html>");
				b4.setBorder(BorderFactory.createBevelBorder(0));
				b4.setBounds(350,95,90, 45);
				b4.setSelected(false);
				b4.setFont(new Font("Arial",Font.BOLD,12));
		        b4.setBorder(BorderFactory.createBevelBorder(0));
		        b4.setForeground(Color.WHITE);
		        b4.setBackground(Color.decode("#4F81BD"));
		        test.add(b4);
				test.repaint();
				Timer timer = new Timer();
		        TimerTask task = new TimerTask() {
		        	boolean bb=true;
		            @Override
		            public void run() {
		            	if(bb) {
		            	b3.setBackground(Color.decode("#e34d4d"));
		            	b3.setText("Off");
		            	b4.setBackground(Color.decode("#e34d4d"));
		            	b4.setText("<html><center>Disable<br>Auto Complete</center></html>");
		            	bb=false;
		            	}else {
		            		bb=true;b3.setText("On");b3.setBackground(Color.decode("#4F81BD"));
		            		b4.setBackground(Color.decode("#4F81BD"));b4.setText("<html><center>Enable<br>Auto Complete</center></html>");}
		            	b3.repaint();
		            }
		        };
		        timer.schedule(task, 000, 1000);
		        
		        JLabel l5=new JLabel("To use Suggestions: Click on suggestion you want to insert");
				l5.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l5.setBounds(10,160, 450, 30);
				test.add(l5);
				test.repaint();
				
				String DemoClausesWords[]= {"ALTER","ALTER COLUMN","ALTER TABLE","BACKUP DATABASE","CREATE TABLE","CREATE DATABASE","CREATE INDEX","DELETE FROM","DROP","DROP TABLE","DROP DATABASE","DROP CONSTRAINT","DROP INDEX","DROP VIEW","INSERT INTO","INSERT INTO SELECT","SELECT * FROM","SELECT INTO","TRUNCATE TABLE","UPDATE","Close VMDBM"};
			    DefaultListModel DemoClausesList = new DefaultListModel();
			    for (int i = 0; i < DemoClausesWords.length; i++) {
					DemoClausesList.add(i, DemoClausesWords[i]);
					System.gc();
				}
			    JList DemoClausesListObject=new JList(DemoClausesList);
			    JScrollPane DemoClausesListSC = new JScrollPane(DemoClausesListObject);
			    DemoClausesListSC.setBounds(10,200,250,85);
			    DemoClausesListSC.setBorder(BorderFactory.createEmptyBorder());
			    DemoClausesListSC.setBackground(Color.WHITE);
			    DemoClausesListSC.revalidate();
			    test.add(DemoClausesListSC);
				test.repaint();
				
				JLabel l6=new JLabel("<html><h2>How to connect to database:</h2><br>1. Enter the server name(default as localhost) it can be IP address.<br>2. Enter SQL SERVER instance.<br>3. Enter Username for database.<br>4. Enter Password for database.</html>");
				l6.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l6.setBounds(10,300, 450, 160);
				test.add(l6);
				test.repaint();
				
				JLabel l7=new JLabel("5. To login : Click on");
				l7.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l7.setBounds(10,460,160, 30);
				test.add(l7);
				test.repaint();
				
				JButton b5=new JButton("Login");
				buttonHover(b5);
				b5.setSelected(false);
				b5.setBounds(170,465, 80, 20);
				test.add(b5);
				test.repaint();
				
				JLabel l8=new JLabel("or press");
				l8.setFont(new Font("Bookman Old Style",Font.PLAIN,15));
				l8.setBounds(260,460,60, 30);
				test.add(l8);
				test.repaint();
				
				JButton b6=new JButton("Enter");
				b6.setBorder(BorderFactory.createBevelBorder(0));
				b6.setBackground(Color.decode("#686868"));
				b6.setForeground(Color.decode("#FFFFFF"));
				b6.setBounds(330,465,60, 20);
				b6.setSelected(false);
				test.add(b6);
				
				JLabel copyrightL = new JLabel("\u00a9 VAIBHAV MOJIDRA",JLabel.CENTER);
				copyrightL.setFont(new Font("Bookman Old Style",Font.PLAIN,25));
				copyrightL.setBounds(50,500,350, 50);
				test.add(copyrightL);
				test.repaint();
				test.repaint();
				JOptionPane.showMessageDialog(jf, scrollFrame, "VM Database Query Executor - Controls",JOptionPane.INFORMATION_MESSAGE);
		        jf.repaint();
			}
		});
        jf.add(HelpB);
        jf.repaint();
        
        JLabel ClausesL=new JLabel("Statements",JLabel.CENTER);
        ClausesL.setBounds(125,160,250,30);
        jf.add(ClausesL);
        jf.repaint();
        
        ClausesListSC.setBounds(125,190,250,85);
        ClausesListSC.setBorder(BorderFactory.createEmptyBorder());
        ClausesListSC.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ClausesListSC.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ClausesListSC.setBackground(Color.WHITE);
        ClausesListSC.revalidate();
        jf.add(ClausesListSC);
        jf.repaint();
        
        JLabel TableColumnsL=new JLabel("Tables And Columns",JLabel.CENTER);
        TableColumnsL.setBounds(400,160,250,30);
        jf.add(TableColumnsL);
        jf.repaint();
        
        TCListSC.setBounds(400,190,250,85);
        TCListSC.setBorder(BorderFactory.createEmptyBorder());
        TCListSC.setBackground(Color.WHITE);
        TCListSC.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        TCListSC.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TCListSC.revalidate();
        jf.add(TCListSC);
        jf.repaint();
        
        JLabel KeywordSL=new JLabel("Keywords",JLabel.CENTER);
        KeywordSL.setBounds(675,160,250,30);
        jf.add(KeywordSL);
        jf.repaint();
        
        KeywordsListSC.setBounds(675,190,250,85);
        KeywordsListSC.setBorder(BorderFactory.createEmptyBorder());
        KeywordsListSC.setBackground(Color.WHITE);
        KeywordsListSC.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        KeywordsListSC.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        KeywordsListSC.revalidate();
        jf.add(KeywordsListSC);
        jf.repaint();
        
        JButton countB=new JButton("COUNT(*)");
        buttonHover(countB);
        countB.setBounds(230, 283, 80, 22);
        countB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("COUNT(*)",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(countB);
        jf.repaint();
        
        JButton sumb=new JButton("SUM()");
        buttonHover(sumb);
        sumb.setBounds(350, 283, 80, 22);
        sumb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("SUM()",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(sumb);
        jf.repaint();
        
        JButton avgB=new JButton("AVG()");
        buttonHover(avgB);
        avgB.setBounds(470, 283, 80, 22);
        avgB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("AVG()",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(avgB);
        jf.repaint();
        
        JButton minB=new JButton("MIN()");
        buttonHover(minB);
        minB.setBounds(590, 283, 80, 22);
        minB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("MIN()",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(minB);
        jf.repaint();
        
        JButton maxB=new JButton("MAX()");
        buttonHover(maxB);
        maxB.setBounds(710, 283, 80, 22);
        maxB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("MAX()",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(maxB);
        jf.repaint();
        
        JButton BracketsB=new JButton("( )");
        buttonHover(BracketsB);
        BracketsB.setBounds(830, 283, 80, 22);
        BracketsB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				q.insert("( )",q.getCaretPosition());
				q.repaint();
			}
		});
        jf.add(BracketsB);
        jf.repaint();
        
     final UndoManager undoManager;

     undoManager = new UndoManager();
     Document doc = q.getDocument();
     doc.addUndoableEditListener(new UndoableEditListener() {
         @Override
         public void undoableEditHappened(UndoableEditEvent e) {
             undoManager.addEdit(e.getEdit());
         }
     });

     InputMap im = q.getInputMap(JComponent.WHEN_FOCUSED);
     ActionMap am = q.getActionMap();

     im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
     im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

     am.put("Undo", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent e) {
             try {
                 if (undoManager.canUndo()) {
                     undoManager.undo();
                 }
             } catch (Exception exp) {
                 exp.printStackTrace();
             }
         }
     });
     am.put("Redo", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent e) {
             try {
                 if (undoManager.canRedo()) {
                     undoManager.redo();
                 }
             } catch (Exception exp) {
                 exp.printStackTrace();
             }
         }
     });
    	} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    private static void CheckWordForSugesstions() {
    	try {ClausesList.removeAllElements();TCList.removeAllElements();KeyWordsList.removeAllElements();} catch (Exception e) {}
    	System.gc();
    	if(UserName.equals("")||Password.equals("")) {}else {
		String sentence=q.getText();
		int windex=0;
		String word="";
		for(int i=sentence.length()-1;i>=0;i--) 
		{
			if(Character.isWhitespace(sentence.charAt(i))) 
			{
				windex=i;
				word=sentence.substring(windex+1,sentence.length());
				break;
			}
		}
		for(int i=0;i<ClausesWords.length;i++)
		{
			if(ClausesWords[i].toLowerCase().contains(word.toLowerCase())||ClausesWords[i].toUpperCase().contains(word.toUpperCase())) 
			{
				ClausesList.addElement(ClausesWords[i]);
			}	
		}
		for(int i=0;i<TCNames.size();i++)
		{
			String ULSTCN=TCNames.get(i).toString();
			if((ULSTCN.toLowerCase()).contains(word.toLowerCase())||(ULSTCN.toUpperCase()).contains(word.toUpperCase())) 
			{
				TCList.addElement(TCNames.get(i).toUpperCase());
			}	
		}
		for(int i=0;i<KeyWords.length;i++)
		{
			if((KeyWords[i].toLowerCase()).contains(word.toLowerCase())||(KeyWords[i].toUpperCase()).contains(word.toUpperCase())) 
			{
				KeyWordsList.addElement(KeyWords[i]);
			}	
		}
		
		TCListObject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addSugesstionsToQuery(TCListObject);
		TCListObject.repaint();
		TCListSC.repaint();
		ClausesListObject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addSugesstionsToQuery(ClausesListObject);
		ClausesListObject.repaint();
		ClausesListSC.repaint();
		KeyWordsListObject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addSugesstionsToQuery(KeyWordsListObject);
		KeyWordsListObject.repaint();
		KeywordsListSC.repaint();
    }
	}

    public static void main(String[] args) {
    	VMDBM();
    	if(args.length>0) 
    	{
    		StringBuilder s=new StringBuilder("");
    		for (int i = 0; i < args.length; i++) {
				s.append(args[i]+" ");
			}
    		String argsfileName=s.toString();
    		argsfileName=argsfileName.trim();
    		if((argsfileName.toUpperCase()).endsWith(".VMDBE_SQLQ")) 
    		{
    			if(setFileContentToTextarea(argsfileName))
    			{
    				File fff=new File(argsfileName);
    				OpennedFileNameOnlyName=fff.getName();
    				OpennedFileNameWithPath=fff.getAbsolutePath();
    				isAnyFileOpenned=true;
    				jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
    				jf.repaint();
    				q.revalidate();
    				if(q.getText().equals("")) 
    				{}
    				else {
    					q.remove(ql);
    					System.gc();
    	                q.repaint();
    	                jf.repaint();
    				}
    				isFileSaved=true;
    			}
    			else {
    				JOptionPane.showMessageDialog(jf, "Unsupported file format","VM Database Query Executor- Error in opening file ", JOptionPane.ERROR_MESSAGE);
    			}
    		}
    		else {
    			JOptionPane.showMessageDialog(jf, "Unsupported file format","VM Database Query Executor- Error in opening file ", JOptionPane.ERROR_MESSAGE);
    			System.exit(0);
    		}
    	}
      }
    private static boolean CheckIfFileExists(String filename) {
    	boolean s=false;
    	try {
    		File f = new File(filename);
        	if(f.exists() && !f.isDirectory()) { 
        		s=true;
        	}
        	else {s=false;}	
		} catch (Exception e) {
			s=false;
		}
    	return s;
    }

    private static void addSugesstionsToQuery(JList myjl) 
    {
    	myjl.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(!arg0.getValueIsAdjusting()) 
				{
					try {
					try {}
					finally {
						String sentence=q.getText();
						int windex=0;
						for(int i=sentence.length()-1;i>=0;i--) 
						{
							if(Character.isWhitespace(sentence.charAt(i))) 
							{
								windex=i;								
								break;
							}
						}
						StringBuilder str = new StringBuilder(sentence);
						str.replace(windex+1,sentence.length(), myjl.getSelectedValue().toString());
						q.setText(str.toString());
					}
							
					try {}
					finally {
						q.repaint();
						CheckWordForSugesstions();
					}
					}catch (Exception e) {
					}
				}
			}
		});
    	myjl.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				arg0.consume();
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				arg0.consume();
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				arg0.consume();
				
			}
		});
    }
    
    private static void CheckVMExit(String Query)
    {
        if(Query.trim().equalsIgnoreCase("Close VMDBE"))
        {
            System.exit(0);
        }
    }
    
    private static void executeDDL(String myMessage,String Query) 
    {
    	try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+dbs.getSelectedItem(),UserName,Password).createStatement().execute(Query);
			tsp.setSelectedIndex(1);
            Message.setForeground(Color.BLACK);
            Message.setText("Message: "+myMessage);
            Message.repaint();
		} catch (Exception e) {
			tsp.setSelectedIndex(1);
            Message.setForeground(Color.RED);
            Message.setText("Erorr Message: "+e.getMessage());
            Message.repaint();
		}
    }
    private static int executeDML(String Query) 
    {
    	int eon=-1;
    	try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			eon=DriverManager.getConnection("jdbc:sqlserver://"+LocalhostN+"\\"+ServerName+":1433;databaseName="+dbs.getSelectedItem(),UserName,Password).createStatement().executeUpdate(Query);
		} catch (Exception e) {
			tsp.setSelectedIndex(1);
            Message.setForeground(Color.RED);
            Message.setText("Erorr Message: "+e.getMessage());
            Message.repaint();
            eon=-1;
		}
    	return eon;	
    } 
    
    private static void showMessage(String Mymes) 
    {
    	tsp.setSelectedIndex(1);
    	Message.setForeground(Color.BLACK);
        Message.setText("Message: "+Mymes);
        Message.repaint();
    }
    private static void refreshTCSugesstions() 
    {
    	try{TCNames.clear();}catch (Exception e) {}
        Vector v=getTableNames(dbs.getSelectedItem().toString());
        Iterator value = v.iterator();
        while (value.hasNext()) {
        	String CC=value.next().toString();
            TCNames.add(CC);
        }
        Vector v2=getColumnNames(dbs.getSelectedItem().toString());
        Iterator value2 = v2.iterator();
        while (value2.hasNext()) {
            TCNames.add(value2.next().toString());
        }
    }
    
    private static boolean setFileContentToTextarea(String FilePath) 
    {
    	boolean isSetted=false;
    	q.setText("");
    	
    	try {
    		BufferedReader in = new BufferedReader(new FileReader(FilePath));
    		q.read(in, "READING FILE");
    		isSetted=true;
    	}
    	catch (Exception e)
    	{isSetted=false;}
    	
    	return isSetted;
    }
    private static boolean saveFile(String filename) {
    	boolean isFiledSaved=false;
    	BufferedWriter outFile = null;
        try {
           outFile = new BufferedWriter(new FileWriter(filename));
           q.write(outFile);  
           isFiledSaved=true;
        } catch (Exception ex) {
        	isFiledSaved=false;
        } finally {
           if (outFile != null) {
              try {
                 outFile.close();
              } catch (Exception e) {
              }
           }
        }
        jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
		jf.repaint();
		isFileSaved=true;
        return isFiledSaved;
	}
    
    private static boolean saveAsFile()
    {
    	boolean isFiledSaved=false;    	
    	JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "DOCUMENTS"+System.getProperty("file.separator")+"VMDBE SQL Queries"));
    	fileChooser.setFileFilter(new FileNameExtensionFilter("VM Database Query File (.VMDBE_SQLQ)","VMDBE_SQLQ"));
    	fileChooser.setAcceptAllFileFilterUsed(false);
        int retval = fileChooser.showSaveDialog(jf);
        if (retval == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          String fn=null;
          if(file.getAbsolutePath().toUpperCase().endsWith(".VMDBE_SQLQ"))
          {
        	  fn=file.getAbsolutePath();
          }
          else { 
        	  fn=file.getAbsolutePath()+".VMDBE_SQLQ";
          }
          
          BufferedWriter outFile = null;
          try {
             outFile = new BufferedWriter(new FileWriter(fn));
             q.write(outFile);
             isFiledSaved=true;
             String Filen=null;
             if(file.getName().toUpperCase().endsWith(".VMDBE_SQLQ")) 
             {
            	 Filen=file.getName();
             }
             else {
            	 Filen=file.getName()+".VMDBE_SQLQ";
             }
             OpennedFileNameOnlyName=Filen;
             OpennedFileNameWithPath=fn;
             isAnyFileOpenned=true;
             isFileSaved=true;
             jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
             jf.repaint();
          } catch (Exception ex) {
          	isFiledSaved=false;
          } finally {
             if (outFile != null) {
                try {
                   outFile.close();
                } catch (Exception e) {
                }
             }
          }
        }
        return isFiledSaved;
	}
    private static void openTheFile() 
    {
    	JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "DOCUMENTS"+System.getProperty("file.separator")+"VMDBE SQL Queries"));
        fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("VM Database Query File (.VMDBE_SQLQ)", "VMDBE_SQLQ");
        fileChooser.addChoosableFileFilter(filter);
		int result = fileChooser.showOpenDialog(jf);
		if (result == JFileChooser.APPROVE_OPTION){
			File selectedFile = fileChooser.getSelectedFile();
			if(setFileContentToTextarea(selectedFile.getAbsolutePath()))
			{
				OpennedFileNameOnlyName=selectedFile.getName();
				OpennedFileNameWithPath=selectedFile.getAbsolutePath();
				isAnyFileOpenned=true;
				isFileSaved=true;
				jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
				jf.repaint();
			}
			else {
				JOptionPane.showMessageDialog(jf,"Error in opening "+selectedFile.getName(),"VM Database Query Executor",JOptionPane.ERROR_MESSAGE);
			}
		}
    }
    private static void newFileAction() 
    {
    	if(isAnyFileOpenned)
		{
			if(isFileSaved) {
				OpennedFileNameOnlyName="";
				OpennedFileNameWithPath="";
				isAnyFileOpenned=false;
				q.setText("");
				jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
				jf.repaint();
			}
			else {
			int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save changes to "+OpennedFileNameOnlyName+" ?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if(answer==JOptionPane.YES_OPTION) 
			{
				boolean isfileSaved=saveFile(OpennedFileNameWithPath);
				if(isfileSaved) {
					OpennedFileNameOnlyName="";
					OpennedFileNameWithPath="";
					isAnyFileOpenned=false;
					q.setText("");
					jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
					jf.repaint();
				}
			}
			else if(answer==JOptionPane.NO_OPTION) 
			{
				OpennedFileNameOnlyName="";
				OpennedFileNameWithPath="";
				isAnyFileOpenned=false;
				q.setText("");
				jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
				jf.repaint();
			}
			else{}
			}
		}
		else 
		{
			if(q.getText().equals("")) 
			{
				OpennedFileNameOnlyName="";
				OpennedFileNameWithPath="";
				isAnyFileOpenned=false;
				q.setText("");
				jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
				jf.repaint();
			}
			else {
				int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save the Query?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer==JOptionPane.YES_OPTION) 
				{
					if(saveAsFile()) 
					{
						OpennedFileNameOnlyName="";
						OpennedFileNameWithPath="";
						isAnyFileOpenned=false;
						q.setText("");
						jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
						jf.repaint();
					}
				}
				else if(answer==JOptionPane.NO_OPTION) 
				{
					OpennedFileNameOnlyName="";
					OpennedFileNameWithPath="";
					isAnyFileOpenned=false;
					q.setText("");
					jf.setTitle("VM Database Query Executor "+OpennedFileNameOnlyName);
					jf.repaint();
				}
				else {}
			}
		}
    	CheckForSaveAsED();
	}
    private static void openFileAction() 
    {
    	if(isAnyFileOpenned) 
		{
			if(isFileSaved) 
			{
				openTheFile();
			}
			else {
			int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save changes to "+OpennedFileNameOnlyName+" ?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if(answer==JOptionPane.YES_OPTION) {
				boolean isfileSaved=saveFile(OpennedFileNameWithPath);
				if(isfileSaved) {
					openTheFile();
				}
				else {
				}	
			}else if(answer==JOptionPane.NO_OPTION)
			{
				openTheFile();
			}
			else 
			{
			}
		 }
		}
		else {
			if((q.getText()).equals("")) 
			{
				openTheFile();
				if(q.getText().equals(""))
				{}
				else {
					q.remove(ql);
	                q.repaint();
	                System.gc();
	                jf.repaint();
				}
			}
			else {
				int answer=JOptionPane.showConfirmDialog(jf,"Do you want to save the Query?","VM Database Query Executor", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer==JOptionPane.YES_OPTION) 
				{
					if(saveAsFile()) 
					{
						openTheFile();
						if(q.getText().equals("")) 
	    				{}
	    				else {
	    					q.remove(ql);
	    	                q.repaint();
	    	                System.gc();
	    	                jf.repaint();
	    				}
					}
				}
				else if(answer==JOptionPane.NO_OPTION) {
					openTheFile();
					if(q.getText().equals("")) 
    				{}
    				else {
    					q.remove(ql);
    	                q.repaint();
    	                System.gc();
    	                jf.repaint();
    				}
				}
				else {}
			}
		}
    	CheckForSaveAsED();
	}
    private static void saveFileAction() 
    {
    	if(!isFileSaved) {
			if(isAnyFileOpenned) 
			{
				saveFile(OpennedFileNameWithPath);
			}
			else 
			{
				saveAsFile();
			}
			}
    	CheckForSaveAsED();
	}
    private static void saveAsFileAction() 
    {
    	saveAsFile();
		isFileSaved=true;
		CheckForSaveAsED();
	}
    private static void CheckForSaveAsED() 
    {
    	if(isAnyFileOpenned) {
    		saveAsFile.setEnabled(true);
    		saveAsFile.repaint();
    	}
    	else {
    		saveAsFile.setEnabled(false);
    		saveAsFile.repaint();
    	}
    }
    private static boolean checkFromChangeLook(String Query) 
    {
    	boolean ss=true;
    	if(Query.trim().equalsIgnoreCase("VMDBE-Theme Motif"))
        {
    		try {
            	UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;
            	
    		} catch (Exception e) {
    		}
        }
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Windows")) {
    		try {
            	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Mac")) {
    		try {
            	UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Nimbus")) {
    		try {
            	UIManager.setLookAndFeel("org.jdesktop.swingx.plaf.nimbus.NimbusLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme SeaGlass")) {
    		try {
            	UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Paper")) {
    		try {
            	UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Smart")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Texture")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Black")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme McWin")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme WINXP")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Pattern")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Alumin")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme VAIBHAV")) {
    		try {
            	UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else if(Query.trim().equalsIgnoreCase("VMDBE-Theme Default")) {
    		try {
    			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            	jf.repaint();
            	SwingUtilities.updateComponentTreeUI(jf);
            	jf.repaint();
            	jf.revalidate();
            	ss=false;	
    		} catch (Exception e) {
    		}
    	}
    	else {ss=true;}
    	return ss;
    } 
    
    private static void executeQueryFun() {
    	try{resultPanel.remove(sp);}catch(Exception e){}
    	System.gc();
    	String Query;
    	String Selected=q.getSelectedText();
    	if(Selected==null) {
    		Query=(q.getText()).toLowerCase();
    	}
    	else {
    		Query=Selected.toLowerCase();
    	}
    	if(UserName.equals("")||Password.equals("")) 
    	{
    		JOptionPane.showMessageDialog(jf,"Make Sure You have Logged in","Login Required",JOptionPane.ERROR_MESSAGE);
    	}
    	else 
    	{
    		CheckVMExit(Query);
    		if(checkFromChangeLook(Query)) 
    		{
    		Message.setText("");
    		Message.setForeground(Color.BLACK);
    		Message.repaint();
    		if(Query.contains("select")) 
    		{
        		addDataToTable(Query);
        		refreshTCSugesstions();
    		}
    		else if(Query.contains("create"))
    		{
    			executeDDL("Table created successfully.", Query);
    			refreshTCSugesstions();
    		}
    		else if(Query.contains("alter"))
    		{
    			executeDDL("Table altered successfully.", Query);
    			refreshTCSugesstions();
    		}
    		else if(Query.contains("drop"))
    		{
    			executeDDL("Table dropped successfully.", Query);
    			refreshTCSugesstions();
    		}
    		else if(Query.contains("truncate"))
    		{
    			executeDDL("Table truncated successfully.", Query);
    			refreshTCSugesstions();
    		}
    		else if(Query.contains("rename"))
    		{
    			executeDDL("Table rename successfully.", Query);
    			refreshTCSugesstions();
    		}
    		else if(Query.contains("insert"))
    		{
    				int rowsAffected=executeDML(Query);
    				if(rowsAffected!=-1) {
    					showMessage(rowsAffected+" rows inserted.");
    				}
    				refreshTCSugesstions();
    		}
    		else if(Query.contains("delete"))
    		{
    				int rowsAffected=executeDML(Query);
    				if(rowsAffected!=-1) {
    					showMessage(rowsAffected+" rows deleted.");
    				}
    				refreshTCSugesstions();
    		}
    		else if(Query.contains("update"))
    		{
    				int rowsAffected=executeDML(Query);
    				if(rowsAffected!=-1) {
    					showMessage(rowsAffected+" rows updated.");
    				}
    				refreshTCSugesstions();
    		}
    		else 
    		{
    			executeDDL("Query executed successfully.", Query);
    			refreshTCSugesstions();
    		}
        }
    }
	}
}
