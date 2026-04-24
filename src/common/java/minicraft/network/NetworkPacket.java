package minicraft.network;

import java.io.Serializable;
import minicraft.network.MinicraftProtocol.InputType;

public class NetworkPacket implements Serializable {
	private static final long serialVersionUID = 1L;

	private final InputType type;
	private final Object payload;
	private final long timestamp;
	private final int playerId;
	private final long tick;
	private final long sequence;


	public NetworkPacket(InputType type, Object payload, int playerId, long tick, long sequence) {
		this(type, payload, System.currentTimeMillis(), playerId, tick, sequence);
	}

	public NetworkPacket(InputType type, Object payload, long timestamp, int playerId, long tick, long sequence) {
		this.type = type;
		this.payload = (payload == null) ? "" : payload;
		this.timestamp = timestamp;
		this.playerId = playerId;
		this.tick = tick;
		this.sequence = sequence;
	}

	public InputType getType() {
		return type;
	}

	public Object getPayload() {
		return payload;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getPlayerId() {
		return playerId;
	}

	public long getTick() {
		return tick;
	}

	public long getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return "NetworkPacket{" +
			"type=" + type +
			", payload='" + payload + '\'' +
			", timestamp=" + timestamp +
			", playerId=" + playerId +
			", tick=" + tick +
			", sequence=" + sequence +
			'}';
	}
}
