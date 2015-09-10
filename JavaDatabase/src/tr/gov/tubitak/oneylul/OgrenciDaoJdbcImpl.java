package tr.gov.tubitak.oneylul;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OgrenciDaoJdbcImpl implements OgrenciDao {

	static {
		// class.forName referans kütüphaneyi eklemiþ olmamýza raðmen driverý
		// constructer içinde yüklemeye ihtiyaç duyuyoruz
		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insert(int id, String ad) throws Exception {
		Connection con = getConnection();

		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("insert into ogrenci values(?,?,?)");
		stmt.setInt(1, id);
		stmt.setString(2, ad);
		stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
		stmt.executeUpdate();
		// statement.executeUpdate("INSERT INTO `testdb`.`ogrenci` (`ID`, `AD`,
		// `KAYIT_TARIHI`) VALUES ('" + id + "', '"
		// + ad + "', '2000-10-23')");

		stmt.close();
		con.close();
		System.out.println("insert iþlemi baþarýlý");

	}

	// transaction örneði
	public void insertWithCommit(int id, String ad) throws Exception {
		Connection con = getConnection();
		con.setAutoCommit(false);
		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("insert into ogrenci values(?,?,?)");
		stmt.setInt(1, id);
		stmt.setString(2, ad);
		stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
		try {
			stmt.executeUpdate();
			con.commit();
			System.out.println("insert iþlemi baþarýlý");
		} catch (Exception e) {
			con.rollback();
			System.out.println("Hata oluþtu mesaj: " + e.getMessage());
		}

		// statement.executeUpdate("INSERT INTO `testdb`.`ogrenci` (`ID`, `AD`,
		// `KAYIT_TARIHI`) VALUES ('" + id + "', '"
		// + ad + "', '2000-10-23')");

		stmt.close();
		con.close();

	}

	@Override
	public void update(int id, String ad) throws Exception {
		Connection con = getConnection();

		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("update ogrenci set AD=? where ID=?");
		stmt.setString(1, ad);
		stmt.setInt(2, id);
		stmt.executeUpdate();
		// statement.executeUpdate("INSERT INTO `testdb`.`ogrenci` (`ID`, `AD`,
		// `KAYIT_TARIHI`) VALUES ('" + id + "', '"
		// + ad + "', '2000-10-23')");

		stmt.close();
		con.close();

	}

	@Override
	public void delete(int id) throws Exception {
		Connection con = getConnection();

		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("delete from ogrenci where ID = ?");
		stmt.setInt(1, id);
		stmt.executeUpdate();
		// statement.executeUpdate("INSERT INTO `testdb`.`ogrenci` (`ID`, `AD`,
		// `KAYIT_TARIHI`) VALUES ('" + id + "', '"
		// + ad + "', '2000-10-23')");

		stmt.close();
		con.close();

	}
	
	static void closeDbItems(ResultSet rs , PreparedStatement stmt, Connection con) {
		try {
			rs.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
		try {
			stmt.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
		try {
			con.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	static void closableDbItems(AutoCloseable... cloesables) {
		for(AutoCloseable closeable : cloesables)
		{
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public List<Ogrenci> getAll() {
		List<Ogrenci> result = new ArrayList<Ogrenci>();
		Connection con = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = (PreparedStatement) con.prepareStatement("select * from ogrenci");
			rs = stmt.executeQuery();
			while (rs.next()) {
				Ogrenci ogrenci = new Ogrenci();
				ogrenci.setId(rs.getInt("ID"));
				ogrenci.setName(rs.getString("AD"));
				ogrenci.setKayitTarihi(rs.getDate("KAYIT_TARIHI"));
				result.add(ogrenci);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		//aþaðýdaki iki türlü de kapatma iþlemini metod içine taþýyabiliriz
		//closableDbItems(rs,stmt,con);
		//closeDbItems(rs, stmt, con);

		return result;
	}

	@Override
	public Ogrenci find(int id) throws Exception {

		Ogrenci result = new Ogrenci();
		Connection con = getConnection();

		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("select * from ogrenci where ID=?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {

			result.setId(rs.getInt("ID"));
			result.setName(rs.getString("AD"));
			result.setKayitTarihi(rs.getDate("KAYIT_TARIHI"));

		}
		rs.close();
		stmt.close();
		con.close();
		return result;
	}

	static Connection getConnection() {
		// farklý port için stringin sonuna jdbc:mysql://localhost/testdb:3306
		// ekliyoruz
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/testdb", "root", "omeromer");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

}
