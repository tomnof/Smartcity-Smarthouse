package il.ac.technion.cs.smarthouse.sensors;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.system.sensors.SensorsLocalServer;
import il.ac.technion.cs.smarthouse.system.services.Core;
import il.ac.technion.cs.smarthouse.system.services.Handler;
import il.ac.technion.cs.smarthouse.system.services.ServiceType;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorsService;

/**
 * @author Sharon
 * @author Yarden
 * @author Inbal Zukerman
 * @since 7.12.16
 */


public abstract class SensorTest {
    private static Logger log = LoggerFactory.getLogger(SensorTest.class);

    protected Sensor sensor;

    protected String id;

    protected static Core core;
    protected SensorsService sensorsManager;

    /**
     * Here you should initialize the fields: sensor, id, observations. It is
     * recommended to change commName as well. Initialization is based on the a
     * concrete sensor (not the abstract Sensor).
     */
    public abstract void customInitSensor();

    @BeforeClass
    public static void initCore() {
        log.debug("SensorTest: Core starting");
        core = new Core();
    }

    @Before
    public void initSensor() throws Exception {
        sensorsManager = (SensorsService) core.serviceManager.getService(ServiceType.SENSORS_SERVICE);
        customInitSensor();
        for (int i = 0;; ++i) {
            try {
                if (sensor.register())
                    break;
            } catch (final Exception e) {
                log.error("I/O error occurred, can't regester", e);
            }
            if (i == 100) {
                log.debug("SensorTest: Registration failed");
                throw new Exception("SensorTest: Registration failed");
            }
        }
    }

    @AfterClass
    public static void close() {
        log.debug("SensorTest: Core closing");
        final Thread t = core.getSensorHandlerThread();
        if (t.isAlive())
            t.interrupt();
        ((SensorsLocalServer) core.getHandler(Handler.SENSORS)).closeSockets();
    }

}
