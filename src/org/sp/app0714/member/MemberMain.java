package org.sp.app0714.member;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class MemberMain extends JFrame{
	//서쪽 영역 구성 컴포넌트
	JPanel p_west;
	JTextField t_id;
	JTextField t_name;
	JTextField t_phone;
	JButton bt_regist;
	//센터 영역 구성 컴포넌트
	JPanel p_center;
	JButton bt_select;
	JTextArea area;
	JScrollPane scroll;
	
	//오라클DB 접속정보 
	String url="jdbc:oracle:thin:@localhost:1521:XE";
	String user="java";
	String pass="1234";
	
	Connection con=null;
	
	public MemberMain() {
		p_west=new JPanel();
		t_id=new JTextField();
		t_name=new JTextField();
		t_phone=new JTextField();
		bt_regist=new JButton("등록");
		
		p_center=new JPanel();
		bt_select=new JButton("조회");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		//스타일 지정
		//p_west.setBackground(Color.YELLOW);
		p_west.setPreferredSize(new Dimension(120, 500));
		//p_center.setBackground(Color.CYAN);
		p_center.setPreferredSize(new Dimension(480, 500));
		
		Dimension d=new Dimension(110, 40);
		t_id.setPreferredSize(d);
		t_name.setPreferredSize(d);
		t_phone.setPreferredSize(d);
		
		area.setPreferredSize(new Dimension(450, 420));
		
		//서쪽 영역 조립
		p_west.add(t_id);
		p_west.add(t_name);
		p_west.add(t_phone);
		p_west.add(bt_regist);
		//센터 영역 조립
		p_center.add(bt_select);
		p_center.add(scroll);
		
		//패널을 윈도우에 부착
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		setSize(600, 500);
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
				System.exit(0); //프로세스 죽이기
			}
		});
		
		//조회 버튼과 리스너 연결
		bt_select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getList();
			}
		});
		
		bt_regist.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				regist();
				
			}
		});
		
		connect(); //오라클 접속
	}
	
	public void connect() {
		//1) 드라이버로드
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("로드 성공");
			
			//2) 접속
			//Connection con=null; -> 멤버변수로 이동!
			con=DriverManager.getConnection(url, user, pass);
			
			if(con==null) {
				System.out.println("접속 실패");
			}else {
				System.out.println("접속 성공");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("로드 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//!! DB접속시도 Connection객체, DriverManager.getConnection("접속주소", "ID", "PW");
		
	}
	
	//오라클DB에서 회원(테이블) 조회 , ResultSet
	public void getList() {
		//java.sql.PreparedStatement
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			String sql="select * from memberjava";
			pstmt=con.prepareStatement(sql);
			
			//쿼리 실행 (DDL, DML, DCL, select문)
			//select문의 경우, 아래의 executeQuery() 메서드를 이용해야함
			//표를 표현하는 ResultSet을 반환받음
			//개발 시 ResultSet은 DB의 표 자체를 표현한다고 염두해두자
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				//커서란? 레코드를 가리키는 포인터
				//boolean result=rs.next(); //커서 1칸 전진
				//System.out.println(result);
				int idx=rs.getInt("member_idx");
				String id=rs.getString("id");
				String name=rs.getString("name");
				String phone=rs.getString("phone");
				
				//기존 데이터 지우고 출력하기
				area.setText(" "); //마지막에 삽입한 레코드만 출력해줌
				area.append(idx+", "+id+", "+name+", "+phone+"\n");
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
			
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	//회원 등록
	public void regist() {
		PreparedStatement pstmt=null;
		
		String id=t_id.getText();
		String name=t_name.getText();
		String phone=t_phone.getText();
		
		try {
			String sql="insert into memberjava(member_idx, id, name, phone)";
			sql+=" values(seq_memberjava.nextval, '"+id+"', '"+name+"', '"+phone+"')";
			pstmt=con.prepareStatement(sql);
			
			int result=pstmt.executeUpdate();
			
			if(result>0) {
				//조회 시도
				System.out.println("등록 성공");
				getList();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new MemberMain();
	}
}
