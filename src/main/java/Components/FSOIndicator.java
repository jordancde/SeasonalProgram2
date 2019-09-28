package Components;

import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class FSOIndicator extends Indicator {
    int lookBack;
    int kPeriods;
    int dPeriods;

    public FSOIndicator(String name, Security s, Security benchmark, Calendar start, Calendar end, int lookBack, int kPeriods, int dPeriods) {
        super(name, s, benchmark, start, end);
        this.lookBack = lookBack;
        this.kPeriods = kPeriods;
        this.dPeriods = dPeriods;
    }

    public List<Series> getFSO() throws SymbolInvalidException {
        Calendar newStart = (Calendar)this.start.clone();
        newStart.add(5, -(this.lookBack + this.kPeriods + this.dPeriods) * 4);
        this.security.refresh(newStart, this.end);
        this.benchmark.refresh(newStart, this.end);
        Series k = this.getK(this.security.getCloses(), this.security.getHighs(), this.security.getLows());
        Series d = this.getD(k, this.dPeriods);
        List<Series> series = new ArrayList();
        series.add(k);
        series.add(d);
        return series;
    }

    Series getK(Series closes, Series highs, Series lows) throws SymbolInvalidException {
        List<Double> kArray = new ArrayList();

        int i;
        for(i = 0; i < this.lookBack; ++i) {
            kArray.add(null);
        }

        for(i = this.lookBack; i < closes.getValues().size(); ++i) {
            Double lowest = (Double)Collections.min(lows.getValues().subList(i - this.lookBack, i + 1));
            Double highest = (Double)Collections.max(highs.getValues().subList(i - this.lookBack, i + 1));
            Double currentClose = (Double)closes.getValues().get(i);
            Double val = (currentClose - lowest) / (highest - lowest) * 100.0D;
            kArray.add(val);
        }

        Series kArraySeries = new Series("FSO %K", closes.getDates(), kArray);
        Series fullK = new Series("FSO %K", closes.getDates(), this.getSMA(kArraySeries.getValues(), this.kPeriods));
        fullK.name = "FSO %K";
        return fullK;
    }

    Series getD(Series fullK, int periods) throws SymbolInvalidException {
        List<Double> dValues = this.getSMA(fullK.getValues(), periods);
        return new Series("FSO %D", fullK.getDates(), dValues);
    }
}
