package Components;

import Components.Exceptions.InvalidInputException;
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
    boolean usingAdjustedCloses;
    private transient StringProperty symbol;

    // yeah this is kind of sketchy, only simple way I could think to do this at this point
    // modified by controller when the checkbox is selected/unselected
    public static transient boolean USE_ADJUSTED_CLOSE = false;

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

    void refreshFromYahoo(Calendar from, Calendar to) throws SymbolInvalidException {
        // Case where we have to check yahoo finance
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
            throw new SymbolInvalidException(this.symbol.get());
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
            volumes.add(h.getVolume().doubleValue());

            if(USE_ADJUSTED_CLOSE){
                closes.add(h.getAdjClose().doubleValue());
            }else{
                closes.add(h.getClose().doubleValue());
            }
            usingAdjustedCloses = USE_ADJUSTED_CLOSE;
        }

        this.closes = new Series(s.getSymbol() + " Close", dates, closes);
        this.opens = new Series(s.getSymbol() + " Open", dates, opens);
        this.highs = new Series(s.getSymbol() + " High", dates, highs);
        this.lows = new Series(s.getSymbol() + " Low", dates, lows);
        this.volumes = new Series(s.getSymbol() + " Volume", dates, volumes);
    }

    void refreshFromInput() throws InvalidInputException {
        ManualInputParser manualInputParser = ManualInputParser.getInstance();

        List<Calendar> dates = manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.DATE);

        ManualInputParser.Type closesType = ManualInputParser.Type.CLOSE;
        if(USE_ADJUSTED_CLOSE) closesType = ManualInputParser.Type.ADJUSTED_CLOSE;

        this.closes = new Series(this.getSymbol() + " Close", dates, manualInputParser.getSeries(this.getSymbol(), closesType));
        this.opens = new Series(this.getSymbol() + " Open", dates, manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.OPEN));
        this.highs = new Series(this.getSymbol() + " High", dates, manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.HIGH));
        this.lows = new Series(this.getSymbol() + " Low", dates, manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.LOW));
        this.volumes = new Series(this.getSymbol() + " Volume", dates, manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.VOLUME));
    }

    // like refresh but without the range checks, used in yearly table
    public void forceRefresh(Calendar from, Calendar to) throws InvalidInputException, SymbolInvalidException {
        from = (Calendar)from.clone();
        to = (Calendar)to.clone();

        ManualInputParser manualInputParser = ManualInputParser.getInstance();

        boolean useInputFile = false;

        if(manualInputParser.hasSymbol(this.getSymbol())) {
            refreshFromInput();
        }else{
            refreshFromYahoo(from, to);
        }
    }

    // refresh with range checks
    public void refresh(Calendar from, Calendar to) throws SymbolInvalidException, InvalidInputException {
        from = (Calendar)from.clone();
        to = (Calendar)to.clone();

        ManualInputParser manualInputParser = ManualInputParser.getInstance();

        boolean useInputFile = false;

        if(manualInputParser.hasSymbol(this.getSymbol())) {
            List<Calendar> dates = manualInputParser.getSeries(this.getSymbol(), ManualInputParser.Type.DATE);
            if (from.after(dates.get(0)) && to.before(dates.get(dates.size() - 1))){
                useInputFile = true;
            }else{
                System.out.println("Data range for symbol " + getSymbol() + " in manual input file does not cover dates, using Yahoo");
            }
        }

        if(useInputFile){
            refreshFromInput();
        }else{
            // cache checks for yahoo refresh
            if (USE_ADJUSTED_CLOSE != this.usingAdjustedCloses ||
                    this.closes == null || this.closes.getValues().size() == 0 ||
                    // case where symbol of security has changed
                    !this.closes.getName().contains(getSymbol()) ||
                    ((Calendar)this.closes.getDates().get(0)).compareTo(from) > 0 ||
                    ((Calendar)this.closes.getDates().get(this.closes.getDates().size() - 1)).compareTo(to) < 0) {

                refreshFromYahoo(from, to);
            }
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
