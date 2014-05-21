package szkuwa.hcables.utils;

public class CableConnection {
	public int x;
	public int y;
	public int z;
	public CableConnectionType type;
	
	public CableConnection(int ax, int ay, int az, CableConnectionType atype){
		this.x = ax;
		this.y = ay;
		this.z = az;
		this.type = atype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CableConnection other = (CableConnection) obj;
		if (type != other.type && type != CableConnectionType.ANY)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}