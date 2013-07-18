package mobi.hubtech.goacg.request;

import java.lang.reflect.Field;

import mobi.hubtech.goacg.global.C;

public abstract class BaseUrlRequest implements IRequest {
    
    public abstract String getMethod();

    @Override
    public String getStringData() {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("?");
            Field[] fields = getClass().getDeclaredFields();
            for (Field field: fields) {
                field.setAccessible(true);
                sb.append(field.getName());
                sb.append("=");
                sb.append(field.get(this));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public String getRequestUrl() {
        return C.URL + getMethod() + getStringData();
    }
    
    @Override
    public String toString() {
        return getRequestUrl();
    }
    
    @Override
    public boolean isNeedCache() {
        return false;
    }
}
