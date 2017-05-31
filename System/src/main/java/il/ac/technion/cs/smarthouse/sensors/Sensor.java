package il.ac.technion.cs.smarthouse.sensors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.networking.messages.Message;
import il.ac.technion.cs.smarthouse.networking.messages.MessageType;
import il.ac.technion.cs.smarthouse.system.file_system.PathBuilder;

/**
 * Represents a physical component that can send information.
 * 
 * @author Sharon
 * @author Yarden
 * @author Inbal Zukerman
 * @since 7.12.16
 */
public abstract class Sensor {
    private static Logger log = LoggerFactory.getLogger(Sensor.class);

    /**
     * Defines the maximal amount of update messages that can be sent by this
     * sensor each second.
     */
    public static final int MAX_MESSAGES_PER_SECOND = 10;
    private final List<Long> lastMessagesMillis = new ArrayList<>();

    protected String id;

    protected InetAddress systemIP;
    protected int systemPort;

    protected Socket socket;
    protected PrintWriter out;
    protected BufferedReader in;

    protected SensorType sType;

    /**
     * Initializes a new sensor given its name and id.
     * 
     * @param id
     *            id of the sensor
     * @param commName
     *            sensor's commercial name
     * @param types
     *            types this sensor qualifies for
     * @param systemIP
     *            IP address of the system
     * @param systemPort
     *            port on which the system listens to incoming messages
     */
    public Sensor(final String id, final String systemIP, final int systemPort) {
        this.id = id;

        try {
            this.systemIP = getSystemIp();
        } catch (IOException | InterruptedException e) {
        }
        this.systemPort = systemPort;
        sType = SensorType.NON_INTERACTIVE;
    }
    
    private InetAddress getSystemIp() throws IOException, SocketException, InterruptedException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(0)); //zero means the port number is chosen dynamically
        channel.socket().setBroadcast(true);
        
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.clear();
        SocketAddress serverAddress;
        do{
            channel.send(buf, new InetSocketAddress("255.255.255.255", systemPort));
            Thread.sleep(1000);
            //see if got answer
            serverAddress = channel.receive(buf);
        }while( serverAddress == null );
        
        channel.close();
        return ((InetSocketAddress)serverAddress).getAddress();
    }
    
    /**
     * Registers the sensor its TCP connection with the system. This method must
     * be called before any calls to the updateSystem method.
     * 
     * @return <code>true</code> if registration was successful,
     *         <code>false</code> otherwise
     */
    public boolean register() {
        try {
            socket = new Socket(systemIP, systemPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (final IOException e) {
            log.error("I/O error occurred when the sensor's socket was created", e);
        }

        final String $ = Message.send(Message.createMessage(MessageType.REGISTRATION, "", "", id), out, in);
        return $ != null && Message.isSuccessMessage($);
    }

    /**
     * Sends an update message to the system with the given observations. The
     * observations are represented as a map from the names of the observations,
     * to their values.
     * 
     * @param data
     *            observations to send to the system
     */
    public void updateSystem(final Object value, final String... path) {
        final long currMillis = System.currentTimeMillis();
        for (int ¢ = lastMessagesMillis.size() - 1; ¢ >= 0; --¢)
            if (currMillis - lastMessagesMillis.get(¢) > 1000)
                lastMessagesMillis.remove(¢);

        if (lastMessagesMillis.size() >= 10)
            return;

        lastMessagesMillis.add(currMillis);

        Message.send(Message.createMessage(MessageType.UPDATE, PathBuilder.buildPath(path), value.toString(),
                        id), out, null);
    }

    /** @return id of the sensor */
    public String getId() {
        return id;
    }

    /** @return sensor's type */
    public SensorType getType() {
        return sType;
    }
}
