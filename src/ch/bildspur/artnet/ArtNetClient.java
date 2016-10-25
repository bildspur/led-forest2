package ch.bildspur.artnet;

/**
 * Created by cansik on 25.10.16.
 */

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.packets.ArtDmxPacket;

import java.net.InetAddress;
import java.net.SocketException;

public class ArtNetClient {
    private int sequenceId;
    private ArtNet artnet;
    private ArtNetNode receiver;

    public ArtNetClient() {
        artnet = new ArtNet();
    }

    public void open() {
        open(null);
    }

    public void open(String address) {
        try {
            artnet.start();
            setReceiver(address);
        } catch (SocketException | ArtNetException e) {
            e.printStackTrace();
        }
    }

    public void setReceiver(String address) {
        if (null == address)
            receiver = null;

        receiver = createNode(address);
    }

    public ArtNetNode createNode(String address) {
        try {
            ArtNetNode node = new ArtNetNode();
            node.setIPAddress(InetAddress.getByName(address));
            return node;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        artnet.stop();
    }

    public void send(int universe, byte[] data) {
        send(receiver, universe, data);
    }

    public void send(ArtNetNode node, int universe, byte[] data) {
        ArtDmxPacket dmx = new ArtDmxPacket();

        dmx.setUniverse(0, universe);
        dmx.setSequenceID(sequenceId % 256);
        dmx.setDMX(data, data.length);

        if (receiver != null)
            artnet.unicastPacket(dmx, node);
        else
            artnet.broadcastPacket(dmx);

        sequenceId++;
    }
}