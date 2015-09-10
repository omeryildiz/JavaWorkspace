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

	@Override
	public List<Ogrenci> getAll() throws Exception {
		List<Ogrenci> result = new ArrayList<Ogrenci>();
		Connection con = getConnection();

		PreparedStatement stmt = (PreparedStatement) con.prepareStatement("select * from ogrenci");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Ogrenci ogrenci = new Ogrenci();
			ogrenci.setId(rs.getInt("ID"));
			ogrenci.setName(rs.getString("AD"));
			ogrenci.setKayitTarihi(rs.getDate("KAYIT_TARIHI"));
			result.add(ogrenci);

		}
		rs.close();
		stmt.close();
		con.close();
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
