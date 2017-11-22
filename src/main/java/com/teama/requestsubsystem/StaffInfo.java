package com.teama.requestsubsystem;

import com.teama.Configuration;
import com.teama.requestsubsystem.data.JavaDatabaseStaffInfo;
import com.teama.requestsubsystem.data.StaffInfoDataSource;

public class StaffInfo {
    private static StaffInfo ourInstance = new StaffInfo();

    public static StaffInfo getInstance() {
        return ourInstance;
    }

    private StaffInfoDataSource stdb;

    private StaffInfo() {
        stdb = new JavaDatabaseStaffInfo(Configuration.dbURL, Configuration.staffInfoTable);
    }

    public StaffInfoDataSource getStaffInfoDB() {
        return stdb;
    }
}
