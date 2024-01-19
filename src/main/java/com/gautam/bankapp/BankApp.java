package com.gautam.bankapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class BankApp {
	Scanner sc;
	public static void main(String[] args) {
		
		BankApp ba=new BankApp();
		ba.startBankApp();
		
		
	
	}
	public  void startBankApp()
	{
		System.out.println("--------------------------------");
		System.out.println("1.Add Account");
		System.out.println("2.Fund Transfer");
		System.out.println("3.Mini Statement");
		System.out.println("4.Exit");
		System.out.println("\nSelect any one option");
		System.out.println("--------------------------------");
		getUserInput();
	}
	
	public void  getUserInput()
	{
	   sc=new Scanner(System.in);
	   int useroption=sc.nextInt();
	   System.out.println("--------------------------------");
	   if(useroption==1)
		{
			addAccount();
			startBankApp();
			
			
			
		}
		else if(useroption==2)
		{

			fundTransfer();
			startBankApp();
			
		
		}
		else if(useroption==3)
		{
			miniStatement();
			startBankApp();
			
			
		}
		else {
			System.out.println("BankApp Closed");
			System.exit(0);
		}
		
	}
	void addAccount()
	{
		System.out.println("Enter ID:");
		int id=sc.nextInt();
		System.out.println("Enter Name:");
		String name=sc.next();
		System.out.println("Enter Email");
		String email=sc.next();
		System.out.println("Enter phone No");
		String phonNo=sc.next();
		System.out.println("Enter Account No:");
	    String accno=sc.next();
		Connection con=null;
		try {
             con=DbConnect.getConnection();
             
			if(con!=null)
			   con.setAutoCommit(false);
			//-----------creating account----------------
			PreparedStatement ps1= con.prepareStatement("insert into users values(?,?,?,?,?)");
			
			ps1.setInt(1, id);
			ps1.setString(2, name);
			ps1.setString(3, email);
			ps1.setString(4, phonNo);
			ps1.setString(5, accno);
			
			int rowCount1=ps1.executeUpdate();
			//-------------------------------------------
			
			//----------------deposit money in account----------------
			PreparedStatement ps2=con.prepareStatement("insert into total_amount values(?,?,?)");
			
			ps2.setInt(1, id);
			ps2.setString(2, accno);
			ps2.setInt(3, 50000);
			
			int rowCount2=ps2.executeUpdate();
			//--------------------------------------------------------
			
			if(rowCount1>0 && rowCount2>0)
			{
				con.commit();
				System.out.println("Account created successfully");
			}
			else
			{
				con.rollback();
				System.out.println("Account creation failed due to some error");
			}
		}
		catch(Exception e)
		{
			try
			{
				con.rollback();
			}
			catch(Exception ee)
			{
				System.out.println(ee);
			}
			System.out.println(e);
		}
		
		
//		finally {
//			try {
//				if(con!=null)
//				{
//					con.close();
//				}
//			}catch(SQLException ee)
//			{
//				System.out.println(ee);
//			}
//			
//		}
		startBankApp();
	}
	
	void fundTransfer()
	{
		System.out.println("Enter from Account No:-");
		int from_acc=sc.nextInt();
		System.out.println("Enter To Account No:-");
		int to_acc=sc.nextInt();
		
		System.out.println("Enter amount:-");
		int deposit_amount=sc.nextInt();
		int from_bal=0; int to_bal=0;
		Connection con=null;
		try {
			
			con=DbConnect.getConnection();
			con.setAutoCommit(false);
			String query="select balance from total_amount where account_no=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setInt(1, from_acc);
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				from_bal=rs.getInt(1);
			}
			String query2="select balance from total_amount where account_no=?";
			PreparedStatement ps2=con.prepareStatement(query2);
			ps2.setInt(1, to_acc);
			ResultSet rs2=ps2.executeQuery();
			while(rs2.next())
			{
				to_bal=rs2.getInt(1);
			}
			
			int new_from_bal=from_bal-deposit_amount;
			int new_to_bal=to_bal+deposit_amount;
			
			String query3="update total_amount set balance=? where account_no=?";
			PreparedStatement ps3=con.prepareStatement(query3);
			ps3.setInt(1,new_from_bal);
			ps3.setInt(2, from_acc);
			int rowCount3=ps3.executeUpdate();
			
			
			String query4="update total_amount set balance=? where account_no=?";
			PreparedStatement ps4=con.prepareStatement(query3);
			ps4.setInt(1,new_to_bal);
			ps4.setInt(2, to_acc);
			int rowCount4=ps4.executeUpdate();
			
			//to add mini_statement
			
			
			//-------------------get systems current date and time
			Date d=new Date();
			SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yy");
			String date1=sdf.format(d);
			
			SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
			String time1=sdf2.format(d);
			//-------------------------------------------------------
			String query5="insert into mini_statement values(?,?,?,?,?)";
			
			PreparedStatement ps5=con.prepareStatement(query5);
			
			ps5.setInt(1, from_acc);
			ps5.setInt(2, deposit_amount);
			ps5.setString(3, "d");
			ps5.setString(4, date1);
			ps5.setString(5, time1);
			
			int rowCoun5=ps5.executeUpdate();
			
			//----------------------------------------------
            String query6="insert into mini_statement values(?,?,?,?,?)";
			
			PreparedStatement ps6=con.prepareStatement(query6);
			
			ps6.setInt(1, to_acc);
			ps6.setInt(2, deposit_amount);
			ps6.setString(3, "c");
			ps6.setString(4, date1);
			ps6.setString(5, time1);
			
			int rowCoun6=ps6.executeUpdate();
			
			if(rowCount3>0&&rowCount4>0&&rowCoun5>0&&rowCoun6>0)
			{
				con.commit();
				System.out.println("Transaction successfull");
			}
			else {
				con.rollback();
				System.out.println("Transaction Failed!!");
			}
			
				
			
		}catch(Exception e)
		{
			
			try {
				con.rollback();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		
		
		}
	
	void miniStatement()
	{
		System.out.println("Enter Account No.");
		int accno=sc.nextInt();
		Connection con=null;
		try {
			
			con=DbConnect.getConnection();
			
			
			
			String query="select * from mini_statement where account_no=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setInt(1, accno);
			ResultSet rs=ps.executeQuery();
			
			System.out.print("AccountNo"+"\t"+"Amount"+"\t"+"Credit/Debit"+"\t"+"Date"+"\t"+"Time");
			System.out.println();
			StringBuffer statement_details=new StringBuffer();
			statement_details.append("Bellow details of this"+"\t"+accno+"\t"+"account is:");
			statement_details.append("\n");
			
			while(rs.next())
			{
				System.out.print(rs.getInt(1));
				statement_details.append(""+rs.getInt(1));
				System.out.print("\t"+rs.getInt(2));
				statement_details.append("\t"+rs.getInt(2));
				System.out.print("\t"+rs.getString(3));
				statement_details.append("\t"+rs.getString(3));
				System.out.print("\t"+rs.getString(4));
				statement_details.append("\t"+rs.getString(4));
				System.out.print("\t"+rs.getString(5));
				statement_details.append("\t"+rs.getString(5));
				statement_details.append("\n");
				System.out.println();
			}
			
			getMiniStatement(statement_details, accno);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	public void getMiniStatement(StringBuffer details,int accountno)
	{
		String s=details.toString();
		FileOutputStream fos=null;
		try {
			 fos=new FileOutputStream("E:/JavaByDeepakSir/BankApp/src/main/resources/doc.txt");
			 fos.write(s.getBytes());
			 System.out.println("File created successfully");
		} catch (FileNotFoundException  e) {
			
			e.printStackTrace();
		}
		catch(IOException ee)
		{
			ee.printStackTrace();
		}
		finally {
			try {
				fos.close();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
		}
		
	}
	

}
