package nl.ou.testar.jfx.settings.bindings.data;

public interface DataSource<T> {
    Class getDataType();
    T getData();
    void setData(T data);
}
