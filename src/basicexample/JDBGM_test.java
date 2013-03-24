package basicexample;

/******************************************************************************
 * Ejemplo básico de uso de JDBGM. Depende de JDBC y el driver correspondiente.
 ******************************************************************************/
import java.sql.ResultSet;
import java.sql.Types;

import com.crossdb.sql.Column;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.nelsonx.jdbgm.GenericManager;
import com.nelsonx.jdbgm.JDException;
import com.nelsonx.jdbgm.ManagerFactory;

public class JDBGM_test {
	//El método de prueba lanza la excepción propia de JDBGM para simplificar el código. 
	public void test() throws JDException{
		//Se instancia la clase que maneja el acceso a la BD mediante una fabrica.
		//se le debe indicar tipo de motor, URI hacia el motor y user/pass.
		GenericManager manager = ManagerFactory.getManager(ManagerFactory.MYSQL_DB, "user", "localhost/test", "password");
		// Se obtiene una fabrica (instanciador) de sentencias.
		SQLFactory sentencesFactory = ManagerFactory.getSQLFactory();
		
		// Las sentencias solo se pueden obtener desde la fabrica, la fabrica se
		//encarga de elegir la instancia que se debe crear (type and vendor).
		CreateTableQuery create = sentencesFactory.getCreateTableQuery();
		create.setName("example_table");
		//Column es una clase propia que representa una columna de una tabla.
		Column col1 = new Column("columna_pk", Types.INTEGER);
		col1.setNullable(0);
		create.addPrimaryKeyColumn(col1);
		Column col2 = new Column("columna_clave_foranea", Types.INTEGER, "tabla_foranea", "pk_foranea");
		create.addForeignKeyColumn(col2);
		create.addUniqueColumn(new Column("columna_unique", Types.DATE));
		/* Esta es la sentencia resultante de create.
		 * CREATE TABLE example_table ( columna_pk INTEGER NOT NULL, columna_clave_foranea INTEGER, columna_unique DATE, 
		 * PRIMARY KEY (columna_pk), 
		 * FOREIGN KEY (columna_clave_foranea) REFERENCES tabla_foranea(pk_foranea), 
		 * UNIQUE (columna_unique) ) 
		 */
		manager.update(create); //se envía la sentencia a el motor mediante manager.
		
		InsertQuery insert = sentencesFactory.getInsertQuery();
		insert.setTable("example_table");
		insert.addColumn("columna_pk", 1);
		insert.addColumn("columna_clave_foranea", 3);
		/* insert equivale a:
		 * INSERT INTO example_table (columna_pk, columna_clave_foranea) 
		 * VALUES (1, 3)
		 */
		manager.update(insert);//Se la envía a el motor.
		
		SelectQuery select = sentencesFactory.getSelectQuery();
		select.addTable("example_table");
		select.addColumn("column_pk");
		select.addJoin().innerJoin("anoter_table", "example_table.id = anoter_table.id");
		select.addOrderBy("column_pk");
		/* select equivale a:
		 * SELECT column_pk FROM example_table 
		 * INNER JOIN anoter_table ON example_table.id = anoter_table.id 
		 * ORDER BY column_pk
		 */
		ResultSet result = manager.query(select); 
		//al enviarle un SELECT se usa otro método de manager, el resultado se almacena en un ResultSet.
		//ResultSet es parte de JDBC pero es independiente de el motor.
		
		manager.beginTransaction();
			for (int i = 0; i < 10; i++) {
				//Se pueden realizar muchas operaciones que serán tratadas como una
				//única transacción dentro de un begin/endTransaction
				manager.update(insert);
			}
		manager.endTransaction(); //en este momento recién se cierra la transacción
		
		manager.endConnection(); //cierro la conexión, libero recursos.
	}
	
	public static void main(String[] args) throws JDException {
		JDBGM_test test = new JDBGM_test();
		test.test();
	}

}
