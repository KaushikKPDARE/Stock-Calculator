package StockPack;

import java.sql.*;
import java.util.*;

/**
 * Class for calculating the Fred's stocks based on Mary's Model
 * 
 * Assumptions:
 * 1) Database and tables for holding, model and ord are already present.
 * 2) Database url, Username, password are hardcoded in the code, if not we can use config/properties file for that.
 * 
 * @author Kaushik Padmanabhan
 */
public class StockCalculator {

	static String JDBC_DRIVER = "", DB = "", user = "", pass = "";
	static Connection connection = null;
	static Statement statement = null;

	static double stockSum = 0.0;
	static List<String> commonStocks = new ArrayList<String>();

	public StockCalculator(){

		JDBC_DRIVER = "com.mysql.jdbc.Driver";
		DB = "jdbc:mysql://localhost:3306/";
		user = "username";
		pass = "password";
		try {
			connection = DriverManager.getConnection(DB, user, pass);
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Get the total stock sum from Holding table
	public void getStockSum() throws SQLException{

		String sql = "SELECT sum(AMT) FROM HOLDING";
		ResultSet result = statement.executeQuery(sql);
		result.next();

		stockSum = Double.parseDouble(result.getString(1));

	}

	// To find the difference amount
	public static List<String> getAmountDiff(double amount, double percent){

		double holdingPercent = (amount/stockSum)*100;
		List<String> resultList = new ArrayList<String>();
		double diffPercent = 0.0;

		if(holdingPercent >= percent){
			diffPercent = holdingPercent - percent;
			resultList.add("S");
		} else{
			diffPercent = percent - holdingPercent;
			resultList.add("B");
		}

		resultList.add(String.valueOf(diffPercent*(stockSum/100)));
		return resultList;

	}

	// Fill out the common stocks in the order table
	public void fillCommonOrderFromBoth() throws SQLException{

		String selectQuery = "SELECT h.AMT, m.PERCENT, h.SEC from HOLDING as h inner join MODEL as m on h.SEC = m.SEC", insertQuery = "";
		ResultSet result = statement.executeQuery(selectQuery);

		while (result.next()) {
			double amt = Double.parseDouble(result.getString("AMT"));
			double percent = Double.parseDouble(result.getString("PERCENT"));
			commonStocks.add(result.getString("SEC"));

			List<String> resultList = getAmountDiff(amt, percent);
			insertQuery = "INSERT INTO ORD VALUES (" + result.getString("SEC") + "," + resultList.get(0) + "," + Double.parseDouble(resultList.get(1)) + ")";
			statement.executeUpdate(insertQuery);
		}

	}

	// Fill out the buying stocks from Model table into the order table
	public void fillBuyingOrderFromModel() throws SQLException{
		String sql = "SELECT * FROM MODEL", insertQuery = "";
		ResultSet result = statement.executeQuery(sql);

		while(result.next()){
			if(!commonStocks.contains(result.getString("SEC"))){
				insertQuery = "INSERT INTO ORD VALUES (" + result.getString("SEC") + ",B," + Double.parseDouble(result.getString("PERCENT"))*(stockSum/100) + ")";
				statement.executeUpdate(insertQuery);
			}
		}
	}

	// Fill out the selling stocks from Holding table into the order table
	public void fillSellingOrderFromHolding() throws SQLException{
		String sql = "SELECT * FROM HOLDING", insertQuery = "";
		ResultSet result = statement.executeQuery(sql);

		while(result.next()){
			if(!commonStocks.contains(result.getString("SEC"))){
				insertQuery = "INSERT INTO ORD VALUES (" + result.getString("SEC") + ",S," + Double.parseDouble(result.getString("AMT")) + ")";
				statement.executeUpdate(insertQuery);
			}
		}
	}

	public static void main(String[] args) {
		try{

			Class.forName("com.mysql.jdbc.Driver");
			StockCalculator stock = new StockCalculator();

			stock.getStockSum();
			stock.fillCommonOrderFromBoth();
			stock.fillSellingOrderFromHolding();
			stock.fillBuyingOrderFromModel();

		}catch(Exception e){
			e.printStackTrace();
		}finally{

			try{
				if(connection!=null || statement!=null)
					connection.close();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}
}