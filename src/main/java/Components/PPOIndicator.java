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
        Series slowEMA = getEMA(security.getCloses(), slow);
        slowEMA.name = "PPO slow EMA";
        Series fastEMA = getEMA(security.getCloses(), fast);
        fastEMA.name = "PPO fast EMA";

        Series PPO = fastEMA.getDiffVs(slowEMA).getRatioVs(slowEMA);
        PPO.name = "PPO (" + fast + " " + slow + ")";

        Series signalSeries = getEMA(PPO, signal).trim(start, end);
        signalSeries.name = "PPO Signal (" + signal + ")";

        Series histogram = PPO.trim(start, end).getDiffVs(signalSeries);

        histogram.name = "PPO Histogram";
        List<Series> series = new ArrayList();
        series.add(PPO);
        series.add(signalSeries);
        series.add(histogram);
        return series;
    }
}
