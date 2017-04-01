package il.ac.technion.cs.eldery.system.applications.API;

import il.ac.technion.cs.eldery.system.applications.api.SmartHouseApplication;

public class TestApplication extends SmartHouseApplication {
    private boolean isLoaded;

    public TestApplication() {}

    @Override public void onLoad() {
        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override public String getApplicationName() {
        return "TestApplication";
    }

}
