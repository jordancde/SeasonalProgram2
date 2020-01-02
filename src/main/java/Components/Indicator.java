package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Indicator {
    int EMA_OFFSET = 300;
    Security security;
    Security benchmark;
    Calendar start;
    Calendar end;
    String name;

    public Indicator(String name, Security s, Calendar start, Calendar end) {
        this.security = s;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public Indicator(String name, Security s, Security benchmark, Calendar start, Calendar end) {
        this.security = s;
        this.benchmark = benchmark;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public Series getValues() throws SymbolInvalidException, InvalidInputException {
        return this.security.closes;
    }

   Series getSMA(Series input, int period) {
        List<Double> inputValues = input.getValues();
        List<Double> values = new ArrayList();

        for(int i = 0; i < inputValues.size(); ++i) {
            if (i >= period - 1 && inputValues.get(i - period + 1) != null) {
                Double sum = 0.0D;

                for(int j = period - 1; j >= 0; --j) {
                    sum = sum + inputValues.get(i - j);
                }

                values.add(sum / (double)period);
            } else {
                values.add(null);
            }
        }

       return new Series("SMA ("+period+") "+input.getName(),input.getDates(),values);
    }

    Series getEMA(Series input, int period) {
        input = input.removeNulls();

        List<Double> inputValues = input.getValues();

        List<Double> values = new ArrayList();
        Double previousEMA = 0.0;
        Double multiplier = 2.0 / (period + 1.0);

        Double sum = 0.0;
        for(int i = 0;i < period -1; i++){
            sum+=inputValues.get(i);
            values.add(null);
        }
        sum+=inputValues.get(period-1);
        previousEMA = sum/Double.valueOf(period);

        values.add(previousEMA);

        for(int i = period; i < inputValues.size(); i++) {
            previousEMA = (inputValues.get(i) - previousEMA) * multiplier + previousEMA;
            values.add(previousEMA);
        }

        return new Series("EMA ("+period+") "+input.getName(),input.getDates(),values);
    }

    static Double calculateSD(List<Double> numArray) {
        Double sum = 0.0D;
        Double standardDeviation = 0.0D;
        int length = numArray.size();

        for(Double d: numArray) sum = sum + d;

        Double mean = sum / (double)length;


        for(Double d: numArray) standardDeviation = standardDeviation + Math.pow(d - mean, 2.0D);

        return Math.sqrt(standardDeviation / (double)length);
    }

    static Double calculateAverage(List<Double> numArray) {
        Double sum = 0.0D;

        for(int i = 0; i < numArray.size(); ++i) {
            sum = sum + (Double)numArray.get(i);
        }

        return sum / (double)numArray.size();
    }
}
