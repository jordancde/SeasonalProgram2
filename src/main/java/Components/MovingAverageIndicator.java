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

    List<Double> getRSI(Series s) {
        List<Double> outputValues = new ArrayList();
        List<Double> inputValues = s.getValues();
        Double averageGain = 0.0D;
        Double averageLoss = 0.0D;
        outputValues.add(null);

        int i;
        for(i = 1; i < this.period; ++i) {
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

        for(i = this.period; i < inputValues.size(); ++i) {
            Double RS = averageGain / averageLoss;
            Double RSI = 100.0D - 100.0D / (1.0D + RS);
            outputValues.add(RSI);
            Double diff = (Double)inputValues.get(i) - (Double)inputValues.get(i - 1);
            averageGain = (averageGain * 13.0D + (diff > 0.0D ? diff : 0.0D)) / 14.0D;
            averageLoss = (averageLoss * 13.0D + (diff > 0.0D ? 0.0D : -diff)) / 14.0D;
        }

        return outputValues;
    }

    public Series getValues() throws SymbolInvalidException, InvalidInputException {
        Calendar newStart = (Calendar)this.start.clone();
        newStart.add(5, -this.period * 4);
        this.security.refresh(newStart, this.end);
        this.benchmark.refresh(newStart, this.end);
        List values;
        switch(this.type) {
            case SIMPLE:
                values = this.getSMA(this.security.getCloses().getValues(), this.period);
                break;
            case EMA:
                values = this.getEMA(this.security.getCloses().getValues(), this.period);
                break;
            case SIMPLE_REL:
                values = this.getSMA(this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses()).getValues(), this.period);
                break;
            case EMA_REL:
                values = this.getEMA(this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses()).getValues(), this.period);
                break;
            case RSI:
                values = this.getRSI(this.security.getCloses());
                break;
            case RSI_REL:
                values = this.getRSI(this.security.getCloses().getRelativePerformanceVs(this.benchmark.getCloses()));
                break;
            default:
                values = this.security.getCloses().getValues();
        }

        return new Series(this.name, this.security.getCloses().getDates(), values);
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
