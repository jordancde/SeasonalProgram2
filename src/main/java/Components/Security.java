package Components;

import Components.Exceptions.SymbolInvalidException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class Security implements Serializable {
    private static final long serialVersionUID = 2L;
    boolean selected;
    Series opens;
    Series highs;
    Series closes;
    Series lows;
    Series volumes;
    private transient StringProperty symbol;

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeUTF((String)this.symbol.get());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.symbol = new SimpleStringProperty(s.readUTF());
    }

    public Security(String symbol) {
        this.symbol = new SimpleStringProperty(symbol);
        this.selected = false;
    }

    public Security() {
        this.symbol = new SimpleStringProperty("None");
        this.selected = false;
    }

    public void refresh(Calendar from, Calendar to) throws SymbolInvalidException {
        from = (Calendar)from.clone();
        to = (Calendar)to.clone();
        if (this.closes == null || this.closes.getValues().size() == 0 || ((Calendar)this.closes.getDates().get(0)).compareTo(from) > 0 || ((Calendar)this.closes.getDates().get(this.closes.getDates().size() - 1)).compareTo(to) < 0) {
            from.add(5, -10);
            to.add(5, 10);
            Stock s = null;
            new ArrayList();

            List history;
            try {
                s = YahooFinance.get((String)this.symbol.get());
                history = s.getHistory(from, to, Interval.DAILY);
            } catch (IOException var13) {
                System.out.println("error retrieving: " + (String)this.symbol.get());
                throw new SymbolInvalidException((String)this.symbol.get());
            }

            List<Calendar> dates = new ArrayList();
            List<Double> opens = new ArrayList();
            List<Double> highs = new ArrayList();
            List<Double> lows = new ArrayList();
            List<Double> closes = new ArrayList();
            List<Double> volumes = new ArrayList();
            Iterator var11 = history.iterator();

            while(var11.hasNext()) {
                HistoricalQuote h = (HistoricalQuote)var11.next();
                dates.add(h.getDate());
                opens.add(h.getOpen().doubleValue());
                highs.add(h.getHigh().doubleValue());
                lows.add(h.getLow().doubleValue());
                closes.add(h.getClose().doubleValue());
                volumes.add(h.getVolume().doubleValue());
            }

            this.closes = new Series(s.getSymbol() + " Close", dates, closes);
            this.opens = new Series(s.getSymbol() + " Open", dates, opens);
            this.highs = new Series(s.getSymbol() + " High", dates, highs);
            this.lows = new Series(s.getSymbol() + " Low", dates, lows);
            this.volumes = new Series(s.getSymbol() + " Volume", dates, volumes);
        }

    }

    public String getSymbol() {
        return (String)this.symbol.get();
    }

    public Series getOpens() {
        return this.opens;
    }

    public Series getHighs() {
        return this.highs;
    }

    public Series getLows() {
        return this.lows;
    }

    public Series getCloses() {
        return this.closes;
    }

    public Series getVolumes() {
        return this.volumes;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean setSelected(boolean selected) {
        this.selected = selected;
        return this.selected;
    }

    public final void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    public StringProperty symbolProperty() {
        return this.symbol;
    }

    public Calendar getDataStart() {
        return (Calendar)this.getCloses().dates.get(0);
    }

    public Calendar getDataEnd() {
        return (Calendar)this.getCloses().dates.get(this.getCloses().dates.size() - 1);
    }
}
