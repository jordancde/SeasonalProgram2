package Components;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;

public class Series implements Serializable {
    private static final long serialVersionUID = 3L;
    String name;
    List<Calendar> dates;
    List<Double> values;

    public Series(String name, List<Calendar> dates, List<Double> values) {
        this.name = name;
        this.dates = dates;
        this.values = values;
    }

    public Series() {
        this.name = "";
        this.dates = new ArrayList();
        this.values = new ArrayList();
    }

    public Series(List<Series> series) {
        this.name = "";
        this.dates = ((Series)series.get(0)).getDates();
        this.values = new ArrayList();

        for(int i = 0; i < ((Series)series.get(0)).getValues().size(); ++i) {
            Double product = 1.0D;

            Series s;
            for(Iterator var4 = series.iterator(); var4.hasNext(); product = product * (1.0D + (Double)s.getValues().get(i))) {
                s = (Series)var4.next();
            }

            this.values.add(product - 1.0D);
        }

    }

    public List<String> getDateStrings(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        List<String> output = new ArrayList();
        Iterator var4 = this.dates.iterator();

        while(var4.hasNext()) {
            Calendar c = (Calendar)var4.next();
            output.add(sdf.format(c.getTime()));
        }

        return output;
    }

    public List<Double> getValuesRounded() {
        List<Double> out = new ArrayList();
        Iterator var2 = this.values.iterator();

        while(var2.hasNext()) {
            Double v = (Double)var2.next();
            out.add(v == null ? 0.0D : this.round(v));
        }

        return out;
    }

    public Double round(Double d) {
        return (double)Math.round(d * 10000.0D) / 10000.0D;
    }

    public List<Calendar> getDates() {
        return this.dates;
    }

    public List<Double> getValues() {
        return this.values;
    }

    public Series getPercentageGain() {
        List<Double> percentageGain = new ArrayList();
        percentageGain.add(0.0D);
        Iterator it = this.values.iterator();

        for(int i = 1; i < this.values.size(); ++i) {
            percentageGain.add((Double)this.values.get(i) / (Double)this.values.get(i - 1) - 1.0D);
        }

        return new Series(this.name + " Percentage Gain", this.dates, percentageGain);
    }

    public Series getRatioVs(Series other) {
        List<Double> divided = new ArrayList();

        for(int i = 0; i < this.values.size(); ++i) {
            if (this.values.get(i) != null && other.values.get(i) != null) {
                divided.add((Double)this.values.get(i) / (Double)other.values.get(i));
            } else {
                divided.add(null);
            }
        }

        return new Series("Ratio " + this.name + " vs. " + other.name, this.dates, divided);
    }

    public Double getAverage() {
        if (this.values.size() == 0) {
            return 0.0D;
        } else {
            Double total = 0.0D;
            int count = 0;

            Double val;
            for(Iterator var3 = this.values.iterator(); var3.hasNext(); total = total + val) {
                val = (Double)var3.next();
                ++count;
            }

            return total / (double)count;
        }
    }

    public Double getMedian() {
        if (this.values.size() == 0) {
            return 0.0D;
        } else {
            DoubleStream sortedValues = this.values.stream().mapToDouble(Double::doubleValue).sorted();
            double median = this.values.size() % 2 == 0 ? sortedValues.skip((long)(this.values.size() / 2 - 1)).limit(2L).average().getAsDouble() : sortedValues.skip((long)(this.values.size() / 2)).findFirst().getAsDouble();
            return median;
        }
    }

    public Double getMax() {
        return this.values.size() == 0 ? 0.0D : (Double)Collections.max(this.values);
    }

    public Double getMin() {
        return this.values.size() == 0 ? 0.0D : (Double)Collections.min(this.values);
    }

    public int getNumPositive() {
        int count = 0;

        Double val;
        for(Iterator var2 = this.values.iterator(); var2.hasNext(); count += val > 0.0D ? 1 : 0) {
            val = (Double)var2.next();
        }

        return count;
    }

    public Double getFreqPositive() {
        return this.values.size() == 0 ? 0.0D : Double.valueOf((double)this.getNumPositive()) / (double)this.values.size();
    }

