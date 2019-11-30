package Components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Table {
    ArrayList<ArrayList<String>> table = new ArrayList();
    int numColumns = 0;
    int numRows = 0;

    public Table() {
    }

    public void squareTable() {
        while(this.table.size() < this.numRows) {
            ArrayList<String> row = new ArrayList();
            this.table.add(row);
            ++this.numRows;
        }

        Iterator var3 = this.table.iterator();

        while(var3.hasNext()) {
            List row = (List)var3.next();

            while(row.size() < this.numColumns) {
                row.add("");
            }
        }

    }

    public <T> void addColumn(List<T> column) {
        while(this.numRows < column.size()) {
            this.addRow();
        }

        for(int i = 0; i < column.size(); ++i) {
            ((ArrayList)this.table.get(i)).add(column.get(i) == null ? "" : column.get(i).toString());
        }

        ++this.numColumns;
        this.squareTable();
    }

    public <T> void addRow(List<T> row) {
        ArrayList newRow;
        Iterator var3;
        Object e;
        if (this.numRows == 0 && this.numColumns == 0) {
            newRow = new ArrayList();
            var3 = row.iterator();

            while(var3.hasNext()) {
                e = var3.next();
                newRow.add(e == null ? "" : e.toString());
            }

            this.table.add(newRow);
            this.numRows = 1;
            this.numColumns = row.size();
        } else {
            while(this.numColumns < row.size()) {
                this.addColumn();
            }

            newRow = new ArrayList();
            var3 = row.iterator();

            while(var3.hasNext()) {
                e = var3.next();
                newRow.add(e == null ? "" : e.toString());
            }

            this.table.add(newRow);
            ++this.numRows;
            this.squareTable();
        }
    }

    public void addColumn() {
        for(int i = 0; i < this.numRows; ++i) {
            ((ArrayList)this.table.get(i)).add("");
        }

        ++this.numColumns;
    }

    public void addRow() {
        ArrayList<String> newRow = new ArrayList();

        for(int i = 0; i < this.numColumns; ++i) {
            newRow.add("");
        }

        this.table.add(newRow);
        ++this.numRows;
    }

    public void addTableRight(Table other, int offset) {
        if (this.numRows == 0 && this.numColumns == 0) {
            this.table = other.table;
            this.numRows = other.numRows;
            this.numColumns = other.numColumns;
        } else {
            while(this.numRows < other.numRows + offset) {
                this.addRow();
            }

            for(int row = 0; row < other.numRows; ++row) {
                ((ArrayList)this.table.get(row + offset)).addAll((Collection)other.table.get(row));
            }

            this.numColumns += other.numColumns;
        }
    }

    public void addTableBelow(Table other) {
        if (this.numRows == 0 && this.numColumns == 0) {
            this.table = other.table;
            this.numRows = other.numRows;
            this.numColumns = other.numColumns;
        } else {
            while(this.numColumns < other.numColumns) {
                this.addColumn();
            }

            Iterator var2 = other.table.iterator();

            while(var2.hasNext()) {
                ArrayList<String> row = (ArrayList)var2.next();
                this.addRow(row);
            }

        }
    }

    public Double round(Double d) {
        return d == null ? 0.0D : (double)Math.round(d * 100000.0D) / 100000.0D;
    }

    public String toString() {
        String output = "";

        ArrayList row;
        for(Iterator var2 = this.table.iterator(); var2.hasNext(); output = output + String.join(",", row) + "\n") {
            row = (ArrayList)var2.next();
        }

        return output;
    }
}
