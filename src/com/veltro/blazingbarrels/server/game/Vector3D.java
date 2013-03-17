package com.veltro.blazingbarrels.server.game;

/**
 * Represents a three dimensional vector with floating point components. This class provides various utility methods
 * for vector operations.
 * 
 * @author LinearLogic
 * @since 0.3.6
 */
public class Vector3D {

	/**
	 * The x-component of the vector (horizontal)
	 */
	private float x;

	/**
	 * The y-component of the vector (horizontal)
	 */
	private float y;

	/**
	 * The z-component of the vector (
	 */
	private float z;

	/**
	 * Constructs a vector of the specified dimensions
	 * 
	 * @param x The x-component of the vector
	 * @param y The y-component of the vector
	 * @param z The z-component of the vector
	 */
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sums this vector with the provided one, but does not change either vector
	 * 
	 * @param vector Another Vector3D
	 * @return The vector produced by the addition
	 */
	public Vector3D add(Vector3D vector) {
		return new Vector3D(x + vector.getX(), y + vector.getY(), z + vector.getZ());
	}

	/**
	 * @param vector Another Vector3D
	 * @return The dot product of the two vectors, a scalar
	 */
	public float dot(Vector3D vector) {
		return x * vector.getX() + y * vector.getY() + z * vector.getZ();
	}

	/**
	 * @return The vector produced by negating each of this vector's components. The vector calling the method is not
	 * changed by this method.
	 */
	public Vector3D negate() {
		return new Vector3D(x * -1F, y * -1F, z * -1F);
	}

	/**
	 * @return The unit vector that has the same direction (component ratio) as the vector calling this method
	 */
	public Vector3D normalize() {
		float magnitude = getLength();
		return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
	}

	/**
	 * Multiplies the vector with itself and returns the resulting scalar
	 * 
	 * @return The magnitude of the vector, squared
	 */
	public float square() {
		return (float) Math.pow(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)), 2);
		// Note that the getLength() method is not used, as the casting to a float does not occur until the very end
		// of the calculation to avoid a loss of accuracy.
	}

	/**
	 * @return The length of the vector (its magnitude, calculated using the Pythagorean theorem in three dimensions)
	 */
	public float getLength() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	/**
	 * @return The vector's x-component
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the vector's x-component
	 * 
	 * @param x
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return The vector's y-component
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the vector's y-component
	 * 
	 * @param y
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return The vector's z-component
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Sets the vector's z-component
	 * 
	 * @param z
	 */
	public void setZ(float z) {
		this.z = z;
	}
}