    public int getNumPositiveVs(Series other) {
        int count = 0;

        for(int i = 0; i < other.values.size(); ++i) {
            count += (Double)this.values.get(i) - (Double)other.values.get(i) > 0.0D ? 1 : 0;
        }

        return count;
    }

    public Double getFreqPositiveVs(Series other) {
        return Double.valueOf((double)this.getNumPositiveVs(other)) / (double)this.values.size();
    }

    public Double getValue(Calendar c) {
        for(int i = 1; i < this.dates.size(); ++i) {
            if (c.compareTo((Calendar)this.dates.get(i)) <= 0) {
                return (Double)this.values.get(i - 1);
            }
        }

        return 4.9E-324D;
    }

    public Double getCurrentValue(Calendar c) {
        for(int i = 0; i < this.dates.size(); ++i) {
            if (c.compareTo((Calendar)this.dates.get(i)) < 0) {
                return (Double)this.values.get(i - 1);
            }
        }

        return 4.9E-324D;
    }

    public Calendar getDate(Calendar c) {
        for(int i = 1; i < this.dates.size(); ++i) {
            if (c.compareTo((Calendar)this.dates.get(i)) <= 0) {
                return (Calendar)this.dates.get(i - 1);
            }
        }

        return null;
    }

    public Series getDiffVs(Series other) {
        List<Double> diff = new ArrayList();

        for(int i = 0; i < Math.min(other.values.size(), this.values.size()); ++i) {
            if (this.values.get(i) != null && other.values.get(i) != null) {
                diff.add((Double)this.values.get(i) - (Double)other.values.get(i));
            } else {
                diff.add(null);
            }
        }

        return new Series("Diff", this.dates, diff);
    }

    public List<Double> getValuesWithStats() {
        List<Double> values = new ArrayList(this.getValuesRounded());
        values.add(null);
        values.addAll(this.getStats());
        return values;
    }

    public List<Double> getStats() {
        List<Double> stats = new ArrayList();
        stats.add(this.round(this.getAverage()));
        stats.add(this.round(this.getMedian()));
        stats.add(this.round(this.getMax()));
        stats.add(this.round(this.getMin()));
        stats.add((double)this.getNumPositive());
        stats.add(this.round(this.getFreqPositive()));
        return stats;
    }

    public Series getCumulativeGains(Calendar start, Calendar end) {
        Double startVal = this.getValue(start);
        List<Double> cumulativeValues = new ArrayList();
        List<Calendar> cumulativeDates = new ArrayList();
        cumulativeDates.add(this.getDate(start));
        cumulativeValues.add(0.0D);
        Iterator var6 = this.dates.iterator();

        while(var6.hasNext()) {
            Calendar c = (Calendar)var6.next();
            if (c.compareTo(start) >= 0) {
                Double result = this.getCurrentValue(c) / startVal - 1.0D;
                cumulativeValues.add(result);
                cumulativeDates.add(c);
            }

            if (end.compareTo(c) < 0) {
                break;
            }
        }

        return new Series(this.name + " Cum. Gains", cumulativeDates, cumulativeValues);
    }

    public Series getRelativePerformanceVs(Series other) {
        List<Double> results = new ArrayList();

        for(int i = 0; i < this.values.size(); ++i) {
            if (this.values.get(i) != null && other.values.get(i) != null) {
                Double result = (1.0D + (Double)this.values.get(i)) / (1.0D + (Double)other.values.get(i)) - 1.0D;
                results.add(result);
            } else {
                results.add(null);
            }
        }

        return new Series("Rel Str " + this.name + " vs. " + other.name, this.dates, results);
    }

    public String getName() {
        return this.name;
    }

    public Series trim(Calendar start, Calendar end) {
        List<Double> values = new ArrayList();
        List<Calendar> dates = new ArrayList();

        int index;
        for(index = 0; start.compareTo((Calendar)this.dates.get(index)) > 0; ++index) {
        }

        while(index < this.dates.size() && end.compareTo((Calendar)this.dates.get(index)) > 0) {
            values.add((Double)this.values.get(index));
            dates.add((Calendar)this.dates.get(index));
            ++index;
        }

        return new Series(this.name, dates, values);
    }
}
