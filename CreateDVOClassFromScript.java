package tr.gov.adalet.uyap3.sample.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import tr.gov.adalet.uyap3.ui.sample.base.SamplePanelBase;
import tr.gov.adalet.uyap3.ui.sample.base.UyapSample;

@SuppressWarnings("serial")

public class CreateDVOClassFromScript extends SamplePanelBase {

	/**
	 * @author Murat YILMAZ
	 */
	Locale localeEn = new Locale("en_EN");
	Locale trlocale = new Locale("tr-TR");
	private static final long serialVersionUID = 1L;
	private JTextArea txtSqlCodes;
	private JLabel lblCreateTable;
	private JScrollPane scrollPane;
	private JTextArea txtDvoClass;
	private JLabel lblDvoClass;
	private JScrollPane scrollPane_1;
	private JButton btnCreateCodes;
	private String tableName;
	private List<ColumnDefinition> columnDefinitions=new ArrayList();
	private JButton btnTemizle;
	
	public CreateDVOClassFromScript() {
		super();
		initialize();
	}

	private void initialize() {
		setTitle("Create DVO Class From Create Table Script");
		setLayout(new MigLayout("", "[grow][grow]", "[]3[grow][]"));
		setSize(1000, 800);
		add(getLblCreateTable(), "cell 0 0,alignx center");
		add(getLblDvoClass(), "cell 1 0");
		add(getScrollPane(), "cell 0 1,grow");
		add(getScrollPane_1(), "cell 1 1,grow");
		add(getBtnCreateCodes(), "flowx,cell 0 2");
		add(getBtnTemizle(), "cell 0 2");
	}
	
//	public static void  main(String args[]){
//		CreateDVOClassFromScript startClass=new CreateDVOClassFromScript();
//		startClass.setVisible(true);
//	}
	
	private JTextArea getTxtSqlCodes() {
		if (txtSqlCodes == null) {
			txtSqlCodes = new JTextArea();
		}
		return txtSqlCodes;
	}
	private JLabel getLblCreateTable() {
		if (lblCreateTable == null) {
			lblCreateTable = new JLabel("Your Create Table Script");
		}
		return lblCreateTable;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTxtSqlCodes());
		}
		return scrollPane;
	}
	private JTextArea getTxtDvoClass() {
		if (txtDvoClass == null) {
			txtDvoClass = new JTextArea();
		}
		return txtDvoClass;
	}
	private JLabel getLblDvoClass() {
		if (lblDvoClass == null) {
			lblDvoClass = new JLabel("Your Entity(DVO) Class Code");
		}
		return lblDvoClass;
	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getTxtDvoClass());
		}
		return scrollPane_1;
	}
	private JButton getBtnCreateCodes() {
		if (btnCreateCodes == null) {
			btnCreateCodes = new JButton("Create DVO Class");
			btnCreateCodes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createDVOClass();
				}
			});
		}
		return btnCreateCodes;
	}

	protected void createDVOClass()  {
		getTxtDvoClass().setText("");
		String sqlCode=getTxtSqlCodes().getText();
		getColumnDef(sqlCode);
		StringBuffer dvoClassCode=new StringBuffer();
		if(columnDefinitions!=null && !columnDefinitions.isEmpty()){
			setTableName(strDuzelt(getTableName(), true)); 
			//dvoClassCode.append("@Getter @Setter \n");
			dvoClassCode.append("public class "+getTableName()+"DVO extends DVOBase {\n\n");
			
			for(ColumnDefinition col:columnDefinitions){
				dvoClassCode.append("private");
				
				ColDataType colDataType=col.getColDataType();
				if(colDataType.getDataType().contains("VARCHAR2"))
					dvoClassCode.append(" String ");
				else if(colDataType.getDataType().contains("CHAR"))
					dvoClassCode.append(" Character ");
				else if(colDataType.getDataType().contains("BLOB"))
					dvoClassCode.append(" byte[] ");
				else if(colDataType.getDataType().contains("DATE"))
					dvoClassCode.append(" Timestamp ");
				else if(colDataType.getDataType().contains("TIMESTAMP"))
					dvoClassCode.append(" Timestamp ");
				else if(colDataType.getDataType().contains("LONG"))
					dvoClassCode.append(" Character[] ");
				else if(colDataType.getDataType().contains("BOOLEAN"))
					dvoClassCode.append(" boolean ");
				else if(colDataType.getDataType().contains("NUMBER"))
				{
					String valueData=colDataType.toString();
					if(valueData.contains("("))
					{
						valueData = valueData.split("[\\(\\)]")[1]; //parantez içindeki veri
						valueData = valueData.replaceAll(", ", "");
						int valueInt=Integer.parseInt(valueData);
						if(valueInt<5)
							dvoClassCode.append(" int ");
						else if(valueInt>=5 && valueInt<10)
							dvoClassCode.append(" Integer ");
						else 
							dvoClassCode.append(" BigDecimal ");
					}
					else
						dvoClassCode.append(" BigDecimal ");
				}
				else
					dvoClassCode.append(" Unrecognized ");
				dvoClassCode.append(toCamelCase(col.getColumnName()));
				dvoClassCode.append(";\n");
			}
			dvoClassCode.append("\n}");
		}
		
		getTxtDvoClass().setText(dvoClassCode.toString());
	}
	
	private String toCamelCase(String s) {
		String[] parts = s.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + toProperCase(part);
		}
		
		//return camelCaseString;
		return camelCaseString.substring(0, 1).toLowerCase(trlocale) + camelCaseString.substring(1);
	}

	private String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(trlocale);
	}

	private String strDuzelt(String str,Boolean isClassName){
		
		String d="";
		
		for(int j=0;j<str.length();j++){				
			
			if(str.substring(j, j+1).equals("_")) 
				continue;
			else if (j>0){
				if(str.substring(j-1, j).equals("_"))
					d = d+  str.substring(j, j+1).toUpperCase(localeEn);
				else
					d = d+  str.substring(j, j+1).toLowerCase(localeEn);
			}else {
				if(isClassName)
					d = d+  str.substring(j, j+1).toUpperCase(localeEn);
				else
					d = d+  str.substring(j, j+1).toLowerCase(localeEn);
			}
		}
		return d;	
	}
	
	private void getColumnDef(String sqlCode){
		CCJSqlParserManager pm = new CCJSqlParserManager();
		try {
			Statement statement = pm.parse(new StringReader(sqlCode));
			if (statement instanceof CreateTable) {

				CreateTable create = (CreateTable) statement;
				setTableName(create.getTable().getName()) ;
				columnDefinitions = create.getColumnDefinitions();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Script Format Error!.");
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	private JButton getBtnTemizle() {
		if (btnTemizle == null) {
			btnTemizle = new JButton("Temizle");
			btnTemizle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTxtDvoClass().setText("");
					getTxtSqlCodes().setText("");
				}
			});
		}
		return btnTemizle;
	}
}
