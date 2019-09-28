package Components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;
    SimpleDateFormat sdf;
    String name;
    Calendar monthlyStatsStart;
    Calendar monthlyStatsEnd;
    Calendar windowStart;
    Calendar windowEnd;
    Security benchmark;
    String outputDirectory;
    public TechnicalsManager technicalsManager;
    public transient ObservableList<Trade> trades;
    public transient ObservableList<Security> securities;
    public transient ObservableList<String> selectedSecurityNames;

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(new ArrayList(this.trades));
        s.writeObject(new ArrayList(this.securities));
        s.writeObject(new ArrayList(this.selectedSecurityNames));
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        if (this.trades == null) {
            this.trades = FXCollections.observableArrayList(new ArrayList());
        } else {
            this.trades.clear();
        }

        if (this.securities == null) {
            this.securities = FXCollections.observableArrayList(new ArrayList());
        } else {
            this.securities.clear();
        }

        if (this.selectedSecurityNames == null) {
            this.selectedSecurityNames = FXCollections.observableArrayList(new ArrayList());
        } else {
            this.selectedSecurityNames.clear();
        }

        s.defaultReadObject();
        this.trades.addAll((List)s.readObject());
        this.securities.addAll((List)s.readObject());
        this.selectedSecurityNames.addAll((List)s.readObject());
    }

    public Profile(String name, Security benchmark, Pair<Integer, Integer> monthlyStart, Pair<Integer, Integer> monthlyEnd, Calendar windowStart, Calendar windowEnd, List<Series> series, List<Trade> trades, List<Security> securities) {
        this.sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        this.name = "";
        this.technicalsManager = new TechnicalsManager();
        this.trades = FXCollections.observableArrayList(new ArrayList());
        this.securities = FXCollections.observableArrayList(new ArrayList());
        this.selectedSecurityNames = FXCollections.observableArrayList(new ArrayList());
        this.initialize();
        this.name = name;
        this.benchmark = benchmark;
        this.monthlyStatsStart = new GregorianCalendar((Integer)monthlyStart.getValue(), (Integer)monthlyStart.getKey(), 1);
        this.monthlyStatsEnd = new GregorianCalendar((Integer)monthlyEnd.getValue(), (Integer)monthlyEnd.getKey(), 1);
        this.monthlyStatsEnd.set(5, this.monthlyStatsEnd.getActualMaximum(5));
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public Profile() throws IOException {
        this.sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        this.name = "";
        this.technicalsManager = new TechnicalsManager();
        this.trades = FXCollections.observableArrayList(new ArrayList());
        this.securities = FXCollections.observableArrayList(new ArrayList());
        this.selectedSecurityNames = FXCollections.observableArrayList(new ArrayList());
        this.initialize();
        File f = new File("profiles");
        if (!f.isFile() || !f.canRead()) {
            FileOutputStream file = new FileOutputStream("profiles");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(new TreeMap());
            out.close();
            file.close();
        }

    }

    public void initialize() {
        this.monthlyStatsStart = Calendar.getInstance();
        this.monthlyStatsEnd = Calendar.getInstance();
        this.windowStart = Calendar.getInstance();
        this.windowEnd = Calendar.getInstance();
        this.benchmark = new Security("^SPX");
        this.outputDirectory = "";
        this.addSecurityListener();
    }

    public void addSecurityListener() {
        ListChangeListener<Security> securitiesListener = (listChange) -> {
            this.selectedSecurityNames.clear();
            List<String> t = (List)this.getSelectedSecurities().stream().map(Security::getSymbol).collect(Collectors.toList());
            this.selectedSecurityNames.addAll((Collection)this.getSelectedSecurities().stream().map(Security::getSymbol).collect(Collectors.toList()));
        };
        this.securities.addListener(securitiesListener);
    }

    public Profile(String profileName) {
        this.sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        this.name = "";
        this.technicalsManager = new TechnicalsManager();
        this.trades = FXCollections.observableArrayList(new ArrayList());
        this.securities = FXCollections.observableArrayList(new ArrayList());
        this.selectedSecurityNames = FXCollections.observableArrayList(new ArrayList());

        try {
            FileInputStream file = new FileInputStream("profiles");
            ObjectInputStream in = new ObjectInputStream(file);
            Profile p = (Profile)((Map)in.readObject()).get(profileName);
            this.name = p.name;
            this.benchmark = p.benchmark;
            this.monthlyStatsStart = p.monthlyStatsStart;
            this.monthlyStatsEnd = p.monthlyStatsEnd;
            this.windowStart = p.windowStart;
            this.windowEnd = p.windowEnd;
            this.outputDirectory = p.outputDirectory;
            this.technicalsManager = p.technicalsManager;
            if (this.trades == null) {
                this.trades = FXCollections.observableArrayList(new ArrayList());
            } else {
                this.trades.clear();
            }

            if (this.securities == null) {
                this.securities = FXCollections.observableArrayList(new ArrayList());
            } else {
                this.securities.clear();
            }

            if (this.selectedSecurityNames == null) {
                this.selectedSecurityNames = FXCollections.observableArrayList(new ArrayList());
            } else {
                this.selectedSecurityNames.clear();
            }

            this.addSecurityListener();
            this.trades.addAll(p.trades);
            this.securities.addAll(p.securities);
            this.selectedSecurityNames.addAll(p.selectedSecurityNames);
            in.close();
            file.close();
        } catch (IOException var5) {
            System.out.println("IOException reading profile");
        } catch (ClassNotFoundException var6) {
            System.out.println("ClassNotFoundException reading profile");
        }

    }

    public void serialize() {
        try {
            FileInputStream inputFile = new FileInputStream("profiles");
            ObjectInputStream in = new ObjectInputStream(inputFile);
            Map<String, Profile> map = (Map)in.readObject();
            map.put(this.name, this);
            in.close();
            inputFile.close();
            FileOutputStream file = new FileOutputStream("profiles");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(map);
            out.close();
            file.close();
        } catch (ClassNotFoundException | IOException var6) {
            System.out.println("IOException/ClassNotFoundException writing profile");
        }

    }

    public static List<Profile> getProfiles() throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream("profiles");
        ObjectInputStream in = new ObjectInputStream(file);
        return (ArrayList)((Map)in.readObject()).values();
    }

    public static List<String> getProfileNames() throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream("profiles");
        ObjectInputStream in = new ObjectInputStream(file);
        return new ArrayList(((Map)in.readObject()).keySet());
    }

    public List<Security> getSelectedSecurities() {
        List<Security> out = new ArrayList();
        Iterator var2 = this.securities.iterator();

        while(var2.hasNext()) {
            Security s = (Security)var2.next();
            if (s.isSelected()) {
                out.add(s);
            }
        }

        return out;
    }

    public List<Trade> getSelectedTrades() {
        List<Trade> out = new ArrayList();
        Iterator var2 = this.trades.iterator();

        while(var2.hasNext()) {
            Trade s = (Trade)var2.next();
            out.add(s);
        }

        return out;
    }

    public Security getBenchmark() {
        return this.benchmark;
    }

    public void setMonthlyStatsStartField(int field, int value) {
        this.monthlyStatsStart.set(field, field == 2 ? value - 1 : value);
    }

    public void setMonthlyStatsEndField(int field, int value) {
        this.monthlyStatsEnd.set(field, field == 2 ? value - 1 : value);
    }

    public Integer getMonthlyStatsStartField(int field) {
        return this.monthlyStatsStart.get(field) + (field == 2 ? 1 : 0);
    }

    public Integer getMonthlyStatsEndField(int field) {
        return this.monthlyStatsEnd.get(field) + (field == 2 ? 1 : 0);
    }

    public void setWindowStart(String formattedDate) throws ParseException {
        this.windowStart.setTime(this.sdf.parse(formattedDate));
    }

    public void setWindowEnd(String formattedDate) throws ParseException {
        this.windowEnd.setTime(this.sdf.parse(formattedDate));
    }

    public String getWindowStart() {
        return this.sdf.format(this.windowStart.getTime());
    }

    public String getWindowEnd() {
        return this.sdf.format(this.windowEnd.getTime());
    }

    public Calendar getWindowStartCal() {
        return this.windowStart;
    }

    public Calendar getWindowEndCal() {
        return this.windowEnd;
    }

    public void setBenchmark(Security s) {
        this.benchmark = s;

        Trade t;
        for(Iterator var2 = this.trades.iterator(); var2.hasNext(); t.benchmark = this.benchmark) {
            t = (Trade)var2.next();
        }

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutputDirectory() {
        return this.outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Calendar getMonthlyStatsStart() {
        return this.monthlyStatsStart;
    }

    public Calendar getMonthlyStatsEnd() {
        return this.monthlyStatsEnd;
    }
}
