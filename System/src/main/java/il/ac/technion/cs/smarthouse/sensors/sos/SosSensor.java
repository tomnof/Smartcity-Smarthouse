package il.ac.technion.cs.smarthouse.sensors.sos;

import il.ac.technion.cs.smarthouse.sensors.Sensor;
import il.ac.technion.cs.smarthouse.system.file_system.PathBuilder;



/**
 * This class represents an SOS button and contains its logic.
 * 
 * @author Yarden
 * @author Inbal Zukerman
 * @since 28.12.16
 */
public class SosSensor extends Sensor {
    public SosSensor(final String id, final String systemIP, final int systemPort) {
        super(id, systemIP, systemPort);
    }

    public void updateSystem() {
        super.updateSystem(true, "sos" + PathBuilder.DELIMITER + "pressed");
    }

}
