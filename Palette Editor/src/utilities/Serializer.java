package utilities;

import java.util.*;
import java.io.*;

public class Serializer {
	
	private Serializer() {
		
	}
	
	// serialize a boolean to a byte array
	public static byte[] serializeBoolean(boolean b) {
		byte[] data = new byte[1];
		data[0] = (byte) (b ? 1 : 0);
		return data;
	}
	
	// deserialize a byte array back into a boolean
	public static boolean deserializeBoolean(byte[] data) {
		if(data == null || data.length != 1) { return false; }
		return (boolean) (data[0] != 0);
	}
	
	// serialize a short to a byte array
	public static byte[] serializeShort(short s) {
		byte[] data = new byte[2];
		data[0] = (byte) (s >> 8);
		data[1] = (byte) (s);
		return data;
	}
	
	// deserialize a byte array back into a short
	public static short deserializeShort(byte[] data) {
		if(data == null || data.length != 2) { return -1; }
		return (short) (data[0] << 8
					 | (data[1] & 0xff));
	}
	
	// serialize an integer to a byte array
	public static byte[] serializeInteger(int i) {
		byte[] data = new byte[4];
		data[0] = (byte) (i >> 24);
		data[1] = (byte) (i >> 16);
		data[2] = (byte) (i >> 8);
		data[3] = (byte) (i);
		return data;
	}
	
	// deserialize a byte array back into an integer
	public static int deserializeInteger(byte[] data) {
		if(data == null || data.length != 4) { return -1; }
		return (int) (data[0] << 24
				   | (data[1] & 0xff) << 16
				   | (data[2] & 0xff) << 8
				   | (data[3] & 0xff));
	}
	
	// serialize a long to a byte array
	public static byte[] serializeLong(long l) {
		byte[] data = new byte[8];
		data[0] = (byte) (l >> 56);
		data[1] = (byte) (l >> 48);
		data[2] = (byte) (l >> 40);
		data[3] = (byte) (l >> 32);
		data[4] = (byte) (l >> 24);
		data[5] = (byte) (l >> 16);
		data[6] = (byte) (l >> 8);
		data[7] = (byte) (l);
		return data;
	}
	
	// deserialize a byte array back into a long
	public static long deserializeLong(byte[] data) {
		if(data == null || data.length != 8) { return -1; }
		long l = 0;
		for(int i=0;i<8;++i) {
			l |= ((long) data[i] & 0xff) << ((8-i-1) << 3);
		}
		return l;
	}
	
	// serialize a float into a byte array
	public static byte[] serializeFloat(float f) {
		return serializeInteger(Float.floatToIntBits(f));
	}
	
	// deserialize a byte array back into a float
	public static float deserializeFloat(byte[] data) {
		return Float.intBitsToFloat(deserializeInteger(data));
	}
	
	// serialize a double into a byte array
	public static byte[] serializeDouble(double d) {
		return serializeLong(Double.doubleToLongBits(d));
	}
	
	// deserialize a byte array back into a double
	public static double deserializeDouble(byte[] data) {
		return Double.longBitsToDouble(deserializeLong(data));
	}
	
	// serialize a single byte character to a byte
	public static byte serializeByteCharacter(char c) {
		return (byte) c;
	}
	
	// deserialize a byte into a single byte character
	public static char deserializeByteCharacter(byte data) {
		return (char) (data & 0xff);
	}
	
	// serialize a character to a byte array
	public static byte[] serializeCharacter(char c) {
		byte[] data = new byte[2];
		data[0] = (byte) (c >> 8);
		data[1] = (byte) (c);
		return data;
	}
	
	// deserialize a byte array back into a character
	public static char deserializeCharacter(byte[] data) {
		if(data == null || data.length != 2) { return '\0'; }
		return (char) (data[0] << 8
					| (data[1] & 0xff));
	}
	
	// serialize the specified byte string
	public static byte[] serializeByteString(String s) {
		if(s == null) { return null; }
		if(s.length() == 0) { return null; }
		
		byte[] data = new byte[s.length()];
		
		// serialize and store the bytes for each character in the string
		int j = 0;
		for(int i=0;i<s.length();i++) {
			data[j++] = (byte) s.charAt(i);
		}
		
		return data;
	}
	
	// de-serialize the specified byte string
	public static String deserializeByteString(byte[] data) {
		if(data == null) { return null; }
		if(data.length == 0) { return null; }
		
		String s = "";
		char c = '\0';
		
		for(int i=0;i<data.length;i++) {
			c = (char) (data[i] & 0xff);
			if(c == '\0') { break; }
			s += c;
		}
		
		return s;
	}
	
