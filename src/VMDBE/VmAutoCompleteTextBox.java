package VMDBE;

import static VMDBE.VMDatabaseQueryExecuter.isAutocompleteEnabled;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

public class VmAutoCompleteTextBox extends KeyAdapter
{
	private JTextArea MyTextArea;
	@SuppressWarnings("rawtypes")
	private List SuggestionList;
	
	@SuppressWarnings("rawtypes")
	public VmAutoCompleteTextBox(JTextArea TA)
	{
		MyTextArea = TA;
		SuggestionList = new ArrayList();
		GetSuggestions();
	}
	
	public void keyPressed(KeyEvent key)
	{
		switch(key.getKeyCode())
		{
		case KeyEvent.VK_BACK_SPACE : 
			break;
		case KeyEvent.VK_F1 : 
			MyTextArea.setText(MyTextArea.getText());
			break;
		default : 
			EventQueue.invokeLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					String kt = MyTextArea.getText().toUpperCase();
					if(isAutocompleteEnabled) 
					{
						autoComplete(kt);
					}
					
				}
			});
		}								
	}
	
	public void autoComplete(String kt)
	{
		String complete = "";
		int start = kt.length();
		int last = kt.length();
		int a;
		
		for(a=0;a<SuggestionList.size();a++)
		{
			if(SuggestionList.get(a).toString().startsWith(kt))
			{
				complete = SuggestionList.get(a).toString();
				last = complete.length();
				break;
			}
		}
		if(last>start)
		{
			MyTextArea.setText(complete);
			MyTextArea.setCaretPosition(last);
			MyTextArea.moveCaretPosition(start);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void GetSuggestions()
	{
		SuggestionList.add("");
		SuggestionList.add("ALTER");
		SuggestionList.add("ALTER COLUMN");
		SuggestionList.add("ALTER TABLE");
		SuggestionList.add("BACKUP DATABASE");
		SuggestionList.add("CREATE TABLE");
		SuggestionList.add("CREATE DATABASE");
		SuggestionList.add("CREATE INDEX");
		SuggestionList.add("DELETE FROM");
		SuggestionList.add("DROP");
		SuggestionList.add("DROP TABLE");
		SuggestionList.add("DROP DATABASE");
		SuggestionList.add("DROP CONSTRAINT");
		SuggestionList.add("DROP INDEX");
		SuggestionList.add("DROP VIEW");
		SuggestionList.add("INSERT INTO");
		SuggestionList.add("INSERT INTO SELECT");
		SuggestionList.add("SELECT * FROM");
		SuggestionList.add("SELECT INTO");
		SuggestionList.add("TRUNCATE TABLE");
		SuggestionList.add("UPDATE");
		SuggestionList.add("Close VMDBM");
	}
}

