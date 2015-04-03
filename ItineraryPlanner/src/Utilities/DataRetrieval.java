package Utilities;

import org.json.simple.JSONObject;

public class DataRetrieval {
	String dataPath;
	String datString;
	
	public DataRetrieval(String dataPath, String datString) {
		this.dataPath = dataPath;
		this.datString = datString;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getDatString() {
		return datString;
	}

	public void setDatString(String datString) {
		this.datString = datString;
	}
	
	public JSONObject getData() {
		
		return null;
	}
}