	// serialize the specified string
	public static byte[] serializeString(String s) {
		if(s == null) { return null; }
		if(s.length() == 0) { return null; }
		
		byte[] data = new byte[s.length() * 2];
		
		// serialize and store the bytes for each character in the string
		int j = 0;
		for(int i=0;i<s.length();i++) {
			data[j++] = (byte) (s.charAt(i) >> 8);
			data[j++] = (byte) (s.charAt(i));
		}
		
		return data;
	}
	
	// de-serialize the specified string
	public static String deserializeString(byte[] data) {
		if(data == null) { return null; }
		if(data.length == 0 || data.length % 2 != 0) { return null; }
		
		String s = "";
		
		for(int i=0;i<data.length;i+=2) {
			s += (char) (data[i] << 8
					  | (data[i+1] & 0xff));
		}
		
		return s;
	}
	
	// return a byte array representing the serialized version of an object
	public static byte[] serializeObject(Object o) throws IOException {
		if(o == null) { return null; }
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = null;
		
	    objectStream = new ObjectOutputStream(byteStream);
	    objectStream.writeObject(o);
	    objectStream.close();
		
	    return byteStream.toByteArray();
	}
	
	// return an object which has been deserialized from a byte array
	public static Object deserializeObject(byte[] data) throws IOException {
		if(data == null || data.length < 1) { return null; }
		
		Object object;
		ObjectInputStream objectStream = null;
		try {
			objectStream = new ObjectInputStream(new ByteArrayInputStream(data));
			object = objectStream.readObject();
			objectStream.close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return object;
	}
	
	// serializes the specified array of objects
	public static byte[] serializeArray(Object[] objects) throws IOException {
		if(objects == null || objects.length == 0) { return null; }
		
		Vector<byte[]> serializedObjects = new Vector<byte[]>(objects.length);
		
		int length = 4;
		
		// serialize and store the bytes for each object
		byte[] serializedObject = null;
		for(int i=0;i<objects.length;i++) {
			serializedObject = serializeObject(objects[i]);
			if(serializedObject == null) {
				throw new IOException("error serializing object");
			}
			length += 4 + serializedObject.length;
			serializedObjects.add(serializedObject);
		}
		
		// initialize the byte stream
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(length);
		
		// write the serialized bytes for the number of objects
		byteStream.write(serializeInteger(serializedObjects.size()));
		
		// write the serialized bytes for the size of the serialized object, followed by the serialized bytes of each object
		for(int i=0;i<serializedObjects.size();i++) {
			byteStream.write(serializeInteger(serializedObjects.elementAt(i).length));
			byteStream.write(serializedObjects.elementAt(i));
		}
		
		return byteStream.toByteArray();
	}
	
	// de-serializes the specified array of objects
	public static Object[] deserializeArray(byte[] data) throws IOException {
		if(data == null || data.length == 0) { return null; }
		
		int length, numberOfObjects = -1;
		byte[] temp;
		Object[] objects;
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		
		// read and de-serialize the bytes for the number of objects
		temp = new byte[4];
		byteStream.read(temp);
		numberOfObjects = deserializeInteger(temp);
		if(numberOfObjects < 1) { return null; }
		objects = new Object[numberOfObjects];
		
		// read all of the objects
		for(int i=0;i<numberOfObjects;i++) {
			// read and de-serialize the bytes for the size of the object
			temp = new byte[4];
			byteStream.read(temp);
			length = deserializeInteger(temp);
			if(length < 1) { return null; }
			
			// read and de-serialize the bytes for each object
			temp = new byte[length];
			byteStream.read(temp);
			objects[i] = deserializeObject(temp);
			if(objects[i] == null) { return null; }
		}
		
		return objects;
	}
	
	// read a serialized boolean off of a specified input stream
	public static boolean readBoolean(InputStream in) throws IOException {
		if(in == null) { return false; }
		
		byte[] data = new byte[1];
		
		in.read(data);
		
		return deserializeBoolean(data);
	}
	
	// serialize and write a boolean to a specified output stream
	public static boolean writeBoolean(boolean b, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeBoolean(b);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized short off of a specified input stream
	public static short readShort(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[2];
		
		in.read(data);
		
		return deserializeShort(data);
	}
	
	// serialize and write a short to a specified output stream
	public static boolean writeShort(short s, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeShort(s);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized integer off of a specified input stream
	public static int readInteger(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[4];
		
		in.read(data);
		
		return deserializeInteger(data);
	}
	
	// serialize and write an integer to a specified output stream
	public static boolean writeInteger(int i, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeInteger(i);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized long off of a specified input stream
	public static long readLong(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[8];
		
		in.read(data);
		
		return deserializeLong(data);
	}
	
	// serialize and write a long to a specified output stream
	public static boolean writeLong(long l, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeLong(l);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized float off of a specified input stream
	public static float readFloat(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[4];
		
		in.read(data);
		
		return deserializeFloat(data);
	}
	
	// serialize and write a float to a specified output stream
	public static boolean writeFloat(float f, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeFloat(f);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized double off of a specified input stream
	public static double readDouble(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[8];
		
		in.read(data);
		
		return deserializeDouble(data);
	}
	
	// serialize and write a double to a specified output stream
	public static boolean writeDouble(double d, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeDouble(d);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized single byte character off of a specified input stream
	public static char readByteCharacter(InputStream in) throws IOException {
		if(in == null) { return '\0'; }
		
		byte[] data = new byte[1];
		
		in.read(data);
		
		return (char) (data[0] & 0xff);
	}
	
	// serialize and write a single byte character to a specified output stream
	public static boolean writeByteCharacter(char c, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = { (byte) c };
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized character off of a specified input stream
	public static char readCharacter(InputStream in) throws IOException {
		if(in == null) { return '\0'; }
		
		byte[] data = new byte[2];
		
		in.read(data);
		
		return deserializeCharacter(data);
	}
	
	// serialize and write a character to a specified output stream
	public static boolean writeCharacter(char c, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeCharacter(c);
		
		out.write(data, 0, data.length);
		
		return true;
	}

	// read characters off of the specified input stream until a newline or cr character is encountered
	public static String readLine(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		char c = '\0';
		String s = new String("");
		
		// accumulate the string until a newline character is encountered
		byte[] data = new byte[2];
		while(true) {
			in.read(data);
			c = deserializeCharacter(data);
			if(c == '\n' || c == '\r') { break; }
			
			s += c;
		}
		return s;
	}
	
	// read characters off of the specified input stream until a newline, cr, space or tab character is encountered
	public static String readToken(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		char c = '\0';
		String s = new String("");
		
		// accumulate the string until a newline or spacing character is encountered
		byte[] data = new byte[2];
		while(true) {
			in.read(data);
			c = deserializeCharacter(data);
			if(c == '\n' || c == '\r' || c == '\t' || c == ' ') { break; }
			
			s += c;
		}
		return s;
	}
	
	// write a single byte string to the specified output stream
	public static boolean writeByteString(String s, OutputStream out) throws IOException {
		if(s == null || out == null) { return false; }
		
		// serialize and write each single byte character in the string
		for(int i=0;i<s.length();i++) {
			byte[] data = { (byte) s.charAt(i) };
			out.write(data, 0, data.length);
		}
		
		return true;
	}
	
	// write a string to the specified output stream
	public static boolean writeString(String s, OutputStream out) throws IOException {
		if(s == null || out == null) { return false; }
		
		// serialize and write each character in the string
		for(int i=0;i<s.length();i++) {
			byte[] data = serializeCharacter(s.charAt(i));
			out.write(data, 0, data.length);
		}
		
		return true;
	}
	
	// read a serialized object from a specified input stream
	public static Object readObject(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		byte[] lengthData = new byte[4];
		in.read(lengthData);
		int length = deserializeInteger(lengthData);
		if(length < 1) { return null; }
		
		byte[] data = new byte[length];
		in.read(data);
		
		return deserializeObject(data);
	}
	
	// serialize and write an object to a specified output stream 
	public static boolean writeObject(Object o, OutputStream out) throws IOException {
		if(o == null || out == null) { return false; }
		
		byte[] data = serializeObject(o);
		if(data == null) { return false; }
		
		byte[] lengthData = serializeInteger(data.length);
		if(lengthData == null) { return false; }
		
		out.write(lengthData, 0, data.length);
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized array from the specified input stream
	public static Object[] readArray(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		byte[] lengthData = new byte[4];
		in.read(lengthData);
		int length = deserializeInteger(lengthData);
		if(length < 1) { return null; }
		
		byte[] data = new byte[length];
		in.read(data);
		
		return deserializeArray(data);
	}
	
	// serialize and write an array to the specified output stream
	public static boolean writeArray(Object[] array, OutputStream out) throws IOException {
		if(array == null || out == null) { return false; }
		
		byte[] data = serializeArray(array);
		if(data == null) { return false; }
		
		byte[] lengthData = serializeInteger(data.length);
		if(lengthData == null) { return false; }
		
		out.write(lengthData, 0, data.length);
		out.write(data, 0, data.length);
		
		return true;
	}
	
}
