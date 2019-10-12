package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CorrelationIndicator extends Indicator {
    int periods;

    public CorrelationIndicator(String name, Security s, Security benchmark, Calendar start, Calendar end, int periods) {
        super(name, s, benchmark, start, end);
        this.periods = periods;
    }

    public Series getValues() throws SymbolInvalidException, InvalidInputException {

        List<Double> values = new ArrayList();

        int i;
        for(i = 0; i < this.periods; ++i) {
            values.add(null);
        }

        for(i = this.periods; i < this.security.getCloses().getValues().size(); ++i) {
            List<Double> securityList = this.security.getCloses().getValues().subList(i - this.periods, i);
            List<Double> benchmarkList = this.benchmark.getCloses().getValues().subList(i - this.periods, i);
            values.add(correlation(securityList, benchmarkList, this.periods));
        }

        return new Series("Correlation", this.security.getCloses().getDates(), values);
    }

    static Double correlation(List<Double> x, List<Double> y, int period) {
        Double xMean = calculateAverage(x);
        Double yMean = calculateAverage(y);
        Double xStd = calculateSD(x);
        Double yStd = calculateSD(y);
        Double sum = 0.0D;

        for(int i = 0; i < x.size(); ++i) {
            Double z_xi = ((Double)x.get(i) - xMean) / xStd;
            Double z_yi = ((Double)y.get(i) - yMean) / yStd;
            sum = sum + z_xi * z_yi;
        }

        return sum / (double)(period - 1);
    }
}
