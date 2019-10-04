package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PPOIndicator extends Indicator {
    int fast;
    int slow;
    int signal;

    public PPOIndicator(String name, Security s, Security benchmark, Calendar start, Calendar end, int fast, int slow, int signal) {
        super(name, s, benchmark, start, end);
        this.fast = fast;
        this.slow = slow;
        this.signal = signal;
    }

    public List<Series> getPPO() throws SymbolInvalidException, InvalidInputException {
        Calendar newStart = (Calendar)this.start.clone();
        newStart.add(5, -(this.signal + Math.max(this.fast, this.slow)) * 4);

        this.benchmark.refresh(newStart, this.end);
        this.security.refresh(newStart, this.end);

        Series slowEMA = new Series("PPO slow EMA", this.security.getCloses().getDates(), this.getEMA(this.security.getCloses().getValues(), this.slow));
        Series fastEMA = new Series("PPO fast EMA", this.security.getCloses().getDates(), this.getEMA(this.security.getCloses().getValues(), this.fast));
        Series PPO = fastEMA.getDiffVs(slowEMA).getRatioVs(slowEMA);
        PPO.name = "PPO (" + this.fast + " " + this.slow + ")";
        Series signal = new Series("PPO Signal (" + this.signal + ")", PPO.getDates(), this.getEMA(PPO.getValues(), this.signal));
        Series histogram = PPO.getDiffVs(signal);
        histogram.name = "PPO Histogram";
        List<Series> series = new ArrayList();
        series.add(PPO);
        series.add(signal);
        series.add(histogram);
        return series;
    }
}
