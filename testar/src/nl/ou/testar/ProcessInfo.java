package nl.ou.testar;

import org.fruit.alayer.SUT;

public class ProcessInfo {
    public SUT sut;
    public long pid;
    public long handle;
    public String Desc;

    public ProcessInfo(SUT sut, long pid, long handle, String desc){
        this.sut = sut;
        this.pid = pid;
        this.handle = handle;
        this.Desc = desc;
    }

    public String toString(){
        return "PID <" + this.pid + "> HANDLE <" + this.handle + "> DESC <" + this.Desc + ">";
    }
}
