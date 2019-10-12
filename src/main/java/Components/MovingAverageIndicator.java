package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MovingAverageIndicator extends Indicator {
    int period;
    MovingAverageIndicator.Type type;

    public MovingAverageIndicator(String name, Security s, Calendar start, Calendar end, int period, MovingAverageIndicator.Type type) {
        super(name, s, start, end);
        this.period = period;
        this.type = type;
    }

    public MovingAverageIndicator(String name, Security s, Security benchmark, Calendar start, Calendar end, int period, MovingAverageIndicator.Type type) {
        super(name, s, benchmark, start, end);
        this.period = period;
        this.type = type;
    }

    Series getRSI(Series s) {
        List<Double> outputValues = new ArrayList();
        List<Double> inputValues = s.getValues();
        Double averageGain = 0.0D;
        Double averageLoss = 0.0D;
        outputValues.add(null);

        for(int i = 1; i < this.period; ++i) {
            if ((Double)inputValues.get(i - 1) < (Double)inputValues.get(i)) {
                averageGain = averageGain + ((Double)inputValues.get(i) - (Double)inputValues.get(i - 1));
            }

            if ((Double)inputValues.get(i - 1) > (Double)inputValues.get(i)) {
                averageLoss = averageLoss + ((Double)inputValues.get(i - 1) - (Double)inputValues.get(i));
            }

            outputValues.add(null);
        }

        averageGain = averageGain / (double)this.period;
        averageLoss = averageLoss / (double)this.period;

        for(int i = this.period; i < inputValues.size(); ++i) {
            Double RS = averageGain / averageLoss;
            Double RSI = 100.0D - 100.0D / (1.0D + RS);
            outputValues.add(RSI);
            Double diff = (Double)inputValues.get(i) - (Double)inputValues.get(i - 1);
            averageGain = (averageGain * 13.0D + (diff > 0.0D ? diff : 0.0D)) / 14.0D;
            averageLoss = (averageLoss * 13.0D + (diff > 0.0D ? 0.0D : -diff)) / 14.0D;
        }

        return new Series("RSI", s.getDates(), outputValues);
    }

    public Series getValues() throws SymbolInvalidException, InvalidInputException {
        switch(this.type) {
            case SIMPLE:
                return this.getSMA(this.security.getCloses(), this.period);
            case EMA:
                return this.getEMA(this.security.getCloses(), this.start, this.end, this.period);
            case SIMPLE_REL:
                Series relative = this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses());
                return this.getSMA(relative, this.period);
            case EMA_REL:
                Series relCloses = this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses());
                return this.getEMA(relCloses, start, end, this.period);
            case RSI:
                return this.getRSI(this.security.getCloses());
            case RSI_REL:
                return this.getRSI(this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses()));
            default:
                return this.security.getCloses();
        }
    }

    public static enum Type {
        SIMPLE,
        EMA,
        SIMPLE_REL,
        EMA_REL,
        RSI,
        RSI_REL;

        private Type() {
        }
    }
}
