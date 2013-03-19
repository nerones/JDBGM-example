/**
 * mc_luis_007@hotmail.com
 */


import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.sun.rowset.CachedRowSetImpl;

public class DBTable extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2761237542570900840L;
	private CachedRowSet cachedRowSet;
    private boolean autocomit;

    private DBTable() {
    }

    public DBTable(CachedRowSet cachedRowSet) {
        this.cachedRowSet = cachedRowSet;
    }

    public int getRowCount() {
        return cachedRowSet.size();
    }

    public boolean isAutocomit() {
        return autocomit;
    }

    public CachedRowSet getCachedRowSet() {
        return cachedRowSet;
    }

    public void setCachedRowSet(CachedRowSet cachedRowSet) {
        this.cachedRowSet = cachedRowSet;
        fireTableStructureChanged();
    }

    public void setAutocomit(boolean autocomit) {
        this.autocomit = autocomit;
    }

    public int getColumnCount() {
        int columncount = 0;
        try {
            columncount = cachedRowSet.getMetaData().getColumnCount();
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
        return columncount;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        try {
            cachedRowSet.absolute(rowIndex + 1);
            value = cachedRowSet.getObject(columnIndex + 1);
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
        return value;
    }

    @Override
    public String getColumnName(int column) {
        String columname = "";
        try {
            columname = cachedRowSet.getMetaData().getColumnLabel(column + 1);
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
        return columname;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            cachedRowSet.absolute(rowIndex + 1);
            cachedRowSet.updateObject(columnIndex + 1, aValue);
            cachedRowSet.updateRow();
            if (isAutocomit()) {
                acceptchange();
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        @SuppressWarnings("rawtypes")
		Class myclass = Object.class;
        try {
            myclass = Class.forName(cachedRowSet.getMetaData().getColumnClassName(columnIndex + 1));
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        } catch (ClassNotFoundException exception) {
            ErrorHandler.spropagete(exception);
        }

        return myclass;
    }

    public void acceptchange() {
        try {
            cachedRowSet.acceptChanges();
        } catch (SyncProviderException exception) {
            ErrorHandler.spropagete(exception);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        int val[];
        boolean vv = true;
        try {
            val = cachedRowSet.getKeyColumns();
            for (int i : val) {
                if (i == columnIndex + 1) {
                    vv = false;
                    break;
                }
            }
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
        return vv;
    }

    public void setKeys(int... keys) {
        try {
            cachedRowSet.setKeyColumns(keys);
            for (int i : keys) {
                fireTableChanged(new TableModelEvent(this, 0, getRowCount(), i));
            }

        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
    }

    public void close() {
        try {
            cachedRowSet.close();
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
    }

    public void refresh() {
        try {
            CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
            cachedRowSetImpl.setUrl(cachedRowSet.getUrl());
            cachedRowSetImpl.setUsername(cachedRowSet.getUsername());
            cachedRowSetImpl.setPassword(cachedRowSet.getPassword());
            cachedRowSetImpl.setCommand(cachedRowSet.getCommand());
            cachedRowSetImpl.setKeyColumns(cachedRowSet.getKeyColumns());
            cachedRowSetImpl.execute();
            cachedRowSet = cachedRowSetImpl;
            fireTableStructureChanged();
        } catch (SQLException exception) {
            ErrorHandler.spropagete(exception);
        }
    }
}


class ErrorHandler {

	public static void spropagete(Exception exception) {
		// TODO Auto-generated method stub
		exception.printStackTrace();
		
	}
	
}