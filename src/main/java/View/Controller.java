package View;

import Components.ManualInputParser;
import Components.Profile;
import Components.Security;
import Components.Tables.*;
import Components.Trade;
import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;
import Components.Tables.MonthlyTable.Type;
import Components.TechnicalsManager.TechnicalType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.Pair;

public class Controller {
    public Profile profile;
    @FXML
    private ComboBox<String> profileDropdown;
    @FXML
    private TextField benchmarkText;
    @FXML
    private TextField monthlyStatsStartText;
    @FXML
    private TextField monthlyStatsEndText;
    @FXML
    private TextField technicalStartText;
    @FXML
    private TextField technicalEndText;
    @FXML
    private CheckBox selectAllSelect;
    @FXML
    private TableView<Security> securitiesTable = new TableView();
    @FXML
    private TableColumn<Security, Boolean> securitiesTableSelectedColumn = new TableColumn();
    @FXML
    private TableColumn<Security, String> securitiesTableSecurityColumn = new TableColumn();
    @FXML
    private Button addSecurityButton;
    @FXML
    private Button addTradeButton;
    @FXML
    private Button removeSecurityButton;
    @FXML
    private Button removeTradeButton;
    @FXML
    private TableView<Trade> tradesTable = new TableView();
    @FXML
    private TableColumn<Trade, Boolean> tradesTableLongColumn = new TableColumn();
    @FXML
    private TableColumn<Trade, String> tradesTableSecurityColumn = new TableColumn();
    @FXML
    private TableColumn<Trade, String> tradesTableEntryColumn = new TableColumn();
    @FXML
    private TableColumn<Trade, String> tradesTableExitColumn = new TableColumn();
    @FXML
    private CheckBox lowSelect;
    @FXML
    private CheckBox gainSelect;
    @FXML
    private CheckBox highSelect;
    @FXML
    private CheckBox openSelect;
    @FXML
    private CheckBox volumeSelect;
    @FXML
    private CheckBox closeSelect;
    @FXML
    private CheckBox relStrBenchmarkRatioSelect;
    @FXML
    private CheckBox relStrBenchmarkGainSelect;
    @FXML
    private CheckBox benchmarkCloseSelect;
    @FXML
    private CheckBox benchmarkGainSelect;
    @FXML
    private CheckBox movAvgSimp1Select;
    @FXML
    private CheckBox movAvgEMA1Select;
    @FXML
    private CheckBox movAvgEMA2Select;
    @FXML
    private CheckBox movAvgSimpRelBM1Select;
    @FXML
    private CheckBox movAvgSimpRelBM2Select;
    @FXML
    private CheckBox movAvgEMARelBM1Select;
    @FXML
    private CheckBox movAvgEMARelBM2Select;
    @FXML
    private CheckBox movAvgSimp2Select;
    @FXML
    private TextField movAvgSimp1Text;
    @FXML
    private TextField movAvgSimp2Text;
    @FXML
    private TextField movAvgEMA1Text;
    @FXML
    private TextField movAvgEMA2Text;
    @FXML
    private TextField movAvgSimpRelBM1Text;
    @FXML
    private TextField movAvgSimpRelBM2Text;
    @FXML
    private TextField movAvgEMARelBM1Text;
    @FXML
    private TextField movAvgEMARelBM2Text;
    @FXML
    private CheckBox RSISelect;
    @FXML
    private CheckBox RSIRelBMSelect;
    @FXML
    private CheckBox FSOSelect;
    @FXML
    private CheckBox FSORelBMSelect;
    @FXML
    private CheckBox MACDSelect;
    @FXML
    private CheckBox PPOSelect;
    @FXML
    private CheckBox correlationSelect;
    @FXML
    private TextField RSIText1;
    @FXML
    private TextField RSIRelBMText1;
    @FXML
    private TextField FSOText1;
    @FXML
    private TextField FSOText2;
    @FXML
    private TextField FSOText3;
    @FXML
    private TextField FSORelBMText1;
    @FXML
    private TextField FSORelBMText2;
    @FXML
    private TextField FSORelBMText3;
    @FXML
    private TextField MACDText1;
    @FXML
    private TextField MACDText2;
    @FXML
    private TextField MACDText3;
    @FXML
    private TextField PPOText1;
    @FXML
    private TextField PPOText2;
    @FXML
    private TextField PPOText3;
    @FXML
    private TextField correlationText1;
    @FXML
    private TextField outputDirText;
    @FXML
    private Button runButton;
    @FXML
    private Button saveProfileButton;
    @FXML
    private Button deleteProfileButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button updateButton;
    @FXML
    private CheckBox adjustedClosesSelect;

