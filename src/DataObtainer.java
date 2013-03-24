

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.nelsonx.jdbgm.GenericManager;
import com.nelsonx.jdbgm.JDException;
import com.nelsonx.jdbgm.ManagerFactory;
import com.nelsonx.sqlite.SQLiteFormatter;
import com.nelsonx.sqlite.SQLiteSelectQuery;

/**
 * @author Nelson Efrain A. Cruz
 * 
 */
public class DataObtainer {
	String location, user, password;
	GenericManager manager;
	SQLFactory sentences;

	public DataObtainer(String user, String location, String password) throws JDException {
		this.location = location;
		this.user = user;
		this.password = password;
		manager = ManagerFactory.getManager(ManagerFactory.SQLITE_DB,user,location , password);
		sentences = ManagerFactory.getSQLFactory();

	}

	public ResultSet getAllStudentList() throws JDException {
		//String sql = "select * from alumnos";
		SelectQuery select = sentences.getSelectQuery();
		select.addTable("alumnos");
		return manager.query(select);
	}

	public ResultSet getListByYear(String date) throws JDException {
		/*
		 * Usar Date?
		 */
		/*String sql = "SELECT a.idAlumno, a.nombre,a.apellido, al.anio " +
				"FROM alumnos a inner join aniolectivo al on a.idAlumno = al.idAlumno " +
				"where al.anio='" + date + "'";*/
		SelectQuery select = sentences.getSelectQuery();
		select.addColumn("a", "idAlumno");
		select.addColumn("a", "nombre");
		select.addColumn("a", "apellido");
		select.addColumn("al", "anio");
		select.addTable("alumnos a");
		select.addJoin().innerJoin("aniolectivo al", "a.idAlumno = al.idAlumno");
		select.addWhere().andEquals("al.anio", date);
		return manager.query(select);
	}
	
	 public ResultSet getListByGrade(int idGrado) throws JDException{
		/*String sql = "SELECT a.idAlumno, a.nombre,a.apellido, al.anio " +
				"FROM alumno a inner join aniolectivo al on a.idAlumno = al.idAlumno " +
				"where al.idgrado="+idGrado;*/
		SelectQuery select = sentences.getSelectQuery();
		select.addColumn("a", "idAlumno");
		select.addColumn("a", "nombre");
		select.addColumn("a", "apellido");
		select.addColumn("al", "anio");
		select.addTable("alumnos a");
		select.addJoin().innerJoin("aniolectivo al", "a.idAlumno = al.idAlumno");
		select.addWhere().andEquals("al.idgrado", idGrado);
		return manager.query(select);
		 
	 }
	 
	 public ResultSet getStudent(int idAlumno) throws JDException{
		 /*String sql = "select * from alumnos " +
		 		"where alumno.idAlumno ="+idAlumno;*/
		 SelectQuery select = sentences.getSelectQuery();
		 select.addTable("alumnos");
		 select.addWhere().andEquals("idAlumno", idAlumno);
		 return manager.query(select);
	 }
	 
	 public String[] listYears() throws JDException{
		//String sql = "select DISTINCT anio from aniolectivo";
		SelectQuery select = sentences.getSelectQuery();
		select.setDistinct(true);
		select.addColumn("anio");
		select.addTable("aniolectivo");
		ResultSet rs = manager.query(select);
		Vector<String> data = new Vector<String>(1);
		try {
			while (rs.next()) {
				data.add((rs.getObject("anio")).toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] dat = new String[data.size()];
		data.toArray(dat);
		return dat;
	 }
	 
	 public Grade[] listGradesByYear(String year) throws JDException{
		/*String sql = "select DISTINCT g.nombre, g.idgrado " +
		 		"from aniolectivo a inner join grados g on a.idgrado = g.idgrado " +
		 		"where a.anio='"+year+"'";*/
		SelectQuery select = sentences.getSelectQuery();
		select.setDistinct(true);
		select.addColumn("g", "nombre");
		select.addColumn("g", "idgrado");
		select.addTable("aniolectivo a");
		select.addJoin().innerJoin("grados g", "a.idgrado = g.idgrado");
		select.addWhere().andEquals("a.anio", year);
		ResultSet rs = manager.query(select);
		Vector<Grade> data = new Vector<Grade>(1);
		try {
			while (rs.next()) {
				String year1 = (String) rs.getObject("nombre");
				int idGrade = (Integer) rs.getObject("idgrado");
				data.add(new Grade(year1, idGrade) );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Grade[] dat = new Grade[data.size()];
		data.toArray(dat);
		return dat;
	 }
	 
	 public ResultSet listStudenbyYearGrade(String year, int idgrade) throws JDException{
		String sql = "select a.nombre, a.apellido, a.dni, a.email " +
				"from aniolectivo al inner join alumnos a on a.idAlumno = al.idAlumno " +
		 		"where al.anio='"+year+"' and al.idgrado="+idgrade;
		return manager.query(sql); 
		
	 }

}
