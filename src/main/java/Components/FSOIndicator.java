package Components;

import Components.Exceptions.InvalidInputException;
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

    public List<Series> getFSO() throws SymbolInvalidException, InvalidInputException {
        Series k = this.getK(this.security.getCloses(), this.security.getHighs(), this.security.getLows());
        Series d = this.getD(k, this.dPeriods);
        List<Series> series = new ArrayList();
        series.add(k);
        series.add(d);
        return series;
    }

    Series getK(Series closes, Series highs, Series lows) {
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
        Series fullK = this.getSMA(kArraySeries, this.kPeriods);
        fullK.name = "FSO %K";

        return fullK;
    }

    Series getD(Series fullK, int periods) throws SymbolInvalidException {
        Series d = this.getSMA(fullK, periods);
        d.name = "FSO %D";

        return d;
    }
}