    HashMap<CheckBox, TechnicalType> boxMapping = new HashMap();
    HashMap<TextField, Pair<TechnicalType, Integer>> technicalFieldMapping = new HashMap();

    public Controller() {
        try {
            this.profile = new Profile();
        } catch (IOException var2) {
            System.out.println("Error creating profile");
        }

    }

    public void saveProfile() throws IOException, ClassNotFoundException {
        this.profile.serialize();
    }

    public void loadProfile(String name) throws IOException {
        if(name.isEmpty()){
            this.profile = new Profile();
        }else{
            this.profile = new Profile(name);
        }
        this.profileDropdown.setValue(this.profile.getName());
        this.setupSecuritiesTable();
        this.setupTradesTable();
        this.outputDirText.setText(this.profile.getOutputDirectory());
        this.benchmarkText.setText(this.profile.getBenchmark().getSymbol());
        this.monthlyStatsStartText.setText(this.profile.getMonthlyStatsStart());
        this.monthlyStatsEndText.setText(this.profile.getMonthlyStatsEnd());
        this.technicalStartText.setText(this.profile.getWindowStart());
        this.technicalEndText.setText(this.profile.getWindowEnd());
        this.boxMapping.forEach((box, type) -> {
            box.setSelected(this.profile.technicalsManager.getSelected(type));
        });
        this.technicalFieldMapping.forEach((field, pair) -> {
            TechnicalType type = (TechnicalType)pair.getKey();
            int fieldIndex = (Integer)pair.getValue();
            Integer value = this.profile.technicalsManager.getParamter(type, fieldIndex);
            String text = value > 0 ? value.toString() : null;
            field.setText(text);
        });
        this.adjustedClosesSelect.setSelected(this.profile.getUseAdjustedCloses());
    }

    public List<String> listProfiles() throws IOException, ClassNotFoundException {
        Profile var10000 = this.profile;
        return Profile.getProfileNames();
    }

    public void initialize() throws IOException, ClassNotFoundException {
        this.setupProfile();
        this.setupSecuritiesTable();
        this.setupTradesTable();
        this.setupButtons();
        this.setupTextFields();
        this.setupTechnicals();
    }

