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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
    boolean useAdjustedCloses;
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
        this.monthlyStatsStart = null;
        this.monthlyStatsEnd = null;
        this.windowStart = null;
        this.windowEnd = null;
        this.benchmark = new Security("");
        this.outputDirectory = "";
        this.useAdjustedCloses = false;
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

    public Profile(String profileName)  {

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
            this.useAdjustedCloses = p.useAdjustedCloses;
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

    public void delete(){
        try {
            FileInputStream inputFile = new FileInputStream("profiles");
            ObjectInputStream in = new ObjectInputStream(inputFile);
            Map<String, Profile> map = (Map)in.readObject();
            map.remove(this.name);
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

    public Security getBenchmark() {
        return this.benchmark;
    }

    public void setMonthlyStatsStart(String formattedDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        try {
            if(this.monthlyStatsStart == null) this.monthlyStatsStart = Calendar.getInstance();
            this.monthlyStatsStart.setTime(format.parse(formattedDate));
        }catch(ParseException e){
            this.monthlyStatsStart = null;
        }
    }

    public void setMonthlyStatsEnd(String formattedDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        try {
            if(this.monthlyStatsEnd == null) this.monthlyStatsEnd = Calendar.getInstance();
            this.monthlyStatsEnd.setTime(format.parse(formattedDate));
        }catch(ParseException e){
            this.monthlyStatsEnd = null;
        }
    }

    public String getMonthlyStatsStart() {
        if (this.monthlyStatsStart == null) return "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        return format.format(getMonthlyStatsStartCal().getTime());
    }

    public String getMonthlyStatsEnd() {
        if (this.monthlyStatsEnd == null) return "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        return format.format(getMonthlyStatsEndCal().getTime());
    }

    public Calendar getMonthlyStatsStartCal() {
        return this.monthlyStatsStart;
    }

    public Calendar getMonthlyStatsEndCal() {
        return this.monthlyStatsEnd;
    }

    public void setWindowStart(String formattedDate) {
        try{
            if(this.windowStart == null) this.windowStart = Calendar.getInstance();
            this.windowStart.setTime(this.sdf.parse(formattedDate));
        }catch(ParseException e){
            this.windowStart = null;
        }
    }

    public void setWindowEnd(String formattedDate) {
        try {
            if(this.windowEnd == null) this.windowEnd = Calendar.getInstance();
            this.windowEnd.setTime(this.sdf.parse(formattedDate));
        }catch(ParseException e){
            this.windowEnd = null;
        }
    }

    public String getWindowStart() {
        if (this.windowStart == null) return "";
        return this.sdf.format(this.windowStart.getTime());
    }

    public String getWindowEnd() {
        if (this.windowEnd == null) return "";
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

    public boolean getUseAdjustedCloses() {
        return this.useAdjustedCloses;
    }

    public void setUseAdjustedCloses(boolean useAdjusted) {
        this.useAdjustedCloses = useAdjusted;
    }
}
