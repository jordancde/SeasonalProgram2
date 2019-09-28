package Components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TechnicalsManager implements Serializable {
    private static final long serialVersionUID = 11L;
    public List<TechnicalsManager.Selection> selections = new ArrayList();

    public TechnicalsManager() {
        TechnicalsManager.TechnicalType[] var1 = TechnicalsManager.TechnicalType.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TechnicalsManager.TechnicalType type = var1[var3];
            this.selections.add(new TechnicalsManager.Selection(type, false));
        }

    }

    public void setSelected(TechnicalsManager.TechnicalType t, boolean selected) {
        ((TechnicalsManager.Selection)this.selections.get(t.ordinal())).selected = selected;
    }

    public boolean getSelected(TechnicalsManager.TechnicalType t) {
        return ((TechnicalsManager.Selection)this.selections.get(t.ordinal())).selected;
    }

    public Integer getParamter(TechnicalsManager.TechnicalType t, int index) {
        return index < ((TechnicalsManager.Selection)this.selections.get(t.ordinal())).parameters.size() ? (Integer)((TechnicalsManager.Selection)this.selections.get(t.ordinal())).parameters.get(index) : null;
    }

    public void setParameter(TechnicalsManager.TechnicalType t, int index, int val) {
        if (index < ((TechnicalsManager.Selection)this.selections.get(t.ordinal())).parameters.size()) {
            ((TechnicalsManager.Selection)this.selections.get(t.ordinal())).parameters.set(index, val);
        }
    }

    public List<TechnicalsManager.Selection> getSelections() {
        List<TechnicalsManager.Selection> selected = new ArrayList();
        Iterator var2 = this.selections.iterator();

        while(var2.hasNext()) {
            TechnicalsManager.Selection s = (TechnicalsManager.Selection)var2.next();
            if (s.selected) {
                selected.add(s);
            }
        }

        return selected;
    }

    public static enum TechnicalType {
        CLOSE,
        OPEN,
        HIGH,
        LOW,
        VOLUME,
        PERCENT_GAIN,
        BENCHMARK_PERCENT_GAIN,
        BENCHMARK_CLOSE,
        REL_STR_VS_BM_PERCENT_GAIN,
        REL_STR_VS_BM_RATIO,
        MOV_AVG_SIMP_1,
        MOV_AVG_SIMP_2,
        MOV_AVG_EMA_1,
        MOV_AVG_EMA_2,
        MOV_AVG_SIMP_REL_BM_1,
        MOV_AVG_SIMP_REL_BM_2,
        MOV_AVG_EMA_REL_BM_1,
        MOV_AVG_EMA_REL_BM_2,
        RSI,
        RSI_REL_BM,
        FSO,
        FSO_REL_BM,
        MACD,
        PPO,
        CORREL;

        private TechnicalType() {
        }
    }

    public class Selection implements Serializable {
        private static final long serialVersionUID = 12L;
        public TechnicalsManager.TechnicalType type;
        public boolean selected;
        public List<Integer> parameters;

        public Selection(TechnicalsManager.TechnicalType type, boolean selected, List<Integer> params) {
            this.type = type;
            this.selected = selected;
            this.parameters = params;
        }

        public Selection(TechnicalsManager.TechnicalType type, boolean selected) {
            this.type = type;
            this.selected = selected;
            this.parameters = new ArrayList();
            this.parameters.add(0);
            this.parameters.add(0);
            this.parameters.add(0);
        }
    }
}
