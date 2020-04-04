package com.baidu.idl.face.main.model;

public class uuid_utime {
    public String uuid;
    public long update_time;
    public uuid_utime(){}
    public uuid_utime(String _uuid,long _update_time)
    {
        uuid=_uuid;
        update_time=_update_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
