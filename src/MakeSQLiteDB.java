

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import com.crossdb.sql.Column;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.nelsonx.jdbgm.GenericManager;
import com.nelsonx.jdbgm.JDException;
import com.nelsonx.jdbgm.ManagerFactory;


public class MakeSQLiteDB {
	GenericManager manager;
	String user,location,password;
	Random ranGene;
	SQLFactory sentencesFactory;
	
	public MakeSQLiteDB(String location, String user, String password) throws JDException {
		// TODO Auto-generated constructor stub
		this.user = user;
		this.password = password;
		this.location = location;
		manager = ManagerFactory.getManager(ManagerFactory.MYSQL_DB, user, location, password);//new SQLiteManager(location, user, password);
		sentencesFactory = ManagerFactory.getSQLFactory();
		ranGene = new Random();
	}
	
	public void makeDB() throws JDException{
		try {
			//Creo la estructura de la base de datos
			/*String sql = "CREATE TABLE if not exists alumnos (idAlumno INTEGER PRIMARY KEY AUTOINCREMENT ,"
						+ "dni int,fechaNacimiento date, email varchar(40), direccion varchar(40),"
						+ " nombre VARCHAR(40), apellido VARCHAR(40), telefono VARCHAR(20))";*/
			CreateTableQuery create = sentencesFactory.getCreateTableQuery();
			create.setName("alumnos");
			create.addAutoincrementPrimaryKeyColumn(new Column("idAlumno", Types.INTEGER));
			create.addColumn(new Column("dni", Types.INTEGER));
			create.addColumn(new Column("fechaNacimiento", Types.TIMESTAMP));
			create.addColumn(new Column("email", Types.VARCHAR, 40));
			create.addColumn(new Column("direccion", Types.VARCHAR, 40));
			create.addColumn(new Column("nombre", Types.VARCHAR, 40));
			create.addColumn(new Column("apellido", Types.VARCHAR, 46));
			create.addColumn(new Column("telefono", Types.VARCHAR, 41));
			manager.update(create);
			
			/*sql = "create table if not exists materias (idMateria INTEGER PRIMARY KEY  AUTOINCREMENT,"
				+ "nombre varchar(20))";*/
			CreateTableQuery tabla_materias = sentencesFactory.getCreateTableQuery();
			tabla_materias.setName("materias");
			tabla_materias.addAutoincrementPrimaryKeyColumn(new Column("idMateria", Types.INTEGER));
			tabla_materias.addColumn(new Column("nombre", Types.VARCHAR, 20));
			manager.update(tabla_materias);
			
			/*sql = "create table if not exists aniolectivo (idAA INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "idAlumno int, idgrado int, anio int, foreign key(idAlumno) references alumnos(idAlumno))";*/
			CreateTableQuery aniolectivo = sentencesFactory.getCreateTableQuery();
			aniolectivo.setName("aniolectivo");
			aniolectivo.addAutoincrementPrimaryKeyColumn(new Column("idAA", Types.INTEGER));
			aniolectivo.addForeignKeyColumn(new Column("idAlumno", Types.INTEGER, "alumnos","idAlumno"));
			aniolectivo.addColumn(new Column("idgrado", Types.INTEGER));
			aniolectivo.addColumn(new Column("anio", Types.INTEGER));
			manager.update(aniolectivo);
			
			/*sql = "create table if not exists materiasxanio (idMP INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "idAA int, idMateria int, "
				+ "foreign key(idMateria) references materias(idMateria), foreign key(idAA) references aniolectivo(idAA))";*/
			create = sentencesFactory.getCreateTableQuery();
			create.setName("materiasxanio");
			create.addAutoincrementPrimaryKeyColumn(new Column("idMP", Types.INTEGER));
			create.addForeignKeyColumn(new Column("idAA", Types.INTEGER, "aniolectivo", "idAA"));
			create.addForeignKeyColumn(new Column("idMateria", Types.INTEGER, "materias", "idMateria"));
			manager.update(create);
			
			/*sql = "create table if not exists asistencias (fecha date, asistencia int,"
				+ "idMP int, foreign key(idMP) references materiasxanio(idMP))";*/
			create = sentencesFactory.getCreateTableQuery();
			create.setName("asistencias");
			create.addColumn(new Column("fecha", Types.DATE));
			create.addColumn(new Column("asistencia", Types.INTEGER));
			create.addForeignKeyColumn(new Column("idMP", Types.INTEGER, "materiasxanio", "idMP"));
			manager.update(create);
			
			/*sql = "create table if not exists grados (idgrado INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "nombre varchar(20))";*/
			create = sentencesFactory.getCreateTableQuery();
			create.setName("grados");
			create.addAutoincrementPrimaryKeyColumn(new Column("idGrado", Types.INTEGER));
			create.addColumn(new Column("nombre", Types.VARCHAR, 20));
			manager.update(create);
			
			
			
			manager.beginTransaction();
			//Empiezo a leer los datos desde un archivo y prepararlos para las tablas de la base de datos
			//empezando por alumnos
			Vector<String> calles = Utils.openFile("DOC/CallesFixed.txt");
			Vector<String> nombres = Utils.openFile("DOC/NombreF.txt");
			Vector<String> apellidos = Utils.openFile("DOC/ApellidoF.txt");
			Vector<String> mails = Utils.openFile("DOC/mails.txt");
			int dni = 0;
			GregorianCalendar dates = new GregorianCalendar();
			Date date;
			String tel = "";
			
			InsertQuery insert;
			for (int i = 0; i < nombres.size(); i++) {
				dni = 10000000 + ranGene.nextInt(10000000);
				tel = String.valueOf(4000000 + ranGene.nextInt(999999));
				int anio = 2000 + ranGene.nextInt(35);
				int mes = ranGene.nextInt(12);
				int dia = ranGene.nextInt(28);
				dates.set(anio, mes, dia);
				date = dates.getTime();
				//date = anio + "/" + mes + "/" + dia;
				/*sql = "INSERT INTO alumnos (dni,fechaNacimiento,email,direccion,nombre,apellido,telefono)"
						+ "VALUES ("
						+ dni
						+ ",'"
						+ date
						+ "','"
						+ mails.elementAt(i)
						+ "','"
						+ calles.elementAt(i)
						+ "','"
						+ nombres.elementAt(i)
						+ "','"
						+ apellidos.elementAt(i) + "','" + tel + "' )";*/
				insert = sentencesFactory.getInsertQuery();
				insert.setTable("alumnos");
				insert.addColumn("dni", dni);
				insert.addColumn("fechaNacimiento", date);
				insert.addColumn("email", mails.elementAt(i));
				insert.addColumn("direccion", calles.elementAt(i));
				insert.addColumn("nombre", nombres.elementAt(i));
				insert.addColumn("apellido", apellidos.elementAt(i));
				insert.addColumn("telefono", tel);

				manager.update(insert);

			}
			
			//datos para materias
			Vector<String> materias = Utils.openFile("DOC/Materias.txt");

			for (int i = 0; i < materias.size(); i++) {
				insert = sentencesFactory.getInsertQuery();
				insert.setTable("materias");
				insert.addColumn("nombre", materias.elementAt(i));
				/*sql = "insert into materias (nombre) values ('"
						+ materias.elementAt(i) + "')";*/
				manager.update(insert);
			}
			
			//datos para los grados
			String[] nom = { "4a", "4b", "5a", "5c" };
			for (int i = 0; i < 3; i++) {
				insert = sentencesFactory.getInsertQuery();
				insert.setTable("grados");
				insert.addColumn("nombre", nom[i]);
				/*sql ="insert into grados (nombre) values('"
						+ nom[i] + "')";*/
				manager.update(insert);
			}
			
			//datos para aniolectivo
			SelectQuery select = sentencesFactory.getSelectQuery();
			select.addColumn("idAlumno");
			select.addTable("alumnos");
			ResultSet rse = manager.query(select);
			Vector<Integer> idsalumnos = new Vector<Integer>();

			while (rse.next()) {
				idsalumnos.add((Integer) rse.getObject("idAlumno"));

			}
			rse.close();
			select = sentencesFactory.getSelectQuery();
			select.addColumn("idgrado");
			select.addTable("grados");
			rse = manager.query(select);
			Vector<Integer> idsgrados = new Vector<Integer>();

			while (rse.next()) {
				idsgrados.add((Integer) rse.getObject("idgrado"));

			}
			rse.close();
			for (int i = 0; i < idsalumnos.size(); i++) {
				int[] anios = {2010,2009,2008};
				int a = ranGene.nextInt(anios.length);
				int b = ranGene.nextInt(idsgrados.size());
				/*sql ="insert into aniolectivo(idAlumno,idgrado,anio) values ("
						+ idsalumnos.elementAt(i)
						+ ","
						+ idsgrados.elementAt(b) + "," + anios[a] + ")";*/
				insert = sentencesFactory.getInsertQuery();
				insert.setTable("aniolectivo");
				insert.addColumn("idAlumno", idsalumnos.elementAt(i));
				insert.addColumn("idgrado", idsgrados.elementAt(b));
				insert.addColumn("anio", anios[a]);
				manager.update(insert);
			}
			
			//datos para materiasxanio
			select = sentencesFactory.getSelectQuery();
			select.addColumn("idAA");
			select.addTable("aniolectivo");
			rse = manager.query(select);
			Vector<Integer> idAA = new Vector<Integer>();
			while (rse.next()) {
				idAA.add((Integer) rse.getObject("idAA"));
			}
			rse.close();
			select = sentencesFactory.getSelectQuery();
			select.addColumn("idMateria");
			select.addTable("materias");
			rse = manager.query(select);
			Vector<Integer> idMAteria = new Vector<Integer>();

			while (rse.next()) {
				idMAteria.add((Integer) rse.getObject("idMateria"));
			}
			rse.close();
			for (int i = 0; i < idAA.size(); i++) {
				int materia = ranGene.nextInt(idMAteria.size());
				/*sql = "insert into materiasxanio (idAA,idMateria) values ("
						+ idAA.elementAt(i)
						+ ","
						+ idMAteria.elementAt(materia) + ")";*/
				insert = sentencesFactory.getInsertQuery();
				insert.setTable("materiasxanio");
				insert.addColumn("idAA", idAA.elementAt(i));
				insert.addColumn("idMateria", idMAteria.elementAt(materia));
				manager.update(insert);
			}

			//datos para asistencias
			select = sentencesFactory.getSelectQuery();
			select.addColumn("idMP");
			select.addTable("materiasxanio");
			rse = manager.query(select);
			Vector<Integer> idMP = new Vector<Integer>();

			while (rse.next()) {
				idMP.add((Integer) rse.getObject("idMP"));
			}
			rse.close();
			for (int i = 0; i < 28; i++) {
				for (int j = 0; j < idAA.size(); j++) {
					String anio = "2010";// String.valueOf(2000 +
											// ranGene.nextInt(1000));
					String mes = "1";// String.valueOf(ranGene.nextInt(12));

					String dia = "" + (i + 1);// String.valueOf(ranGene.nextInt(28));
					dates.set(2010, 1, i + 1);
					date = dates.getTime();
					//date = anio + "/" + mes + "/" + dia;
					/*sql = "insert into asistencias (fecha,asistencia,idMP) values ('"
							+ date + "'," + 1 + "," + idMP.elementAt(j) + ")";*/
					insert = sentencesFactory.getInsertQuery();
					insert.setTable("asistencias");
					insert.addColumn("fecha", date);
					insert.addColumn("asistencia", 1);
					insert.addColumn("idMP", idMP.elementAt(j));
					manager.update(insert);
				}
			}
			manager.endTransaction();
			manager.endConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) throws JDException {
		MakeSQLiteDB mkdb = new MakeSQLiteDB("localhost/AsistenciaAlumnos", "tester", "tester");
		mkdb.makeDB();
		//mkdb.doit();
		
	}
}
