package mobi.hubtech.goacg.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSegment {
    
    private long begin;
    private long end;
    
    public TimeSegment() {
    }
    
    public TimeSegment(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }
    
    public long getBegin() {
        return begin;
    }
    public void setBegin(long begin) {
        this.begin = begin;
    }
    public long getEnd() {
        return end;
    }
    public void setEnd(long end) {
        this.end = end;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        
        calendar.setTimeInMillis(begin);
        sb.append("begin: ").append(sdf.format(calendar.getTime()));
        calendar.setTimeInMillis(end);
        sb.append(" end: ").append(sdf.format(calendar.getTime()));
        
        return sb.toString();
    }
}
