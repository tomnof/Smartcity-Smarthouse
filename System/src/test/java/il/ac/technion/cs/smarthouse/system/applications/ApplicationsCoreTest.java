package il.ac.technion.cs.smarthouse.system.applications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import il.ac.technion.cs.smarthouse.system.SystemCore;
import il.ac.technion.cs.smarthouse.system.applications.api.SmartHouseApplication;
import il.ac.technion.cs.smarthouse.system.applications.installer.ApplicationPath;
import il.ac.technion.cs.smarthouse.system.applications.installer.ApplicationPath.PathType;
import il.ac.technion.cs.smarthouse.system.applications.smarthouseApplicationExamples.MyApp1;
import il.ac.technion.cs.smarthouse.utils.BoolLatch;

/**
 * [[SuppressWarningsSpartan]]
 */
public class ApplicationsCoreTest {
    private static Class<? extends SmartHouseApplication> APP1_CLASS = MyApp1.class; 
    private static String APP1_CLASSPATH = ApplicationsCoreTest.class.getPackage().getName() + ".smarthouseApplicationExamples.MyApp1";
    private ApplicationsCore appCore;
    
    @Before
    public void init() {
        appCore = new SystemCore().applicationsHandler;
    }
    
    @Test
    public void addApplicationAndApplicationManagerTest() throws Exception {
        assert appCore.getApplicationManagers().isEmpty();
        ApplicationManager m;
        Assert.assertNotNull(m = appCore.addApplication(new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH)));
        assert !appCore.getApplicationManagers().isEmpty();
        Assert.assertEquals(appCore.getInstalledApplicationNames().stream().filter(n->n.equals(APP1_CLASS.getName())).count(), 1);
        
        m.setId("XXX");
        Assert.assertEquals(m.getId(), "XXX");
        
        assert m.equals(m);
        assert !m.equals(null);
        
        ApplicationManager m2 = new ApplicationManager(m.getId(), new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH));
        assert m.equals(m2);
        assert m2.equals(m);
        
        Assert.assertEquals(m + "", m2 + "");
        Assert.assertEquals(m.hashCode(), m2.hashCode());
        
        assert !m.initialize(null);
        assert m2.initialize(null);
        
        m.reopen(null);
        m2.reopen(null);
    }
    
    @Test
    public void stupidApplicationManagerTest() {
        ApplicationManager m1 = new ApplicationManager(null, new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH));
        ApplicationManager m2 = new ApplicationManager(null, new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH));
        ApplicationManager m3 = new ApplicationManager("XXX", new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH));
        
        assert m1.equals(m2);
        assert m2.equals(m1);
        
        assert !m1.equals(m3);
        assert !m3.equals(m1);
        
        assert !m2.equals(m3);
        assert !m3.equals(m2);
        
        assert !m1.equals(null);
        assert !m1.equals("Bla");
        
        Assert.assertEquals(m1.hashCode(), m2.hashCode());
        Assert.assertNotEquals(m1.hashCode(), m3.hashCode());
        Assert.assertNotEquals(m2.hashCode(), m3.hashCode());
        
        Assert.assertNull(m1.getApplicationName());
    }
    
    @Test(timeout = 1000)
    public void onAppsListChangeTest() throws Exception {
        final BoolLatch wasCalled = new BoolLatch();
        
        appCore.setOnAppsListChange(()->wasCalled.setTrueAndRelease());
        Assert.assertNotNull(appCore.addApplication(new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH)));
        wasCalled.blockUntilTrue();
    }
    
    @Test
    public void savebleTest() throws Exception {
        Assert.assertNotNull(appCore.addApplication(new ApplicationPath(PathType.CLASS_NAME, APP1_CLASSPATH)));
        
        ApplicationsCore appCoreNew = new SystemCore().applicationsHandler;
        appCoreNew.populate(appCore.toJsonString());
        assert !appCoreNew.getApplicationManagers().isEmpty();
        Assert.assertEquals(appCoreNew.getInstalledApplicationNames().stream().filter(n->n.equals(APP1_CLASS.getName())).count(), 1);
    }
}