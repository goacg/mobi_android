package mobi.hubtech.goacg.request;

public interface IRequest {
    public String getMethod();
    public String getStringData();
    public String getRequestUrl();
    public boolean isNeedCache();
}
