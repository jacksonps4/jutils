package com.minorityhobbies.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetUtils {
	private NetUtils() {}
	
	public static InetAddress getLocalIPv4Address() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface eth = networkInterfaces.nextElement();
			if (eth.isLoopback()) {
				continue;
			}
			
			Enumeration<InetAddress> addresses = eth.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				if (addr instanceof Inet6Address) {
					continue;
				}
				
				return addr;
			}
		}
		
		return null;
	}
}