    public void setupProfile() throws IOException, ClassNotFoundException {
        this.profileDropdown.setItems(FXCollections.observableList(this.listProfiles()));
        this.profileDropdown.valueProperty().addListener((obs, oldItem, newItem) -> {
            try {
                this.loadProfile(newItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setupButtons() {
        this.selectAllSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Iterator var4 = Controller.this.profile.securities.iterator();

                while(var4.hasNext()) {
                    Security s = (Security)var4.next();
                    s.setSelected(newValue);
                }

                Controller.this.refreshTables();
            }
        });

        this.addSecurityButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Controller.this.securitiesTable.setEditable(false);
                Controller.this.profile.securities.add(new Security());
                Controller.this.securitiesTable.setEditable(true);
            }
        });
        this.addTradeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Controller.this.tradesTable.setEditable(false);
                Controller.this.profile.trades.add(new Trade(new Security(Controller.this.benchmarkText.getText())));
                Controller.this.tradesTable.setEditable(true);
            }
        });

        this.removeSecurityButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Security selected = securitiesTable.getSelectionModel().getSelectedItem();
                securitiesTable.getItems().remove(selected);
                profile.securities.remove(selected);
            }
        });
        this.removeTradeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Trade selected = tradesTable.getSelectionModel().getSelectedItem();
                tradesTable.getItems().remove(selected);
                profile.trades.remove(selected);
            }
        });

        this.saveProfileButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                TextInputDialog td = new TextInputDialog("Enter Profile Name");
                Optional<String> result = td.showAndWait();
                if (result.isPresent()) {

                    String name = td.getEditor().getText();

                    // confirm if replacing profile
                    try {
                        List<String> profileNames = listProfiles();

                        if (profileNames.contains(name)) {
                            Alert alert = new Alert(AlertType.CONFIRMATION, "Profile exists, would you like to overwrite?");
                            Optional<ButtonType> overwriteProfile = alert.showAndWait();

                            if (!(result.isPresent()) || (overwriteProfile.get() != ButtonType.OK))
                                return;

                            profileDropdown.getItems().remove(name);
                        }
                    }catch(IOException e){}
                    catch(ClassNotFoundException e){}

                    Controller.this.profile.setName(td.getEditor().getText());

                    try {
                        Controller.this.validateInput();
                        Controller.this.saveProfile();
                        profileDropdown.getItems().add(profile.getName());
                        Controller.this.profileDropdown.setValue(Controller.this.profile.getName());
                    } catch (InvalidInputException var6) {
                        Alert alert = new Alert(AlertType.ERROR, "Input Invalid: " + var6.getLocalizedMessage(), new ButtonType[]{ButtonType.CLOSE});
                        alert.showAndWait();
                    } catch (Exception var7) {
                        System.out.println(var7);
                    }

                }
            }
        });
        this.deleteProfileButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(profile.getName().isEmpty()) return;

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete Profile");
                String s = "Are you sure you'd like to delete " + profile.getName() + "?";
                alert.setContentText(s);

                Optional<ButtonType> result = alert.showAndWait();

                if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                    profileDropdown.setValue("");
                    profileDropdown.getItems().remove(profile.getName());
                    profile.delete();
                    try {
                        profile = new Profile();
                        Alert resultAlert = new Alert(AlertType.CONFIRMATION, "Profile deleted.", new ButtonType[]{ButtonType.CLOSE});
                        resultAlert.showAndWait();
                    } catch (IOException ex) {
                        Alert errorAlert = new Alert(AlertType.ERROR, "IOException while deleting profile.", new ButtonType[]{ButtonType.CLOSE});
                        errorAlert.showAndWait();
                    }
                }
            }
        });

        this.runButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                (new Thread(() -> {
                    Controller.this.run();
                })).start();
            }
        });

        this.updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                (new Thread(() -> {
                    try {
                        update();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(AlertType.CONFIRMATION, "Latest version installed. Please relaunch.", new ButtonType[]{ButtonType.CLOSE});
                            alert.showAndWait();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(AlertType.ERROR, "Update server unavailable.", new ButtonType[]{ButtonType.CLOSE});
                            alert.showAndWait();
                        });
                    }
                })).start();
            }
        });

        this.adjustedClosesSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                profile.setUseAdjustedCloses(newValue);
                Security.USE_ADJUSTED_CLOSE = newValue;
            }
        });
    }

    public void setupTechnicals() {
        this.boxMapping = new HashMap();
        this.boxMapping.put(this.closeSelect, TechnicalType.CLOSE);
        this.boxMapping.put(this.openSelect, TechnicalType.OPEN);
        this.boxMapping.put(this.highSelect, TechnicalType.HIGH);
        this.boxMapping.put(this.lowSelect, TechnicalType.LOW);
        this.boxMapping.put(this.volumeSelect, TechnicalType.VOLUME);
        this.boxMapping.put(this.gainSelect, TechnicalType.PERCENT_GAIN);
        this.boxMapping.put(this.benchmarkGainSelect, TechnicalType.BENCHMARK_PERCENT_GAIN);
        this.boxMapping.put(this.benchmarkCloseSelect, TechnicalType.BENCHMARK_CLOSE);
        this.boxMapping.put(this.relStrBenchmarkGainSelect, TechnicalType.REL_STR_VS_BM_PERCENT_GAIN);
        this.boxMapping.put(this.relStrBenchmarkRatioSelect, TechnicalType.REL_STR_VS_BM_RATIO);
        this.boxMapping.put(this.movAvgSimp1Select, TechnicalType.MOV_AVG_SIMP_1);
        this.boxMapping.put(this.movAvgSimp2Select, TechnicalType.MOV_AVG_SIMP_2);
        this.boxMapping.put(this.movAvgEMA1Select, TechnicalType.MOV_AVG_EMA_1);
        this.boxMapping.put(this.movAvgEMA2Select, TechnicalType.MOV_AVG_EMA_2);
        this.boxMapping.put(this.movAvgSimpRelBM1Select, TechnicalType.MOV_AVG_SIMP_REL_BM_1);
        this.boxMapping.put(this.movAvgSimpRelBM2Select, TechnicalType.MOV_AVG_SIMP_REL_BM_2);
        this.boxMapping.put(this.movAvgEMARelBM1Select, TechnicalType.MOV_AVG_EMA_REL_BM_1);
        this.boxMapping.put(this.movAvgEMARelBM2Select, TechnicalType.MOV_AVG_EMA_REL_BM_2);
        this.boxMapping.put(this.RSISelect, TechnicalType.RSI);
        this.boxMapping.put(this.RSIRelBMSelect, TechnicalType.RSI_REL_BM);
        this.boxMapping.put(this.FSOSelect, TechnicalType.FSO);
        this.boxMapping.put(this.FSORelBMSelect, TechnicalType.FSO_REL_BM);
        this.boxMapping.put(this.MACDSelect, TechnicalType.MACD);
        this.boxMapping.put(this.PPOSelect, TechnicalType.PPO);
        this.boxMapping.put(this.correlationSelect, TechnicalType.CORREL);
        this.technicalFieldMapping = new HashMap();
        this.technicalFieldMapping.put(this.movAvgSimp1Text, new Pair(TechnicalType.MOV_AVG_SIMP_1, 0));
        this.technicalFieldMapping.put(this.movAvgSimp2Text, new Pair(TechnicalType.MOV_AVG_SIMP_2, 0));
        this.technicalFieldMapping.put(this.movAvgEMA1Text, new Pair(TechnicalType.MOV_AVG_EMA_1, 0));
        this.technicalFieldMapping.put(this.movAvgEMA2Text, new Pair(TechnicalType.MOV_AVG_EMA_2, 0));
        this.technicalFieldMapping.put(this.movAvgSimpRelBM1Text, new Pair(TechnicalType.MOV_AVG_SIMP_REL_BM_1, 0));
        this.technicalFieldMapping.put(this.movAvgSimpRelBM2Text, new Pair(TechnicalType.MOV_AVG_SIMP_REL_BM_2, 0));
        this.technicalFieldMapping.put(this.movAvgEMARelBM1Text, new Pair(TechnicalType.MOV_AVG_EMA_REL_BM_1, 0));
        this.technicalFieldMapping.put(this.movAvgEMARelBM2Text, new Pair(TechnicalType.MOV_AVG_EMA_REL_BM_2, 0));
        this.technicalFieldMapping.put(this.RSIText1, new Pair(TechnicalType.RSI, 0));
        this.technicalFieldMapping.put(this.RSIRelBMText1, new Pair(TechnicalType.RSI_REL_BM, 0));
        this.technicalFieldMapping.put(this.FSOText1, new Pair(TechnicalType.FSO, 0));
        this.technicalFieldMapping.put(this.FSOText2, new Pair(TechnicalType.FSO, 1));
        this.technicalFieldMapping.put(this.FSOText3, new Pair(TechnicalType.FSO, 2));
        this.technicalFieldMapping.put(this.FSORelBMText1, new Pair(TechnicalType.FSO_REL_BM, 0));
        this.technicalFieldMapping.put(this.FSORelBMText2, new Pair(TechnicalType.FSO_REL_BM, 1));
        this.technicalFieldMapping.put(this.FSORelBMText3, new Pair(TechnicalType.FSO_REL_BM, 2));
        this.technicalFieldMapping.put(this.MACDText1, new Pair(TechnicalType.MACD, 0));
        this.technicalFieldMapping.put(this.MACDText2, new Pair(TechnicalType.MACD, 1));
        this.technicalFieldMapping.put(this.MACDText3, new Pair(TechnicalType.MACD, 2));
        this.technicalFieldMapping.put(this.PPOText1, new Pair(TechnicalType.PPO, 0));
        this.technicalFieldMapping.put(this.PPOText2, new Pair(TechnicalType.PPO, 1));
        this.technicalFieldMapping.put(this.PPOText3, new Pair(TechnicalType.PPO, 2));
        this.technicalFieldMapping.put(this.correlationText1, new Pair(TechnicalType.CORREL, 0));
        this.boxMapping.forEach((box, type) -> {
            box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    Controller.this.profile.technicalsManager.setSelected(type, new_val);
                }
            });
        });
        this.technicalFieldMapping.forEach((field, pair) -> {
            TechnicalType type = (TechnicalType)pair.getKey();
            int fieldIndex = (Integer)pair.getValue();
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Integer val = Integer.valueOf(newValue);
                    this.profile.technicalsManager.setParameter(type, fieldIndex, val);
                } catch (NumberFormatException var7) {
                }

            });
        });
    }

    public void setupSecuritiesTable() {
        this.securitiesTable.setEditable(true);
        this.securitiesTable.setItems(this.profile.securities);
        this.securitiesTableSelectedColumn.setCellValueFactory(new Callback<CellDataFeatures<Security, Boolean>, ObservableValue<Boolean>>() {
            public ObservableValue<Boolean> call(CellDataFeatures<Security, Boolean> param) {
                final Security security = (Security)param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(security.isSelected());
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        security.setSelected(newValue);
                        Controller.this.refreshTables();
                    }
                });
                return booleanProp;
            }
        });
        this.securitiesTableSelectedColumn.setCellFactory(new Callback<TableColumn<Security, Boolean>, TableCell<Security, Boolean>>() {
            public TableCell<Security, Boolean> call(TableColumn<Security, Boolean> p) {
                CheckBoxTableCell<Security, Boolean> cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        this.securitiesTableSecurityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.securitiesTableSecurityColumn.setCellValueFactory((data) -> {
            return ((Security)data.getValue()).symbolProperty();
        });
        this.securitiesTableSecurityColumn.setOnEditCommit((event) -> {
            TablePosition<Security, String> pos = event.getTablePosition();
            String newSymbol = (String)event.getNewValue();
            int row = pos.getRow();
            Security security = (Security)event.getTableView().getItems().get(row);
            security.setSymbol(newSymbol);
            this.refreshTables();
        });
    }

    public void setupTradesTable() {
        this.tradesTable.setEditable(true);
        this.tradesTable.setItems(this.profile.trades);
        this.tradesTableLongColumn.setCellValueFactory(new Callback<CellDataFeatures<Trade, Boolean>, ObservableValue<Boolean>>() {
            public ObservableValue<Boolean> call(CellDataFeatures<Trade, Boolean> param) {
                final Trade trade = (Trade)param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(trade.isLong());
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        trade.setIsLong(newValue);
                    }
                });
                return booleanProp;
            }
        });
        this.tradesTableLongColumn.setCellFactory(new Callback<TableColumn<Trade, Boolean>, TableCell<Trade, Boolean>>() {
            public TableCell<Trade, Boolean> call(TableColumn<Trade, Boolean> p) {
                CheckBoxTableCell<Trade, Boolean> cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        this.tradesTableSecurityColumn.setCellValueFactory(new Callback<CellDataFeatures<Trade, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<Trade, String> param) {
                Trade trade = (Trade)param.getValue();
                String security = trade.getSecurity().getSymbol();
                return new SimpleObjectProperty(security);
            }
        });
        this.tradesTableSecurityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(this.profile.selectedSecurityNames));
        this.tradesTableSecurityColumn.setOnEditCommit((event) -> {
            TablePosition<Trade, String> pos = event.getTablePosition();
            Security newSecurity = (Security)this.profile.securities.stream().filter((s) -> {
                return ((String)event.getNewValue()).equals(s.getSymbol());
            }).findFirst().orElse(null);
            int row = pos.getRow();
            Trade trade = (Trade)event.getTableView().getItems().get(row);
            trade.setSecurity(newSecurity);
        });
        this.tradesTableEntryColumn.setCellValueFactory(new PropertyValueFactory("start"));
        this.tradesTableEntryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.tradesTableEntryColumn.setOnEditCommit((event) -> {
            TablePosition<Trade, String> pos = event.getTablePosition();
            String newStart = (String)event.getNewValue();
            int row = pos.getRow();
            Trade trade = (Trade)event.getTableView().getItems().get(row);
            trade.setStart(newStart);
        });
        this.tradesTableExitColumn.setCellValueFactory(new PropertyValueFactory("end"));
        this.tradesTableExitColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.tradesTableExitColumn.setOnEditCommit((event) -> {
            TablePosition<Trade, String> pos = event.getTablePosition();
            String newEnd = (String)event.getNewValue();
            int row = pos.getRow();
            Trade trade = (Trade)event.getTableView().getItems().get(row);
            trade.setEnd(newEnd);
        });
    }

    public void setupTextFields() {

        this.benchmarkText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setBenchmark(new Security(newValue));
        });

        this.monthlyStatsStartText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setMonthlyStatsStart(newValue);
        });

        this.monthlyStatsEndText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setMonthlyStatsEnd(newValue);
        });

        this.technicalStartText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setWindowStart(newValue);
        });

        this.technicalEndText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setWindowEnd(newValue);
        });

        this.outputDirText.textProperty().addListener((observable, oldValue, newValue) -> {
            this.profile.setOutputDirectory(newValue);
        });
    }

    public void refreshTables() {
        Security s = (Security)this.profile.securities.get(0);
        this.profile.securities.remove(s);
        this.profile.securities.add(0, s);
        this.securitiesTable.refresh();
        this.tradesTable.refresh();
    }

    public void run() {
        try {
            // refresh input file
            ManualInputParser manualInputParser = ManualInputParser.getInstance();
            manualInputParser.refresh();

            this.validateInput();

            String directory = this.profile.getOutputDirectory();
            String dir = directory.replaceAll("/$", "") + "/";

            DateFormat df = new SimpleDateFormat("dd_MM_yy HH_mm_ss");
            Date now = Calendar.getInstance().getTime();
            String dateString = df.format(now);
            String directoryName = "Output - " + dateString;

            dir = dir + directoryName;
            new File(dir).mkdirs();

            int numTables = 1 + 5 * this.profile.getSelectedSecurities().size();

            double progress = 0.0D;
            this.progressBar.setProgress(progress);
            PrintWriter writer;

            // disabled this for now as I need to cover cases when the data ends before the end year of technical stats
            /*if(technicalStatsEnabled()) {
                writer = new PrintWriter(dir + "/Sectors Summary.csv");
                writer.write((new YearlyPerformanceTable(this.profile.getSelectedSecurities(), this.profile.getBenchmark(), this.profile.getWindowStartCal(), this.profile.getWindowEndCal())).toString());
                writer.close();
            }*/

            if(monthlyStatsEnabled()) {
                writer = new PrintWriter(dir + "/Thermometer Monthly.csv");
                writer.write((new ThermometerTable(this.profile.getSelectedSecurities(), this.profile.getBenchmark(), this.profile.getMonthlyStatsStartCal(), this.profile.getMonthlyStatsEndCal())).toString());
                writer.close();
            }

            progress += 1.0D / numTables;
            this.progressBar.setProgress(progress);

            for(Security s: this.profile.getSelectedSecurities()) {

                String currentDir = dir + "/" + s.getSymbol();
                (new File(currentDir)).mkdirs();

                if(seasonalityEnabled()) {
                    writer = new PrintWriter(currentDir + "/" + s.getSymbol() + " - Seasonality.csv");
                    writer.write((new SecurityTradesTable(this.profile.trades, s)).toString());
                    writer.close();
                }
                progress += 1.0D / numTables;
                this.progressBar.setProgress(progress);

                if(monthlyStatsEnabled()) {
                    writer = new PrintWriter(currentDir + "/" + s.getSymbol() + " - Monthly Gains (Horiz).csv");
                    writer.write((new MonthlyTable(s.getSymbol() + " Monthly % Gains", s, this.profile.getBenchmark(), this.profile.getMonthlyStatsStartCal(), this.profile.getMonthlyStatsEndCal(), Type.HORIZONTAL)).toString());
                    writer.close();
                }
                progress += 1.0D / numTables;
                this.progressBar.setProgress(progress);

                if(monthlyStatsEnabled()) {
                    writer = new PrintWriter(currentDir + "/" + s.getSymbol() + " - Monthly Gains (Vert).csv");
                    writer.write((new MonthlyTable(s.getSymbol() + " Monthly % Gains", s, this.profile.getBenchmark(), this.profile.getMonthlyStatsStartCal(), this.profile.getMonthlyStatsEndCal(), Type.VERTICAL)).toString());
                    writer.close();
                }
                progress += 1.0D / numTables;
                this.progressBar.setProgress(progress);

                if(technicalStatsEnabled()) {
                    writer = new PrintWriter(currentDir + "/" + s.getSymbol() + " - Cumulative Average.csv");
                    writer.write((new CumulativeTable("", s, this.profile.getBenchmark(), this.profile.getWindowStartCal(), this.profile.getWindowEndCal())).toString());
                    writer.close();
                }
                progress += 1.0D / (double)numTables;
                this.progressBar.setProgress(progress);

                if(technicalStatsEnabled()) {
                    writer = new PrintWriter(currentDir + "/" + s.getSymbol() + " - Technicals.csv");
                    writer.write((new TechnicalTable(this.profile.technicalsManager, s, this.profile.getBenchmark(), this.profile.getWindowStartCal(), this.profile.getWindowEndCal())).toString());
                    writer.close();
                }
                progress += 1.0D / (double)numTables;
                this.progressBar.setProgress(progress);
            }
        } catch (IOException var12) {
            Platform.runLater(() -> {
                var12.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Output directory invalid." + var12.getLocalizedMessage(), new ButtonType[]{ButtonType.CLOSE});
                alert.showAndWait();
            });
        } catch (InvalidInputException var13) {
            Platform.runLater(() -> {
                var13.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Input Invalid: " + var13.getLocalizedMessage(), new ButtonType[]{ButtonType.CLOSE});
                alert.showAndWait();
            });
        } catch (SymbolInvalidException var14) {
            Platform.runLater(() -> {
                var14.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Symbol " + var14.getLocalizedMessage() + " is invalid. Please check symbol and that data is available for that symbol for its trade dates.", new ButtonType[]{ButtonType.CLOSE});
                alert.showAndWait();
            });
        } catch (Exception var15) {
            Platform.runLater(() -> {
                var15.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Error: " + var15.getLocalizedMessage(), new ButtonType[]{ButtonType.CLOSE});
                alert.showAndWait();
            });
        }

    }

    public boolean isDateValid(String dateToValidate, String dateFormat) {
        if (dateToValidate == null) {
            return false;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);

            try {
                sdf.parse(dateToValidate);
                return true;
            } catch (ParseException var5) {
                return false;
            }
        }
    }

    public void update() throws IOException {
        InputStream inputStream = new URL("https://github.com/jordandearsley/SeasonalProgram2/raw/master/target/SeasonalProgram-2.0.jar").openStream();
        try {
            Files.copy(inputStream, Paths.get(Controller.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()), StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void validateInput() throws InvalidInputException, SymbolInvalidException {

        // These fields must be filled for any run
        if (this.benchmarkText.getText().isEmpty()) {
            throw new InvalidInputException("Benchmark symbol is empty.");
        } else if (this.profile.getSelectedSecurities().size() == 0) {
            throw new InvalidInputException("No securities selected.");
        }
        for(Security s: this.profile.getSelectedSecurities()){
            if(s.getSymbol() == "None")
                throw new InvalidInputException("Selected security does not have a symbol.");
        }
        if (this.profile.getOutputDirectory().isEmpty())
            throw new InvalidInputException("Output directory field empty.");


        // Monthly stats specific validation
        if(monthlyStatsEnabled()){
            if (!this.isDateValid(this.monthlyStatsStartText.getText() + "/01", "yyyy/mm/dd")) {
                throw new InvalidInputException("Monthly stats start date invalid.");
            } else if (!this.isDateValid(this.monthlyStatsEndText.getText()+ "/01", "yyyy/mm/dd")) {
                throw new InvalidInputException("Monthly stats end date invalid.");
            } else if (this.profile.getMonthlyStatsStart().compareTo(this.profile.getMonthlyStatsEnd()) >= 0) {
                throw new InvalidInputException("Monthly stats start date must precede end date.");
            }
        }

        // Technical stats specific validation
        if(technicalStatsEnabled()) {
            if (!this.isDateValid(this.technicalStartText.getText(), "yyyy/mm/dd")) {
                throw new InvalidInputException("Technical stats start date invalid.");
            } else if (!this.isDateValid(this.technicalEndText.getText(), "yyyy/mm/dd")) {
                throw new InvalidInputException("Technical stats end date invalid.");
            } else if (this.profile.getWindowStartCal().compareTo(this.profile.getWindowEndCal()) >= 0) {
                throw new InvalidInputException("Technical stats start date must precede end date.");
            }

            for(TextField field: this.technicalFieldMapping.keySet()){
                try {
                    Integer.parseInt(field.getText());

                    if(this.profile.technicalsManager.getSelected(this.technicalFieldMapping.get(field).getKey())){
                        if (Integer.parseInt(field.getText()) < 1)
                            throw new InvalidInputException("Selected technical indicator parameter must be greater than 0.");

                        if (Integer.parseInt(field.getText()) < 0)
                            throw new InvalidInputException("Selected technical indicator parameter must be greater than 0.");
                    }

                } catch (NumberFormatException e) {
                    if(this.profile.technicalsManager.getSelected(this.technicalFieldMapping.get(field).getKey()))
                        throw new InvalidInputException("Technical indicator parameter not an integer.");

                } catch (NullPointerException e) {}
            }

            if (this.FSORelBMSelect.isSelected())
                throw new InvalidInputException("FSO indicator relative to benchmark is not implemented.");
        }

        // Seasonality gains specific validation
        if(seasonalityEnabled()){

            // Validate the trades
            for(Trade t: this.profile.trades){
                if (t.getSecurity().getSymbol() == "None")
                    throw new InvalidInputException("All trades must select security.");

                if (!this.isDateValid(this.tradesTableEntryColumn.getCellObservableValue(t).getValue(), "yyyy/mm/dd"))
                    throw new InvalidInputException("Trade entry date " + this.tradesTableEntryColumn.getCellObservableValue(t) + " is invalid.");

                if (!this.isDateValid(this.tradesTableExitColumn.getCellObservableValue(t).getValue(), "yyyy/mm/dd"))
                    throw new InvalidInputException("Trade exit date " + this.tradesTableExitColumn.getCellObservableValue(t).getValue() + " is invalid.");
            }

            // validate that all trades for a particular security do not violate any rules
            for(Security s: this.profile.getSelectedSecurities()){
                ArrayList<Trade> securityTrades = new ArrayList();
                for(Trade t: this.profile.trades) {
                    if (t.getSecurity() == s) securityTrades.add(t);
                }

                int numPeriods = securityTrades.get(0).getNumPeriods();
                int startYear = securityTrades.get(0).getStartCal().get(Calendar.YEAR);

                for(Trade t: securityTrades){
                    if (t.getNumPeriods() != numPeriods)
                        throw new InvalidInputException("Trade " + t.getSecurity().getSymbol() + " with entry " + t.getStart() + " has an invalid number of periods. All trades for a security must have the same number of periods.");

                    //if (t.getStartCal().get(1) != startYear)
                     //   throw new InvalidInputException("Trade " + t.getSecurity().getSymbol() + " with entry " + t.getStart() + " has an invalid entry year. All trades for a security must have the same entry year.");

                    if (t.getStartCal().compareTo(t.getEndCal()) > 0)
                        throw new InvalidInputException("Trade " + t.getSecurity().getSymbol() + " with entry " + t.getStart() + " has an invalid entry date. All trades must have entry preceding exit.");
                }
            }
        }
    }

    boolean monthlyStatsEnabled(){
        return !this.monthlyStatsStartText.getText().isEmpty();
    }
    boolean technicalStatsEnabled(){
        return !this.technicalStartText.getText().isEmpty();
    }
    boolean seasonalityEnabled(){
        return this.profile.trades.size() > 0;
    }
}
