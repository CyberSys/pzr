package zombie.network;

import java.util.ArrayList;
import java.util.HashMap;


public class DBResult {
	private HashMap values = new HashMap();
	private ArrayList columns = new ArrayList();
	private String type;
	private String tableName;

	public HashMap getValues() {
		return this.values;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String string) {
		this.type = string;
	}

	public ArrayList getColumns() {
		return this.columns;
	}

	public void setColumns(ArrayList arrayList) {
		this.columns = arrayList;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String string) {
		this.tableName = string;
	}
}
