package com.veltro.blazingbarrels.server.game;

/**
 * Location3D objects represent three-dimensional locations that factor in rotation (yaw, pitch, and roll). NOTE: the
 * 'up and down' dimension is the y dimension, as is the convention for graphics design. The z axis runs into the
 * screen, and the x axis runs across it, increasing from left to right.
 * 
 * <p>Coordinate values are in pixels.
 * 
 * @author LinearLogic
 * @since 0.1.0
 */
public class Location3D {

	/**
	 * The x-coordinate of the location, in pixels
	 */
	private float x;

	/**
	 * The y-coordinate of the location, in pixels
	 */
	private float y;

	/**
	 * The z-coordinate of the location, in pixels
	 */
	private float z;

	/**
	 * The yaw (rotation about the y-axis), in degrees, of the location. This value is on the domain [0, 360)
	 */
	private float yaw;

	/**
	 * The pitch (rotation about the x-axis), in degrees, of the location. This value is on the domain [0, 360)
	 */
	private float pitch;

	/**
	 * The roll (rotation about the z-axis), in degrees, of the location. This value is on the domain [0, 360)
	 */
	private float roll;

	/**
	 * Simplest constructor - calls the {@link #Location3D(float, float, float, float, float, float) complete
	 * constructor} passing 0, 0, 0, 0, 0, 0 for x, y, z, yaw, pitch, roll
	 */
	public Location3D() {
		this(0, 0, 0, 0, 0, 0);
	}

	/**
	 * Simple constructor - takes the x, y, and z coordinates of the location as parameters, and initializes the
	 * rotation variables to zero, the default value
	 * 
	 * @param x The x-coordinate, in pixels, of the location
	 * @param y The y-coordinate, in pixels, of the location
	 * @param z The z-coordinate, in pixels, of the location
	 */
	public Location3D(float x, float y, float z) {
		this(x, y, z, 0, 0, 0);
	}

	/**
	 * String-based constructor - constructs the location with default values and then attempts to update the values
	 * based on the contents of the provided location string. If said string is improperly formatted, the default
	 * location values are used.
	 * 
	 * @param locationString The location rendered as a String (usually via the {@link #toString()} method)
	 */
	public Location3D(String locationString) {
		this(); // Load defaults
		String[] data = locationString.split(":");
		if (data.length != 6)
			return;
		try {
			x = Float.parseFloat(data[0]);
			y = Float.parseFloat(data[1]);
			z = Float.parseFloat(data[2]);
			yaw = Float.parseFloat(data[3]);
			pitch = Float.parseFloat(data[4]);
			roll = Float.parseFloat(data[5]);
		} catch (NumberFormatException e) { // Invalid formatting - revert to default location
			return;
		}
			
	}

	/**
	 * Complete constructor - takes the x, y, and z coordinates of the location, as well as its pitch and yaw, as
	 * parameters.
	 * 
	 * @param x The x-coordinate, in pixels, of the location
	 * @param y The y-coordinate, in pixels, of the location
	 * @param z The z-coordinate, in pixels, of the location
	 * @param yaw The {@link #yaw} of the location
	 * @param pitch The {@link #pitch} of the location
	 * @param roll The {@link #roll} of the location
	 */
	public Location3D(float x, float y, float z, float yaw, float pitch, float roll) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	/**
	 * Shifts the location by the specified integer amounts in each direction (casts the provided ints to floats, which
	 * are in turn passed into the {@link #translate(float x, float y)} method)
	 * 
	 * @param dx x-displacement
	 * @param dy y-displacement
	 * @param dz z-displacement
	 */
	public void translate(int dx, int dy, int dz) {
		this.translate((float) dx, (float) dy, (float) dz);
	}

	/**
	 * Shifts the location by the specified floating point amounts in each direction.
	 * 
	 * @param dx x-displacement
	 * @param dy y-displacement
	 * @param dz z-displacement
	 */
	public void translate(float dx, float dy, float dz) {
		x += dx;
		y += dy;
		z += dz;
	}

	/**
	 * Rotates the location by the provided integer amount in each rotational direction
	 * 
	 * @param yawAmount
	 * @param pitchAmount
	 * @param rollAmount
	 */
	public void rotate(int yawAmount, int pitchAmount, int rollAmount) {
		this.rotate((float) yawAmount, (float) pitchAmount, (float) rollAmount);
	}

	/**
	 * Rotates the location by the provided floating point amount in each rotational direction
	 * 
	 * @param yawAmount The value by which to increment the location's {@link #yaw}
	 * @param pitchAmount The value by which to increment the location's {@link #pitch}
	 * @param rollAmount The value by which to increment the location's {@link #roll}
	 */
	public void rotate(float yawAmount, float pitchAmount, float rollAmount) {
		yaw = (yaw + yawAmount) % 360;
		if (yaw < 0)
			yaw = 360 + yaw;
		pitch = (pitch + pitchAmount) % 360;
		if (pitch < 0)
			pitch = 360 + pitch;
		roll = (roll + rollAmount) % 360;
		if (roll < 0)
			roll = 360 + roll;
	}

	/**
	 * Uses the Pythagorean theorem in three dimensions to determine the shortest distance between two locations
	 * 
	 * @param anotherLocation The second location in the distance equation (along with the location running the method)
	 * @return The distance, a float value, between the two locations
	 */
	public float distanceTo(Location3D anotherLocation) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	/**
	 * Stores the location's data in a String to be sent in a BBPacket
	 */
	public String toString() {
		return x + ":" + y + ":" + z + ":" + yaw + ":" + pitch + ":" + roll;
	}

	/**
	 * Utility method for altering multiple coordinate values simultaneously (when teleporting, for instance)
	 * 
	 * @param x The new x-coordinate, in pixels
	 * @param y The new y-coordinate, in pixels
	 * @param z The new z-coordinate, in pixels
	 */
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Utility method for altering multiple rotational angles simultaneously
	 * 
	 * @param yaw The new yaw, in degrees
	 * @param pitch The new pitch, in degrees
	 * @param roll The new roll, in degrees
	 */
	public void setRotation(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	/**
	 * @return The x-coordinate of the location, in pixels
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the pixel x-coordinate of the location to the specified value
	 * 
	 * @param x A floating point value
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return The y-coordinate of the location, in pixels
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the pixel y-coordinate of the location to the specified value
	 * 
	 * @param x A floating point value
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return The z-coordinate of the location, in pixels
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Sets the pixel z-coordinate of the location to the specified value
	 * 
	 * @param z A floating point value
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * @return The {@link #yaw} of the location
	 */
	public float getYaw() {
		return yaw;
	}
	
	/**
	 * Sets the {@link #yaw} of the location to the specified value
	 * 
	 * @param yaw A floating point value
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * @return The {@link #pitch} of the location
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the {@link #pitch} of the location to the provided value
	 * 
	 * @param pitch A floating point value
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return The {@link #roll} of the location
	 */
	public float getRoll() {
		return roll;
	}
	
	/**
	 * Sets the {@link #roll} of the location to the provided value
	 * 
	 * @param roll A floating point value
	 */
	public void setRoll(float roll) {
		this.roll = roll;
	}
}

