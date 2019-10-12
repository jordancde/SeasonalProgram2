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
        Series slowEMA = this.getEMA(this.security.getCloses(), this.start, this.end, this.slow);
        slowEMA.name = "PPO slow EMA";
        Series fastEMA = this.getEMA(this.security.getCloses(), this.start, this.end, this.fast);
        fastEMA.name = "PPO fast EMA";

        Series PPO = fastEMA.getDiffVs(slowEMA).getRatioVs(slowEMA);
        PPO.name = "PPO (" + this.fast + " " + this.slow + ")";

        Series signal = this.getEMA(PPO, start, end, this.signal);
        signal.name = "PPO Signal (" + this.signal + ")";

        Series histogram = PPO.getDiffVs(signal);

        histogram.name = "PPO Histogram";
        List<Series> series = new ArrayList();
        series.add(PPO);
        series.add(signal);
        series.add(histogram);
        return series;
    }
}
