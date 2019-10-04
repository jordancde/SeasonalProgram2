package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Indicator {
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

    List<Double> getSMA(List<Double> input, int period) {
        List<Double> values = new ArrayList();

        for(int i = 0; i < input.size(); ++i) {
            if (i >= period - 1 && input.get(i - period + 1) != null) {
                Double sum = 0.0D;

                for(int j = period - 1; j >= 0; --j) {
                    sum = sum + (Double)input.get(i - j);
                }

                values.add(sum / (double)period);
            } else {
                values.add(null);
            }
        }

        return values;
    }

    List<Double> getEMA(List<Double> input, int period) {
        List<Double> values = new ArrayList();
        Double previousEMA = 0.0D;
        Double multiplier = 2.0D / ((double)period + 1.0D);

        for(int i = 0; i < input.size(); ++i) {
            if (i >= period - 1 && input.get(i - period + 1) != null) {
                if (i != period - 1) {
                    previousEMA = ((Double)input.get(i) - previousEMA) * multiplier + previousEMA;
                    values.add(previousEMA);
                } else {
                    for(int j = period - 1; j >= 0; --j) {
                        previousEMA = previousEMA + (Double)input.get(i - j);
                    }

                    previousEMA = previousEMA / (double)period;
                    values.add(null);
                }
            } else {
                values.add(null);
            }
        }

        return values;
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
