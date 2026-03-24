package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumParticleTypes;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class S2APacketParticles implements Packet<INetHandlerPlayClient> {
	private EnumParticleTypes particleType;
	private float xCoord;
	private float yCoord;
	private float zCoord;
	private float xOffset;
	private float yOffset;
	private float zOffset;
	private float particleSpeed;
	private int particleCount;
	private boolean longDistance;
	private int[] particleArguments;

	public S2APacketParticles() {
	}

	public S2APacketParticles(EnumParticleTypes particleTypeIn, boolean longDistanceIn, float x, float y, float z,
			float xOffsetIn, float yOffset, float zOffset, float particleSpeedIn, int particleCountIn,
			int... particleArgumentsIn) {
		this.particleType = particleTypeIn;
		this.longDistance = longDistanceIn;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.xOffset = xOffsetIn;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.particleSpeed = particleSpeedIn;
		this.particleCount = particleCountIn;
		this.particleArguments = particleArgumentsIn;
	}

	public void readPacketData(PacketBuffer parPacketBuffer) throws IOException {
		this.particleType = EnumParticleTypes.getParticleFromId(parPacketBuffer.readInt());
		if (this.particleType == null) {
			this.particleType = EnumParticleTypes.BARRIER;
		}

		this.longDistance = parPacketBuffer.readBoolean();
		this.xCoord = parPacketBuffer.readFloat();
		this.yCoord = parPacketBuffer.readFloat();
		this.zCoord = parPacketBuffer.readFloat();
		this.xOffset = parPacketBuffer.readFloat();
		this.yOffset = parPacketBuffer.readFloat();
		this.zOffset = parPacketBuffer.readFloat();
		this.particleSpeed = parPacketBuffer.readFloat();
		this.particleCount = parPacketBuffer.readInt();
		int i = this.particleType.getArgumentCount();
		this.particleArguments = new int[i];

		for (int j = 0; j < i; ++j) {
			this.particleArguments[j] = parPacketBuffer.readVarIntFromBuffer();
		}

	}

	public void writePacketData(PacketBuffer parPacketBuffer) throws IOException {
		parPacketBuffer.writeInt(this.particleType.getParticleID());
		parPacketBuffer.writeBoolean(this.longDistance);
		parPacketBuffer.writeFloat(this.xCoord);
		parPacketBuffer.writeFloat(this.yCoord);
		parPacketBuffer.writeFloat(this.zCoord);
		parPacketBuffer.writeFloat(this.xOffset);
		parPacketBuffer.writeFloat(this.yOffset);
		parPacketBuffer.writeFloat(this.zOffset);
		parPacketBuffer.writeFloat(this.particleSpeed);
		parPacketBuffer.writeInt(this.particleCount);
		int i = this.particleType.getArgumentCount();

		for (int j = 0; j < i; ++j) {
			parPacketBuffer.writeVarIntToBuffer(this.particleArguments[j]);
		}

	}

	public EnumParticleTypes getParticleType() {
		return this.particleType;
	}

	public boolean isLongDistance() {
		return this.longDistance;
	}

	public double getXCoordinate() {
		return (double) this.xCoord;
	}

	public double getYCoordinate() {
		return (double) this.yCoord;
	}

	public double getZCoordinate() {
		return (double) this.zCoord;
	}

	public float getXOffset() {
		return this.xOffset;
	}

	public float getYOffset() {
		return this.yOffset;
	}

	public float getZOffset() {
		return this.zOffset;
	}

	public float getParticleSpeed() {
		return this.particleSpeed;
	}

	public int getParticleCount() {
		return this.particleCount;
	}

	public int[] getParticleArgs() {
		return this.particleArguments;
	}

	public void processPacket(INetHandlerPlayClient inethandlerplayclient) {
		inethandlerplayclient.handleParticles(this);
	}
}