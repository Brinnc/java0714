package org.sp.app0714.table2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MySQLMain extends JFrame{
	JTable table;
	JScrollPane scroll;
	
	String[][] data= {};
	String[] column= {"member_idx", "id", "name", "phone"};
	
	String url="jdbc:mysql://localhost:3306/javase?characterEncoding=utf8";
	String user="root";
	String pass="1234";
	
	Connection con;
	
	public MySQLMain() {
		table=new JTable(data, column);
		scroll=new JScrollPane(table);
		
		add(scroll);
		setSize(600, 400);
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				
				if(con!=null) {
					try {
						con.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			
				//프로세스 죽이기
				System.exit(0);
			}
		});
		
		connect();
		getList();
	}
	
	public void connect() {
		//1) 드라이버 로드
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			//2) MySQL 접속 시도
			con=DriverManager.getConnection(url, user, pass);
			if(con==null) {
				System.out.println("접속 실패");
			}else {
				System.out.println("접속 성공");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getList() {
		java.sql.PreparedStatement pstmt=null;
		ResultSet rs=null; //표를 표현한 객체
		
		try {
			pstmt=con.prepareStatement("select * from member");
			rs=pstmt.executeQuery();
			
			rs.last();
			int row=rs.getRow();
			System.out.println(row);
			
			//rs를 이차원 배열로 변환한 후 JTable에 적용하자
			//이차원 배열 준비해 놓기
			String[][] record=new String[row][4];
			
			rs.beforeFirst(); //제일 마지막으로 보넀던 커서를 원상복귀 시킴
			rs.next();
			
			int member_idx=rs.getInt("member_idx"); // [][0]
			String id=rs.getString("id");  // [][1]
			String name=rs.getString("name");  // [][2]
			String phone=rs.getString("phone");  // [][3]
			
			//하나의 레코드를 이차원배열의 한 일차원 배열로 옮기자
			record[0][0]=Integer.toString(member_idx);
			record[0][1]=id;
			record[0][2]=name;
			record[0][3]=phone;
			
			table=new JTable(record, column);
			//this.updateUI();
			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new MySQLMain();
	}
}
